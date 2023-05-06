import machine
import neopixel

class LedStrip:
    def __init__(self, touchPins: dict, ledHandler: neopixel) -> None:
        self.touchPins = touchPins 
        self.ledHandler = ledHandler
        self.data = -1
    
    def touchStepAnium(self):
        pass
    
    def idleAnim(self):
        pass
    
    def winnerAnim(self):
        pass
    
    def setData(self, data):
        self.data = data