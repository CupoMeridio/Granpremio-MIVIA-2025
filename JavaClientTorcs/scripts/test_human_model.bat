@echo off
echo ================================
echo   TEST MODEL - Human Data
echo ================================
echo Testing behavioral cloning with manually collected data
echo Dataset: human_dataset.csv
echo ================================

rem Cleanup function to kill any remaining Java processes
for /f "tokens=*" %%a in ('tasklist ^| findstr /i "java.exe" ^| findstr /i "BehavioralCloningDriver"') do (
    echo Cleaning up Java processes...
    taskkill /f /im java.exe /fi "imagename eq java.exe" 2>nul
    timeout /t 1 /nobreak >nul
)

cd /d "%~dp0.."
if not exist "human_dataset.csv" (
    echo.
    echo ⚠️  WARNING: human_dataset.csv not found!
    echo.
    echo To test the model with human data you must first:
    echo 1. Run 'run_manual_driving.bat' to collect manual data
    echo 2. Or run 'torcs_menu.bat' and select option 1
    echo.
    pause
    exit /b
)

echo Press Ctrl+C to interrupt test...
echo.
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.BehavioralCloningDriver host:localhost port:3001
cd /d "%~dp0"
echo.
echo Test completed!