# 1 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
# 2 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 3 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 4 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 5 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 6 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 7 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2

//Sends the data no matter if the game is requesting information
# 17 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
// See the following for generating UUIDs:
// https://www.uuidgenerator.net/





void sendData(char *buffer, BLECharacteristic *pTXCharacteristic);
void sendStringData(char *buffer, BLECharacteristic *pTXCharacteristic);

int32_t rawAngle, pastAngle = 0;
int32_t angle;
BLEServer *pServer = 
# 29 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 3 4
                    __null
# 29 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
                        ;
BLECharacteristic *pTxCharacteristic;
bool valueIsDiff = false;
bool deviceConnected = false;
bool oldDeviceConnected = false;
static char Buffer[16];

static const char *doTransmit = "Transmit";
static const char *stopTX = "Stop";
static const char *sleepTX = "Sleep";
static const char *wakeupTX = "Wake";

struct bleRXFlags {
    bool doTransmit;
    bool sleep;
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
            else
                Serial.println("Data not Handled");
        }
    }
};

void setup()
{
    Serial.begin(115200);







    // Create the BLE Device
    BLEDevice::init("ShoulderWheel");

    // Create the BLE Server
    pServer = BLEDevice::createServer();
    pServer->setCallbacks(new MyServerCallbacks());

    // Create the BLE Service
    BLEService *pService = pServer->createService("0a6131ee-7c3a-11ee-b962-0242ac120002" /* UART service UUID*/);

    // Create a BLE Characteristic
    pTxCharacteristic = pService->createCharacteristic(
                                        "131552ca-7c3a-11ee-b962-0242ac120002",
                                        BLECharacteristic::PROPERTY_NOTIFY
                                    );

    pTxCharacteristic->addDescriptor(new BLE2902());

    BLECharacteristic *pRxCharacteristic = pService->createCharacteristic(
                                                "0f16c6ae-7c3a-11ee-b962-0242ac120002",
                                            BLECharacteristic::PROPERTY_WRITE
                                        );

    pRxCharacteristic->setCallbacks(new MyCallbacks());

    // Start the service
    pService->start();

    // Start advertising
    pServer->getAdvertising()->start();
    Serial.println("Waiting a client connection to notify...");
}

void loop()
{
 getData(&rawAngle);

    valueIsDiff =
    (rawAngle > (pastAngle + 10) || rawAngle < (pastAngle - 10))
    && (rawAngle != pastAngle);

    if (valueIsDiff) {
        scaleData(&angle, rawAngle);




        if(deviceConnected)

        {
            sprintf(Buffer, "Angle:%d\n", angle);
            sendStringData(Buffer, pTxCharacteristic);
        }
    }

    // disconnecting
    if (!deviceConnected && oldDeviceConnected) {
        if(digitalRead(2) == 1)
            digitalWrite(2, 0);
        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("start advertising");
        oldDeviceConnected = deviceConnected;
    }

    // connecting
    if (deviceConnected && !oldDeviceConnected) {
  if(digitalRead(2) == 0)
            digitalWrite(2, 1);
        oldDeviceConnected = deviceConnected;
    }







    pastAngle = rawAngle;
}

void getData(int32_t *read) {
    *read = analogRead(4);
}

void scaleData(int32_t *value, int32_t read) {
    *value = (read * 360) / 4095; //12Bits

    Serial.printf("rawAngle: %d, Angle: %d\n", rawAngle, *value);

}

/**

 * @brief Send data to the desired BLE characteristic in UTF-8

 * @note  a string finisher is sent at the end of the transmit

 * @param buffer String data to send 

 * @param pTXCharacteristic Characteristic that will be modified

 */
# 195 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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
# 207 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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
