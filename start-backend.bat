@echo off
echo Starting PointSeven Cafe Backend...
cd /d "%~dp0backend" || (
  echo ERROR: backend folder not found
  pause
  exit /b
)

if not exist package.json (
  echo ERROR: package.json not found in backend folder
  pause
  exit /b
)

echo Installing dependencies...
call npm install

echo Starting server...
call npm start

pause