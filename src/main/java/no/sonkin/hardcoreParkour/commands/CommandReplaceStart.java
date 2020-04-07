package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static no.sonkin.hardcoreParkour.objects.MessageHandler.*;

public class CommandReplaceStart extends ParkourCommand implements ICommandHandler {

    public CommandReplaceStart(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Location location = player.getLocation();
        location.setPitch(0);

        parkourController.getCourse(args[0]).setStartLocation(location);
        MessageHandler.sendInfo(sender, "The start for §6" + args[0] + MessageHandler.getPrimaryColor() + " was set to your position");
    }
}

