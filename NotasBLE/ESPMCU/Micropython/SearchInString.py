"""_summary_

This is a practice code only to search some
specific characters in a string on python taking care
about the implementation on micripython because
micropython doesn't admit the fastest method to do 
this action
"""

string = "R255 G255 B255"
charArray = string.split()

strR = "".join(charArray[0])
strG = "".join(charArray[1])
strB = "".join(charArray[2])

strArr = [strR, strG, strG]

print(strR + "\n")
print(strG + "\n")
print(strB + "\n")

#red = int(strR[1:])
#print(red)

strArr = ["0", "0", "0"]

print(strArr)

for i in range(0, 3):
            strToInt = strArr[i]
            print("strToInt: " + str(strToInt))
            dataReceived = int(strToInt[1:])
            print("dataReceived: " + str(dataReceived))
            duty = (dataReceived*1023)/255
            print(str(int(duty)) + "%")