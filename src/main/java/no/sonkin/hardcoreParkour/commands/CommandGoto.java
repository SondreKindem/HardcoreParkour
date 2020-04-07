package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

/**
 * Handler for the goto command. Gets the parkourCourse with matching name and teleports the player to the course start point.
 */
public class CommandGoto extends ParkourCommand implements ICommandHandler {

    public CommandGoto(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ParkourCourse course = parkourController.getCourse(args[0]);

        if (course != null) {
            sendInfo(player, "Teleporting to " + args[0]);
            player.teleport(course.getStartLocation());
        } else {
            sendError(player, "Found no course named " + args[0]);
        }
    }
}
