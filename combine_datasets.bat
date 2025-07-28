@echo off
echo =================================
echo   COMBINA DATASET
echo =================================
echo Combinazione di dataset automatico e manuale...
echo Input: dataset.csv + human_dataset.csv
echo Output: combined_dataset.csv
echo =================================
cd JavaClientTorcs

:: Verifica se i file esistono
if not exist "dataset.csv" (
    echo ERRORE: dataset.csv non trovato!
    echo Esegui prima run_auto_collection.bat
    pause
    exit /b
)

if not exist "human_dataset.csv" (
    echo ERRORE: human_dataset.csv non trovato!
    echo Esegui prima run_manual_driving.bat
    pause
    exit /b
)

:: Combinazione dei file
echo Combinazione in corso...
copy /b dataset.csv + human_dataset.csv combined_dataset.csv >nul

echo.
echo Dataset combinati con successo!
echo File creato: combined_dataset.csv
echo Righe totali:
find /c /v "" combined_dataset.csv
pause