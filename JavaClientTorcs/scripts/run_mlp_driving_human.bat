@echo off
REM Script per eseguire la guida autonoma MLP con dataset umano
REM Questo script avvia TORCS e il driver MLP con distanza euclidea

echo ========================================
echo    MLP Driving - Dataset Umano
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0.."

echo [INFO] Assicurati che:
echo 1. Il progetto sia gia compilato
echo 2. TORCS sia gia avviato e configurato
echo 3. La gara sia pronta per iniziare
echo.
echo Premi un tasto per avviare il driver MLP...
pause >nul
echo.
echo [INFO] Avvio del driver MLP con dataset umano...
echo [INFO] Configurazione: Distanza Euclidea, Dataset Umano
echo [INFO] Il driver si connettera automaticamente a TORCS
echo [INFO] Premi Ctrl+C per interrompere
echo.

REM Esegui il driver MLP con dataset umano
java -cp "dist\JavaClientTorcs.jar;lib\*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.MLPDriver verbose:on

echo.
echo ========================================
echo      Sessione KNN Terminata
echo ========================================
echo.
