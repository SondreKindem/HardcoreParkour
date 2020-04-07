package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRespawn extends ParkourCommand implements ICommandHandler {

    public CommandRespawn(ParkourController parkourController) {
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(parkourController.respawnPlayer(player)){
            MessageHandler.sendInfo(sender, "Respawning...");
        } else{
            MessageHandler.sendError(sender, "Cannot respawn. You are not in a course or race!");
        }
    }
}

