
#include <unistd.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h> // Ajout de l'en-tête pour les fonctions de thread
#include <assert.h>
#include "../../src/tracker/command_parser.h"
#include "../../src/tracker/thpool.h"



#define MAX_CONNECTED_PEERS 10

file_t *files[MAX_FILES];
int num_files = 0;




void test_announce_command(){
    printf("Starting Test announce command \n");

     char commande_announce_1[] = "announce listen 222267 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_2[] = "announce listen 300037 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_3[] = "announce listen 450017 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_4[] = "announce listen 120077 seed [file_a.dat 2097152 1024 200e92afeb80fc7722ec89eb0bf0711 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";

    handle_announce_command(commande_announce_1, "127.0.0.1");
    handle_announce_command(commande_announce_2, "127.0.0.1");
    handle_announce_command(commande_announce_3, "127.0.0.1");
    handle_announce_command(commande_announce_4, "127.0.0.1");
        assert(strcmp(files[0]->name, "file_a.dat") == 0);
        assert(files[0]->length == 2097152);
        assert(files[0]->piece_size == 1024);
        assert(strcmp(files[0]->key, "8905e92afeb80fc7722ec89eb0bf0966" ) == 0);
        
        for (int j = 0; j < files[0]->num_peers; ++j)
        {
            assert(strcmp(files[0]->possessors[j].port, "222267")  ==0|| strcmp(files[0]->possessors[j].port, "300037")  == 0|| strcmp(files[0]->possessors[j].port, "450017") == 0);
        }
    printf("\t Test 1: PASSED\n");

        assert(strcmp(files[1]->name, "file_b.dat") == 0);
        assert(files[1]->length == 3149028);
        assert(files[1]->piece_size == 1536);
        assert(strcmp(files[1]->key, "330a57722ec8b0bf09669a2b35f77e9e" ) == 0);
        
        for (int j = 0; j < files[1]->num_peers; ++j)
        {
            assert(strcmp(files[1]->possessors[j].port, "222267")  ==0|| strcmp(files[1]->possessors[j].port, "300037")  == 0|| strcmp(files[1]->possessors[j].port, "450017") == 0 || strcmp(files[1]->possessors[j].port, "120077") == 0 );
        }

    printf("\t Test 2: PASSED\n");

        assert(strcmp(files[2]->name, "file_c.dat") == 0);
        assert(files[2]->length == 2100152);
        assert(files[2]->piece_size == 1024);
        assert(strcmp(files[2]->key, "8905e92afeb10hc7722ec77eb0bf0966" ) == 0);
        
        for (int j = 0; j < files[2]->num_peers; ++j)
        {
            assert(strcmp(files[2]->possessors[j].port, "222267")  ==0|| strcmp(files[1]->possessors[j].port, "300037")  == 0|| strcmp(files[1]->possessors[j].port, "450017") == 0 || strcmp(files[1]->possessors[j].port, "120077") == 0 );
        }
    printf("\t Test 3: PASSED\n");

        printf("Test announce command : PASSED\n");
}



void test_look_command(){
    printf("Starting Test look command \n");
    char commande_announce_1[] = "announce listen 222267 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_2[] = "announce listen 300037 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_3[] = "announce listen 450017 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_4[] = "announce listen 120077 seed [file_a.dat 2097152 1024 200e92afeb80fc7722ec89eb0bf0711 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_5[]= "announce listen 100000 seed [reseaux.dat 2097152 1024 8905e92afeb80fc77212ec89eb0bf0966 systeme.dat 3149028 1536 330a57702ec8b0bf09669a2b35f77e9e file_ia.dat 6100152 1024 8905e92afeb10hcj722ec77eb0bf0966]";
    char commande_announce_6[] = "announce listen 999999 seed [traitement_image.dat 2097152 1024 8905e92zfeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330an7722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92hfeb10hc7722ec77eb0bf0966]";


    handle_announce_command(commande_announce_1, "127.0.0.1");
    handle_announce_command(commande_announce_2, "127.0.0.1");
    handle_announce_command(commande_announce_3, "127.0.0.1");
    handle_announce_command(commande_announce_4, "127.0.0.1");
    char commande_look[] = "look [filename=\"file_a.dat\" filesize>\"1048576\"]";
    // Lister les fichiers selon les critères spécifiés
    char * look_response = handle_look_command(commande_look);
    assert(strcmp(look_response, "> list [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_a.dat 2097152 1024 200e92afeb80fc7722ec89eb0bf0711 file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_a.dat 2097152 1024 200e92afeb80fc7722ec89eb0bf0711 ]") == 0);
    printf("\t Test 1: PASSED\n");

    handle_announce_command(commande_announce_5, "127.0.0.2");
    handle_announce_command(commande_announce_6, "127.0.0.4");
    char commande_look_2[] = "look [filesize>\"2100151\"]";
    char * look_response_2 = handle_look_command(commande_look_2);
    // printf("%s\n", look_response_2);
    assert(strcmp(look_response_2, "> list [file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966 systeme.dat 3149028 1536 330a57702ec8b0bf09669a2b35f77e9e file_ia.dat 6100152 1024 8905e92afeb10hcj722ec77eb0bf0966 file_b.dat 3149028 1536 330an7722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92hfeb10hc7722ec77eb0bf0966 ]") == 0);
    printf("\t Test 2: PASSED\n");

    printf("Test look command : PASSED\n");

}


void test_getfile_command(){
    printf("Test getfile command \n");


    char commande_announce_1[] = "announce listen 222267 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_2[] = "announce listen 300037 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_3[] = "announce listen 450017 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";
    char commande_announce_4[] = "announce listen 120077 seed [file_a.dat 2097152 1024 200e92afeb80fc7722ec89eb0bf0711 file_b.dat 3149028 1536 330a57722ec8b0bf09669a2b35f77e9e file_c.dat 2100152 1024 8905e92afeb10hc7722ec77eb0bf0966]";

    handle_announce_command(commande_announce_1, "127.0.0.1");
    handle_announce_command(commande_announce_2, "127.0.0.1");
    handle_announce_command(commande_announce_3, "127.0.0.1");
    handle_announce_command(commande_announce_4, "127.0.0.1");
    char commande_getfile[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";
    char * get_file_response = handle_getfile_command(commande_getfile);
    assert(strcmp(get_file_response, "peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.1:222267 127.0.0.1:300037 127.0.0.1:450017 127.0.0.1:222267 127.0.0.1:300037 127.0.0.1:450017 127.0.0.1:222267 127.0.0.1:300037 127.0.0.1:450017]") == 0);
    printf("\t Test 1: PASSED\n");


    char commande_getfile_2[] = "getfile 200e92afeb80fc7722ec89eb0bf0711";
    char * get_file_response_2 = handle_getfile_command(commande_getfile_2);
    assert(strcmp(get_file_response_2, "peers 200e92afeb80fc7722ec89eb0bf0711 [127.0.0.1:120077 127.0.0.1:120077 127.0.0.1:120077]") == 0);
    printf("\t Test 2: PASSED\n");

    printf("Test getfile command : PASSED\n");

}

int test_update_command(){


    // Exemple de commande
        const char *command = "update seed [8905e92afeb80fc7722ec89eb0bf0966 8905e92afeb80fc7722ec89eb0bf0966 8905e92afeb80fc7722ec89eb0bf0966] leech [8905e92afeb80fc7722ec89eb0bf0966 8905e92afeb80fc7722ec89eb0bf0966 8905e92afeb80fc7722ec89eb0bf0966]";
        // Appeler la fonction pour extraire les clés de seed
        handle_update_command(command, "127.0.0.1");
}
int main()
{
    test_announce_command();
    test_look_command();
    // test_getfile_command();
    test_update_command();

    for (int i = 0; i < num_files; i++)
    {
        free(files[i]->name);
        files[i]->name = NULL;
        free(files[i]->key);
        files[i]->key = NULL;
        for (int j = 0; j < files[i]->num_peers; ++j)
        {
            free(files[i]->possessors[j].port);
        }
        free(files[i]);
    }
    return 0;
}
