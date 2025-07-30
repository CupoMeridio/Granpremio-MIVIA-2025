@echo off
echo ================================
echo   COMBINE DATASETS
echo ================================
echo Combining automatic and manual datasets...
echo Input: dataset.csv + human_dataset.csv
echo Output: combined_dataset.csv
echo ================================
cd /d "%~dp0.."

:: Check if files exist
if not exist "dataset.csv" (
    echo ERROR: dataset.csv not found!
    echo Run run_auto_collection.bat first
    pause
    exit /b
)

if not exist "human_dataset.csv" (
    echo ERROR: human_dataset.csv not found!
    echo Run run_manual_driving.bat first
    pause
    exit /b
)

:: Combine files
echo Combining datasets...
copy /b dataset.csv + human_dataset.csv combined_dataset.csv >nul

echo.
echo Datasets combined successfully!
echo File created: combined_dataset.csv
echo Total rows:
find /c /v "" combined_dataset.csv
cd /d "%~dp0"