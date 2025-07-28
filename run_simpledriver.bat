@echo off
echo =================================
echo   SIMPLE DRIVER - Esecuzione
echo =================================
echo Avvio di SimpleDriver per guida automatica base
echo (senza raccolta dati)
echo =================================
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client SimpleDriver
echo.
echo Esecuzione completata!
pause