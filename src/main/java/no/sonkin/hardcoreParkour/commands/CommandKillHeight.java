package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandKillHeight extends ParkourCommand implements ICommandHandler {

    public CommandKillHeight(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender commandSender, String[] args) {
        int killHeight;
        int heightArgument = Integer.parseInt(args[1]);
        Player player = (Player) commandSender;

        if (heightArgument < 0) {
            killHeight = player.getLocation().getBlockY();
        } else {
            killHeight = heightArgument;
        }

        ParkourCourse course = parkourController.getCourse(args[0]);
        if (course != null) {
            course.setKillHeight(killHeight);
            sendInfo(player, "Kill level set to " + killHeight);
            if(killHeight >= course.getStartLocation().getY()){
                sendError(player, "Kill level is set to the same height or higher as the course start. The course will be unplayable.");
            }
        } else {
            sendError(player, "Could not find course named " + args[0]);
        }


    }
}
