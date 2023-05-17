from datetime import datetime

# Convert timestamps to datetime objects
timestamp1 = datetime.strptime("2023/05/17 13:21:49:99", "%Y/%m/%d %H:%M:%S:%f")
timestamp2 = datetime.strptime("2023/05/17 13:21:50:66", "%Y/%m/%d %H:%M:%S:%f")

# Calculate the time difference
time_difference = (timestamp2 - timestamp1).total_seconds()

print(time_difference)
