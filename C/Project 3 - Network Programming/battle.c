/*This is a server program that accepts clients
 * and client can play game together and enjoy.
*/

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <features.h>
#include <time.h>
#include <stdbool.h>
#include <termios.h>
#include <errno.h>
#include <fcntl.h>

#ifndef PORT
    #define PORT 16524 //Port
#endif

struct client {
    int fd;
    char name[50];
    int hitpoints;
    int powermoves;
    int active; //1 = active, 0 not active
    int turn; //1 = their turn, 0 not their turn
    struct in_addr ipaddr;
    struct client *opnt;
    struct client *next;
};

static struct client *addclient(struct client *top, int fd, struct in_addr addr);
static struct client *removeclient(struct client *top, int fd);
static struct client *findclient(struct client *top, int fd);
static struct client *findopponent(struct client *top, struct client *player);
static struct client *clientReset(struct client *cli);

static void broadcast(struct client *top, char *s, int size, int exceptFd);
static void setup(struct client *payer);

int sendMsg(int fd, char *msg);
void moveclient(struct client *client);
void execute(struct client *player, char command);
int bindandlisten(void);

struct client *head = NULL;
struct client *tail = NULL;
struct client *headNext = NULL;
int numclients =0;
fd_set allset;

//prompt for all commands. (i.e a, p, s)
char commandsAPS[50] = "\r\n(a)ttack\r\n(p)owermove\r\n(s)peak something\r\n\0";

//prompt for a and s commands only.
char commandsAS[50] = "\r\n(a)ttack\r\n(s)peak something\r\n\0";

//Wait message to be sent for client without opponent.
char waitMsg[100] = "\r\nAwaiting next opponent...\r\n";

int main(void) {
    char namePrompt[25] = "What is your name?\0"; //prompt to ask for name.
    int clientfd, maxfd, nready;
    socklen_t len;
    struct sockaddr_in q;
    struct timeval tv;
    fd_set rset;
    int i;

    srand(time(NULL));

    int listenfd = bindandlisten(); //building and listening on server.

    FD_ZERO(&allset); //clear allset refering to FD set.
    FD_SET(listenfd, &allset); //add listening socket to allset.
    maxfd = listenfd; //max of all fds connected.

    while (1) {
        rset = allset;  // make a copy of the set before we pass it into select
        tv.tv_sec = 10;  /* timeout in seconds*/
        tv.tv_usec = 0;  /* and microseconds */

        nready = select(maxfd + 1, &rset, NULL, NULL, &tv);
        if (nready == 0) {
            //waiting for some fd to send react.
            continue;
        }

        //checking select return.
        if (nready == -1) {
            perror("select");
            continue;
        }

        //listening for new client connections.
        if (FD_ISSET(listenfd, &rset)){
            
            len = sizeof(q);

            //accepting new clients.
            if ((clientfd = accept(listenfd, (struct sockaddr *)&q, &len)) < 0) {
                perror("accept");
                exit(1);
            }

            //adding new client to allset FD set.
            FD_SET(clientfd, &allset);
            if (clientfd > maxfd) {
                maxfd = clientfd;
            }

            //adding a client to a list.
            tail = addclient(head, clientfd, q.sin_addr);
            
            //sending newly connected client name prompt.
            sendMsg(clientfd, namePrompt);
        }


        //for loop for all connected clients actions.
        for(i = 0; i <= maxfd; i++) {

            //checking for action from a client.
            if (FD_ISSET(i, &rset)) {
                struct client *p;
                char command;

                p= head;
                
                if(p != NULL){
                    
                    //finding client with fd=i in a client list.
                    p = findclient(head, i);

                    //when client successfully found in a list.
                    if(p != NULL && (p->fd) == i){
                        char buf[256];
                        char outbuf[512];
                        int len=0;
                        int pos=0;
                        
                        //if name not already enterted.
                        if(strlen(p->name)==0){
                            char s;
                            do{
                                //reading one character at a time.
                                len = read(i, &s, sizeof(char));
                                //checking if client dropped out in between.
                                if(len == 0 || len == -1){
                                    break;
                                }
                                buf[pos] = s;
                                pos++;

                            }while(s != '\n');//end buffering when new line entered.
                            buf[pos] = '\0'; // insert end character to buf.
                        }
                        //if name is already typed and assigned by player.
                        else{
                            //read a command (i.e a, p, or s)
                            len = read(i, &command, sizeof(char));
                        }

                        //checking if client is still connected.
                        if (len>0){
                            //checking for non-newline name.
                            if(buf[0] != '\n'){
                                if(strlen(p->name)==0){
                                    strncpy(p->name, buf, ((int)strlen(buf))-1);
                                    sprintf(outbuf, "Welcome, %s! Awaiting opponent...\r\n", p->name);
                                    sendMsg(p->fd, outbuf);
                                    memset(outbuf, '\0', sizeof(outbuf));

                                    sprintf(outbuf, "**%s enters the arena**\r\n", p->name);
                                    broadcast(head, outbuf, strlen(outbuf), i);
                                    
                                    FD_CLR(i, &rset);
                                    struct client *player = findopponent(head,p);

                                    if(player != NULL){
                                        setup(player);
                                    }
                                }
                                else{
                                    if(command == 'a' || command == 'p' || command=='s'){
                                        if(p->active == 1 && p->turn == 1){
                                            execute(p, command);
                                        }
                                    }
                                }
                            }
                        }

                        //when client has dropped out
                        else if(numclients >0 && (len==0 || len == -1)) {
                            //removing a client from list and moving opponent to end of list.
                            head = removeclient(head, p->fd);
                            //finding an opponent for opponent of removed client.
                            struct client *newOpponent = findopponent(head, tail);
                            //when opponent found successfully, setup and run game.
                            if(newOpponent != NULL){
                                setup(newOpponent);
                            }
                        }
                    }
                }
            }
        }
    }
    return 0;
}

