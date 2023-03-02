/*
 * Un serveur httpd basique
 * Time-stamp: <17 fév 2022 16:00 queinnec@enseeiht.fr>
 */

#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <signal.h>
#include <string.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <err.h>
#include "common.h"
#include "auth.h"

static unsigned int myport; /* my port number */

/* Handle one http request. */
void handle_request (int sock)
{

    char *line, *filename;
    int method;
    int fd;
    char buf[1024];
    int r;
    int authorized;

    authorized = auth_check();
    if (authorized == 0) {
        writef(sock, "HTTP/1.0 403 Forbidden\n");
        writef(sock,"Server: Le mien\n");
        writef(sock, "Content-Type: text/plain\n");
        writef(sock,"\n");
        writef(sock, "Not authorized\n");
        return;
    }
    
    line = read_request(sock);
    printf("Request: %s\n",line);
    parse_request(line, &method, &filename);
    if (method == REQUEST_GET) {
        // Renvoyez le fichier demandé
        fd = open(filename, O_RDONLY);
        if (fd == -1) {
            printf("File not found: %s\n", filename);
            writef(sock, "HTTP/1.0 404 Not Found\n");
            writef(sock,"Server: Le mien\n");
            writef(sock, "Content-Type: text/plain\n");
            writef(sock,"\n");
            writef(sock, "File not found %d\n", authorized);
        } else {
            // Le fichier existe
            // Renvoyez le code 200 OK
            writef(sock, "HTTP/1.0 200 OK\n");
            writef(sock,"Server: Le mien\n");
            writef(sock, "Content-Type: text/plain\n");
            writef(sock,"\n");
            while((r=read(fd,buf,sizeof(buf)))>0) {
                write(sock,buf,r);
            }
            close(fd);
        }

    } else {
        // Renvoyez une erreur 404
        writef(sock, "HTTP/1.0 404 Not Found\n");
            writef(sock,"Server: Le mien\n");
            writef(sock, "Content-Type: text/plain\n");
            writef(sock,"\n");
            writef(sock, "Method not found\n");
    }       
}

int main (int argc, char *argv[])
{
    struct sockaddr_in adrserv;
    int soc, r, val;
    int authport;

    if (argc != 3) {
        fprintf(stderr, "%s <port> <authport>\n", argv[0]);
        exit(2);
    }

    myport = atoi(argv[1]);
    authport = atoi(argv[2]);

    auth_init(authport);

    signal(SIGPIPE, SIG_IGN);

    /* socket Internet, de type stream (fiable, bi-directionnel) */
    soc = socket (AF_INET, SOCK_STREAM, 0);
    if (soc == -1) err(1, "socket");

    /* Force la réutilisation de l'adresse si non allouée.
     * (cela permet de relancer le serveur immédiatement après une
     * "sale" terminaison, i.e. sans fermeture propre (close ou shutdown)
     * du socket d'écoute). */
    val = 1;
    if (setsockopt(soc, SOL_SOCKET, SO_REUSEADDR, &val, sizeof(val)) == -1)
      err (1, "setsockopt");

    /* Nomme le socket: socket inet, port myport, adresse IP quelconque */
    adrserv.sin_family = AF_INET;
    adrserv.sin_addr.s_addr = INADDR_ANY;
    adrserv.sin_port = htons(myport);
    r = bind (soc, (struct sockaddr *)&adrserv, sizeof(adrserv));
    if (r == -1) err(1, "bind");

    /* Prépare le socket à la réception de connexions */
    listen (soc, 5);

    while(1) {
        int sock;
        struct sockaddr_in adresse;
        socklen_t lg;

        // Attente connexion client

        lg = sizeof(adresse);
        sock = accept(soc, (struct sockaddr *)&adresse, &lg);

        if (sock == -1) err(1, "accept");

        handle_request(sock);
        close(sock);
    }

}

