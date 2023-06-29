/**
 * @file respirador.ino
 * @author Pedro Rojo (pedroeroca@outlook.com); Lord448 @ github.com
 * @brief 
 * @version 0.1
 * @date 2023-06-08
 * @copyright Copyright (c) 2023
 */
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <Wire.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include "Adafruit_VL53L0X.h"

#define SERVER_NAME "Globo"
#define LED_PIN 2

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID           "526dd52e-0d80-11ee-be56-0242ac120002" // UART service UUID
#define CHARACTERISTIC_UUID_RX "5561bdb8-0d80-11ee-be56-0242ac120002"
#define CHARACTERISTIC_UUID_TX "583e0dfc-0d80-11ee-be56-0242ac120002"

struct RxBuffers {
    std::string maxLimIn;
    std::string minLimIn;
}RxBuffers = {
    .maxLimIn = "Max",
    .minLimIn = "Min"
};

struct TxBuffers {
    std::string OK;
}TxBuffers = {
    .OK = "GLOK"
};

void tryReconnect(void);
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic);
void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);

BLEServer *pServer = NULL;
BLECharacteristic *pTxCharacteristic;
Adafruit_VL53L0X lox = Adafruit_VL53L0X();
VL53L0X_RangingMeasurementData_t measure;
uint32_t distance;
uint32_t minLim = 30, maxLim = 75;
bool deviceConnected = false;
bool oldDeviceConnected = false;
char buffer[5];

//Setup callbacks onConnect and onDisconnect
class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
        deviceConnected = true;
    };
    void onDisconnect(BLEServer* pServer) {
        deviceConnected = false;
    }
};

class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
        bool maxFill = false;
        bool minFill = false;
        std::string RxBuffer;
        std::string rxValue = pCharacteristic->getValue();

        if (rxValue.length() > 0) {
            Serial.print("Received Value: ");
            for (int32_t i = 0, j = 0; i < rxValue.length(); i++, j++) {
                Serial.print(rxValue[i]);
                if(i == 2){
                    j = 0;
                    if(RxBuffer.compare(RxBuffers.maxLimIn) == 0)
                        maxFill = true;
                    else
                        maxFill = false;
                    if(RxBuffer.compare(RxBuffers.minLimIn) == 0)
                        minFill = true;
                    else 
                        minFill = false;
                }
                else if(minFill || maxFill) {
                    RxBuffer[j] = rxValue[j];
                }
                else
                    RxBuffer[i] = rxValue[i];

            }
            /*
            if(minFill) {
                minLim = std::stoi(RxBuffer);
                Serial.printf("New value of min: %d", minLim);
            }
                
            else if (maxFill) {
                maxLim = std::stoi(RxBuffer);
                Serial.printf("New value of max: %d", maxLim);
            }

            */
            if(rxValue.compare("TRY") == 0) {
                sendStringData("GLOK", pTxCharacteristic);
            }
                
        }
    }
};

void setup() {
    //Procesor configs
    pinMode(LED_PIN, OUTPUT);

    //SDA GPIO 21
    //SCL GPIO 22
    Wire.begin();

    Serial.begin(115200); // Start serial communication 

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

    if(!lox.begin()) {
        Serial.println("Error trying to start sensor");
        tryReconnect();
    }
}

void loop() {
    if(deviceConnected) {
        if(digitalRead(LED_PIN) == 0)
            digitalWrite(LED_PIN, 1);
        //Code here
        lox.rangingTest(&measure, false);
        distance = measure.RangeMilliMeter;
        #ifdef ONLY_RANGE
        if(distance >= minLim && distance <= maxLim) {
            Serial.println(distance);
            sprintf(buffer, "%d", distance);
            sendStringData(buffer, pTxCharacteristic);
        }
        #else
        Serial.println(distance);
        if(distance < 390) {
          sprintf(buffer, "%d", distance);
          sendStringData(buffer, pTxCharacteristic);
        }
        #endif
    }
    // disconnecting
    if (!deviceConnected && oldDeviceConnected) {
        if(digitalRead(LED_PIN) == 1)
            digitalWrite(LED_PIN, 0);
        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("start advertising");
        oldDeviceConnected = deviceConnected;
    }
    // connecting
    if (deviceConnected && !oldDeviceConnected) {
		// do stuff here on connecting
        oldDeviceConnected = deviceConnected;
    }
}

void tryReconnect(void) {
    for(uint32_t i = 0; i <= 5; i++) {
        delay(1500);
        if(!lox.begin()) {
            Serial.printf("Trying to start sensor number: %d \r\n", i);
        }
        else {
            Serial.println("Sensor started");
            return;
        }
    }
    Serial.println("Could not start sensor");
    while(1);
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
 * @brief Send data to the desired BLE characteristic in UTF-8
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
