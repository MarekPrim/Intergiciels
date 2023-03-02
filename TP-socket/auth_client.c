/* Time-stamp: <17 fév 2022 14:02 queinnec@enseeiht.fr> */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <strings.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <errno.h>
#include <err.h>
#include "auth.h"

static int authorized =1; /* 0 or 1 */

static int auth_socket; /* to talk with the authorization server. */
static struct sockaddr_in adrserv;

/* Cancel the current authorization. Used as a sig handler for SIGALRM. */
static void cancel_authorization(int unused)
{
    printf("Authorization ended.\n");
    authorized = 0;
}

void auth_init(int serverport)
{
    struct sigaction act;
    struct timeval tv = { AUTH_CLIENT_TIMEOUT, 1 };

    act.sa_handler = cancel_authorization;
    act.sa_flags = SA_RESTART;
    sigaction (SIGALRM, &act, NULL);

    authorized = 0;

    /* XXXX Build destination address. */
    bzero (&adrserv, sizeof(adrserv));
    adrserv.sin_family = AF_INET;
    adrserv.sin_port = htons(serverport);
    adrserv.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    
    /* XXXX Open a UDP socket */
    auth_socket = socket(AF_INET, SOCK_DGRAM, 0);
    if (auth_socket == -1)
      err(1, "socket");

    

    
    /* XXXX Allow broadcast on this socket. */
    
    int val = 1;
    setsockopt (auth_socket, SOL_SOCKET, SO_BROADCAST, &val, sizeof(val));

    /* XXXX Timeout on receive. */

    setsockopt (auth_socket, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

}

int auth_check()
{

    Auth_Message msg;

    char buf[1024];
    int err_code;

    msg.kind = AUTH_QUERY;



    if (authorized) /* already authorized. */
      return 1;

    printf ("Auth query\n");


    /* XXXX A compléter */
    /* send query to the server */
    sendto (auth_socket, &msg, sizeof(msg), 0, (struct sockaddr *)&adrserv, sizeof(adrserv));
    /* wait for answer */
    printf("Waiting for answer\n");
    err_code = recvfrom(auth_socket, buf, sizeof(buf), 0, NULL,NULL);

    if (err_code == -1){
      if (errno == EAGAIN || errno == EWOULDBLOCK)
        printf("Timeout\n");
      else
        err(1, "recvfrom");
    }
    
    /* and check if this answer is positive */

    // Print the answer
    printf("Received: %s\n", buf);
    // printf("Received: %d\n", len);

    msg = *(Auth_Message *)buf;

    printf("Received: %d\n", err_code);

    if(msg.kind == AUTH_ACK) {
      authorized = 1;
      printf("Authorized\n");
      return 1;
    } else if(msg.kind == AUTH_NACK){
      authorized = 0;
      printf("Not authorized\n");
      return 0;
    } else {
      err("Unknown message type", -1);
    }


    if (authorized) /* start timer */
      alarm(AUTH_LIFETIME);
    
    return authorized;
}
