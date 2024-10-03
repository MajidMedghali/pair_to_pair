#!/bin/bash

# Vérifier si le nombre d'arguments est correct
if [ $# -ne 3 ]; then
    echo "Usage: $0 input_text_file output_directory part_size"
    exit 1
fi

# Assigner les arguments aux variables
input_file="$1"
output_dir="$2"

# Vérifier si le fichier d'entrée existe
if [ ! -f "$input_file" ]; then
    echo "Input file $input_file not found."
    exit 1
fi

# Vérifier si le répertoire de sortie existe, sinon le créer
mkdir -p "$output_dir"

# Taille des morceaux en termes de nombre de caractères
# chunk_size=1024  # Par exemple, 1000 caractères par morceau
chunk_size=$3

# Compter le nombre total de caractères dans le fichier
total_chars=$(wc -c < "$input_file")

# Calculer le nombre total de morceaux nécessaires
total_chunks=$(( (total_chars + chunk_size - 1) / chunk_size ))

# Découper le fichier en morceaux de taille fixe
split -a 3 -d -b "$chunk_size" "$input_file" "$output_dir/chunk_"

# Renommer les morceaux pour qu'ils aient une numérotation séquentielle
for ((i = 0; i < total_chunks; i++)); do
    mv "$output_dir/chunk_$(printf "%03d" $i)" "$output_dir/chunk_$i.txt"
done

# Vérifier et remplir la dernière pièce si nécessaire
last_chunk_size=$(wc -c < "$output_dir/chunk_$((total_chunks-1)).txt")
if [ "$last_chunk_size" -lt "$chunk_size" ]; then
    padding=$((chunk_size - last_chunk_size))
    printf "%0.sx" $(seq 1 "$padding") >> "$output_dir/chunk_$((total_chunks-1)).txt"
    echo "Last chunk filled with padding to reach $chunk_size characters."
fi


mkdir -p "$output_dir/bin"
# Appliquer le codage binaire à chaque morceau
for ((i = 0; i < total_chunks; i++)); do
    input_chunk="$output_dir/chunk_$i.txt"
    output_chunk="$output_dir/bin/chunk_$i.bin"
    ./src/encode_in_bin.sh "$input_chunk" "$output_chunk"
    echo "Chunk $input_chunk encoded to binary and saved as $output_chunk."
done
echo "File split into $total_chunks chunks of size $chunk_size characters each."
