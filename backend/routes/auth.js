const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const crypto = require("crypto");
const nodemailer = require("nodemailer");
const User = require("../models/User");

const router = express.Router();

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS
  }
});

const BASE_URL = process.env.BASE_URL || "http://10.0.2.2:3000";

// POST /auth/register
router.post("/register", async (req, res) => {
  try {
    const { name, email, username, password, role } = req.body;

    if (!name || !email || !username || !password) {
      return res.status(400).json({ error: "Missing fields" });
    }

    const existing = await User.findOne({ $or: [{ email }, { username }] });
    if (existing) {
      return res.status(409).json({ error: "User already exists" });
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const verificationToken = crypto.randomBytes(32).toString("hex");

    const user = await User.create({
      name,
      email,
      username,
      passwordHash,
      role: role || "staff",
      isVerified: false,
      emailVerificationToken: verificationToken
    });

    const verifyLink = `${BASE_URL}/auth/verify-email/${verificationToken}`;

    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: user.email,
      subject: "Verify your PointSeventhCafe account",
      html: `
        <p>Hello ${user.name},</p>
        <p>Thanks for registering for PointSeventhCafe.</p>
        <p>Please verify your email by clicking the link below:</p>
        <a href="${verifyLink}">${verifyLink}</a>
        <p>If you did not create this account, you can ignore this email.</p>
      `
    });

    return res.status(201).json({
      message: "Account created. Please verify your email before logging in.",
      id: user._id,
      username: user.username,
      role: user.role
    });
  } catch (err) {
    console.log("REGISTER ERROR:", err.message);
    return res.status(500).json({ error: err.message });
  }
});

// GET /auth/verify-email/:token
router.get("/verify-email/:token", async (req, res) => {
  try {
    const { token } = req.params;

    const user = await User.findOne({ emailVerificationToken: token });

    if (!user) {
      return res.status(400).send("Invalid verification token.");
    }

    user.isVerified = true;
    user.emailVerificationToken = null;
    await user.save();

    return res.send(`
      <html>
        <body style="font-family: Arial; max-width: 500px; margin: 40px auto; text-align: center;">
          <h2>Email Verified</h2>
          <p>Your account has been verified successfully.</p>
          <p>You can now return to the app and log in.</p>
        </body>
      </html>
    `);
  } catch (err) {
    console.log("VERIFY EMAIL ERROR:", err.message);
    return res.status(500).send("Server error");
  }
});

// POST /auth/login
router.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;

    if (!username || !password) {
      return res.status(400).json({ error: "Missing fields" });
    }

    const user = await User.findOne({ username });
    if (!user) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const ok = await bcrypt.compare(password, user.passwordHash);
    if (!ok) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    if (!user.isVerified) {
      return res.status(403).json({ error: "Please verify your email before logging in" });
    }

    const token = jwt.sign(
      { id: user._id.toString(), role: user.role, username: user.username },
      process.env.JWT_SECRET,
      { expiresIn: "7d" }
    );

    res.json({
      token,
      user: {
        id: user._id,
        name: user.name,
        username: user.username,
        role: user.role
      }
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /auth/forgot-password
router.post("/forgot-password", async (req, res) => {
  try {
    const email = req.body?.email;

    if (!email) {
      return res.status(400).json({ error: "Email is required" });
    }

    const user = await User.findOne({ email });

    if (!user) {
      return res.json({
        message: "If that email exists, a reset link has been sent."
      });
    }

    const resetToken = crypto.randomBytes(32).toString("hex");
    const expiry = new Date(Date.now() + 1000 * 60 * 15);

    user.resetPasswordToken = resetToken;
    user.resetPasswordExpires = expiry;
    await user.save();

    const resetLink = `${BASE_URL}/auth/reset-password/${resetToken}`;

    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: user.email,
      subject: "Reset your PointSeventhCafe password",
      html: `
        <p>Hello ${user.name},</p>
        <p>You requested a password reset.</p>
        <p>Click the link below to reset your password:</p>
        <a href="${resetLink}">${resetLink}</a>
        <p>This link expires in 15 minutes.</p>
      `
    });

    res.json({
      message: "If that email exists, a reset link has been sent."
    });
  } catch (err) {
    console.log("FORGOT PASSWORD ERROR:", err.message);
    res.status(500).json({ error: err.message });
  }
});

// GET /auth/reset-password/:token
router.get("/reset-password/:token", (req, res) => {
  const { token } = req.params;

  res.send(`
    <html>
      <body style="font-family: Arial; max-width: 400px; margin: 40px auto;">
        <h2>Reset Password</h2>
        <form method="POST" action="/auth/reset-password/${token}">
          <input
            type="password"
            name="password"
            placeholder="Enter new password"
            required
            style="width: 100%; padding: 10px; margin-bottom: 12px;"
          />
          <button type="submit" style="padding: 10px 16px;">Reset Password</button>
        </form>
      </body>
    </html>
  `);
});

// POST /auth/reset-password/:token
router.post("/reset-password/:token", express.urlencoded({ extended: true }), async (req, res) => {
  try {
    const { token } = req.params;
    const { password } = req.body;

    if (!password) {
      return res.status(400).send("Password is required");
    }

    const user = await User.findOne({
      resetPasswordToken: token,
      resetPasswordExpires: { $gt: new Date() }
    });

    if (!user) {
      return res.status(400).send("Invalid or expired reset token");
    }

    user.passwordHash = await bcrypt.hash(password, 10);
    user.resetPasswordToken = null;
    user.resetPasswordExpires = null;

    await user.save();

    res.send("Password reset successful. You can now return to the app and log in.");
  } catch (err) {
    res.status(500).send("Server error");
  }
});

module.exports = router;