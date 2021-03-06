Changelog
=========

## [1.0.1] - 2020-03-06
### Added
- The `/thimble reload` command reloads the configuration and language files.

### Changed
- The Bukkit aliases can be declared in the configuration file, in `aliases`.
- SQL queries at the end of the game are now batched and use the *UPSERT* syntax.

### Removed
- Some *get* prefixes are removed in the API.
