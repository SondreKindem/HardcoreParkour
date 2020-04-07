package no.sonkin.hardcoreParkour.sessions;

import no.sonkin.hardcoreParkour.Main;
import no.sonkin.hardcoreParkour.enums.RaceSessionState;
import no.sonkin.hardcoreParkour.events.RaceEndedEvent;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import no.sonkin.hardcoreParkour.objects.ResultTuple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A RaceSession lets players create and join a race on a course. It handles a lot of similar logic to the
 * ParkourController, but in a race context - i.e. keeps track of standings and results
 * TODO: Set player to spectator mode after finishing
 */
public class RaceSession {
    private UUID creatorID;
    private String sessionName;
    private ParkourCourse course;
    private HashMap<UUID, PlayerSession> playerSessions;
    private int playersRemaining;
    private ArrayList<ResultTuple> finalStandings;
    private int timeLimit;
    private RaceSessionState state;
    private Timer timerScheduler;
    private BukkitRunnable timeLimitRunner;

    public RaceSession(UUID creatorID, String sessionName, ParkourCourse course) {
        this.creatorID = creatorID;
        this.sessionName = sessionName;
        this.course = course;
        this.playerSessions = new HashMap<>();
        this.finalStandings = new ArrayList<>();
        this.timerScheduler = new Timer();
        this.state = RaceSessionState.WAITING;
    }

    public RaceSession(UUID creatorID, String sessionName, ParkourCourse course, int timeLimit) {
        this.creatorID = creatorID;
        this.sessionName = sessionName;
        this.course = course;
        this.playerSessions = new HashMap<>();
        this.finalStandings = new ArrayList<>();
        this.timeLimit = timeLimit;
        this.timerScheduler = new Timer();
        this.state = RaceSessionState.WAITING;

        timeLimitRunner = new BukkitRunnable() {
            @Override
            public void run() {
                MessageHandler.sendInfoMultipleID(playerSessions.keySet(), "Race time limit reached!");
                // Main.getInstance().getParkourController().endRace(null, sessionName);
                Bukkit.getServer().getPluginManager().callEvent(new RaceEndedEvent(sessionName));
            }
        };
    }

    /**
     * Lets a player join the race session
     */
    public boolean join(Player player) {
        if (this.state != RaceSessionState.ENDED) {
            MessageHandler.sendInfoMultipleID(playerSessions.keySet(), player.getName() + MessageHandler.getPrimaryColor() + " joined the race :D");
            playerSessions.put(player.getUniqueId(), new PlayerSession(course, player));
            return true;
        }
        return false;
    }

    /**
     * Removes a player from the race session
     */
    public boolean leave(Player player) {
        PlayerSession session = playerSessions.get(player.getUniqueId());
        if (playerSessions.remove(player.getUniqueId()) != null) {
            MessageHandler.sendInfoMultipleID(playerSessions.keySet(), player.getName() + MessageHandler.getPrimaryColor() + " left the race :(");
            if (!session.isFinished()) {
                playersRemaining--;
            }
            if (playerSessions.size() <= 0 || playersRemaining <= 0) {
                Bukkit.getServer().getPluginManager().callEvent(new RaceEndedEvent(sessionName));
            }
            return true;
        }
        return false;
    }

    public void startCountdown(int time) {
        if (timeLimit != 0) {  // If there is a time limit we should set it now. The time limit cannot be called from the async context of a timer.
            scheduleTimeLimit(timeLimit + time);
        }
        if (time > 0) {
            this.state = RaceSessionState.COUNTDOWN;
            MessageHandler.announceRaceStarting(sessionName, time);
            MessageHandler.sendInfoMultipleID(playerSessions.keySet(), "Race starting in §a" + time + MessageHandler.getPrimaryColor() + " seconds. Get ready!");

            this.timerScheduler = new Timer();  // Gotta create a new timer instance, in case the old one was cancelled. Should be fine as long as this is the only place the timer is scheduled.
            timerScheduler.scheduleAtFixedRate(new TimerTask() {
                int timeRemaining = time;

                @Override
                public void run() {
                    timeRemaining--;
                    if (timeRemaining < 1) {
                        this.cancel();
                        startRace();
                    } else if (timeRemaining < 5) {
                        MessageHandler.sendInfoMultipleID(playerSessions.keySet(), "COUNTDOWN:  §a" + timeRemaining);
                    }
                }
            }, 0, 1000);
        } else {
            startRace();
        }
    }

