package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.Main;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;

public class CommandConfigBroadcastRaceFinish extends ParkourCommand implements ICommandHandler {

    public CommandConfigBroadcastRaceFinish(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Main.getInstance().getSettings().setBroadcastRaceFinish(Boolean.parseBoolean(args[0]));
        MessageHandler.sendInfo(sender, "Set broadcastRaceFinish to " + args[0]);
    }
}

