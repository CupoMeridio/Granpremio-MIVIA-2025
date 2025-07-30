#!/bin/bash

echo "=============================="
echo "  MANUAL DRIVING - Data Collection"
echo "=============================="
echo "Controls:"
echo "- Arrow keys: Directional controls"
echo "- W/A/S/D: WASD gaming"
echo "- I/J/K/L: Alternative symmetric"
echo "- 8/2/4/6: Numeric keypad"
echo "- C: Toggle data collection"
echo "- P: Statistics"
echo "- R: Reset"
echo "- Q: Exit"
echo "=============================="
echo "Press Ctrl+C to interrupt..."
echo

# Cleanup function to kill any remaining Java processes
cleanup() {
    echo "Cleaning up..."
    pkill -f "java.*HumanController" 2>/dev/null || true
    sleep 1
    exit 0
}

# Set trap for script termination
trap cleanup EXIT SIGINT SIGTERM

cd "$(dirname "$0")/.."
java -cp target/JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.HumanController host:localhost port:3001 --collect
echo
echo "Driving session completed!"