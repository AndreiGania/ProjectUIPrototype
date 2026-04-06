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
    const user = await User.findOne({
      emailVerificationToken: req.params.token
    });

    if (!user) {
      return res.send(`
        <!DOCTYPE html>
        <html>
        <head>
          <title>Verification Failed</title>
          <style>
            body {
              margin: 0;
              font-family: Arial, sans-serif;
              background: linear-gradient(rgba(40, 24, 16, 0.78), rgba(40, 24, 16, 0.78)),
                          url('/images/IMG_5925.jpg')
                          no-repeat center center fixed;
              background-size: cover;
              display: flex;
              justify-content: center;
              align-items: center;
              min-height: 100vh;
            }

            .card {
              width: 90%;
              max-width: 430px;
              background: rgba(34, 20, 14, 0.92);
              border-radius: 22px;
              padding: 32px 28px;
              box-shadow: 0 8px 24px rgba(0,0,0,0.35);
              color: #fff8f0;
              text-align: center;
            }

            h1 {
              margin: 0 0 14px;
              color: #f3d9a3;
              font-size: 28px;
            }

            p {
              color: #f8ead7;
              font-size: 16px;
              line-height: 1.6;
              margin-bottom: 22px;
            }

            .icon {
              font-size: 52px;
              margin-bottom: 12px;
            }

            .btn {
              display: inline-block;
              background: #d4a017;
              color: white;
              text-decoration: none;
              padding: 12px 24px;
              border-radius: 999px;
              font-weight: bold;
              margin-top: 10px;
            }

            .btn:hover {
              background: #b8860b;
            }
          </style>
        </head>
        <body>
          <div class="card">
            <div class="icon">☕</div>
            <h1>Verification Failed</h1>
            <p>This email verification link is invalid or has already been used.</p>
            <a class="btn" href="#">Return to App</a>
          </div>
        </body>
        </html>
      `);
    }

    user.isVerified = true;
    user.emailVerificationToken = null;
    await user.save();

    res.send(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>Email Verified</title>
        <style>
          body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: linear-gradient(rgba(40, 24, 16, 0.78), rgba(40, 24, 16, 0.78)),
                        url('/images/IMG_5925.jpg')
                        no-repeat center center fixed;
            background-size: cover;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
          }

          .card {
            width: 90%;
            max-width: 430px;
            background: rgba(34, 20, 14, 0.92);
            border-radius: 22px;
            padding: 32px 28px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.35);
            color: #fff8f0;
            text-align: center;
          }

          h1 {
            margin: 0 0 14px;
            color: #f3d9a3;
            font-size: 28px;
          }

          p {
            color: #f8ead7;
            font-size: 16px;
            line-height: 1.6;
            margin-bottom: 22px;
          }

          .icon {
            font-size: 52px;
            margin-bottom: 12px;
          }

          .btn {
            display: inline-block;
            background: #d4a017;
            color: white;
            text-decoration: none;
            padding: 12px 24px;
            border-radius: 999px;
            font-weight: bold;
            margin-top: 10px;
          }

          .btn:hover {
            background: #b8860b;
          }
        </style>
      </head>
      <body>
        <div class="card">
          <div class="icon">✅</div>
          <h1>Email Verified</h1>
          <p>Your PointSeven Cafe account has been confirmed successfully.</p>
          <p>You can now return to the app and log in.</p>
          <a class="btn" href="#">Back to Login</a>
        </div>
      </body>
      </html>
    `);
  } catch (err) {
    res.status(500).send("Server error");
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
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>PointSeven Cafe - Reset Password</title>
      <style>
        * {
          box-sizing: border-box;
        }

        body {
          margin: 0;
          font-family: Arial, sans-serif;
          min-height: 100vh;
          background:
            linear-gradient(rgba(33, 20, 12, 0.72), rgba(33, 20, 12, 0.72)),
            url('/images/IMG_5925.jpg')
            center/cover no-repeat;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 24px;
        }

        .card {
          width: 100%;
          max-width: 440px;
          background: rgba(44, 28, 19, 0.92);
          border-radius: 22px;
          padding: 32px 28px;
          box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
          color: #fff8f0;
          backdrop-filter: blur(6px);
        }

        .brand {
          text-align: center;
          margin-bottom: 10px;
          font-size: 13px;
          letter-spacing: 2px;
          color: #d9b07a;
          text-transform: uppercase;
        }

        h2 {
          margin: 0 0 10px;
          text-align: center;
          font-size: 28px;
          color: #f7e7d3;
        }

        .subtitle {
          text-align: center;
          font-size: 14px;
          line-height: 1.5;
          color: #ead6bf;
          margin-bottom: 24px;
        }

        label {
          display: block;
          margin-bottom: 8px;
          font-size: 14px;
          font-weight: bold;
          color: #f5ddbf;
        }

        .input-group {
          margin-bottom: 18px;
        }

        input[type="password"] {
          width: 100%;
          padding: 14px 16px;
          border-radius: 14px;
          border: 1px solid rgba(217, 176, 122, 0.35);
          background: rgba(255, 248, 240, 0.08);
          color: #fff;
          font-size: 15px;
          outline: none;
        }

        input[type="password"]::placeholder {
          color: #d9c3ab;
        }

        input[type="password"]:focus {
          border-color: #d9b07a;
          box-shadow: 0 0 0 3px rgba(217, 176, 122, 0.18);
        }

        .hint {
          font-size: 12px;
          color: #d8c1a7;
          margin-top: 6px;
          line-height: 1.4;
        }

        .btn {
          width: 100%;
          border: none;
          border-radius: 999px;
          padding: 14px 18px;
          font-size: 15px;
          font-weight: bold;
          cursor: pointer;
          background: linear-gradient(90deg, #b7792b, #d4a017);
          color: white;
          margin-top: 8px;
        }

        .btn:hover {
          opacity: 0.95;
        }

        .footer-note {
          margin-top: 18px;
          text-align: center;
          font-size: 12px;
          color: #cdb69c;
        }

        .error-box {
          background: rgba(180, 60, 40, 0.18);
          border: 1px solid rgba(255, 120, 90, 0.35);
          color: #ffd7ce;
          border-radius: 12px;
          padding: 10px 12px;
          margin-bottom: 16px;
          font-size: 13px;
          display: none;
        }
      </style>
    </head>
    <body>
      <div class="card">
        <div class="brand">PointSeven Cafe</div>
        <h2>Reset Password</h2>
        <div class="subtitle">
          Create a new password for your account.
          Keep it secure and easy for you to remember.
        </div>

        <div id="errorBox" class="error-box"></div>

        <form id="resetForm" method="POST" action="/auth/reset-password/${token}">
          <div class="input-group">
            <label for="password">New Password</label>
            <input
              id="password"
              type="password"
              name="password"
              placeholder="Enter new password"
              required
              minlength="6"
            />
            <div class="hint">
              Password must be at least 6 characters long.
            </div>
          </div>

          <div class="input-group">
            <label for="confirmPassword">Confirm New Password</label>
            <input
              id="confirmPassword"
              type="password"
              name="confirmPassword"
              placeholder="Confirm new password"
              required
              minlength="6"
            />
          </div>

          <button class="btn" type="submit">Set New Password</button>
        </form>

        <div class="footer-note">
          PointSeven Cafe Account Security
        </div>
      </div>

      <script>
        const form = document.getElementById("resetForm");
        const password = document.getElementById("password");
        const confirmPassword = document.getElementById("confirmPassword");
        const errorBox = document.getElementById("errorBox");

        form.addEventListener("submit", function (e) {
          errorBox.style.display = "none";
          errorBox.textContent = "";

          if (password.value.length < 6) {
            e.preventDefault();
            errorBox.textContent = "Password must be at least 6 characters.";
            errorBox.style.display = "block";
            return;
          }

          if (password.value !== confirmPassword.value) {
            e.preventDefault();
            errorBox.textContent = "Passwords do not match.";
            errorBox.style.display = "block";
          }
        });
      </script>
    </body>
    </html>
  `);
});

