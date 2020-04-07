package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandRemove extends ParkourCommand implements ICommandHandler {

    public CommandRemove(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (parkourController.removeCourse(args[0])) {
            sendInfo(player, "Course named " + args[0] + " was removed.");
        } else {
            sendError(player, "No course named " + args[0] + " was found.");
        }
    }
}