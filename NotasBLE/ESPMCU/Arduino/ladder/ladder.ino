/**
 * @file ladder.ino
 * @author Pedro Rojo (pedroeroca@outlook.com); Lord448 @ github.com
 * @brief 
 * @version 0.1
 * @date 2023-06-08
 * @todo Process the RX messsage to automate calibration of the sensors and other configs
 * @copyright Copyright (c) 2023
 */
#include <stdio.h>
#include <string.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define SERVER_NAME "Ladder"
#define LED_PIN 2
#define DEFAULT_THRESHOLD 60

//This symbol disables the touch sensor checkout and prepare the raw data to calibrate the threshold via serial monitor
//#define CALIBRATION_MODE

#ifdef CALIBRATION_MODE
//This symbol enable the data send to the bluetooth
#define CALIBRATION_MODE_BT
#endif

//Comment this symbol to use ESP32 the WROOM 32 pin version
#define PIN30 

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID           "670b1f26-0a44-11ee-be56-0242ac120002" // UART service UUID
#define CHARACTERISTIC_UUID_RX "006e861c-0a45-11ee-be56-0242ac120002"
#define CHARACTERISTIC_UUID_TX "058804de-0a45-11ee-be56-0242ac120002"

/**
 * @brief contanins the booleans that handle the response of the
 *        ble client into the calibration subrutine
 */
struct bleFlags{
    bool start;
    bool interrupt;
    bool confirm;
    bool denied;
    bool undo;
    bool restart;
}bleFlags = {
    .start = false,
    .interrupt = false,
    .confirm = false,
    .denied = false,
    .undo = false,
    .restart = false
};

/**
 * @brief 
 * 
 */
struct bleSendStrigs{
    char dataTakenFinished[5];
    char finishedCalibration[5];
    char doCalReceived[15];
    char startCal[15];
    char isrCal[15];
    char deniedResponse[20];
    char requestConfirm[15];
    char dataSet[10];
    char deniedRequest[20];
    char undoCal[10];
    char restartCal[20];
    char valueToSend[5];
}bleSend = {
    .dataTakenFinished = "DTKF",
    .finishedCalibration = "FCAL", /*
    .doCalReceived = "Calibrating.." 
    .startCal = "Starting Cal", 
    .isrCal = "Interrup Cal",
    .deniedResponse = "Response denied", 
    .requestConfirm = "Please confirm",
    .dataSet = "dataSet",
    .deniedRequest = "Undo or restart",
    .undoCal = "undoCal",
    .restartCal = "restarting cal"*/
};

/**
 * @brief 
 * 
 */
struct bleRxStrings{
    char doCalibration[6];
    char startCalibration[9];
    char interruptCalibration[7];
    char confirmThresValue[8];
    char valueDenied[7];
    char undo[6];
    char restart[10];
}bleRxStrings = {
    .doCalibration = "doCal",
    .startCalibration = "startCal",
    .interruptCalibration = "isrCal",
    .confirmThresValue = "confVal",
    .valueDenied = "valDen",/*
    .undo = "undo",
    .restart = "restart"*/
};

/**
 * @brief 
 * 
 */
enum action{
    doCalibrate,
    normalRutine
}action;

void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);

const uint32_t promValues = 1000;
uint32_t touchVals[promValues];
BLEServer *pServer = NULL;
BLECharacteristic *pTxCharacteristic;
bool deviceConnected = false;
bool oldDeviceConnected = false;
char Buffer[4];
uint32_t threshold = DEFAULT_THRESHOLD;
uint32_t touchPins[10];
bool wasTouched[10];
uint32_t touchGPIO[10] = {
                    4,   //Touch pin 0
                    0,   //Touch Pin 1
                    2,   //Touch pin 2
                    15,  //Touch pin 3
                    13,  //Touch pin 4
                    12,  //Touch pin 5
                    14,  //Touch pin 6
                    27,  //Touch pin 7
                    33,  //Touch pin 8
                    32}; //Touch pin 9

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
        std::string rxValue = pCharacteristic->getValue();

        if (rxValue.length() > 0) {
            Serial.print("Received Value: ");
            for (int i = 0; i < rxValue.length(); i++)
            Serial.print(rxValue[i]);
            if(rxValue.compare(bleRxStrings.doCalibration) == 0) {
                action = doCalibrate;
                Serial.println(bleSend.doCalReceived);
                sendData(bleSend.doCalReceived, pTxCharacteristic);
            }
            else if(rxValue.compare(bleRxStrings.startCalibration) == 0) {
                bleFlags.start = true;
                Serial.println(bleSend.startCal);
                sendData(bleSend.startCal, pTxCharacteristic);
            }
            else if(rxValue.compare(bleRxStrings.interruptCalibration) == 0) {
                bleFlags.interrupt = true;
                Serial.println(bleSend.isrCal);
                sendData(bleSend.isrCal, pTxCharacteristic);
            }
            else if(rxValue.compare(bleRxStrings.confirmThresValue) == 0) {
                bleFlags.confirm = true;

            }
            else if(rxValue.compare(bleRxStrings.valueDenied) == 0)
                bleFlags.denied = true;
            else
                Serial.println("Data not handled");
        }
    }
};


