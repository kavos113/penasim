import csv
import sys
import random

players_row = ["id", "firstName", "lastName", "teamId", "meet", "power", "speed", "throwing", "defense", "catching", "ballspeed", "control", "stamina"]
player_positions_row = ["playerId", "position", "defense"]
pitcher_appointments_row = ["teamId", "playerId", "isMain", "type", "number"]
fielder_appointments_row = ["teamId", "playerId", "position", "isMain", "number"]

def create_players(teamId, fielder_count, pitcher_count, csv_dir):
    players = []
    player_positions = []
    pitcher_appointments = []
    fielder_appointments = []

    # create fielders
    isMain = True
    for i in range(fielder_count):
        player_id = len(players) + 1
        defense = random.randint(10, 90)
        players.append([
            player_id, 
            f"First{i}",
            f"Last{i}", 
            teamId,
            random.randint(10, 90),
            random.randint(10, 90),
            random.randint(10, 90),
            random.randint(10, 90),
            defense,
            random.randint(10, 90),
            120,
            1,
            1
        ])

        position = random.randint(1, 8)
        if position >= 6:
            position = 6
        player_positions.append([player_id, position, defense])
        fielder_appointments.append([teamId, player_id, position, 1 if isMain else 0, i + 1])

        isMain = len(fielder_appointments) <= 16

    # create pitchers
    isMain = True
    closerCount = 0
    for i in range(pitcher_count):
        player_id = len(players) + 1
        defense = random.randint(10, 50)
        players.append([
            player_id,
            f"PFirst{i}",
            f"PLast{i}",
            teamId,
            random.randint(10, 30),
            random.randint(10, 30),
            random.randint(10, 60),
            random.randint(50, 90),
            defense,
            random.randint(10, 50),
            random.randint(140, 160),
            random.randint(10, 90),
            random.randint(10, 90)
        ])

        player_positions.append([player_id, 0, defense])

        pitcher_type = random.randint(0, 2) if closerCount < 5 else random.randint(0, 1)
        pitcher_appointments.append([teamId, player_id, 1 if isMain else 0, pitcher_type, i + 1])

        isMain = len(pitcher_appointments) <= 12
        if pitcher_type == 2:
            closerCount += 1
        

    # write csv
    with open(f"{csv_dir}/players.csv", "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(players_row)
        writer.writerows(players)

    with open(f"{csv_dir}/player_positions.csv", "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(player_positions_row)
        writer.writerows(player_positions)

    with open(f"{csv_dir}/pitcher_appointments.csv", "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(pitcher_appointments_row)
        writer.writerows(pitcher_appointments)

    with open(f"{csv_dir}/fielder_appointments.csv", "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(fielder_appointments_row)
        writer.writerows(fielder_appointments)

if __name__ == "__main__":
    if len(sys.argv) != 5:
        print("Usage: python player.py <teamId> <fielder_count> <pitcher_count> <csv_dir>")
        sys.exit(1)

    teamId = int(sys.argv[1])
    fielder_count = int(sys.argv[2])
    pitcher_count = int(sys.argv[3])
    csv_dir = sys.argv[4]

    create_players(teamId, fielder_count, pitcher_count, csv_dir)