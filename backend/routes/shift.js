const express = require("express");
const Shift = require("../models/Shift");

const router = express.Router();

router.get("/", async (req, res) => {
  try {
    const isManager = req.user.role === "manager" || req.user.role === "admin";

    const query = isManager
      ? {}
      : { employeeUsername: req.user.username };

    const shifts = await Shift.find(query).sort({ start: 1 });
    res.json(shifts);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

router.post("/", async (req, res) => {
  try {
    if (req.user.role !== "manager" && req.user.role !== "admin") {
      return res.status(403).json({ error: "Manager only" });
    }

    const { employeeId, employeeUsername, start, end, position, notes } = req.body;

    if (!employeeId || !employeeUsername || !start || !end || !position) {
      return res.status(400).json({
        error: "employeeId, employeeUsername, start, end, position are required"
      });
    }

    const created = await Shift.create({
      employeeId,
      employeeUsername,
      start: new Date(start),
      end: new Date(end),
      position,
      notes: notes ?? ""
    });

    res.status(201).json(created);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

router.put("/:id", async (req, res) => {
  try {
    if (req.user.role !== "manager" && req.user.role !== "admin") {
      return res.status(403).json({ error: "Manager only" });
    }

    const updated = await Shift.findByIdAndUpdate(req.params.id, req.body, { new: true });
    if (!updated) return res.status(404).json({ error: "Not found" });

    res.json(updated);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

router.delete("/:id", async (req, res) => {
  try {
    if (req.user.role !== "manager" && req.user.role !== "admin") {
      return res.status(403).json({ error: "Manager only" });
    }

    const deleted = await Shift.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ error: "Not found" });

    res.json({ ok: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;