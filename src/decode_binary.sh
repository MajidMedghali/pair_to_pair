#!/bin/bash

# Vérifier si le nombre d'arguments est correct
if [ $# -ne 2 ]; then
    echo "Usage: $0 input_file output_file"
    exit 1
fi

# Assigner les noms de fichiers aux variables
input_file="$1"
output_file="$2"

# Vérifier si le fichier d'entrée existe
if [ ! -f "$input_file" ]; then
    echo "Input file $input_file not found."
    exit 1
fi

# Convertir le fichier hexadécimal en binaire en utilisant xxd
xxd -r -p "$input_file" "$output_file"

# Vérifier si l'opération a réussi
if [ $? -eq 0 ]; then
    echo "Hexadecimal file decoded to binary successfully."
else
    echo "Failed to decode hexadecimal file to binary."
    exit 1
fi

# message=$(xxd -r -p "$input_file" "$output_file")
echo "Process completed successfully."