    private boolean startRace() {
        if (playerSessions.size() > 0) {
            this.playersRemaining = playerSessions.size();
            this.state = RaceSessionState.STARTED;  // Once started movement is no longer blocked
            resetAllPlayerStartTimes();
            MessageHandler.sendInfoMultipleID(playerSessions.keySet(), "§cRACE STARTED! GO GO GO");
            return true;
        } else {
            // Don't start if there are no players.
            Player player = Bukkit.getPlayer(creatorID);
            if (player != null) {
                MessageHandler.sendError(player, "Race did not start because there are no players in it.");
            }
            return false;
        }
    }

    /**
     * Resets the start time in the playerSessions. Needed because time is ticking while players wait for the race to start
     */
    private void resetAllPlayerStartTimes() {  // TODO: does this cause problems with many players?
        long startTime = System.currentTimeMillis();
        for (PlayerSession session : playerSessions.values()) {
            session.setStartTime(startTime);
        }
    }

    public void cancelCountdown() {
        timerScheduler.cancel();
    }

    /**
     * Sets the timeLimitRunner task to run after a delay
     *
     * @param compensatedTime the delay in seconds. Should compensate for the countdown time
     */
    private void scheduleTimeLimit(int compensatedTime) {
        // End race after the time limit. WARN if the tickrate is low, this will take longer to execute
        timeLimitRunner.runTaskLater(Main.getInstance(), compensatedTime * 20);
    }

    /**
     * Once a player finishes the race the playerFinish method adds the player to the leaderboard
     *
     * @param player the player who finished the race
     */
    public void playerFinish(Player player) {
        finalStandings.add(new ResultTuple(player, playerSessions.get(player.getUniqueId()).getFinishTIme()));
        playersRemaining--;

        if (Main.getInstance().getSettings().isBroadcastRaceFinish()) {
            MessageHandler.announce(player.getName() + " finished as No. " + finalStandings.size() + " in " + this.sessionName + "'s race!");
        } else {
            MessageHandler.sendInfoMultipleID(playerSessions.keySet(), player.getName() + " finished as No. " + finalStandings.size() + " in " + this.sessionName + "'s race!");
        }
        if (playersRemaining <= 0) {
            Bukkit.getServer().getPluginManager().callEvent(new RaceEndedEvent(sessionName));
        }
    }


    public void end() {
        this.state = RaceSessionState.ENDED;
        timerScheduler.cancel();
        if (timeLimitRunner != null) {
            try {
                timeLimitRunner.cancel();
            } catch (IllegalStateException ignored) {
            }  // Can safely be ignored. Is thrown if the runnable has not been scheduled yet.
        }
        // Add players who have not yet finished to the final result, sorted by checkpoints
        for (PlayerSession session : playerSessions.values().stream().sorted(Comparator.comparing(PlayerSession::getCheckpointIndex)).collect(Collectors.toList())) {
            if (!session.isFinished()) {
                finalStandings.add(new ResultTuple(session.getPlayer(), 0));
            }
        }

        MessageHandler.sendRaceResult(finalStandings);

        playerSessions = null;
    }

    public boolean hasStarted() {
        return state == RaceSessionState.STARTED;
    }

    public boolean isWaiting() {
        return state == RaceSessionState.WAITING;
    }

    public PlayerSession getPlayerSession(Player player) {
        return playerSessions.get(player.getUniqueId());
    }

    public HashMap<UUID, PlayerSession> getPlayerSessions() {
        return playerSessions;
    }

    public ParkourCourse getCourse() {
        return course;
    }

    public ArrayList<ResultTuple> getFinalStandings() {
        return finalStandings;
    }

    public int getRanking(Player player) {
        return finalStandings.indexOf(player);
    }

    public boolean isInRace(Player player) {
        return playerSessions.containsKey(player.getUniqueId());
    }

    public String getSessionName() {
        return sessionName;
    }

    public UUID getCreatorID() {
        return creatorID;
    }

    public RaceSessionState getState() {
        return state;
    }

    public int getPlayersRemaining() {
        return playersRemaining;
    }
}
