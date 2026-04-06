# ☕ PointSeven Cafe Management System

## 📱 Mobile Application (Android)

The PointSeven Cafe Mobile App helps streamline cafe operations by providing tools for staff and managers to manage daily tasks efficiently.

### ✨ Features
- User Authentication (Login / Register)
- Inventory Management
- Staff Scheduling
- Announcements System
- Role-Based Access (Manager / Staff / Customer)

---

## 🚀 Getting Started

Follow these steps to run the mobile application locally.

---

## 📋 Requirements

- Android Studio
- Java (JDK 8 or higher)
- Node.js backend server

---

## ⚙️ Start the Backend Server

The mobile app requires the backend server to be running.

### Quick Start (Recommended)

start-backend.bat

---

### Manual Setup (If script doesn’t work)

cd backend
npm install
npm start

---

## 📱 Run the Mobile App

1. Open the project in Android Studio
2. Wait for Gradle to sync
3. Start an emulator or connect a device
4. Click Run ▶️

---

## 🔑 Demo Login Credentials

Use the following admin account to access the system for testing:

- **Username:** `admin`
- **Password:** `admin123`

---

## ⚠️ Troubleshooting

App cannot connect to backend:
- Ensure backend is running
- Verify API URL is correct
- Check firewall / port 3000

Emulator issues:
- Android Studio → Device Manager → Wipe Data → Restart

---

## 📌 Notes

- Backend must be running before launching the app
- First run may take longer due to Gradle build
- Ensure .env file is configured for backend