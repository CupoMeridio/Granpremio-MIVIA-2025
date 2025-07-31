#!/bin/bash

echo "=============================="
echo "  TEST MODEL - Human Data"
echo "=============================="
echo "Testing behavioral cloning with manually collected data"
echo "Dataset: human_dataset.csv"
echo "=============================="

# Cleanup function to kill any remaining Java processes
cleanup() {
    echo "Cleaning up..."
    pkill -f "java.*BehavioralCloningDriver" 2>/dev/null || true
    sleep 1
    exit 0
}

# Set trap for script termination
trap cleanup EXIT SIGINT SIGTERM

cd "$(dirname "$0")/.."

if [ ! -f "human_dataset.csv" ]; then
    echo
    echo "⚠️  WARNING: human_dataset.csv not found!"
    echo
    echo "To test the model with human data you must first:"
    echo "1. Run './run_manual_driving.sh' to collect manual data"
    echo "2. Or run the menu script and select option 1"
    echo
    read -p "Press Enter to continue..."
    exit 1
fi

echo "Press Ctrl+C to interrupt test..."

java -cp "dist/JavaClientTorcs.jar:lib/*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.BehavioralCloningDriver human_dataset.csv
echo
echo "Test completed!"