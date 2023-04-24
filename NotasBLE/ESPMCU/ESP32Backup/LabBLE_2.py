#Prender y apagar led
#Mandar estado del led
import esp32 #Sensores internos
import time
import machine
from machine import Pin
from machine import Pin, PWM
from BLE import BLEUART
import bluetooth
#import queue as FIFO

#Globals
switchPin = Pin(0, Pin.IN, Pin.PULL_UP) #GPIO 0
#FIFOLed = FIFO.Queue(10)
ledState = 0
ledPin = Pin(1, Pin.OUT, value=0) #GPIO 1

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
        uart.write("SWON")
    else:
        uart.write("SWOFF")
    if FIFOLed.empty() == False: #FIFO is not empty
        ledState = FIFOLed.get()
        ledPin.value(ledState)
    time.sleep(10)
    

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read.decode().strip()
    uart.write("ESP32 " + str(rx_buffer) + "\n")
    print("ESP32 " + str(rx_buffer) + "\n")
    #FIFOLed.put(int(rx_buffer))
    
#Register BLE event
uart.irq(handler=on_rx)