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


void setInitialValues(FILE *configFile);
bool strIsEmpty(char *string);

char *optarg;
bool runTest = false, infoGet = true;
int numberSamples, numberReps;

int main(int argc, char *const *argv)
{
    FILE *users, *config;
    char portname[50] = "", baudrate[50] = "";
    char fileBuffer[50] = "";
    int opt, fd;
    
    config = fopen("config.log", "r");
    if(config == NULL) {
        printf("Couldn't open the config file, creating one\n");
        config = fopen("config.log", "w+");
        if(config == NULL) {
            printf("Couldn't create the config.log file \n");
            exit(-1);
        }
        setInitialValues(config);
    }

    fgets(fileBuffer, sizeof(fileBuffer), config);
    if(fileBuffer[0] == 'P') {
        for(int i = 6, j = 0; i < strlen(fileBuffer); i++, j++) {
            portname[j] = fileBuffer[i];
        }
    }
    fgets(fileBuffer, sizeof(fileBuffer), config);
    if(fileBuffer[0] == 'B') {
        for(int i = 6, j = 0; i < strlen(fileBuffer); i++, j++) {
            baudrate[j] = fileBuffer[i];
        }
    }

    while((opt = getopt(argc, argv, ":nd:cp:b:r:x:j:")) != -1) {
        switch(opt) {
            case 'n':
                flags.n = true;
                char *confirm;

                users = fopen("persons.txt", "a");
                if(users == NULL) {
                    printf("The persons.txt can't be opened");
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
            users = fopen("persons.txt", "r");
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
                        printf("Person founded: %s\nHeight: %s, Weight: %s", person.name, person.height, person.weight);
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
        //Receive the number of samples and the number of reps
        bool receivingData = true;
        int iterator = 0;
        do {
            unsigned char buf[80];
            int rdlen;

            rdlen = read(fd, buf, sizeof(buf) - 1);
            if (rdlen > 0) {
                buf[rdlen] = 0;
                printf("%s", buf);
                if(dataProcess(buf, &numberReps, &numberSamples))
                    iterator++;
                if(iterator == 2)
                    receivingData = false;
            } 
            else if (rdlen < 0) {
                printf("Error from read: %d: %s\n", rdlen, strerror(errno));
            } 
            else {  /* rdlen == 0 */
                printf("Timeout from read\n");
            }               
        } while (receivingData);
        
        //Prepare the files
        FILE *files[numberReps];
        char *tmpname;
        for(int i = 0; i < numberReps; i++) {
            sprintf(tmpname, "tmp%d", i);
            files[i] = fopen(tmpname, "w");
            if(files[i] == NULL) {
                printf("Error trying to create the tmp file\nError: %s", strerror(errno));
                exit(-1);
            }
        }
        //Receive all the data
        receivingData = true;
        //Receive string - 505,\n -
        for(int i = 0; i < numberReps; i++){
            //Send the ready message
            do{
                //Receive all the data and print into the file
            }while(receivingData);
        }
        //Reorganize the data base
        
        printf("Running test\n");
    }
    exit(0);
}

void setInitialValues(FILE *configFile) {
    fprintf(configFile, "Port: %s\n", DEFAULT_PORT);
    fprintf(configFile, "Baud: %s", DEFAULT_BAUDRATE);
}

bool strIsEmpty(char *string) {
    if(strcmp(string, "") == 0)
        return true;
    else
        return false;
}

bool dataProcess(char *string, int *numberReps, int *numberSamps) {
    if(string[0] == 'R'){
        if(string[1] == 'e'){
            if(string[2] == 'p') {
                if(string[3] == 's'){
                    for(int i = 5; string[i] != '!'; i++) {
                        *numberReps *= 10;
                        *numberReps = toInt(string[i]);
                    }
                }
            }
        }
    }
    else if(string[0] == 'S'){
        if(string[1] == 'a'){
            if(string[2] == 'm'){
                if(string[3] == 'p') {
                    if(string[4] == 's'){
                        for(int i = 0; string[i] != '!'; i++) {
                            *numberSamps = 10;
                            *numberSamps = toInt(string[i]);
                        }
                    }
                }
            }
        }
    }
    else {
        printf("Cannot process the data:\n%s", string);
        return false;
    }
}