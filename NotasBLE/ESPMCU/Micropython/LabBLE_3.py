#RGB LED
#TODO
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

RED = 0
GREEN = 1
BLUE = 2

#Globals
PwmTimer = [PWM(Pin(GPIO_RED_PIN)), PWM(Pin(GPIO_GREEN_PIN)), PWM(Pin(GPIO_BLUE_PIN))]
strPWMconfig = "R0 G0 B0"
bleOrders = []
red = 0
green = 0
blue = 0
flag = False

#Init Config
name = 'LabBLE_2'
ble = bluetooth.BLE()
uart = BLEUART(ble, name)
machine.freq(240000000) # set the CPU frequency to 240 MHz
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)

for Pin in PwmTimer:
    PwmTimer[Pin].freq(5000)
    PwmTimer[Pin].duty(0)

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read.decode().strip()
    uart.write("ESP32 " + str(rx_buffer) + "\n")
    print("ESP32 " + str(rx_buffer) + "\n")
    bleOrders = [*rx_buffer] #TODO
    red = int("".join(bleOrders[1:4]))
    green = int("".join(bleOrders[6:9]))
    blue = int("".join(bleOrders[11:]))
    flag = True
    
#Register BLE event
uart.irq(handler=on_rx)

#Infinite loop
while 1:
    if flag:
        colors = [red, green, blue]
        i = 0
        for color in colors:
            i += 1
            color = (color*1023)/255
            PwmTimer[i].duty = color
        flag = False