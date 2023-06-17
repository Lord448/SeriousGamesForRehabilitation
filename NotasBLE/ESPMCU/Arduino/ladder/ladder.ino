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

#define SERVER_NAME "UART Service"
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

void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);

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
            //@todo handle message
        }
    }
};


void setup() {
    Serial.begin(115200);

    for(uint32_t i = 0; i < 10; i++) {
        touchPins[i] = i;
        wasTouched[i] = false;
    }

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