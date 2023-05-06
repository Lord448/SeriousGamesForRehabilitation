import esp32 #Sensores internos
import time
import machine
import neopixel
from machine import Pin
from machine import Pin, PWM
from machine import TouchPad, Pin
from BLE import BLEUART
import bluetooth
import uasyncio as asyncio
from LedStrip import LedStrip

GPIO_WS2812_LED = 4 #Remember tu put a 330 resistor
NUMBER_OF_LEDS = 8

#GPIO Capactitive sensor
"""
The cables needs to be installed in the following order
GPIO_CAPACITIVE_1 : The lower step of the ladder 
GPIO_CAPACITIVE_10 : The higher step of the ladder
"""
GPIO_CAPACITIVE_1 = 32
GPIO_CAPACITIVE_2 = 33
GPIO_CAPACITIVE_3 = 27
GPIO_CAPACITIVE_4 = 14
GPIO_CAPACITIVE_5 = 12
GPIO_CAPACITIVE_6 = 13
GPIO_CAPACITIVE_7 = 4
GPIO_CAPACITIVE_8 = 2
GPIO_CAPACITIVE_9 = 15

GPC1 = TouchPad(Pin(GPIO_CAPACITIVE_1))
GPC2 = TouchPad(Pin(GPIO_CAPACITIVE_2))
GPC3 = TouchPad(Pin(GPIO_CAPACITIVE_3))
GPC4 = TouchPad(Pin(GPIO_CAPACITIVE_4))
GPC5 = TouchPad(Pin(GPIO_CAPACITIVE_5))
GPC6 = TouchPad(Pin(GPIO_CAPACITIVE_6))
GPC7 = TouchPad(Pin(GPIO_CAPACITIVE_7))
GPC8 = TouchPad(Pin(GPIO_CAPACITIVE_8))
GPC9 = TouchPad(Pin(GPIO_CAPACITIVE_9))

#Global Constants
THRESHOLD = 150 #Adjust for better performance
TOUCHPINS = [GPC1, GPC2, GPC3, GPC4,
              GPC5, GPC6, GPC7, GPC8, GPC9]
TOUCHPINS = {
    1 : GPC1,
    2 : GPC2,
    3 : GPC3,
    4 : GPC4,
    5 : GPC5,
    6 : GPC6,
    7 : GPC7,
    8 : GPC8,
    9 : GPC9
}

#Emulated SO globals
FIFODataSend = asyncio.Queue()
FIFODataReceive = asyncio.Queue()

#Init config
machine.freq(240000000) # set the CPU frequency to 240 MHz
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)
#BLE Config
name = 'Escalera'
ble = bluetooth.BLE()
uart = BLEUART(ble, name)

#Bluetooth Rx event callback
def on_rx():
    rx_buffer = uart.read().decode().strip()
    print(str(rx_buffer))
    FIFODataReceive.put(rx_buffer)

#Register BLE event
uart.irq(handler=on_rx)

async def TaskSearchforPressions():
    capacitiveValue = 0
    while True:
        for touchPin in TOUCHPINS:
            capacitiveValue = TOUCHPINS[touchPin]
            if capacitiveValue < THRESHOLD:
                FIFODataSend.put(touchPin)

async def TaskSendData():
    while True:
        data = await FIFODataSend.get()
        print("Sending data")
        uart.write("Ladder: " + str(data))

async def TaskLedHandler():
    ledHandler = neopixel.NeoPixel(machine.Pin(GPIO_WS2812_LED), NUMBER_OF_LEDS) #Assuming a normal RGB strip
    ledStrip = LedStrip(TOUCHPINS, ledHandler)
    while True:
        pass
        
async def main():
    await asyncio.gather(
        TaskSearchforPressions(),
        TaskSendData(),
    )
    
loop = asyncio.get_event_loop()
loop.run_until_complete(main())