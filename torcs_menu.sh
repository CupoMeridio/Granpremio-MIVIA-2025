#!/bin/bash

# TORCS Menu Script for Linux/Mac
show_menu() {
    clear
    echo "=========================="
    echo "   TORCS - COMPLETE MENU"
    echo "=========================="
    echo
    echo "DATA COLLECTION:"
    echo "1. Manual Driving (Data Collection)"
    echo "2. Automatic Data Collection"
    echo
    echo "DATA MANAGEMENT:"
    echo "3. Combine Datasets"
    echo "4. View Dataset Statistics"
    echo
    echo "AUTONOMOUS DRIVING:"
    echo "5. SimpleDriver (Basic Autonomous)"
    echo
    echo "DOCUMENTATION:"
    echo "8. Open Complete Guide"
    echo
    echo "TORCS GAME:"
    echo "9. Launch TORCS Game"
    echo
    echo "EXIT:"
    echo "0. Exit program"
    echo
    echo "================================================="
    echo "   NOTE: Press Ctrl+C to interrupt any operation"
    echo "================================================="
}

# Get directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCRIPTS_DIR="$SCRIPT_DIR/JavaClientTorcs/scripts"

while true; do
    show_menu
    read -p "Select option (0-9): " choice
    
    case $choice in
        1)
            "$SCRIPTS_DIR/run_manual_driving.sh"
            read -p "Press Enter to continue..."
            ;;
        2)
            "$SCRIPTS_DIR/run_auto_collection.sh"
            read -p "Press Enter to continue..."
            ;;
        3)
            "$SCRIPTS_DIR/combine_datasets.sh"
            read -p "Press Enter to continue..."
            ;;
        4)
            cd "$SCRIPT_DIR/JavaClientTorcs"
            echo
            echo "=== DATASET STATISTICS ==="
            if [ -f "auto_dataset.csv" ]; then
                echo "auto_dataset.csv: $(wc -l < auto_dataset.csv) rows"
            else
                echo "auto_dataset.csv: Not found"
            fi
            if [ -f "human_dataset.csv" ]; then
                echo "human_dataset.csv: $(wc -l < human_dataset.csv) rows"
            else
                echo "human_dataset.csv: Not found"
            fi
            if [ -f "combined_dataset.csv" ]; then
                echo "combined_dataset.csv: $(wc -l < combined_dataset.csv) rows"
            else
                echo "combined_dataset.csv: Not found"
            fi
            echo
            cd "$SCRIPT_DIR"
            read -p "Press Enter to return to menu..."
            ;;
        5)
            "$SCRIPTS_DIR/run_simpledriver.sh"
            read -p "Press Enter to continue..."
            ;;
        6)
            if command -v xdg-open > /dev/null; then
                xdg-open "$SCRIPT_DIR/README.md"
            elif command -v open > /dev/null; then
                open "$SCRIPT_DIR/README.md"
            else
                echo "Opening README.md with default text editor..."
                "$SCRIPT_DIR/README.md" &
            fi
            ;;
        7)
            echo
            echo "Launching TORCS - The Open Racing Car Simulator..."
            echo "Note: Ensure TORCS is installed and in PATH or provide full path"
            if command -v torcs > /dev/null; then
                torcs &
            else
                echo "Please launch TORCS manually from your applications menu"
                read -p "Press Enter when TORCS is running..."
            fi
            ;;
        0)
            echo "Goodbye!"
            sleep 2
            exit 0
            ;;
        *)
            echo "Invalid choice! Press any key to continue..."
            read -p "Press Enter to continue..."
            ;;
    esac
done