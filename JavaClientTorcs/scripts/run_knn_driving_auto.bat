@echo off
REM Script per eseguire la guida autonoma KNN con dataset automatico
REM Questo script avvia TORCS e il driver KNN con distanza euclidea

echo ========================================
echo   KNN Driving - Dataset Automatico
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0.."

REM Verifica che esista il dataset automatico
if not exist "auto_dataset.csv" (
    echo [ERROR] Dataset automatico non trovato!
    echo Esegui prima la raccolta dati automatica con:
    echo   - scripts\run_auto_collection.bat
    echo.
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
echo [INFO] Avvio del driver KNN con dataset automatico...
echo [INFO] Configurazione: Distanza Euclidea, Dataset Automatico
echo [INFO] Il driver si connettera automaticamente a TORCS
echo [INFO] Premi Ctrl+C per interrompere
echo.

REM Esegui il driver KNN con dataset automatico
java -cp "dist\JavaClientTorcs.jar;lib\*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNDriver auto_dataset.csv

echo.
echo ========================================
echo      Sessione KNN Terminata
echo ========================================
echo.
