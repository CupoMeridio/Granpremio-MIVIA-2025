@echo off
setlocal enabledelayedexpansion

:menu
title TORCS - Main Menu
color 0A
cls
echo ==========================
echo   TORCS - COMPLETE MENU
echo ==========================
echo.
echo DATA COLLECTION:
echo 1. Manual Driving (Data Collection)
echo 2. Automatic Data Collection
echo.
echo MODEL TESTING:
echo 3. Test Model - Human Data
echo 4. Test Model - Automatic Data
echo.
echo DATA MANAGEMENT:
echo 5. Combine Datasets
echo 6. View Dataset Statistics
echo.
echo AUTONOMOUS DRIVING:
echo 7. SimpleDriver (Basic Autonomous)
echo.
echo DOCUMENTATION:
echo 8. Open Complete Guide
echo.
echo TORCS GAME:
echo 9. Launch TORCS Game
echo.
echo EXIT:
echo 0. Exit program
echo.
echo =================================================
echo   NOTE: Press Ctrl+C to interrupt any operation
echo =================================================
set /p choice="Select option (0-9): "

if "%choice%"=="1" goto manual
if "%choice%"=="2" goto auto
if "%choice%"=="3" goto test_human
if "%choice%"=="4" goto test_auto
if "%choice%"=="5" goto combine
if "%choice%"=="6" goto stats
if "%choice%"=="7" goto simpledriver
if "%choice%"=="8" goto guide
if "%choice%"=="9" goto torcs_game
if "%choice%"=="0" goto exit
echo Invalid choice! Press any key to continue...
pause >nul
goto menu

:manual
call "%~dp0run_manual_driving.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:auto
call "%~dp0run_auto_collection.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:test_human
call "%~dp0test_human_model.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:test_auto
call "%~dp0test_auto_model.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:combine
call "%~dp0combine_datasets.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:stats
cd JavaClientTorcs
echo.
echo === DATASET STATISTICS ===
if exist "dataset.csv" (
    echo dataset.csv: 
    find /c /v "" dataset.csv
) else (
    echo dataset.csv: Not found
)
if exist "human_dataset.csv" (
    echo human_dataset.csv: 
    find /c /v "" human_dataset.csv
) else (
    echo human_dataset.csv: Not found
)
if exist "combined_dataset.csv" (
    echo combined_dataset.csv: 
    find /c /v "" combined_dataset.csv
) else (
    echo combined_dataset.csv: Not found
)
echo.
echo Press any key to return to menu...
pause >nul
goto menu

:simpledriver
call "%~dp0run_simpledriver.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:guide
start notepad "%~dp0README.md"
goto menu

:torcs_game
echo.
echo Launching TORCS - The Open Racing Car Simulator...
echo.
start "" "C:\Users\cupom\Desktop\TORCS - The Open Racing Car Simulator.lnk"
goto menu

:exit
echo Goodbye!
timeout /t 2 >nul