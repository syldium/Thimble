Changelog
=========

## [Unreleased]
### Changed
- The commands at the end of a game are executed after the inventory is restored.
- If all except one of the players leave the game, the game ends with the remaining player as the winner.

### Fixed
- Leaving a started game could lead to continuous NPEs.
- The jump queue was far from being random.
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
