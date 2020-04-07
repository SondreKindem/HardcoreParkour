package no.sonkin.hardcoreParkour.objects;

import no.sonkin.hardcoreParkour.Main;
import no.sonkin.hardcoreParkour.enums.RaceSessionState;
import no.sonkin.hardcoreParkour.events.RaceEndedEvent;
import no.sonkin.hardcoreParkour.sessions.PlayerSession;
import no.sonkin.hardcoreParkour.sessions.RaceSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * The ParkourController controls most of the functionality of the parkor courses and handles the backend of commands
 */
public class ParkourController implements Listener {
    // For casual play:
    private HashMap<String, ParkourCourse> courses;
    private HashMap<UUID, PlayerSession> playerSessions;
    // For races:
    private HashMap<String, RaceSession> raceSessions;
    private HashMap<UUID, RaceSession> playersInRaceSessions;
    private HashMap<UUID, String> activeChallenges;

    public ParkourController() {
        this.courses = new HashMap<>();
        this.playerSessions = new HashMap<>();
        this.raceSessions = new HashMap<>();
        this.playersInRaceSessions = new HashMap<>();
        this.activeChallenges = new HashMap<>();

        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

        loadFromConfig();
    }

    /**
     * Loads saved courses from config.yml
     */
    @SuppressWarnings("unchecked")
    private void loadFromConfig() {
        Bukkit.getLogger().info("[HardcoreParkour] Loading courses");

        FileConfiguration config = Main.getInstance().getConfig();

        ConfigurationSection coursesSection = config.getConfigurationSection("courses");
        if (coursesSection != null) {
            Set<String> courseNames = coursesSection.getKeys(false);

            for (String courseName : courseNames) {
                String basePath = "courses." + courseName;

                Location location = config.getLocation(basePath + ".location");
                int killHeight = config.getInt(basePath + ".killheight");
                String bestPlayer = config.getString(basePath + ".bestPlayer");
                long bestTime = config.getLong(basePath + ".bestTime");
                int timesPlayed = config.getInt(basePath + ".timesPlayed");
                int timesCompleted = config.getInt(basePath + ".timesCompleted");
                if (timesCompleted > timesPlayed)
                    timesPlayed = timesCompleted;  // A course cannot be completed more times than it has been played, duh

                ArrayList<Checkpoint> checkpointList = new ArrayList<>();

                try {
                    // The checkpoints are stored as key-value pairs, and all the checkpoints are in a list in t he config.
                    // getList returns unknown type, so the list has to be cast to correct values.
                    List<Map<String, Object>> checkpointsFromConfig = (List<Map<String, Object>>) Main.getInstance().getConfig().getList(basePath + ".checkpoints");

                    if (checkpointsFromConfig != null) {  // We might not have any checkpoints
                        if (checkpointsFromConfig.size() <= 0) {
                            Bukkit.getLogger().warning("[HardcoreParkour] Course " + courseName + " does not contain any checkpoints!");
                        }

                        // We must create new checkpoint objects for the stored checkpoint maps
                        for (Map<String, Object> checkpoint : checkpointsFromConfig) {
                            checkpointList.add(new Checkpoint((Location) checkpoint.get("location"), (int) checkpoint.get("radius"), (int) checkpoint.get("height")));
                        }
                    }
                } catch (ClassCastException e) {  // If someone formats the config file wrong, some values might no longer be castable
                    Bukkit.getLogger().severe("[HardcoreParkour] Encountered a CastException when loading checkpoints from course '" + courseName + "'. The course will be loaded without checkpoints. Did someone mess with the plugin's config.yml?");
                }

                // Pfhew, we can finally add the course
                courses.put(courseName, new ParkourCourse(courseName, location, killHeight, checkpointList, bestTime, bestPlayer, timesPlayed, timesCompleted));
                Bukkit.getLogger().info("[HardcoreParkour] Loaded course " + courseName);
            }
        } else {
            Bukkit.getLogger().info("[HardcoreParkour] No courses found in config.yml");
        }
    }

    /**
     * Create a new parkour course
     *
     * @param name          the name of the course
     * @param startLocation the start position of the course. Joining players will teleport here
     * @return true if course created, false if course already exists
     */
    public boolean createCourse(String name, Location startLocation) {
        if (courses.containsKey(name)) {
            return false;
        }
        ParkourCourse course = new ParkourCourse(name, startLocation);
        courses.put(name, course);
        return true;
    }

