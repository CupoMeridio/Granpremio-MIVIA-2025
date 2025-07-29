@echo off
echo ================================
echo   TEST MODEL - Automatic Data
echo ================================
echo Testing behavioral cloning with automatically collected data
echo Dataset: dataset.csv
echo ================================

if not exist "dataset.csv" (
    echo.
    echo ⚠️  WARNING: dataset.csv not found!
    echo.
    echo To test the model with automatic data you must first:
    echo 1. Run 'run_auto_collection.bat' to collect data
    echo 2. Or run 'torcs_menu.bat' and select option 2
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