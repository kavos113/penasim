import csv
import datetime

FILE_NAME = 'dates.csv'
COLUMNS = ['id', 'totalDay', 'month', 'day', 'dayOfWeek', 'numberOfGames', 'homeTeamId', 'awayTeamId']

START = datetime.date(2025, 3, 28)
END = datetime.date(2025, 10, 4)

time = START
delta = datetime.timedelta(days=1)

data = []

while time <= END:
    total_day = (time - START).days + 1
    month = time.month
    day = time.day
    day_of_week = time.weekday()  # Monday is 0 and Sunday is 6

    for i in range(6):
        data.append({
            'id': len(data) + 1,
            'totalDay': total_day,
            'month': month,
            'day': day,
            'dayOfWeek': day_of_week,
            'numberOfGames': i,
            'homeTeamId': 0,
            'awayTeamId': 0
        })

    time += delta

with open(FILE_NAME, mode='w', newline='') as file:
    writer = csv.DictWriter(file, fieldnames=COLUMNS)
    writer.writeheader()
    writer.writerows(data)