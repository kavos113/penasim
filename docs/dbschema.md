# DB Schema

## Game Info Table

### teams

| name     | type   | description |
| -------- | ------ | ----------- |
| id       | int    |             |
| name     | string |             |
| leagueId | int    | 0 or 1      |

### game_fixtures

| name          | type | description             |
| ------------- | ---- | ----------------------- |
| id            | int  |                         |
| date          | date | format: 2025-10-23      |
| numberOfGames | int  | number of games per day |
| homeTeamId    | int  | foreign key             |
| awayTeamId    | int  | foreign key             |

## Player Info Table

### players

| name      | type   | description |
| --------- | ------ | ----------- |
| id        | int    |             |
| firstName | string |             |
| lastName  | string |             |
| teamId    | int    | foreign key |
| meet      | int    |             |
| power     | int    |             |
| speed     | int    |             |
| throwing  | int    |             |
| defense   | int    |             |
| catching  | int    |             |
| ballSpeed | int    |             |
| control   | int    |             |
| stamina   | int    |             |

### player_positions

player's ability

| name     | type   | description                              |
| -------- | ------ | ---------------------------------------- |
| playerId | int    | foreign key                              |
| position | string | enum: PITCHER, CATCHER, ,..., OUTFIELDER |
| defense  | int    |                                          |

### fielder_appointments

| name      | type   | description                     |
| --------- | ------ | ------------------------------- |
| teamId    | int    | foreign key                     |
| playerId  | int    | foreign key                     |
| position  | string | enum:                           |
| isMain    | int    | 1: true. 0: false               |
| number    | int    |                                 |
| orderType | string | enum: NORMAL, LEFT, DH, LEFT_DH |

### pitcher_appointments 

| name     | type   | description                     |
| -------- | ------ | ------------------------------- |
| teamId   | int    | foreign key                     |
| playerId | int    | foreign key                     |
| isMain   | int    | 1: true, 0: false               |
| type     | string | enum: STARTER, RELIEVER, CLOSER |
| number   | int    |                                 |