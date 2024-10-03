#!/bin/bash

# Define the file size in bytes (1 KB)
file_size=$((1024))

# Number of files per folder
num_files_per_folder=10

# Total number of folders to create
total_folders=20

# Output directory (parent directory)
output_dir="./generated_folders"

# Create the parent directory if it doesn't exist
mkdir -p $output_dir

# Loop to create the desired number of folders
for ((folder_index=1; folder_index <= total_folders; folder_index++)); do
    # Create the subfolder name
    subfolder_name="$output_dir/folder_$folder_index"

    # Create the subfolder if it doesn't exist
    mkdir -p $subfolder_name

    # Loop to create files within the subfolder
    for ((file_index=1; file_index <= num_files_per_folder; file_index++)); do
        # Generate the filename
        filename="$subfolder_name/file_$file_index.bin"

        # Create the binary file of specified size with random data
        dd if=/dev/urandom of=$filename bs=$file_size count=1 status=none

        echo "File $filename created in folder $subfolder_name."
    done
done
