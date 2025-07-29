# Behavioral Cloning per TORCS - Guida Completa

## 🚗 Introduzione

Questo progetto estende un client Java fornito dall'Università degli Studi di Salerno per implementare behavioral cloning usando K-NN per TORCS (The Open Racing Car Simulator). L'approccio imita il comportamento umano basandosi su dati raccolti durante sessioni di guida manuale e automatica.

## 🏗️ Architettura

### Componenti Principali
- **DataCollector**: Raccoglie coppie osservazione-azione
- **BehavioralCloningDriver**: Implementa K-NN per prendere decisioni di guida
- **HumanController**: Controller manuale per raccolta dati
- **SimpleDriver**: Driver automatico per raccolta dati
- **Dataset**: File CSV con struttura: 19_track_sensors,speedX,angle,position,target_speed,steer

### Flusso di Lavoro
1. **Raccolta Dati**: Guida manuale o automatica per generare dataset
2. **Addestramento**: Il modello K-NN usa direttamente i dati raccolti (no training esplicito)
3. **Test**: Valuta le prestazioni del modello behavioral cloning
4. **Miglioramento**: Aumenta dataset con dati aggiuntivi per situazioni problematiche

## 🚀 Avvio Rapido

### 1. Prerequisiti
- Java installato e nel PATH
- TORCS avviato con modulo JavaClientTorcs
- Maven per build del progetto

### 2. Build del Progetto

#### **Metodo 1: NetBeans 26 (Raccomandato)**
**Ambiente di sviluppo:** NetBeans 26 + JDK 24

**Procedura semplificata:**
1. **Apri il progetto in NetBeans 26**
   - File → Open Project
   - Seleziona la cartella `JavaClientTorcs`
   - NetBeans riconoscerà automaticamente il progetto Maven

2. **Build con un click**
   - **Pulsante verde "Run Project"** (▶️) nella toolbar
   - **Oppure:** Right-click sul progetto → Clean and Build

3. **Verifica build**
   - Il JAR verrà generato in: `JavaClientTorcs/target/JavaClientTorcs-1.0-SNAPSHOT.jar`
   - **Nessuna configurazione aggiuntiva richiesta**

#### **Metodo 2: Maven da terminale**
```bash
cd JavaClientTorcs
mvn clean package
```

#### **Metodo 3: NetBeans da terminale**
```bash
cd JavaClientTorcs
mvn -Dnetbeans.deploy=true clean package
```

### **Note sulla compatibilità**
- **Testato con:** NetBeans 26 + JDK 24 ✅
- **Compatibile con:** NetBeans 25+ e JDK 21+
- **Sistema operativo:** Windows 10/11 (testato)

### **Note sui warning di build**
Durante la build con NetBeans o Maven, potresti vedere warning come:
- `WARNING: A restricted method in java.lang.System has been called`
- `WARNING: A terminally deprecated method in sun.misc.Unsafe has been called`

**Questi warning sono normali e non influenzano il funzionamento del progetto.** Sono warning generati da Maven stesso e dalle librerie di supporto, non dal codice. Il build terminerà con successo come indicato da `BUILD SUCCESS`.

### 3. Utilizza le Utility Windows

#### Menu Interattivo (Raccomandato)
```bash
torcs_menu.bat
```

#### Utilità Singole
- `run_manual_driving.bat` - Guida manuale con raccolta dati
- `run_auto_collection.bat` - Raccolta dati automatica
- `run_simpledriver.bat` - Esecuzione SimpleDriver (senza raccolta)
- `test_human_model.bat` - Test con dati umani
- `test_auto_model.bat` - Test con dati automatici
- `combine_datasets.bat` - Combina dataset
- `torcs_menu.bat` - Menu interattivo unificato (raccomandato)

## 📊 Raccolta Dati

### 🎮 Guida Manuale (Raccomandata)
```bash
run_manual_driving.bat
```

**Comandi di guida disponibili:**
- **↑** (Freccia Su) / **W** / **I** / **8** (tastierino) - Accelerare
- **↓** (Freccia Giù) / **S** / **K** / **2** (tastierino) - Frenare
- **←** (Freccia Sinistra) / **A** / **J** / **4** (tastierino) - Sterzare sinistra
- **→** (Freccia Destra) / **D** / **L** / **6** (tastierino) - Sterzare destra
- **Q** - Scalare marcia
- **E** - Salire marcia
- **R** - Reset posizione
- **C** - Toggle raccolta dati (ON/OFF)
- **P** - Mostra statistiche dataset
- **X** - Esci

