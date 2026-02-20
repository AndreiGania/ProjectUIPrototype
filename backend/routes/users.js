const express = require("express");
const User = require("../models/User");

const router = express.Router();

// Only allow managers/admins
function requireManager(req, res, next) {
  const role = req.user?.role;
  if (role !== "manager" && role !== "admin") {
    return res.status(403).json({ error: "Manager only" });
  }
  next();
}

// GET /users  (manager/admin only)
router.get("/", requireManager, async (req, res) => {
  try {
    const users = await User.find({}, { passwordHash: 0 }).sort({ username: 1 });
    // return safe fields only
    res.json(
      users.map(u => ({
        id: u._id.toString(),
        name: u.name,
        email: u.email,
        username: u.username,
        role: u.role
      }))
    );
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PATCH /users/:id/promote  (manager/admin only)
router.patch("/:id/promote", requireManager, async (req, res) => {
  try {
    const { id } = req.params;

    const user = await User.findById(id);
    if (!user) return res.status(404).json({ error: "User not found" });

    // prevent promoting admins (optional safety)
    if (user.role === "admin") {
      return res.status(400).json({ error: "Cannot change admin role" });
    }

    user.role = "manager";
    await user.save();

    res.json({
      id: user._id.toString(),
      name: user.name,
      email: user.email,
      username: user.username,
      role: user.role
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;