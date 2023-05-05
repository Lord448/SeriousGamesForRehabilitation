#Sample code copied from
#https://docs.micropython.org/en/latest/esp8266/tutorial/neopixel.html
import machine, neopixel
import time

np = neopixel.NeoPixel(machine.Pin(4), 8)
#For LEDs with more than 3 colours, such as RGBW pixels or RGBY pixels,
#the NeoPixel class takes a bpp parameter. To setup a NeoPixel object for an RGBW Pixel, do the following:
np = neopixel.NeoPixel(machine.Pin(4), 8, bpp=4) 

#In a 4-bpp mode, remember to use 4-tuples instead of 3-tuples to set the colour.
#For example to set the first three pixels use:

np[0] = (255, 0, 0) # set to red, full brightness
np[1] = (0, 128, 0) # set to green, half brightness
np[2] = (0, 0, 64)  # set to blue, quarter brightness

#Then use the write() method to output the colours to the LEDs:
np.write()

#The following demo function makes a fancy show on the LEDs:
def demo(np):
    n = np.n

    # cycle
    for i in range(4 * n):
        for j in range(n):
            np[j] = (0, 0, 0)
        np[i % n] = (255, 255, 255)
        np.write()
        time.sleep_ms(25)

    # bounce
    for i in range(4 * n):
        for j in range(n):
            np[j] = (0, 0, 128)
        if (i // n) % 2 == 0:
            np[i % n] = (0, 0, 0)
        else:
            np[n - 1 - (i % n)] = (0, 0, 0)
        np.write()
        time.sleep_ms(60)

    # fade in/out
    for i in range(0, 4 * 256, 8):
        for j in range(n):
            if (i // 256) % 2 == 0:
                val = i & 0xff
            else:
                val = 255 - (i & 0xff)
            np[j] = (val, 0, 0)
        np.write()

    # clear
    for i in range(n):
        np[i] = (0, 0, 0)
    np.write()
    
#Execute it using
#demo(np)