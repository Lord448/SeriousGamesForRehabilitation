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
#include <errno.h>
#include "SerialPort.h"

#define READALL_CHUNK 2097152 //2MiB

/* Size of each input chunk to be
   read and allocate for. */
#ifndef  READALL_CHUNK
#define  READALL_CHUNK  262144
#endif

#define  READALL_OK          0  /* Success */
#define  READALL_INVALID    -1  /* Invalid parameters */
#define  READALL_ERROR      -2  /* Stream error */
#define  READALL_TOOMUCH    -3  /* Too much input */
#define  READALL_NOMEM      -4  /* Out of memory */

//Config symbols
#define MULTITABLE
#define ONE_TABLE

#define NUMBER_ROWS

#define SOFTWARE_START
#define DELETE_TMP
//#define PRINT_DATA

//Debug symbols
//#define DEBUG
//#define STAY_SERIAL_RX

#define DEFAULT_PORT "/dev/ttyUSB0"
#define DEFAULT_BAUDRATE "115200"

#define ToLowerCase(x) x+32
#define ToUpperCase(x) x-32
#define ToInt(x) x-0x30

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
    int iteration;
}person = {
    .name = "",
    .height = "",
    .weight = "",
    .iteration = 0
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
void strinsert(char *dest, const char *src);
void strclean(char *string);
int readall(FILE *in, char **dataptr, size_t *sizeptr);

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
                fprintf(users, "Name: %s, Height: %sm, Weight: %sKg, It: 0\n", person.name, person.height, person.weight);
                printf("The user has been registered\n");
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
            int lastCharOfName;
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
                        //Filling the height, the weight and the iterations
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
                            else if (personsBuffer[i] == 'I') {
                                //Fill the iteration
                                person.iteration = ToInt(personsBuffer[i+4]);
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
        int wlen;
        char buffer[50] = "";

        sendData(fd, SerialString.getreps);
        receiveData(fd, buffer);
        dataProcess(buffer, &numberReps, &numberSamples);
        strclean(buffer);
        sendData(fd, SerialString.getsamps);
        receiveData(fd, buffer);
        dataProcess(buffer, &numberReps, &numberSamples);
        printf("Repetitions: %d, Samples: %d\n", numberReps, numberSamples);
        printf("Receiving data...\n");
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
        char tmpname[50];

        for(int i = 0; i < numberReps; i++) {
            sprintf(tmpname, "CapacitanceDataSets/tmp%d.csv", i);
            files[i] = fopen(tmpname, "w");
            if(files[i] == NULL) {
                printf("Error trying to create the tmp %d file\nError: %s\n", i, strerror(errno));
                exit(-1);
            }
        }
        usleep(300*100);
        sendData(fd, SerialString.maketest);
        
        char repe[20];
        bool isNotFirst = false;
        for(int i = 0; i < numberReps; i++) {
            sprintf(repe, "R%d,", i);
            if(isNotFirst)
                fprintf(files[i], "R");
            do{
                unsigned char buf[2];
                int rdlen;
                rdlen = read(fd, buf, sizeof(buf) - 1);
                if(rdlen > 0) {
                    if(buf[0] == 'F') //End of transmission
                        break;
                    if(buf[0] == repe[0]) {
                        if(isNotFirst) 
                            break;
                        isNotFirst = true;
                    }
                #ifdef PRINT_DATA
                    printf("%s", buf);
                #endif
                    fprintf(files[i], "%s", buf);
                }
                else if (rdlen < 0)
                {
                    printf("Error from read: %d: %s\n", rdlen, strerror(errno));
                }
                else { //rdlen == 0 
                    printf("Timeout from read");
                }
            }while(1);
        }

        for(int i = 0; i < numberReps; i++)
            fclose(files[i]);
        //Reorganize the data base

#if defined(MULTITABLE) && defined(ONE_TABLE)
    #undef ONE_TABLE //Preprocesor protection
#endif

#if defined(MULTITABLE) && defined(NUMBER_ROWS)
        FILE *finalDataSet;
        char finalBuffer[100000] = "";
        char nameOfFinalDataSet[100] = "";

        for(int i = 0; i < numberReps; i++) {
            sprintf(tmpname, "CapacitanceDataSets/tmp%d.csv", i);
            files[i] = fopen(tmpname, "r");
            if(files[i] == NULL) {
                printf("Error trying to create the tmp %d file\nError: %s\n", i, strerror(errno));
                exit(-1);
            }
        }
        for(int i = 0; i < numberSamples; i++) {
            char tmp[20] = "", tmpnum[20] = "";
            char chunk[100] = "";
            sprintf(tmpnum, "%d, ", i);
            strcat(chunk, tmpnum);
            fgets(tmp, 20, files[0]);
            strcat(chunk, tmp);
            for(int j = 1; j < numberReps; j++) {
                fgets(tmp, sizeof(tmp) - 1, files[j]);
                strinsert(chunk, tmp);
            }
            strcat(finalBuffer, chunk);
            strclean(chunk);
        }
        sprintf(nameOfFinalDataSet, "CapacitanceDataSets/%s_%d.csv", person.name, person.iteration);
        finalDataSet = fopen(nameOfFinalDataSet, "w");
        if(finalDataSet == NULL) {
            printf("Error trying to create the final data set file\nError: %s\n", strerror(errno));
            exit(-1);
        }
        //Start printing the info
        fprintf(finalDataSet, "%s", finalBuffer);
        fclose(finalDataSet);
        for(int i = 0; i < numberReps; i++)
            fclose(files[i]);
#elif defined(ONE_TABLE) && defined(NUMBER_ROWS)
        FILE *finalDataSet;
        char finalBuffer[100000] = "";
        char nameOfFinalDataSet[100] = "";

        for(int i = 0; i < numberReps; i++) {
            sprintf(tmpname, "CapacitanceDataSets/tmp%d.csv", i);
            files[i] = fopen(tmpname, "r");
            if(files[i] == NULL) {
                printf("Error trying to create the tmp %d file\nError: %s\n", i, strerror(errno));
                exit(-1);
            }
        }
        for(int i = 0; i < numberReps; i++) {
            char tmp[20] = "";
            for(int j = 0; j < numberSamples; j++) {
                fgets(tmp, sizeof(tmp) - 1, files[i]);
                if(tmp[0] == 'R' || tmp[0] == 'F')
                    continue;
                strcat(finalBuffer, tmp);
            }
        }

        sprintf(nameOfFinalDataSet, "CapacitanceDataSets/%s_%d.csv", person.name, person.iteration);
        finalDataSet = fopen(nameOfFinalDataSet, "w");
        if(finalDataSet == NULL) {
            printf("Error trying to create the final data set file\nError: %s\n", strerror(errno));
            exit(-1);
        }
        //Start printing the info
        fprintf(finalDataSet, "%s", finalBuffer);
        fclose(finalDataSet);
        for(int i = 0; i < numberReps; i++)
            fclose(files[i]);
#elif defined(ONE_TABLE)
        //@todo
#elif defined(MULTITABLE)
        //@todo
        FILE *finalDataSet;
        char *finalBuffer;
        char nameOfFinalDataSet[100] = "";
#elif defined(NUMBER_ROWS)
        //@todo
        /*
        //Build the number rows
        tmpRows = fopen("CapacitanceDataSets/tmpRows.csv", "w+");
        if(tmpRows == NULL) {
            printf("Error trying to create the tmpRows file\nError: %s\n", strerror(errno));
            exit(-1);
        }
        for(int i = 1; i <= numberSamples; i++)
            fprintf(tmpRows, "%d,\n", i);
        //Build the multitable
        for(int i = 0; i < numberSamples; i++) {
            char tmp[50] = "";
            char chunk[50] = "";
            fgets(tmp, sizeof(tmp) -1, tmpRows);
            strcat(chunk, tmp);
            for(int j = 0; j < numberReps; j++) {
                fgets(tmp, sizeof(tmp) - 1, files[j]);
                strinsert(chunk, tmp);
            }
            strcat(finalBuffer, chunk);
            strclean(chunk);
        }
        */
#endif
/*
        //Write the iteration
        //char dataStream[200] = "";
        char dataSearch[200] = "";
        char finalStream[100*100] = "";

        users = fopen("data/persons.txt", "r");
        if(users == NULL) {
            printf("The persons.txt can't be opened\nError: %s", strerror(errno));
            exit(-1);
        }

        sprintf(dataSearch, "Name: %s, Height: %sm, Weight: %sKg, It: %d\n", person.name, person.height, person.weight, person.iteration);
        */
        /*
        while(feof(users) != 0) { //Search all the file
            fgets(dataStream, sizeof(dataStream) - 1, users);
            if(strcmp(dataStream, dataSearch) == 0) {
                printf("Se encontro");
                person.iteration++;
                sprintf(dataSearch, "Name: %s, Height: %sm, Weight: %sKg, It: %d\n", person.name, person.height, person.weight, person.iteration);
                strcat(finalStream, dataSearch);
                continue;
            }
            strcat(finalStream, dataStream);
        }
        */
        /*
        char *dataStream = (char *)malloc(10*sizeof(char));
        readall(users, &dataStream, sizeof(char));

        fclose(users);

        users = fopen("data/persons.txt", "w");
        if(users == NULL) {
            printf("The persons.txt can't be opened\nError: %s", strerror(errno));
            exit(-1);
        }
        fprintf(users, "%s", finalStream);
        fclose(users);
        */
#ifdef DELETE_TMP
        usleep(300*100);
        for(int i = 0; i < numberReps; i++) {
            char tmpfile[50] = "";
            sprintf(tmpfile, "CapacitanceDataSets/tmp%d.csv", i);
            remove(tmpfile);
        }
#endif
    }
    //fclose(config);
    printf("Done\n");
    exit(0);
}
/**
 * @brief Set the Initial Values of the config file
 * 
 * @param configFile: pointer to type FILE
 */
