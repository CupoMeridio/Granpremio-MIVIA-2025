@echo off
REM Script per testare il sistema KNN
REM Questo script compila ed esegue i test del KNN

echo ========================================
echo           Test Sistema KNN
echo ========================================
echo.

REM Vai alla directory del progetto
cd /d "%~dp0.."

REM Verifica che esistano i dataset
if not exist "auto_dataset.csv" (
    if not exist "human_dataset.csv" (
        echo [ERROR] Nessun dataset trovato!
        echo Esegui prima la raccolta dati con:
        echo   - scripts\run_auto_collection.bat
        echo   - scripts\run_manual_driving.bat
        echo.
        pause
        exit /b 1
    )
)

REM Compila il progetto
echo [INFO] Compilazione del progetto...
javac -cp "lib\*;." -d . src\it\unisa\javaclienttorcs\*.java
if errorlevel 1 (
    echo [ERROR] Errore durante la compilazione!
    pause
    exit /b 1
)

echo [SUCCESS] Compilazione completata!
echo.

REM Esegui i test KNN
echo [INFO] Esecuzione test KNN...
echo.
java -cp "lib\*;." it.unisa.javaclienttorcs.KNNTestDriver

echo.
echo ========================================
echo         Test KNN Completati
echo ========================================
echo.
echo Premi un tasto per tornare al menu...
pause >nul