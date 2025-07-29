@echo off
echo ================================
echo   AUTOMATIC DATA COLLECTION
echo ================================
echo Collecting data with SimpleDriver...
echo Data will be saved to: dataset.csv
echo ================================
echo Press Ctrl+C to interrupt collection...
echo.
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.SimpleDriver host:localhost port:3001 --collect
cd ..
echo.
echo Collection completed!
echo File saved: dataset.csv