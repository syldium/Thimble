Changelog
=========

## [Unreleased]
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
