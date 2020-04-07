package no.sonkin.hardcoreParkour;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * From https://bukkit.org/threads/lib-ghostfactory-make-players-look-like-ghosts.149088/page-4
 */
public class GhostFactory {
    /**
     * Team of ghosts and people who can see ghosts.
     */
    private static final String GHOST_TEAM_NAME = "Ghosts";
    private static final long UPDATE_DELAY = 20L;

    private Team ghostTeam;

    // Task that must be cleaned up
    private BukkitTask task;
    private boolean closed;

    // Players that are actually ghosts
    private Set<String> ghosts = new HashSet<>();

    public GhostFactory(Plugin plugin) {
        createTask(plugin);
        createGetTeam();
    }

    private void createGetTeam() {
        if(Bukkit.getServer().getScoreboardManager() != null){
            Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

            ghostTeam = board.getTeam(GHOST_TEAM_NAME);

            // Create a new ghost team if needed
            if (ghostTeam == null) {
                ghostTeam = board.registerNewTeam(GHOST_TEAM_NAME);
            }
            // Thanks to Rprrr for noticing a bug here
            ghostTeam.setCanSeeFriendlyInvisibles(true);
        }
    }

    private void createTask(Plugin plugin) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (String member : getMembers()) {

                if (member != null) {
                    // Update invisibility effect
                    setGhost(member, isGhost(member));
                } else {
                    ghosts.remove(member);
                    ghostTeam.removeEntry(member);
                }
            }
        }, UPDATE_DELAY, UPDATE_DELAY);
    }

    /**
     * Add the given player to this ghost manager. This ensures that it can see ghosts, and later become one.
     * @param player - the player to add to the ghost manager.
     */
    public void addPlayer(String player) {
        validateState();
        if (!ghostTeam.hasEntry(player) && Bukkit.getPlayer(player) != null) {
            ghostTeam.addEntry(player);
            Bukkit.getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        }
    }

    /**
     * Determine if the given player is tracked by this ghost manager and is a ghost.
     * @param player - the player to test.
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean isGhost(String player) {
        return player != null && hasPlayer(player) && ghosts.contains(player);
    }

    /**
     * Determine if the current player is tracked by this ghost manager, or is a ghost.
     * @param player - the player to check.
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean hasPlayer(String player) {
        validateState();
        return ghostTeam.hasEntry(player);
    }

    /**
     * Set whether or not a given player is a ghost.
     * @param player - the player to set as a ghost.
     * @param isGhost - TRUE to make the given player into a ghost, FALSE otherwise.
     */
    public void setGhost(String player, boolean isGhost) {
        if(Bukkit.getPlayer(player) != null){
            // Make sure the player is tracked by this manager
            if (!hasPlayer(player))
                addPlayer(player);

            if (isGhost) {
                ghosts.add(player);
                Bukkit.getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
            } else {
                ghosts.remove(player);
                Bukkit.getPlayer(player).removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    /**
     * Remove the given player from the manager, turning it back into the living and making it unable to see ghosts.
     * @param player - the player to remove from the ghost manager.
     */
    public void removePlayer(Player player) {
        validateState();
        if (ghostTeam.removeEntry(player.getName())) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    /**
     * Retrieve every ghost and every player that can see ghosts.
     * @return Every ghost or every observer.
     */
    public String[] getMembers() {
        validateState();
        return ghostTeam.getEntries().toArray(new String[0]);
    }

    public void close() {
        if (!closed) {
            task.cancel();
            ghostTeam.unregister();
            closed = true;
        }
    }

    private void validateState() {
        if (closed) {
            throw new IllegalStateException("Ghost factory has closed. Cannot reuse instances.");
        }
    }
}