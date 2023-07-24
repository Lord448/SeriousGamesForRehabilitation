/**
 * @file senseCapacitive.ino
 * @author Pedro Rojo (pedroeroca@outlook.com); Lord448 @ github.com
 * @brief 
 * @version 0.1
 * @date 2023-06-08
 * @copyright Copyright (c) 2023
 */

#include <stdio.h>
#include <string.h>

#define touchPin 4 //GPIO number 4 in board
#define buttonPin 5 //GPIO number 5 in board
#define ledPin 2 //Internal led on board

#define CSV_FORMAT //Deploy the data in CSV format
//#define BUTTON_START //Set up a button in pull up mode for start the data acquire

#define NumberOfRepetitions 5
#define NumberOfSamples 500

struct SerialString{
    std::string reset;
    std::string start;
    std::string ready;
    std::string getreps;
    std::string getsamps;
    std::string makeTest;
}SerialString = {
    .reset = "reset",
    .start = "start",
    .ready = "ready",
    .getreps = "getreps",
    .getsamps = "getsamps",
    .makeTest = "maketest"
};

void strclean(char *string);

uint32_t touchValue;
char buffer[100];
char c;

void setup()
{
    Serial.begin(115200);
    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, 0); //It turns down with logic one
#ifdef BUTTON_START
    pinMode(buttonPin, INPUT_PULLUP);
    //Press to start
    while(digitalRead(buttonPin)); //The pin it's on high by default
    Serial.print("start");
#else
    do{
        Serial.read(buffer, sizeof(buffer));
    }while(SerialString.ready.compare(buffer));
#endif
    digitalWrite(ledPin, 1);
    Serial.print("start");
}

void loop()
{
#ifdef CSV_FORMAT
    if(Serial.available() != 0) {            

        Serial.read(buffer, sizeof(buffer));

        if(strcmp(buffer, "getreps") == 0) //Get reps
            Serial.printf("Reps:%d!\n", NumberOfRepetitions);
        else if (strcmp(buffer, "getsamps") == 0) //Get samps
            Serial.printf("Samps:%d!\n", NumberOfSamples);
        else if(strcmp(buffer, "reset") == 0)
            ESP.restart();
        else if(strcmp(buffer, "maketest") == 0)
            makeTest();
        strclean(buffer);
    }
#else
    touchValue = touchRead(touchPin);
    Serial.printf("%d\n", touchValue);
#endif
}

void makeTest(void) {
    for(uint32_t i = 0; i < NumberOfRepetitions; i++) {
        Serial.printf("R%d,\n", i);
        delay(100);
        for(uint32_t j = 0; j < NumberOfSamples; j++) {
            touchValue = touchRead(touchPin);
            Serial.printf("%d,\n", touchValue);
            //delayMicroseconds(1500);
            //delay(50);
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