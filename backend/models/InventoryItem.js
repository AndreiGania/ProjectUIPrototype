const mongoose = require("mongoose");

const InventoryItemSchema = new mongoose.Schema(
  {
    name: { type: String, required: true, trim: true },
    quantity: { type: Number, required: true, default: 0 },
    unit: { type: String, default: "" },
    lowStockThreshold: { type: Number, default: 0 }
  },
  { timestamps: true }
);

module.exports = mongoose.model("InventoryItem", InventoryItemSchema);
