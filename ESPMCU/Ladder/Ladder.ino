/**
 * @file      Ladder.ino
 * 
 * @author    Pedro Rojo (pedroeroca@outlook.com) 
 * 
 * @brief     It uses TTP223 capacitive touch sensors to get the position of
 *            the finger in the ladder, then it sends via BLE to the 
 *            Hungry Hamster game
 *            for more info about the connections check the local README.md file
 * 
 * @version   0.1.0
 * @date      2023-11-14
 * 
 * @copyright This Source Code Form is subject to the terms of the Mozilla Public
              License, v. 2.0. If a copy of the MPL was not distributed with this
              file, You can obtain one at https://mozilla.org/MPL/2.0/

 * @todo      Implement enhanced communication with the BLE device
 */
#include <stdio.h>
#include <string.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

//Disables the control for the BLE game and sends the strings no matter the instructions
#define TEST

//Name that will appear on the mobile device
//!Needs to be the same as declared in the game
#define SERVER_NAME "Ladder" 

//Led pin on the ESP32 board used
//It indicates if a BLE device is connected
//USE 0xFF or comment to disable it
#define LED_PIN 0xFF

//Macro to know if the led pin of the ESP32 is used
#define LED_ENABLED (LED_PIN != 0xFF && defined(LED_PIN))

//Macro to get the lenght of the touchGPIO array
#define LIMIT i < (sizeof(touchGPIO)/4)

//Cooldown time
#define COOLDOWN_TIME 1000 //Milliseconds

// See the following url for generating UUIDs:
// https://www.uuidgenerator.net/

// "UART" Service UUID
#define SERVICE_UUID           "670b1f26-0a44-11ee-be56-0242ac120002" 
// BLE RX Characteristic -- Here you receive the incoming data
#define CHARACTERISTIC_UUID_RX "006e861c-0a45-11ee-be56-0242ac120002" 
// BLE TX Characteristic -- Here you notify the GATT Client
#define CHARACTERISTIC_UUID_TX "058804de-0a45-11ee-be56-0242ac120002" 
//----------------------------------------------------------------------
//                            PROTOTYPES
//----------------------------------------------------------------------
void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic);
//----------------------------------------------------------------------
//                          GLOBAL VARIABLES
//----------------------------------------------------------------------
BLEServer *pServer = NULL; //Pointer to the BLE server class
BLECharacteristic *pTxCharacteristic; //Pointer to the TX characteristic
bool deviceConnected = false; //Indicates the status of the connection
bool oldDeviceConnected = false; //Used report the disconection or connection once
bool needToSend = false; //Used to know if we need to send data 
bool morePinsTouched = false; //Used to avoid the option to touch two steps at the same time
uint32_t touchedPin = 0xFF; //Pin that will be notified to the game
uint32_t lastPin = 0xFF; //Last pin used to send only one string per touch
char Buffer[4]; //Data buffer that will be send to the GATT Client (is constructed in the Loop code)
const uint32_t touchGPIO[] = { //GPIO Pins used in the ESP32
    23,  //Step 1 
    22,  //Step 2
    21,  //Step 3
    19,  //Step 4
    18,  //Step 5
    17,  //Step 6
    16,  //Step 7
    4,  //Step 8 
    /* Other side of the ESP32 */
    34, //Step 9   
    32, //Step 10
    33, //Step 11
    25, //Step 12
    26, //Step 13
    27, //Step 14
    14, //Step 15
    13  //Step 16
};
//----------------------------------------------------------------------
//                          RECEIVE STRINGS
//----------------------------------------------------------------------
//!Low power mode feature is not going to be implemented (The residual code will be removed on future releases)
static const char *doTransmit = "Transmit"; //Indicates that the ESP32 is allowed to send information to the GATT Client
static const char *stopTX = "Stop"; //Indicates that the ESP32 is not allowed to send data to the GATT Client
static const char *sleepTX = "Sleep"; //Indicates that the ESP32 needs to go to sleep
static const char *wakeupTX = "Wake"; //Indicates that needs to wake up
//----------------------------------------------------------------------
//                           SEND STRINGS
//----------------------------------------------------------------------
//todo
//----------------------------------------------------------------------
//                            BLE FLAGS
//----------------------------------------------------------------------
struct bleRXFlags {
    bool doTransmit; //Indicates that the ESP32 is allowed to send information to the GATT Client
    bool sleep; //Indicates that the ESP32 needs to go to sleep
}bleRXFlags;
//----------------------------------------------------------------------
//                         SERVER CALLBACKS
//----------------------------------------------------------------------
class MyServerCallbacks : public BLEServerCallbacks {
    void onConnect(BLEServer *pServer) {
        deviceConnected = true;
    };
    
