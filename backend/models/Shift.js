const mongoose = require("mongoose");

const ShiftSchema = new mongoose.Schema(
  {
    employeeId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    employeeUsername: { type: String, required: true }, // easy filtering without join
    start: { type: Date, required: true },
    end: { type: Date, required: true },
    role: { type: String, default: "employee" },
    notes: { type: String, default: "" }
  },
  { timestamps: true }
);

module.exports = mongoose.model("Shift", ShiftSchema);