package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.Main;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;

public class CommandConfigPlayerGhosts extends ParkourCommand implements ICommandHandler {

    public CommandConfigPlayerGhosts(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Main.getInstance().getSettings().setPlayerGhosts(Boolean.parseBoolean(args[0]));
        MessageHandler.sendInfo(sender, "Set playerGhosts to " + args[0]);
    }
}