//A function to add a new client to the end of the clients list and return list's tail.
static struct client *addclient(struct client *top, int fd, struct in_addr addr) {
    struct client *p = malloc(sizeof(struct client));
    if (!p) {
        perror("malloc");
        exit(1);
    }

    p->fd = fd;
    p->ipaddr = addr;
    p->active = 0;
    p->opnt = NULL;
    p->next = NULL;

    //create head of clients' list
    if(numclients < 1){
        head = p;
    }
    //if head already initialized
    else if(numclients >=1){
        //if tail exists.
        if(tail != NULL){
            tail->next = p;
        }
    }
    //making new client to be tail.
    tail = p;
    //increasing number of clients by 1.
    numclients++;
    //returning reference to tail of the list.
    return tail;
}

//A function to remove a client from clients list and return list's tail.
static struct client *removeclient(struct client *top, int fd) {
    struct client *prev;
    struct client *toRm;
    struct client *next;
    char buf[1024];

    struct client *saveHead = head;

    for (toRm = top; toRm->fd != fd; toRm = toRm->next);

    if(numclients ==2){
        if(toRm->active == 1){
            sprintf(buf, "\r\n**--%s dropped. You Win!**\r\n\r\nAwaiting next opponent\r\n",toRm->name);
            sendMsg(toRm->opnt->fd, buf);
            memset(buf, '\0', sizeof(buf));

            toRm->opnt = clientReset(toRm->opnt);

            struct client *opnt = toRm->opnt;

            moveclient(opnt);
        }
    }
    else if(numclients >2){
        if(toRm->active == 1){
            sprintf(buf, "\r\n**--%s dropped. You Win!**\r\n\r\nAwaiting next opponent\r\n",toRm->name);
            sendMsg(toRm->opnt->fd, buf);
            memset(buf, '\0', sizeof(buf));

            toRm->opnt = clientReset(toRm->opnt);

            struct client *opnt = toRm->opnt;
            
            moveclient(opnt);
        }
    }

    if(toRm == head && toRm == tail){
        prev = NULL;
        next = NULL;
        tail = NULL;
        head = NULL;
    }

    else{
        if(toRm == head){
            prev = NULL;
            head = head->next;
        }
        else{
            for (prev = top; prev->next->fd != fd; prev = prev->next);
        }
        if(toRm == tail && toRm != head){
            tail = prev;
            tail->next = NULL;
            next = NULL;
        }
        else{
           if(numclients >=3 && toRm != saveHead){
                prev->next = toRm->next; 
           } 
        }
    }

    toRm->next=NULL;

    FD_CLR(toRm->fd, &allset);
    close(toRm->fd);

    sprintf(buf, "**%s leaves**\r\n",toRm->name);
    broadcast(head,buf,strlen(buf),-1);
    memset(buf, '\0', sizeof(buf));

    free(toRm);
    toRm = NULL;

    numclients--;
    
    return head;
}

//A function to find a client in a clients' list.
static struct client *findclient(struct client *top, int fd){
    struct client *p = top;

