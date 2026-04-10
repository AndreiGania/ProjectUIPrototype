const express = require("express");
const Announcement = require("../models/Announcement");

const router = express.Router();

// GET ALL announcements (newest first)
router.get("/", async (req, res) => {
  try {
    const announcements = await Announcement.find().sort({ createdAt: -1 });
    res.json(announcements);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET latest announcement (BEST for dashboard)
router.get("/latest", async (req, res) => {
  try {
    const latest = await Announcement.findOne().sort({ createdAt: -1 });

    if (!latest) {
      return res.status(404).json({ message: "No announcements found" });
    }

    res.json(latest);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// CREATE new announcement
router.post("/", async (req, res) => {
  try {
    const { title, message } = req.body;

    if (!title || !message) {
      return res.status(400).json({ error: "title and message are required" });
    }

    const created = await Announcement.create({
      title,
      message
    });

    res.status(201).json(created);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// DELETE announcement by id
router.delete("/:id", async (req, res) => {
  try {
    const deleted = await Announcement.findByIdAndDelete(req.params.id);

    if (!deleted) {
      return res.status(404).json({ error: "Announcement not found" });
    }

    res.json({ message: "Announcement deleted successfully" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;