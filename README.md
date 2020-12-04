# DeCoudre
A kind of Minecraft plugin for the "dé à coudre" mini game.

## Usage

### Arena setup

After installing the plugin, you just need to create an arena where players can compete against each other.
Connect to your server and execute the following commands (`<arena>` is to be replaced by the name of your choice):
- `/dac arena create <arena>` to create an arena
- `/dac arena setSpawn <arena>` to define the location where players appear when they join the arena
- `/dac arena setJump <arena>` to define where players will jump from

### Playing

Type `/dac join <arena>` to join an arena. The game will start automatically when there are enough players.

### PlaceholderAPI

This plugin automatically registers placeholders if PlaceholderAPI is installed.
Currently, only the leaderboard is accessible with the syntax: `%decoudre_lb_<table>[_<pos>][_name]%` where `table` is `wins`, `losses`, `jumps` or `dacs` and `pos` a number from 0 to 9.
Examples:
- `%decoudre_lb_wins%`
- `%decoudre_lb_losses_3_name%`
- `%decoudre_lb_jumps_6_name%`
- `%decoudre_lb_dacs_9%`

### Permissions

- *decoudre.player.**: access to commands for players (`/dac block`, `/dac join`, `/dac leave`)
- *decoudre.arena.**: `/dac arena create`, `/dac arena setSpawn`...

Full list in the [plugin.yml](bukkit/src/main/resources/plugin.yml) file

## Building

This project uses Gradle. Use `./gradlew shadowJar` to get a .jar file (under Windows use `./gradle.bat` instead of `./gradlew`).
