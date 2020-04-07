package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;

public class CommandClose extends ParkourCommand implements ICommandHandler {

    public CommandClose(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (parkourController.closeCourse(args[0])) {
            MessageHandler.sendInfo(sender, "Course " + args[0] + " was closed.");
        } else {
            MessageHandler.sendInfo(sender, "Could not find a course named " + args[0] + ".");
        }
    }
}

