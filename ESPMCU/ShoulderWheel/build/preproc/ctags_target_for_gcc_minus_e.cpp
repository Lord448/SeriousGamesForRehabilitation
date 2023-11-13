# 1 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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

 * @version   0.1.0

 * @date      2023-11-06

 * 

 * @copyright This Source Code Form is subject to the terms of the Mozilla Public

              License, v. 2.0. If a copy of the MPL was not distributed with this

              file, You can obtain one at https://mozilla.org/MPL/2.0/

 * 

 */
# 19 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
# 20 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 21 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 22 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 23 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 24 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 25 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 26 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2
# 27 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 2

//Sends the data no matter if the game is requesting information
//!More battery power will be consumed if defined


//Configure the battery power notification
//#define BATT
//Macro to get volts

//Macro to get ADCCounts

//Battery volts when discharge

//Battery volts when full charge



//#define WORKING_AXYS_Y
//#define WORKING_AXYS_Z

//#define UPSIDEDOWN_MOUNT







// See the following for generating UUIDs:
// https://www.uuidgenerator.net/



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
BLEServer *pServer = 
# 80 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino" 3 4
                    __null
# 80 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
                        ;
BLECharacteristic *pTxCharacteristic;
MPU6050 mpu(Wire);
float rawAngle;
bool deviceConnected = false;
bool oldDeviceConnected = false;
static char Buffer[16];
//----------------------------------------------------------------------
//                          RECEIVE STRINGS
//----------------------------------------------------------------------
static const char *doTransmit = "Transmit";
static const char *stopTX = "Stop";
static const char *sleepTX = "Sleep";
static const char *wakeupTX = "Wake";
static const char *Reset = "Reset";
static const char *MPUStartCal = "MPUStartCal"; //Order to calibrate MPU
//----------------------------------------------------------------------
//                           SEND STRINGS
//----------------------------------------------------------------------
static char *MPUError = "MPUError";
static char *ResetSend = "ResetESP32";
static char *MPUCal = "MPUCal"; //No Move MPU
static char *MPUReady = "MPUReady";
static char *LowBatt = "LowBatt";
static char *FullBatt = "FullBatt";
//----------------------------------------------------------------------
//                            BLE FLAGS
//----------------------------------------------------------------------
struct bleRXFlags {
    bool doTransmit;
    bool sleep;
    bool mpuCal;
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
//----------------------------------------------------------------------
//                              SETUP
//----------------------------------------------------------------------
void setup()
{
    Serial.begin(115200);
    Wire.begin();







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
 getData(&rawAngle);
    valueIsDiff =
    (rawAngle > (pastAngle + 10) || rawAngle < (pastAngle - 10))
    && (rawAngle != pastAngle);

    if (valueIsDiff) {
        scaleData(&angle, rawAngle);



        if(deviceConnected)

        {
            sprintf(Buffer, "Angle:%3.2f\n", angle);
            sendStringData(Buffer, pTxCharacteristic);
        }
    }

    // BLE device disconnect
    if (!deviceConnected && oldDeviceConnected) {
        if(digitalRead(2) == 1)
            digitalWrite(2, 0);
        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("start advertising");
        oldDeviceConnected = deviceConnected;
    }

    // BLE device connect
    if (deviceConnected && !oldDeviceConnected) {
  if(digitalRead(2) == 0)
            digitalWrite(2, 1);
        oldDeviceConnected = deviceConnected;
    }

    //MPU Calibration
    if(bleRXFlags.mpuCal) {
        mpuCalc();
        bleRXFlags.mpuCal = false;
    }

    //Check battery voltage







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
# 290 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void getData(float *read) {
    float lectures = 0;
    float tmp = 0;
    for(uint32_t i = 0; i < 6; i++) {
        mpu.update();

        tmp = mpu.getAngleX();







        if(tmp < 0) //Adjust to get absolute scale
            tmp += 360;
        lectures += tmp;
    }
    *read = lectures/6;
   /*

   delay(10);

   mpu.update();

    float tmp = 0; 

    tmp = mpu.getAngleX();

    Serial.println(tmp);

    */
# 316 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
}

/**

 * @brief Scales and proccess the data so it can be send to the mobile

 * @note  The data is already scaled on the GetData method

 * @param value: pointer to the value that will be sent

 * @param read: raw read of the value

 */
# 324 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void scaleData(float *value, float read) {
    *value = read; //The data is already scaled because the mean requires the adjust

    Serial.printf("rawAngle: %3.2f, Angle: %3.2f\n", rawAngle, *value);

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
# 339 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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
# 351 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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
# 370 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void mpuCalc(void) {
    sendStringData(MPUCal, pTxCharacteristic);
    Serial.println("MPU About to calibrate, no move");
    delay(1000);



    mpu.calcOffsets(); //Gyrometer and Accelerometer
    Serial.println("MPU Calibrated");
    sendStringData(MPUReady, pTxCharacteristic);
}

/**

 * @brief Called when the MPU6050 could not be initializated

 *        it traps the ESP32 inside a loop and notifies each 2

 *        seconds to the Serial port and the mobile app

 */
# 387 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
void fatalError(void) {
    while(1) {
        Serial.println("Fatal Error: Could not connect to MPU6050");
        if(deviceConnected) {
            sendStringData(MPUError, pTxCharacteristic);
        }
        delay(2000);
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
# 404 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/ShoulderWheel/ShoulderWheel.ino"
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
