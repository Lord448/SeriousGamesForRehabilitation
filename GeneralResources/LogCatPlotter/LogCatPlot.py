import subprocess
import re
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

# Regular expression to match the logged data
pattern = re.compile(r'Data raw: (\d+\.\d+)')

# Function to parse and plot the data
def plot_data(i):
    process = subprocess.Popen(['/home/lord448/Android/Sdk/platform-tools/adb', 'logcat', '-d'], stdout=subprocess.PIPE)
    output, _ = process.communicate()
    lines = output.decode().split('\n')
    x_data = []
    y_data = []
    for line in lines:
        match = pattern.search(line)
        if match:
            # Extract data from the log message
            y_value = float(match.group(1))  # Extract the numerical value
            x_data.append(i)
            y_data.append(y_value)
    #plt.cla()  # Clear the current plot
    plt.plot(x_data, y_data)
    plt.xlabel('Time')
    plt.ylabel('Data Value')
    plt.title('Real-time Data Plot')

# Create a FuncAnimation to continuously update the plot
ani = FuncAnimation(plt.gcf(), plot_data, interval=10)  # Update every 10 ms

# Show the plot
plt.show()
