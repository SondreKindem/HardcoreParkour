package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.CommandResult;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandRaceForceStart extends ParkourCommand implements ICommandHandler {

    public CommandRaceForceStart(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        CommandResult result = parkourController.startRace(null, args[0], Integer.parseInt(args[1]));
        if (result.isSuccess()) {
            sendInfo(sender, "Forcing the race to start.");
        }else{
            sendError(sender, result.getMessage());
        }
    }
}

