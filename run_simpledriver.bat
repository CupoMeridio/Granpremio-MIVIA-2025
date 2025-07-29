@echo off
echo ================================
echo   SIMPLE DRIVER - Execution
echo ================================
echo Starting SimpleDriver for basic autonomous driving
echo (without data collection)
echo ================================
echo Press Ctrl+C to interrupt execution...
echo.
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.SimpleDriver host:localhost port:3001 verbose:on
cd ..
echo.
echo Execution completed!