    /**
     * Removes all players and races from the course, then marks the course as closed.
     * A closed course cannot be joined by anyone except OPs
     *
     * @param name the name of the course
     * @return true if the course exists, false if not
     */
    public boolean closeCourse(String name) {
        ParkourCourse course = courses.get(name);
        if (course == null) {
            return false;
        }

        for (PlayerSession playerSession : playerSessions.values()) {
            if (playerSession.getCourse().getName().equals(name)) {
                playerSession.leaveSession();
            }
        }
        for (RaceSession raceSession : raceSessions.values()) {
            if (raceSession.getCourse() == course) {
                endRace(null, raceSession.getSessionName());
            }
        }
        course.close();
        return true;
    }

    /**
     * Mark a course as open, allowing players to join it
     *
     * @param name the name of the course
     * @return true if the course exists, false if not
     */
    public boolean openCourse(String name) {
        ParkourCourse course = courses.get(name);
        if (course != null) {
            course.open();
            return true;
        }
        return false;
    }

    /**
     * Puts a player into a given course
     */
    public CommandResult joinCourse(Player player, String courseName) {
        ParkourCourse course = courses.get(courseName);
        if (course == null) {
            return new CommandResult(false, "Could not find a course named " + courseName + ".");
        }
        if (course.isClosed() && !player.isOp()) {
            return new CommandResult(false, "Cannot join course " + courseName + ". The course is closed.");
        }
        if (isInRace(player)) {
            return new CommandResult(false, "Please leave the race first!");
        }

        if (isInCourse(player)) {
            leaveCourse(player);
        }

        playerSessions.put(player.getUniqueId(), new PlayerSession(course, player));
        return new CommandResult(true, "Joined course " + courseName + ".");
    }

    /**
     * Remove a course from the courses map
     *
     * @param name the name of the course
     * @return true if the course was removed, false if the course was not found.
     */
    public boolean removeCourse(String name) {
        ParkourCourse course = courses.remove(name);
        if (course == null) {
            return false;
        }

        for (UUID playerID : playerSessions.keySet()) {  // Not the best way to do this, but it works
            PlayerSession session = playerSessions.get(playerID);
            Player player = Bukkit.getPlayer(playerID);
            if (session.getCourse().getName().equals(course.getName()) && player != null) {
                MessageHandler.sendError(player, "Course was deleted by admin. Leaving course...");

                leaveCourse(player);
            }
        }
        for (RaceSession raceSession : raceSessions.values()) {
            if (raceSession.getCourse().getName().equals(course.getName())) {
                endRace(null, raceSession.getSessionName());
            }
        }

        Main.getInstance().getConfig().set("courses." + name, null);
        Main.getInstance().saveConfig();
        return true;
    }

    /**
     * Handles leaving for both general courses and races
     *
     * @param player The player who is leaving
     * @return true if successfully left, else false
     */
    public boolean leaveCourse(Player player) {
        if (isInCourse(player)) {
            PlayerSession session = playerSessions.get(player.getUniqueId());
            playerSessions.remove(player.getUniqueId());

            session.leaveSession();
            return true;
        }
        if (isInRace(player)) {
            RaceSession raceSession = playersInRaceSessions.get(player.getUniqueId());
            PlayerSession session = raceSession.getPlayerSession(player);
            playersInRaceSessions.remove(player.getUniqueId());

            session.leaveSession();
            raceSession.leave(player);
            return true;
        }
        return false;
    }

    /**
     * Create a race with infinite time-limit
     */
    public void createRace(UUID creatorID, String name, ParkourCourse course) {
        raceSessions.put(name, new RaceSession(creatorID, name, course));
    }

    /**
     * Create a race with a time-limit
     */
    public void createRace(UUID creatorID, String name, ParkourCourse course, int timeLimit) {
        raceSessions.put(name, new RaceSession(creatorID, name, course, timeLimit));
    }

    public void createRaceChallenge(UUID creatorID, UUID opponentID, String raceName, ParkourCourse course) {
        raceSessions.put(raceName, new RaceSession(creatorID, raceName, course));
        activeChallenges.put(opponentID, raceName);
    }

    public void acceptRaceChallenge(Player player) {
        if (activeChallenges.containsKey(player.getUniqueId())) {
            RaceSession raceSession = raceSessions.get(activeChallenges.get(player.getUniqueId()));
            // Make both players join the race session
            joinRace(player, raceSession.getSessionName());
            joinRace(Bukkit.getPlayer(raceSession.getCreatorID()), raceSession.getSessionName());
            startRace(null, raceSession.getSessionName(), 5);
        }
    }

