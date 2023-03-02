/* Time-stamp: <29 jan 2020 08:24 queinnec@enseeiht.fr> */

/* Formatted write on a file descriptor. Works like printf.
 * Example: writef(sockfd, "File %s not found\n", filename);
 */
void writef(int fd, const char *fmt, ...)
    __attribute__ ((format (printf, 2, 3)));

/* Read a complete http request from the file descriptor fd and return the first line.
 * A line is a sequence of characters ending with \n or \r\n; the trailing \n or \r\n is dropped.
 * The request ends with \r\n\r\n.
 * The line returned is allocated with malloc; the caller must free it when finished.
 * read_request has an arbitrary internal limit on the line length: a longer line will be truncated (beurk).
 */
char *read_request(int fd);

/* Known methods. */
#define REQUEST_GET 1
#define REQUEST_UNKNOWN 10

/* Parse the request, returns method and url.
 * The parsing is not solid at all !
 * line is modified in place, and filename points into it.
 *
 * Usage:
 *   char *line, *filename;
 *   int method;
 *   line = read_request(sockfd);
 *   parse_request(line, &method, &filename);
 *   if (method == REQUEST_GET) {  do something with filename }
 *   else { handle unknown method }
 *   ...
 */
void parse_request(char *line, int *method, char **filename);

/* Return the mimetype for the given filename.
 * It's actually a mock implantation. It only handles:
 * - if filename ends with ".html" => "text/html; charset=utf-8"
 * - if filename ends with ".jpg" or ".jpeg => "image/jpeg"
 * - if filename ends with ".png" => "image/png"
 * - any other file is assumed to be text => "text/plain; charset=utf-8"
 */
char *find_mimetype(char *filename);
