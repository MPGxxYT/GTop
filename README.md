# GTop

This plugin is used to track the blocks mined in a Gang.

## Dependencies
This plugin has two dependencies at the moment.

 - [Skript](https://github.com/SkriptLang/Skript)
 - [Gangs+](https://www.spigotmc.org/resources/gangs-1-8-1-20.2604/)
## Commands

`/gtop` Brings up the GTop Menu displaying the stats.


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

#### Gangs Blocks : Property Expression
The blocks a gang has mined today in EST time zone.

`gang['s] blocks`

example: 
```java
add 15 to gang's blocks of {_gang}
```

</details> 

<details> 

<summary>Player's Gang</summary>

#### Player's Gang : Expression
The gang of a player. Returns `<none>` if not in a gang.

`%player%['s] gang`

example: `set {_gang} to player's gang`

</details> 

## Plans / To-do
- [ ] v1.1 ( In Progress )
  - [ ] Add Config
- [ ] v1.2 
  - [ ] Refactor Code