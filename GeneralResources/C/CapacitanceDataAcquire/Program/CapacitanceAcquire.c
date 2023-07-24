/**
 * @file CapacitanceAcquire.c
 * @author Pedro Rojo (pedroeroca@outlook.com) - Lord448 @ github
 * @note  This Source Code Form is subject to the terms of the Mozilla Public
  		  License, v. 2.0. If a copy of the MPL was not distributed with this
  		  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * @brief 
 * @version 0.1.0
 * @date 2023-07-20
 * @copyright Copyright (c) 2023
 * 
 */

/**
 * Options:
 * n: new dataset (-n)
 * d: delete dataset (-d person_to_delete)
 * c: clear all (-c)
 * p: port to open (-p /dev/ttyUSB0)
 * b: baudrate (-b 115200)
 * r: register default port (-r /dev/ttyUSB0)
 * x: execute test: person that will have the test (-x person)
 * j: just register data, cancel closing the program (-j name_of_register)
 *    it creates the data base if it doesn't exists
 */

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <ctype.h>
#include "SerialPort.h"

//#define STAY_SERIAL_RX
#define SOFTWARE_START
//#define DEBUG

#define DEFAULT_PORT "/dev/ttyUSB0"
#define DEFAULT_BAUDRATE "115200"

#define ToLowerCase(x) x+32
#define ToUpperCase(x) x-32
#define toInt(x) x-0x30

typedef enum bool {
    false,
    true
}bool;

struct flags{
    bool n;
    bool d;
    bool c;
    bool p;
    bool b;
    bool r;
    bool x;
    bool j;
}flags = {
    .n = false,
    .d = false,
    .c = false,
    .p = false,
    .b = false,
    .r = false,
    .x = false,
    .j = false
};

struct persons
{
    char name[50];
    char height[50];
    char weight[50];
}person = {
    .name = "",
    .height = "",
    .weight = ""
};

struct SerialString{
    char reset[6];
    char start[7];
    char ready[7];
    char getreps[8];
    char getsamps[9];
    char maketest[9];
}SerialString = {
    .reset = "reset",
    .start = "start",
    .ready = "ready",
    .getreps = "getreps",
    .getsamps = "getsamps",
    .maketest = "maketest"
};

bool strIsEmpty(char *string);
bool dataProcess(char *string, int *numberReps, int *numberSamps);
void setInitialValues(FILE *configFile);
void receiveData(int fd, char *string);
void receiveDataUntil(int fd, char *stringToFinish);
void sendData(int fd, char *string);
void strclean(char *string);

char *optarg;
bool runTest = false, infoGet = true;
int numberSamples = 0, numberReps = 0;

