import esp32 #Sensores internos
import time
import machine
from machine import Pin
from machine import Pin, PWM,ADC
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)
"""
machine.freq(240000000) # set the CPU frequency to 240 MHz
valor=machine.freq() # get the current frequency of the CPU
print("la frecuencia en MHZ es",valor/1000000)
"""
sensorhall=esp32.hall_sensor() # read the internal hall sensor
sensortemp=esp32.raw_temperature() # read the internal temperature of the MCU, in Farenheit
print("Efecto Hall es "+str(sensorhall))
print("Temperatura en F",sensortemp)
"""
p1 = time.ticks_ms() # get millisecond counter
time.sleep(1) # sleep for 1 second
time.sleep_ms(500) # sleep for 500 milliseconds
time.sleep_us(100) # sleep for 10 microseconds
delta = time.ticks_diff(time.ticks_ms(), p1) # compute time difference entre p2 -p1
print("tiempo es",delta)
"""

p0 = Pin(0, Pin.OUT) # create output pin on GPIO0

for i in range(1):
    p0.on() # set pin to "on" (high) level
    print(p0.value())
    time.sleep(1) # sleep for 1 second
    p0.off() # set pin to "off" (low) level
    print(p0.value())
    time.sleep(1) # sleep for 1 second
    p0.value(1) # set pin to on/high
    print(p0.value())
    time.sleep(1) # sleep for 1 second
    p0.value(0) # set pin to on/high
    print(p0.value())
    time.sleep(1) # sleep for 1 second
    
#p5 = Pin(5, Pin.OUT, value=1) # set pin high on creation
#print(dir(machine)) #Da informacion de los modulos y metodos
#p2 = Pin(2, Pin.IN) # create input pin on GPIO2
#print(p2.value()) # get value, 0 or 1
#p4 = Pin(4, Pin.IN, Pin.PULL_UP) # enable internal pull-up resistor
#print(dir(Pin))

pwm0 = PWM(Pin(0)) # create PWM object from a pin
pwm0.freq(2000) # set frequency
valor=pwm0.freq() # get current frequency
print("Frecuencia del PWM",valor)
pwm0.duty(512) # set duty cycle va de 0 a 1023 para 0 a 100%, para 50%
valor=pwm0.duty() # get current duty cycle
print("Ciclo de Trabajo",valor)
#pwm0.deinit() # turn off PWM on the pin
#pwm2 = PWM(Pin(2), freq=20000, duty=512) # create and configure in one go
adc = ADC(Pin(32)) # create ADC object on ADC pin
adc.atten(ADC.ATTN_11DB) # set 11dB input attenuation (voltage range roughly 0.0v - 3.6v)
adc.width(ADC.WIDTH_9BIT) # set 9 bit return values (returned range 0-511)
valor=adc.read() # read value, 0-4095 across voltage range 0.0v - 1.0v
print("ADC",valor)
#print(dir(ADC))
for x in range(2):
    valor=adc.read() # read value, 0-4095 across voltage range 0.0v - 1.0v
    pwm0.duty(valor*2) # set duty cycle va de 0 a 1023 para 0 a 100%, para 50%  
    time.sleep(1) # sleep for 1 second