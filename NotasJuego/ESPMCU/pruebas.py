string = input("Halo").lower()

print(string)

GPC1 = "GPIO_CAPACITIVE_1"
GPC2 = "GPIO_CAPACITIVE_2"
GPC3 = "GPIO_CAPACITIVE_3"
GPC4 = "GPIO_CAPACITIVE_4"
GPC5 = "GPIO_CAPACITIVE_5"
GPC6 = "GPIO_CAPACITIVE_6"

TOUCHPINS = {
    1 : GPC1,
    2 : GPC2,
    3 : GPC3,
    4 : GPC4,
    5 : GPC5,
    6 : GPC6
}

for touchPin in TOUCHPINS:
    print(TOUCHPINS[touchPin])
    print("\n")
    print(touchPin)