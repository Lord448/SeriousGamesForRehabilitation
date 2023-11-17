#include <Arduino.h>
#line 1 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
/**
 * @file HungryTest.ino
 * @author your name (you@domain.com)
 * @brief
 * @version 0.1
 * @date 2023-11-16
 *
 * @copyright Copyright (c) 2023
 *
 */
#include <string.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define SERVER_NAME "Ladder1"

// #define BUG_FAST_PRESURES
#define BUG_NEXT_STEP

// General configs
#define MaxSteps 16

// Config macros for fast presures bug
#ifdef BUG_FAST_PRESURES
#endif

// Config macros for next step bug
#ifdef BUG_NEXT_STEP
#endif

// "UART" Service UUID
#define SERVICE_UUID "670b1f26-0a44-11ee-be56-0242ac120002"
// BLE RX Characteristic -- Here you receive the incoming data
#define CHARACTERISTIC_UUID_RX "006e861c-0a45-11ee-be56-0242ac120002"
// BLE TX Characteristic -- Here you notify the GATT Client
#define CHARACTERISTIC_UUID_TX "058804de-0a45-11ee-be56-0242ac120002"

void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic);

BLEServer *pServer = NULL;            // Pointer to the BLE server class
BLECharacteristic *pTxCharacteristic; // Pointer to the TX characteristic
bool deviceConnected = false;         // Indicates the status of the connection
bool oldDeviceConnected = false;      // Used report the disconection or connection once
bool hasBeenSent = false;             // Used to send only on string per presion to the BLE
uint32_t lastPin = 0xFF;              // Last pin used to
char Buffer[4];                       // Data buffer that will be send to the GATT Client (is constructed in the Loop code)

char stepString[6][MaxSteps];

class MyServerCallbacks : public BLEServerCallbacks
{
    void onConnect(BLEServer *pServer)
    {
        deviceConnected = true;
    };

    void onDisconnect(BLEServer *pServer)
    {
        deviceConnected = false;
    }
};

class MyCallbacks : public BLECharacteristicCallbacks
{
    /**
     * @brief Receive the data that has been writed on the BLE
     *        characteristic
     *
     * @param pCharacteristic Writed characteristic
     */
    void onWrite(BLECharacteristic *pCharacteristic)
    {
        std::string rxValue = pCharacteristic->getValue();
        if (rxValue.length() > 0)
        {
            Serial.print("Received Value: ");
            for (int i = 0; i < rxValue.length(); i++)
                Serial.print(rxValue[i]);
            Serial.print("\n");
        }
    }
};

#line 87 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
void setup();
#line 131 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
void loop();
#line 87 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
void setup()
{
    Serial.begin(115200);

    for (int i = 0; i < MaxSteps; i++) {
        sprintf(stepString[i], "T:%d", i);
        Serial.println(stepString[i]);
    }
    // Create the BLE Device
    BLEDevice::init(SERVER_NAME);
    

    // Create the BLE Server
    pServer = BLEDevice::createServer();
    Serial.println("a");
    pServer->setCallbacks(new MyServerCallbacks());
    

    // Create the BLE Service
    BLEService *pService = pServer->createService(SERVICE_UUID);

    // Create a BLE Characteristic
    pTxCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_UUID_TX,
        BLECharacteristic::PROPERTY_NOTIFY);

    pTxCharacteristic->addDescriptor(new BLE2902());

    BLECharacteristic *pRxCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_UUID_RX,
        BLECharacteristic::PROPERTY_WRITE);

    pRxCharacteristic->setCallbacks(new MyCallbacks());

    // Start the service
    pService->start();

    // Start advertising
    pServer->getAdvertising()->start();
    Serial.println("Waiting a client connection to notify...");

    Serial.setTimeout(10); // In order to avoid polling
}

void loop()
{
    if (deviceConnected)
    {
        if (Serial.available())
        {
            String dataReceived = Serial.readStringUntil('\n');
            bool makeTest = dataReceived == "s" 
                            || dataReceived == "sfs" 
                            || dataReceived == "sfns" 
                            || dataReceived == "sfisns";

            if (makeTest)
            {
#ifdef BUG_FAST_PRESURES
                //Sending four steps
                if (dataReceived == "sfs") //Send fast serial steps
                { 
                    for (int i = 0; i < 4; i++) {
                        sendStringData(stepString[i], pTxCharacteristic);
                        Serial.printf("Send: %s", stepString[i]);
                    }
                    Serial.println("Done");
                }
                else if (dataReceived == "sfns") //Send fast no serial steps
                {
                    sendStringData(stepString[0], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[0]);
                    sendStringData(stepString[4], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[4]);
                    sendStringData(stepString[3], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[3]);
                    sendStringData(stepString[6], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[6]);
                    Serial.println("Done");
                }
                else if (dataReceived == "sfisns") // Send fast initial serial and then no serial steps
                {
                    for(int i = 0; i < 3; i++) {
                        sendStringData(stepString[i], pTxCharacteristic);
                        Serial.printf("Send: %s", stepString[i]);
                    }
                    sendStringData(stepString[0], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[0]);
                    sendStringData(stepString[6], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[6]);
                    sendStringData(stepString[9], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[9]);
                    Serial.println("Done");
                }
#endif
#ifdef BUG_NEXT_STEP
                // Jump 3 serial steps starting in zero
                for (int i = 0; i < 3; i++)
                {
                    sendStringData(stepString[i], pTxCharacteristic);
                    Serial.printf("Send: %s", stepString[i]);
                    delay(1000);
                }
                // Send upper incorrect step
                sendStringData(stepString[5], pTxCharacteristic);
                Serial.printf("Send: %s", stepString[5]);
                delay(1000);
                // Send lower incorrect step
                sendStringData(stepString[1], pTxCharacteristic);
                Serial.printf("Send: %s", stepString[1]);
                delay(1000);
                Serial.println("Done");
            }
        }
#endif
    }

    // disconnecting
    if (!deviceConnected && oldDeviceConnected)
    {
#if LED_ENABLED
        if (digitalRead(LED_PIN) == 1)
            digitalWrite(LED_PIN, 0);
#endif
        delay(500);                  // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("Start advertising");
        oldDeviceConnected = deviceConnected;
    }

    // connecting
    if (deviceConnected && !oldDeviceConnected)
    {
        // do stuff here on connecting
#if LED_ENABLED
        digitalWrite(LED_PIN, 1);
#endif
        oldDeviceConnected = deviceConnected;
    }
}

/**
 * @brief Send data to the desired BLE characteristic in UTF-8
 * @note  a string finisher is sent at the end of the transmit
 * @param buffer String data to send
 * @param pTXCharacteristic Characteristic that will be modified
 */
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic)
{
    pTXCharacteristic->setValue((uint8_t *)buffer, strlen(buffer));
    pTXCharacteristic->notify();
    delay(10); // bluetooth stack will go into congestion, if too many packets are sent, 10ms min
}
/**
 * @brief Send data char by char to the desired BLE characteristic in UTF-8
 * @note  a string finisher is sent at the end of the transmit
 * @param buffer String data to send
 * @param pTXCharacteristic Characteristic that will be modified
 */
void sendData(char *buffer, BLECharacteristic *pTXCharacteristic)
{
    char charTX;
    for (uint32_t i = 0; i <= strlen(buffer); i++)
    {
        if (i != strlen(buffer))
            charTX = buffer[i];
        else
            charTX = '\n';
        pTXCharacteristic->setValue((uint8_t *)&charTX, sizeof(uint8_t));
        pTXCharacteristic->notify();
        delay(15); // bluetooth stack will go into congestion, if too many packets are sent, 10ms min
    }
}
