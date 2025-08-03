# Driver Behavioral Cloning per TORCS

[![Language: English](https://img.shields.io/badge/lang-en-green.svg)](README.md) [![Language: Italian](https://img.shields.io/badge/lang-it-blue.svg)](README.it.md)

Un sistema di behavioral cloning per TORCS (The Open Racing Car Simulator) che utilizza K-NN per imitare il comportamento di guida umano basandosi su dati di guida raccolti.

## 🎯 Cos'è

Questo progetto implementa un driver autonomo per TORCS che:
- **Impara dal comportamento umano** tramite raccolta dati
- **Utilizza K-NN** per prendere decisioni di guida in tempo reale
- **Supporta raccolta dati** sia manuale che automatica
- **È cross-platform** (Windows, Linux, macOS)

## 🚀 Installazione

### Prerequisiti
- **Java 21+** installato e nel PATH ([Download JDK](https://www.oracle.com/it/java/technologies/downloads))
- **Apache Ant** per build del progetto ([Download Apache Ant](https://ant.apache.org/bindownload.cgi)) (o **NetBeans 26+** per build IDE - [Download NetBeans](https://netbeans.apache.org/front/main/download/nb26))
- TORCS avviato con modulo JavaClientTorcs

### Build

#### Metodo 1: NetBeans 26 (Raccomandato)
**Ambiente di sviluppo:** NetBeans 26 + JDK 21+

**Procedura semplificata:**
1. **Apri il progetto in NetBeans 26**
   - File → Open Project
   - Seleziona la cartella `JavaClientTorcs`
   - NetBeans riconoscerà automaticamente il progetto Ant

2. **Build con un click**
   - **Pulsante verde "Run Project"** (▶️) nella toolbar
   - **Oppure:** Right-click sul progetto → Clean and Build

3. **Verifica build**
   - Il JAR verrà generato in: `JavaClientTorcs/dist/JavaClientTorcs.jar`
   - **Nessuna configurazione aggiuntiva richiesta**

#### Metodo 2: Ant da terminale

#### Windows
```cmd
cd JavaClientTorcs
ant clean
ant jar
```

#### Linux/Mac
```bash
cd JavaClientTorcs
ant clean
ant jar
```

## 📁 Struttura del Progetto

```
Progetto/
├── README.md               # Questa documentazione
├── torcs_menu.bat          # Menu Windows
├── torcs_menu.sh           # Menu Linux/Mac
└── JavaClientTorcs/
    ├── build.xml           # Configurazione Ant
    ├── manifest.mf         # Manifest JAR
    ├── nbproject/          # Configurazione NetBeans Ant
    │   ├── build-impl.xml
    │   ├── project.properties
    │   └── project.xml
    ├── src/
    │   └── it/unisa/javaclienttorcs/
    │       ├── Action.java                 # Azioni di controllo
    │       ├── BehavioralCloningDriver.java # Driver K-NN principale
    │       ├── Client.java                 # Client principale
    │       ├── Controller.java             # Interfaccia controller
    │       ├── DataCollector.java          # Raccolta dati
    │       ├── DeadSimpleSoloController.java # Controller base
    │       ├── HumanController.java        # Controller manuale
    │       ├── MessageBasedSensorModel.java # Modello sensori
    │       ├── MessageParser.java          # Parser messaggi
    │       ├── SensorModel.java            # Interfaccia sensori
    │       ├── SimpleDriver.java           # Driver automatico
    │       └── SocketHandler.java        # Gestione socket
    ├── lib/
    │   ├── Jamepad.jar
    │   └── sdl2gdx-1.0.5.jar
    ├── build/              # Output build temporaneo
    ├── dist/
    │   ├── JavaClientTorcs.jar
    │   └── lib/            # Librerie copiate
    └── scripts/
        ├── run_manual_driving.bat    # Guida manuale Windows
        ├── run_manual_driving.sh     # Guida manuale Linux/Mac
        ├── run_auto_collection.bat   # Raccolta automatica Windows
        ├── run_auto_collection.sh    # Raccolta automatica Linux/Mac
        ├── test_human_model.bat      # Test dati umani Windows
        ├── test_human_model.sh       # Test dati umani Linux/Mac
        ├── test_auto_model.bat       # Test dati automatici Windows
        ├── test_auto_model.sh        # Test dati automatici Linux/Mac
        ├── combine_datasets.bat      # Combina dataset Windows
        └── combine_datasets.sh       # Combina dataset Linux/Mac
```

## 🎮 Come Usare

### Metodo 1: Menu Interattivo (Raccomandato)

#### Windows
```cmd
torcs_menu.bat
```

#### Linux/Mac
```bash
./torcs_menu.sh
```

### Metodo 2: Script Individuali

#### Raccolta Dati Manuali
- **Windows**: `JavaClientTorcs/scripts/run_manual_driving.bat`
- **Linux/Mac**: `./JavaClientTorcs/scripts/run_manual_driving.sh`

#### Raccolta Dati Automatica
- **Windows**: `JavaClientTorcs/scripts/run_auto_collection.bat`
- **Linux/Mac**: `./JavaClientTorcs/scripts/run_auto_collection.sh`

#### Test Modello
- **Test dati umani**: 
  - Windows: `JavaClientTorcs/scripts/test_human_model.bat`
  - Linux/Mac: `./JavaClientTorcs/scripts/test_human_model.sh`
- **Test dati automatici**:
  - Windows: `JavaClientTorcs/scripts/test_auto_model.bat`
  - Linux/Mac: `./JavaClientTorcs/scripts/test_auto_model.sh`

#### Gestione Dataset
- **Combinare dataset**:
  - Windows: `JavaClientTorcs/scripts/combine_datasets.bat`
  - Linux/Mac: `./JavaClientTorcs/scripts/combine_datasets.sh`

## 🕹️ Controlli di Guida

Durante la guida manuale, usa:
- **Freccette/WASD/IJKL/8426** - Controlli direzione
- **C** - Toggle raccolta dati ON/OFF
- **P** - Mostra statistiche
- **R** - Reset posizione
- **Q** - Esci

## 📊 Dataset

I dataset vengono creati automaticamente nella directory principale:
- `dataset.csv` - Dati raccolti automaticamente
- `human_dataset.csv` - Dati raccolti manualmente
- `combined_dataset.csv` - Dataset combinati

## 🔧 Tecnologia

- **Linguaggio**: Java
- **IDE**: NetBeans 26 (progetto nativo)
- **Build Tool**: Apache Ant
- **Algoritmo**: K-Nearest Neighbors (K-NN)
- **Comunicazione**: Socket UDP con TORCS
- **Formato Dati**: CSV

### Compatibilità NetBeans
- **Testato con**: NetBeans 26 + JDK 21/24 ✅
- **Compatibile con**: NetBeans 25+ e JDK 21+
- **Progetto nativo**: Apri direttamente la cartella `JavaClientTorcs` come progetto Ant

## 🚗 TORCS Setup

1. Avvia TORCS
2. Configura la gara con il client Java
3. Il driver si connetterà automaticamente alla porta 3001

## ⚡ Esecuzione Veloce

1. **Build**: `ant clean && ant jar` (da JavaClientTorcs/)
2. **Menu**: Usa il menu appropriato per il tuo sistema
3. **Raccogli dati**: Guida manualmente o automaticamente
4. **Testa**: Usa il modello con i tuoi dati

## 📋 Note Importanti

- Assicurati che TORCS sia in esecuzione prima di avviare i driver
- I dataset vengono salvati nella directory dove esegui gli script
- Per migliori risultati, raccogli almeno 1000-5000 esempi per pista
- Il sistema è cross-platform: funziona su Windows, Linux e macOS

## 🪟 Configurazione TORCS per Windows

### ⚠️ Versione Richiesta: 1.3.7
**Nota Importante**: Il progetto MIVIA 2025 richiede **specificamente la versione 1.3.7**, non la 1.3.8 più recente.

### Installazione Completa TORCS + SCR Patch

Per configurare correttamente TORCS su Windows per il progetto MIVIA 2025:

#### Passo 1: Installazione TORCS 1.3.7
1. **Scarica l'esatta versione 1.3.7**:
   - [Download torcs_1.3.7_setup.exe](https://sourceforge.net/projects/torcs/files/torcs-win32-bin/1.3.7/torcs_1.3.7_setup.exe/download)
   - Salva il file nella cartella Downloads
   - **⚠️ Garanzia Versione**: Questo link fornisce **esattamente la versione 1.3.7** richiesta dal progetto

2. **Installa TORCS**:
   - Esegui `torcs_1.3.7_setup.exe` come Amministratore
   - Segui la procedura guidata di installazione
   - Installa nella directory predefinita: `C:\Program Files\TORCS`
   - **Verifica versione**: Vai in `Help > About TORCS` per confermare 1.3.7

#### Passo 2: Applicazione Patch SCR
1. **Scarica il pacchetto SCR**:
   - Visita: [Computational Intelligence in Games](http://sourceforge.net/projects/cig/)
   - Scarica: `scr-win-patch.zip`

2. **Applica la patch**:
   - Vai nella directory di installazione TORCS (es. `C:\Program Files\TORCS`)
   - Estrai `scr-win-patch.zip` direttamente in questa directory
   - **IMPORTANTE**: Quando richiesto, seleziona **"Sì a tutti"** per sovrascrivere i file esistenti
   - Verifica che tutti i file siano stati estratti correttamente

#### Passo 3: Verifica Installazione
1. **Avvia TORCS**:
   - Esegui `C:\Program Files\TORCS\torcs.exe`
   - Verifica che appaiano le funzionalità SCR aggiuntive

2. **Test connessione**:
   - Avvia TORCS prima di eseguire i driver Java
   - Il client Java si connetterà automaticamente su porta 3001

#### Risoluzione Problemi Comuni
- **TORCS non parte**: Assicurati di avere la versione esatta 1.3.7
- **Patch non applicata**: Re-scarica `scr-win-patch.zip` ed estrai di nuovo
- **Permessi**: Esegui sempre come Amministratore su Windows

#### ⚠️ Problema: Versione Errata Installata (1.3.8 invece di 1.3.7)
- **Windows**: Usa il link specifico fornito sopra per scaricare 1.3.7
- **Linux**: Se il package manager installa 1.3.8, usa:
  ```bash
  # Verifica versione disponibile
  apt-cache show torcs | grep Version
  # Se solo 1.3.8 disponibile, usa la compilazione da sorgente
  ```

#### Problema: TORCS non si avvia
- **Soluzione**: Controlla le dipendenze OpenGL
- **Windows**: Aggiorna i driver della scheda video
- **Linux**: Installa `libgl1-mesa-glx`

#### Problema: SCR Patch non funziona
- **Soluzione**: Verifica che il patch sia stato applicato nella directory corretta
- **Windows**: Controlla che i file siano in `C:\Program Files\TORCS`
- **Linux**: Verifica il percorso di installazione con `which torcs`

### Prossimi Passi
Dopo la configurazione:
1. Testa l'installazione avviando TORCS manualmente
2. Usa `torcs_menu.bat` per accedere alle funzionalità del progetto
3. Procedi con la raccolta dati o test del modello

## 🐧 Configurazione TORCS per Linux

### ⚠️ Versione Richiesta: 1.3.7
**Nota Importante**: Il progetto MIVIA 2025 richiede **specificamente la versione 1.3.7**, non la 1.3.8 più recente.

### Installazione TORCS 1.3.7 + SCR Patch

Per configurare TORCS su Linux, usa **una delle seguenti opzioni per ottenere esattamente la 1.3.7**:

#### Opzione 1: Installazione via Package Manager (Raccomandato - Versione 1.3.7)

**Ubuntu/Debian (versione 1.3.7 confermata):**
```bash
# Installa TORCS 1.3.7 dai repository (versione attuale: 1.3.7+dfsg-5)
sudo apt update
sudo apt install torcs=1.3.7+dfsg-5

# Verifica la versione installata
torcs --version

# Installa dipendenze aggiuntive per SCR
sudo apt install libalut-dev libvorbis-dev libpng-dev
```

**Debian:**
```bash
# Debian stable/bookworm include 1.3.7
sudo apt update
sudo apt install torcs
```

**Fedora:**
```bash
# Fedora include 1.3.7 nei repository
sudo dnf install torcs-1.3.7
```

**⚠️ Attenzione**: Alcune distribuzioni potrebbero avere aggiornato alla 1.3.8. In tal caso, usa l'opzione 2 o 3.

#### Opzione 2: Installazione da Sorgente con SCR Patch

**Ubuntu/Debian completo:**
```bash
# Installa tutte le dipendenze necessarie
sudo apt-get install libglib2.0-dev libgl1-mesa-dev libglu1-mesa-dev freeglut3-dev libplib-dev libopenal-dev libalut-dev libxi-dev libxmu-dev libxrender-dev libxrandr-dev libpng-dev libvorbis-dev cmake build-essential git

# Scarica e compila TORCS 1.3.7 con SCR patch
git clone https://github.com/fmirus/torcs-1.3.7.git
cd torcs-1.3.7

export CFLAGS="-fPIC"
export CPPFLAGS=$CFLAGS
export CXXFLAGS=$CFLAGS

./configure --prefix=$(pwd)/BUILD
make -j$(nproc)
make install
make datainstall
```

**CentOS/RHEL:**
```bash
# Installa dipendenze
sudo yum install gcc gcc-c++ mesa-libGL-devel mesa-libGLU-devel freeglut-devel plib-devel openal-soft-devel libvorbis-devel libpng-devel cmake git

# Segui gli stessi passi di compilazione Ubuntu
```

#### Opzione 3: Repository GitHub Pre-Patchato (Raccomandato per Linux)

**Repository GitHub con TORCS 1.3.7 + SCR già patchato:**
```bash
# Clona il repository con TORCS 1.3.7 pre-patchato
# Include dipendenze e patch SCR già applicate
git clone https://github.com/fmirus/torcs-1.3.7.git
cd torcs-1.3.7

# Installa dipendenze
sudo apt-get install libglib2.0-dev libgl1-mesa-dev libglu1-mesa-dev freeglut3-dev libplib-dev libopenal-dev libalut-dev libxi-dev libxmu-dev libxrender-dev libxrandr-dev libpng-dev libvorbis-dev cmake build-essential

# Compila e installa
export CFLAGS="-fPIC"
export CPPFLAGS=$CFLAGS
export CXXFLAGS=$CFLAGS
./configure --prefix=$(pwd)/BUILD
make -j$(nproc)
make install
make datainstall
```

#### Opzione 4: Installazione Flatpak (Universale)
```bash
# Installa Flatpak se non presente
sudo apt install flatpak  # Ubuntu/Debian
sudo dnf install flatpak  # Fedora

# Installa TORCS da Flathub
flatpak install flathub net.sourceforge.torcs
```

### Applicazione Patch SCR su Linux

Dopo l'installazione di TORCS, applica la patch SCR:

1. **Scarica il pacchetto SCR:**
   ```bash
   wget http://sourceforge.net/projects/cig/files/scr-linux-patch.tar.gz
   ```

2. **Trova la directory di installazione di TORCS:**
   - Package manager: `/usr/games/torcs` o `/opt/torcs`
   - Sorgente: `~/torcs-1.3.7/BUILD/bin/torcs`

3. **Applica la patch:**
   ```bash
   # Estrai la patch nella directory TORCS
   sudo tar -xzvf scr-linux-patch.tar.gz -C /usr/share/games/torcs/
   
   # Dai i permessi necessari
   sudo chmod +x /usr/games/torcs
   ```

### Verifica Installazione Linux

1. **Test avvio TORCS:**
   ```bash
   # Se installato da package manager
   torcs
   
   # Se installato da sorgente
   ~/torcs-1.3.7/BUILD/bin/torcs
   
   # Se installato da Flatpak
   flatpak run net.sourceforge.torcs
   ```

2. **Verifica patch SCR:**
   - In TORCS, vai su "Race" → "Practice"
   - Verifica che siano presenti opzioni SCR aggiuntive

### Risoluzione Problemi Linux

#### Problemi comuni:
- **Librerie mancanti**: `sudo apt install --fix-missing`
- **Permessi**: `sudo chmod +x /usr/games/torcs`
- **OpenGL**: Verifica con `glxinfo | grep "direct rendering"`
- **Audio**: Installa `libopenal-dev` se necessario

#### Dipendenze specifiche per distribuzioni:
- **Ubuntu 20.04+**: Tutte le dipendenze disponibili nei repository
- **Ubuntu 18.04**: Potrebbe richiedere repository aggiuntivi
- **Debian**: Usa `apt-get` con i repository non-free
- **Fedora**: Usa `dnf` con i repository RPM Fusion

### Prossimi Passi Linux
Dopo la configurazione:
1. Testa l'installazione con `torcs_menu.sh`
2. Verifica la connessione client-server su porta 3001
3. Procedi con la raccolta dati usando gli script `.sh`