router.post("/reset-password/:token", express.urlencoded({ extended: true }), async (req, res) => {
  try {
    const { token } = req.params;
    const { password, confirmPassword } = req.body;

    if (!password || !confirmPassword) {
      return res.status(400).send("Both password fields are required.");
    }

    if (password.length < 6) {
      return res.status(400).send("Password must be at least 6 characters.");
    }

    if (password !== confirmPassword) {
      return res.status(400).send("Passwords do not match.");
    }

    const user = await User.findOne({
      resetPasswordToken: token,
      resetPasswordExpires: { $gt: new Date() }
    });

    if (!user) {
      return res.status(400).send("Invalid or expired reset token.");
    }

    user.passwordHash = await bcrypt.hash(password, 10);
    user.resetPasswordToken = null;
    user.resetPasswordExpires = null;

    await user.save();

    res.send(`
      <!DOCTYPE html>
      <html lang="en">
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Password Reset Successful</title>
        <style>
          body {
            margin: 0;
            font-family: Arial, sans-serif;
            min-height: 100vh;
            background:
              linear-gradient(rgba(33, 20, 12, 0.72), rgba(33, 20, 12, 0.72)),
              url('/images/IMG_5925.jpg')
              center/cover no-repeat;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
          }

          .card {
            width: 100%;
            max-width: 420px;
            background: rgba(44, 28, 19, 0.92);
            border-radius: 22px;
            padding: 32px 28px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
            color: #fff8f0;
            text-align: center;
          }

          h2 {
            margin-top: 0;
            color: #f7e7d3;
          }

          p {
            color: #ead6bf;
            line-height: 1.5;
          }

          .tag {
            color: #d9b07a;
            text-transform: uppercase;
            letter-spacing: 2px;
            font-size: 12px;
            margin-bottom: 12px;
          }
        </style>
      </head>
      <body>
        <div class="card">
          <div class="tag">PointSeven Cafe</div>
          <h2>Password Updated</h2>
          <p>Your password has been reset successfully.</p>
          <p>You can now return to the app and log in with your new password.</p>
        </div>
      </body>
      </html>
    `);
  } catch (err) {
    res.status(500).send("Server error");
  }
});

module.exports = router;