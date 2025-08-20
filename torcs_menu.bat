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
echo.
echo DATA MANAGEMENT:
echo 2. View Dataset Statistics
echo 3. Convert Enhanced to Human Dataset
echo 4. Open Dataset Analysis Tools (Google Colab)
echo.
echo AUTONOMOUS DRIVING:
echo 5. SimpleDriver (Basic Autonomous)
echo.
echo ARTIFICIAL INTELLIGENCE:
echo 6. KNN Driving (Human Dataset)
echo 7. KNN Classifier (Discrete Actions)
echo 8. MLP Driving (Neural Network)
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
if "%choice%"=="2" goto stats
if "%choice%"=="3" goto convert
if "%choice%"=="4" goto colab
if "%choice%"=="5" goto simpledriver
if "%choice%"=="6" goto knndriving_human
if "%choice%"=="7" goto knnclassifier
if "%choice%"=="8" goto mlpdriving
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

:convert
echo ========================================
echo   CONVERT ENHANCED TO HUMAN DATASET
echo ========================================
echo.
cd /d "%~dp0JavaClientTorcs"
if not exist "enhanced_dataset.csv" (
    echo [ERROR] enhanced_dataset.csv not found!
    echo Please run data collection first.
    echo.
    echo Press any key to return to menu...
    pause >nul
    goto menu
)

echo [INFO] Converting enhanced_dataset.csv to human_dataset.csv...
echo.
java -cp "dist\JavaClientTorcs.jar;lib\*" it.unisa.javaclienttorcs.DatasetConverter enhanced_dataset.csv human_dataset.csv

if errorlevel 1 (
    echo [ERROR] Conversion failed!
    echo.
) else (
    echo [SUCCESS] Conversion completed successfully!
    echo.
    if exist "human_dataset.csv" (
        echo File created: human_dataset.csv
        for %%A in ("human_dataset.csv") do echo Size: %%~zA bytes
        find /c /v "" human_dataset.csv | find /v "------"
    )
)
echo.
cd /d "%~dp0"
echo Press any key to return to menu...
pause >nul
goto menu

:stats
	cd /d "%~dp0JavaClientTorcs"
	color 0B
	echo.
	echo ========================================
	echo           DATASET STATISTICS
	echo ========================================
	echo.
	
	REM Check human_dataset.csv
	    echo [✓] HUMAN DATASET ^(human_dataset.csv^)
	    for %%A in ("human_dataset.csv") do (
	        set /a size_kb=%%~zA/1024
	        echo     Size: %%~zA bytes ^(!size_kb! KB^)
	        echo     Modified: %%~tA
	    )
	    for /f "tokens=3" %%i in ('find /c /v "" human_dataset.csv 2^>nul') do (
	        set /a total_lines=%%i
	        set /a data_records=%%i-1
	        echo     Total lines: !total_lines!
	        if !data_records! gtr 0 (
	            echo     Data records: !data_records! training points
	        ) else (
	            echo     Data records: 0 ^(empty or header only^)
	        )
	    )
	    echo     Status: Ready for KNN training
	) else (
	    echo [✗] HUMAN DATASET ^(human_dataset.csv^)
	    echo     Status: NOT FOUND - Run manual data collection first
	)
	echo.if exist "human_dataset.csv" (
	
	
	REM Check enhanced_dataset.csv
	if exist "enhanced_dataset.csv" (
	    echo [✓] ENHANCED DATASET ^(enhanced_dataset.csv^)
	    for %%A in ("enhanced_dataset.csv") do (
	        set /a size_kb=%%~zA/1024
	        echo     Size: %%~zA bytes ^(!size_kb! KB^)
	        echo     Modified: %%~tA
	    )
	    for /f "tokens=3" %%i in ('find /c /v "" enhanced_dataset.csv 2^>nul') do (
	        set /a total_lines=%%i
	        set /a data_records=%%i-1
	        echo     Total lines: !total_lines!
	        if !data_records! gtr 0 (
	            echo     Data records: !data_records! sensor readings
	        ) else (
	            echo     Data records: 0 ^(empty or header only^)
	        )
	    )
	    echo     Status: Complete sensor data available
	) else (
	    echo [✗] ENHANCED DATASET ^(enhanced_dataset.csv^)
	    echo     Status: NOT FOUND - Run manual data collection to generate
	)
	echo.
	
	echo ========================================
	echo SUMMARY:
	if exist "human_dataset.csv" (
	    echo • Human dataset: AVAILABLE - Ready for AI training
	) else (
	    echo • Human dataset: MISSING - Collect data first
	)
	if exist "enhanced_dataset.csv" (
	    echo • Enhanced dataset: AVAILABLE - Full sensor data
	) else (
	    echo • Enhanced dataset: MISSING - Run data collection
	)
	echo ========================================
	echo.
	color 0A
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



:knndriving_human
call "%~dp0JavaClientTorcs\scripts\run_knn_driving_human.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu

:knnclassifier
call "%~dp0JavaClientTorcs\scripts\run_knn_classifier.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu


:mlpdriving
echo ========================================
echo        MLP AUTONOMOUS DRIVING
echo ========================================
echo.
echo [INFO] Starting MLP Python server...
echo Please wait while the MLP model loads...
echo.

REM Start Python MLP server in background
start "MLP Server" /min "%~dp0.venv\Scripts\python.exe" "%~dp0mlpDriver\mlpDrive.py"

REM Wait a moment for the server to start
timeout /t 3 >nul

echo [INFO] MLP server started successfully!
echo [INFO] Starting TORCS and MLP driver...
echo.

REM Start the Java MLP driver
call "%~dp0JavaClientTorcs\scripts\run_mlp_driving_human.bat"
echo.
echo Press any key to continue...
pause >nul
goto menu


:colab
echo =======================================
echo  DATASET ANALYSIS TOOLS - GOOGLE COLAB
echo =======================================
echo.
echo Opening Google Colab notebook for dataset analysis...
echo This tool provides:
echo • Dataset normalization and balancing
echo • Feature analysis and visualization
echo • Data quality assessment
echo • Statistical insights
echo.
echo The browser will open automatically.
echo.
start "" "https://colab.research.google.com/drive/1k-cV_NJBRxCdNuzrNbqFrxhazVwFKo3e?usp=sharing"
echo.
echo Google Colab opened in your default browser!
echo You can now upload your dataset files and use the analysis tools.
echo.
echo Press any key to return to menu...
pause >nul
goto menu

:exit
echo Goodbye!
timeout /t 2 >nul