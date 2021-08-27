def say_hello():
    print("Hello, World!")

x = 5
while x < 8:
    x = x + 1
    if x == 4:
        break
    else:
        continue

y = say_hello()
