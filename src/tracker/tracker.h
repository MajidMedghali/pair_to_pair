#ifndef TRACKER_H
#define TRACKER_H

#define MAX_SOCKETS 1000
#define MAX_FILES 5000
#define MAX_PEERS 30

typedef struct
{
    char *ip_address;
    char *port;
} pair_t;

typedef struct
{
    char *name;
    unsigned int length;
    unsigned int piece_size;
    char *key;
    int num_peers;
    pair_t possessors[MAX_PEERS];
} file_t;



#endif