@echo off
REM Script per eseguire la guida autonoma KNN con dataset umano
REM Questo script avvia TORCS e il driver KNN con distanza euclidea

echo ========================================
echo    KNN Driving - Dataset Umano
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0.."

REM Verifica che esista il dataset umano
if not exist "human_dataset.csv" (
    echo [ERROR] Dataset umano non trovato!
    echo Esegui prima la raccolta dati manuale con:
    echo   - scripts\run_manual_driving.bat
    echo.
    pause
    exit /b 1
)

echo [INFO] Assicurati che:
echo 1. Il progetto sia gia compilato
echo 2. TORCS sia gia avviato e configurato
echo 3. La gara sia pronta per iniziare
echo.
echo Premi un tasto per avviare il driver KNN...
pause >nul
echo.
echo [INFO] Avvio del driver KNN con dataset umano...
echo [INFO] Configurazione: Distanza Euclidea, Dataset Umano
echo [INFO] Il driver si connettera automaticamente a TORCS
echo [INFO] Premi Ctrl+C per interrompere
echo.

REM Esegui il driver KNN con dataset umano
java -cp "dist\JavaClientTorcs.jar;lib\*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNDriver human_dataset.csv

echo.
echo ========================================
echo      Sessione KNN Terminata
echo ========================================
echo.
