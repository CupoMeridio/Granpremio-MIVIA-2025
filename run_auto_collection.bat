@echo off
echo =================================
echo   RACCOLTA DATI AUTOMATICA
echo =================================
echo Raccolta dati con SimpleDriver...
echo I dati verranno salvati in: dataset.csv
echo =================================
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client SimpleDriver --collect
echo.
echo Raccolta completata!
echo File salvato: dataset.csv
pause