@echo off
echo =================================
echo   TEST MODELLO - Dati Automatici
echo =================================
echo Test del behavioral cloning con dati raccolti automaticamente
echo Dataset: dataset.csv
echo =================================
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client BehavioralCloningDriver dataset.csv
echo.
echo Test completato!
pause