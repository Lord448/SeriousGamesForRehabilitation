# Serious Games for Rehabilitation
This is a project in collaboration with the Essex university, CRIT Morelia and the Instituto Tecnológico de Morelia that
presents two games with it's own hardware in order to increase the interaction
of the patient with the rehabilitation machine, in this case we worked two rehabilitation machines: Finger Ladder and Shoulder Wheel
## Finger Ladder
This ladder helps the patient is used to to progressively restore range of motion to the shoulder, elbow and wrist joints.
In this ladder the user needs to put his fingers on the steps and progressively reach the top of the ladder. In the project this rehab machine is interfaced with the game Hungry Hamster, this game uses as hardware an ESP32 with capacitive touch sensors, so the ladder can detect the position of the finger

## Shoulder Wheel
Is a device which allows patients to perform resistance exercises to improve range of motion and relieve pain. The level of resistance can be adjusted by turning the knob on the wheel. Can be used in a standing or seated position. In the project this machine its connected to the Treasure Hunter game, and this game uses an ESP32 with a MPU6050, a mems sensor that works as a gyroscope an accelerometer, this sensor is used to determine the angular position of the user.

# Technical Concepts and general definitions
* **BLE** - (Bluetooth Low Energy) Version that consumes less energy than the classic version, the ideal choice to send non continous data and save energy
* **ESP32** - 32 bits RISC-V Microcontroller that is used to watch the physical variables and send processed data to the games via BLE
* **MPU6050** - Accelerometer and Gyroscope sensor
* **LibGDX** - Java Game framework that is used in Hungry Hamster and Treasure Hunter
* **SDK** - Software Development Kit: API distributed by Google for Android systems
* **Graddle** - Build tool that helps with the compilation of complex java projects
* **CSV** - Structurated data files compatible with sheet calculus programs like Excel or LibreOffice Calc
## Hungry Hamster
* Game available for desktop systems (Without hardware) and Android systems
* Minimum SDK: 27
* Minimum JDK: 8
* Minimum Graddle: 8.0.2
* LibGDX Version: 1.12.1
#### Concepts:
* **Repetitions** - Times that the patient goes up and down of the Ladder
* **Uncompleted Repetitions** - Repetition in which the patient didn't reached the desired steps by the therapist
* **Completed Repetitions** - Repetitions in which the patient reached the desired steps
* **Repetition time** - Time that the patient spend during the repetition (stops when the top of the house is reached)
* **Session** - Group of repetitions (It's designed to run once for patient and allows the program to bound the information of the CSV data file)
* **Session time** - Time the patient spend during the session (only stops when the finish session button is pressed)
* **No. Carnet** - Number of the carnet (Identifier of the patient in the rehab center CRIT)
#### Features:
* ESP32 microcontroller for BLE communication and monitoring of the capacitive touch sensors (Hardware touch sensors)
* New eating, celebration, Inmmersive sounds
* Configurable user interface in order to select patient id (No. Carnet), number of steps, session time, initial step, final step, etc.
* Code to force the patient to touch the sensors in order to avoid cheating
* Data acquisition displayed on game screen (session time, repetition time, number of completed and uncompleted repetitions)

## -- Treasure Hunter -- 
* Game Available on desktop systems (without hardware) and Android systems
* Minimum SDK: 27
* Minimum JDK: 8
* Minimum Graddle: 8.0.2
* LibGDX Version: 1.12.1
#### Concepts
None
#### Features
* ESP32 microcontroller for BLE communication and monitoring of the angle with MPU6050
* DSP for the filtering of the high frequency noise
* Measures of session time
* Number of treasures hunted
* Stop of the guide ball when the user separates from some desired umbral
* Angle mode
* Laps mode
* Configuration user interface that allows the therapists choose the modes, angles, etc.

## More components to see
## --Finger Ladder ESP32 --
## --Shoulder Wheel ESP32 --

# Steps to run the LibGDX Games on desktop environments
* Wait until Android Studio finishes the configuration 
* Go to the configuration Text Button (The one that by default says Android)
* Select: "Edit configurations"
* On the new window select the "+" button located in the left superior corner
* On the new deployed menu select "Application"
* Name the configuration as you want e.g. Desktop
* On the "<no module>" textbox select "GameName".desktop.main
* On the main class text field, click the browse button (located inside the field on the right)
* Select the "DesktopLauncher" option
Now you are ready to run the game!

# PC Controls for Hungry Hamster
Control the position of the hamster by using the numbers of the keyboard (Only compatible with the principal number key)
"0" is the first step, "1" is the second and so on
When you run out of numbers you'll need to use the characters, in order of the QWERTY standard keyboard, i.e.
Q -> 10
W -> 11
...

# PC Controls for Treasure Hunter
Control the position of the green ball by using the right and left keys

# Patch notes:
## Hungry Hamster
* Added new error logger feature with .log file custom format
* New date format on the GameHandler class for the csv file save
* Fixed food prints on the ladder
* Adjustment on ladder numbers
* Added Logo to the game app
## TreasureHunter
* Added logo to the game app

The setting up tutorials of each component and program will be in the local README file.
At the moment we haven't done that documentation.
