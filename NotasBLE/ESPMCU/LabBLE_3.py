#RGB LED
#TODO
from machine import Pin
from machine import PWM
from BLE import BLEUART
import queue as FIFO
import bluetooth
import time

GPIO_RED_PIN = 0
GPIO_BLUE_PIN = 0
GPIO_GREEN_PIN = 0

#Globals
FIFOLed = FIFO.Queue(10)
PwmTimer = [PWM(Pin(GPIO_RED_PIN)), PWM(Pin(GPIO_GREEN_PIN)), PWM(Pin(GPIO_BLUE_PIN))]

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

#Infinite loop
while 1:
    if FIFOLed.empty() == False:
        pass
        

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read.decode().strip()
    uart.write("ESP32 " + str(rx_buffer) + "\n")
    print("ESP32 " + str(rx_buffer) + "\n")
    FIFOLed.put(int(rx_buffer))
    
#Register BLE event
uart.irq(handler=on_rx)