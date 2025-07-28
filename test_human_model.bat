@echo off
echo =================================
echo   TEST MODELLO - Dati Umani
echo =================================
echo Test del behavioral cloning con dati raccolti manualmente
echo Dataset: human_dataset.csv
echo =================================
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client BehavioralCloningDriver human_dataset.csv
echo.
echo Test completato!
pause