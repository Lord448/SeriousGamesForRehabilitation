# 1 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
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
# 11 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
# 12 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino" 2
# 13 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino" 2
# 14 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino" 2
# 15 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino" 2
# 16 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino" 2



// #define BUG_FAST_PRESURES


// General configs


// Config macros for fast presures bug



// Config macros for next step bug



// "UART" Service UUID

// BLE RX Characteristic -- Here you receive the incoming data

// BLE TX Characteristic -- Here you notify the GATT Client


void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic);

BLEServer *pServer = 
# 43 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino" 3 4
                    __null
# 43 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
                        ; // Pointer to the BLE server class
BLECharacteristic *pTxCharacteristic; // Pointer to the TX characteristic
bool deviceConnected = false; // Indicates the status of the connection
bool oldDeviceConnected = false; // Used report the disconection or connection once
bool hasBeenSent = false; // Used to send only on string per presion to the BLE
uint32_t lastPin = 0xFF; // Last pin used to
char Buffer[4]; // Data buffer that will be send to the GATT Client (is constructed in the Loop code)

char stepString[6][16];

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
# 74 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
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

void setup()
{
    Serial.begin(115200);

    for (int i = 0; i < 16; i++) {
        sprintf(stepString[i], "T:%d", i);
        Serial.println(stepString[i]);
    }
    // Create the BLE Device
    BLEDevice::init("Ladder1");


    // Create the BLE Server
    pServer = BLEDevice::createServer();
    Serial.println("a");
    pServer->setCallbacks(new MyServerCallbacks());


    // Create the BLE Service
    BLEService *pService = pServer->createService("670b1f26-0a44-11ee-be56-0242ac120002");

    // Create a BLE Characteristic
    pTxCharacteristic = pService->createCharacteristic(
        "058804de-0a45-11ee-be56-0242ac120002",
        BLECharacteristic::PROPERTY_NOTIFY);

    pTxCharacteristic->addDescriptor(new BLE2902());

    BLECharacteristic *pRxCharacteristic = pService->createCharacteristic(
        "006e861c-0a45-11ee-be56-0242ac120002",
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
# 183 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
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

    }

    // disconnecting
    if (!deviceConnected && oldDeviceConnected)
    {




        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("Start advertising");
        oldDeviceConnected = deviceConnected;
    }

    // connecting
    if (deviceConnected && !oldDeviceConnected)
    {
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
# 234 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
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
# 246 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/HungryTest/HungryTest.ino"
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
