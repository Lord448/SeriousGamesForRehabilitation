#RGB LED
import esp32 #Sensores internos
import time
import machine
from machine import Pin
from machine import Pin, PWM
from BLE import BLEUART
import bluetooth

GPIO_RED_PIN = 27
GPIO_GREEN_PIN = 26
GPIO_BLUE_PIN = 25

#Globals
PwmTimer = [PWM(Pin(GPIO_RED_PIN)), PWM(Pin(GPIO_GREEN_PIN)), PWM(Pin(GPIO_BLUE_PIN))]
gattClientOrders = []
dataReceived = 0
strArr = ["R000", "G000", "B000"]
strToInt = ""

#Init Config
name = 'LabBLE_3'
ble = bluetooth.BLE()
uart = BLEUART(ble, name)
machine.freq(240000000) # set the CPU frequency to 240 MHz
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)

for i in range(0, 3):
    PwmTimer[i].duty(0)
    PwmTimer[i].freq(5000)

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read().decode().strip()
    print("Data received: " + str(rx_buffer) + "\n")
    if rx_buffer != "TRY":    
        gattClientOrders = rx_buffer.split()
        for i in range(0, 3):
            strArr[i] = "".join(gattClientOrders[i])
        print(strArr)
     
#Register BLE event
uart.irq(handler=on_rx)

#Infinite loop
while 1:
    if strArr != ["A", "A", "A"]:
        print("Entre al if")
        for i in range(0, 3):
            strToInt = strArr[i]
            dataReceived = int(strToInt[1:])
            duty = (dataReceived*1023)/255
            PwmTimer[i].duty(int(duty))
            print(str(int(duty)) + "%")
        strArr = ["A", "A", "A"]