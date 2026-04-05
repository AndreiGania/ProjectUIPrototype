const express = require("express");
const Announcement = require("../models/Announcement");

const router = express.Router();

// GET /announcements
router.get("/", async (req, res) => {
  const announcements = await Announcement.find().sort({ createdAt: -1 });
  res.json(announcements);
});

// POST /announcements
router.post("/", async (req, res) => {
  const { title, message } = req.body;

  if (!title || !message) {
    return res.status(400).json({ error: "title and message are required" });
  }

  const created = await Announcement.create({
    title,
    message
  });

  res.status(201).json(created);
});

module.exports = router;