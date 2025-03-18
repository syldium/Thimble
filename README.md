# Thimble

[Version française](README.fr.md)

A Minecraft plugin for the thimble (dé à coudre) mini-game for Paper and Spigot servers from 1.8 to 1.21.4.

## Usage

### Arena setup

After installing the plugin, you just need to create an arena where players can compete against each other.
Connect to your server and execute the following commands (`<arena>` is to be replaced by the name of your choice):
- `/th arena create <arena>` to create an arena
- `/th arena setSpawn <arena>` to define the location where players appear when they join the arena
- `/th arena setJump <arena>` to define where players will jump from

### Playing

Type `/th join <arena>` to join an arena. The game will start automatically when there are enough players.

### Signs

Every player who has the relevant permissions can create clickable signs as an alternative to player commands.
Place a sign and write on the first line *[Thimble]* or *[DéÀCoudre]* (case-insensitive). On the next line:

- the name of an arena to join
- *block* to open the block selection inventory
- *leave* to leave any arena

Note: once the sign is created, its content is no longer used, you can replace it with commands such as `/setblock` and keep the original behavior!

### PlaceholderAPI

This plugin automatically registers placeholders if [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is installed.
The leaderboard is accessible with the syntax: `%thimble_lb_<table>[_<pos>][_name]%` where `table` is `wins`, `losses`, `jumps`, `fails` or `thimbles` and `pos` a number from 0 to 9.
Examples:
- `%thimble_lb_wins%`
- `%thimble_lb_losses_3_name%`
- `%thimble_lb_jumps_6_name%`
- `%thimble_lb_thimbles_9%`

You can display the player's statistics:
- `%thimble_wins%`
- `%thimble_jumps%`
- `%thimble_fails%`
- `%thimble_thimbles%`

Two placeholders are also registered for every arena. They expose the current state of the game (`state`) and the number of players currently in (`players`).
For the `demo` arena for instance, the placeholders will be:
- `%thimble_ar_demo_state%`
- `%thimble_ar_demo_players%`

### Permissions

- `thimble.commands.arena.*`: `/th arena create`, `/th arena setSpawn`...
- `thimble.commands.player.*`: access to commands for players (`/th block`, `/th join` and `/th leave`)
- `thimble.sign.place`: to place clickable signs
- `thimble.commands.stats.*`: access to statistics

Full list in the [plugin.yml](bukkit/src/main/resources/plugin.yml) file

### Language files

By default, the system language is used. You can change it in the configuration file with the `locale` option in the `config.yml` file.
You can also customize the messages by creating your own locale file.
To do this, create a new file `plugins/Thimble/messages_en.properties` (replace `en` with the same value as in `locale`) and add the messages you want to modify. [Here is the default language file](common/src/main/resources/messages.properties).
The messages use the [MiniMessage formatting](https://docs.adventure.kyori.net/minimessage.html#format).

### Scoreboards

As of version 1.1.0, the plugin allows you to create a custom scoreboard for each arena. Here's two examples:
```yml
# plugins/Thimble/scoreboard.yml
default:
  # The default scoreboard for all arenas
  title: "<blue>Thimble</blue>"
  lines:
    - "Arena: <yellow><arena></yellow>"
    - ""
    - "Current: <dark_green><jumper></dark_green>"
    - "Next players:"
    - "1. <#d003d0><next_jumper></#d003d0>"
    - "2. <light_purple><next_jumper></light_purple>"
    - ""
    - "Jumps: <gold><jumps></gold>"
    - "Thimbles: <gold><thimble></gold>"
  empty:
    # When these placeholders return null values,
    # use the following replacements:
    jumper: "<gray>none</gray>"
    next_jumper: "<gray>none</gray>"
```

```yml
# plugins/Thimble/scoreboard.yml
default:
  # The default scoreboard for all arenas
  title: "<blue>Thimble</blue>"
  lines:
    - "Arena: <yellow><arena></yellow>"
    - ""
    - "Current: <dark_green><jumper></dark_green>"
    - "Best: <#d003d0><top_player></#d003d0> (<top_points>)"
    - "Time: <yellow><countdown></yellow> sec"
    - ""
    - "<points> <red>❤</red>"
    - "Players: <yellow><playing></yellow>/<capacity>"
  empty:
    # When these placeholders return null values,
    # use the following replacements:
    top_player: "<gray>none</gray>"
    top_points: "<gray>0</gray>"
```

## API

### Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.syldium</groupId>
    <artifactId>Thimble</artifactId>
    <version>1.5.4</version>
    <scope>provided</scope>
</dependency>
```

### Usage

Your plugin must be loaded after Thimble using the plugin.yml file:
```yml
depend: [Thimble] # If Thimble must be installed.
soft-depend: [Thimble] # If Thimble is not required.
```
When the plugin is enabled on a Bukkit server, the instance of `GameService` and `StatsService` will be provided by the Bukkit ServiceManager.
```java
public final class ThimblePluginHook extends JavaPlugin {
    @Override
    public void onEnable() {
        if (this.getServer().getPluginManager().isPluginEnabled("Thimble")) { // If it's a soft-depend
            this.getServer().getServicesManager().load(GameService.class);
        }
    }
}
```
You can view the methods using your IDE or [the javadoc](https://javadoc.jitpack.io/com/github/syldium/Thimble/1.5.4/javadoc/).

## Building

This project uses Gradle. Use `./gradlew build` to get a .jar file (under Windows use `./gradle.bat` instead of `./gradlew`).
