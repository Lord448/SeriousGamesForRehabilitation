#Sensor de temperatura
#TODO
import esp32 #Sensores internos
import time
import machine
from machine import Pin
from machine import Pin, PWM
from BLE import BLEUART
import bluetooth

#Globals

#Init Config
name = 'LabBLE_4'
ble = bluetooth.BLE()
uart = BLEUART(ble, name)
machine.freq(240000000) # set the CPU frequency to 240 MHz
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read().decode().strip()
    print("Data received: " + str(rx_buffer) + "\n")
    
#Register BLE event
uart.irq(handler=on_rx)

#Infinite loop
while 1:
    sensorTemp = esp32.raw_temperature()
    sensorTemp = (sensorTemp - 32) * (5/9)
    uart.write("{:.2f}".format(sensorTemp))
    print("{:.2f}".format(sensorTemp))
    time.sleep(0.2)