**Suggerimenti per raccolta efficace:**
1. **Variabilità**: Guida in diverse condizioni (curve, rettilinei, velocità)
2. **Quantità**: Minimo 1000-5000 esempi per pista
3. **Qualità**: Evita dati di crash o comportamenti anomali
4. **Salvataggio**: Premi 'C' regolarmente per salvare i dati

### 🤖 Raccolta Automatica
```bash
run_auto_collection.bat
```

Il SimpleDriver guida automaticamente e raccoglie dati. Utile per:
- Generare dataset di base rapidamente
- Avere comportamenti consistenti come baseline
- Combinare con dati manuali per migliorare copertura

## 🧪 Test del Modello

### ⚠️ **Importante:** Devi prima raccogliere i dati!
Prima di testare il modello, assicurati di avere i file dataset:

### **Sequenza Corretta:**
1. **Prima raccogli i dati:**
   - Per dati umani: `run_manual_driving.bat`
   - Per dati automatici: `run_auto_collection.bat`

2. **Poi testa il modello:**
   - Test con Dati Umani: `test_human_model.bat`
   - Test con Dati Automatici: `test_auto_model.bat`

### **Cosa succede se mancano i dati:**
I file batch controlleranno automaticamente se esistono i dataset e ti guideranno nel processo corretto.

### Test con Dataset Combinati
```bash
combine_datasets.bat
test_auto_model.bat
```

### Valutazione Performance
Durante il test, il modello:
1. Legge lo stato corrente (sensori, velocità, posizione)
2. Trova i k-esimi vicini nel dataset
3. Usa l'azione del vicino più simile
4. Monitora distanza dalla linea ideale e tempo giro

## 📁 Struttura File

### Dataset Generati
- `dataset.csv` - Dati raccolti automaticamente con SimpleDriver
- `human_dataset.csv` - Dati raccolti manualmente con HumanController
- `combined_dataset.csv` - Dataset combinati per training migliore

### Struttura Progetto
```
Progetto/
├── README.md                          # Questa guida completa
├── torcs_menu.bat                       # Menu principale interattivo
├── run_manual_driving.bat            # Guida manuale
├── run_auto_collection.bat            # Raccolta automatica
├── run_simpledriver.bat              # SimpleDriver base
├── test_human_model.bat              # Test dati umani
├── test_auto_model.bat               # Test dati automatici
├── combine_datasets.bat              # Combina dataset
└── JavaClientTorcs/
    ├── pom.xml
    └── target/
        └── JavaClientTorcs-1.0-SNAPSHOT.jar
```

## 🔧 Parametri Configurabili

### Nel codice BehavioralCloningDriver:
```java
// Numero di vicini per K-NN (default: 5)
int k = 5;

// Soglia per filtrare stati troppo diversi
double distanceThreshold = 0.1;

// Nome file dataset
String datasetFile = "dataset.csv";
```

### Ottimizzazione K-NN
- **k piccolo (3-5)**: Decisioni più precise ma sensibili al rumore
- **k grande (7-15)**: Più stabile ma può perdere dettagli
- **distance_threshold**: Filtra stati troppo diversi (0.05-0.2)

## 📈 Struttura Dati

### Input (Osservazione)
- **19 sensori track**: Distanze dai bordi pista (range: 0-200 metri)
- **speedX**: Velocità attuale (km/h)
- **angleToTrackAxis**: Angolo rispetto alla pista (radianti, -π a π)
- **trackPosition**: Posizione laterale (-1 = bordo sinistro, 1 = bordo destro)

### Output (Azione)
- **targetSpeed**: Velocità desiderata (km/h)
- **steer**: Angolo di sterzata (-1 = sinistra massima, 1 = destra massima)

### Formato CSV
```
19_track_sensors,speedX,angle,position,target_speed,steer
1.2,3.4,5.6,7.8,9.0,10.1
...
```

## 🛠️ Comandi Avanzati

