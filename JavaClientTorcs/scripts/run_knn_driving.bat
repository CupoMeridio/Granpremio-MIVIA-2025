@echo off
REM Script per eseguire la guida autonoma con KNN
REM Questo script avvia TORCS e il driver KNN

echo ========================================
echo        Guida Autonoma con KNN
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0.."

REM Verifica che esista il dataset umano
if not exist "human_dataset.csv" (
    echo [ERROR] Dataset umano non trovato!
    echo Esegui prima la raccolta dati con:
    echo   - scripts\run_manual_driving.bat
    echo.
    pause
    exit /b 1
)


echo.
echo [INFO] Avvio del driver KNN...
echo [INFO] Il driver si connettera automaticamente a TORCS
echo [INFO] Premi Ctrl+C per interrompere
echo [INFO] Dataset: human_dataset.csv
echo.

java -cp "dist\JavaClientTorcs.jar;lib\*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNDriver human_dataset.csv

echo.
echo ========================================
echo      Sessione KNN Terminata
echo ========================================
echo.
echo Premi un tasto per tornare al menu...
pause >nul