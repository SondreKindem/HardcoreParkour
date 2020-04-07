package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUsage extends ParkourCommand implements ICommandHandler {

    public CommandUsage(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        try{
            Player player = (Player) sender;
            MessageHandler.sendUsagePlayer(player, Integer.parseInt(args[0]));
        } catch (ClassCastException Ignored){  // If we cannot cast to player, that means that the console ran the command
            MessageHandler.sendUsageConsole(sender, Integer.parseInt(args[0]));
        }
    }
}

