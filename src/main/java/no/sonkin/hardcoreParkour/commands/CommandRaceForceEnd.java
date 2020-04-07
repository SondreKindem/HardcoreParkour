package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.CommandResult;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandRaceForceEnd extends ParkourCommand implements ICommandHandler {

    public CommandRaceForceEnd(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        CommandResult result = parkourController.endRace(null, args[0]);
        if(result.isSuccess()){
            sendInfo(sender, "Ended race successfully");
        } else{
            sendError(sender,result.getMessage());
        }
    }
}

