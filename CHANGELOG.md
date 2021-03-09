Changelog
=========

## [1.0.2] - 2020-03-09
### Added
- The `game.clear-inventory` option determines if the player's inventory should be cleared when joining.

### Fixed
- Aliases were not registered for Brigadier (for supported servers).
- Unable to register aliases in 1.8.

## [1.0.1] - 2020-03-06
### Added
- The `/thimble reload` command reloads the configuration and language files.

### Changed
- The Bukkit aliases can be declared in the configuration file, in `aliases`.
- SQL queries at the end of the game are now batched and use the *UPSERT* syntax.

### Removed
- Some *get* prefixes are removed in the API.
