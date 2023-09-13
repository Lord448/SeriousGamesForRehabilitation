#include <stdio.h>
#include <string.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define SERVER_NAME "Ladder"
#define LED_PIN 2
#define NUMBER_TOUCH_PINS 32

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID           "670b1f26-0a44-11ee-be56-0242ac120002" // UART service UUID
#define CHARACTERISTIC_UUID_RX "006e861c-0a45-11ee-be56-0242ac120002"
#define CHARACTERISTIC_UUID_TX "058804de-0a45-11ee-be56-0242ac120002"

uint32_t touchGPIO[NUMBER_TOUCH_PINS];

void setup()
{
    
}

void loop()
{
	
}

/**
 * @brief Send data to the desired BLE characteristic in UTF-8
 * @note  a string finisher is sent at the end of the transmit
 * @param buffer String data to send 
 * @param pTXCharacteristic Characteristic that will be modified
 */
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic) {
    pTXCharacteristic -> setValue((uint8_t *)buffer, strlen(buffer));
    pTXCharacteristic -> notify();
    delay(10); // bluetooth stack will go into congestion, if too many packets are sent, 10ms min
}

/**
 * @brief Send data char by char to the desired BLE characteristic in UTF-8
 * @note  a string finisher is sent at the end of the transmit
 * @param buffer String data to send 
 * @param pTXCharacteristic Characteristic that will be modified
 */
void sendData(char *buffer, BLECharacteristic *pTXCharacteristic) {
    char charTX;
    for(uint32_t i = 0; i <= strlen(buffer); i++) {
        if(i != strlen(buffer))
            charTX = buffer[i];
        else
            charTX = '\n';
        pTXCharacteristic -> setValue((uint8_t *)&charTX, sizeof(uint8_t));
        pTXCharacteristic -> notify();
        delay(15); // bluetooth stack will go into congestion, if too many packets are sent, 10ms min
    }
}