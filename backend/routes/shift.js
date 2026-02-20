const express = require("express");
const Shift = require("../models/Shift");

const router = express.Router();


router.get("/", async (req, res) => {
  const isManager = req.user.role === "manager";

  const query = isManager
    ? {}
    : { employeeUsername: req.user.username };

  const shifts = await Shift.find(query).sort({ start: 1 });
  res.json(shifts);
});

router.post("/", async (req, res) => {
  if (req.user.role !== "manager") {
    return res.status(403).json({ error: "Manager only" });
  }

  const { employeeId, employeeUsername, start, end, role, notes } = req.body;

  if (!employeeId || !employeeUsername || !start || !end) {
    return res.status(400).json({ error: "employeeId, employeeUsername, start, end are required" });
  }

  const created = await Shift.create({
    employeeId,
    employeeUsername,
    start: new Date(start),
    end: new Date(end),
    role: role ?? "employee",
    notes: notes ?? ""
  });

  res.status(201).json(created);
});

router.put("/:id", async (req, res) => {
  if (req.user.role !== "manager") {
    return res.status(403).json({ error: "Manager only" });
  }

  const updated = await Shift.findByIdAndUpdate(req.params.id, req.body, { new: true });
  if (!updated) return res.status(404).json({ error: "Not found" });
  res.json(updated);
});

router.delete("/:id", async (req, res) => {
  if (req.user.role !== "manager") {
    return res.status(403).json({ error: "Manager only" });
  }

  const deleted = await Shift.findByIdAndDelete(req.params.id);
  if (!deleted) return res.status(404).json({ error: "Not found" });
  res.json({ ok: true });
});

module.exports = router;