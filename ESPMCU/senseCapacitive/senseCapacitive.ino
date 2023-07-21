/**
 * @file senseCapacitive.ino
 * @author Pedro Rojo (pedroeroca@outlook.com); Lord448 @ github.com
 * @brief 
 * @version 0.1
 * @date 2023-06-08
 * @copyright Copyright (c) 2023
 */

#define touchPin 4 //GPIO number 4 in board

//Uncomment this Symbol to deploy the data in csv fiel format
#define CSV_FORMAT

#define NumberOfRepetitions 5
#define NumberOfSamples 500

uint32_t touchValue;

void setup()
{
    Serial.begin(115200);
    
    Serial.printf("Reps:%d", NumberOfRepetitions);

    #ifdef CSV_FORMAT
    for(uint32_t i = 0; i < NumberOfRepetitions; i++) {
        for(uint32_t j = 0; j < NumberOfSamples; j++) {

        }
    }
    #else
    while(1) {
        touchValue = touchRead(touchPin);
    }
    #endif
}
