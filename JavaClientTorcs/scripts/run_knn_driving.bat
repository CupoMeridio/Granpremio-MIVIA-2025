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

REM Mostra opzioni di configurazione
echo Seleziona la configurazione KNN:
echo.
echo 1. Configurazione umana ottimizzata (dataset umano)
echo 2. Configurazione personalizzata Euclidea
echo 3. Configurazione personalizzata Minkowski
echo 4. Test comparativo (solo test, non guida)
echo.
set /p choice="Inserisci la tua scelta (1-4): "

REM Compila il progetto
echo.
echo [INFO] Compilazione del progetto...
javac -cp "lib\*;." -d . src\it\unisa\javaclienttorcs\*.java
if errorlevel 1 (
    echo [ERROR] Errore durante la compilazione!
    pause
    exit /b 1
)

echo [SUCCESS] Compilazione completata!
echo.

if "%choice%"=="4" (
    echo [INFO] Esecuzione test comparativo...
    java -cp "lib\*;." it.unisa.javaclienttorcs.KNNTestDriver
    echo.
    echo Test completato!
    pause
    exit /b 0
)

REM Avvia TORCS in background
echo [INFO] Avvio di TORCS...
echo Assicurati di:
echo 1. Selezionare 'Practice' -^> 'Quick Race'
echo 2. Configurare la gara (pista, auto, ecc.)
echo 3. Premere 'New Race' per iniziare
echo 4. Tornare a questa finestra e premere un tasto quando la gara e pronta
echo.

REM Avvia TORCS (modifica il percorso se necessario)
start "" "C:\Program Files (x86)\torcs\wtorcs.exe"

echo Premi un tasto quando TORCS e pronto e la gara e configurata...
pause >nul

echo.
echo [INFO] Avvio del driver KNN...
echo [INFO] Il driver si connettera automaticamente a TORCS
echo [INFO] Premi Ctrl+C per interrompere
echo.

REM Esegui il driver KNN con la configurazione selezionata
if "%choice%"=="1" (
    echo [INFO] Usando configurazione umana ottimizzata
    java -cp "lib\*;." it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNDriver human
) else if "%choice%"=="2" (
    echo [INFO] Usando configurazione Euclidea personalizzata
    java -cp "lib\*;." it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNDriver euclidean
) else if "%choice%"=="3" (
    echo [INFO] Usando configurazione Minkowski personalizzata
    java -cp "lib\*;." it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNDriver minkowski
) else (
    echo [WARNING] Scelta non valida, usando configurazione di default
    java -cp "lib\*;." it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNDriver
)

echo.
echo ========================================
echo      Sessione KNN Terminata
echo ========================================
echo.
echo Premi un tasto per tornare al menu...
pause >nul