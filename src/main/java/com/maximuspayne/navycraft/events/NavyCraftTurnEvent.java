package com.maximuspayne.navycraft.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.maximuspayne.navycraft.craft.Craft;

public class NavyCraftTurnEvent extends Event implements Cancellable {
    
    private int degrees;
    private boolean cancelled;
    private final Craft craft;
    
    public NavyCraftTurnEvent(Craft craft, int degrees) {
        this.craft = craft;
        this.setDegrees(degrees);
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    public int getDegrees() {
        return degrees;
    }

    public Craft getCraft() {
        return craft;
    }

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
}
