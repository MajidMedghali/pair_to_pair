#!/bin/bash

# Vérifier si le nombre d'arguments est correct
if [ $# -ne 2 ]; then
    echo "Usage: $0 input_file output_file"
    exit 1
fi

# Assigner les noms de fichiers aux variables
input_file="$1"
output_file="$2"



nom_dossier=$(dirname "$2")
nom_fichier=$(basename "$2")

mkdir -p $nom_dossier
touch $1

# Vérifier si le fichier d'entrée existe
if [ ! -f "$input_file" ]; then
    echo "Input file $input_file not found."
    exit 1
fi

# Convertir le fichier binaire en hexadécimal en utilisant xxd
xxd -p "$input_file" > "$output_file"

# Vérifier si l'opération a réussi
if [ $? -eq 0 ]; then
    echo "Binary file encoded to hexadecimal successfully."
else
    echo "Failed to encode binary file to hexadecimal."
    exit 1
fi

echo "Process completed successfully."