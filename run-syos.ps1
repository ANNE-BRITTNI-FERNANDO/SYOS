# SYOS PowerShell Launcher
# Store Your Outstanding Stock Management System

Write-Host "Starting SYOS (Store Your Outstanding Stock) Management System..." -ForegroundColor Green
Write-Host ""

# Change to the project directory
Set-Location "c:\Users\ASUS\Desktop\SE Y3 S1\CCCP 1\SYOS - Test"

try {
    # Run the main application
    Write-Host "Launching SYOS Main Application..." -ForegroundColor Yellow
    java -cp "target/dependency/*;target/classes" syos.Main
}
catch {
    Write-Host "Error launching SYOS: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")