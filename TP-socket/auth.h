/* Time-stamp: <04 avr 2012 22:25 queinnec@enseeiht.fr> */

/* Categories of protocol messages. */
enum  Auth_Kind {
    AUTH_QUERY,
    AUTH_ACK,
    AUTH_NACK
};

/* Exchanged message. */
typedef struct {
    enum Auth_Kind kind;
} Auth_Message;

/* Authorization lifetime (en secondes). */
#define AUTH_LIFETIME 6

/* Timeout waiting for server answer. */
#define AUTH_CLIENT_TIMEOUT 3

/****************************************************************/

/* Client interface. */

/* Module initialization. */
void auth_init(int serverport);

/* Return true if the client is currently authorized, else false. */
int auth_check();
