from machine import TouchPad, Pin
import time

capacitiveValue = 500
threshold = 150 # Threshold to be adjusted
touch_pin = TouchPad(Pin(4))

print("\nESP32 Touch Demo")
while True: # Infinite loop
  capacitiveValue = touch_pin.read()
  if capacitiveValue < threshold:
    print("Touch pressed")
    time.sleep_ms(500)