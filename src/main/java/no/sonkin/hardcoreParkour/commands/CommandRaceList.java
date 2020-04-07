package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;

public class CommandRaceList extends ParkourCommand implements ICommandHandler {

    public CommandRaceList(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        MessageHandler.sendRaceList(sender, parkourController.getRaceSessions());
    }
}

