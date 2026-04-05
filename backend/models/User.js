const mongoose = require("mongoose");

const UserSchema = new mongoose.Schema(
  {
    name: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    username: { type: String, required: true, unique: true },
    passwordHash: { type: String, required: true },
    role: { type: String, enum: ["manager", "staff", "customer"], default: "staff" },

    resetPasswordToken: { type: String, default: null },
    resetPasswordExpires: { type: Date, default: null },

    isVerified: { type: Boolean, default: false },
    emailVerificationToken: { type: String, default: null }
  },
  { timestamps: true }
);

module.exports = mongoose.model("User", UserSchema);