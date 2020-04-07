package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.CommandResult;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandRaceEnd extends ParkourCommand implements ICommandHandler {

    public CommandRaceEnd(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        CommandResult result = parkourController.endRace(player, player.getName());
        if (result.isSuccess()) {
            sendInfo(player, "Ended race successfully");
        } else {
            sendError(player, result.getMessage());
        }
    }
}

