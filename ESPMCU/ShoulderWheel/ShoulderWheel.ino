/**
 * @file      ShoulderWheel.ino
 * 
 * @author    Pedro Rojo (pedroeroca@outook.com)
 * 
 * @brief     Makes the connection and acquires the angle information with
 *            a MPU6050 Sensor, for physical connections check the README.md, the ESP32
 *            sends fixed strings to notify to the mobile the current status of the
 *            embedded device
 * 
 * @version   0.1.1
 * @date      2023-11-06
 * 
 * @copyright This Source Code Form is subject to the terms of the Mozilla Public
              License, v. 2.0. If a copy of the MPL was not distributed with this
              file, You can obtain one at https://mozilla.org/MPL/2.0/

 * @todo      Implement enhanced communication with the BLE device
 * 
 */
#include <stdio.h>
#include <string.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <MPU6050_light.h>
#include "Wire.h"

//Sends the data no matter if the game is requesting information
//!More battery power will be consumed if defined
#define TEST

//Configure the battery power notification
//#define BATT
//Macro to get volts
#define toVolts(x) (x*3.3f)/4095
//Macro to get ADCCounts
#define toADCCount(x) ((float)x*4095)/3.3f
//Battery volts when discharge
#define BATTERY_LOW_VOLTAGE toADCCount(1.5) //Value analog scaled with OpAmps
//Battery volts when full charge
#define BATTERY_FULL_VOLTAGE toADCCount(3) //Value analog scaled with OpAmps

//The axys that will use the MPU6050 to get the angle
//For more detail refer to the MPU6050 datasheet or MPU6050_Light library
#define WORKING_AXYS_X
//#define WORKING_AXYS_Y
//#define WORKING_AXYS_Z

//Define it if the sensor will be in this position
//#define UPSIDEDOWN_MOUNT

//Name that will appear on the mobile device
//!Needs to be the same as declared in the game
#define SERVER_NAME "ShoulderWheel"

//Led pin on the ESP32 board used
//it indicates if a BLE device is connected
//USE 0xFF or comment to disable it
#define LED_PIN 0xFF

//Macro to know if the led pin of the ESP32 is used
#define LED_ENABLED (LED_PIN != 0xFF && defined(LED_PIN))

//Macro to get the Absolut value
#define ABS(x) x>0? x:-x

//Value for the window on the sending data, avoids noise
#define HYSTERESYS 10

//Maximum value of the angle units increments
#define MAX_INCREMENT 50.0

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

