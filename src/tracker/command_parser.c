#include <unistd.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "tracker.h"
#include "command_parser.h"

extern file_t *files[MAX_FILES];
extern int num_files;

void handle_announce_command(const char *commande, const char *ip_add)
{

    char *ptr = strstr(commande, "listen");
    if (ptr == NULL)
    {
        printf("Commande incorrecte: aucun numéro de port trouvé.\n");
        return;
    }
    // Recherche du numéro de port
    ptr += strlen("listen") + 1; // Avancer au début du numéro de port
    char *end_ptr = strchr(ptr, ' ');
    if (end_ptr == NULL)
    {
        printf("Numéro de port invalide.\n");

        return;
    }
    // Allouer de la mémoire pour stocker le numéro de port
    char *port = (char *)malloc(end_ptr - ptr + 1);
    // Copier le numéro de port dans la chaîne nouvellement allouée
    strncpy(port, ptr, end_ptr - ptr);
    port[end_ptr - ptr] = '\0'; // Ajouter le caractère de fin de chaîne
    // Recherche du reste des donnes
    ptr = strstr(commande, "[");
    if (ptr == NULL)
    {
        printf("Aucune donnée de fichier trouvée.\n");
        return;
    }
    ptr++; // avancer au prochain caractère après '['
    int i = 0;
    char *token = strtok(ptr, " []");
    while (token != NULL)
    {
        switch (i % 4)
        {
        case 0:
            files[num_files] = malloc(sizeof(file_t));
            files[num_files]->name = strdup(token);

            break;
        case 1:
            files[num_files]->length = atoi(token);
            break;
        case 2:
            files[num_files]->piece_size = atoi(token);
            break;
        case 3:
         char *file_key = strdup(token);
            int file_exists = 0;
            char *ip_address = strdup(ip_add);
            for (int x = 0; x < num_files; ++x)
            {
                if (strncmp(files[x]->key, file_key, 32) == 0)
                {
                    int existed_peer = 0;
                    for (int z =0; z < files[x]->num_peers; z++){
                        
                        if (strcmp(files[x]->possessors[z].port, port) == 0){
                            existed_peer = 1;
                            break;
                        }
                    }
                        if (existed_peer == 0){
                            files[x]->possessors[files[x]->num_peers].ip_address = ip_address; // Not specified in this code snippet
                            files[x]->possessors[files[x]->num_peers].port = strdup(port);
                            files[x]->num_peers++;
                            file_exists = 1;
                        }
                }
            }
            if (!file_exists)
            {
                files[num_files]->key = file_key;
                files[num_files]->num_peers = 1;
                files[num_files]->possessors[0].ip_address = ip_address; // Not specified in this code snippet
                files[num_files]->possessors[0].port = strdup(port);
                num_files++;
            }
            break;
        }
        token = strtok(NULL, " []");
        i++;
    }
    free(port);
}

void display_data()
{

    // Afficher les informations des fichiers extraits
    for (int i = 0; i < num_files; i++)
    {
        printf("File Name: %s\n", files[i]->name);
        printf("File Length: %u\n", files[i]->length);
        printf("Piece Size: %u\n", files[i]->piece_size);
        printf("Key: %s\n", files[i]->key);
        printf("list of possessors of %s: [ ", files[i]->name);
        for (int j = 0; j < files[i]->num_peers; ++j)
        {
            printf("%s ", files[i]->possessors[j].port);
        }
        printf("]");
        printf("\n");
    }
}

