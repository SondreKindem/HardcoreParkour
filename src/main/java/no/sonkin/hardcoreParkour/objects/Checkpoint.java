package no.sonkin.hardcoreParkour.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A checkpoint is an area consisting of a center location, a radius and a height, which in turn defines a region that a player can collide with.
 * A ParkourCourse usually contains multiple checkpoints, where the last checkpoint is the finish line
 */
public class Checkpoint {
    private Location checkpointCenter;
    private int radius;
    private int height;
    private Location bottomCorner;
    private Location topCorner;

    public Checkpoint(Location checkpointCenter, int radius, int height) {
        this.checkpointCenter = checkpointCenter.getBlock().getLocation();
        this.checkpointCenter.setYaw(checkpointCenter.getYaw());  // Gotta set the yaw, else it is 0 after getting block location
        this.radius = radius;
        this.height = height;

        // NOTE: the isInCube function will automatically get the corners of the block in the location.
        // This is just so we can get the rough location of the edges of the checkpoint region
        topCorner = this.checkpointCenter.clone();
        topCorner.subtract(radius, 0, radius);

        bottomCorner = this.checkpointCenter.clone();
        bottomCorner.add(radius, height, radius);

        this.checkpointCenter.setX(this.checkpointCenter.getX() + 0.5);  // Set location to center of block instead of bottom-left corner
        this.checkpointCenter.setZ(this.checkpointCenter.getZ() + 0.5);  // This way the player respawns in the center of the block
    }

    public boolean isInside(Player player) {
        return no.sonkin.hardcoreParkour.Util.isInCube(player.getLocation(), bottomCorner, topCorner);
    }

    public Location getCheckpointCenter() {
        return checkpointCenter;
    }

    public int getRadius() {
        return radius;
    }

    public int getHeight() {
        return height;
    }

    public float getYaw() {
        return checkpointCenter.getYaw();
    }

    public void setYaw(float yaw) {
        checkpointCenter.setYaw(yaw);
    }

    /**
     * Put all fields into a map. Useful for serializing a checkpoint to store it in a config file
     *
     * @return a map containing the class fields
     */
    public Map<String, Object> toMap() {
        return Stream.of(new Object[][]{
                {"radius", radius},
                {"height", height},
                {"location", checkpointCenter}
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));
    }
}
