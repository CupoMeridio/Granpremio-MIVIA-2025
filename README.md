# 🏎️ TORCS Data Collection and Autonomous Driving System

[![Language: English](https://img.shields.io/badge/lang-en-green.svg)](README.md) [![Language: Italian](https://img.shields.io/badge/lang-it-blue.svg)](README.it.md)

A comprehensive system for TORCS (The Open Racing Car Simulator) that combines intelligent data collection, autonomous driving via multiple AI technologies (KNN, MLP Neural Networks, traditional algorithms), and manual control with keyboard and gamepad support.

## 📋 Table of Contents

- [🎯 Project Overview](#-project-overview)
- [🚀 Installation and Setup](#-installation-and-setup)
- [🎮 How to Use the System](#-how-to-use-the-system)
- [📁 Project Structure](#-project-structure)
- [🕹️ Driving Controls & Controller Support](#️-driving-controls--controller-support)
- [🤖 KNN System](#-knn-system)
- [📊 Datasets & Car Configuration](#-datasets--car-configuration)
- [🔧 Technology and Architecture](#-technology-and-architecture)
- [⚡ Quick Start Guide](#-quick-start-guide)
- [📋 Important Notes](#-important-notes)
- [🪟 TORCS Configuration for Windows](#-torcs-configuration-for-windows)

## 🎯 Project Overview

This advanced system for TORCS offers:
- 🧠 **Advanced autonomous driving** via multiple AI technologies:
  - **K-Nearest Neighbors (KNN)** with optimized KD-Tree algorithm
  - **Multi-Layer Perceptron (MLP)** neural networks with Python-Java communication
  - **SimpleDriver** with traditional rule-based control algorithms
- 🎮 **Manual driving** with keyboard and gamepad support
- 📊 **Intelligent data collection** for training and analysis
- 🔧 **Modular system** with multiple controllers and configurations
- 🪟 **Windows compatibility** with optimized setup

[⬆️ Back to Table of Contents](#-table-of-contents)

## 🚀 Installation and Setup

### Prerequisites
- **Java 24+** installed and in PATH ([Download JDK](https://www.oracle.com/it/java/technologies/downloads))
- **Apache Ant** for project building ([Download Apache Ant](https://ant.apache.org/bindownload.cgi)) (or **NetBeans 26+** for IDE build - [Download NetBeans](https://netbeans.apache.org/front/main/download/nb26))
- **Python 3.13.X+** for MLP neural network functionality ([Download Python](https://www.python.org/downloads/))
- **TORCS 1.3.7 with SCR patch** (see [TORCS Configuration for Windows](#-torcs-configuration-for-windows))
- TORCS started with JavaClientTorcs module

### Python Setup for MLP Neural Network

The MLP (Multi-Layer Perceptron) autonomous driving feature requires Python. The virtual environment and dependencies are already provided in the repository.

#### 1. Install Latest Python Version
- Download the latest Python version from [python.org](https://www.python.org/downloads/)
- **Important:** During installation, check "Add Python to PATH"
- Verify installation: `python --version`

#### 2. Virtual Environment (Already Provided)
The repository includes a pre-configured virtual environment (`.venv` folder) with all necessary dependencies already installed. No additional setup is required.

**Pre-installed Libraries in Virtual Environment:**
- `pandas` - Data manipulation and analysis
- `numpy` - Numerical computing
- `scikit-learn` - Machine learning algorithms (MLPRegressor)
- `joblib` - Model serialization
- `jupyter` - For running training notebooks (optional)

### Build

#### Method 1: NetBeans 26 (Recommended)
**Development Environment:** NetBeans 26 + JDK 24+

**Simplified Procedure:**
1. **Open the project in NetBeans 26**
   - File → Open Project
   - Select the `JavaClientTorcs` folder
   - NetBeans will automatically recognize the Ant project

2. **Build with one click**
   - Right-click on project → Clean and Build

3. **Verify build**
   - The JAR will be generated in: `JavaClientTorcs/dist/JavaClientTorcs.jar`
   - **No additional configuration required**

#### Method 2: Ant from terminal

#### Windows
```cmd
cd JavaClientTorcs
ant clean
ant jar
```

[⬆️ Back to Table of Contents](#-table-of-contents)

## 🎮 How to Use the System

### Method 1: Interactive Menu (Recommended)

The project includes a comprehensive menu system with all functionalities:

#### Windows
```cmd
torcs_menu.bat
```

### Menu Options

**Data Collection:**
- Option 1: Manual driving (human data collection)

**Dataset Management:**
- Option 2: View dataset statistics
- Option 3: Convert complete dataset to ML sensors dataset
- Option 4: Open Dataset Analysis Tools (Google Colab)

**Autonomous Driving & Artificial Intelligence:**
- Option 5: **SimpleDriver** (traditional rule-based autonomous)
- Option 6: **KNN Driving** (Continuous Values)
- Option 7: **KNN Classifier** (Discrete Actions)
- Option 8: **MLP Driving** (Neural Network) - *Requires Python setup*

**Documentation:**
- Option 9: Open complete guide

**TORCS Game:**
- Option 0: Start TORCS Game

### Method 2: Individual Scripts

#### Manual Data Collection
- `JavaClientTorcs/scripts/run_manual_driving.bat`

#### Traditional Autonomous Driving
- **SimpleDriver**: `JavaClientTorcs/scripts/run_simpledriver.bat`

#### Machine Learning Autonomous Driving
- **KNN Standard**: `JavaClientTorcs/scripts/run_knn_driving.bat`
- **KNN Human dataset**: `JavaClientTorcs/scripts/run_knn_driving_human.bat`
- **KNN Classifier**: `JavaClientTorcs/scripts/run_knn_classifier.bat`
- **MLP Neural Network**: `JavaClientTorcs/scripts/run_mlp_driving_human.bat`



[⬆️ Back to Table of Contents](#-table-of-contents)

## 📁 Project Structure

```
Project/
├── .gitignore              # Git ignore file
├── .venv/                  # Python virtual environment
├── .vscode/                # VS Code configuration
│   ├── extensions.json
│   ├── java.json
│   ├── launch.json
│   ├── settings.json
│   └── tasks.json
├── README.md               # This documentation
├── README.it.md            # Italian documentation
├── torcs_menu.bat          # Windows menu
├── JavaClientTorcs/
│   ├── build.xml           # Ant configuration
│   ├── manifest.mf         # JAR manifest
│   ├── nbproject/          # NetBeans Ant configuration
│   │   ├── build-impl.xml
│   │   ├── project.properties
│   │   └── project.xml
│   ├── src/
│   │   └── it/unisa/javaclienttorcs/
│   │       ├── Action.java                 # Control actions
│   │       ├── Client.java                 # Main client
│   │       ├── Controller.java             # Controller interface
│   │       ├── HumanController.java        # Manual controller with gamepad
│   │       ├── SimpleDriver.java           # Traditional automatic driver
│   │       ├── KNNDriver.java              # KNN driver with KD-Tree
│   │       ├── MLPDriver.java              # MLP neural network driver
│   │       ├── KNNClassifierDriver.java    # KNN driver with discrete actions
│   │       ├── DataPoint.java              # Data structure for ML
│   │       ├── KDTree.java                 # KD-Tree implementation
│   │       ├── EnhancedDataCollectionManager.java # Advanced data management
│   │       ├── MessageBasedSensorModel.java # Sensor model
│   │       ├── MessageParser.java          # Message parser
│   │       ├── SensorModel.java            # Sensor interface
│   │       └── SocketHandler.java          # Socket management
│   ├── lib/
│   │   ├── Jamepad.jar
│   │   └── sdl2gdx-1.0.5.jar
│   ├── build/              # Temporary build output
│   ├── dist/
│   │   ├── JavaClientTorcs.jar
│   │   └── lib/            # Copied libraries
│   └── scripts/                      # Execution scripts (see Usage section)

└── mlpDriver/
    ├── mlpDrive.py               # UDP server for neural network
    ├── mlpFitting.ipynb          # Jupyter notebook for training
    ├── best_mlp_model.pkl        # Trained MLP model
    ├── data.csv                  # Training data
    └── socketTest.py             # Socket communication testing
```

[⬆️ Back to Table of Contents](#-table-of-contents)

## 🕹️ Driving Controls & Controller Support

### Keyboard Controls
During manual driving, use:
- **Acceleration/Braking**: W/S | ↑↓ | 8/2 | I/K
- **Steering Left/Right**: A/D | ←→ | 4/6 | J/L
- **Manual gear shifting**: Q/E (only when manual mode active)
- **Toggle automatic transmission**: G
- **Toggle ABS**: V
- **Toggle steering assist**: B
- **Toggle automatic clutch**: N
- **Toggle data collection**: C (ON/OFF)
- **Reset controls**: R
- **Exit**: X

### Gamepad Controller Support
The system supports **XInput compatible controllers** (Xbox controllers and compatible gamepads) via **Jamepad library**.

#### Controller Mapping
- **Left Stick** - Steering (left/right)
- **Right Trigger** - Acceleration
- **Left Trigger** - Braking
- **Left Bumper (LB)** - Gear down (manual mode only)
- **Right Bumper (RB)** - Gear up (manual mode only)
- **A Button** - Toggle automatic clutch
- **B Button** - Toggle ABS
- **Y Button** - Toggle steering assist
- **Start** - Toggle data collection ON/OFF
- **Back** - Toggle automatic transmission

#### Controller Setup Requirements
- **Jamepad library** (included in `lib/Jamepad.jar`)
- **SDL2** (included via `lib/sdl2gdx-1.0.5.jar`)
- **XInput compatible controller** (Xbox 360, Xbox One, or compatible)

#### Troubleshooting Controller Issues
1. **Controller not detected**: Ensure controller is connected before starting the application
2. **No response**: Try restarting with controller already connected
3. **Wrong mappings**: Use keyboard controls as fallback


### Control Priority
The system automatically prioritizes:
1. **Gamepad controller** (if connected and detected)
2. **Keyboard controls** (as fallback)

### Data Collection Notes
- **All control inputs** (keyboard and controller) are recorded in datasets
- **Controller inputs** provide smoother steering for better training data
- **Mixed input methods** are supported during collection

[⬆️ Back to Table of Contents](#-table-of-contents)

## 🤖 KNN (K-Nearest Neighbors) System

The project implements an advanced **K-Nearest Neighbors** algorithm for autonomous driving using machine learning, with two distinct implementations:

### 🚗 KNN Driving (Continuous Control)
Traditional KNN implementation for smooth autonomous driving with continuous steering, acceleration, and braking values.

### 🎯 KNN Classifier (Discrete Actions)
Advanced KNN implementation that uses discrete action classification for more precise and predictable driving behavior.

### KNN Features

**Algorithm Implementation:**
- **KD-Tree** data structure for efficient nearest neighbor search
- **Euclidean distance** as distance metric
- **Data normalization** for improved accuracy
- **Weighted voting** system for predictions
- **Configurable parameters** (K value, normalization, etc.)

**Available Configurations:**
- **KNN Continuous**: Trained on driving data (K=8, Euclidean distance, weighted voting)
- **KNN Classifier**: Trained on discretized data (K=15, Euclidean distance, action classification)

### KNN Usage

**KNN Driving (Continuous Control):**
```cmd
# Use menu option 6 or run directly:
JavaClientTorcs/scripts/run_knn_driving_human.bat
```

**KNN Classifier (Discrete Actions):**
```cmd
# Use menu option 7 or run directly:
JavaClientTorcs/scripts/run_knn_classifier.bat
```



### KNN Technical Details

**Input Features:**
- Track sensors (10 selected distance sensors)
- Speed (speedX)
- Angle to track axis (angleToTrackAxis)
- Track position (trackPosition)
- Distance from start line (distanceFromStartLine)
- **Total: 14 normalized features**

**Output Actions:**
- Steering angle (-1.0 to 1.0)
- Acceleration (0.0 to 1.0)
- Brake (0.0 to 1.0)
- Gear selection

**Performance:**
- Real-time prediction (< 10ms per decision)
- **Weighted voting** based on inverse Euclidean distance
- **Automatic data normalization** for improved accuracy
- **Specialized handling** for off-track situations
- **Classifier mode** with 15 neighbors for enhanced stability

[⬆️ Back to Table of Contents](#-table-of-contents)

## 📊 Dataset Analysis Tools (Google Colab)

The project includes **Google Colab integration** for advanced dataset analysis and visualization.

### Google Colab Features

**Analysis Tools:**
- **Dataset normalization and balancing** for improved ML performance
- **Feature analysis and visualization** with interactive charts
- **Data quality assessment** and outlier detection
- **Statistical insights** and correlation analysis
- **Data preprocessing** for machine learning models

**Access:**
- **Menu Option 4**: Opens Google Colab notebook automatically
- **Direct Link**: [Dataset Analysis Notebook](https://colab.research.google.com/drive/1k-cV_NJBRxCdNuzrNbqFrxhazVwFKo3e?usp=sharing)
- **Browser Integration**: Automatic opening in default browser

**Usage:**
1. Select **Option 4** from the main menu
2. Upload your dataset files (`human_dataset.csv`, `enhanced_dataset.csv`)
3. Run the analysis cells to generate insights
4. Download processed datasets for improved ML training

**Benefits:**
- **Cloud-based processing** - No local Python setup required
- **Interactive visualizations** for data exploration
- **Professional data science tools** (Pandas, NumPy, Matplotlib)
- **Collaborative analysis** - Share notebooks with team members

[⬆️ Back to Table of Contents](#-table-of-contents)

## 🧠 MLP (Multi-Layer Perceptron) Neural Network System

The project includes an advanced **Neural Network** implementation using **Multi-Layer Perceptron** for autonomous driving with deep learning capabilities.

### MLP Features

**Neural Network Architecture:**
- **Multi-Layer Perceptron** with configurable hidden layers
- **Scikit-learn MLPRegressor** implementation
- **Data preprocessing** with scaling and normalization
- **Grid search optimization** for hyperparameter tuning
- **Real-time prediction** via UDP communication

**Training Process:**
- **Jupyter Notebook** training environment (`mlpFitting.ipynb`)
- **Cross-validation** for model evaluation
- **Pipeline architecture** with preprocessing and model
- **Model serialization** using joblib
- **Performance metrics** (MSE, R²)

### MLP Usage

**Autonomous Driving:**
```cmd
# Use menu option 6 - automatically starts Python server:
torcs_menu.bat
# Select option 6: MLP Driving (Neural Network)
```

**Manual Server Start:**
```cmd
# Start Python MLP server manually:
python mlpDriver\mlpDrive.py

# Then run Java client:
JavaClientTorcs\scripts\run_mlp_driving_human.bat
```

**Training New Models:**
```cmd
# Open Jupyter notebook for training:
jupyter notebook mlpDriver\mlpFitting.ipynb
```

### MLP Technical Details

**Architecture:**
- **Input Layer**: 14 features (track sensors + vehicle state)
- **Hidden Layers**: Configurable (default: 2 layers, 100 neurons each)
- **Output Layer**: 3 actions (steering, acceleration, brake)
- **Activation**: ReLU for hidden layers, linear for output

**Input Features:**
- Track sensors: `track0, track2, track4, track6, track8, track10, track12, track14, track16, track18`
- Vehicle state: `speedX, angleToTrackAxis, trackPosition, distanceFromStartLine`

**Output Actions:**
- Steering angle (-1.0 to 1.0)
- Acceleration (0.0 to 1.0)
- Brake (0.0 to 1.0)

**Communication:**
- **UDP Server**: Python server on `localhost:35567`
- **Java Client**: Sends sensor data, receives predictions
- **Protocol**: Comma-separated values
- **Graceful shutdown**: Handles termination commands

**Dependencies:**
- `pandas` - Data manipulation
- `numpy` - Numerical operations
- `scikit-learn` - Neural network implementation
- `joblib` - Model serialization

[⬆️ Back to Table of Contents](#-table-of-contents)

## 📊 Datasets & Car Configuration

**Car Configuration**: The project is configured to work with the standard **car1-trb1 2001 Ferrari 360 GT**. The complete list of available TORCS cars can be found at: https://www.igcd.net/vehicle.php?id=15647

**Changing the Car**: To use a different car, modify the `car name` field in the scr_server configuration file:
- `C:\Program Files (x86)\torcs\drivers\scr_server\scr_server.xml`

Example configuration:
```xml
<attstr name="car name" val="car1-trb1"></attstr>
```

**Additional Car-Specific Settings**: You may need to adjust gear shifting parameters based on the selected car's specifications.

**Gear Shifting Configuration**: Modify gear shifting parameters in the Java driver files:

**For SimpleDriver (AI driving)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/SimpleDriver.java`
- **Lines 20-22**: Edit the gear shifting constants
  ```java
  // RPM for upshift [for gears 1-6]
  final int[] gearUp = { 4500, 5500, 6500, 6500, 7000, 0 };
  // RPM for downshift [for gears 1-6]
  final int[] gearDown = { 0, 2500, 3500, 4000, 4500, 5000 };
  ```

**For HumanController (manual driving)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/HumanController.java`
- **Lines 37-39**: Edit the gear shifting constants
  ```java
  // RPM for upshift [for gears 1-6]
  private static final int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
  // RPM for downshift [for gears 1-6]
  private static final int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };
  ```

**Adjust these values based on your car's specifications**:
- Lower `gearUp` values for cars with lower redline RPM
- Adjust `gearDown` values for optimal engine performance
- Modify gear logic if your car has different number of gears

Datasets are automatically created in their respective directories:
- `JavaClientTorcs/human_dataset.csv` - Manually collected data (HumanController) - Contains essential sensors for analysis
- `JavaClientTorcs/enhanced_dataset.csv` - Comprehensive dataset with almost all available TORCS sensors - Ideal for future implementations and data analysis
- `mlpDriver/data.csv` - Dataset used for MLP neural network training

**Dataset Conversion (Option 3)**: Menu option 3 converts the complete dataset `enhanced_dataset.csv` (containing all TORCS sensors) into an optimized dataset `human_dataset.csv` containing only the specific sensors used by machine learning models (10 alternating track sensors, speed, position, control actions).

[⬆️ Back to Table of Contents](#-table-of-contents)

## 🔧 Technology and Architecture

### Technology Stack
- **Language**: Java 21+
- **Machine Learning**: 
  - **KNN with KD-Tree** optimization
  - **MLP Neural Networks** with Python 3.13+ integration
  - **Traditional rule-based** algorithms
- **Controller Libraries**: Jamepad (gamepad), SDL2 (input handling)
- **IDE**: NetBeans 26 (native project)
- **Build Tool**: Apache Ant
- **Data Format**: CSV
- **Communication**: 
  - **UDP Socket** with TORCS (port 3001)
  - **Python-Java UDP** communication (port 3002)
- **Python Libraries**: pandas, numpy, scikit-learn, joblib

### System Architecture
- **Modular Controllers**: 
  - **HumanController**: Manual driving with intelligent data collection
  - **SimpleDriver**: Traditional rule-based autonomous driving
  - **KNNDriver**: Continuous control via K-Nearest Neighbors
  - **KNNClassifierDriver**: Discrete action classification
  - **MLPDriver**: Neural network-based driving with Python integration
- **Machine Learning Algorithms**:
  - **KNN**: Optimized with KD-Tree for fast neighbor search
  - **MLP**: Multi-layer perceptron with Python-Java communication
  - **Traditional**: Rule-based algorithms for baseline performance
- **Intelligent Data Collection**: 
  - Real-time sensor data capture and preprocessing
  - Automatic CSV file generation with enhanced features
  - Multi-format dataset support (human, auto, enhanced)
- **TORCS Communication**: 
  - **Primary UDP socket** (port 3001) - Java client communication
  - **Secondary UDP socket** (port 3002) - Python MLP communication
  - Real-time sensor data reception and action transmission
- **Sensor Model**: 
  - **19 distance sensors** for obstacle detection (10 selected for KNN algorithms)
  - **Vehicle dynamics**: Speed, angle, track position
  - **Engine data**: Gear, RPM, damage information
  - **Enhanced features**: Normalized data, weighted inputs

### NetBeans Compatibility
- **Tested with**: NetBeans 26 + JDK 24 ✅
- **Native project**: Open the `JavaClientTorcs` folder directly as Ant project

[⬆️ Back to Table of Contents](#-table-of-contents)

## ⚡ Quick Start Guide

1. **Setup TORCS**: Install TORCS 1.3.7 with SCR patch (see configuration section)
2. **Build Project**: `ant clean && ant jar` (from JavaClientTorcs/) or use NetBeans
3. **Launch Menu**: Run `torcs_menu.bat` for interactive options
4. **Data Collection**: Choose option 1 for manual driving data collection
5. **Choose your autonomous driving option**:
   - **Option 5**: SimpleDriver (traditional rule-based)
   - **Option 6**: KNN Driving (Human Dataset)
   - **Option 7**: KNN Classifier (Discrete Actions)
   - **Option 8**: MLP Neural Network (requires Python setup)

[⬆️ Back to Table of Contents](#-table-of-contents)

## 📋 Important Notes

- ⚠️ **TORCS must be running** before starting any driver
- 📁 **Datasets are saved** in the directory where you run scripts
- 📊 **For best results**, collect at least 1000-5000 examples per track
- 🪟 **Optimized for Windows** with specific TORCS configuration
- 🏎️ **car1-trb1 2001 Ferrari 360 GT** - configured for this standard car

[⬆️ Back to Table of Contents](#-table-of-contents)

## 🪟 TORCS Configuration for Windows

### ⚠️ Required Version: 1.3.7
**Important Note**: The MIVIA 2025 project specifically requires **version 1.3.7**, not the more recent 1.3.8.

### Complete TORCS + SCR Patch Installation

To properly configure TORCS on Windows for the MIVIA 2025 project:

#### Step 1: Install TORCS 1.3.7
1. **Download exact version 1.3.7**:
   - [Download torcs_1.3.7_setup.exe](https://sourceforge.net/projects/torcs/files/torcs-win32-bin/1.3.7/torcs_1.3.7_setup.exe/download)
   - Save the file in Downloads folder
   - **⚠️ Version Guarantee**: This link provides **exactly version 1.3.7** required by the project

2. **Install TORCS**:
   - Run `torcs_1.3.7_setup.exe` as Administrator
   - Follow the installation wizard
   - Install in default directory: `C:\Program Files\TORCS`
   - **Verify version**: Go to `Help > About TORCS` to confirm 1.3.7

#### Step 2: Apply SCR Patch
1. **Download SCR package**:
   - Visit: [Computational Intelligence in Games](http://sourceforge.net/projects/cig/)
   - Download: `scr-win-patch.zip`

2. **Apply patch**:
   - Go to TORCS installation directory (e.g. `C:\Program Files\TORCS`)
   - Extract `scr-win-patch.zip` directly into this directory
   - **IMPORTANT**: When prompted, select **"Yes to all"** to overwrite existing files
   - Verify all files were extracted correctly

#### Step 3: Verify Installation
1. **Start TORCS**:
   - Run `C:\Program Files\TORCS\torcs.exe`
   - Verify SCR additional features appear

2. **Test connection**:
   - Start TORCS before running Java drivers
   - Java client will automatically connect on port 3001

[⬆️ Back to Table of Contents](#-table-of-contents)

---

## 🎯 Conclusion

This TORCS system represents a comprehensive solution for:
- **🤖 Autonomous driving research** with multiple AI technologies:
  - Traditional algorithms (SimpleDriver)
  - KNN Machine Learning with KD-Tree
  - MLP Neural Networks with Python
  - Discrete action classification
- **📊 Data collection and analysis** for machine learning applications
- **🎮 Interactive driving simulation** with multiple control methods
- **🔬 Educational purposes** in AI and automotive engineering

### 🚀 Future Developments
- Implementation of additional ML algorithms (Reinforcement Learning, Deep Learning)
- Multi-track optimization
- Advanced sensor fusion techniques
- Real-time performance analytics
- Integration of additional sensors and sensor fusion
- Automatic hyperparameter optimization for MLP
- Graphical interface for real-time data visualization
- Optimizations for multi-agent competitions

### 📞 Support
For technical support or questions:
- Check the [TORCS Configuration](#-torcs-configuration-for-windows) section
- Review [Important Notes](#-important-notes) for common issues
- Ensure all prerequisites are correctly installed

---

**🏁 Ready to race with AI? Start your engines and let the algorithms drive!**

[⬆️ Back to Table of Contents](#-table-of-contents)