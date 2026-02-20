require("dotenv").config();
const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const jwt = require("jsonwebtoken");//test
const authRoutes = require("./routes/auth");
const shiftsRoutes = require("./routes/shift");
const userRoutes = require("./routes/users");
const auth = require("./middleware/auth");
const inventoryRoutes = require("./routes/inventory");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/users", auth, userRoutes);
app.use("/auth", authRoutes);
app.use("/inventory", auth, inventoryRoutes);
app.use("/shifts", auth, shiftsRoutes);

//test
function authMiddleware(req, res, next) {
  const header = req.headers.authorization;

  if (!header) return res.status(401).json({ error: "No token provided" });

  const token = header.split(" ")[1];

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch (err) {
    return res.status(401).json({ error: "Invalid token" });
  }
}
app.get("/protected", authMiddleware, (req, res) => {
  res.json({
    message: "You accessed a protected route!",
    user: req.user
  });
});
//-----

app.get("/", (req, res) => {
  res.json({ message: "PointSeventhCafe API running" });
});

mongoose.connect(process.env.MONGODB_URI)
  .then(() => console.log("MongoDB connected"))
  .catch(err => console.error("MongoDB connection error:", err));

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
