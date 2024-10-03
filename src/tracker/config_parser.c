#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "config_parser.h"

char* extract_tracker_address(const char* filename) {
    FILE* file = fopen(filename, "r");
    if (file == NULL) {
        perror("Erreur lors de l'ouverture du fichier");
        exit(EXIT_FAILURE);
    }

    char* tracker_address = NULL;
    char line[100];

    while (fgets(line, sizeof(line), file)) {
        if (strstr(line, "tracker-address") != NULL) {
            char* value = strchr(line, '=') + 2; // Pointer vers la valeur après le signe '='
            tracker_address = strdup(value); // Copier la valeur dans une nouvelle chaîne
            // Supprimer le saut de ligne à la fin si présent
            char* newline = strchr(tracker_address, '\n');
            if (newline != NULL) {
                *newline = '\0';
            }
            break;
        }
    }

    fclose(file);
    return tracker_address;
}

int extract_tracker_port(const char* filename) {
    FILE* file = fopen(filename, "r");
    if (file == NULL) {
        perror("Erreur lors de l'ouverture du fichier");
        exit(EXIT_FAILURE);
    }

    int tracker_port = -1;
    char line[100];

    while (fgets(line, sizeof(line), file)) {
        if (strstr(line, "tracker-port") != NULL) {
            char* value = strchr(line, '=') + 2; // Pointer vers la valeur après le signe '='
            tracker_port = atoi(value); // Convertir la valeur en entier
            break;
        }
    }

    fclose(file);
    return tracker_port;
}


