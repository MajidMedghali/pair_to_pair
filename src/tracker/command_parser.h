#ifndef COMMAND_PARSER_H
#define COMMAND_PARSER_H
#include "config_parser.h"
#include "tracker.h"

void handle_announce_command(const char *commande, const char *ip_add);
void display_data();
int file_matches_criteria(const file_t *f, const char *criteria);
char *handle_look_command(const char *commande);
char *handle_getfile_command(char *command);
char* handle_update_command(const char *command, char* ip_add);
#endif
