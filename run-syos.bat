@echo off
REM SYOS Main Application Launcher
REM This batch file provides an easy way to start the SYOS system

echo Starting SYOS (Store Your Outstanding Stock) Management System...
echo.

REM Change to the project directory
cd /d "c:\Users\ASUS\Desktop\SE Y3 S1\CCCP 1\SYOS - Test"

REM Run the main application
java -cp "target/dependency/*;target/classes" syos.Main

REM Pause to see any error messages
echo.
echo Press any key to exit...
pause > nul