int file_matches_criteria(const file_t *f, const char *criteria)
{
    const char *delimiter = "=\"";

    // Copie de la chaîne de critères pour éviter les modifications
    char *criteria_copy = strdup(criteria);
    if (criteria_copy == NULL)
    {
        perror("Erreur lors de l'allocation de mémoire pour la copie des critères");
        exit(EXIT_FAILURE);
    }

    char *token = strtok(criteria_copy, delimiter);
    while (token != NULL)
    {
        char *field = token;
        token = strtok(NULL, delimiter);
        char *value = token;
        token = strtok(NULL, delimiter);

        if (field != NULL && value != NULL)
        {
            if (strcmp(field, "filename") == 0)
            {
                if (strcmp(f->name, value) != 0)
                {
                    free(criteria_copy);
                    return 0;
                }
            }
            else if (strcmp(field, "filesize>") == 0)
            {
                if (f->length <= atoi(value))
                {
                    free(criteria_copy);
                    return 0;
                }
            }
            else if (strcmp(field, "filesize<") == 0)
            {
                if (f->length >= atoi(value))
                {
                    free(criteria_copy);
                    return 0;
                }
            }
        }
    }

    free(criteria_copy);
    return 1;
}

char *handle_look_command(const char *commande)
{
    char *ptr = strstr(commande, "[");
    if (ptr == NULL)
    {
        char *output = malloc(37); // Taille suffisante pour le message d'erreur
        if (output == NULL)
        {
            printf("Erreur d'allocation de mémoire.\n");
            return NULL;
        }
        strcpy(output, "Aucune donnée de fichier trouvée.\n");
        return output;
    }
    ptr++; // avancer au prochain caractère après '['
    char *criteria = strtok(ptr, "]");

    // Allocation de mémoire pour la chaîne de caractères de sortie
    char *output = malloc(MAX_FILES * 100); // Supposant une taille maximale de 100 caractères par fichier
    if (output == NULL)
    {
        printf("Erreur d'allocation de mémoire.\n");
        return NULL;
    }

    // Parcourir les fichiers et formater les données dans la chaîne de sortie
    int offset = 0;
    for (int i = 0; i < num_files; i++)
    {
        if (file_matches_criteria(files[i], criteria))
        {
            offset += sprintf(output + offset, "%s %u %u %s ", files[i]->name, files[i]->length, files[i]->piece_size, files[i]->key);
        }
    }

    // Ajouter le caractère de fin de chaîne
    output[offset] = '\0';

    // Formater la chaîne de sortie au format spécifié
    char *formatted_output = malloc(strlen(output) + 9); // Taille pour "> list []" plus la chaîne de fichiers
    if (formatted_output == NULL)
    {
        printf("Erreur d'allocation de mémoire.\n");
        free(output);
        return NULL;
    }
    sprintf(formatted_output, "> list [%s]", output);

    // Libérer la mémoire allouée pour la chaîne de sortie
    free(output);

    return formatted_output;
}

char *handle_getfile_command(char *command)
{
    char *command_copy = strdup(command); // Créer une copie modifiable de la chaîne
    if (command_copy == NULL)
    {
        char *error_message = strdup("Erreur lors de l'allocation de mémoire.\n");
        if (error_message == NULL)
        {
            fprintf(stderr, "Erreur lors de l'allocation de mémoire.\n");
        }
        return error_message;
    }

    char *token = strtok(command_copy, " ");
    token = strtok(NULL, " "); // Passer au jeton suivant (la clé du fichier)
    if (token == NULL)
    {
        char *error_message = strdup("Clé de fichier manquante.\n");
        free(command_copy); // Libérer la mémoire allouée
        return error_message;
    }

    // Rechercher le fichier correspondant à la clé
    file_t *file = NULL;
    for (int i = 0; i < num_files; i++)
    {
        if (strcmp(files[i]->key, token) == 0)
        {
            file = files[i];
            break;
        }
    }

    if (file == NULL)
    {
        char *error_message = malloc(strlen(token) + 50); // Taille pour le message d'erreur
        if (error_message == NULL)
        {
            fprintf(stderr, "Erreur lors de l'allocation de mémoire.\n");
            free(command_copy); // Libérer la mémoire allouée
            return NULL;
        }
        sprintf(error_message, "Fichier non trouvé pour la clé %s.\n", token);
        free(command_copy); // Libérer la mémoire allouée
        return error_message;
    }

    // Allocation de mémoire pour la chaîne de caractères de sortie
    char *output = malloc(MAX_PEERS * (INET_ADDRSTRLEN + 7)); // Taille pour l'adresse IP et le port
    if (output == NULL)
    {
        fprintf(stderr, "Erreur lors de l'allocation de mémoire.\n");
        free(command_copy); // Libérer la mémoire allouée
        return NULL;
    }
    output[0] = '\0';

    // Formater la liste des pairs possédant le fichier
    strcat(output, "peers ");
    strcat(output, file->key);
    strcat(output, " [");
    for (int i = 0; i < file->num_peers; i++)
    {

        char peer_info[INET_ADDRSTRLEN + +strlen(file->possessors[i].port)]; // Taille pour l'adresse IP et le port
        sprintf(peer_info, "%s:%s", file->possessors[i].ip_address, file->possessors[i].port);
        strcat(output, peer_info);
        if (i != file->num_peers - 1)
        {
            strcat(output, " ");
        }
    }
    strcat(output, "]");

    free(command_copy); // Libérer la mémoire allouée
    return output;
}