void setInitialValues(FILE *configFile) {
    fprintf(configFile, "Port: %s \n", DEFAULT_PORT);
    fprintf(configFile, "Baud: %s\n", DEFAULT_BAUDRATE);
}
/**
 * @brief Check if the string is empty
 * 
 * @param string: string that will be checked
 * @return true 
 * @return false 
 */
bool strIsEmpty(char *string) {
    if(strcmp(string, "") == 0)
        return true;
    else
        return false;
}
/**
 * @brief Process the incoming data of the serial port in order
 *        to receive the samples and the repetitions
 * @param string: Incoming data
 * @param numberReps: variable of reps
 * @param numberSamps: variable of samps
 * @return true if the process was succesful
 * @return false if it does not have success
 */
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
                        *numberReps += ToInt(string[i]);
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
                            *numberSamps += ToInt(string[i]);
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

/**
 * @brief Receive the data from the serial port
 * 
 * @param fd: integer that handle the serial port selected
 * @param string: string that will contain the buffer (80 chars)
 */
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

/**
 * @brief It blocks the program until the specified string is received
 *        from the serial port
 * @param fd: integer that handle the serial port selected
 * @param stringToFinish: string that will unblock the program
 */
void receiveDataUntil(int fd, char *stringToFinish) {
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

/**
 * @brief Send data to the serial port
 * 
 * @param fd: integer that handle the serial port selected
 * @param string: string that will be sended
 */
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
 * @brief It appends the source string into the destinatary string, and all
 *        the result in saved on the destinatary, all separated by a new line and it also finishes
 *        when a new line is founded
 * @note  The new line character is conserved at the end of the string
 * @param dest: Destinatary string
 * @param src: Source string
 */
void strinsert(char *dest, const char *src) {
    for(int i = 0; i < strlen(dest); i++) {
        if(dest[i] == '\n') {
            //printf("%d", i);
            for(int j = 0; j < strlen(src); j++) {
                if(src[j] == '\0')
                    return;
                dest[i] = src[j];
                i++;
            }
        }
    }
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

/**
 * @brief This functions reads a hole file stream and 
 *        allocates this information on a string
 * @note The buffer is allocated for one extra char, which is NUL,
 *       and automatically appended after the data.
 *       Initial values of (*dataptr) and (*sizeptr) are ignored.
 * @param in: File stream data
 * @param dataptr: points to a dynamically allocated buffer
 * @param sizeptr: chars read from the file
 * @return one of the READALL_ constants above.
 */
int readall(FILE *in, char **dataptr, size_t *sizeptr)
{
    char  *data = NULL, *temp;
    size_t size = 0;
    size_t used = 0;
    size_t n;

    /* None of the parameters can be NULL. */
    if (in == NULL || dataptr == NULL || sizeptr == NULL)
        return READALL_INVALID;
    /* A read error already occurred? */
    if (ferror(in))
        return READALL_ERROR;
    while (1) {
        if (used + READALL_CHUNK + 1 > size) {
            size = used + READALL_CHUNK + 1;

            /* Overflow check. Some ANSI C compilers
               may optimize this away, though. */
            if (size <= used) {
                free(data);
                return READALL_TOOMUCH;
            }

            temp = realloc(data, size);
            if (temp == NULL) {
                free(data);
                return READALL_NOMEM;
            }
            data = temp;
        }

        n = fread(data + used, 1, READALL_CHUNK, in);
        if (n == 0)
            break;
        used += n;
    }

    if (ferror(in)) {
        free(data);
        return READALL_ERROR;
    }

    temp = realloc(data, used + 1);
    if (temp == NULL) {
        free(data);
        return READALL_NOMEM;
    }
    data = temp;
    data[used] = '\0';

    *dataptr = data;
    *sizeptr = used;

    return READALL_OK;
}