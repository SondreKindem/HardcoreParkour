package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChallenge extends ParkourCommand implements ICommandHandler {

    public CommandChallenge(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Player opponent = Bukkit.getPlayer(args[0]);
        String name = player.getName();
        ParkourCourse course = parkourController.getCourse(args[1]);

        // Check if info is correct
        if (course == null) {
            MessageHandler.sendError(player, "Could not create race: no course found named " + args[1] + ".");
            return;
        }
        if (opponent == null) {
            MessageHandler.sendError(player, "Could not find player named " + args[0] + ".");
            return;
        }
        if (course.isClosed() && !player.isOp()) {
            MessageHandler.sendError(player, "Could not create race: course " + args[1] + " is closed.");
            return;
        }
        if (parkourController.getRace(name) != null) {
            MessageHandler.sendError(player, "Could not create race: a race for '" + name + "' already exists.");
            return;
        }

        parkourController.createRaceChallenge(player.getUniqueId(), opponent.getUniqueId(), name, course);
        MessageHandler.sendInfo(player, "Sent race challenge to " + args[0]);
        MessageHandler.sendRaceChallenge(player, opponent, args[1]);
    }
}

