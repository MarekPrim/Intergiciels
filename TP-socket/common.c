/* Time-stamp: <31 mar 2014 10:34 queinnec@enseeiht.fr> */

#define _GNU_SOURCE
#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include "common.h"

/* Formatted write on a file descriptor. Works like printf. */
void writef(int fd, const char *fmt, ...)
{
    char *tampon;
    va_list ap;
    int len, r;
    va_start(ap, fmt);
    vasprintf(&tampon, fmt, ap);
    va_end(ap);
    len = strlen(tampon);
    r = write(fd, tampon, len);
    if (r == -1) {
        perror("write failed");
    } else if (r != len) {
        fprintf(stderr, "partial write: %d of %d\n", r, len);
    }
    free(tampon);
}

/****************************************************************/

/* beurk */
#define MAX_LINE_SIZE 511

/* Read a line from the file descriptor fd and return it.
 * A line is a sequence of characters ending with \n or \r\n; the trailing \n or \r\n is dropped.
 * The line returned is allocated with malloc; the caller must free it when finished.
 * readline has an arbitrary internal limit on the line length: a longer line will be split in as many "lines" as necessary.
 */
static char *readline(int fd)
{
    char *line = malloc(MAX_LINE_SIZE+1);
    char c;
    int i = 0;
    while (i < MAX_LINE_SIZE && (read(fd, &c, 1) == 1) && (c != '\n'))
      line[i++] = c;
    line[i] = 0;
    if ((i > 0) && (line[i-1] == '\r'))
        line[i-1] = 0;
    return line;
}

/* Read on fd until two consecutive \r\n.
 *
 * state | \r | \n | other
 * ------+----+----+------
 *   0   |  0 |  1 |  0
 *   1   |  2 |  0 |  0
 *   2   |  0 |  $ |  0
 */
static void purge_request(int fd)
{
    int state = 0;
    char c;
    while (read(fd, &c, 1) == 1) {
        if ((state == 0) && (c == '\n'))
          state = 1;
        else if ((state == 1) && (c == '\r'))
          state = 2;
        else if ((state == 2) && (c == '\n'))
          break;
        else
          state = 0;
    }
}

/* Read a complete http request from the file descriptor fd and return the first line. */
char *read_request(int fd)
{
    char *line = readline(fd);
    purge_request(fd);
    return line;
}


/****************************************************************/

/* Parse the request, returns method and url. */
void parse_request(char *line, int *method, char **filename)
{
    if (strncmp(line, "GET ", 4) == 0) {
        *method = REQUEST_GET;
        *filename = line+5; /* ignores "GET /" */
        char *p = strchr(*filename, ' ');
        if (p != NULL) *p = '\0';
        printf("Request: GET %s\n", *filename);
    } else {
        *method = REQUEST_UNKNOWN;
        *filename = NULL;
        printf("Request: UNKNOWN\n");
    }
}


/****************************************************************/

/* returns true if s1 ends with s2. */
static int strend(char *s1, char *s2)
{
    int l1 = strlen(s1);
    int l2 = strlen(s2);
    return ((l1 >= l2) && (strcmp(s1 + l1 - l2, s2) == 0));
}

/* This is really not clever. */
char *find_mimetype(char *filename)
{
    if (strend(filename, ".html"))
      return "text/html; charset=utf-8";
    else if (strend(filename, ".jpg") || strend(filename, ".jpeg"))
      return "image/jpeg";
    else if (strend(filename, ".png"))
      return "image/png";
    else 
      return "text/plain; charset=utf-8";
}
