str = "R100 G250 B300"
charArray = [*str]

red = 0
green = 0
blue = 0

strR = "".join(charArray[1:4])
strG = "".join(charArray[6:9])
strB = "".join(charArray[11:])

print(strR)
print(strG)
print(strB)

colors = [100, 200, 300]

def printable(color):
    print(color)

for color in colors:
    printable(color)