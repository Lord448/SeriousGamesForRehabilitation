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

char *optarg;
bool runTest = false;
int numberSamples, numberReps;

int main(int argc, char *const *argv)
{
    FILE *users, *config;
    char portname[50] = "", baudrate[50] = "";
    char fileBuffer[50] = "";
    int opt;
    int fd;
    int wlen;
    
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
                fprintf(users, "Name: %s, Height: %sm, Weight: %sKg \n", person.name, person.height, person.weight);
                fclose(users);
                printf("The user has been registered\n");
                printf("Do you want to do the test? ");
                scanf("%s", confirm);
                if(confirm[0] == 's' || confirm[0] == 'y' || confirm[0] == 'S' || confirm[0] == 'Y')
                    runTest = true;
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
        /*
        @todo
        Search for the information of the person
        Connect to the serial port
        Receive the number of samples and the number of reps
        Receive all the data
        Reorganize the data base
        */

        printf("Running test");
    }

    exit(0);
}

void setInitialValues(FILE *configFile) {
    fprintf(configFile, "Port: %s\n", DEFAULT_PORT);
    fprintf(configFile, "Baud: %s", DEFAULT_BAUDRATE);
}