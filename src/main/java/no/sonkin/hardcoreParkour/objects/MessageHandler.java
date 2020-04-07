package no.sonkin.hardcoreParkour.objects;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import no.sonkin.hardcoreParkour.Main;
import no.sonkin.hardcoreParkour.Util;
import no.sonkin.hardcoreParkour.sessions.RaceSession;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * A shortcut for sending formatted chat messages. Adds plugin prefix and formats the message based on type, i.e. errors are colored red.
 */
public class MessageHandler {
    private static ChatColor primaryColor = ChatColor.DARK_AQUA;
    private static String horizontalRule = ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 30);
    private static String horizontalRuleSmall = ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 20);
    private static String prefix = "§9[" + primaryColor + "HCP§9]" + primaryColor;

    /*##################*/
    /* GENERIC MESSAGES */
    /*##################*/

    public static void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(prefix + " " + message);
    }

    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(prefix + " §c" + message);
    }

    public static void sendInfoMultiplePlayer(Collection<Player> players, String message) {
        if (players.size() <= 0) {
            return;
        }

        for (Player player : players) {
            sendInfo(player, message);
        }
    }

    public static void sendInfoMultipleID(Collection<UUID> players, String message) {
        if (players.size() <= 0) {
            return;
        }

        for (UUID id : players) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                sendInfo(player, message);
            }
        }
    }

    public static void announce(String message) {
        Bukkit.getServer().broadcastMessage(prefix + message);
    }

    /*######################*/
    /* INFORMATION MESSAGES */
    /*######################*/

    public static void sendCourseCompleteMessage(Player player, String courseName, String completedIn) {
        player.sendMessage(prefix + " You completed the course \"" + courseName + "\" in " + completedIn);
    }

    public static void sendCourseList(CommandSender sender, Set<String> courses) {
        if (courses.size() <= 0) {
            sendError(sender, "There are no courses!");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§9").append(horizontalRuleSmall).append(prefix).append("§9").append(horizontalRuleSmall)
                .append("\n").append("§6Courses:");

        int index = 1;
        for (String course : courses) {
            stringBuilder.append("\n§6").append(index += 1).append(". ").append(primaryColor).append(course);
        }

        sender.sendMessage(stringBuilder.toString());
    }

    public static void sendRaceList(CommandSender sender, RaceSession[] sessions) {
        if (sessions.length <= 0) {
            sendError(sender, "There are no races! Create your own race :)");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§9").append(horizontalRuleSmall).append(prefix).append(horizontalRuleSmall)
                .append("\n").append("§6Active races:");

        int index = 1;
        for (RaceSession session : sessions) {
            stringBuilder.append("\n§6").append(index += 1).append(". ").append(primaryColor).append(session.getSessionName())
                    .append(" - ").append(session.getCourse().getName())
                    .append(" - ").append(session.getState());
        }

        sender.sendMessage(stringBuilder.toString());
    }

    public static void sendCourseInfo(CommandSender sender, ParkourCourse course) {
        int numOfCheckpoints = course.getNumOfCheckpoints();
        int killHeight = course.getKillHeight();
        long bestTime = course.getBestTime();
        String bestPlayer = course.getBestPlayer();
        int timesPlayed = course.getTimesPlayed();
        int timesCompleted = course.getTimesCompleted();
        int completionPercentage = timesCompleted > 0 ? timesCompleted * 100 / timesPlayed : 0;
        String bestTimeString = (bestTime == 0) ? "none" : Util.millisToFormattedTime(bestTime);

        sender.sendMessage(new String[]{
                "§9" + horizontalRuleSmall + prefix + "§9" + horizontalRuleSmall,
                "§6" + course.getName() + primaryColor + " info:",
                "§6" + horizontalRuleSmall,
                primaryColor + "Best player: §6" + (bestPlayer == null ? "none" : bestPlayer),
                primaryColor + "Best time: §6" + bestTimeString,
                primaryColor + "Completion percentage: §6" + completionPercentage + "%",
                primaryColor + "Times played: " + timesPlayed,
                primaryColor + "Times completed: " + timesCompleted,
                "§6" + horizontalRuleSmall,
                primaryColor + "Checkpoints: " + numOfCheckpoints,
                primaryColor + "KillHeight: " + killHeight,
        });
    }

    /*###############*/
    /* RACE MESSAGES */
    /*###############*/

    public static void sendRaceChallenge(Player sender, Player opponent, String courseName) {
        ComponentBuilder message = new ComponentBuilder(prefix + "§6" + sender.getName() + primaryColor + " challenged you to a race on §6" + courseName + ".\n" + primaryColor + "§nClick this message or type §b/hcp accept");
        BaseComponent[] msg = message
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp accept"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Accept the challenge!").bold(true).create()))
                .create();
        opponent.spigot().sendMessage(msg);
    }

    public static void announceRaceCreated(String raceName) {
        ComponentBuilder message = new ComponentBuilder(prefix + "§6" + raceName + primaryColor + " created a new race!\n Click §3>>[§a§lJoin§3]<< " + primaryColor + "or §b/hcp race join " + raceName + "\n");
        BaseComponent[] msg = message
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp race join " + raceName))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Join the race!").bold(true).create()))
                .create();
        Bukkit.getServer().spigot().broadcast(msg);
    }

    public static void announceRaceStarting(String raceName, int timeToStart) {
        ComponentBuilder message = new ComponentBuilder(prefix + "§6" + raceName + primaryColor + "'s race is starting in " + timeToStart + " seconds!\n §3>>[§a§lJoin§3]<< " + primaryColor + "or §b/hcp race join " + raceName + "\n");
        BaseComponent[] msg = message
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp race join " + raceName))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Join the race!").bold(true).create()))
                .create();
        Bukkit.getServer().spigot().broadcast(msg);
    }

    public static void sendRaceResult(ArrayList<ResultTuple> finalStandings) {
        if (finalStandings.size() <= 0) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§9").append(horizontalRuleSmall).append(prefix).append(horizontalRuleSmall)
                .append("\n").append(primaryColor).append("Race results:")
                .append("\n§6").append(horizontalRuleSmall)
                .append("\n§c§lWINNER: §6§l").append(finalStandings.get(0).player.getName())
                .append("\n§6").append(horizontalRuleSmall);

        for (int i = 0; i < finalStandings.size(); i++) {
            ResultTuple tuple = finalStandings.get(i);
            String finishTimeString = tuple.finishTime > 0 ? Util.millisToFormattedTime(tuple.finishTime) : "DnF";
            stringBuilder.append(primaryColor).append("\n").append(i + 1).append(". §6").append(tuple.player.getName()).append(" - ").append(finishTimeString);
        }

        stringBuilder.append("\n").append(primaryColor).append(horizontalRule);

        for (ResultTuple tuple : finalStandings) {
            tuple.player.sendMessage(stringBuilder.toString());
        }
    }

    public static void sendRaceControlPanel(Player player, String raceName, String courseName) {
        player.sendMessage(new String[]{
                "§9" + horizontalRuleSmall + prefix + "§9" + horizontalRuleSmall,
                primaryColor + "Created a new race called on course " + courseName,
                primaryColor + "Join with §b/hcp race join " + raceName,
                primaryColor + "Once players have joined use:",
                "§b/hcp race start " + raceName + " <countdown>",
                "§6" + horizontalRule
        });
        BaseComponent[] raceStart = new ComponentBuilder(" §3>>[§2Start§3]<< ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp race start" + " 10"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Start the race").create()))
                .create();
        BaseComponent[] raceEnd = new ComponentBuilder(" §3>>[§cEnd§3]<< ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp race end"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cEnd the race").create()))
                .create();
        BaseComponent[] raceJoin = new ComponentBuilder(" §3>>[§bJoin§3]<< ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp race join " + raceName))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9Join the race").create()))
                .create();
        player.spigot().sendMessage(new ComponentBuilder("§6Commands: ").append(raceStart).append(raceEnd).append(raceJoin).append("\n§6").append(horizontalRule).create());
    }

    /*###############*/
    /* HELP MESSAGES */
    /*###############*/

    public static void sendHelp(CommandSender sender) {
        TextComponent goToWikiText = new TextComponent("Click here to go to the tutorial (web).\n");
        goToWikiText.setColor(ChatColor.GOLD);
        goToWikiText.setUnderlined(true);
        goToWikiText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/SondreKindem/HardcoreParkour/wiki"));
        goToWikiText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Go to wiki").create()));

        String createString = (sender.isOp() || sender.hasPermission("hcp.admin")) ? "§b/hcp create <name>§r: Creates a new course at your position.\n" : "";

        TextComponent textComponent = new TextComponent();
        textComponent.addExtra(prefix + " help:\n");
        textComponent.addExtra(goToWikiText);
        textComponent.addExtra(
                "§b/hcp commands§r: lists all available commands\n" +
                         createString +
                        "§b/hcp list§r: List all courses.\n" +
                        "§b/hcp info <course>§r: See course info\n" +
                        "§b/hcp join <course>§r: Join a course\n" +
                        "§b/hcp race join <name>§r: Join a race.\n" +
                        "§b/hcp leave§r: Leave a course or race.\n" +
                        "§b/hcp race create <course>§r: Create a race.\n" +
                        "§b/hcp race start §7[countdown]§r: Start the race (creator).\n" +
                        "§b/hcp challenge <player>§r: Challenge a player to a race.\n" +
                        "§b/hcp accept§r: Accept a race challenge."
        );

        sender.spigot().sendMessage(textComponent);
    }

    public static void sendUsagePlayer(Player player, int page) {
        List<String> helpTexts = Main.getInstance().getHelpTexts(player);
        int helpTextsSize = helpTexts.size();
        int perPage = 10;
        int totalPages = helpTextsSize / perPage + (helpTextsSize % perPage == 0 ? 0 : 1);

        if (page <= 0) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        List<String> helpTextsInRange = Util.getPage(helpTexts, page, perPage);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n§6").append(horizontalRule).append(horizontalRuleSmall)
                .append("\n").append(ChatColor.RESET)
                .append(prefix).append("commands:")
                .append("\n§8").append(horizontalRule).append(horizontalRuleSmall)
                .append("\n").append(ChatColor.RESET);
        for (String string : helpTextsInRange) {
            stringBuilder.append(string).append("\n");
        }
        player.sendMessage(stringBuilder.toString());

        TextComponent nextComp = new TextComponent(" §6Next>> ");
        nextComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp usage " + (page + 1)));
        nextComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Next page").create()));

        TextComponent prevComp = new TextComponent(" §6<<Prev ");
        prevComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hcp usage " + (page - 1)));
        prevComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Previous page").create()));

        String space = StringUtils.repeat(" ", 10);

        TextComponent message = new TextComponent();
        message.addExtra("§8" + horizontalRule + horizontalRuleSmall + "\n");
        message.addExtra(prevComp);
        message.addExtra(space + "Page " + page + space);
        message.addExtra(nextComp);
        message.addExtra("\n§6" + horizontalRule + horizontalRuleSmall);

        player.spigot().sendMessage(message);
    }

    public static void sendRaceHelp(CommandSender sender) {
        sender.sendMessage(new String[]{
                "§9" + horizontalRuleSmall + prefix + "§9" + horizontalRuleSmall,
                primaryColor + "You can create races on parkour courses!",
                "§b/hcp race create <name> " + primaryColor + ": Create a new race",
                "§b/hcp race start <name> <countdown> " + primaryColor + ": Start the race",
                "§b/hcp race join <name> " + primaryColor + ": Join a race",
                "§b/hcp race list " + primaryColor + ": list active races"
        });
    }

    /**
     * Print all commands to console, split by page
     */
    public static void sendUsageConsole(CommandSender sender, int page) {
        List<String> helpTexts = Main.getInstance().getHelpTexts(null);
        int helpTextsSize = helpTexts.size();
        int perPage = 10;
        int totalPages = helpTextsSize / perPage + (helpTextsSize % perPage == 0 ? 0 : 1);

        if (page <= 0) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        List<String> helpTextsInRange = Util.getPage(helpTexts, page, perPage);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(prefix).append("commands ").append("page ").append(page).append(":\n");
        for (String string : helpTextsInRange) {
            stringBuilder.append(string).append("\n");
        }
        sender.sendMessage(stringBuilder.toString());
    }


    public static String getPrefix() {
        return prefix;
    }

    public static ChatColor getPrimaryColor() {
        return primaryColor;
    }
}
