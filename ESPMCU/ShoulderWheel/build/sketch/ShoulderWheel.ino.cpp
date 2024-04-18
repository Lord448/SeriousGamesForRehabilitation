#include <Arduino.h>
#line 1 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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
 * @version   0.3.0
 * @date      2023-11-06
 * 
 * @copyright This Source Code Form is subject to the terms of the Mozilla Public
 *            License, v. 2.0. If a copy of the MPL was not distributed with this
 *            file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
#include <stdio.h>
#include <string.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <MPU6050_light.h>
#include "Wire.h"

//Sends the data even though the game is not requesting information
//!More battery power will be consumed if defined
#define TEST

//Types to define the type of data
#define Volts 1
#define Counts 2

//Configure the battery power notification
//#define BATT //Comment to disable battery options
//If defined send via BLE the voltage of the battery
#define BATT_SEND_VOLTAGE Counts //Or Volts
//Macro to get volts
#define toVolts(x) (x*3.3f)/4095
//Macro to get ADCCounts from volts
#define toADCCount(x) (int)(((float)x*4095)/3.3f)
//Battery volts when discharge
#define BATTERY_LOW_VOLTAGE toADCCount(2.1) //Value analog scaled with OpAmps
//Battery volts when full charge
#define BATTERY_FULL_VOLTAGE toADCCount(2.9) //Value analog scaled with OpAmps
//Pin that will sense the voltage of the Battery
#define ANALOG_PIN 14
//Pin that will handle a LED to notify low battery charge
//#define BATT_LED_LOW 16 //Comment this to disable the PIN
//Pin that will handle a LED to notify full battery charge
//#define BATT_LED_FULL 17 //Comment this to disable the PIN

//The axys that will use the MPU6050 to get the angle
//For more detail refer to the MPU6050 datasheet or MPU6050_Light library
#define DEFAULT_AXYS_X
//#define DEFAULT_AXYS_Y
//#define DEFAULT_AXYS_Z

//Define it if the sensor will be in this position
//#define UPSIDEDOWN_MOUNT

//Defines the filter of the MPU6050, undefined sets default (0.98)
//#define FILTER_COEF

//Name that will appear on the mobile device
//!Needs to be the same as declared in the game
#define SERVER_NAME "ShoulderWheel"

//Led pin on the ESP32 board used
//it indicates if a BLE device is connected
//USE 0xFF or comment to disable it
#define BLE_LED_PIN 33

//Macro to know if the led pin of the ESP32 is used
#define LED_ENABLED (BLE_LED_PIN != 0xFF && defined(BLE_LED_PIN))

//Macro to get the Absolut value
#define ABS(x) x>0? x:-x

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

// "UART" Service UUID
#define SERVICE_UUID           "0a6131ee-7c3a-11ee-b962-0242ac120002"
// BLE RX Characteristic -- Here you receive the incoming data
#define CHARACTERISTIC_UUID_RX "0f16c6ae-7c3a-11ee-b962-0242ac120002"
// BLE TX Characteristic -- Here you notify the GATT Client
#define CHARACTERISTIC_UUID_TX "131552ca-7c3a-11ee-b962-0242ac120002" 


//DSP Configurations
#define ENABLE_FILTER
//----------------------------------------------------------------------
//                          ENUMS & STRUCTS
//----------------------------------------------------------------------
typedef enum BattFlags {
    LowBatt_t,
    FullBatt_t,
    NormalLevel_t
}BattFlags;

