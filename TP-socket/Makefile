
CC = cc
CFLAGS = -g -W -Wall
LDFLAGS = -g
LDLIBS = -lpthread

EXE = httpd auth_server

all : $(EXE)

httpd : httpd.o common.o auth_client.o
auth_server : auth_server.o

httpd.o : httpd.c common.h auth.h
common.o : common.c common.h
auth_server.o : auth_server.c auth.h
auth_client.o : auth_client.c auth.h

clean :
	rm -f $(EXE) *.o
