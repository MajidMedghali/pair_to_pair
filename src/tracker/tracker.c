#include <unistd.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h> // Ajout de l'en-tête pour les fonctions de thread
#include "command_parser.h"
#include "thpool.h"
#include <pthread.h>

#define MAX_CONNECTED_PEERS 10



file_t *files[MAX_FILES];
int num_files = 0;


int sockets[MAX_SOCKETS];
int number_sockets;


struct ClientInfo {
    int socket;
    char ip_address[INET_ADDRSTRLEN];
};



pthread_mutex_t files_mutex;
pthread_mutex_t sockets_mutex;


void fill_sockets_array(){
    for (int i = 0; i < MAX_SOCKETS; i++)
    {
        sockets[i] = -1; // Initialiser à -1 pour indiquer une entrée vide
    }
}




int serverSocket = -1;

void handle_client_connection(void *arg)
{
    struct ClientInfo *client_info = (struct ClientInfo *)arg;
    int clientSocket = client_info->socket;
    char *client_ip_address = client_info->ip_address;

    if (clientSocket == -1)
    {
        perror("Erreur lors de l'acceptation de la connexion entrante");
        close(serverSocket);
        exit(EXIT_FAILURE);
    }
    // Trouver un emplacement vide dans le tableau pour stocker la nouvelle socket
    int index;
    for (index = 0; index < MAX_SOCKETS; index++)
    {
        if (sockets[index] == -1)
        {
            sockets[index] = clientSocket;
            number_sockets++;
            break;
        }
    }
    char buffer[5000];

    ssize_t bytesRead = read(clientSocket, buffer, sizeof(buffer));
    if (bytesRead == -1)
    {
        perror("Erreur lors de la lecture des données du client");
        // close(clientSocket);
        close(serverSocket);
        exit(EXIT_FAILURE);
    }
    char fullCommand[5000];
    strcpy(fullCommand, buffer);
    printf("buffer = ");
    for (int i = 0; i < 50; ++i)
    {
        printf("%c", fullCommand[i]);
    }
    printf("\n");

    char *command = strtok(buffer, " ");
    // handle announce command
    if (strcmp(command, "announce") == 0)
    {
        const char *response = "okkkaay\n";
        ssize_t bytesSent = write(clientSocket, response, strlen(response));
        if (bytesSent == -1)
        {
            perror("Erreur lors de l'envoi de la réponse au client");
            // close(clientSocket);
            close(serverSocket);
            exit(EXIT_FAILURE);
        }
        pthread_mutex_lock(&sockets_mutex);
        handle_announce_command(fullCommand, client_ip_address);
        pthread_mutex_unlock(&sockets_mutex);

    }
    // handle look command
    else if (strcmp(command, "look") == 0)
    {
        pthread_mutex_lock(&sockets_mutex);
        const char *response = handle_look_command(fullCommand);
        pthread_mutex_unlock(&sockets_mutex);

        ssize_t bytesSent = write(clientSocket, response, strlen(response));
        if (bytesSent == -1)
        {
            perror("Erreur lors de l'envoi de la réponse au client");
            // close(clientSocket);
            close(serverSocket);
            exit(EXIT_FAILURE);
        }
    }

    //handle update command
    else if (strcmp(command, "update") == 0)
    {
                pthread_mutex_lock(&sockets_mutex);

        const char *response = handle_update_command(fullCommand,  client_ip_address);
                pthread_mutex_unlock(&sockets_mutex);

        ssize_t bytesSent = write(clientSocket, response, strlen(response));
        if (bytesSent == -1)
        {
            perror("Erreur lors de l'envoi de la réponse au client");
            close(serverSocket);
            exit(EXIT_FAILURE);
        }
    }
    // handle getfile command
    else if (strcmp(command, "getfile") == 0)
    {
        // Extraire les 36 premiers caractères de fullCommand
        char first_36_chars[41]; // 36 caractères + le caractère nul de fin de chaîne
        strncpy(first_36_chars, fullCommand, 40);
        first_36_chars[40] = '\0'; // Assurer que la chaîne est correctement terminée
        first_36_chars[41] = '\n';
                pthread_mutex_lock(&sockets_mutex);

        const char *response = handle_getfile_command(first_36_chars);
                pthread_mutex_unlock(&sockets_mutex);

        printf("response getfile = %s\n", response);
        ssize_t bytesSent = write(clientSocket, response, strlen(response));
        printf("bytesSent = %ld", bytesSent);
        if (bytesSent == -1)
        {
            perror("Erreur lors de l'envoi de la réponse au client");
            close(serverSocket);
            exit(EXIT_FAILURE);
        }
    }
    else
    {
        const char *response = "Commande inconnue";
        ssize_t bytesSent = write(clientSocket, response, strlen(response));
        if (bytesSent == -1)
        {
            perror("Erreur lors de l'envoi de la réponse au client");
            // close(clientSocket);
            close(serverSocket);
            exit(EXIT_FAILURE);
        }
    }
    close(clientSocket);
    free(arg);
    return;
}




