package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import org.bukkit.command.CommandSender;

public class CommandClearCheckpoints extends ParkourCommand implements ICommandHandler {

    public CommandClearCheckpoints(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        ParkourCourse course = parkourController.getCourse(args[0]);
        if(course != null){
            course.clearCheckpoints();
            MessageHandler.sendInfo(sender, "Cleared checkpoints for " + args[0]);
        } else{
            MessageHandler.sendError(sender, "Could not find course named " + args[0]);
        }
    }
}

