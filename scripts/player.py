import csv
import sys
import random

players_row = ["id", "firstName", "lastName", "teamId", "meet", "power", "speed", "throwing", "defense", "catching", "ballSpeed", "control", "stamina"]
player_positions_row = ["playerId", "position", "defense"]
pitcher_appointments_row = ["teamId", "playerId", "isMain", "type", "number"]
fielder_appointments_row = ["teamId", "playerId", "position", "isMain", "number"]

position_map = {
    0: "PITCHER",
    1: "CATCHER",
    2: "FIRST_BASEMAN",
    3: "SECOND_BASEMAN",
    4: "SHORTSTOP",
    5: "THIRD_BASEMAN",
    6: "OUTFIELDER",
    7: "LEFT_FIELDER",
    8: "CENTER_FIELDER",
    9: "RIGHT_FIELDER"
}

pitcher_type_map = {
    0: "STARTER",
    1: "RELIEVER",
    2: "CLOSER"
}

def create_players(teamId, fielder_count, pitcher_count, csv_dir):
    players = []
    player_positions = []
    pitcher_appointments = []
    fielder_appointments = []

    # create fielders
    # starting members
    for i in range(1, 9):
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
        position = i if i != 6 else 9
        player_positions.append([player_id, position_map[position], defense])
        fielder_appointments.append([teamId, player_id, position_map[position], 1, i])
    # pitcher as fielder
    player_id = len(players) + 1
    players.append([player_id, f"Pitcher", "", teamId, 0, 0, 0, 0, 0, 0, 0, 0, 0])
    player_positions.append([player_id, position_map[0], 0])
    fielder_appointments.append([teamId, player_id, position_map[0], 1, 9])
    # substitute members
    isMain = True
    for i in range(9, fielder_count):
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
        player_positions.append([player_id, position_map[position], defense])
        fielder_appointments.append([teamId, player_id, position_map[position], 1 if isMain else 0, i + 1])

        isMain = len(fielder_appointments) <= 16

    # create pitchers
    isMain = True
    num_starters = 0
    num_relievers = 0
    num_closers = 0
    num_sub = 0
    for i in range(pitcher_count):
        pitcher_type = random.randint(0, 1)  
        if isMain: 
            if i == 0:
                pitcher_type = 2
            elif i < 6:
                pitcher_type = 0
            elif i < 13:
                pitcher_type = 1

        player_id = len(players) + 1
        
        player_number = 0
        if isMain:
            if pitcher_type == 0:
                player_number = num_starters + 1
                num_starters += 1
            elif pitcher_type == 1:
                player_number = num_relievers + 1
                num_relievers += 1
            elif pitcher_type == 2:
                player_number = num_closers + 1
                num_closers += 1
        else:
            player_number = num_sub + 1
            num_sub += 1

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

        player_positions.append([player_id, position_map[0], defense])

        pitcher_appointments.append([teamId, player_id, 1 if isMain else 0, pitcher_type_map[pitcher_type], player_number])

        if len(pitcher_appointments) >= 12:
            isMain = False

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