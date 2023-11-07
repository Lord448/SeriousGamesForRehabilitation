#include <Arduino.h>
#line 1 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
#include <stdio.h>
#include <string.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <MPU6050_light.h>
#include "Wire.h"

//Sends the data no matter if the game is requesting information
//!More battery consume if defined
#define TEST

#define WORKING_AXYS_X
//#define WORKING_AXYS_Y
//#define WORKING_AXYS_Z

//#define UPSIDEDOWN_MOUNT

#define SERVER_NAME "ShoulderWheel"
#define LED_PIN 2
#define ANALOG_PIN 4

#define HYSTERESYS 10

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID           "0a6131ee-7c3a-11ee-b962-0242ac120002" // UART service UUID
#define CHARACTERISTIC_UUID_RX "0f16c6ae-7c3a-11ee-b962-0242ac120002"
#define CHARACTERISTIC_UUID_TX "131552ca-7c3a-11ee-b962-0242ac120002"

void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic);
void fatalError(void);

BLEServer *pServer = NULL;
BLECharacteristic *pTxCharacteristic;
MPU6050 mpu(Wire);
float rawAngle, pastAngle = 0;
float angle;
bool valueIsDiff = false;
bool deviceConnected = false;
bool oldDeviceConnected = false;
static char Buffer[16];

//Receive Strings
static const char *doTransmit = "Transmit";
static const char *stopTX = "Stop";
static const char *sleepTX = "Sleep";
static const char *wakeupTX = "Wake";
static const char *Reset = "Reset";
static const char *MPUStartCal = "MPUStartCal"; //Order to calibrate MPU
//Send Strings
static char *MPUError = "MPUError";
static char *ResetSend = "ResetESP32";
static char *MPUCal = "MPUCal"; //No Move MPU
static char *MPUReady = "MPUReady";

struct bleRXFlags {
    bool doTransmit;
    bool sleep;
    bool mpuCal;
}bleRXFlags;

class MyServerCallbacks : public BLEServerCallbacks {
    void onConnect(BLEServer *pServer) {
        deviceConnected = true;
    };

    void onDisconnect(BLEServer *pServer) {
        deviceConnected = false;
    }
};

class MyCallbacks : public BLECharacteristicCallbacks {
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
            else if(rxValue.compare(MPUStartCal) == 0) {
                bleRXFlags.mpuCal = true;
                Serial.println("mpuCal true");
            }
            else if(rxValue.compare(Reset) == 0) {
                Serial.println("Reseting ESP32");
                sendStringData(ResetSend, pTxCharacteristic);
                ESP.restart();
            }
            else
                Serial.println("Data not Handled");
        }
    }
};

#line 116 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void setup();
#line 166 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void loop();
#line 216 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void getData(float *read);
#line 232 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void scaleData(float *value, float read);
#line 270 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void mpuCalc(void);
#line 116 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void setup()
{
    Serial.begin(115200);
    Wire.begin();
#ifndef TEST
    //Init flags structure
    bool *pFlags = (bool *) &bleRXFlags;
    for(uint32_t i = 0; i < sizeof(bleRXFlags); i++, pFlags++)
        *pFlags = false;
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
    
    byte status = mpu.begin();

    if(status != 0)
        fatalError();   
    mpuCalc();
}

void loop()
{
	getData(&rawAngle);

    valueIsDiff = 
    (rawAngle > (pastAngle + HYSTERESYS) || rawAngle < (pastAngle - HYSTERESYS))
    && (rawAngle != pastAngle);

    if (valueIsDiff) {
        scaleData(&angle, rawAngle);

#ifndef TEST
        if(deviceConnected && bleRXFlags.doTransmit)
#else
        if(deviceConnected)
#endif
        {
            sprintf(Buffer, "Angle:%3.2f\n", angle);
            sendStringData(Buffer, pTxCharacteristic);
        }
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
		if(digitalRead(LED_PIN) == 0)
            digitalWrite(LED_PIN, 1);
        oldDeviceConnected = deviceConnected;
    }
    if(bleRXFlags.mpuCal) {
        mpuCalc();
        bleRXFlags.mpuCal = false;
    }
    pastAngle = rawAngle;
}

/**
 * @brief Getting the mean value of the lectures (6 Lectures)
 * 
 * @param read: Value where is saved the mean
 */
void getData(float *read) {
    float lectures = 0;
    for(uint32_t i = 0; i < 6; i++) {
#ifdef WORKING_AXYS_X
        lectures += mpu.getAngleX();
#elif defined(WORKING_AXYS_Y)
        *lectures += mpu.getAngleY();
#elif defined(WORKING_AXYS_Z)
        *lectures += mpu.getAngleZ();
#else
        *lectures += mpu.getAngleX();
#endif
    }
    *read = lectures/6;
}

void scaleData(float *value, float read) {
    *value = read+360;
#ifdef TEST
    Serial.printf("rawAngle: %3.2f, Angle: %3.2f\n", rawAngle, *value);
#endif
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

void mpuCalc(void) {
    sendStringData(MPUCal, pTxCharacteristic);
    Serial.println("MPU About to calibrate, no move");
    delay(1000);
#ifdef UPSIDEDOWN_MOUNT
    mpu.upsideDownMounting = true;
#endif
    mpu.calcOffsets(); //Gyrometer and Accelerometer
    Serial.println("MPU Calibrated");
    sendStringData(MPUReady, pTxCharacteristic);
}

void fatalError(void) {
    while(1) {
        Serial.println("Fatal Error: Could not connect to MPU6050");
        if(deviceConnected) {
            sendStringData(MPUError, pTxCharacteristic);
        }
        delay(2000);
    }
}
