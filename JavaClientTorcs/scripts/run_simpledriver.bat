@echo off
echo ================================
echo   SIMPLE DRIVER - Execution
echo ================================
echo Starting SimpleDriver for basic autonomous driving
echo (without data collection)
echo ================================
echo Press Ctrl+C to interrupt execution...
echo.

rem Cleanup function to kill any remaining Java processes
for /f "tokens=*" %%a in ('tasklist ^| findstr /i "java.exe" ^| findstr /i "SimpleDriver"') do (
    echo Cleaning up Java processes...
    taskkill /f /im java.exe /fi "imagename eq java.exe" 2>nul
    timeout /t 1 /nobreak >nul
)

cd /d "%~dp0\.."
java -cp dist\JavaClientTorcs.jar;lib\* it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.SimpleDriver host:localhost port:3001 verbose:on
cd /d "%~dp0"
echo.
echo Execution completed!