    //looping until client with fd in function is found or
    //until end of list.
    while(p->fd != fd){
        p = p->next;
        if(p == NULL){
            return NULL;
        }
    }
    //returning found client with fd=fd or NULL if not found.
    return p;
}

//A function to find an opponent for a player.
static struct client *findopponent(struct client *top, struct client *player){

    struct client *retOpnt = NULL;

    if(numclients > 1){
        if(numclients==2){
            if(head->opnt == NULL || tail->opnt == NULL){ 
                if((int)strlen(head->name) > 0 && (int)strlen(tail->name) > 0){
                    head->opnt = tail;
                    tail->opnt = head;
                    retOpnt = head;
                }
            }
            else if (head->opnt != tail || tail->opnt != head){
                if((int)strlen(head->name) > 0 && (int)strlen(tail->name) > 0){
                    head->opnt = tail;
                    tail->opnt = head;
                    retOpnt = head;
                }
            }
        }
        else if (numclients >2){
            int i = 0;
            struct client *p = head;
            if(player->fd == p->fd ){
                    p=p->next;
            }
            while(i < numclients){
                if(p != NULL && 
                    p->active == 0 && 
                    player->fd != p->fd &&
                    (int)strlen(p->name) != 0 &&
                    (int)strlen(player->name) != 0){
                    break;
                }
                if(p!= NULL){
                    p = p->next;
                }
                i++;
            }
            if(p!=NULL){
                player->opnt = p;
                p->opnt = player;
                retOpnt = p;
            }
        }
    }
    return retOpnt;
}

//A function to reset variables of a client.
static struct client *clientReset(struct client *cli){
    cli->hitpoints = 0; //make client's hit points to 0
    cli->powermoves = 0; //make client's powermoves to 0
    cli->active = 0; //make client's active to 0(unactive)
    cli->turn=0; //make client's turn to 0.

    //return client.
    return cli;
}

//A function to send message to all connected clients.
static void broadcast(struct client *top, char *s, int size, int exceptFd) {
    struct client *p;
    //broadcast only when any clients exists.
    if(numclients>0){
        //if there is any client to which message is not
        //to be sent.
        if(exceptFd != -1){
            for (p = top; p; p = p->next) {
                if(p->fd != exceptFd){
                    //write to every client except
                    //client with fd = exceptFd
                    int ret = write(p->fd, s, size);
                    if(ret <0){
                        perror("write");
                        exit(1);
                    }
                }
            }
        }
        else{
            //send message to every client connected.
            for (p = top; p; p = p->next) {
                int ret = write(p->fd, s, size);
                if(ret <0){
                    perror("write");
                    exit(1);
                }
            }
        }
    }
}

//A function to setup a game between two players.
static void setup(struct client *player){
    struct client *p = player;
    int hp = rand() % 11 + 20;
    int pm = rand() % 3 + 1 ;
    int flip = rand() % 2;

    //associate points
    p->hitpoints = hp;
    p->powermoves= pm;
    
    hp = rand() % 11 + 20;
    pm = rand() % 3 + 1 ;

    p->opnt->hitpoints = hp;
    p->opnt->powermoves= pm;


    //activate players
    p->active = 1;
    p->opnt->active = 1;

    //establish first go
    if(flip == 1){
        p->turn = 1;
        p->opnt->turn = 0;
    }
    else{
        p->turn = 0;
        p->opnt->turn = 1;
    }

    char buf[1024];

    sprintf(buf, "You engage %s!\r\nYour hitpoints: %d\r\nYour powermoves: %d\r\n\r\n%s's hitpoints: %d\r\n",
                p->opnt->name, p->hitpoints, p->powermoves, p->opnt->name, p->opnt->hitpoints);

    sendMsg(p->fd, buf);
    memset(buf, '\0', sizeof(buf));

    sprintf(buf, "You engage %s!\r\nYour hitpoints: %d\r\nYour powermoves: %d\r\n\r\n%s's hitpoints: %d\r\n", 
                p->name, p->opnt->hitpoints, p->opnt->powermoves, p->name, p->hitpoints);

    sendMsg(p->opnt->fd, buf);
    memset(buf, '\0', sizeof(buf));

    
    if(p->turn == 1){
        if(p->powermoves>0){
            sendMsg(p->fd, commandsAPS);
        }
        else{
            sendMsg(p->fd, commandsAS);
        }
        sprintf(buf, "Waiting for %s to stike...\r\n", p->name);
        sendMsg(p->opnt->fd, buf);
        memset(buf, '\0', sizeof(buf));
    }
    else if(p->opnt->turn == 1){
        if(p->opnt->powermoves>0){
            sendMsg(p->opnt->fd, commandsAPS);
        }
        else{
            sendMsg(p->opnt->fd, commandsAS);
        }
        sprintf(buf, "Waiting for %s to stike...\r\n", p->opnt->name);
        sendMsg(p->fd, buf);
        memset(buf, '\0', sizeof(buf));
    }
}

