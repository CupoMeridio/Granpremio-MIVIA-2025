@echo off
echo =================================
echo   GUIDA MANUALE - Behavioral Cloning
echo =================================
echo Comandi disponibili:
echo - ↑/↓/←/→ : Frecce direzionali
echo - W/A/S/D : WASD gaming
echo - I/J/K/L : Alternative simmetriche
echo - 8/2/4/6 : Tastierino numerico
echo - C : Toggle raccolta dati
echo - P : Statistiche
echo - R : Reset
echo - Q : Esci
echo =================================
cd JavaClientTorcs
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client HumanController --collect
pause