package no.sonkin.hardcoreParkour;

import com.github.hornta.Carbon;
import com.github.hornta.CarbonArgument;
import com.github.hornta.CarbonArgumentType;
import com.github.hornta.CarbonCommand;
import no.sonkin.hardcoreParkour.commands.*;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import no.sonkin.hardcoreParkour.objects.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

/*
 * TODO: BUG - Sometimes changes since restart are removed from config, but are added again when saveConfig() is ran. I.e the in-memory config is correct, but for some reason the on-disk config reverts?
 * TODO: format messages properly
 *
 * FUTURE FEATURES:
 * TODO: make race autojoin - joins any race on a given course or creates one if there is none
 * TODO: show particles around checkpoint (optional)
 * TODO: let course creator define a lobby location
 * TODO: ability to add checkpoint from worldedit
 * TODO: ability to spectate a race from start to finish
 * TODO: vote start? low priority
 * TODO: chain courses - auto start another course when one is finished
 * TODO: let race creator offer a prize item to race over
 * TODO: use placeholderapi as softdep to show race countdown and winner status in a placeholder instead of chat
 * TODO: use placeholderapi to display players time
 * TODO: Write tests
 * TODO: Check for new release on github when plugin is enabled
 */

public class Main extends JavaPlugin {
    private static Main instance;
    private Carbon carbon;
    private ParkourController parkourController;
    private Settings settings;
    private GhostFactory ghostFactory;

    /**
     * Gets the plugin instance
     *
     * @return an instance of Main
     */
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        carbon = new Carbon();
        parkourController = new ParkourController();
        ghostFactory = new GhostFactory(this);
        this.settings = new Settings(this.getConfig().getConfigurationSection("settings"));