int main(int argc, char *const *argv)
{
    FILE *users, *config;
    char portname[50] = "", baudrate[50] = "";
    char fileBuffer[50] = "";
    int opt, fd;
    
    config = fopen("data/config.log", "r");
    if(config == NULL) {
        printf("Couldn't open the config file, creating one\n");
        config = fopen("data/config.log", "w+");
        if(config == NULL) {
            printf("Couldn't create the config.log file \n");
            exit(-1);
        }
        setInitialValues(config);
    }

    fgets(fileBuffer, sizeof(fileBuffer), config);
    if(fileBuffer[0] == 'P') {
        for(int i = 6, j = 0; fileBuffer[i] != ' '; i++, j++) {
            portname[j] = fileBuffer[i];
        }
    }
    fgets(fileBuffer, sizeof(fileBuffer), config);
    if(fileBuffer[0] == 'B') {
        for(int i = 6, j = 0; i < (int)strlen(fileBuffer); i++, j++) {
            baudrate[j] = fileBuffer[i];
        }
    }

    while((opt = getopt(argc, argv, ":nd:cp:b:r:x:j:")) != -1) {
        switch(opt) {
            case 'n':
                flags.n = true;
                char confirm[10];

                users = fopen("data/persons.txt", "a");
                if(users == NULL) {
                    printf("The persons.txt can't be opened\n");
                    exit(-1);
                }
                printf("Name: ");
                scanf("%s", person.name);
                printf("Height in meters: ");
                scanf("%s", person.height);
                printf("Weight in kg: ");
                scanf("%s", person.weight);
                fprintf(users, "Name: %s, Height: %sm, Weight: %sKg\n", person.name, person.height, person.weight);
                printf("The user has been registered\n");
                printf("Do you want to do the test? ");
                scanf("%s", confirm);
                if(confirm[0] == 's' || confirm[0] == 'y' || confirm[0] == 'S' || confirm[0] == 'Y')
                    runTest = true;
                fclose(users);
            break;
            case 'd':
                //@todo
                printf("Function not implemented");
            break;
            case 'c':
                //@todo
                printf("Function not implemented");
            break;
            case 'p':
                flags.p = true;
                strcpy(portname, optarg);
                printf("Selected Port: %s\n", portname);
            break;
            case 'b':
                char tmpBuffer[15];
                flags.b = true;
                strcpy(tmpBuffer, optarg);
                for(int i = 0; i < strlen(tmpBuffer); i++) {
                    if(tmpBuffer[i] > 0x39 || tmpBuffer[i] < 0x30) {
                        printf("Please select a valid value\n");
                        exit(-1);
                    }
                }
                strcpy(baudrate, optarg);
                printf("Selected baudrate: %s\n", baudrate);
            break;
            case 'r':
                //@todo
                printf("Function not implemented");
            break;
            case 'x':
                if(flags.n)
                    break;
                flags.x = true;
                strcpy(person.name, optarg);
                runTest = true;
            break;
            case 'j':
                //@todo
                printf("Function not implemented");
            break;
        }
    }

    if(runTest) {
        char personsBuffer[100] = "";
        char nameSearching[50] = "";

        //Search for the information of the person
        if(!flags.n || strIsEmpty(person.name) || strIsEmpty(person.height) || strIsEmpty(person.weight)) {
            int lastCharOfName, lastCharOfHeight;
            users = fopen("data/persons.txt", "r");
            if(users == NULL) {
                printf("Could not open the file: persons.txt");
                exit(-1);
            }
            while(infoGet) {
                fgets(personsBuffer, 100, users);
                if(personsBuffer[0] == 'N') {
                    //Searching in all the list of the names
                    for(int i = 0; i < strlen(personsBuffer); i++) {
                        if(personsBuffer[i] == ',') {
                            //fill the name on the person
                            for(int j = 6, k = 0; personsBuffer[j] != ','; j++, k++) {
                                nameSearching[k] = personsBuffer[j];
                                lastCharOfName = i;
                            }
                            break;
                        }
                    }
                    if(strcmp(nameSearching, person.name) == 0) {
                        //Filling the height and the weight
                        for(int i = lastCharOfName+1; i < strlen(personsBuffer); i++) {
                            if(personsBuffer[i] == 'H') {
                                //Fill the height
                                for(int j = i+7, k = 0; personsBuffer[j] != ','; j++, k++) {
                                    person.height[k] = personsBuffer[j];
                                }
                                    
                            }
                            else if(personsBuffer[i] == 'W') {
                                //Fill the weight
                                for(int j = i+7, k = 0; j < strlen(personsBuffer); j++, k++) {
                                    person.weight[k] = personsBuffer[j];
                                }
                                infoGet = false;
                            }
                        }
                        printf("Person founded: %s\nHeight:%s, Weight:%s", person.name, person.height, person.weight);
                    }
                }
            }
            fclose(users);
        }
        //Set up the connection to the serial port
        fd = open(portname, O_RDWR | O_NOCTTY | O_SYNC);
        if (fd < 0) {
            printf("Error opening %s: %s\n", portname, strerror(errno));
            exit(-1);
        }
        set_interface_attribs(fd, B115200); //baudrate 115200, 8 bits, no parity, 1 stop bit

        //Wait for get a string of ready
#ifdef SOFTWARE_START
            sendData(fd, SerialString.reset);
            usleep(300*100);
            tcflush(fd, TCIOFLUSH);
            printf("Press intro to continue");
            getchar();
            sendData(fd, SerialString.ready);
            receiveDataUntil(fd, SerialString.start);
#else
            printf("Waiting to confirm");
            receiveDataUntil(fd, SerialString.start);
#endif //!SOFTWARE_START
        //Receive the number of samples and the number of reps
        bool receivingData = true;
        int iterator = 0, wlen;
        char buffer[50] = "";

        sendData(fd, SerialString.getreps);
        receiveData(fd, buffer);
        dataProcess(buffer, &numberReps, &numberSamples);
        strclean(buffer);
        sendData(fd, SerialString.getsamps);
        receiveData(fd, buffer);
        dataProcess(buffer, &numberReps, &numberSamples);
        printf("Repetitions: %d, Samples: %d\n", numberReps, numberSamples);

#ifdef STAY_SERIAL_RX
        do {
            unsigned char buf[80];
            int rdlen;

            rdlen = read(fd, buf, sizeof(buf) - 1);
            if (rdlen > 0) {
                buf[rdlen] = 0;
                printf("%s", buf);
            } 
            else if (rdlen < 0) {
                printf("Error from read: %d: %s\n", rdlen, strerror(errno));
            } 
            else {  /* rdlen == 0 */
                printf("Timeout from read\n");
            }               
        } while (1);
#endif //!STAY_SERIAL_RX

        //Prepare the files
        FILE *files[numberReps];
        FILE *finalDataSet;
        char tmpname[100];

        for(int i = 0; i < numberReps; i++) {
            sprintf(tmpname, "CapacitanceDataSets/tmp%d.csv", i);
            files[i] = fopen(tmpname, "w");
            if(files[i] == NULL) {
                printf("Error trying to create the tmp %d file\nError: %s\n", i, strerror(errno));
                exit(-1);
            }
        }

        //usleep(300*100);
        //tcflush(fd, TCIOFLUSH);
        usleep(300*100);
        sendData(fd, SerialString.maketest);
        
        char repe[20];
        for(int i = 0; i < numberReps; i++) {
            sprintf(repe, "Rep:%d,", i);
            do{
                unsigned char buf[2];
                int rdlen;

                rdlen = read(fd, buf, sizeof(buf) - 1);
                if(rdlen > 0) {
                    if(strcmp(buf, repe) == 0) {
                        printf("Next rep");
                        break;
                    }
                    else {
                        printf("%s", buf);
                        fprintf(files[i], "%s", buf);
                    }
                }
                else if (rdlen < 0)
                {
                    printf("Error from read: %d: %s\n", rdlen, strerror(errno));
                }
                else { /* rdlen == 0 */
                    printf("Timeout from read");
                }
            }while(1);
        }

        //Reorganize the data base

        //Add number of prooves

        for(int i = 0; i < numberReps; i++)
            fclose(files[i]);
    }
    //fclose(config);
    exit(0);
}

