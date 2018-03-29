package com.maximuspayne.navycraft.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import com.maximuspayne.navycraft.craft.Craft;

public class NavyCraftMoveEvent extends Event implements Cancellable {


    private Vector movement;
    private final Craft craft;
    private boolean cancelled;
    
    public NavyCraftMoveEvent(Craft craft, int x, int y, int z) {
        this.movement = new Vector(x, y, z);
        this.craft = craft;
        this.cancelled = false;
    }
    
    public Vector getMovement() {
        return movement;
    }

    public void setMovement(Vector movement) {
        this.movement = movement;
    }

    public Craft getCraft() {
        return craft;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

	@Override
	public HandlerList getHandlers() {
		
		return null;
	}
}
