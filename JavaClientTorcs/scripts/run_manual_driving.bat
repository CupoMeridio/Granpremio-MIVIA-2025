@echo off
echo ==============================
echo   MANUAL DRIVING - Data Collection
echo ==============================
echo Controls:
echo - Arrow keys: Directional controls
echo - W/A/S/D: WASD gaming
echo - I/J/K/L: Alternative symmetric
echo - 8/2/4/6: Numeric keypad
echo - C: Toggle data collection
echo - P: Statistics
echo - R: Reset
echo - Q: Exit
echo ==============================
echo.

rem Cleanup function to kill any remaining Java processes
setlocal enabledelayedexpansion
for /f "tokens=*" %%a in ('tasklist ^| findstr /i "java.exe" ^| findstr /i "HumanController"') do (
    echo Cleaning up Java processes...
    taskkill /f /im java.exe /fi "imagename eq java.exe" 2>nul
    timeout /t 1 /nobreak >nul
)

cd /d "%~dp0\.."
java -cp dist\JavaClientTorcs.jar;lib\* it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.HumanController host:localhost port:3001
echo.
echo Driving session completed!