@echo off
REM Script per eseguire il classificatore KNN
REM Questo script discretizza il dataset e avvia il classificatore KNN

echo ========================================
echo      Classificatore KNN Discreto
echo ========================================
echo.
echo PREREQUISITI:
echo 1. Il progetto deve essere compilato con NetBeans
echo 2. Il dataset umano deve essere presente
echo.

REM Vai alla directory del progetto
cd /d "%~dp0.."

REM Verifica che esista il dataset umano
if not exist "human_dataset.csv" (
    echo [ERROR] Dataset umano non trovato!
    echo Esegui prima la raccolta dati con:
    echo   - scripts\run_manual_driving.bat
    echo.
    exit /b 1
)

REM Chiedi se discretizzare il dataset
echo Il classificatore KNN richiede un dataset discretizzato.
echo.
set /p discretize="Vuoi discretizzare il dataset ora? (s/n): "

if /i "%discretize%"=="s" (
    echo.
    echo [INFO] Discretizzazione del dataset in corso...
    
    REM Verifica se il progetto e gia compilato
    if exist "dist\JavaClientTorcs.jar" (
        echo [INFO] Usando JAR compilato da NetBeans...
        java -cp "dist\JavaClientTorcs.jar;lib\*" it.unisa.javaclienttorcs.ActionDiscretizer human_dataset.csv human_dataset_discrete.csv
    ) else (
        echo [WARNING] JAR non trovato. Tentativo di compilazione...
        echo [INFO] NOTA: Assicurati che il progetto sia compilato con NetBeans prima di eseguire questo script.
        javac -cp "lib\*;." -d . src\it\unisa\javaclienttorcs\*.java
        if errorlevel 1 (
            echo [ERROR] Errore durante la compilazione!
            echo [ERROR] Compila il progetto con NetBeans e riprova.
            exit /b 1
        )
        java -cp "lib\*;." it.unisa.javaclienttorcs.ActionDiscretizer human_dataset.csv human_dataset_discrete.csv
    )
    
    if errorlevel 1 (
        echo [ERROR] Errore durante la discretizzazione!
        exit /b 1
    )
    
    echo [SUCCESS] Dataset discretizzato salvato come human_dataset_discrete.csv
    echo.
)

REM Verifica che esista il dataset discretizzato
if not exist "human_dataset_discrete.csv" (
    echo [ERROR] Dataset discretizzato non trovato!
    echo Esegui prima la discretizzazione del dataset.
    echo.
    exit /b 1
)

echo [INFO] Assicurati che:
echo 1. Il progetto sia gia compilato (o usa NetBeans per compilare)
echo 2. TORCS sia gia avviato e configurato
echo 3. La gara sia pronta per iniziare
echo.
echo Premi un tasto per avviare il classificatore KNN...
pause >nul
echo.
echo [INFO] Avvio del classificatore KNN...
echo [INFO] Il driver si connettera automaticamente a TORCS
echo [INFO] Premi Ctrl+C per interrompere
echo.

REM Esegui il classificatore KNN usando il JAR compilato da NetBeans
if exist "dist\JavaClientTorcs.jar" (
    echo [INFO] Usando JAR compilato da NetBeans...
    java -cp "dist\JavaClientTorcs.jar;lib\*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNClassifierDriver human_dataset_discrete.csv
) else (
    echo [INFO] Usando classi compilate...
    java -cp "lib\*;." it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.KNNClassifierDriver human_dataset_discrete.csv
)

echo.
echo ========================================
echo    Sessione Classificatore Terminata
echo ========================================
echo.
