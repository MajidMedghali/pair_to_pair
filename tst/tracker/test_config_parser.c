// int main() {
//     char* address = extract_tracker_address("config.ini");
//     int port = extract_tracker_port("config.ini");

//     if (address != NULL) {
//         printf("Adresse du tracker : %s\n", address);
//         free(address); // Libérer la mémoire allouée dynamiquement
//     } else {
//         printf("Adresse du tracker non trouvée dans le fichier de configuration.\n");
//     }

//     if (port != -1) {
//         printf("Port du tracker : %d\n", port);
//     } else {
//         printf("Port du tracker non trouvé dans le fichier de configuration.\n");
//     }

//     return 0;
// }