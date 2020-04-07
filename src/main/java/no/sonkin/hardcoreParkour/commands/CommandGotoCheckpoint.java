package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import no.sonkin.hardcoreParkour.objects.Checkpoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandGotoCheckpoint extends ParkourCommand implements ICommandHandler {

    public CommandGotoCheckpoint(ParkourController parkourController){
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ParkourCourse course = parkourController.getCourse(args[0]);

        if (course != null){
            Checkpoint checkpoint = course.getCheckpoint(Integer.parseInt(args[1]));
            if(checkpoint != null){
                player.teleport(checkpoint.getCheckpointCenter());
            }else{
                sendInfo(player,"Did not find checkpoint of index " + args[1]);
            }
        }else{
            sendError(player,"Could not find course named " + args[0]);
        }
    }
}

