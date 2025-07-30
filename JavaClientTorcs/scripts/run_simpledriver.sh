#!/bin/bash

echo "=============================="
echo "  SIMPLE DRIVER - Execution"
echo "=============================="
echo "Starting SimpleDriver for basic autonomous driving"
echo "(without data collection)"
echo "=============================="
echo "Press Ctrl+C to interrupt execution..."
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
java -cp target/JavaClientTorcs-1.0-SNAPSHOT.jar it.unisa.javaclienttorcs.Client it.unisa.javaclienttorcs.SimpleDriver host:localhost port:3001 verbose:on
echo
echo "Execution completed!"}]}