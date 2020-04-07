package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandLeave extends ParkourCommand implements ICommandHandler {

    public CommandLeave(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (parkourController.leaveCourse(player)) {
            sendInfo(player, "Left the course");
        } else {
            sendError(player, "Could not leave (are you in a game? If you are, try reconnecting)");
        }
    }
}