// "UART" Service UUID
#define SERVICE_UUID           "0a6131ee-7c3a-11ee-b962-0242ac120002"
// BLE RX Characteristic -- Here you receive the incoming data
#define CHARACTERISTIC_UUID_RX "0f16c6ae-7c3a-11ee-b962-0242ac120002"
// BLE TX Characteristic -- Here you notify the GATT Client
#define CHARACTERISTIC_UUID_TX "131552ca-7c3a-11ee-b962-0242ac120002" 
//----------------------------------------------------------------------
//                          ENUMS & STRUCTS
//----------------------------------------------------------------------
typedef enum BattFlags {
    LowBatt_t,
    FullBatt_t
}BattFlags;
//----------------------------------------------------------------------
//                            PROTOTYPES
//----------------------------------------------------------------------
void getData(float *read);
void scaleData(float *value, float read);
void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic);
void mpuCalc(void);
void fatalError(void);
void battHandler(BattFlags BattFlags);
//----------------------------------------------------------------------
//                          GLOBAL VARIABLES
//----------------------------------------------------------------------
BLEServer *pServer = NULL; //Pointer to the BLE server class
BLECharacteristic *pTxCharacteristic; //Pointer to the TX characteristic
MPU6050 mpu(Wire); //Object that handles the MPU6050 sensor
float rawAngle; //Angle obtained from the MPU6050
bool deviceConnected = false;  //Indicates the status of the connection
bool oldDeviceConnected = false;  //Used report the disconection or connection once
static char Buffer[16]; //Data buffer that will be send to the GATT Client
//----------------------------------------------------------------------
//                          RECEIVE STRINGS
//----------------------------------------------------------------------
//!Low power mode feature is not going to be implemented (The residual code will be removed on future releases)
static const char *doTransmit = "Transmit"; //Indicates that the ESP32 is allowed to send information to the GATT Client
static const char *stopTX = "Stop"; //Indicates that the ESP32 is not allowed to send data to the GATT Client
static const char *sleepTX = "Sleep"; //Indicates that the ESP32 needs to go to sleep
static const char *wakeupTX = "Wake"; //Indicates that needs to wake up
static const char *Reset = "Reset"; //Reset the MCU
static const char *MPUStartCal = "MPUStartCal"; //Start calibration on MPU6050
//----------------------------------------------------------------------
//                           SEND STRINGS
//----------------------------------------------------------------------
static char *MPUError = "MPUError"; //An error has been ocurred between the ESP32 and the MPU6050
static char *ResetSend = "ResetESP32"; //The ESP32 will reset
static char *MPUCal = "MPUCal"; //No Move MPU
static char *MPUReady = "MPUReady"; //The sensor is ready to use
static char *LowBatt = "LowBatt"; //The battery of the system is low
static char *FullBatt = "FullBatt"; //The battery of the system is full
//----------------------------------------------------------------------
//                            BLE FLAGS
//----------------------------------------------------------------------
struct bleRXFlags {
    bool doTransmit; //Indicates that the ESP32 is allowed to send information to the GATT Client
    bool sleep; //Indicates that the ESP32 needs to go to sleep
    bool mpuCal; //Start calibration on MPU6050
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
//----------------------------------------------------------------------
//                              SETUP
//----------------------------------------------------------------------
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
    //Config pin
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

    if(mpu.begin() != 0)
        fatalError();   
    
    mpuCalc();
}
//----------------------------------------------------------------------
//                                LOOP
//----------------------------------------------------------------------
void loop()
{
    float pastAngle = 0;
    float angle;
    uint32_t BattVoltage;
    bool valueIsDiff = false;
    
    //Data process
	getData(&rawAngle); //Getting data from the sensor and store it in rawAngle

    //Checking if is worth to send the data
    valueIsDiff = 
    (rawAngle > (pastAngle + HYSTERESYS) || rawAngle < (pastAngle - HYSTERESYS))
    && (rawAngle != pastAngle);

    if (valueIsDiff) {
        scaleData(&angle, rawAngle); //At the moment this function does nothing
#ifndef TEST
        if(deviceConnected && bleRXFlags.doTransmit)
#else
        if(deviceConnected)
#endif
        {
            sprintf(Buffer, "%3.2f\n", angle); //Building the string
            sendStringData(Buffer, pTxCharacteristic); //Sending to the GATT Client
        }
    }
    
    // BLE device disconnect
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

    // BLE device connect
    if (deviceConnected && !oldDeviceConnected) {
#if LED_ENABLED
		if(digitalRead(LED_PIN) == 0)
            digitalWrite(LED_PIN, 1);
#endif
        oldDeviceConnected = deviceConnected;
    }

    //MPU Calibration
    if(bleRXFlags.mpuCal) {
        mpuCalc();
        bleRXFlags.mpuCal = false;
    }

    //Check battery voltage
#ifdef BATT
    BattVoltage = analogRead(ANALOG_PIN);
    if(BattVoltage <= BATTERY_LOW_VOLTAGE)
        battHandler(LowBatt_t);
    else if(BattVoltage >= BATTERY_LOW_VOLTAGE)
        battHandler(FullBatt_t);
#endif
    pastAngle = rawAngle;
}
//----------------------------------------------------------------------
//                         DATA PROCESS FUNCTIONS
//----------------------------------------------------------------------
/**
 * @brief Gets the mean value of the lectures (6 Lectures)
 * @note  Since the mean arithmetics needs the absolut scale (0 - 360) 
 *        instead of the relative scale (0 - -180) this functions returns 
 *        the data ready to send
 * @param read: Value where is saved the mean
 */
