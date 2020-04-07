package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.CommandResult;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandJoin extends ParkourCommand implements ICommandHandler {

    public CommandJoin(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        CommandResult result = parkourController.joinCourse(player, args[0]);
        if (result.isSuccess()) {
            sendInfo(player, result.getMessage());
        } else {
            sendError(player, result.getMessage());
        }

    }
}
