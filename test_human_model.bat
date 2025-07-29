@echo off
echo ================================
echo   TEST MODEL - Human Data
echo ================================
echo Testing behavioral cloning with manually collected data
echo Dataset: human_dataset.csv
echo ================================

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
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.BehavioralCloningDriver host:localhost port:3001
cd ..
echo.
echo Test completed!