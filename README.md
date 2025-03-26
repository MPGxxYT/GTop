# GTop

This plugin is used to track the blocks mined in a Gang.
It also has some Skript Integration with the GangsPlusAPI.

## Dependencies
This plugin has two dependencies at the moment.

- [Skript](https://github.com/SkriptLang/Skript)
- [Gangs+](https://www.spigotmc.org/resources/gangs-1-8-1-20.2604/)
## Commands

- `/gtop` Brings up the GTop Menu displaying the stats.
- `/gtop save` Saves the current gang data to file ~ **Perm: gtop.admin**
- `/gtop reload` Reloads the main config ~ **Perm: gtop.admin**


## Skript Expressions

<details> 

<summary>All Time Blocks</summary>

### All Time Blocks : Property Expression
The count of all the blocks a gang has ever mined.

`gang['s] all[ ]time blocks`

example:
```java
set gang's all time blocks of {_gang} to 5
```
</details> 

<details> 

<summary>Gangs Blocks</summary>

### Gangs Blocks : Property Expression
The blocks a gang has mined today in EST time zone.

`gang['s] blocks`

example:
```java
add 15 to gang blocks of player
```

</details> 

<details> 

<summary>Player's Gang</summary>

### Player's Gang : Expression
The gang of a player. Returns `<none>` if not in a gang.

`%player%['s] gang`

example:
```java
set {_gang} to player's gang
```

</details>

## Plans / To-do
- [X] v1.1.6
  - [X] Update Command API to latest
  - [X] Gang stats
- [X] v1.1.7
  - [X] Per Player Breakdown [gang stats]
- [ ] v1.1.8
  - [ ] GTOP Podium (for displaying previous winners)
- [ ] v1.2
  - [ ] Refactor & Optimize Code
