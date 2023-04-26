#Prender y apagar led
import esp32 #Sensores internos
import time
import machine
from machine import Pin
from machine import Pin, PWM
from BLE import BLEUART
import bluetooth

#Globals
PinLed = Pin(2, Pin.OUT, value = 0) #On GPIO

#Init config
machine.freq(240000000) # set the CPU frequency to 240 MHz
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)
#BLE Config
name = 'LabBLE_1'
ble = bluetooth.BLE()
uart = BLEUART(ble, name)

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read().decode().strip()
    uart.write("ESP32 " + str(rx_buffer) + "\n")
    print("ESP32 " + str(rx_buffer) + "\n")
    if rx_buffer == "ON":
        PinLed.on()
        print("Led on")
    elif rx_buffer == "OFF":
        PinLed.off()
        print("Led off")
    else:
        print("String not handled")

#Register BLE event
uart.irq(handler=on_rx)