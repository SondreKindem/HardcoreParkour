package no.sonkin.hardcoreParkour;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Util {
    /**
     * Check if a location is within the cuboid region of two other locations.
     *
     * @param locationToCheck the location which may or may not be inside the region
     * @param corner1         one corner of the region. Has to be opposite to corner2
     * @param corner2         the other corner of the region. Has to be opposite to corner1
     * @return true if the location is within the region, false if not
     */
    public static Boolean isInCube(Location locationToCheck, Location corner1, Location corner2) {
        int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        if ((locationToCheck.getBlockX() <= xMax) && (locationToCheck.getBlockX() >= xMin)) {
            if ((locationToCheck.getBlockY() <= yMax) && (locationToCheck.getBlockY() >= yMin)) {
                return (locationToCheck.getBlockZ() <= zMax) && (locationToCheck.getBlockZ() >= zMin);
            }
        }
        return false;
    }

    /**
     * Convert a timestamp of milliseconds into minutes and seconds.
     * Example output: 01:22
     *
     * @param millis time in milliseconds
     * @return a string containing minutes and seconds
     */
    public static String millisToFormattedTime(long millis) {
        return String.format("%02d:%02d", (millis / 1000) / 60, (millis / 1000) % 60);
    }

    /**
     * Split a list into pages and return a given page
     * By kisna @ https://stackoverflow.com/a/36854196
     *
     * @param sourceList the list to extract a page from
     * @param page       which page to get. Must be larger than 1
     * @param pageSize   how large each page should be
     * @return a sublist of the input list
     */
    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        if (pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() < fromIndex) {
            return Collections.emptyList();
        }

        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }
}