void getData(float *read) {
    const uint32_t numberOfValues = 8;
    float lectures = 0;
    float tmp = 0;
    for(uint32_t i = 0; i < numberOfValues; i++) {
        mpu.update();
#ifdef WORKING_AXYS_X
        tmp = mpu.getAngleX();
#elif defined(WORKING_AXYS_Y)
        tmp = mpu.getAngleY();
#elif defined(WORKING_AXYS_Z)
        tmp = mpu.getAngleZ();
#else
        tmp = mpu.getAngleX();
#endif
        if(tmp < 0) //Adjust to get absolute scale
            tmp += 360;
        lectures += tmp;
    }
    *read = lectures/numberOfValues;
}

/**
 * @brief Scales and proccess the data so it can be send to the mobile
 * @note  The data is already scaled on the GetData method modify if needed
 * @param value: pointer to the value that will be sent
 * @param read: raw read of the value
 */
void scaleData(float *value, float read) {
    *value = read; //The data is already scaled because the mean requires the adjust
}

/**
 * @brief Split a floating point variable in two integer variables
 * 
 * @note  IEEE standard has 8 bit for exponential part which means you cannot
 *        have more than 7 decimals of presicion
 * 
 * @param value: Float value that will be splited
 * @param intPart: Integer part of the number
 * @param fraccPart: Fraccional part of the number 
 * @param decimals: Number of decimals presicion (for IEEE Standard 7 max)
 */
void splitFloat(float value, int *intPart, int *fraccPart, uint32_t decimals) {
    uint32_t decMultiplier = 1;

    //Decimals limit check
    if(decimals > 7) 
        decimals = 7;
    //Getting the multiplier
    while(decimals > 0) {
        decMultiplier *= 10;
        decimals--;
    }
    //Calculate the values
    *intPart = (int) value;
    *fraccPart = ((value - *intPart) * decMultiplier);
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
//----------------------------------------------------------------------
//                          MPU6050 FUNCTIONS
//----------------------------------------------------------------------
/**
 * @brief Calibrates the MPU6050
 * @note  Do not move the sensor when calibration is going on
 */
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

/**
 * @brief Called when the MPU6050 could not be initializated
 *        it traps the ESP32 inside a loop until the MPU is connected 
 *        and notifies each second to the Serial port and the mobile app
 *        if a maximum value is reached the ESP32 goes to reset
 */
void fatalError(void) {
    const uint32_t intents = 5;
    for(uint32_t i = 0; mpu.begin() == 0; i++) {
        Serial.println("Fatal Error: Could not connect to MPU6050");
        if(deviceConnected) {
            sendStringData(MPUError, pTxCharacteristic);
        }
        delay(1000);
        Serial.println("Retrying MPU begin...");

        if(i >= intents) {
            Serial.println("Restarting ESP32");
            ESP.restart();
        }
            
    }
}
//----------------------------------------------------------------------
//                         BATTERY FUNCTIONS
//----------------------------------------------------------------------
/**
 * @brief Handle and notifies to the BLE device the status of the battery
 * 
 * @param BattFlags 
 */
void battHandler(BattFlags BattFlags) {
    const uint32_t period = 2500; //2.5segs
    static uint32_t time = millis();
    
    if(millis() > time + period) {
        switch(BattFlags) {
            case LowBatt_t:
                Serial.println("Low battery");
                sendStringData(LowBatt, pTxCharacteristic);
            break;
            case FullBatt_t:
                Serial.println("Full battery");
                sendStringData(FullBatt, pTxCharacteristic);
            break;
        }
        time = millis();
    }
}