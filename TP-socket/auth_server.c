/* Time-stamp: <16 déc 2021 15:07 queinnec@enseeiht.fr> */

#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <errno.h>
#include <unistd.h>
#include <strings.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>
#include <err.h>

#include "auth.h"


/* Authorization query.
 * It arbitrarily returns ACK or NACK.
 * A real implementation would use the information on the client.
 */
static void check_authorization (int sserv, struct sockaddr_in *client)
{
    Auth_Message answer;
    int n;

    if ((random() % 2) == 0) {
        answer.kind = AUTH_NACK;
        printf ("Access: forbidden\n");
    } else {
        answer.kind = AUTH_ACK;
        printf ("Access: authorized\n");
    }

    n = sendto (sserv, (void*)&answer, sizeof(answer), 0, (struct sockaddr*)client, sizeof (struct sockaddr_in));
    if (n == -1)
      err (1, "sendto");
}

int main (int argc, char **argv)
{
   int sserv;
   struct sockaddr_in adrserv;
   int myport;

   if (argc != 2) {
        fprintf(stderr, "%s <port>\n", argv[0]);
        exit(2);
   }
   
   myport = atoi(argv[1]);
   srandom (getpid());
   
   /* Open a socket DGRAM (UDP) */
   sserv = socket (PF_INET, SOCK_DGRAM, 0);
   if (sserv == -1)
     err(1, "socket");

   /* Bind it */
   bzero (&adrserv, sizeof(adrserv));
   adrserv.sin_family = AF_INET;
   adrserv.sin_addr.s_addr = htonl (INADDR_ANY);
   adrserv.sin_port = htons (myport);
   if (bind (sserv, (struct sockaddr*)&adrserv, sizeof(adrserv)) == -1)
     err (1, "bind");

   printf("Authentification server ready on %d\n", myport);
   
   for (;;) {
       struct sockaddr_in adrcli;
       unsigned int adrclilen;
       int n;
       Auth_Message msg;

       /* Wait for a request. */
       bzero (&adrcli, sizeof (adrcli));
       adrclilen = sizeof (adrcli);
       n = recvfrom (sserv, (void*)&msg, sizeof (msg), 0, (struct sockaddr*)&adrcli, &adrclilen);
       if (n == -1) err(1, "recvfrom");

       if (random() % 4 == 1) {    /* to simulate loss */
           printf ("Ignored request (assumed lost).\n");
           continue;
       }

       switch (msg.kind) {
         case AUTH_QUERY :
           check_authorization (sserv, &adrcli);
           break;
         default :
           printf ("Unknown request: %d\n", msg.kind);
       }
   }
   /* NOTREACHED */
   return 0;
}
