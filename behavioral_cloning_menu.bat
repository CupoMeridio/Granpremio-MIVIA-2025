@echo off
:menu
title Behavioral Cloning - Menu Principale
color 0A
cls
echo =========================================
echo   BEHAVIORAL CLONING - TORCS
echo =========================================
echo.
echo 1. Guida Manuale (Raccolta Dati)
echo 2. Raccolta Dati Automatica
echo 3. Test Modello - Dati Umani
echo 4. Test Modello - Dati Automatici
echo 5. Combinare Dataset
echo 6. Visualizza Statistiche Dataset
echo 7. Apri Guida Completa
echo 8. Esci
echo.
echo =========================================
set /p choice="Seleziona un'opzione (1-8): "

if "%choice%"=="1" goto manual
goto menu

if "%choice%"=="1" goto manual
if "%choice%"=="2" goto auto
if "%choice%"=="3" goto test_human
if "%choice%"=="4" goto test_auto
if "%choice%"=="5" goto combine
if "%choice%"=="6" goto stats
if "%choice%"=="7" goto guide
if "%choice%"=="8" goto exit

echo Scelta non valida! Premi un tasto per continuare...
pause >nul
goto menu

:manual
call run_manual_driving.bat
goto menu

:auto
call run_auto_collection.bat
goto menu

:test_human
call test_human_model.bat
goto menu

:test_auto
call test_auto_model.bat
goto menu

:combine
call combine_datasets.bat
goto menu

:stats
cd JavaClientTorcs
echo.
echo === STATISTICHE DATASET ===
if exist "dataset.csv" (
    echo dataset.csv: 
    find /c /v "" dataset.csv
) else (
    echo dataset.csv: Non trovato
)
if exist "human_dataset.csv" (
    echo human_dataset.csv: 
    find /c /v "" human_dataset.csv
) else (
    echo human_dataset.csv: Non trovato
)
if exist "combined_dataset.csv" (
    echo combined_dataset.csv: 
    find /c /v "" combined_dataset.csv
) else (
    echo combined_dataset.csv: Non trovato
)
pause
goto menu

:guide
start notepad JavaClientTorcs\BEHAVIORAL_CLONING_GUIDE.md
goto menu

:exit
echo Arrivederci!
timeout /t 2 >nul