#Prender y apagar led
#Mandar estado del led
#TODO
import esp32 #Sensores internos
import time
import machine
from machine import Pin
from machine import Pin, PWM
from BLE import BLEUART
import bluetooth
#import queue as FIFO

#Globals
switchPin = Pin(13, Pin.IN, Pin.PULL_UP) #GPIO 13
PinLed = Pin(2, Pin.OUT, value = 0) #On GPIO
GNDPin = Pin(12, Pin.OUT, value = 0)

#Init config
machine.freq(240000000) # set the CPU frequency to 240 MHz
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)

#BLE Config
name = 'LabBLE_2'
ble = bluetooth.BLE()
uart = BLEUART(ble, name)

#Infinite loop
while 1:
    if switchPin.value() == 1:
        uart.write("SW OFF")
        print("SW OFF")
    else:
        uart.write("SW ON")
        print("SW ON")
    time.sleep(0.1)
    

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read.decode().strip()
    #uart.write("ESP32 " + str(rx_buffer) + "\n")
    print("ESP32 " + str(rx_buffer) + "\n")
    if rx_buffer == "ON":
        PinLed.on()
        print("Led on")
    elif rx_buffer == "OFF":
        PinLed.off()
        print("Led off")
    elif rx_buffer == "TRY":
        print("Message Recieved")
    else:
        print("String not handled")
    
    
#Register BLE event
uart.irq(handler=on_rx)