package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.sendError;
import static no.sonkin.hardcoreParkour.objects.MessageHandler.sendInfo;

/**
 * Handles the command for creating a new parkour course.
 */
public class CommandCreate extends ParkourCommand implements ICommandHandler {

    public CommandCreate(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;
        Location location = player.getLocation();
        location.setPitch(0);
        if (parkourController.createCourse(args[0], location)) {
            sendInfo(player, "Created a new parkour course at your position named §6" + args[0]);
        } else {
            sendError(player, "A course named '" + args[0] + "' already exists");
        }
    }
}
