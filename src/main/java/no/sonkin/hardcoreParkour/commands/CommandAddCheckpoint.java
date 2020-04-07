package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import no.sonkin.hardcoreParkour.objects.Checkpoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandAddCheckpoint extends ParkourCommand implements ICommandHandler {

    public CommandAddCheckpoint(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        ParkourCourse course = parkourController.getCourse(args[0]);
        Player player = (Player) sender;

        if (course != null) {
            Checkpoint checkpoint;
            Location location = player.getLocation();

            int radius = Integer.parseInt(args[1]);
            int height = Integer.parseInt(args[2]);

            if (radius != -1) {
                if (height != -1) {
                    if (radius < 0 || height < 0) {
                        sendError(player, "All values must be positive integers!");
                        return;
                    }
                    checkpoint = new Checkpoint(location, radius, height);
                } else {
                    // all missing arg
                    sendError(player, "Missing seconds argument.");
                    return;
                }
            } else {
                // 0 args
                checkpoint = new Checkpoint(location, 0, 0);
            }

            course.addCheckpoint(checkpoint);
            sendInfo(player, "Added checkpoint at your location.");

        } else {
            sendError(player, "Could not find course named " + args[0]);
        }
    }
}
