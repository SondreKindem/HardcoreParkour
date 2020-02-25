# HardcoreParkour
A simple plugin for creating parkour courses and arranging parkour races.

### Requirements
Spigot/Paper/anything else compatible with the Spigot API
Built & tested on Minecraft v1.15.2 - could be compatible with earlier & later

### Commands & permissions
[Head over to the command reference in the wiki](https://github.com/SondreKindem/HardcoreParkour/wiki/Command-reference)

### Tutorial
1. Create a parkour course (duh).
2. Stand on the block you want the spawn for the course to be. Run `/hcp create [name]`
3. Now for each checkpoint you want, stand in the center of the area you want you checkpoint, and run `/hcp addcheckpoint [course-name] <radius> <height>` Height and radius are optional, defaulting to a value of 1.
4. Repeat for each checkpoint. The final checkpoint acts as the goal.
5. Set the kill-height. If players move below this height, they will respawn to the latest checkpoint. Position yourself at the height you want, and use `/hcp setkillheight`. You can specify the height manually if you really want to as well.

Done! Now players can join with `/hcp join [name]`, or create races on your new course. Remember to make sure players can't grief your course!
