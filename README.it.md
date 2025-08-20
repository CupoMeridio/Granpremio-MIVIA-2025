# Sistema di Raccolta Dati e Guida Autonoma per TORCS

[![Language: English](https://img.shields.io/badge/lang-en-green.svg)](README.md) [![Language: Italian](https://img.shields.io/badge/lang-it-blue.svg)](README.it.md)

Un sistema completo per TORCS (The Open Racing Car Simulator) che combina raccolta dati, machine learning e guida autonoma tramite multiple tecnologie di intelligenza artificiale: algoritmi KNN, reti neurali MLP e driver tradizionali.

## 📋 Indice

- [🎯 Panoramica del Progetto](#-panoramica-del-progetto)
- [🚀 Installazione e Setup](#-installazione-e-setup)
- [🎮 Come Usare il Sistema](#-come-usare-il-sistema)
- [📁 Struttura del Progetto](#-struttura-del-progetto)
- [🕹️ Controlli di Guida & Supporto Controller](#️-controlli-di-guida--supporto-controller)
- [🤖 Sistema KNN (K-Nearest Neighbors)](#-sistema-knn-k-nearest-neighbors)
- [📊 Dataset & Configurazione Auto](#-dataset--configurazione-auto)
- [🔧 Tecnologia e Architettura](#-tecnologia-e-architettura)
- [⚡ Guida Rapida](#-guida-rapida)
- [📋 Note Importanti](#-note-importanti)
- [🪟 Configurazione TORCS per Windows](#-configurazione-torcs-per-windows)

## 🎯 Panoramica del Progetto

Questo progetto implementa un sistema avanzato per TORCS che offre:
- **🎮 Guida manuale** con supporto tastiera e gamepad per raccolta dati
- **🤖 Guida autonoma avanzata** tramite multiple tecnologie AI:
  - **K-Nearest Neighbors (KNN)** con algoritmo KD-Tree ottimizzato
  - **Reti Neurali MLP** (Multi-Layer Perceptron) con comunicazione Python-Java
  - **SimpleDriver** con logiche di controllo tradizionali
- **📊 Raccolta dati intelligente** con dataset CSV ottimizzati per machine learning
- **🔧 Sistema modulare** con controller intercambiabili
- **🪟 Compatibilità Windows** completa con menu interattivo

[⬆️ Torna all'indice](#-indice)

## 🚀 Installazione e Setup

### Prerequisiti
- **Java 24+** installato e nel PATH ([Download JDK](https://www.oracle.com/it/java/technologies/downloads))
- **Apache Ant** per build del progetto ([Download Apache Ant](https://ant.apache.org/bindownload.cgi)) (o **NetBeans 26+** per build IDE - [Download NetBeans](https://netbeans.apache.org/front/main/download/nb26))
- **Python 3.13.X+** per la funzionalità di rete neurale MLP ([Download Python](https://www.python.org/downloads/))
- **TORCS 1.3.7** con patch SCR (vedi sezione [Configurazione TORCS](#-configurazione-torcs-per-windows))

### Setup Python per Rete Neurale MLP

La funzionalità di guida autonoma MLP (Multi-Layer Perceptron) richiede Python. L'ambiente virtuale e le dipendenze sono già forniti nel repository.

#### 1. Installare l'Ultima Versione di Python
- Scarica l'ultima versione di Python da [python.org](https://www.python.org/downloads/)
- **Importante:** Durante l'installazione, spunta "Add Python to PATH"
- Verifica installazione: `python --version`

#### 2. Ambiente Virtuale (Già Fornito)
Il repository include un ambiente virtuale pre-configurato (cartella `.venv`) con tutte le dipendenze necessarie già installate. Non è richiesta alcuna configurazione aggiuntiva.

**Librerie Pre-installate nell'Ambiente Virtuale:**
- `pandas` - Manipolazione e analisi dati
- `numpy` - Calcolo numerico
- `scikit-learn` - Algoritmi di machine learning (MLPRegressor)
- `joblib` - Serializzazione modelli
- `jupyter` - Per eseguire notebook di training (opzionale)

### Build

#### Metodo 1: NetBeans 26 (Raccomandato)
**Ambiente di sviluppo:** NetBeans 26 + JDK 24+

**Procedura semplificata:**
1. **Apri il progetto in NetBeans 26**
   - File → Open Project
   - Seleziona la cartella `JavaClientTorcs`
   - NetBeans riconoscerà automaticamente il progetto Ant

2. **Build con un click**
   - Right-click sul progetto → Clean and Build

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

[⬆️ Torna all'indice](#-indice)

## 🎮 Come Usare il Sistema

### Metodo 1: Menu Interattivo (Raccomandato)

Il progetto include un sistema di menu completo con tutte le funzionalità:

#### Windows
```cmd
torcs_menu.bat
```

### Opzioni del Menu

**🎮 Raccolta Dati:**
- Opzione 1: Guida manuale (raccolta dati umani)

**📊 Gestione Dataset:**
- Opzione 2: Visualizza statistiche dataset
- Opzione 3: Converti dataset completo in dataset sensori ML
- Opzione 4: Strumenti di Analisi Dataset (Google Colab)

**🤖 Guida Autonoma e Intelligenza Artificiale:**
- Opzione 5: **SimpleDriver** (guida automatica tradizionale)
- Opzione 6: **Guida KNN** (Valori Continui)
- Opzione 7: **KNN Classifier** (Azioni Discrete)
- Opzione 8: **Guida MLP** (Rete Neurale)

**📖 Documentazione:**
- Opzione 9: Apri guida completa

### Metodo 2: Script Individuali

#### Raccolta Dati Manuali
- `JavaClientTorcs/scripts/run_manual_driving.bat`

#### Guida Autonoma Tradizionale
- **SimpleDriver**: `JavaClientTorcs/scripts/run_simpledriver.bat`

#### Guida Autonoma con Machine Learning
- **KNN Standard**: `JavaClientTorcs/scripts/run_knn_driving.bat`
- **KNN Dataset umano**: `JavaClientTorcs/scripts/run_knn_driving_human.bat`
- **KNN Classifier**: `JavaClientTorcs/scripts/run_knn_classifier.bat`
- **MLP Rete Neurale**: `JavaClientTorcs/scripts/run_mlp_driving_human.bat`



[⬆️ Torna all'indice](#-indice)

## 📁 Struttura del Progetto

```
Progetto/
├── README.md               # Questa documentazione
├── torcs_menu.bat          # Menu Windows
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
│   │       ├── Client.java                 # Client principale
│   │       ├── Controller.java             # Interfaccia controller
│   │       ├── HumanController.java        # Controller manuale con gamepad
│   │       ├── SimpleDriver.java           # Driver automatico tradizionale
│   │       ├── KNNDriver.java              # Driver KNN con KD-Tree
│   │       ├── MLPDriver.java              # Driver rete neurale MLP
│   │       ├── KNNClassifierDriver.java    # Driver KNN con azioni discrete
│   │       ├── DataPoint.java              # Struttura dati per ML
│   │       ├── KDTree.java                 # Implementazione KD-Tree
│   │       ├── EnhancedDataCollectionManager.java # Gestione dati avanzata
│   │       ├── MessageBasedSensorModel.java # Modello sensori
│   │       ├── MessageParser.java          # Parser messaggi
│   │       ├── SensorModel.java            # Interfaccia sensori
│   │       └── SocketHandler.java          # Gestione socket
│   ├── lib/
│   │   ├── Jamepad.jar
│   │   └── sdl2gdx-1.0.5.jar
│   ├── build/              # Output build temporaneo
│   ├── dist/
│   │   ├── JavaClientTorcs.jar
│   │   └── lib/            # Librerie copiate
│   └── scripts/                      # Script di esecuzione (vedi sezione Utilizzo)

└── mlpDriver/
    ├── mlpDrive.py               # Server UDP per rete neurale
    ├── mlpFitting.ipynb          # Notebook training MLP
    ├── best_mlp_model.pkl        # Modello MLP pre-addestrato
    ├── data.csv                  # Dataset per training MLP
    └── socketTest.py             # Test comunicazione socket UDP
```

[⬆️ Torna all'indice](#-indice)

## 🕹️ Controlli di Guida & Supporto Controller

### Controlli Tastiera
Durante la guida manuale, usa:
- **Accelerazione/Frenata**: W/S | ↑↓ | 8/2 | I/K
- **Sterzo Sinistra/Destra**: A/D | ←→ | 4/6 | J/L
- **Cambio marcia manuale**: Q/E (solo se modalità manuale attiva)
- **Toggle cambio automatico**: G
- **Toggle ABS**: V
- **Toggle assistenza sterzo**: B
- **Toggle frizione automatica**: N
- **Toggle raccolta dati**: C (ON/OFF)
- **Reset controlli**: R
- **Esci**: X

### Supporto Controller Gamepad
Il sistema supporta **controller compatibili XInput** (controller Xbox e gamepad compatibili) tramite **libreria Jamepad**.

#### Mappatura Controller
- **Stick Sinistro** - Sterzo (sinistra/destra)
- **Grilletto Destro** - Accelerazione
- **Grilletto Sinistro** - Frenata
- **Bumper Sinistro (LB)** - Cambio marcia giù (solo modalità manuale)
- **Bumper Destro (RB)** - Cambio marcia su (solo modalità manuale)
- **Pulsante A** - Toggle frizione automatica
- **Pulsante B** - Toggle ABS
- **Pulsante Y** - Toggle assistenza sterzo
- **Start** - Toggle raccolta dati ON/OFF
- **Back** - Toggle cambio automatico

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

[⬆️ Torna all'indice](#-indice)

## 🤖 Sistema KNN (K-Nearest Neighbors)

Il progetto implementa un algoritmo avanzato **K-Nearest Neighbors** per la guida autonoma utilizzando machine learning, con due implementazioni distinte:

### 🚗 Guida KNN (Controllo Continuo)
Implementazione KNN tradizionale per guida autonoma fluida con valori continui di sterzo, accelerazione e frenata.

### 🎯 KNN Classifier (Azioni Discrete)
Implementazione KNN avanzata che utilizza la classificazione di azioni discrete per un comportamento di guida più preciso e prevedibile.

### Utilizzo KNN

**Guida KNN (Controllo Continuo):**
```cmd
# Usa l'opzione 6 del menu o direttamente:
JavaClientTorcs/scripts/run_knn_driving_human.bat
```

**KNN Classifier (Azioni Discrete):**
```cmd
# Usa l'opzione 7 del menu o direttamente:
JavaClientTorcs/scripts/run_knn_classifier.bat
```



### Dettagli Tecnici KNN

**Caratteristiche di Input:**
- Sensori pista (10 sensori di distanza selezionati)
- Velocità (speedX)
- Angolo rispetto alla pista (angleToTrackAxis)
- Posizione in pista (trackPosition)
- Distanza dalla linea di partenza (distanceFromStartLine)
- **Totale: 14 features normalizzate**

**Azioni di Output:**
- Angolo sterzo (-1.0 a 1.0)
- Accelerazione (0.0 a 1.0)
- Freno (0.0 a 1.0)
- Selezione marcia

**Prestazioni:**
- Predizione in tempo reale (< 10ms per decisione)
- **Voto pesato** basato sulla distanza euclidea inversa
- **Normalizzazione automatica** dei dati per maggiore accuratezza
- **Gestione specializzata** per situazioni fuori strada
- **Modalità classificatore** con 15 vicini per maggiore stabilità

[⬆️ Torna all'indice](#-indice)

## 📊 Strumenti di Analisi Dataset (Google Colab)

Il progetto include **integrazione con Google Colab** per analisi avanzate e visualizzazione dei dataset.

### Caratteristiche Google Colab

**Strumenti di Analisi:**
- **Normalizzazione e bilanciamento dataset** per migliori prestazioni ML
- **Analisi delle caratteristiche e visualizzazione** con grafici interattivi
- **Valutazione qualità dati** e rilevamento outlier
- **Insights statistici** e analisi delle correlazioni
- **Preprocessing dati** per modelli di machine learning

**Accesso:**
- **Opzione Menu 4**: Apre automaticamente il notebook Google Colab
- **Link Diretto**: [Notebook Analisi Dataset](https://colab.research.google.com/drive/1k-cV_NJBRxCdNuzrNbqFrxhazVwFKo3e?usp=sharing)
- **Integrazione Browser**: Apertura automatica nel browser predefinito

**Utilizzo:**
1. Seleziona **Opzione 4** dal menu principale
2. Carica i tuoi file dataset (vedi sezione [Dataset & Configurazione Auto](#-dataset--configurazione-auto) per dettagli sui file)
3. Esegui le celle di analisi per generare insights
4. Scarica i dataset processati per un training ML migliorato

**Vantaggi:**
- **Elaborazione cloud** - Nessun setup Python locale richiesto
- **Visualizzazioni interattive** per esplorazione dati
- **Strumenti professionali data science** (Pandas, NumPy, Matplotlib)
- **Analisi collaborativa** - Condividi notebook con il team

[⬆️ Torna all'indice](#-indice)

## 🧠 Sistema MLP (Multi-Layer Perceptron) - Rete Neurale

Il progetto include un'implementazione avanzata di **Rete Neurale** utilizzando **Multi-Layer Perceptron** per la guida autonoma con capacità di deep learning.

### Caratteristiche MLP

**Architettura Rete Neurale:**
- **Multi-Layer Perceptron** con layer nascosti configurabili
- **Implementazione Scikit-learn MLPRegressor**
- **Preprocessing dati** con scaling e normalizzazione
- **Ottimizzazione grid search** per hyperparameter tuning
- **Predizione in tempo reale** tramite comunicazione UDP

**Processo di Training:**
- **Ambiente Jupyter Notebook** per training (`mlpFitting.ipynb`)
- **Cross-validation** per valutazione modello
- **Architettura pipeline** con preprocessing e modello
- **Serializzazione modello** usando joblib
- **Metriche prestazioni** (MSE, R²)

### Utilizzo MLP

**Guida Autonoma:**
```cmd
# Usa l'opzione 7 del menu - avvia automaticamente il server Python:
torcs_menu.bat
# Seleziona opzione 7: Guida MLP (Rete Neurale)
```

**Avvio Manuale Server:**
```cmd
# Avvia server MLP Python manualmente:
python mlpDriver\mlpDrive.py

# Poi esegui client Java:
JavaClientTorcs\scripts\run_mlp_driving_human.bat
```

**Training Nuovi Modelli:**
```cmd
# Apri notebook Jupyter per training:
jupyter notebook mlpDriver\mlpFitting.ipynb
```

### Dettagli Tecnici MLP

**Architettura:**
- **Layer Input**: 14 caratteristiche (sensori pista + stato veicolo)
- **Layer Nascosti**: Configurabili (default: 2 layer, 100 neuroni ciascuno)
- **Layer Output**: 3 azioni (sterzo, accelerazione, freno)
- **Attivazione**: ReLU per layer nascosti, lineare per output

**Caratteristiche Input:**
- Sensori pista: `track0, track2, track4, track6, track8, track10, track12, track14, track16, track18`
- Stato veicolo: `speedX, angleToTrackAxis, trackPosition, distanceFromStartLine`

**Azioni Output:**
- Angolo sterzo (-1.0 a 1.0)
- Accelerazione (0.0 a 1.0)
- Freno (0.0 a 1.0)

**Comunicazione:**
- **Server UDP**: Server Python su `localhost:35567`
- **Client Java**: Invia dati sensori, riceve predizioni
- **Protocollo**: Valori separati da virgola
- **Shutdown graceful**: Gestisce comandi di terminazione

**Dipendenze:**
- `pandas` - Manipolazione dati
- `numpy` - Operazioni numeriche
- `scikit-learn` - Implementazione rete neurale
- `joblib` - Serializzazione modelli

[⬆️ Torna all'indice](#-indice)

## 📊 Dataset & Configurazione Auto

**Configurazione Auto**: Il progetto è configurato per funzionare con l'auto standard **car1-trb1 2001 Ferrari 360 GT**. L'elenco completo delle auto disponibili in TORCS può essere trovato su: https://www.igcd.net/vehicle.php?id=15647

**Cambiare l'Auto**: Per utilizzare un'auto diversa, modificare il campo `car name` nel file di configurazione scr_server:
- **Windows**: `C:\Program Files (x86)\torcs\drivers\scr_server\scr_server.xml`
- **Linux**: `/usr/local/share/games/torcs/drivers/scr_server/scr_server.xml`

Esempio di configurazione:
```xml
<attstr name="car name" val="car1-trb1"></attstr>
```

**Impostazioni Aggiuntive Specifiche dell'Auto**: Potrebbe essere necessario regolare i parametri del cambio marcia in base alle specifiche dell'auto selezionata.

**Configurazione Cambio Marcia**: Modifica i parametri del cambio marcia nei file Java del driver:

**Per SimpleDriver (guida AI)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/SimpleDriver.java`
- **Linee 20-22**: Modifica le costanti del cambio marcia
  ```java
  // RPM per salire di marcia [per marcia 1-6]
  final int[] gearUp = { 4500, 5500, 6500, 6500, 7000, 0 };
  // RPM per scalare di marcia [per marcia 1-6]
  final int[] gearDown = { 0, 2500, 3500, 4000, 4500, 5000 };
  ```

**Per HumanController (guida manuale)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/HumanController.java`
- **Linee 37-39**: Modifica le costanti del cambio marcia
  ```java
  // RPM per salire di marcia [per marcia 1-6]
  private static final int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
  // RPM per scalare di marcia [per marcia 1-6]
  private static final int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };
  ```

**Regola questi valori in base alle specifiche della tua auto**:
- Riduci i valori `gearUp` per auto con RPM massimi più bassi
- Regola i valori `gearDown` per prestazioni ottimali del motore
- Modifica la logica del cambio se la tua auto ha un numero diverso di marce

I dataset vengono creati automaticamente nelle rispettive directory:
- `JavaClientTorcs/human_dataset.csv` - Dati raccolti manualmente (HumanController) - Contiene sensori essenziali per analisi
- `JavaClientTorcs/enhanced_dataset.csv` - Dataset completo con quasi tutti i sensori disponibili di TORCS - Ideale per implementazioni future e analisi dati
- `mlpDriver/data.csv` - Dataset utilizzato per l'addestramento della rete neurale MLP

**Conversione Dataset (Opzione 3)**: L'opzione 3 del menu converte il dataset completo `enhanced_dataset.csv` (che contiene tutti i sensori TORCS) in un dataset ottimizzato `human_dataset.csv` contenente solo i sensori specifici utilizzati dai modelli di machine learning (10 sensori di pista alternati, velocità, posizione, azioni di controllo).

[⬆️ Torna all'indice](#-indice)

## 🔧 Tecnologia e Architettura

### Stack Tecnologico
- **Linguaggio Principale**: Java 24+
- **Linguaggio ML**: Python 3.13+ (per MLP)
- **IDE**: NetBeans 26 (progetto nativo)
- **Build Tool**: Apache Ant
- **Formato Dati**: CSV
- **Comunicazione**: Socket UDP con TORCS (porta 3001) e Python (porta 35567)
- **Machine Learning**: 
  - K-Nearest Neighbors con KD-Tree ottimizzato
  - Multi-Layer Perceptron (scikit-learn)
  - Algoritmi tradizionali basati su regole
- **Librerie**: Jamepad (gamepad), SDL2 (input), pandas, numpy, scikit-learn

### Compatibilità NetBeans
- **Testato con**: NetBeans 26 + JDK 24 ✅
- **Progetto nativo**: Apri direttamente la cartella `JavaClientTorcs` come progetto Ant

### Architettura del Sistema
- **Controller modulari**: 
  - HumanController (guida manuale con gamepad)
  - SimpleDriver (algoritmi tradizionali)
  - KNNDriver (machine learning KNN)
  - MLPDriver (rete neurale)
  - KNNClassifierDriver (azioni discrete)
- **Raccolta dati intelligente**: EnhancedDataCollectionManager, DataPoint
- **Algoritmi ML**: KDTree, KNNConfig, ActionDiscretizer
- **Comunicazione**: SocketHandler, MessageParser, UDP Python-Java
- **Modello sensoriale**: SensorModel, MessageBasedSensorModel

[⬆️ Torna all'indice](#-indice)

## ⚡ Guida Rapida

1. **Setup**: Installa TORCS 1.3.7 + patch SCR
2. **Build**: `ant clean && ant jar` (da JavaClientTorcs/)
3. **Avvia**: `torcs_menu.bat` per il menu interattivo
4. **Raccogli dati**: Opzione 1 per guida manuale
5. **Testa sistemi autonomi**:
   - Opzione 5: SimpleDriver (tradizionale)
   - Opzione 6: KNN con dataset umano
   - Opzione 7: KNN Classifier (azioni discrete)
   - Opzione 8: MLP Rete Neurale

[⬆️ Torna all'indice](#-indice)

## 📋 Note Importanti

- **⚠️ TORCS deve essere avviato PRIMA** dei driver Java
- **📊 Dataset**: Salvati nella directory di esecuzione degli script
- **🎯 Dati ottimali**: Raccogli almeno 1000-5000 esempi per pista
- **🪟 Sistema**: Progettato specificamente per Windows
- **🚗 Auto**: Configurato per car1-trb1 2001 Ferrari 360 GT

[⬆️ Torna all'indice](#-indice)

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
1. **Testa l'installazione** avviando TORCS manualmente
2. **Usa il menu interattivo** con `torcs_menu.bat`
3. **Raccogli dati** con la guida manuale (Opzione 1)
4. **Testa il sistema KNN** (Opzione 6)
5. **Prova la guida autonoma** (Opzione 7)

[⬆️ Torna all'indice](#-indice)

---

## 🎉 Conclusione

Questo progetto rappresenta un sistema completo per l'integrazione di multiple tecnologie di intelligenza artificiale con TORCS, offrendo:

- **🎮 Interfaccia utente intuitiva** con menu interattivo
- **🤖 Algoritmi di guida autonoma diversificati**:
  - Algoritmi tradizionali (SimpleDriver)
  - Machine Learning KNN con KD-Tree
  - Reti Neurali MLP con Python
  - Classificazione azioni discrete
- **📊 Raccolta dati professionale** per ricerca e sviluppo
- **🔧 Architettura modulare** facilmente estendibile
- **🪟 Compatibilità Windows** completa

### 🚀 Sviluppi Futuri

- Implementazione di algoritmi ML aggiuntivi (Reinforcement Learning, Deep Learning)
- Supporto per piste multiple e configurazioni avanzate
- Interfaccia grafica per visualizzazione dati in tempo reale
- Ottimizzazioni per competizioni multi-agente
- Integrazione di sensori aggiuntivi e fusione sensoriale
- Ottimizzazione hyperparameter automatica per MLP

### 📞 Supporto

Per problemi o domande:
1. Controlla la sezione [Note Importanti](#-note-importanti)
2. Verifica la [Configurazione TORCS](#-configurazione-torcs-per-windows)
3. Consulta i log di errore per diagnostica

---

**Progetto MIVIA 2025 - Sistema di Raccolta Dati e Guida Autonoma per TORCS**

*Sviluppato per ricerca e applicazioni educative in Machine Learning e Guida Autonoma*

[⬆️ Torna all'indice](#-indice)