### Avvio Manuale da Terminale
```bash
cd JavaClientTorcs

# Guida manuale
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client HumanController --collect

# Raccolta automatica
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client SimpleDriver --collect

# Test con dataset specifico
java -cp target\JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client BehavioralCloningDriver human_dataset.csv
```

### Debug e Statistiche
```java
// Stampa statistiche dataset
BehavioralCloningDriver driver = new BehavioralCloningDriver();
driver.printDatasetStats();

// Abilita log dettagliati
System.setProperty("debug", "true");
```

## 🔍 Risoluzione Problemi

### Data Distribution Mismatch
- **Sintomo**: Comportamento erratico in situazioni nuove
- **Soluzione**: Aumenta il dataset con più varietà di situazioni
- **Identificazione**: Analizza dove il modello fallisce

### Prestazioni K-NN
- **Problema**: K-NN diventa lento con dataset grandi (>10k righe)
- **Soluzioni**:
  - Limita k a 3-5 per tempi reali
  - Usa campionamento del dataset
  - Considera KD-tree per dataset molto grandi

### Raccolta Dati Inefficace
- **Problema**: Dataset piccolo o non rappresentativo
- **Soluzione**: Guida più giri, varia velocità e traiettorie

### Comandi Freccia non Funzionano
- **Causa**: Terminale non supporta sequenze ANSI
- **Soluzione**: Usa WASD o IJKL come alternativa

## 🎯 Esempi di Utilizzo

### Scenario 1: Nuova Pista
1. `torcs_menu.bat`
2. Seleziona "Guida Manuale"
3. Guida per 10-20 giri
4. Salva con 'C'
5. Testa con "Test Modello - Dati Umani"

### Scenario 2: Miglioramento Performance
1. Raccolta automatica: `run_auto_collection.bat`
2. Raccolta manuale: `run_manual_driving.bat`
3. Combinazione: `combine_datasets.bat`
4. Test finale: `test_auto_model.bat`

### Scenario 3: Debugging Comportamento
1. Identifica situazione problematica (es. curve strette)
2. Raccogli dati specifici per quella situazione
3. Testa nuovamente
4. Valuta miglioramento

## 🧪 Analisi Dataset

### Statistiche Utili
- **Dimensione dataset**: Numero totale di esempi
- **Distribuzione velocità**: Range e frequenze
- **Copertura angoli**: Distribuzione nelle curve
- **Outlier**: Dati anomali da rimuovere

### Visualizzazione Dati
Puoi analizzare il dataset.csv con:
- **Excel/LibreOffice**: Per grafici e statistiche
- **Python pandas**: Analisi avanzata
- **Script custom**: Visualizzazione sensori specifici

## 🔮 Estensioni Future

### Algoritmi Alternativi
- **Rete Neurale**: Sostituisci K-NN con una piccola rete neurale
- **Random Forest**: Per gestire meglio le interazioni non lineari
- **Regressione Lineare**: Per relazioni più semplici

### Feature Engineering
- **Derivate temporali**: Velocità di cambiamento
- **Normalizzazione**: Scala input tra 0-1
- **Pesi feature**: Importanza diversa per sensori

### Validazione Avanzata
- **Cross-validation**: Valuta generalizzazione
- **Separazione train/test**: 80/20 split
- **Metriche performance**: Tempo giro, distanza linea ideale

## 📚 Risorse Aggiuntive

### File di Configurazione
- `BehavioralCloningDriver.java`: Parametri principali
- `HumanController.java`: Mappatura tasti
- `SimpleDriver.java`: Logica guida automatica

### Link Utili
- TORCS: http://torcs.sourceforge.net/
- JavaClientTorcs: Documentazione progetto base
- K-NN: https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm

---

## 📝 Note Finali

1. **Requisiti Terminale**: Per i tasti freccia, il terminale deve supportare le sequenze di escape ANSI
2. **Salvataggio Dati**: Premi 'C' regolarmente per salvare i dati manuali
3. **Connessione TORCS**: Assicurati che TORCS sia avviato prima di eseguire i comandi
4. **Build Maven**: Esegui sempre `mvn clean package` dopo modifiche al codice
5. **Backup Dataset**: Copia i file CSV importanti prima di sovrascrivere

Per supporto tecnico o domande, consulta la documentazione completa o i file sorgente.