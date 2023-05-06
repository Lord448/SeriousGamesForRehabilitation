import asyncio

# Define the global queue
queue = asyncio.Queue()

# Define a task to add items to the queue
async def add_items():
    for i in range(10):
        await queue.put(i)
        await asyncio.sleep_ms(100)

# Define a task to remove items from the queue
async def remove_items():
    while True:
        item = await queue.get()
        print("Got item:", item)
        #await asyncio.sleep_ms(500)

# Start the tasks
async def main():
    await asyncio.gather(
        add_items(),
        remove_items(),
    )

# Run the event loop
loop = asyncio.get_event_loop()
loop.run_until_complete(main())