char *get_seed_list(const char *command) {
    // Recherche de la partie "seed []"
    char *seed_start = strstr(command, "seed [");
    if (seed_start == NULL) {
        printf("Partie 'seed []' non trouvée dans la commande.\n");
        return NULL;
    }
    
    // Avancer au début de la liste après "seed []"
    seed_start += strlen("seed [");
    
    // Recherche de la partie "leech []" après "seed []"
    char *leech_start = strstr(seed_start, "leech [");
    if (leech_start != NULL) {
        // Si "leech []" est trouvé, le contenu entre "seed []" et "leech []" est la liste que nous voulons récupérer
        // Nous devons donc calculer la longueur de la liste en soustrayant les pointeurs
        size_t seed_list_length = leech_start - seed_start;
        
        // Allouer de la mémoire pour stocker la liste
        char *seed_list = malloc(seed_list_length);
        if (seed_list == NULL) {
            printf("Erreur d'allocation de mémoire.\n");
            return NULL;
        }
        
        // Copier la liste dans la mémoire allouée
        strncpy(seed_list, seed_start, seed_list_length - 2); // Ignorer le "]"
        seed_list[seed_list_length - 2] = '\0'; // Ajouter le caractère de fin de chaîne
        
        return seed_list;
    } else {
        // Si "leech []" n'est pas trouvé, la liste est la partie restante après "seed []"
        // Nous pouvons donc simplement retourner le reste de la chaîne
        return strdup(seed_start);
    }
}


char *handle_update_command(const char *command, char * ip_add)
{
    // Obtenir la liste de seed
    char *list_seed = get_seed_list(command);
    printf("%s\n", list_seed);

    // Utiliser strtok pour diviser la chaîne en mots
    char *token = strtok(list_seed, " ");
    while (token != NULL) {
        // Allouer de la mémoire pour stocker la clé extraite
        int key_length = 32;
        char *key = malloc(key_length + 1);
        strncpy(key, token, key_length);
        key[key_length] = '\0';

        // Rechercher le fichier correspondant à la clé
        file_t *file = NULL;
        for (int i = 0; i < num_files; i++) {
            if (strcmp(files[i]->key, key) == 0) {
                int existed_peer = 0;
                for (int z =0; z < files[i]->num_peers; z++){
                        
                    if (strcmp(files[i]->possessors[z].ip_address, ip_add) == 0){
                        existed_peer = 1;
                        break;
                        }
                }
                if (existed_peer == 0){
                            files[i]->possessors[files[i]->num_peers].ip_address = strdup(ip_add);
                            files[i]->possessors[files[i]->num_peers].port = "222267";
                            files[i]->num_peers++;
                }
                break;
            }
        }

        // Libérer la mémoire allouée pour la clé
        free(key);

        // Avancer au mot suivant
        token = strtok(NULL, " ");
    }

    // Libérer la mémoire allouée pour la liste de seed
    free(list_seed);

    return "ok";
}