typedef enum Axys {
    X,
    Y,
    Z
}Axys;
//----------------------------------------------------------------------
//                            PROTOTYPES
//----------------------------------------------------------------------
void getData(float *read);
void lowPassFilter(float *angle);
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
bool deviceConnected = false;  //Indicates the status of the connection
bool oldDeviceConnected = false;  //Used report the disconection or connection once
static char Buffer[16]; //Data buffer that will be send to the GATT Client
//Axys that the MPU6050 will use
#ifdef DEFAULT_AXYS_Y
Axys workingAxys = Y;
Axys pastAxys = Y;
#elif defined(DEFAULT_AXYS_Z)
Axys workingAxys = Z;
Axys pastAxys = Z;
#else
Axys workingAxys = X;
Axys pastAxys = X;
#endif
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
static const char *SetAxysX = "AxysX"; //Set the axys X in the MPU6050 
static const char *SetAxysY = "AxysY"; //Set the axys Y in the MPU6050
static const char *SetAxysZ = "AxysZ"; //Set the axys Z in the MPU6050
//----------------------------------------------------------------------
//                           SEND STRINGS
//----------------------------------------------------------------------
static char *MPUError = "MPUError\n"; //An error has been ocurred between the ESP32 and the MPU6050
static char *ResetSend = "ResetESP32\n"; //The ESP32 will reset
static char *MPUCal = "MPUCal\n"; //No Move MPU
static char *MPUReady = "MPUReady\n"; //The sensor is ready to use
static char *LowBatt = "LowBatt\n"; //The battery of the system is low
static char *FullBatt = "FullBatt\n"; //The battery of the system is full
static char *SettingX = "Working Axys: X\n"; //Reports that the working axys has been changed
static char *SettingY = "Working Axys: Y\n"; //Reports that the working axys has been changed
static char *SettingZ = "Working Axys: Z\n"; //Reports that the working axys has been changed
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
        char rxBuffer[32] = "";

        if(rxValue.length() > 0) {
            Serial.print("Received Value: ");
            for (int i = 0; i < rxValue.length(); i++) {
                if(rxValue[i] > 0x40) { //Filter the other characters
                    Serial.print(rxValue[i]);
                    rxBuffer[i] = rxValue[i];
                }
            }
                
            Serial.print("\n");
            
            if(strcmp(rxBuffer, doTransmit) == 0) {
                bleRXFlags.doTransmit = true;
                Serial.println("doTransmit true");
            }
            else if(strcmp(rxBuffer, stopTX) == 0) {
                bleRXFlags.doTransmit = false;
                Serial.println("doTransmit false");    
            }
            else if(strcmp(rxBuffer, sleepTX) == 0) {
                bleRXFlags.sleep = true;
                Serial.println("sleep true");
            }
            else if(strcmp(rxBuffer, wakeupTX) == 0) {
                bleRXFlags.sleep = false;
                Serial.println("sleep false");
            }
            else if(strcmp(rxBuffer, MPUStartCal) == 0) {
                bleRXFlags.mpuCal = true;
                Serial.println("mpuCal true");
            }
            else if(strcmp(rxBuffer, SetAxysX) == 0) {
                workingAxys = X;
                sendStringData(SettingX, pTxCharacteristic);
            }
            else if(strcmp(rxBuffer, SetAxysY) == 0) {
                workingAxys = Y;
                sendStringData(SettingY, pTxCharacteristic);
            }
            else if(strcmp(rxBuffer,SetAxysZ) == 0) {
                workingAxys = Z;
                sendStringData(SettingZ, pTxCharacteristic);
            }
            else if(strcmp(rxBuffer, Reset) == 0) {
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
#line 254 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void setup();
#line 317 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void loop();
#line 410 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void setCoef(float *a, float *b);
#line 588 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void battHandler(BattFlags battFlags);
#line 254 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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
    pinMode(BLE_LED_PIN, OUTPUT);
#endif 

#ifdef BATT_LED_LOW
    pinMode(BATT_LED_LOW, OUTPUT);
#endif

#ifdef BATT_LED_FULL
    pinMode(BATT_LED_FULL, OUTPUT);
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
    float angle;
    uint32_t BattVoltage;
#ifdef BATT_SEND_VOLTAGE
    char BattBuffer[32] = "";
#endif
    
    //Data process
	getData(&angle); //Getting data from the sensor

#ifdef ENABLE_FILTER
    //Data filter
    //lowPassFilter(&angle);
#endif

#ifndef TEST
    if(deviceConnected && bleRXFlags.doTransmit)
#else
    if(deviceConnected && angle > 0 && angle < 360)
#endif
    {
        sprintf(Buffer, "%3.2f\n", angle); //Building the string
        sendStringData(Buffer, pTxCharacteristic); //Sending to the GATT Client
        
#if LED_ENABLED
        digitalWrite(BLE_LED_PIN, 1);
#endif 
    }
#if LED_ENABLED    
    else {
        digitalWrite(BLE_LED_PIN, 0);
    }
#endif


    // BLE device disconnect
    if (!deviceConnected && oldDeviceConnected) {
#if LED_ENABLED
        if(digitalRead(BLE_LED_PIN) == 1)
            digitalWrite(BLE_LED_PIN, 0);
#endif
        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("Start advertising");
        oldDeviceConnected = deviceConnected;
    }

    // BLE device connect
    if (deviceConnected && !oldDeviceConnected) {
#if LED_ENABLED
		if(digitalRead(BLE_LED_PIN) == 0)
            digitalWrite(BLE_LED_PIN, 1);
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
    #ifdef BATT_SEND_VOLTAGE
        static uint32_t time = millis();
        const uint32_t period = 2000; //Each 2 seconds
        if(millis() > time + period) {
            #if BATT_SEND_VOLTAGE == Volts
                sprintf(BattBuffer, "Batt: %2.2f\n", toVolts(BattVoltage));
                sendStringData(BattBuffer, pTxCharacteristic);
            #elif BATT_SEND_VOLTAGE == Counts
                sprintf(BattBuffer, "Batt: %d\n", BattVoltage);
                sendStringData(BattBuffer, pTxCharacteristic);
            #endif
            time = millis();
        }
    #endif
    if(BattVoltage <= BATTERY_LOW_VOLTAGE)
        battHandler(LowBatt_t);
    else if(BattVoltage >= BATTERY_FULL_VOLTAGE)
        battHandler(FullBatt_t);
    else
        battHandler(NormalLevel_t);
#endif
}
//----------------------------------------------------------------------
//                         DATA PROCESS FUNCTIONS
//----------------------------------------------------------------------

#ifdef ENABLE_FILTER
void setCoef(float *a, float *b)
{
    const float CutOffFrequency = 10;
    const float sqr2 = sqrt(2);
    static float dt = 0, tn1 = 0;

    float omega = 6.28318530718*(CutOffFrequency);
    float t = micros()/1.0e6;

    dt = t - tn1;
    tn1 = t;

    float alpha = omega*dt;
    float alphaSq = alpha*alpha;
    float beta[] = {1, sqr2, 1};
    float D = alphaSq*beta[0] + 2*alpha*beta[1] + 4*beta[2];

    b[0] = alphaSq/D;
    b[1] = 2*b[0];
    b[2] = b[0];
    a[0] = -(2*alphaSq*beta[0] - 8*beta[2])/D;
    a[1] = -(beta[0]*alphaSq - 2*beta[1]*alpha + 4*beta[2])/D;
}

/**
 * @brief 2nd order Butterworth low pass filter for the signal processing
 *        of the MPU6050 this filter has a cutoff frequency of 30Hz 
 * 
 * @param angle: Angle that will be processed
 */
void lowPassFilter(float *angle) 
{
    //Constant coefficients for the filter obtained with the equations on the README files
    static float ACoeff[3] = {1.95558189, -0.95654717, 0};
    static float BCoeff[3] = {0.00024132, 0.00048264, 0.00024132};
    //Variables that interact in the filter difference equation
    static float x[3] = {0, 0, 0};
    static float y[3] = {0, 0, 0};

    //setCoef(ACoeff, BCoeff);

    //Input data of the filter
    x[0] = *angle;
    //2nd order Butterworth difference equation
    y[0] = ACoeff[0]*y[1] + ACoeff[1]*y[2] +
           BCoeff[0]*x[0] + BCoeff[1]*x[1] + BCoeff[2]*x[2];
    //Storing data
    for(int i = 1; i >= 0; i--) 
    {
        x[i+1] = x[i];
        y[i+1] = y[i];
    }
    //Output of the filter
    *angle = y[0];
}
#endif

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
    float mean = 0;
    for(uint32_t i = 0; i < numberOfValues; i++) {
        mpu.update();
        switch (workingAxys) {    
            case Y:
                tmp = mpu.getAngleY();
            break;
            case Z:
                tmp = mpu.getAngleZ();
            break;
            case X:
            default:
                tmp = mpu.getAngleX();
            break;
        }
        if(tmp < 0) //Adjust to get absolute scale
            tmp += 360;
        lectures += tmp;
    }
    mean = lectures/numberOfValues;
    mean -= 360;
    *read = ABS(mean);
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
#ifdef FILTER_COEF && FILTER_COEF!=NULL
    mpu.setFilterGyroCoef((float) FILTER_COEF);
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
void battHandler(BattFlags battFlags) {
    const uint32_t period = 2500; //2.5segs
    static uint32_t time = millis();
    static BattFlags pastState;
    static uint16_t counter = 0;
    
    if(millis() > time + period) {
        switch(battFlags) {
            case LowBatt_t:
                Serial.println("Low battery");
                sendStringData(LowBatt, pTxCharacteristic);
#ifdef BATT_LED_LOW
                digitalWrite(BATT_LED_LOW, 1);
#endif
#ifdef BATT_LED_FULL
                digitalWrite(BATT_LED_FULL, 0);
#endif
            break;
            case FullBatt_t:
                if(counter < 3) { //Reports it 3 times
                    Serial.println("Full battery");
                    sendStringData(FullBatt, pTxCharacteristic);
                    counter++;
#ifdef BATT_LED_LOW
                digitalWrite(BATT_LED_LOW, 0);
#endif
#ifdef BATT_LED_FULL
                digitalWrite(BATT_LED_FULL, 1);
#endif
                }
            break;
            case NormalLevel_t:
#ifdef BATT_LED_LOW
                digitalWrite(BATT_LED_LOW, 0);
#endif
#ifdef BATT_LED_FULL
                digitalWrite(BATT_LED_FULL, 0);
#endif
            break;
        }
        time = millis();
        pastState = battFlags;
    }
}