int main()
{

    // Initialisation des mutex
    if (pthread_mutex_init(&files_mutex, NULL) != 0) {
        perror("Erreur lors de l'initialisation du mutex pour les fichiers");
        exit(EXIT_FAILURE);
    }
    if (pthread_mutex_init(&sockets_mutex, NULL) != 0) {
        perror("Erreur lors de l'initialisation du mutex pour les sockets");
        exit(EXIT_FAILURE);
    }
    // Initialisation du pool de threads
    threadpool thread_pool = thpool_init(10); // 10 threads dans le pool

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1)
    {
        perror("Erreur lors de la création du socket");
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(extract_tracker_port("config.ini"));

    if (bind(serverSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) == -1)
    {
        perror("Erreur lors du bind");
        close(serverSocket);
        exit(EXIT_FAILURE);
    }

    if (listen(serverSocket, 1) == -1)
    {
        perror("Erreur lors de l'écoute des connexions entrantes");
        close(serverSocket);
        exit(EXIT_FAILURE);
    }

    while (1)
    {
        struct sockaddr_in clientAddr;
        socklen_t clientAddrLen = sizeof(clientAddr);
        int clientSocket = accept(serverSocket, (struct sockaddr *)&clientAddr, &clientAddrLen);
        if (clientSocket == -1)
        {
            perror("Erreur lors de l'acceptation de la connexion entrante");
            close(serverSocket);
            continue;
        }
          // Obtenez le port du client
        uint16_t clientPort = ntohs(clientAddr.sin_port);
        printf("Nouvelle connexion de %d\n", clientPort);


        char client_ip_address[INET_ADDRSTRLEN];
        if (inet_ntop(AF_INET, &clientAddr.sin_addr, client_ip_address, INET_ADDRSTRLEN) == NULL) {
            perror("Erreur lors de la récupération de l'adresse IP du client");
            close(clientSocket);
            continue;
        }

        printf("Nouvelle connexion de %s\n", client_ip_address);

        // Créer la structure ClientInfo
        struct ClientInfo *client_info = malloc(sizeof(struct ClientInfo));
        if (client_info == NULL) {
            perror("Erreur lors de l'allocation de mémoire pour la structure ClientInfo");
            close(clientSocket);
            continue;
        }

        client_info->socket = clientSocket;
        strcpy(client_info->ip_address, client_ip_address);
        if (thpool_add_work(thread_pool, handle_client_connection, client_info) != 0)
        {
            perror("Erreur lors de l'ajout du travail au pool de threads");
            close(clientSocket);
            free(client_info);
            continue;
        }
    }

    // Attendez que tous les travaux soient terminés
    thpool_wait(thread_pool);

    // Détruisez le pool de threads
    thpool_destroy(thread_pool);
    
    // Destruction des mutex
    pthread_mutex_destroy(&files_mutex);
    pthread_mutex_destroy(&sockets_mutex);
    

    close(serverSocket);

    return 0;
}