    /**
     * Starts the race if the player sending the command is the owner of the race, is OP, or the sender is null
     *
     * @param sender    the player who ran the command. Can be null for internal use
     * @param raceName  the name of the race
     * @param countdown how many seconds we should wait before the race starts, allowing players to join. If this is 0 or less the race starts immediately
     * @return true if race started, false if there is no race matching the name, or the player does not have permission to start the race
     */
    public CommandResult startRace(Player sender, String raceName, int countdown) {
        RaceSession raceSession = raceSessions.get(raceName);
        if (raceSession == null) {
            return new CommandResult(false, "There is no race session for " + raceName);
        }
        if (raceSession.hasStarted()) {
            return new CommandResult(false, "The race session has already started.");
        }

        if (sender == null || raceSession.getCreatorID() == sender.getUniqueId() || sender.isOp()) {
            if (raceSession.getState() == RaceSessionState.COUNTDOWN) {
                raceSession.cancelCountdown();
                if (sender != null) {
                    MessageHandler.sendInfo(sender, "Cancelling countdown, using your new input instead.");
                }
            }

            raceSession.startCountdown(countdown);
            return new CommandResult(true, "Starting race...");
        }
        return new CommandResult(false, "You do not have a race session.");
    }

    /**
     * Puts a player into a race
     *
     * @param player the player that will be added to the race
     * @param race   the name of the race
     * @return true if player was able to join, false if the player is already racing or the race does not exist
     */
    public CommandResult joinRace(Player player, String race) {
        if (isInCourse(player)) {
            leaveCourse(player);
        }
        if (isInRace(player)) {
            return new CommandResult(false, "Please leave your current race first!");
        }
        RaceSession raceSession = raceSessions.get(race);
        if (raceSession != null) {
            if (raceSession.join(player)) {
                playersInRaceSessions.put(player.getUniqueId(), raceSession);
                return new CommandResult(true, "Joined the race!");
            } else {
                return new CommandResult(false, "Could not join. Race session finished.");
            }
        }
        return new CommandResult(false, "Could not find a race session for " + race);
    }

    /**
     * Make the race finish. The sender must be OP or the race creator. If this method is used internally the sender can be null.
     *
     * @param sender   the player running the end command. Set as null if used internally
     * @param raceName the name of the race
     * @return true if race ended, false if there is no race session, or sender is not allowed to end the race
     */
    public CommandResult endRace(Player sender, String raceName) {
        RaceSession raceSession = raceSessions.get(raceName);
        if (raceSession == null) {
            return new CommandResult(false, "There is no race session for " + raceName);
        }
        if (sender == null || raceSession.getCreatorID() == sender.getUniqueId() || sender.isOp()) {
            for (PlayerSession playerSession : raceSession.getPlayerSessions().values()) {
                Player player = playerSession.getPlayer();

                playersInRaceSessions.remove(player.getUniqueId());

                playerSession.leaveSession();
            }
            raceSession.end();
            raceSessions.remove(raceSession.getSessionName());

            return new CommandResult(true, "Ending race session");
        }
        return new CommandResult(false, "You do not have a race session.");
    }

    public boolean respawnPlayer(Player player) {
        PlayerSession session = playerSessions.get(player.getUniqueId());
        if (session != null) {
            session.respawn();
            return true;
        } else {
            RaceSession raceSession = playersInRaceSessions.get(player.getUniqueId());
            if (raceSession != null) {
                raceSession.getPlayerSession(player).respawn();
                return true;
            }
        }
        return false;
    }

    public void restartPlayer(Player player) {
        PlayerSession session = playerSessions.get(player.getUniqueId());
        if (session != null) {
            MessageHandler.sendInfo(player, "Restarted course.");
            session.restart();
        }
    }

    /**
     * Prepares for plugin disable by forcing all players to leave the courses
     */
    public void prepareShutdown() {
        for (PlayerSession playerSession : playerSessions.values()) {
            playerSession.leaveSession();
        }
        playerSessions = null;
        for (RaceSession raceSession : raceSessions.values()) {
            for (PlayerSession playerSession : raceSession.getPlayerSessions().values()) {
                playerSession.leaveSession();
            }
        }
        raceSessions = null;
        playersInRaceSessions = null;
    }

    /**
     * Check if a player is currently playing in a parkour course
     *
     * @param player the player to check
     * @return true if player has a session, false if not
     */
    private boolean isInCourse(Player player) {
        return playerSessions.containsKey(player.getUniqueId());
    }

    private boolean isInRace(Player player) {
        return playersInRaceSessions.containsKey(player.getUniqueId());
    }

    /**
     * Let the player know that they finished the course, then make them leave
     */
    private void playerFinish(Player player) {
        leaveCourse(player);
    }

    /*
     * EVENT HANDLERS
     */