void setInitialValues(FILE *configFile) {
    fprintf(configFile, "Port: %s \n", DEFAULT_PORT);
    fprintf(configFile, "Baud: %s\n", DEFAULT_BAUDRATE);
}

bool strIsEmpty(char *string) {
    if(strcmp(string, "") == 0)
        return true;
    else
        return false;
}

bool dataProcess(char *string, int *numberReps, int *numberSamps) {
#ifdef DEBUG
    printf("Incoming string to process %s\n", string);
#endif //!DEBUG
    if(string[0] == 'R'){
        if(string[1] == 'e'){
            if(string[2] == 'p') {
                if(string[3] == 's'){
                    for(int i = 5; string[i] != '!'; i++) {
                        *numberReps *= 10;
                        *numberReps += toInt(string[i]);
                    }
#ifdef DEBUG
                    printf("Received Reps %d\n", *numberReps);
#endif //!DEBUG
                    return true;
                }
            }
        }
    }
    else if(string[0] == 'S'){
        if(string[1] == 'a'){
            if(string[2] == 'm'){
                if(string[3] == 'p') {
                    if(string[4] == 's'){
                        for(int i = 6; string[i] != '!'; i++) {
                            *numberSamps *= 10;
                            *numberSamps += toInt(string[i]);
                        }
#ifdef DEBUG
                        printf("Received Samps %d\n", *numberSamps);
#endif //!DEBUG
                        return true;
                    }
                }
            }
        }
    }
    else {
        printf("Cannot process the data:\n%s", string);
        return false;
    }
    return true;
}

void receiveData(int fd, char *string) {
    bool receivingData = true;
    do {
        unsigned char buf[80];
        int rdlen;

        rdlen = read(fd, buf, sizeof(buf) - 1);
        if (rdlen > 0) {
            buf[rdlen] = 0;
#ifdef DEBUG
            printf("Data res: %s\n", buf);
#endif //!DEBUG
            strcpy(string, buf);
            receivingData = false;
        } 
        else if (rdlen < 0) {
            printf("Error from read: %d: %s\n", rdlen, strerror(errno));
        } 
        else {  /* rdlen == 0 */
            printf("Timeout from read\n");
        }               
    } while(receivingData);
}

void receiveDataUntil(int fd, char *stringToFinish) {
    bool receivingData = true;
    bool foundS = false;
    int sume = 0, counter = 0;
    do {
        unsigned char buf[80];
        int rdlen;

        rdlen = read(fd, buf, sizeof(buf) - 1);
        if (rdlen > 0) {
            buf[rdlen] = 0;
#ifdef DEBUG
            printf("Data res: %s\n", buf);
#endif //!DEBUG
            if(strcmp(buf, stringToFinish) == 0) { //Sume of all chars of start
                printf("Starting\n");
                receivingData = false;
            }
        } 
        else if (rdlen < 0) {
            printf("Error from read: %d: %s\n", rdlen, strerror(errno));
        } 
        else {  /* rdlen == 0 */
            printf("Timeout from read\n");
        }               
    } while (receivingData);
}

void sendData(int fd, char *string) {
#ifdef DEBUG
    printf("Sending: %s\n", string);
#endif //!DEBUG
    int wlen, len;
    len = strlen(string);
    wlen = write(fd, string, len);
    if(wlen != len) {
        printf("Error from write: %d, %d\n", wlen, errno);
    }
    tcdrain(fd); //delay for output
}

/**
 * @brief Cleans the string with the termination character
 * @note It only cleans till the termination character of the string
 *       because it uses strlen to calculate the lenght of the string
 * @param string: String tha will be cleaned
 */
void strclean(char *string) {
    int lenght = strlen(string);
    for(int i = 0; i < lenght; i++) {
        string[i] = '\0';
    }
}