//A function to move a client to the end of client's list.
void moveclient(struct client *client){
    struct client *toMovePrev;
    struct client *toMove = client;
    struct client *toMoveNext;

    if(toMove != head && toMove != tail){
        struct client *p = head;
        while (p->next != NULL && p->next->fd != toMove->fd){
            p=p->next;
        }
        toMovePrev = p;
    }

    if(toMove == head){
        toMovePrev = NULL;
        head = head->next;
    }
    if(toMove == tail){
        //No need to move to end of list.
        tail = clientReset(tail);
    }

    if(toMove != tail){
        toMoveNext = toMove->next;
        if(toMovePrev != NULL){
            toMovePrev->next = toMoveNext;
        }
    }

    toMove->next = NULL;
    
    if(toMove != tail){
        tail->next = toMove;
        tail = toMove;
        tail = clientReset(tail);
    }
}

//A function to send a message to a client.
int sendMsg(int fd, char *msg){

    //checking for error from write function.
    if(write(fd, msg, strlen(msg)) < 0){
        perror("write");
        exit(1);
    }
    //returning zero.
    return 0;
}

//A function to execute specified user command.(i.e a, p, s)
void execute(struct client *player, char command){
    //player, p1 - when with turn
    //player, p2 = p1's opponent

    char pMsg[256]; //active player message
    char pOpntMsg[256]; //non-active player message
    int damage =0;
    int powermove=0;

    struct client *p1, *p2;
    p1 = findclient(head, player->fd);
    p2 = findclient(head, player->opnt->fd);


    int regDamage = rand() % 5 + 2;
    int powDamage = 3*regDamage;
    //int opntHitpnts = player->opnt->hitpoints;

    int pfd = player->fd;
    char pName[50];

    sprintf(pName, player->name);
    pName[strlen(pName)] = '\0';

    int php = player->hitpoints;
    int ppm = player->powermoves;

    int pOpntfd = player->opnt->fd;
    char pOpntName[50];

    sprintf(pOpntName, player->opnt->name);
    pOpntName[strlen(pOpntName)] = '\0';

    int pOpnthp = player->opnt->hitpoints;
    int pOpntpm = player->opnt->powermoves;

    if(command == 'a' && php>0){
        damage = regDamage;
        pOpnthp = pOpnthp - regDamage;
    }

    if(command == 'p' && ppm >0){
        powermove = rand() % 2;

        ppm = ppm -1;

        if(powermove == 1){
            int loss = powDamage;
            damage = loss;
            pOpnthp = pOpnthp - loss;
        }
    }

    p1->hitpoints = php;
    p1->powermoves = ppm;
    p2->hitpoints = pOpnthp;
    p2->powermoves = pOpntpm;

    if(command == 's'){
        sendMsg(pfd, "\r\nSpeak: ");

        char message[512];
        int len=0;

        char s;
        read(pfd, &s, sizeof(char));
        message[len++] = s;

        while(s != '\n'){
            read(pfd, &s, sizeof(char));
            message[len++] = s;
        }
        message[len] = '\0';

        memset(pMsg, '\0', sizeof(pMsg));
        memset(pOpntMsg, '\0', sizeof(pOpntMsg));

        sprintf(pMsg, "\r\nYou speak: %s\r\n",message);
        sprintf(pOpntMsg, "\r\n%s takes a break to tell you:\r\n%s",pName, message);
        
        p1->turn = 0;
        p2->turn = 1;
        
        sendMsg(pfd, pMsg);
        sendMsg(pOpntfd, pOpntMsg);
    }

    if(command == 'a' || command == 'p'){
        p1->turn = 0;
        p2->turn = 1;
        if(pOpnthp > 0){
            
            if(command == 'a'){
                sprintf(pMsg, "\r\nYou hit %s for %d damage!\r\n"
                        "\r\nYour hitpoints: %d\r\n"
                        "Your powermoves: %d\r\n"
                        "\r\n%s's hitpoints: %d\r\n"
                        "\r\nWaiting for %s to strike...\r\n",
                        pOpntName,damage,php,ppm,pOpntName,pOpnthp,pOpntName);

                     sprintf(pOpntMsg, "\r\n%s hits you for %d damage!\r\n"
                        "\r\nYour hitpoints: %d\r\n"
                        "Your powermoves: %d\r\n"
                        "\r\n%s's hitpoints: %d\r\n",
                        pName, damage,pOpnthp, pOpntpm, pName, php);
            }
            if(command == 'p'){
                if(powermove == 1){
                    sprintf(pMsg, "\r\nYou hit %s for %d damage!\r\n"
                        "\r\nYour hitpoints: %d\r\n"
                        "Your powermoves: %d\r\n"
                        "\r\n%s's hitpoints: %d\r\n"
                        "\r\nWaiting for %s to strike...\r\n",
                        pOpntName,damage,php,ppm,pOpntName,pOpnthp,pOpntName);

                     sprintf(pOpntMsg, "\r\n%s powermoves you for %d damage!\r\n"                
                        "\r\nYour hitpoints: %d\r\n"
                        "Your powermoves: %d\r\n"
                        "\r\n%s's hitpoints: %d\r\n",
                        pName, damage,pOpnthp, pOpntpm, pName, php);
                }
                else{
                    sprintf(pMsg, "\r\nYou missed!\r\n"
                        "\r\nYour hitpoints: %d\r\n"
                        "Your powermoves: %d\r\n"
                        "\r\n%s's hitpoints: %d\r\n"
                        "\r\nWaiting for %s to strike...\r\n",
                        php,ppm,pOpntName,pOpnthp,pOpntName);

                    sprintf(pOpntMsg, "\r\n%s missed you!\r\n"
                        "\r\nYour hitpoints: %d\r\n"
                        "Your powermoves: %d\r\n"
                        "\r\n%s's hitpoints: %d\r\n",
                        pName,pOpnthp, pOpntpm, pName, php);
                }
            }

            sendMsg(pfd, pMsg);
            sendMsg(pOpntfd, pOpntMsg);

            if(player->opnt->powermoves >0){
                sendMsg(pOpntfd, commandsAPS);
            }
            else{
                sendMsg(pOpntfd, commandsAS);
            }

        }
        else{
            memset(pMsg, '\0', sizeof(pMsg));
            memset(pOpntMsg, '\0', sizeof(pOpntMsg));
            //player with turn wins
            sprintf(pMsg, "\r\nYou hit %s for %d damage!\r\n"
                "%s gives up.You win!\r\n\r\n", pOpntName, damage, pOpntName);
            sprintf(pOpntMsg, "\r\n%s hits you for %d damage!\r\n"
                "You are no match for %s. You scurry away...\r\n\r\n",
                pName, damage,pOpntName);

            sendMsg(pfd, pMsg);
            sendMsg(pOpntfd, pOpntMsg);

            struct client *playerOpnt;
            playerOpnt = findclient(head, player->opnt->fd);
            player = clientReset(player);
            playerOpnt = clientReset(playerOpnt);
            player->opnt = playerOpnt;
            playerOpnt->opnt = player;

            moveclient(playerOpnt);
            moveclient(player);

            struct client *p1, *p2;
            p1 = findopponent(head, playerOpnt);
            if(p1 != NULL){
                setup(p1);
            }
            p2 = findopponent(head, player);
            if(p2 != NULL){
                setup(p2);
            }
            if(p1 == NULL){
                sendMsg(playerOpnt->fd, waitMsg);
            }
            if(p2 == NULL){
                sendMsg(player->fd, waitMsg);
            }
        }
    }
}

//A function to setup a server.
int bindandlisten(void) {
    struct sockaddr_in r; // to store network information.
    int listenfd; //server listing fd.

    //building a socket for server.
    if ((listenfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket");
        exit(1);
    }
    int yes = 1;
    if ((setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int))) == -1) {
        perror("setsockopt");
    }
    memset(&r, '\0', sizeof(r));
    r.sin_family = AF_INET;
    r.sin_addr.s_addr = INADDR_ANY;
    r.sin_port = htons(PORT);

    if (bind(listenfd, (struct sockaddr *)&r, sizeof r)) {
        perror("bind");
        exit(1);
    }

    //making server ready to listen for clients connections.
    if (listen(listenfd, 5)) {
        perror("listen");
        exit(1);
    }
    //return listening fd on which new clients 
    //connection are listened on.
    return listenfd;
}