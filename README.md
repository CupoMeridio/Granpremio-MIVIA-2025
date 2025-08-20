# 🏎️ TORCS Data Collection and Autonomous Driving System

[![Language: English](https://img.shields.io/badge/lang-en-green.svg)](README.md) [![Language: Italian](https://img.shields.io/badge/lang-it-blue.svg)](README.it.md)

A comprehensive system for TORCS (The Open Racing Car Simulator) that combines intelligent data collection, autonomous driving via KNN machine learning, and manual control with keyboard and gamepad support.

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
- 🧠 **Autonomous driving via KNN** with machine learning algorithms
- 🎮 **Manual driving** with keyboard and gamepad support
- 📊 **Intelligent data collection** for training and analysis
- 🔧 **Modular system** with multiple controllers and configurations
- 🪟 **Windows compatibility** with optimized setup

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
**Development Environment:** NetBeans 26 + JDK 21+

**Simplified Procedure:**
1. **Open the project in NetBeans 26**
   - File → Open Project
   - Select the `JavaClientTorcs` folder
   - NetBeans will automatically recognize the Ant project

2. **Build with one click**
   - **Green "Run Project" button** (▶️) in the toolbar
   - **Or:** Right-click on project → Clean and Build

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
- Option 3: Convert enhanced to human dataset

**Autonomous Driving:**
- Option 4: SimpleDriver (basic autonomous)

**Artificial Intelligence:**
- Option 5: **KNN Driving** (Human Dataset)
- Option 6: **MLP Driving** (Neural Network) - *Requires Python setup*

**Documentation:**
- Option 7: Open complete guide

**TORCS Game:**
- Option 0: Start TORCS Game

### Method 2: Individual Scripts

#### Manual Data Collection
- `JavaClientTorcs/scripts/run_manual_driving.bat`

#### KNN Driving
- **Human dataset**: `JavaClientTorcs/scripts/run_knn_driving_human.bat`

#### Simple Driver
- `JavaClientTorcs/scripts/run_simpledriver.bat`

#### Dataset Management
- **Combine datasets**: `JavaClientTorcs/scripts/combine_datasets.bat`
- **Test KNN**: `JavaClientTorcs/scripts/test_knn.bat`

## 📁 Project Structure

```
Project/
├── README.md               # This documentation
├── README.it.md            # Italian documentation
├── torcs_menu.bat          # Windows menu
└── JavaClientTorcs/
    ├── build.xml           # Ant configuration
    ├── manifest.mf         # JAR manifest
    ├── nbproject/          # NetBeans Ant configuration
    │   ├── build-impl.xml
    │   ├── project.properties
    │   └── project.xml
    ├── src/
    │   └── it/unisa/javaclienttorcs/
    │       ├── Action.java                 # Control actions
    │       ├── DataCollector.java          # Data collection
    │       ├── Client.java                 # Main client
    │       ├── Controller.java             # Controller interface
    │       ├── DataCollector.java          # Data collection
    │       ├── DeadSimpleSoloController.java # Base controller
    │       ├── HumanController.java        # Manual controller
    │       ├── MessageBasedSensorModel.java # Sensor model
    │       ├── MessageParser.java          # Message parser
    │       ├── SensorModel.java            # Sensor interface
    │       ├── SimpleDriver.java           # Automatic driver
    │       └── SocketHandler.java        # Socket management
    ├── lib/
    │   ├── Jamepad.jar
    │   └── sdl2gdx-1.0.5.jar
    ├── build/              # Temporary build output
    ├── dist/
    │   ├── JavaClientTorcs.jar
    │   └── lib/            # Copied libraries
    └── scripts/
        ├── run_manual_driving.bat    # Manual driving
        ├── run_knn_driving_human.bat # KNN with human data
        ├── run_knn_driving_auto.bat  # KNN with auto data
        ├── run_simpledriver.bat      # Simple driver
        ├── combine_datasets.bat      # Combine datasets
        ├── test_knn.bat              # Test KNN
        ├── test_auto_model.sh        # Test automatic data Linux/Mac
        ├── combine_datasets.bat      # Combine datasets Windows
        └── combine_datasets.sh       # Combine datasets Linux/Mac
```

## 🕹️ Driving Controls & Controller Support

### Keyboard Controls
During manual driving, use:
- **Arrow keys/WASD/IJKL/8426** - Directional controls
- **C** - Toggle data collection ON/OFF
- **P** - Show statistics
- **R** - Reset position
- **Q** - Exit

### Gamepad Controller Support
The system supports **XInput compatible controllers** (Xbox controllers and compatible gamepads) via **Jamepad library**.

#### Controller Mapping
- **Left Stick** - Steering (left/right)
- **Right Trigger** - Acceleration
- **Left Trigger** - Braking
- **A Button** - Toggle data collection ON/OFF
- **B Button** - Reset position
- **Y Button** - Show statistics
- **Start/Back** - Exit

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

## 🤖 KNN (K-Nearest Neighbors) System

The project implements an advanced **K-Nearest Neighbors** algorithm for autonomous driving using machine learning.

### KNN Features

**Algorithm Implementation:**
- **KD-Tree** data structure for efficient nearest neighbor search
- **Euclidean distance** as distance metric
- **Data normalization** for improved accuracy
- **Weighted voting** system for predictions
- **Configurable parameters** (K value, normalization, etc.)

**Available Configurations:**
- **Auto-Optimized**: Trained on existing automatic data (K=5, Euclidean distance)
- **Human-Optimized**: Trained on human driving data (K=5, Euclidean distance)

### KNN Usage

**Testing the System:**
```cmd
# Use menu option 6 or run directly:
JavaClientTorcs/scripts/test_knn.bat
```

**Autonomous Driving:**
```cmd
# Use menu option 7 for human dataset:
JavaClientTorcs/scripts/run_knn_driving_human.bat


```

**Configuration Comparison:**
```cmd
# Use menu option 8 for detailed comparison
```

### KNN Technical Details

**Input Features:**
- Track sensors (19 distance sensors)
- Speed, RPM, gear information
- Track position and angle
- Opponent positions (if available)

**Output Actions:**
- Steering angle (-1.0 to 1.0)
- Acceleration (0.0 to 1.0)
- Brake (0.0 to 1.0)
- Gear selection

**Performance:**
- Real-time prediction (< 10ms per decision)
- Adaptive learning from training data
- Robust handling of edge cases

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

**Performance:**
- **Training dataset**: 55,307 samples
- **Real-time inference**: < 5ms per prediction
- **Model size**: Optimized for fast loading
- **Error handling**: Robust parsing and validation

**Dependencies:**
- `pandas` - Data manipulation
- `numpy` - Numerical operations
- `scikit-learn` - Neural network implementation
- `joblib` - Model serialization

## 📊 Datasets & Car Configuration

**Car Configuration**: All datasets and project configurations are specifically optimized for the **Ferrari F2001** (car1-ow1). The complete list of available TORCS cars can be found at: https://www.igcd.net/vehicle.php?id=15647

**Changing the Car**: To use a different car, modify the `car name` field in the scr_server configuration file:
- `C:\Program Files (x86)\torcs\drivers\scr_server\scr_server.xml`

Example configuration:
```xml
<attstr name="car name" val="car1-ow1"></attstr>
```

**Additional Car-Specific Settings**: You may need to adjust gear shifting parameters based on the selected car's specifications.

**Gear Shifting Configuration**: Modify gear shifting parameters in the Java driver files:

**For SimpleDriver (AI driving)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/SimpleDriver.java`
- **Lines 30-35**: Edit the gear shifting constants
  ```java
  /* === COSTANTI PER IL CAMBIO MARCIA === */
  // RPM minimi per salire di marcia [per marcia 1-6]
  final int[] gearUp = { 19000, 19000, 19000, 19000, 19000, 0 };
  // RPM massimi per scalare di marcia [per marcia 1-6]
  final int[] gearDown = { 0, 7000, 7000, 7000, 7000, 7000 };
  ```

**For HumanController (manual driving)**:
- **File**: `JavaClientTorcs/src/it/unisa/javaclienttorcs/HumanController.java`
- **Lines 197-244**: Modify the gear shifting logic in `getGear()` method
  - **Line 220**: `if (currentGear > 0 && currentGear < 6 && rpm >= 19000)` - Change `19000` for upshift RPM
  - **Line 224**: `else if (currentGear > 1 && rpm <= 7000)` - Change `7000` for downshift RPM

**Adjust these values based on your car's specifications**:
- Lower `gearUp` values for cars with lower redline RPM
- Adjust `gearDown` values for optimal engine performance
- Modify gear logic if your car has different number of gears

Datasets are automatically created in the main directory:
- `auto_dataset.csv` - Previously collected automatic data (if available) - Contains essential sensors for analysis
- `human_dataset.csv` - Manually collected data (HumanController) - Contains essential sensors for analysis
- `enhanced_dataset.csv` - Comprehensive dataset with almost all available TORCS sensors - Ideal for future implementations and data analysis


## 🔧 Technology and Architecture

### Technology Stack
- **Language**: Java 21+
- **Machine Learning**: KNN with KD-Tree optimization
- **Controller Libraries**: Jamepad (gamepad), SDL2 (input handling)
- **IDE**: NetBeans 26 (native project)
- **Build Tool**: Apache Ant
- **Data Format**: CSV
- **Communication**: UDP Socket with TORCS

### System Architecture
- **Modular Controllers**: HumanController, SimpleDriver, KNNDriver
- **Intelligent Data Collection**: Automatic sensor data capture
- **TORCS Communication**: Real-time UDP protocol
- **Sensor Model**: Comprehensive track and vehicle sensors

### NetBeans Compatibility
- **Tested with**: NetBeans 26 + JDK 21/24 ✅
- **Compatible with**: NetBeans 25+ and JDK 21+
- **Native project**: Open the `JavaClientTorcs` folder directly as Ant project

## ⚡ Quick Start Guide

1. **Setup TORCS**: Install TORCS 1.3.7 with SCR patch (see configuration section)
2. **Build Project**: `ant clean && ant jar` (from JavaClientTorcs/) or use NetBeans
3. **Launch Menu**: Run `torcs_menu.bat` for interactive options
4. **Data Collection**: Choose option 1 for manual driving data collection
5. **Test KNN**: Choose option 6 to test autonomous driving

## 📋 Important Notes

- ⚠️ **TORCS must be running** before starting any driver
- 📁 **Datasets are saved** in the directory where you run scripts
- 📊 **For best results**, collect at least 1000-5000 examples per track
- 🪟 **Optimized for Windows** with specific TORCS configuration
- 🏎️ **Ferrari F2001 optimized** - all configurations tuned for this car

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

---

## 🎯 Conclusion

This TORCS system represents a comprehensive solution for:
- **🤖 Autonomous driving research** with advanced KNN algorithms
- **📊 Data collection and analysis** for machine learning applications
- **🎮 Interactive driving simulation** with multiple control methods
- **🔬 Educational purposes** in AI and automotive engineering

### 🚀 Future Developments
- Enhanced neural network implementations
- Multi-track optimization
- Advanced sensor fusion techniques
- Real-time performance analytics

### 📞 Support
For technical support or questions:
- Check the [TORCS Configuration](#-torcs-configuration-for-windows) section
- Review [Important Notes](#-important-notes) for common issues
- Ensure all prerequisites are correctly installed

---

**🏁 Ready to race with AI? Start your engines and let the algorithms drive!**