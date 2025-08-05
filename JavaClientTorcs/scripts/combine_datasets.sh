#!/bin/bash

echo "=============================="
echo "  COMBINE DATASETS"
echo "=============================="
echo "Combining automatic and manual datasets..."
echo "Input: auto_dataset.csv + human_dataset.csv"
echo "Output: combined_dataset.csv"
echo "=============================="

cd "$(dirname "$0")/.."

# Check if files exist
if [ ! -f "auto_dataset.csv" ]; then
    echo "ERROR: auto_dataset.csv not found!"
    echo "Run ./run_auto_collection.sh first"
    read -p "Press Enter to continue..."
    exit 1
fi

if [ ! -f "human_dataset.csv" ]; then
    echo "ERROR: human_dataset.csv not found!"
    echo "Run ./run_manual_driving.sh first"
    read -p "Press Enter to continue..."
    exit 1
fi

# Combine files
echo "Combining datasets..."
cat auto_dataset.csv human_dataset.csv > combined_dataset.csv

echo
echo "Datasets combined successfully!"
echo "File created: combined_dataset.csv"
echo "Total rows:"
wc -l < combined_dataset.csv