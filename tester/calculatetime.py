from datetime import datetime

# Convert timestamps to datetime objects
timestamp1 = datetime.strptime("2023/05/17 13:36:57:66", "%Y/%m/%d %H:%M:%S:%f")
timestamp2 = datetime.strptime("2023/05/17 13:36:57:80", "%Y/%m/%d %H:%M:%S:%f")

# Calculate the time difference
time_difference = (timestamp2 - timestamp1).total_seconds()

print(timestamp1)
print(timestamp2)
print(time_difference)
