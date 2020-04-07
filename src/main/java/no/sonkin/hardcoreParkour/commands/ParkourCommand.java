package no.sonkin.hardcoreParkour.commands;

import no.sonkin.hardcoreParkour.objects.ParkourController;

/**
 * Can be extended for easy instantiation of the parkourController
 */
public abstract class ParkourCommand {
    protected ParkourController parkourController;

    public ParkourCommand(ParkourController parkourController){
        this.parkourController = parkourController;
    }
}
