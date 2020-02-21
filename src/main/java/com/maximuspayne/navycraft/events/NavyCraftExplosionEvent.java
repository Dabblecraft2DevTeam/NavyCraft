package com.maximuspayne.navycraft.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;

public class NavyCraftExplosionEvent extends Event implements Cancellable{
    /*
    Thrown when Max's custom explosion code is used, created for use in restoring navycraft explosions.

    Note: Max's explosion code uses standard spigot explosions alongside hia proprietary method; if you're using this event for
    restoring explosions, you'll probably have to catch the BlockExplosion event from Spigot, too.

    -iiz
     */
    public NavyCraftExplosionEvent (HashMap<Location,Material> initDestroyedBlocks) {
        destroyedBlocks = initDestroyedBlocks;
    }

    private static final HandlerList handlers = new HandlerList();

    public HashMap<Location,Material> destroyedBlocks = new HashMap<>();
    boolean cancelled = false;

    /*
    public ArrayList<Block> getDestroyedBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();

        for (BlockState blockState : destroyedBlocks) {
            blocks.add(blockState.getBlock());
        }

        return blocks;
    }
    */

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }

}
