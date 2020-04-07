package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRaceCreate extends ParkourCommand implements ICommandHandler {

    public CommandRaceCreate(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String name = player.getName();
        ParkourCourse course = parkourController.getCourse(args[0]);
        int timeLimit = Integer.parseInt(args[1]);

        // Check if info is correct
        if (course == null) {  // Does the course exist?
            MessageHandler.sendError(player, "Could not create race: no course found named " + args[0] + ".");
            return;
        }
        if(course.isClosed() && !player.isOp()){
            MessageHandler.sendError(player, "Could not create race: course " + args[0] + " is closed.");
            return;
        }
        if (parkourController.getRace(name) != null) {  // Does a race already exist with this name?
            MessageHandler.sendError(player, "Could not create race: a race for '" + name + "' already exists.");
            return;
        }

        if (timeLimit <= 0) {
            parkourController.createRace(player.getUniqueId(), name, course);
        } else {
            parkourController.createRace(player.getUniqueId(), name, course, timeLimit);
        }

        // Announce to server
        MessageHandler.announceRaceCreated(name);

        // Give creator a control panel
        MessageHandler.sendRaceControlPanel(player, name, args[0]);
    }
}

