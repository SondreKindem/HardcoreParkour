package no.sonkin.hardcoreParkour.objects;

import org.bukkit.entity.Player;

public class ResultTuple {

    public final Player player;
    public final long finishTime;

    public ResultTuple(Player player, long finishTime) {
        super();
        this.player = player;
        this.finishTime = finishTime;
    }
}