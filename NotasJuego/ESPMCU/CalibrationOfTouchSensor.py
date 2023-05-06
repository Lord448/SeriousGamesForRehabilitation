from machine import TouchPad, Pin
import time
import micropython

GPIO_ISR = 10

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

touch_pins = [GPC1, GPC2, GPC3, GPC4,
              GPC5, GPC6, GPC7, GPC8, GPC9]

printable_pins = [GPIO_CAPACITIVE_1, GPIO_CAPACITIVE_2, GPIO_CAPACITIVE_3,
                  GPIO_CAPACITIVE_4, GPIO_CAPACITIVE_5, GPIO_CAPACITIVE_6,
                  GPIO_CAPACITIVE_7, GPIO_CAPACITIVE_8, GPIO_CAPACITIVE_9]

def selectTouchPad():
    noPinSelected = True
    while noPinSelected:
        pin = int(input("Select a pin"))
        for i in range(0, 8):
            if pin == touch_pins[i]:
                print("Pin number: " + str(pin) + " selected")
                noPinSelected = False
                return pin
        print("Invalid Pin, select again")

isr_activated = False

def isr(change):
    global isr_activated
    isr_activated = True

isrPin = Pin(GPIO_ISR, Pin.IN, Pin.PULL_UP)
isrPin.irq(handler=isr, trigger=Pin.IRQ_FALLING)

capacitiveValue = 500
threshold = 150 # Threshold to be adjusted
touch_pin = 0
stages = "Print"


print("Touch pins:\n")
for i in range(0, 9):
    print(str(printable_pins[i]) + "\n")

touch_pin = TouchPad(Pin(selectTouchPad()))

while True:
    run = input("Calibrate?").lower()
    if run == "s" or run == "si" or run == "y" or run == "yes":
        while not isr_activated:
            print(touch_pin.read())
    else:
        run == input("Select pin?").lower()
        if run == "s" or run == "si" or run == "y" or run == "yes":
            touch_pin = TouchPad(Pin(selectTouchPad()))
            
            