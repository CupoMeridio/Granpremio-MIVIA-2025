#!/bin/bash

echo "=============================="
echo "  AUTOMATIC DATA COLLECTION"
echo "=============================="
echo "Collecting data with SimpleDriver..."
echo "Data will be saved to: dataset.csv"
echo "=============================="
echo "Press Ctrl+C to interrupt collection..."
echo
# Cleanup function to kill any remaining Java processes
cleanup() {
    echo "Cleaning up..."
    pkill -f "java.*SimpleDriver" 2>/dev/null || true
    sleep 1
    exit 0
}

# Set trap for script termination
trap cleanup EXIT SIGINT SIGTERM

cd "$(dirname "$0")/.."
java -cp "dist/JavaClientTorcs.jar:lib/*" it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.SimpleDriver --collect-data
echo
echo "Collection completed!"
echo "File saved: dataset.csv"