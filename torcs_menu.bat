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
echo DATA MANAGEMENT:
echo 3. Combine Datasets
echo 4. View Dataset Statistics
echo.
echo AUTONOMOUS DRIVING:
echo 5. SimpleDriver (Basic Autonomous)
echo.
echo ARTIFICIAL INTELLIGENCE:
echo 6. Test KNN System
echo 7. KNN Driving (Human Dataset)
echo 8. KNN Driving (Auto Dataset)
echo.
echo DOCUMENTATION:
echo 9. Open Complete Guide
echo.
echo TORCS GAME:
echo 0. Start TORCS Game
echo.
echo EXIT:
echo X. Exit program
echo.
echo =================================================
echo   NOTE: Press Ctrl+C to interrupt any operation
echo =================================================
set /p choice="Select option (0-9, X): "

if "%choice%"=="1" goto manual
if "%choice%"=="2" goto auto
if "%choice%"=="3" goto combine
if "%choice%"=="4" goto stats
if "%choice%"=="5" goto simpledriver
if "%choice%"=="6" goto testknn
if "%choice%"=="7" goto knndriving_human
if "%choice%"=="8" goto knndriving_auto
if "%choice%"=="9" goto guide
if "%choice%"=="0" goto torcs
if "%choice%"=="X" goto exit
if "%choice%"=="x" goto exit

if "%choice%"=="0" goto exit
echo Invalid choice! Press any key to continue...
pause >nul
goto menu

:manual
call "%~dp0JavaClientTorcs\scripts\run_manual_driving.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:auto
call "%~dp0JavaClientTorcs\scripts\run_auto_collection.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu



:combine
call "%~dp0JavaClientTorcs\scripts\combine_datasets.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:stats
	cd /d "%~dp0JavaClientTorcs"
	echo.
	echo === DATASET STATISTICS ===
	if exist "dataset.csv" (
	    echo dataset.csv: 
	    find /c /v "" dataset.csv
	) else (
	    echo dataset.csv: Not found
	)
	if exist "enhanced_dataset.csv" (
	    echo enhanced_dataset.csv: 
	    find /c /v "" enhanced_dataset.csv
	) else (
	    echo enhanced_dataset.csv: Not found
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
	cd /d "%~dp0"
echo Press any key to return to menu...
pause >nul
goto menu

:simpledriver
call "%~dp0JavaClientTorcs\scripts\run_simpledriver.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:guide
start notepad "%~dp0README.md"
goto menu

:torcs
echo.
echo Starting TORCS...
echo Please wait...
cd /d "C:\Program Files (x86)\torcs"
start "" wtorcs.exe
echo.
echo TORCS has been launched!
echo You can now configure the race and connect the AI drivers.
echo.
echo Press any key to return to menu...
pause >nul
goto menu

:testknn
call "%~dp0JavaClientTorcs\scripts\test_knn.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:knndriving_human
call "%~dp0JavaClientTorcs\scripts\run_knn_driving_human.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:knndriving_auto
call "%~dp0JavaClientTorcs\scripts\run_knn_driving_auto.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:exit
echo Goodbye!
timeout /t 2 >nul