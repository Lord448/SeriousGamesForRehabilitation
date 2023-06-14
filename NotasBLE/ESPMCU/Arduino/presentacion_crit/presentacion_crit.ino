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
#define SERVICE_UUID "6f466716-0681-11ee-be56-0242ac120002"

void tryReconnect(void);

uint32_t distance;
bool deviceConnected = false;
Adafruit_VL53L0X lox = Adafruit_VL53L0X();
VL53L0X_RangingMeasurementData_t measure;

BLECharacteristic distaceCharacteristic(
    "91be8cc4-0681-11ee-be56-0242ac120002",
    BLECharacteristic::PROPERTY_NOTIFY
);
BLEDescriptor distanceDescriptor(BLEUUID((uint16_t)0xFA12));

//Setup callbacks onConnect and onDisconnect
class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
        deviceConnected = true;
    };
    void onDisconnect(BLEServer* pServer) {
        deviceConnected = false;
    }
};

void setup() {
    //Procesor configs
    pinMode(LED_PIN, OUTPUT);
    Wire.begin();

    Serial.begin(115200); // Start serial communication 

    BLEDevice::init(SERVER_NAME); // Create the BLE Device

    BLEServer *pServer = BLEDevice::createServer(); // Create the BLE Server
    pServer->setCallbacks(new MyServerCallbacks());

    BLEService *ladService = pServer->createService(SERVICE_UUID); // Create the BLE Service

    // Create BLE Characteristics and Create a BLE Descriptor
    ladService->addCharacteristic(&distaceCharacteristic);
    distanceDescriptor.setValue("Ladder number");
    distaceCharacteristic.addDescriptor(&distanceDescriptor);

    ladService->start(); // Start the service

    // Start advertising
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_UUID);
    pServer->getAdvertising()->start();
    Serial.println("Waiting a client connection to notify...");

    if(!lox.begin()) {
        Serial.println("Error al iniciar el sensor");
        tryReconnect();
    }
}

void loop() {
    if(deviceConnected) {
        if(digitalRead(LED_PIN) == 0)
            digitalWrite(LED_PIN, 1);
        //Code here
        lox.rangingTest(&measure, true);
        distance = measure.RangeMilliMeter;
    }
    else {
        if(digitalRead(LED_PIN) == 1)
            digitalWrite(LED_PIN, 0);
    }
}

void tryReconnect(void) {
    for(uint32_t i = 0; i <= 5; i++) {
        delay(1500);
        if(!lox.begin()) {
            Serial.printf("Error al iniciar el sensor, intento: %d", i);
        }
        else {
            Serial.println("Sensor started");
            return;
        }
    }
    Serial.println("Could not start sensor");
    while(1);
}

/*
  lox.rangingTest(&measure, false);            // se obtiene la medicion obtenida por el sensor
  measure.RangeMilliMeter;
*/