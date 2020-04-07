package no.sonkin.hardcoreParkour.objects;

import no.sonkin.hardcoreParkour.Main;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A parkour course that contains a start and one or more checkpoints.
 * TODO: track how many times played and completed - in info cmd show completion percentage
 */
public class ParkourCourse {
    private String name;
    private Location startLocation;
    private int killHeight;
    private ArrayList<Checkpoint> checkpoints;
    private ArrayList<Checkpoint> backupCheckpoints;
    private boolean closed = false;

    // Stats
    private long bestTime;
    private String bestPlayer;
    private int timesPlayed;
    private int timesCompleted;

    public ParkourCourse(String name, Location startLocation) {
        this.name = name;
        this.killHeight = 0;
        this.checkpoints = new ArrayList<>();
        startLocation.setX(startLocation.getX() + 0.5);  // Set location to center of block instead of bottom-left corner
        startLocation.setZ(startLocation.getZ() + 0.5);
        this.startLocation = startLocation;

        this.bestTime = 0;
        this.bestPlayer = null;

        doFirstTimeSetup();
    }

    /**
     * Constructor for creating a parkour course from config.
     * Courses created with this constructor will not be written to config.yml.
     * It is designed for use when loading saved courses
     */
    public ParkourCourse(String name, Location startLocation, int killHeight, ArrayList<Checkpoint> checkpoints, long bestTime, String bestPlayer, int timesPlayed, int timesCompleted) {
        this.name = name;
        this.startLocation = startLocation;
        this.killHeight = killHeight;
        this.checkpoints = checkpoints;
        this.bestTime = bestTime;
        this.bestPlayer = bestPlayer;
        this.timesCompleted = timesCompleted;
        this.timesPlayed = timesPlayed;
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);

        Main.getInstance().getConfig().set("courses." + name + ".checkpoints", checkpoints.stream().map(Checkpoint::toMap).collect(Collectors.toList()));
        Main.getInstance().saveConfig();
    }

    /**
     * Removes all checkpoints for this course
     */
    @SuppressWarnings("unchecked")
    public void clearCheckpoints() {  // TODO: before this is done force all players to leave this course
        this.backupCheckpoints = (ArrayList<Checkpoint>) checkpoints.clone();  // Unchecked cast - should be OK
        this.checkpoints.clear();

        Main.getInstance().getConfig().set("courses." + name + ".checkpoints", new String[]{});
        Main.getInstance().saveConfig();
    }

    /**
     * Restores previous checkpoint collection. Could be used in case clearCheckpoints was run on accident
     */
    @SuppressWarnings("unchecked")
    public void restoreCheckpoints() {
        if (backupCheckpoints != null) {
            this.checkpoints = (ArrayList<Checkpoint>) backupCheckpoints.clone();  // Unchecked cast - should be OK
            this.backupCheckpoints.clear();

            Main.getInstance().getConfig().set("courses." + name + ".checkpoints", checkpoints.stream().map(Checkpoint::toMap).collect(Collectors.toList()));
            Main.getInstance().saveConfig();
        }
    }

    /**
     * Check if the new time is better than the stored time - if so, save the new time
     *
     * @param newTime    the time it took to beat the course in milliseconds
     * @param playerName the name of the player
     * @return true if the time was better, false if not
     */
    public boolean checkNewRecord(long newTime, String playerName) {
        if (bestTime == 0 || newTime < bestTime) {
            bestTime = newTime;
            bestPlayer = playerName;

            Main.getInstance().getConfig().set("courses." + name + ".bestTime", newTime);
            Main.getInstance().getConfig().set("courses." + name + ".bestPlayer", playerName);
            Main.getInstance().saveConfig();
            return true;
        }
        return false;
    }

    public void resetHightscore() {
        bestTime = 0;
        bestPlayer = null;

        Main.getInstance().getConfig().set("courses." + name + ".bestTime", null);
        Main.getInstance().getConfig().set("courses." + name + ".bestPlayer", null);
        Main.getInstance().saveConfig();
    }

    /**
     * Saves the initial course data to config. Should only be run for a completely new course
     */
    public void doFirstTimeSetup() {
        Main.getInstance().getConfig().set("courses." + name + ".location", this.startLocation);
        Main.getInstance().getConfig().set("courses." + name + ".killheight", this.killHeight);
        Main.getInstance().getConfig().set("courses." + name + ".checkpoints", new String[]{});
        Main.getInstance().saveConfig();
    }

    /*
     * Getters and setters
     */

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
        Main.getInstance().getConfig().set("courses." + name + ".location", this.startLocation);
        Main.getInstance().saveConfig();
    }

    public void setKillHeight(int killHeight) {
        this.killHeight = killHeight;

        Main.getInstance().getConfig().set("courses." + name + ".killheight", killHeight);
        Main.getInstance().saveConfig();
    }

    public Checkpoint getCheckpoint(int index) {
        try {
            return checkpoints.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public int getNumOfCheckpoints() {
        return checkpoints.size();
    }

    public String getName() {
        return name;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public int getKillHeight() {
        return killHeight;
    }

    public Long getBestTime() {
        return bestTime;
    }

    public String getBestPlayer() {
        return bestPlayer;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public int getTimesCompleted() {
        return timesCompleted;
    }

    public void incrementTimesPlayed() {
        timesPlayed++;
        // TimesPlayed and timesCompleted might increase often, so saving these values to config should only be done once every n minutes
        Main.getInstance().getConfig().set("courses." + name + ".timesPlayed", timesPlayed);
    }

    public void incrementTimesCompleted() {
        timesCompleted++;
        Main.getInstance().getConfig().set("courses." + name + ".timesCompleted", timesCompleted);
    }

    public boolean isClosed() {
        return closed;
    }

    public void open() {
        this.closed = false;
    }

    public void close() {
        this.closed = true;
    }
}
