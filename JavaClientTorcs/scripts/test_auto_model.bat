@echo off
echo ================================
echo   TEST MODEL - Automatic Data
echo ================================
echo Testing behavioral cloning with automatically collected data
echo Dataset: dataset.csv
echo ================================

rem Cleanup function to kill any remaining Java processes
for /f "tokens=*" %%a in ('tasklist ^| findstr /i "java.exe" ^| findstr /i "BehavioralCloningDriver"') do (
    echo Cleaning up Java processes...
    taskkill /f /im java.exe /fi "imagename eq java.exe" 2>nul
    timeout /t 1 /nobreak >nul
)

cd /d "%~dp0.."
if not exist "dataset.csv" (
    echo.
    echo WARNING: dataset.csv not found!
    echo.
    echo To test the model with automatic data you must first:
    echo 1. Run 'run_auto_collection.bat' to collect data
    echo 2. Or run 'torcs_menu.bat' and select option 2
    
    exit /b
)

echo Press Ctrl+C to interrupt test...
echo.
java -cp dist\JavaClientTorcs.jar;lib\* it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.BehavioralCloningDriver dataset.csv
cd /d "%~dp0"
echo.
echo Test completed!