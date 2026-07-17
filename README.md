# FakePlayer

A fork of FakePlayer maintained by hxrry27

This is a server side plugin inspired by [Carpet-Mod](https://github.com/gnembon/fabric-carpet) for Minecraft, aiming specifically at 26.X

## Features

+ Emulates the `/player` command from carpet, for paper/purpur servers
+ Each player has it's own configuration

## Commands


| Command       | Description                               | Permission                   | Note                                                            |
| ------------- | ----------------------------------------- | ---------------------------- | --------------------------------------------------------------- |
| /fp spawn     | Spawn a fake player                       | fakeplayer.command.spawn     |                                                                 |
| /fp kill      | Kill a fake player                        | fakeplayer.command.kill      |                                                                 |
| /fp killall   | Kill all fake players on the server       | OP                           |                                                                 |
| /fp select    | Select a fake player as default           | fakeplayer.command.select    | Available  when player spawned more then 1 fake players         |
| /fp selection | View selected fake player                 | fakeplayer.command.selection | Available  only when player spawned more then 1 fake players    |
| /fp list      | List spawned fake players                 | fakeplayer.command.list      |                                                                 |
| /fp distance  | Show distance to a fake player            | fakeplayer.command.distance  |                                                                 |
| /fp drop      | Drop held item                            | fakeplayer.command.drop      |                                                                 |
| /fp dropstack | Drop entire stack of the held item        | fakeplayer.command.dropstack |                                                                 |
| /fp dropinv   | Drop all items in the inventory           | fakeplayer.command.dropinv   |                                                                 |
| /fp skin      | Copy skin from another player             | fakeplayer.command.skin      | 60 seconds cooldown if copy from a offline player               |
| /fp invsee    | Open an inventory of a fake player        | fakeplayer.command.invsee    | Right-clicking on fake players has the same effect              |
| /fp sleep     | Sleep                                     | fakeplayer.command.sleep     |                                                                 |
| /fp wakeup    | Wake up                                   | fakeplayer.command.wakeup    |                                                                 |
| /fp status    | Show status                               | fakeplayer.command.status    |                                                                 |
| /fp respawn   | Respawn a dead fake player                | fakeplayer.command.respawn   | Available when server config does not kick on fake player death |
| /fp tp        | Teleport to a fake player                 | fakeplayer.command.tp        |                                                                 |
| /fp tphere    | Teleport a fake player to you             | fakeplayer.command.tphere    |                                                                 |
| /fp tps       | Swap positions with fake player           | fakeplayer.command.tps       |                                                                 |
| /fp set       | Change the configuration of a fake player | fakeplayer.command.set       |                                                                 |
| /fp config    | Change default configuration              | fakeplayer.command.config    |                                                                 |
| /fp expme     | Transfer exp to you                       | fakeplayer.command.expme     |                                                                 |
| /fp attack    | Attack                                    | fakeplayer.command.attack    |                                                                 |
| /fp mine      | Mine                                      | fakeplayer.command.mine      |                                                                 |
| /fp use       | Use/Interact/Place                        | fakeplayer.command.use       |                                                                 |
| /fp jump      | Jump                                      | fakeplayer.command.jump      |                                                                 |
| /fp stop      | Stop all actions                          | fakeplayer.command.stop      |                                                                 |
| /fp turn      | Turn around                               | fakeplayer.command.turn      |                                                                 |
| /fp look      | Look at specified location                | fakeplayer.command.look      |                                                                 |
| /fp move      | Move                                      | fakeplayer.command.mvoe      |                                                                 |
| /fp ride      | Ride                                      | fakeplayer.command.ride      |                                                                 |
| /fp sneak     | Sneak                                     | fakeplayer.command.sneak     |                                                                 |
| /fp sprint    | Sprinting                                 | fakeplayer.command.sprint    |                                                                 |
| /fp swap      | Swap main and off-hand items              | fakeplayer.command.swap      |                                                                 |
| /fp hold      | Hold corresponding hotbar item            | fakeplayer.command.hold      |                                                                 |
| /fp cmd       | Execute command                           | fakeplayer.command.cmd       |                                                                 |
| /fp reload    | Reload config file                        | OP                           |                                                                 |


## Config

| Configuration Item | Note                                                                                                                               |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------- |
| collidable         | Whether collision box is enabled                                                                                                   |
| invulnerable       | Whether invincible mode is enabled                                                                                                 |
| wolverine          | Whether super heal mode is enabled                                                                                                 |
| look_at_entity     | Automatically look at nearby attackable entities (including players), can be combined with`attack` to automatically fight monsters |
| pickup_items       | Whether to pick up items                                                                                                           |
| skin               | Whether to use your skin                                                                                                           |
| replenish          | Whether to auto-replenish                                                                                                          |
| autofish           | Whether to autofish                                                                                                                |

## Permissions

On top of individual permission nodes noted in this readme, the below packs come as a default

### Permission `fakeplayer.spawn`

`fakeplayer.spawn` includes the following permissions:

- fakeplayer.command.spawn - Create fake player
- fakeplayer.command.kill - Kill fake player
- fakeplayer.command.list - List fake players
- fakeplayer.command.distance - View distance
- fakeplayer.command.select - Select fake player
- fakeplayer.command.selection - View selected fake player
- fakeplayer.command.drop - Drop an item
- fakeplayer.command.dropstack - Drop entire stack of items
- fakeplayer.command.dropinv - Drop all inventory items
- fakeplayer.command.skin - Copy skin
- fakeplayer.command.invsee - View inventory
- fakeplayer.command.status - View status
- fakeplayer.command.respawn - Respawn fake player
- fakeplayer.command.config - Set default settings
- fakeplayer.command.set - Set fake player settings

### Permission `fakeplayer.tp`

`fakeplayer.tp` includes the following permissions:

- fakeplayer.command.tp
- fakeplayer.command.tphere
- fakeplayer.command.tps

### Permission `fakeplayer.action`

`fakeplayer.action` includes the following permissions:

- fakeplayer.command.attack - Attack
- fakeplayer.command.mine - Mine
- fakeplayer.command.use - Use
- fakeplayer.command.jump - Jump
- fakeplayer.command.sneak - Sneak
- fakeplayer.command.sprint - Sprinting
- fakeplayer.command.look - Look
- fakeplayer.command.turn - Turn
- fakeplayer.command.move - Move
- fakeplayer.command.ride - Ride
- fakeplayer.command.swap - Swap main and off-hand items
- fakeplayer.command.sleep - Sleep
- fakeplayer.command.wakeup - Wake up
- fakeplayer.command.stop - Stop all actions
- fakeplayer.command.hold - Switch hotbar
- fakeplayer.config.replenish - Auto-replenish
- fakeplayer.config.replenish.chest - Can replenish from nearby chests when auto-replenishing
- fakeplayer.config.autofish - Autofish

## Placeholder Variables

+ `%fakeplayer_total%`: Total count of fake players
+ `%fakeplayer_creator%`: The creator name of a fake player
+ `%fakeplayer_actions`: Active actions of a fake player such as : `USE|ATTACK`
