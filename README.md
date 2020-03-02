# HardcoreParkour
A simple plugin for creating parkour courses and arranging parkour races.

### Requirements
Spigot/Paper/anything else compatible with the Spigot API.<br>
Built & tested on Minecraft v1.15.2 - could be compatible with earlier & later

### Commands & permissions
[Head over to the command reference in the wiki](https://github.com/SondreKindem/HardcoreParkour/wiki/Command-reference)

### Tutorial
1. Create a parkour course (duh).
2. Stand on the block where you want the spawn for the course to be. Run `/hcp create <name>`
3. Now for each checkpoint, stand in the center of where the checkpoint should be, and run `/hcp addcheckpoint <course-name> [radius] [height]` Height and radius are optional, defaulting to a value of 0, resulting in a size of one block. Note that if you want to specify a radius, you also have to provide the height.
4. Repeat for each checkpoint. The final checkpoint acts as the goal.
5. Set the kill-height. If players move below this height, they will respawn to the latest checkpoint. Position yourself at the height you want, and use `/hcp setkillheight`. You can specify the height manually if you really want to as well.

Done! Now players can join with `/hcp join <name>`, or create races on your new course.

### Protips:
* The direction you face when creating a course and adding checkpoints is the direction players will look when respawning!
* Remember to make sure players can't grief your course!
* Mark your checkpoints with colored blocks!
