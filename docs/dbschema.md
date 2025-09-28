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

## Game Result Table

### games

| name          | type | description |
| ------------- | ---- | ----------- |
| gameFixtureId | int  | foreign key |
| homeScore     | int  |             |
| awayScore     | int  |             |

### inning_scores

| name          | type | description |
| ------------- | ---- | ----------- |
| gameFixtureId | int  | foreign key |
| teamId        | int  | foreign key |
| inning        | int  | 1-9,extra   |
| score         | int  |             |

### batting_stats

| name          | type | description   |
| ------------- | ---- | ------------- |
| gameFixtureId | int  | foreign key   |
| playerId      | int  | foreign key   |
| atBat         | int  | exclude walks |
| hit           | int  | include 2,3B  |
| doubleHit     | int  |               |
| tripleHit     | int  |               |
| homeRun       | int  |               |
| walk          | int  |               |
| rbi           | int  |               |
| strikeOut     | int  |               |

### pitching_stats

| name          | type  | description |
| ------------- | ----- | ----------- |
| gameFixtureId | int   | foreign key |
| playerId      | int   | foreign key |
| inningPitched | float | e.g. 4.2    |
| hit           | int   | 被安打      |
| run           | int   | 失点        |
| earnedRun     | int   | 自責点      |
| walk          | int   | 与四球      |
| strikeOut     | int   |             |
| homeRun       | int   |             |
| win           | int   | bool        |
| loss          | int   | bool        |
| hold          | int   | bool        |
| save          | int   | bool        |


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
| starter   | int    | 0-2         |
| reliever  | int    | 0-2         |

### player_positions

player's ability

| name     | type   | description                              |
| -------- | ------ | ---------------------------------------- |
| playerId | int    | foreign key                              |
| position | string | enum: PITCHER, CATCHER, ,..., OUTFIELDER |
| defense  | int    |                                          |

### main_members

1軍/2軍

| name       | type   | description     |
| ---------- | ------ | --------------- |
| teamId     | int    | foreign key     |
| playerId   | int    | foreign key     |
| memberType | string | enum: MAIN, SUB |
| isFielder  | int    | bool            |

### fielder_appointments

| name      | type   | description                         |
| --------- | ------ | ----------------------------------- |
| teamId    | int    | foreign key                         |
| playerId  | int    | foreign key                         |
| position  | string | enum: P,C,1,2,3,S,L,C,R,D,BENCH,SUB |
| number    | int    | main/bench/subで1から               |
| orderType | string | enum: NORMAL, LEFT, DH, LEFT_DH     |

### pitcher_appointments 

| name     | type   | description                          |
| -------- | ------ | ------------------------------------ |
| teamId   | int    | foreign key                          |
| playerId | int    | foreign key                          |
| type     | string | enum: STARTER, RELIEVER, CLOSER, SUB |
| number   | int    |                                      |