    void onDisconnect(BLEServer *pServer) {
        deviceConnected = false;
    }
};
//----------------------------------------------------------------------
//                     CHARACTERISTIC CALLBACKS
//----------------------------------------------------------------------
//!Only valid for writeable characteristics
class MyCallbacks : public BLECharacteristicCallbacks {
    /**
     * @brief Receive the data that has been writed on the BLE 
     *        characteristic
     * 
     * @param pCharacteristic Writed characteristic
     */
    void onWrite(BLECharacteristic *pCharacteristic) {
        std::string rxValue = pCharacteristic -> getValue();
        if(rxValue.length() > 0) {
            Serial.print("Received Value: ");
            for (int i = 0; i < rxValue.length(); i++)
                Serial.print(rxValue[i]);
            Serial.print("\n");
            
            if(rxValue.compare(doTransmit) == 0) {
                bleRXFlags.doTransmit = true;
                Serial.println("doTransmit true");
            }
            else if(rxValue.compare(stopTX) == 0) {
                bleRXFlags.doTransmit = false;
                Serial.println("doTransmit false");    
            }
            else if(rxValue.compare(sleepTX) == 0) {
                bleRXFlags.sleep = true;
                Serial.println("sleep true");
            }
            else if(rxValue.compare(wakeupTX) == 0) {
                bleRXFlags.sleep = false;
                Serial.println("sleep false");
            }
            else
                Serial.println("Data not Handled");
        }
    }
};
//----------------------------------------------------------------------
//                              SETUP
//----------------------------------------------------------------------
void setup()
{
    //Init the UART communication
    Serial.begin(115200);

#ifndef TEST
    //Init flags structure
    bool *pFlags = (bool *) &bleRXFlags;
    for(uint32_t i = 0; i < sizeof(bleRXFlags); i++, pFlags++)
        *pFlags = false;
#endif

    //Config Pins
    for(uint32_t i = 0; LIMIT; i++)
        pinMode(touchGPIO[i], INPUT);

#if LED_ENABLED
    pinMode(LED_PIN, OUTPUT);
#endif

    // Create the BLE Device
    BLEDevice::init(SERVER_NAME);

    // Create the BLE Server
    pServer = BLEDevice::createServer();
    pServer->setCallbacks(new MyServerCallbacks());

    // Create the BLE Service
    BLEService *pService = pServer->createService(SERVICE_UUID);

    // Create a BLE Characteristic
    pTxCharacteristic = pService->createCharacteristic(
                                        CHARACTERISTIC_UUID_TX,
                                        BLECharacteristic::PROPERTY_NOTIFY
                                    );
                        
    pTxCharacteristic->addDescriptor(new BLE2902());

    BLECharacteristic *pRxCharacteristic = pService->createCharacteristic(
                                                CHARACTERISTIC_UUID_RX,
                                            BLECharacteristic::PROPERTY_WRITE
                                        );

    pRxCharacteristic->setCallbacks(new MyCallbacks());

    // Start the service
    pService->start();

    // Start advertising
    pServer->getAdvertising()->start();
    Serial.println("Waiting a client connection to notify...");
}
//----------------------------------------------------------------------
//                                LOOP
//----------------------------------------------------------------------
void loop()
{
    static int time = millis();
    needToSend = false;
    morePinsTouched = false;

    //GPIO Read
	for(uint32_t i = 0; LIMIT; i++) {
        //Fetching pressions
        if(digitalRead(touchGPIO[i]) == 1) {
            //A touch pad has been pressed
            if(morePinsTouched || lastPin == i) {
                //Discard the event
                touchedPin = 0xFF;
                needToSend = false;
                break;
            }
            touchedPin = i;
            needToSend = true;
            morePinsTouched = true;
        }
    }

    //Sending information
#if defined(COOLDOWN_TIME) && COOLDOWN_TIME != 0
    if(needToSend && touchedPin != 0xFF && (millis()-time) > COOLDOWN_TIME) { 
#else
    if(needToSend && touchedPin != 0xFF) {
#endif
        Serial.printf("Touch send: T%d\n", touchedPin);
        //The pin is touched, its a diferent pin and cooldown has passed
        sprintf(Buffer, "T:%d\n", touchedPin); //Bulding the string
#ifndef TEST
        if(deviceConnected && bleRXFlags.doTransmit)
#else
        if(deviceConnected)
#endif
        {
            sendStringData(Buffer, pTxCharacteristic); //Sending to the GATT Client
        }
        Serial.println(Buffer);
        lastPin = touchedPin;
        time = millis();
    }
    
    
    // disconnecting
    if (!deviceConnected && oldDeviceConnected) {
#if LED_ENABLED
        if(digitalRead(LED_PIN) == 1)
            digitalWrite(LED_PIN, 0);
#endif 
        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("Start advertising");
        oldDeviceConnected = deviceConnected;
    }

    // connecting
    if (deviceConnected && !oldDeviceConnected) {
		// do stuff here on connecting
#if LED_ENABLED
        digitalWrite(LED_PIN, 1);
#endif
        oldDeviceConnected = deviceConnected;
    }
}
//----------------------------------------------------------------------
//                           BLE SEND FUNCTIONS
//----------------------------------------------------------------------
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