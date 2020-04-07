package no.sonkin.hardcoreParkour.objects;

import no.sonkin.hardcoreParkour.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class Settings {
    private boolean spectateAfterRace;
    private boolean broadcastRaceFinish;
    private boolean playerGhosts;
    private int autosave;

    public Settings(ConfigurationSection configuration) {
        if (configuration == null) {
            // Uh-oh for some reason there is no "settings" value in the config. Lets try to recover...
            Bukkit.getServer().getLogger().warning("[HardcoreParkour] Did not find the 'settings' section in config.yml. Trying to recover...");
            Main.getInstance().getConfig().set("settings", "");
            Main.getInstance().saveConfig();
            configuration = Main.getInstance().getConfig().getConfigurationSection("settings");
        }

        assert configuration != null;  // Surely configuration cannot be null at this point
        this.spectateAfterRace = configuration.getBoolean("spectateAfterRace", true); // The second param, the boolean, is the default value if null
        this.broadcastRaceFinish = configuration.getBoolean("broadcastRaceFinish", true);
        this.playerGhosts = configuration.getBoolean("playerGhosts", true);
        if (configuration.contains("autosave")) {
            this.autosave = configuration.getInt("autosave");
        } else {
            this.autosave = 30;
        }
    }

    public boolean isSpectateAfterRace() {
        return spectateAfterRace;
    }

    public void setSpectateAfterRace(boolean spectateAfterRace) {
        this.spectateAfterRace = spectateAfterRace;
        Main.getInstance().getConfig().set("settings.spectateAfterRace", spectateAfterRace);
        Main.getInstance().saveConfig();
    }

    public boolean isBroadcastRaceFinish() {
        return broadcastRaceFinish;
    }

    public void setBroadcastRaceFinish(boolean broadcastRaceFinish) {
        this.broadcastRaceFinish = broadcastRaceFinish;
        Main.getInstance().getConfig().set("settings.broadcastRaceFinish", broadcastRaceFinish);
        Main.getInstance().saveConfig();
    }

    public boolean isPlayerGhosts() {
        return playerGhosts;
    }

    public void setPlayerGhosts(boolean playerGhosts) {
        this.playerGhosts = playerGhosts;
    }

    public int getAutosave() {
        return autosave;
    }

    public void setAutosave(int autosave) {
        this.autosave = autosave;
    }
}
