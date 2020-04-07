package no.sonkin.hardcoreParkour.commands;

import com.github.hornta.ICommandHandler;
import no.sonkin.hardcoreParkour.objects.MessageHandler;
import no.sonkin.hardcoreParkour.objects.ParkourController;
import no.sonkin.hardcoreParkour.objects.ParkourCourse;
import org.bukkit.command.CommandSender;

public class CommandInfo  extends ParkourCommand implements ICommandHandler {

    public CommandInfo(ParkourController parkourController){
        super(parkourController);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        ParkourCourse course = parkourController.getCourse(args[0]);
        if(course == null){
            MessageHandler.sendError(sender, "Could not find a course named " + args[0]);
        }else{
            MessageHandler.sendCourseInfo(sender, course);
        }
    }
}