    @EventHandler
    void onRaceEnded(RaceEndedEvent event) {
        endRace(null, event.getSessionName());
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isInCourse(player)) {
            PlayerSession playerSession = playerSessions.get(player.getUniqueId());

            if (playerSession.sessionStarted()) {
                Checkpoint nextCheckpoint = playerSession.getNextCheckpoint();
                if (nextCheckpoint != null) {
                    if (nextCheckpoint.isInside(player)) {

                        playerSession.incrementCheckpoints();
                        if (playerSession.isFinished()) {
                            // FINISHED COURSE
                            playerFinish(player);
                            return;
                        }
                    }
                }
            } else if (event.getFrom().getX() != event.getTo().getX()
                    || event.getFrom().getZ() != event.getTo().getZ()) {
                // If the playerSession has not started and the player moves, start the session and set the start time to now.
                // This way the players can orient themselves without it hurting their start time.
                playerSession.setSessionStarted(true);
                playerSession.setStartTime(System.currentTimeMillis());
            }


            // IF PLAYER FELL
            if (player.getLocation().getBlockY() <= playerSession.getCourse().getKillHeight()) {
                playerSession.respawn();
            }
        } else if (isInRace(player)) {
            // PLAYER IS RACING
            RaceSession raceSession = playersInRaceSessions.get(player.getUniqueId());
            //player.sendMessage(raceSession.getPlayersRemaining() + "");

            // Disable movement if race not started
            if (!raceSession.hasStarted()) {
                event.setCancelled(true);
            } else {

                PlayerSession playerSession = raceSession.getPlayerSession(player);
                Checkpoint nextCheckpoint = playerSession.getNextCheckpoint();

                if (nextCheckpoint != null && !playerSession.isFinished()) {
                    if (nextCheckpoint.isInside(player)) {

                        playerSession.incrementCheckpoints();
                        if (playerSession.isFinished()) {

                            if (Main.getInstance().getSettings().isSpectateAfterRace()) {
                                //if(raceSession.getPlayersRemaining() > 0){
                                // Only set to spectate if there are players who have not yet finished.
                                // If all players have finished, all players will already have left the course
                                playerSession.spectate();
                                raceSession.playerFinish(player); // <-- handles adding player to leaderboard
                                MessageHandler.sendInfo(player, "Entering spectator mode.");
                                //}
                            } else {
                                raceSession.playerFinish(player); // <-- handles adding player to leaderboard
                                playerFinish(player);  // <-- handles removal of playerSession and returing player to previous location
                            }
                            return;
                        }
                    }
                }
                // IF PLAYER FELL
                if (player.getLocation().getBlockY() <= playerSession.getCourse().getKillHeight()) {
                    playerSession.respawn();
                }
            }
        }
    }

    @EventHandler
        // disable elytra glide
    void onElytraGlide(EntityToggleGlideEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (isInCourse(player) || isInRace(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
        // disable teleport
    void onPlayerTeleport(PlayerTeleportEvent event) {
        if ((isInCourse(event.getPlayer()) || isInRace(event.getPlayer())) && (
                event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL ||
                        event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT ||
                        event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND
        )) {
            event.setCancelled(true);
        }
    }

    @EventHandler
        // disable hunger
    void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (isInCourse(player) || isInRace(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (isInCourse(player) || isInRace(player)) {
            leaveCourse(player);
        }
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isInCourse(player) || isInRace(player)) {
            leaveCourse(player);
        }
    }

    @EventHandler
    void onPlayerQuit(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (isInCourse(player) || isInRace(player)) {
            leaveCourse(player);
        }
    }

    @EventHandler
        // Disable all commands except hcp when in a course
    void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isInCourse(player) || isInRace(player)) {
            if (!event.getMessage().contains("hcp")) {
                MessageHandler.sendError(player, "Can only use hcp command while in a course.");
                event.setCancelled(true);
            }
        }
    }

   /* @EventHandler
        // disable gamemode
    void onPlayerGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (isInCourse(event.getPlayer()) || isInRace(player)) {
            event.setCancelled(true);
        }
    }*/


    @EventHandler
        // Disable phantoms targeting players
    void disablePhantomTargeting(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.PHANTOM && event.getTarget() != null && event.getTarget().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getTarget();
            if (isInCourse(player) || isInRace(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
        // Disable flight toggle
    void onPlayerFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (isInCourse(player) || isInRace(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
        // disable damage
    void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (isInCourse(player) || isInRace(player)) {
            event.setCancelled(true);
        }
    }

    private PlayerSession getPlayerSession(Player player) {
        return playerSessions.get(player.getUniqueId());
    }

    public ParkourCourse getCourse(String name) {
        return courses.get(name);
    }

    public HashMap<String, ParkourCourse> getCourses() {
        return courses;
    }

    public RaceSession[] getRaceSessions() {
        return raceSessions.values().toArray(new RaceSession[0]);
    }

    public HashMap<String, RaceSession> getRaceSessionsMap() {
        return raceSessions;
    }

    public String[] getRaceNames() {
        return raceSessions.keySet().toArray(new String[0]);
    }

    public RaceSession getRace(String name) {
        return raceSessions.get(name);
    }
}
