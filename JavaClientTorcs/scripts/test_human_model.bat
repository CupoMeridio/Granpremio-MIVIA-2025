@echo off
echo ================================
echo   TEST MODEL - Human Data
echo ================================
echo Testing behavioral cloning with manually collected data
echo Dataset: human_dataset.csv
echo ================================

rem Cleanup function to kill any remaining Java processes
echo Cleaning up Java processes...
taskkill /f /im java.exe 2>nul
timeout /t 1 /nobreak >nul

cd /d "%~dp0.."
if not exist "human_dataset.csv" (
    echo.
    echo WARNING: human_dataset.csv not found!
    echo.
    echo To test the model with human data you must first:
    echo 1. Run 'run_manual_driving.bat' to collect manual data
    echo 2. Or run 'torcs_menu.bat' and select option 1
    echo.
    exit /b
)

echo Press Ctrl+C to interrupt test...
echo.
java -cp dist\JavaClientTorcs.jar;lib\* it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.BehavioralCloningDriver human_dataset.csv
cd /d "%~dp0"
echo.
echo Test completed!