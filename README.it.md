# Sistema di Raccolta Dati per TORCS

[![Language: English](https://img.shields.io/badge/lang-en-green.svg)](README.md) [![Language: Italian](https://img.shields.io/badge/lang-it-blue.svg)](README.it.md)

Un sistema di raccolta dati per TORCS (The Open Racing Car Simulator) che cattura il comportamento di guida tramite file CSV per analisi e machine learning.

## 🎯 Cos'è

Questo progetto implementa un sistema di raccolta dati per TORCS che:
- **Cattura il comportamento di guida umano** tramite dati sensoriali completi
- **Genera dataset CSV** per applicazioni di machine learning
- **Supporta raccolta dati** sia manuale che automatica
- **Compatibile con Windows**

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



## 📁 Struttura del Progetto

```
Progetto/
├── README.md               # Questa documentazione
├── torcs_menu.bat          # Menu Windows

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
    │       ├── DataCollector.java          # Raccolta dati
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
        ├── run_manual_driving.bat    # Guida manuale
        ├── run_auto_collection.bat   # Raccolta automatica
        ├── run_knn_driving_human.bat # KNN con dati umani
        ├── run_knn_driving_auto.bat  # KNN con dati automatici
        ├── run_simpledriver.bat      # Driver semplice
        ├── combine_datasets.bat      # Combina dataset
        └── test_knn.bat              # Test KNN
        ├── combine_datasets.bat      # Combina dataset Windows
        └── combine_datasets.sh       # Combina dataset Linux/Mac
```

## 🎮 Come Usare

### Metodo 1: Menu Interattivo (Raccomandato)

Il progetto include un sistema di menu completo con tutte le funzionalità:

#### Windows
```cmd
torcs_menu.bat
```



### Opzioni del Menu

**Raccolta Dati:**
- Opzione 1: Guida manuale (raccolta dati umani)
- Opzione 2: Raccolta automatica (SimpleDriver)

**Gestione Dataset:**
- Opzione 3: Combina dataset (auto + umano)
- Opzione 4: Visualizza statistiche dataset
- Opzione 5: Converti dataset per modelli ML

**Intelligenza Artificiale:**
- Opzione 6: **Test sistema KNN**
- Opzione 7: **Guida autonoma con KNN**
- Opzione 8: **Confronto configurazioni KNN**

**Guida Autonoma Classica:**
- Opzione 9: SimpleDriver (guida automatica base)

**Utilità:**
- Opzione 10: Pulisci file temporanei
- Opzione 11: Informazioni sul progetto

### Metodo 2: Script Individuali

#### Raccolta Dati Manuali
- `JavaClientTorcs/scripts/run_manual_driving.bat`

#### Raccolta Dati Automatica
- `JavaClientTorcs/scripts/run_auto_collection.bat`

#### Guida KNN
- **Dataset umano**: `JavaClientTorcs/scripts/run_knn_driving_human.bat`
- **Dataset automatico**: `JavaClientTorcs/scripts/run_knn_driving_auto.bat`

#### Driver Semplice
- `JavaClientTorcs/scripts/run_simpledriver.bat`

#### Gestione Dataset
- **Combinare dataset**: `JavaClientTorcs/scripts/combine_datasets.bat`
- **Test KNN**: `JavaClientTorcs/scripts/test_knn.bat`

## 🕹️ Controlli di Guida & Supporto Controller

### Controlli Tastiera
Durante la guida manuale, usa:
- **Freccette/WASD/IJKL/8426** - Controlli direzione
- **C** - Toggle raccolta dati ON/OFF
- **P** - Mostra statistiche
- **R** - Reset posizione
- **Q** - Esci

### Supporto Controller Gamepad
Il sistema supporta **controller compatibili XInput** (controller Xbox e gamepad compatibili) tramite **libreria Jamepad**.

#### Mappatura Controller
- **Stick Sinistro** - Sterzo (sinistra/destra)
- **Grilletto Destro** - Accelerazione
- **Grilletto Sinistro** - Frenata
- **Pulsante A** - Toggle raccolta dati ON/OFF
- **Pulsante B** - Reset posizione
- **Pulsante Y** - Mostra statistiche
- **Start/Back** - Esci

#### Requisiti Setup Controller
- **Libreria Jamepad** (inclusa in `lib/Jamepad.jar`)
- **SDL2** (incluso tramite `lib/sdl2gdx-1.0.5.jar`)
- **Controller compatibile XInput** (Xbox 360, Xbox One, o compatibile)

#### Risoluzione Problemi Controller
1. **Controller non rilevato**: Assicurati che il controller sia connesso prima di avviare l'applicazione
2. **Nessuna risposta**: Prova a riavviare con il controller già connesso
3. **Mappatura errata**: Usa i controlli tastiera come fallback


### Priorità di Controllo
Il sistema prioritizza automaticamente:
1. **Controller gamepad** (se connesso e rilevato)
2. **Controlli tastiera** (come fallback)

### Note Raccolta Dati
- **Tutti gli input di controllo** (tastiera e controller) vengono registrati nei dataset
- **Input del controller** fornisce sterzo più fluido per dati di training migliori
- **Metodi di input misti** sono supportati durante la raccolta

## 🤖 Sistema KNN (K-Nearest Neighbors)

Il progetto implementa un algoritmo avanzato **K-Nearest Neighbors** per la guida autonoma utilizzando machine learning.

### Caratteristiche KNN

**Implementazione Algoritmo:**
- **Struttura dati KD-Tree** per ricerca efficiente dei vicini più prossimi
- **Distanza Euclidea** come metrica di distanza
- **Normalizzazione dati** per maggiore accuratezza
- **Sistema di voto pesato** per le predizioni
- **Parametri configurabili** (valore K, normalizzazione, ecc.)

**Configurazioni Disponibili:**
- **Auto-Ottimizzata**: Addestrata su dati SimpleDriver (K=5, distanza Euclidea)
- **Umana-Ottimizzata**: Addestrata su dati di guida umana (K=5, distanza Euclidea)

### Utilizzo KNN

**Test del Sistema:**
```cmd
# Usa l'opzione 6 del menu o esegui direttamente:
JavaClientTorcs/scripts/test_knn.bat
```

**Guida Autonoma:**
```cmd
# Usa l'opzione 7 per dataset umano:
JavaClientTorcs/scripts/run_knn_driving_human.bat

# Usa l'opzione 8 per dataset automatico:
JavaClientTorcs/scripts/run_knn_driving_auto.bat
```

**Confronto Configurazioni:**
```cmd
# Usa l'opzione 8 del menu per confronto dettagliato
```

### Dettagli Tecnici KNN

**Caratteristiche di Input:**
- Sensori pista (19 sensori di distanza)
- Velocità, RPM, informazioni marcia
- Posizione e angolo in pista
- Posizioni avversari (se disponibili)

**Azioni di Output:**
- Angolo sterzo (-1.0 a 1.0)
- Accelerazione (0.0 a 1.0)
- Freno (0.0 a 1.0)
- Selezione marcia

**Prestazioni:**
- Predizione in tempo reale (< 10ms per decisione)
- Apprendimento adattivo dai dati di training
- Gestione robusta dei casi limite

## 📊 Dataset & Configurazione Auto

**Configurazione Auto**: Tutti i dataset e le configurazioni del progetto sono specificamente ottimizzati per la **Ferrari F2001** (car1-ow1). L'elenco completo delle auto disponibili in TORCS può essere trovato su: https://www.igcd.net/vehicle.php?id=15647

**Cambiare l'Auto**: Per utilizzare un'auto diversa, modificare il campo `car name` nel file di configurazione scr_server:
- **Windows**: `C:\Program Files (x86)\torcs\drivers\scr_server\scr_server.xml`
- **Linux**: `/usr/local/share/games/torcs/drivers/scr_server/scr_server.xml`

Esempio di configurazione:
```xml
<attstr name="car name" val="car1-ow1"></attstr>
```

**Impostazioni Aggiuntive Specifiche dell'Auto**: Potrebbe essere necessario regolare i parametri del cambio marcia in base alle specifiche dell'auto selezionata.

**Configurazione Cambio Marcia**: Modifica i parametri del cambio marcia nei file Java del driver:

**Per SimpleDriver (guida AI)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/SimpleDriver.java`
- **Linee 30-35**: Modifica le costanti del cambio marcia
  ```java
  /* === COSTANTI PER IL CAMBIO MARCIA === */
  // RPM minimi per salire di marcia [per marcia 1-6]
  final int[] gearUp = { 19000, 19000, 19000, 19000, 19000, 0 };
  // RPM massimi per scalare di marcia [per marcia 1-6]
  final int[] gearDown = { 0, 7000, 7000, 7000, 7000, 7000 };
  ```

**Per HumanController (guida manuale)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/HumanController.java`
- **Linee 197-244**: Modifica la logica del cambio marcia nel metodo `getGear()`
  - **Linea 220**: `if (currentGear > 0 && currentGear < 6 && rpm >= 19000)` - Cambia `19000` per RPM di upshift
  - **Linea 224**: `else if (currentGear > 1 && rpm <= 7000)` - Cambia `7000` per RPM di downshift

**Regola questi valori in base alle specifiche della tua auto**:
- Riduci i valori `gearUp` per auto con RPM massimi più bassi
- Regola i valori `gearDown` per prestazioni ottimali del motore
- Modifica la logica del cambio se la tua auto ha un numero diverso di marce

I dataset vengono creati automaticamente nella directory principale:
- `auto_dataset.csv` - Dati raccolti automaticamente (SimpleDriver) - Contiene sensori essenziali per analisi
- `human_dataset.csv` - Dati raccolti manualmente (HumanController) - Contiene sensori essenziali per analisi
- `enhanced_dataset.csv` - Dataset completo con quasi tutti i sensori disponibili di TORCS - Ideale per implementazioni future e analisi dati
- `combined_dataset.csv` - Dataset combinati (auto_dataset.csv + human_dataset.csv) - Solo sensori essenziali

## 🔧 Tecnologia

- **Linguaggio**: Java
- **IDE**: NetBeans 26 (progetto nativo)
- **Build Tool**: Apache Ant
- **Formato Dati**: CSV
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
- Il sistema è progettato per Windows

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


#### Problema: TORCS non si avvia
- **Soluzione**: Controlla le dipendenze OpenGL
- **Windows**: Aggiorna i driver della scheda video


#### Problema: SCR Patch non funziona
- **Soluzione**: Verifica che il patch sia stato applicato nella directory corretta
- **Windows**: Controlla che i file siano in `C:\Program Files\TORCS`


### Prossimi Passi
Dopo la configurazione:
1. Testa l'installazione avviando TORCS manualmente
2. Usa `torcs_menu.bat` per accedere alle funzionalità del progetto
3. Procedi con la raccolta dati o test del modello
