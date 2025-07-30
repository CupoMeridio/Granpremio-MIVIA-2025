@echo off
echo ================================
echo   AUTOMATIC DATA COLLECTION
echo ================================
echo Collecting data with SimpleDriver...
echo Data will be saved to: dataset.csv
echo ================================
echo Press Ctrl+C to interrupt collection...
echo.

rem Cleanup function to kill any remaining Java processes
for /f "tokens=*" %%a in ('tasklist ^| findstr /i "java.exe" ^| findstr /i "SimpleDriver"') do (
    echo Cleaning up Java processes...
    taskkill /f /im java.exe /fi "imagename eq java.exe" 2>nul
    timeout /t 1 /nobreak >nul
)

cd /d "%~dp0.."
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.SimpleDriver host:localhost port:3001 --collect
cd /d "%~dp0"
echo.
echo Collection completed!
echo File saved: dataset.csv