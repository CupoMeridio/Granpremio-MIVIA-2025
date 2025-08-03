# TORCS Behavioral Cloning Driver

[![Language: English](https://img.shields.io/badge/lang-en-green.svg)](README.md) [![Language: Italian](https://img.shields.io/badge/lang-it-blue.svg)](README.it.md)

A behavioral cloning system for TORCS (The Open Racing Car Simulator) that uses K-NN to imitate human driving behavior based on collected driving data.

## 🎯 What It Is

This project implements an autonomous driver for TORCS that:
- **Learns from human behavior** through data collection
- **Uses K-NN** to make real-time driving decisions
- **Supports data collection** both manual and automatic
- **Is cross-platform** (Windows, Linux, macOS)

## 🚀 Installation

### Prerequisites
- **Java 21+** installed and in PATH ([Download JDK](https://www.oracle.com/it/java/technologies/downloads))
- **Apache Ant** for project building ([Download Apache Ant](https://ant.apache.org/bindownload.cgi)) (or **NetBeans 26+** for IDE build - [Download NetBeans](https://netbeans.apache.org/front/main/download/nb26))
- TORCS started with JavaClientTorcs module

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

#### Linux/Mac
```bash
cd JavaClientTorcs
ant clean
ant jar
```

## 📁 Project Structure

```
Project/
├── README.md               # This documentation
├── README.it.md            # Italian documentation
├── torcs_menu.bat          # Windows menu
├── torcs_menu.sh           # Linux/Mac menu
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
    │       ├── BehavioralCloningDriver.java # Main K-NN driver
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
        ├── run_manual_driving.bat    # Manual driving Windows
        ├── run_manual_driving.sh     # Manual driving Linux/Mac
        ├── run_auto_collection.bat   # Automatic collection Windows
        ├── run_auto_collection.sh    # Automatic collection Linux/Mac
        ├── test_human_model.bat      # Test human data Windows
        ├── test_human_model.sh       # Test human data Linux/Mac
        ├── test_auto_model.bat       # Test automatic data Windows
        ├── test_auto_model.sh        # Test automatic data Linux/Mac
        ├── combine_datasets.bat      # Combine datasets Windows
        └── combine_datasets.sh       # Combine datasets Linux/Mac
```

## 🎮 How to Use

### Method 1: Interactive Menu (Recommended)

#### Windows
```cmd
torcs_menu.bat
```

#### Linux/Mac
```bash
./torcs_menu.sh
```

### Method 2: Individual Scripts

#### Manual Data Collection
- **Windows**: `JavaClientTorcs/scripts/run_manual_driving.bat`
- **Linux/Mac**: `./JavaClientTorcs/scripts/run_manual_driving.sh`

#### Automatic Data Collection
- **Windows**: `JavaClientTorcs/scripts/run_auto_collection.bat`
- **Linux/Mac**: `./JavaClientTorcs/scripts/run_auto_collection.sh`

#### Model Testing
- **Test human data**: 
  - Windows: `JavaClientTorcs/scripts/test_human_model.bat`
  - Linux/Mac: `./JavaClientTorcs/scripts/test_human_model.sh`
- **Test automatic data**:
  - Windows: `JavaClientTorcs/scripts/test_auto_model.bat`
  - Linux/Mac: `./JavaClientTorcs/scripts/test_auto_model.sh`

#### Dataset Management
- **Combine datasets**:
  - Windows: `JavaClientTorcs/scripts/combine_datasets.bat`
  - Linux/Mac: `./JavaClientTorcs/scripts/combine_datasets.sh`

## 🕹️ Driving Controls

During manual driving, use:
- **Arrow keys/WASD/IJKL/8426** - Directional controls
- **C** - Toggle data collection ON/OFF
- **P** - Show statistics
- **R** - Reset position
- **Q** - Exit

## 📊 Datasets

Datasets are automatically created in the main directory:
- `dataset.csv` - Automatically collected data
- `human_dataset.csv` - Manually collected data
- `combined_dataset.csv` - Combined datasets

## 🔧 Technology

- **Language**: Java
- **IDE**: NetBeans 26 (native project)
- **Build Tool**: Apache Ant
- **Algorithm**: K-Nearest Neighbors (K-NN)
- **Communication**: UDP Socket with TORCS
- **Data Format**: CSV

### NetBeans Compatibility
- **Tested with**: NetBeans 26 + JDK 21/24 ✅
- **Compatible with**: NetBeans 25+ and JDK 21+
- **Native project**: Open the `JavaClientTorcs` folder directly as Ant project

## 🚗 TORCS Setup

1. Start TORCS
2. Configure race with Java client
3. The driver will automatically connect to port 3001

## ⚡ Quick Start

1. **Build**: `ant clean && ant jar` (from JavaClientTorcs/)
2. **Menu**: Use appropriate menu for your system
3. **Collect data**: Drive manually or automatically
4. **Test**: Use model with your data

## 📋 Important Notes

- Make sure TORCS is running before starting drivers
- Datasets are saved in the directory where you run scripts
- For best results, collect at least 1000-5000 examples per track
- The system is cross-platform: works on Windows, Linux and macOS

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

## 🐧 TORCS Configuration for Linux

### ⚠️ Required Version: 1.3.7
**Important Note**: The MIVIA 2025 project specifically requires **version 1.3.7**, not the more recent 1.3.8.

### TORCS 1.3.7 + SCR Patch Installation

To configure TORCS on Linux, use **one of the following options to get exactly 1.3.7**:

#### Option 1: Package Manager Installation (Recommended - Version 1.3.7)

**Ubuntu/Debian (confirmed 1.3.7 version):**
```bash
# Install TORCS 1.3.7 from repositories (current version: 1.3.7+dfsg-5)
sudo apt update
sudo apt install torcs=1.3.7+dfsg-5

# Verify installed version
torcs --version

# Install additional dependencies for SCR
sudo apt install libalut-dev libvorbis-dev libpng-dev
```

**Debian:**
```bash
# Debian stable/bookworm includes 1.3.7
sudo apt update
sudo apt install torcs
```

**Fedora:**
```bash
# Fedora includes 1.3.7 in repositories
sudo dnf install torcs-1.3.7
```

**⚠️ Attention**: Some distributions might have updated to 1.3.8. In this case, use option 2 or 3.

#### Option 2: Source Installation with SCR Patch

**Complete Ubuntu/Debian:**
```bash
# Install all necessary dependencies
sudo apt-get install libglib2.0-dev libgl1-mesa-dev libglu1-mesa-dev freeglut3-dev libplib-dev libopenal-dev libalut-dev libxi-dev libxmu-dev libxrender-dev libxrandr-dev libpng-dev libvorbis-dev cmake build-essential git

# Download and compile TORCS 1.3.7 with SCR patch
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

#### Option 3: Pre-patched GitHub Repository (Recommended for Linux)

**GitHub repository with TORCS 1.3.7 + SCR already patched:**
```bash
# Clone repository with TORCS 1.3.7 pre-patched
# Includes dependencies and SCR patch already applied
git clone https://github.com/fmirus/torcs-1.3.7.git
cd torcs-1.3.7

# Install dependencies
sudo apt-get install libglib2.0-dev libgl1-mesa-dev libglu1-mesa-dev freeglut3-dev libplib-dev libopenal-dev libalut-dev libxi-dev libxmu-dev libxrender-dev libxrandr-dev libpng-dev libvorbis-dev cmake build-essential

# Compile and install
export CFLAGS="-fPIC"
export CPPFLAGS=$CFLAGS
export CXXFLAGS=$CFLAGS
./configure --prefix=$(pwd)/BUILD
make -j$(nproc)
make install
make datainstall
```

### Next Steps for Linux
After configuration:
1. Test installation with `torcs_menu.sh`
2. Verify client-server connection on port 3001
3. Proceed with data collection using `.sh` scripts