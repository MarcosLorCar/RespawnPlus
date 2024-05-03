# RespawnPlus Plugin

The RespawnPlus plugin allows players to spectate the player who killed them for a certain period before being able to respawn in place.

## Features

- When a player dies by another player, they are set into spectator mode and can spectate the killer.
- After a specified cooldown period, players are given the option to respawn in the same location where they died.

## Commands

- `/respawncd <time in ticks>` - Set the respawn cooldown time.
- `/checkscoreboard` - Show the deaths scoreboard for a period of time.

## Config

- `deaths_scoreboard` - The scoreboard's name to add the deaths to.
- `scoreboard_time` - The time to show the scoreboard for in ticks.