void setup() {

    Serial.begin(115200);

    for(uint32_t i = 0; i < 10; i++) {
        touchPins[i] = i;
        wasTouched[i] = false;
    }

    action = normalRutine;

    pinMode(LED_PIN, OUTPUT);

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

void loop() {
    if (deviceConnected) {
        if(digitalRead(LED_PIN) == 0)
            digitalWrite(LED_PIN, 1);

        switch(action)
        {
            case normalRutine:
                for(uint32_t i = 0; i < 10; i++) {
                    #ifndef CALIBRATION_MODE
                        #ifdef PIN30
                        if(i == 1)
                            continue;
                        #endif // PIN30

                    if(touchRead(touchGPIO[i]) < threshold) { //User takes pin
                        if(!wasTouched[i]) {
                            sprintf(Buffer, "T%d", touchPins[i]);
                            sendData(Buffer, pTxCharacteristic);
                            wasTouched[i] = true;
                        }
                    }
                    else if(wasTouched[i]) { //User drops pin
                        wasTouched[i] = false;
                    }
                    #else
                        Serial.printf("Pin: %d, Value: %d \n", touchPins[i], touchRead(touchGPIO[i]));
                            #ifdef CALIBRATION_MODE_BT
                                static char pinval[25];
                                sprintf(pinval, "Pin: %d, Value: %d \n", touchPins[i], touchRead(touchGPIO[i]));
                                sendData(pinval, pTxCharacteristic);
                            #endif // SEND_TO_BT
                    #endif // ENABLE_CALIBRATION
                }  
            break;
            case doCalibrate:
                uint32_t sume;
                float average;

                Serial.println("Entering in calibration mode");
                if(bleFlags.interrupt) {
                    resetFlags();
                    threshold = DEFAULT_THRESHOLD;
                    action = normalRutine;
                }
                else if(bleFlags.start) {
                    for(uint32_t i = 0; i < promValues; i++) {
                        touchVals[i] = touchRead(touchGPIO[0]);
                        sendData(bleSend.dataTakenFinished, pTxCharacteristic);
                    }
                    for(uint32_t i = 0; i < promValues; i++)
                        sume += touchVals[i];
                    average = sume/promValues;
                    Serial.printf("Avg: %2.2f", average);
                    sprintf(bleSend.valueToSend, "%d", (uint32_t) average);
                    sendData(bleSend.valueToSend, pTxCharacteristic);

                    for(uint32_t i = 0; bleFlags.confirm; i++) {
                        if(i == 1)
                            sendData(bleSend.requestConfirm, pTxCharacteristic);
                    }

                    if(bleFlags.confirm) {
                        threshold = (uint32_t) average;
                        action = normalRutine;
                        sendData(bleSend.dataSet, pTxCharacteristic);
                        Serial.println("Value confirmed");
                    }

                    else if(bleFlags.denied) {
                        for(uint32_t i = 0; bleFlags.undo || bleFlags.restart; i++) {
                            if(i == 1)
                                sendData(bleSend.deniedRequest, pTxCharacteristic);
                        }
                        if(bleFlags.undo) {
                            action = normalRutine;
                            sendData(bleSend.undoCal, pTxCharacteristic);
                        }
                        else if(bleFlags.restart) {
                            sendData(bleSend.restartCal, pTxCharacteristic);
                        }
                        else {
                            Serial.println("Data not handled");
                            action = normalRutine;
                        }
                    }
                    
                }
            break;
            default:
                Serial.println("Warning: Case not handled");
            break;
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
		// do stuff here on connecting
        oldDeviceConnected = deviceConnected;
    }
}

/**
 * @brief Using pointer all the structure is cleaned by 
 *        setting the flags to false
 */
void resetFlags(void) {
    bool *p = &bleFlags.start;
    for(uint16_t i = 0; i < sizeof(bleFlags); i++) {
        *p = false;
        p++;
    }
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