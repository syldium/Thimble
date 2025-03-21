Changelog
=========

## [1.5.4] - 2025-03-18
### Fixed
- Fixed commands reported as unknown since Paper 1.21.4 #163.

### Other
- A reduced JAR for Paper 1.18.2+ is now available for download.

## [1.5.3] - 2024-05-13
### Added
- Updated injection points to fully support Minecraft 1.20.5 and 1.20.6.
- Allowed customizing the title of the waiting progress bar via `bossbar.remaining-time` messages.

### Other
- The plugin is now marked as compatible with Mojang mapped servers.

## [1.5.2] - 2024-02-11
### Added
- Updated adventure-platform to fully support Minecraft 1.20.3 and 1.20.4.

### Fixed
- Fixed exception on 1.8 servers when detecting player food level changes.

## [1.5.1] - 2023-10-06
### Added
- Updated adventure-platform to fully support Minecraft 1.20.2.

### Fixed
- Ensure that the restored health is not greater than the maximum health.

## [1.5.0] - 2022-12-26
### Added
- Updated adventure-platform to fully support Minecraft 1.19.3.
- The `/th join` command without any argument allows the player to join a free game with the most players.
- The `%thimble_ar_<arena>_state%` placeholder exposes the current game state in an arena for PlaceholderAPI.
- The `%thimble_ar_<arena>_players%` placeholder exposes the numbers of players in an arena for PlaceholderAPI.
- The `<countdown>` scoreboard placeholder exposes the current countdown in seconds.
- The `<playing>` scoreboard placeholder displays the current number of alive players.
- The `<capacity>` scoreboard placeholder displays the maximum number of players that can be in the current arena.
- A special block can be placed when a thimble is made using the `thimble-block` config option.
- Players can now wait in spectator mode using the `game.waitAsSpectator` config option.

### Fixed
- Fixed PlaceholderAPI placeholder autocompletion.

## [1.4.0] - 2022-06-25
### Added
- Components such as clickable text are now supported on 1.19 servers.
- The block configuration is now reloadable.

### Fixed
- A player name change is now correctly reflected in the database.

### Other
- More information is logged when using database drivers.
- The player name is now stored with a VARCHAR instead of a CHAR type.

## [1.3.0] - 2022-02-03
### Added
- The `<top_player>` placeholder returns the name of the player with the most points/lifes in descending order.
- The `<top_points>` placeholder returns the score with the most points/lifes in descending order.
- API: A `Game` instance now has a leaderboard with the current top players.

### Fixed
- When a leaderboard is updated with the same score, an exception may be thrown.

## [1.2.2] - 2022-01-16
### Fixed
- Support for Brigadier commands in command blocks.
- Rich text components for chat and scoreboard on 1.18.1 servers.

### Other
- MiniMessage updated to 4.10.0-SNAPSHOT (2022-01-16).

## [1.2.1] - 2021-06-26
### Added
- Full compatibility with 1.17 servers.

## [1.2.0] - 2021-04-30
### Added
- Player scores can now be displayed with placeholders.
- API: GameAbortedEvent and JumpVerdictEvent have been added.

### Changed
- The commands at the end of a game are executed after the inventory is restored.
- If all except one of the players leave the game, the game ends with the remaining player as the winner.
- The game task is now run every two ticks instead of every tick.

### Fixed
- Leaving a started game could lead to continuous NPEs.
- The initial order of the jump queue is more random.
- When the reload command is used, some changes were not reflected to the listeners.
- The number of players could be incorrect when they were added with the API.

## [1.1.1] - 2021-03-26
### Fixed
- The leaderboard was disordered at initialization.
- Inventory restoration now uses the `game.clear-inventory` option.

## [1.1.0] - 2021-03-16
### Added
- The *scoreboard.yml* file defines the scoreboards to be displayed for each arena.
- The `commands-at-end` option lets you execute commands at the end of a game.
- The hook into Vault allows choosing how much money players should receive.

### Fixed
- When there were two players left, the one who failed won the game.
- Two messages have been changed to use "jumps" instead of "times".

## [1.0.2] - 2021-03-09
### Added
- The `game.clear-inventory` option determines if the player's inventory should be cleared when joining.

### Fixed
- Aliases were not registered for Brigadier (for supported servers).
- Unable to register aliases in 1.8.

## [1.0.1] - 2021-03-06
### Added
- The `/thimble reload` command reloads the configuration and language files.

### Changed
- The Bukkit aliases can be declared in the configuration file, in `aliases`.
- SQL queries at the end of the game are now batched and use the *UPSERT* syntax.

### Removed
- Some *get* prefixes are removed in the API.
