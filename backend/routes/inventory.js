const express = require("express");
const InventoryItem = require("../models/InventoryItem");

const router = express.Router();

// GET /inventory
router.get("/", async (req, res) => {
  const items = await InventoryItem.find().sort({ createdAt: -1 });
  res.json(items);
});

// POST /inventory
router.post("/", async (req, res) => {
  const { name, quantity, unit, lowStockThreshold } = req.body;

  if (!name) return res.status(400).json({ error: "name is required" });

  const created = await InventoryItem.create({
    name,
    quantity: Number(quantity ?? 0),
    unit: unit ?? "",
    lowStockThreshold: Number(lowStockThreshold ?? 0)
  });

  res.status(201).json(created);
});

// PUT /inventory/:id
router.put("/:id", async (req, res) => {
  const updated = await InventoryItem.findByIdAndUpdate(
    req.params.id,
    req.body,
    { new: true }
  );
  if (!updated) return res.status(404).json({ error: "Not found" });
  res.json(updated);
});

// DELETE /inventory/:id
router.delete("/:id", async (req, res) => {
  const deleted = await InventoryItem.findByIdAndDelete(req.params.id);
  if (!deleted) return res.status(404).json({ error: "Not found" });
  res.json({ ok: true });
});

module.exports = router;