        carbon.addCommand("hcp").withHandler(new CommandHelp(parkourController));
        // CREATION AND MODIFICATION
        carbon.addCommand("hcp create")
                .withHandler(new CommandCreate(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp addcheckpoint")
                .withHandler(new CommandAddCheckpoint(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .withArgument(new CarbonArgument.Builder("Radius").setType(CarbonArgumentType.INTEGER).setDefaultValue(-1).create())
                .withArgument(new CarbonArgument.Builder("height").setType(CarbonArgumentType.INTEGER).setDefaultValue(-1).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp setkillheight")
                .withHandler(new CommandKillHeight(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .withArgument(new CarbonArgument.Builder("height").setType(CarbonArgumentType.INTEGER).setDefaultValue(-1).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp setrespawnheight")
                .withHandler(new CommandKillHeight(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .withArgument(new CarbonArgument.Builder("height").setType(CarbonArgumentType.INTEGER).setDefaultValue(-1).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp remove")
                .withHandler(new CommandRemove(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp delete")
                .withHandler(new CommandRemove(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp clearcheckpoints")
                .withHandler(new CommandClearCheckpoints(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp replacestart")
                .withHandler(new CommandReplaceStart(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin");
        // ADMIN COMMANDS
        carbon.addCommand("hcp goto")
                .withHandler(new CommandGoto(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp gotocheckpoint")
                .withHandler(new CommandGotoCheckpoint(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .withArgument(new CarbonArgument.Builder("id").setType(CarbonArgumentType.INTEGER).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp race forcestart")
                .withHandler(new CommandRaceForceStart(parkourController))
                .withArgument(new CarbonArgument.Builder("race").setType(CarbonArgumentType.STRING).create())
                .withArgument(new CarbonArgument.Builder("countdown").setType(CarbonArgumentType.INTEGER).setDefaultValue(0).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp race forceend")
                .withHandler(new CommandRaceForceEnd(parkourController))
                .withArgument(new CarbonArgument.Builder("race").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp resethighscore")
                .withHandler(new CommandResetHighscore(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp open")
                .withHandler(new CommandOpen(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp close")
                .withHandler(new CommandClose(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.admin");
        // PLAYER SESSION
        carbon.addCommand("hcp join")
                .withHandler(new CommandJoin(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.player")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp leave")
                .withHandler(new CommandLeave(parkourController))
                .requiresPermission("hcp.player")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp respawn")
                .withHandler(new CommandRespawn(parkourController))
                .requiresPermission("hcp.player")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp restart")
                .withHandler(new CommandRestart(parkourController))
                .requiresPermission("hcp.player")
                .preventConsoleCommandSender();
        // MISC COMMANDS
        carbon.addCommand("hcp info")
                .withHandler(new CommandInfo(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.player");
        carbon.addCommand("hcp list")
                .withHandler(new CommandList(parkourController))
                .requiresPermission("hcp.player");
        carbon.addCommand("hcp help")
                .withHandler(new CommandHelp(parkourController))
                .requiresPermission("hcp.player");
        carbon.addCommand("hcp usage")
                .withHandler(new CommandUsage(parkourController))
                .withArgument(new CarbonArgument.Builder("page").setType(CarbonArgumentType.INTEGER).setDefaultValue(1).create())
                .requiresPermission("hcp.player");
        carbon.addCommand("hcp commands")
                .withHandler(new CommandUsage(parkourController))
                .withArgument(new CarbonArgument.Builder("page").setType(CarbonArgumentType.INTEGER).setDefaultValue(1).create())
                .requiresPermission("hcp.player");
        carbon.addCommand("hcp courses")
                .withHandler(new CommandList(parkourController))
                .requiresPermission("hcp.player");
        carbon.addCommand("hcp race list")
                .withHandler(new CommandRaceList(parkourController))
                .requiresPermission("hcp.player");
        carbon.addCommand("hcp races")
                .withHandler(new CommandRaceList(parkourController))
                .requiresPermission("hcp.player");
        // RACE COMMANDS
        carbon.addCommand("hcp race")
                .withHandler(new CommandRace(parkourController))
                .requiresPermission("hcp.race.player");
        carbon.addCommand("hcp race help")
                .withHandler(new CommandRace(parkourController))
                .requiresPermission("hcp.race.player");
        carbon.addCommand("hcp race join")
                .withHandler(new CommandRaceJoin(parkourController))
                .withArgument(new CarbonArgument.Builder("race").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.race.player")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp race leave")
                .withHandler(new CommandLeave(parkourController))
                .requiresPermission("hcp.race.player")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp challenge")
                .withHandler(new CommandChallenge(parkourController))
                .withArgument(new CarbonArgument.Builder("player").setType(CarbonArgumentType.STRING).create())
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .requiresPermission("hcp.challenge")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp accept")
                .withHandler(new CommandAccept(parkourController))
                .requiresPermission("hcp.challenge")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp race create")
                .withHandler(new CommandRaceCreate(parkourController))
                .withArgument(new CarbonArgument.Builder("course").setType(CarbonArgumentType.STRING).create())
                .withArgument(new CarbonArgument.Builder("time-limit").setType(CarbonArgumentType.INTEGER).setDefaultValue(-1).create())
                .requiresPermission("hcp.race.create")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp race start")
                .withHandler(new CommandRaceStart(parkourController))
                .withArgument(new CarbonArgument.Builder("countdown").setType(CarbonArgumentType.INTEGER).setDefaultValue(0).create())
                .requiresPermission("hcp.race.create")
                .preventConsoleCommandSender();
        carbon.addCommand("hcp race end")
                .withHandler(new CommandRaceEnd(parkourController))
                .requiresPermission("hcp.race.create")
                .preventConsoleCommandSender();
        // CONFIG COMMANDS
        carbon.addCommand("hcp config")
                .withHandler(new CommandConfigBroadcastRaceFinish(parkourController))
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp config broadcastRaceFinish")
                .withHandler(new CommandConfigBroadcastRaceFinish(parkourController))
                .withArgument(new CarbonArgument.Builder("value").setType(CarbonArgumentType.BOOLEAN).create())
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp config spectateAfterRace")
                .withHandler(new CommandConfigSpectateAfterRace(parkourController))
                .withArgument(new CarbonArgument.Builder("value").setType(CarbonArgumentType.BOOLEAN).create())
                .requiresPermission("hcp.admin");
        carbon.addCommand("hcp config playerGhosts")
                .withHandler(new CommandConfigPlayerGhosts(parkourController))
                .withArgument(new CarbonArgument.Builder("value").setType(CarbonArgumentType.BOOLEAN).create())
                .requiresPermission("hcp.admin");

        carbon.setMissingCommandHandler((CommandSender sender, List<CarbonCommand> suggestions) -> {
            String suggestion = suggestions.stream()
                    .map(CarbonCommand::getHelpText)
                    .collect(Collectors.joining("\n"));
            sender.sendMessage("Command wasn't found.\nSuggestions:\n" + suggestion);
        });
        carbon.setNoPermissionHandler((CommandSender sender, CarbonCommand command) -> MessageHandler.sendError(sender, "You do not have permission to run the command. Missing permission " + command.getPermissions()));
        carbon.setMissingArgumentHandler((CommandSender sender, CarbonCommand command) -> MessageHandler.sendError(sender, "You've entered an incorrect number of arguments.Usage:§b" + command.getHelpText()));

        startAutosaveTimer();
    }

    private void startAutosaveTimer() {
        long timeBetween = settings.getAutosave() * 60 * 20; // convert to seconds, then convert to ticks
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getLogger().severe("RUNNING SAVE CONFIG");
            Main.getInstance().saveConfig();
        }, timeBetween, timeBetween);
    }

    @Override
    public void onDisable() {
        ghostFactory.close();
        parkourController.prepareShutdown();
        this.saveConfig();
    }

    // forward commands to carbon
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageHandler.sendInfo(sender, Arrays.toString(args));
        if(args.length <= 0){
            MessageHandler.sendHelp(sender);
        }
        else if (Objects.equals(args[0], "addcheckpoint")) {
            if (args.length == 1) {
                MessageHandler.sendError(sender, "You've entered an incorrect number of arguments.\nUsage:§b /hcp addcheckpoint <course> [radius] [height]");
                return true;
            } else if (args.length == 3) {
                MessageHandler.sendError(sender, "Addcheckpoint requires both radius and height, or neither.");
                return true;
            }
        }

        return carbon.handleCommand(sender, command, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //sender.sendMessage(String.valueOf(args.length));
        List<String> completion = new ArrayList<>();
        if
        (
                (args.length == 2 &&
                        (
                                args[0].equals("join") ||
                                        args[0].equals("addcheckpoint") ||
                                        args[0].equals("setkillheight") ||
                                        args[0].equals("remove") ||
                                        args[0].equals("delete") ||
                                        args[0].equals("goto") ||
                                        args[0].equals("info") ||
                                        args[0].equals("replacestart") ||
                                        args[0].equals("clearcheckpoints") ||
                                        args[0].equals("gotocheckpoint") ||
                                        args[0].equals("resethighscore")
                        )
                ) ||
                        (args.length == 3 &&
                                (
                                        args[1].equals("create") ||
                                                args[0].equals("challenge")
                                )
                        )
        ) {
            for (ParkourCourse course : parkourController.getCourses().values()) {
                if (course.getName().startsWith(args[args.length - 1])) {
                    completion.add(course.getName());
                } else if (args[args.length - 1].isEmpty()) {
                    completion.add(course.getName());
                }
            }
            return completion;
        } else if (args.length == 2 && args[0].equals("challenge")) {
            completion.addAll(getServer().getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
            return completion;
        }
        return carbon.handleAutoComplete(sender, command, args);
    }

    public List<String> getHelpTexts(Player player) {
        return carbon.getHelpTexts(player);
    }

    public ParkourController getParkourController() {
        return parkourController;
    }

    public Settings getSettings() {
        return settings;
    }

    public GhostFactory getGhostFactory() {
        return ghostFactory;
    }
}



