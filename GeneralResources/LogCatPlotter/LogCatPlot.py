import subprocess
import re
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
from collections import deque
import fcntl
import os

# Create figure for plotting
fig = plt.figure()
ax = fig.add_subplot(1, 1, 1)
xs = deque(maxlen=100)  # Limit the number of x values to keep
ys = deque(maxlen=100)  # Limit the number of y values to keep

# Regular expression to match the logged data
pattern = re.compile(r'Data raw: (\d+)')

# Function to make file non-blocking
def make_non_blocking(fd):
    flags = fcntl.fcntl(fd, fcntl.F_GETFL)
    fcntl.fcntl(fd, fcntl.F_SETFL, flags | os.O_NONBLOCK)

# Function to parse and plot the data
def plot_data(i):
    process = subprocess.Popen(['/home/lord448/Android/Sdk/platform-tools/adb', 'logcat', '-d'], stdout=subprocess.PIPE)
    make_non_blocking(process.stdout.fileno())  # Make subprocess stdout non-blocking
    
    while True:
        try:
            line = process.stdout.readline().decode().strip()  # Read a line from subprocess stdout
            if not line:
                break  # No more data available
            match = pattern.search(line)
            if match:
                # Extract data from the log message
                y_value = float(match.group(1))  # Extract the numerical value
                ys.append(y_value)
                break
        except (IOError, OSError):
            pass  # No data available, continue reading

    xs.append(i)
    
    ax.clear()
    ax.plot(xs, ys, marker='o', color='b', linestyle='-')
    
    plt.xlabel('Time')
    plt.ylabel('Data Value')
    plt.title('Real-time Data Plot')
    plt.subplots_adjust(bottom=0.30)
    plt.grid(True)  # Add grid lines
    plt.tight_layout()  # Adjust layout to prevent clipping of labels

# Create a FuncAnimation to continuously update the plot
ani = FuncAnimation(fig, plot_data, interval=100)  # Update every 100 ms

# Show the plot
plt.show()
