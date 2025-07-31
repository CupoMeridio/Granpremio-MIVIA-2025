#!/bin/bash

echo "=============================="
echo "  TEST MODEL - Automatic Data"
echo "=============================="
echo "Testing behavioral cloning with automatically collected data"
echo "Dataset: dataset.csv"
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

if [ ! -f "dataset.csv" ]; then
    echo
    echo "⚠️  WARNING: dataset.csv not found!"
    echo
    echo "To test the model with automatic data you must first:"
    echo "1. Run './run_auto_collection.sh' to collect data"
    echo "2. Or run the menu script and select option 2"
    echo
    read -p "Press Enter to continue..."
    exit 1
fi

echo "Press Ctrl+C to interrupt test..."

java -cp "dist/JavaClientTorcs.jar:lib/*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.BehavioralCloningDriver dataset.csv
echo
echo "Test completed!"