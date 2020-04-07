package no.sonkin.hardcoreParkour.sessions;

import no.sonkin.hardcoreParkour.Main;
import no.sonkin.hardcoreParkour.objects.Checkpoint;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.Util.millisToFormattedTime;

public class PlayerSession {
    // Player info
    private Player player;
    private Location previousLocation;
    private GameMode previousGameMode;
    private boolean wasFlying;
    private boolean wasCollideable;
    // Session info
    private ParkourCourse course;
    private int checkpointIndex = 0;
    private Checkpoint currentCheckpoint;
    private Checkpoint nextCheckpoint;
    private long startTime;
    private Long finishTime;
    private boolean finished = false;
    private boolean sessionStarted = false;  // For marking that the player has started. Ignored for races

    public PlayerSession(ParkourCourse course, Player player) {
        this.course = course;
        this.player = player;
        this.previousLocation = player.getLocation();
        this.previousGameMode = player.getGameMode();
        this.wasFlying = player.isFlying();
        this.wasCollideable = player.isCollidable();

        this.currentCheckpoint = null;
        this.nextCheckpoint = course.getCheckpoint(0);

        this.startTime = System.currentTimeMillis();

        // Go to course start
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.teleport(course.getStartLocation());
        course.incrementTimesPlayed();
        player.setCollidable(false);
        if(Main.getInstance().getSettings().isPlayerGhosts()){
            Main.getInstance().getGhostFactory().setGhost(player.getName(), true);
        }
    }

    public void restart() {
        currentCheckpoint = null;
        this.nextCheckpoint = course.getCheckpoint(0);
        checkpointIndex = 0;
        respawn();
    }

    public void respawn() {
        player.setFallDistance(0);
        if (currentCheckpoint == null) {
            player.teleport(course.getStartLocation());
        } else {
            player.teleport(currentCheckpoint.getCheckpointCenter());
        }
    }

    public void incrementCheckpoints() {
        checkpointIndex++;
        if (checkpointIndex == course.getCheckpoints().size()) {
            finished = true;

            // Calculate the time it took to complete
            this.finishTime = Math.abs(startTime - System.currentTimeMillis());
            // long completionTimeSeconds = completionTime / 1000;
            String completionTimeString = millisToFormattedTime(finishTime);
            MessageHandler.sendCourseCompleteMessage(player, course.getName(), completionTimeString);

            course.incrementTimesCompleted();

            if (course.checkNewRecord(finishTime, player.getName())) {
                MessageHandler.sendInfo(player, "CONGRATULATIONS! You beat the previous best time on this course!");
            }

        } else {
            MessageHandler.sendInfo(player, "Reached checkpoint " + checkpointIndex);
            currentCheckpoint = course.getCheckpoint(checkpointIndex - 1);
            nextCheckpoint = course.getCheckpoint(checkpointIndex);
        }
    }

    public void leaveSession() {
        player.teleport(previousLocation);
        player.setGameMode(previousGameMode);
        player.setCollidable(wasCollideable);
        if (wasFlying) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        Main.getInstance().getGhostFactory().removePlayer(player);
    }

    public void spectate() {
        player.setGameMode(GameMode.SPECTATOR);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public ParkourCourse getCourse() {
        return course;
    }

    public Checkpoint getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    public Checkpoint getNextCheckpoint() {
        return nextCheckpoint;
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public GameMode getPreviousGameMode() {
        return previousGameMode;
    }

    public boolean wasFlying() {
        return wasFlying;
    }

    public int getCheckpointIndex() {
        return checkpointIndex;
    }

    public long getFinishTIme() {
        return finishTime;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean sessionStarted() {
        return sessionStarted;
    }

    public void setSessionStarted(boolean sessionStarted) {
        this.sessionStarted = sessionStarted;
    }
}
