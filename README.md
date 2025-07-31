# TORCS Behavioral Cloning Driver

Un sistema di behavioral cloning per TORCS (The Open Racing Car Simulator) che utilizza K-NN per imitare il comportamento di guida umano basandosi su dati raccolti durante sessioni di guida.

## 🎯 Cos'è

Questo progetto implementa un driver autonomo per TORCS che:
- **Impara dal comportamento umano** tramite raccolta dati
- **Utilizza K-NN** per prendere decisioni di guida in tempo reale
- **Supporta raccolta dati** sia manuale che automatica
- **È cross-platform** (Windows, Linux, macOS)

## 🚀 Installazione

### Prerequisiti
- Java 21+ installato e nel PATH
- Apache Ant per build del progetto (o NetBeans 26+ per build IDE)
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
├── torcs_menu.bat          # Menu Windows
├── torcs_menu.sh           # Menu Linux/Mac
├── JavaClientTorcs/
│   ├── build.xml           # Configurazione Ant
│   ├── manifest.mf         # Manifest JAR
│   ├── nbproject/          # Configurazione NetBeans Ant
│   │   ├── build-impl.xml
│   │   ├── project.properties
│   │   └── project.xml
│   ├── src/
│   │   └── it/unisa/javaclienttorcs/
│   │       ├── Action.java                 # Azioni di controllo
│   │       ├── BehavioralCloningDriver.java # Driver K-NN principale
│   │       ├── Client.java                 # Client principale
│   │       ├── Controller.java             # Interfaccia controller
│   │       ├── DataCollector.java          # Raccolta dati
│   │       ├── DeadSimpleSoloController.java # Controller base
│   │       ├── HumanController.java        # Controller manuale
│   │       ├── MessageBasedSensorModel.java # Modello sensori
│   │       ├── MessageParser.java          # Parser messaggi
│   │       ├── SensorModel.java            # Interfaccia sensori
│   │       ├── SimpleDriver.java           # Driver automatico
│   │       └── SocketHandler.java        # Gestione socket
│   ├── lib/
│   │   ├── Jamepad.jar
│   │   └── sdl2gdx-1.0.5.jar
│   ├── build/              # Output build temporaneo
│   ├── dist/
│   │   ├── JavaClientTorcs.jar
│   │   └── lib/            # Librerie copiate
│   ├── scripts/
│   │   ├── run_manual_driving.bat    # Guida manuale Windows
│   │   ├── run_manual_driving.sh     # Guida manuale Linux/Mac
│   │   ├── run_auto_collection.bat   # Raccolta automatica Windows
│   │   ├── run_auto_collection.sh    # Raccolta automatica Linux/Mac
│   │   ├── test_human_model.bat      # Test dati umani Windows
│   │   ├── test_human_model.sh       # Test dati umani Linux/Mac
│   │   ├── test_auto_model.bat       # Test dati automatici Windows
│   │   ├── test_auto_model.sh        # Test dati automatici Linux/Mac
│   │   ├── combine_datasets.bat      # Combina dataset Windows
│   │   └── combine_datasets.sh       # Combina dataset Linux/Mac
└── README.md                # Questa documentazione
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
