import csv
import sys
import random

players_row = ["id", "firstName", "lastName", "teamId", "meet", "power", "speed", "throwing", "defense", "catching", "ballSpeed", "control", "stamina"]
player_positions_row = ["playerId", "position", "defense"]
pitcher_appointments_row = ["teamId", "playerId", "type", "number"]
fielder_appointments_row = ["teamId", "playerId", "position", "number", "orderType"]
main_members_row = ["teamId", "playerId", "memberType", "isFielder"]

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
    9: "RIGHT_FIELDER",
    10: "DH",
    11: "BENCH",
    12: "SUBSTITUTE"
}

pitcher_type_map = {
    0: "STARTER",
    1: "RELIEVER",
    2: "CLOSER",
    3: "SUB"
}

order_type_map = {
    0: "NORMAL",
    1: "LEFT",
    2: "DH",
    3: "LEFT_DH"
}

member_type_map = {
    0: "MAIN",
    1: "SUB"
}

def create_players(teamId, fielder_count, pitcher_count, csv_dir):
    players = []
    player_positions = []
    pitcher_appointments = []
    fielder_appointments = []
    main_members = []

    # create fielders
    # starting members
    for i in range(1, 10):
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
        position = i if i < 6 else i + 1
        player_position = position
        if player_position >= 6:
            player_position = 6

        player_positions.append([player_id, position_map[player_position], defense])
        main_members.append([teamId, player_id, member_type_map[0], 1])  # main member, fielder
        # order type
        for j in range(4):
            if i == 9:
                if j >= 2:
                    fielder_appointments.append([teamId, player_id, position_map[10], i, order_type_map[j]])
                else:
                    fielder_appointments.append([teamId, player_id, position_map[11], 17 - 8, order_type_map[j]])
            else:
                fielder_appointments.append([teamId, player_id, position_map[position], i, order_type_map[j]])

    # pitcher as fielder
    player_id = len(players) + 1
    players.append([player_id, f"Pitcher", "Pitcher", teamId, 0, 0, 0, 0, 0, 0, 0, 0, 0])
    player_positions.append([player_id, position_map[0], 0])
    fielder_appointments.append([teamId, player_id, position_map[0], 9, order_type_map[0]])
    fielder_appointments.append([teamId, player_id, position_map[0], 9, order_type_map[1]])

    # bench fielder
    for i in range(9, 17):
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
        main_members.append([teamId, player_id, member_type_map[0], 1])  # main member, fielder
        for j in range(4):
            fielder_appointments.append([teamId, player_id, position_map[11], i - 8, order_type_map[j]])

    # sub fielder
    for i in range(17, fielder_count):
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
        main_members.append([teamId, player_id, member_type_map[1], 1])  # sub member, fielder
        for j in range(4):
            fielder_appointments.append([teamId, player_id, position_map[12], i - 16, order_type_map[j]])

    # create pitchers
    num_starters = 0
    num_relievers = 0
    num_closers = 0
    num_sub = 0
    # main
    for i in range(12):
        pitcher_type = 0
        if i == 0:
            pitcher_type = 2
        elif i < 6:
            pitcher_type = 0
        elif i < 13:
            pitcher_type = 1

        player_id = len(players) + 1
        
        player_number = 0
        if pitcher_type == 0:
            player_number = num_starters + 1
            num_starters += 1
        elif pitcher_type == 1:
            player_number = num_relievers + 1
            num_relievers += 1
        elif pitcher_type == 2:
            player_number = num_closers + 1
            num_closers += 1

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

        pitcher_appointments.append([teamId, player_id, pitcher_type_map[pitcher_type], player_number])
        main_members.append([teamId, player_id, member_type_map[0], 0])  # main member, pitcher

    # sub
    for i in range(12, pitcher_count):
        player_id = len(players) + 1
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

        pitcher_appointments.append([teamId, player_id, pitcher_type_map[3], player_number])
        main_members.append([teamId, player_id, member_type_map[1], 0])  # sub member, pitcher

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

    with open(f"{csv_dir}/main_members.csv", "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(main_members_row)
        writer.writerows(main_members)

if __name__ == "__main__":
    if len(sys.argv) != 5:
        print("Usage: python player.py <teamId> <fielder_count> <pitcher_count> <csv_dir>")
        sys.exit(1)

    teamId = int(sys.argv[1])
    fielder_count = int(sys.argv[2])
    pitcher_count = int(sys.argv[3])
    csv_dir = sys.argv[4]

    create_players(teamId, fielder_count, pitcher_count, csv_dir)