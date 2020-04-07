# HardcoreParkour
A simple plugin for creating parkour courses and arranging parkour races.

Hopefully simple and flexible.

**Found any bugs? Something does not work? Want a feature? Post in the issues section :)**

### Features:
* Create parkour courses players can join.
* Players can create and join races.
* Players can challenge each other to a race.
* Keeps track of the fastest recorded time for each course.
* All settings can be changed in-game


### Requirements
Spigot/Paper/anything else compatible with the Spigot API.<br>
Built & tested on Minecraft v1.15.2 - could be compatible with earlier & later


## Commands
### [Head over to the command reference in the wiki](https://github.com/SondreKindem/HardcoreParkour/wiki/Command-reference)

## Permissions
The plugin uses generally broad permissions, defined as:
* **hcp.player** - Can play on courses and run different help and info commands. Given to everyone by default.
* **hcp.race.player** - Can join races. Given to everyone by default.
* **hcp.race.create** - Can create races and can start and end their own race. Given to everyone by default.
* **hcp.challenge** - Can challenge other players and can accept challenges. Given to everyone by default.
* **hcp.admin** - Can create, modify and delete courses. Can also force races to start and end. Lastly the admin can modify plugin settings. Given to OP by default.

## Tutorials

### Course tutorial
1. Create an actual parkour course with blocks (duh).
2. Stand on the block where you want the spawn for the course to be. Run `/hcp create <name>`
3. Now for each checkpoint, stand on the block where the checkpoint should be, and type `/hcp addcheckpoint <course-name> [radius] [height]` Height and radius are optional. By default the checkpoint only covers the block you are standing in. Note that if you want to specify a radius, you also have to provide the height.
4. Repeat for each checkpoint. The final checkpoint acts as the goal.
5. Set the kill-height. If players move below this height, they will respawn to the latest checkpoint. Position yourself at the height you want, and use `/hcp setkillheight`. You can specify the height manually if you really want to as well.

Done! Now players can join with `/hcp join <course>`, or create races on your new course.


### Race tutorial
Races are designed to be created by normal players. If you do not want this, you can use permissions to remove access to race creation.
1. create a race with `/hcp race create [course-name] <optional: [time-limit]>`
2. The plugin will announce everyone on the server that a race has been created. The creator of the race does not automatically join the race. Anyone who wishes to join can click the announcement in chat, or run `/hcp race join [name-of-race-creator]`.
3. Once ready use `/hcp race start <optional: [countdown]>`. If there is a countdown another announcement is sent.
4. Players who finish will spectate automatically if the relevant setting is set. 


### Protips:
* The direction you face when creating a course and adding checkpoints is the direction players will look when respawning!
* Checkpoint radius is calculated with the center of the block you are standing on in mind: i.e. a radius of 1 will result in an area covering 3x3 blocks.
* If you add or remove checkpoints for an existing course, you should run `/hcp resethighscore <course>`
* Remember to make sure players can't grief your course!
* Make your checkpoints obvious!
