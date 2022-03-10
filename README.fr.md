# Thimble

[English version](README.md)

Un plugin Minecraft pour le mini jeu dé à coudre pour les serveurs Paper et Spigot de la 1.8 à la 1.18.2.

## Utilisation

### Configuration d'arène

Une fois le plugin installé, vous avez seulement besoin de créer une arène où les joueurs pourront s'affronter.
Connectez-vous à votre serveur et exécutez les commandes suivantes (`<arène>` est à remplacer par le nom de votre choix) :
- `/th arena create <arène>` pour créer une arène
- `/th arena setSpawn <arène>` pour définir l'endroit où les joueurs apparaissent lorsqu'ils rejoignent l'arène
- `/th arena setJump <arène>` pour définir l'endroit d'où les joueurs sauteront

### Jouer

Tapez `/th join <arène>` pour rejoindre une arène. La partie commencera automatiquement lorsqu'il y aura suffisamment de joueurs.

### Pancartes

Tout joueur avec les permissions suffisantes peut créer des pancartes cliquables comme alternatives aux commandes.
Placez une pancarte et écrivez sur la première ligne *[Thimble]* ou *[DéÀCoudre]* (insensible à la casse). Sur la ligne suivante :

- le nom d'une arène à rejoindre
- *block* pour ouvrir l'inventaire de sélection des blocs
- *leave* pour quitter n'importe quelle arène

Note : une fois la pancarte créée, son contenu n'est plus utilisé, vous pouvez la remplacer par des commandes telles que `/setblock` et garder le comportement original !

### PlaceholderAPI

Ce plugin ajoute automatiquement des placeholders si le plugin [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) est installé.
Le classement global est accessible avec la syntaxe : `%thimble_lb_<table>[_<pos>][_name]%` où `table` est `wins`, `losses`, `jumps`, `fails` ou `thimbles` et `pos` un nombre entre 0 et 9.
Exemples :
- `%thimble_lb_wins%`
- `%thimble_lb_losses_3_name%`
- `%thimble_lb_jumps_6_name%`
- `%thimble_lb_thimbles_9%`

Vous pouvez afficher les statistiques du joueur :
- `%thimble_wins%`
- `%thimble_jumps%`
- `%thimble_fails%`
- `%thimble_thimbles%`

### Permissions

- `thimble.commands.arena.*` : `/th arena create`, `/th arena setSpawn`...
- `thimble.commands.player.*` : accès aux commandes joueurs (`/th block`, `/th join` et `/th leave`)
- `thimble.sign.place` : pour placer des pancartes cliquables
- `thimble.commands.stats.*` : accès aux statistiques

Liste complète dans le fichier [plugin.yml](bukkit/src/main/resources/plugin.yml)

### Fichiers de langue

Par défaut, la langue du système est utilisée. Vous pouvez la changer dans le fichier de configuration avec l'option `locale` dans le fichier `config.yml`.
Vous pouvez également personnaliser les messages en créant votre propre fichier de langue.
Pour ce faire, créez un nouveau fichier `plugins/Thimble/messages_en.properties` (remplacer `en` avec la même valeur que l'option `locale`) et ajoutez les messages que vous souhaitez modifier. [Fichier de langue par défaut](common/src/main/resources/messages_fr.properties).
Les messages utilisent le [format de MiniMessage](https://docs.adventure.kyori.net/minimessage.html#format).

### Scoreboards

Depuis la version 1.1.0, le plugin permet de créer un scoreboard personnalisé pour chaque arène. Voici un exemple :
```yml
# plugins/Thimble/scoreboard.yml
default:
  # Le scoreboard par défaut pour toutes les arènes
  title: "<blue>Thimble</blue>"
  lines:
    - "Arène : <yellow><arena></yellow>"
    - ""
    - "Actuel : <dark_green><jumper></dark_green>"
    - "Suivants :"
    - "1. <#d003d0><next_jumper></#d003d0>"
    - "2. <light_purple><next_jumper></light_purple>"
    - ""
    - "Sauts : <gold><jumps></gold>"
    - "Dés à coudre : <gold><thimble></gold>"
  empty:
    # Lorsque ces placeholders retournent des valeurs nulles,
    # utiliser les remplacements suivants :
    jumper: "<gray>aucun</gray>"
    next_jumper: "<gray>aucun</gray>"
```

## API

Suivre la [version anglaise](README.md) du README pour utiliser l'API de *Thimble*.
