package com.maximuspayne.navycraft.craft;

import com.maximuspayne.aimcannon.OneCannon;
import com.maximuspayne.navycraft.NavyCraft;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ConcurrentModificationException;

public class fireWeapon extends BukkitRunnable
{

    Sign s;
    Craft craft;
    Craft targetCraft;
    int dist;
    public fireWeapon(Sign sign, Craft c, Craft tc, int distance) {
        s = sign;
        craft = c;
        targetCraft = tc;
        dist = distance;
    }
    @Override

    public void run()
    {
        int i = craft.fireControlSigns.get(s);
        int targetID = -1;
        for (int id : craft.sonarTargetIDs2.keySet()) {
            if (craft.sonarTargetIDs2.get(id) == targetCraft)
                targetID = id;
        }

        if (targetID > -1) {
            craft.fireControlTargets.put(i, targetID);
            craft.tubeFiringMode.put(i, targetID);
            craft.tubeFiringDisplay.put(i, 0);
            craft.tubeFiringDepth.put(i, targetCraft.getLocation().getBlockY());
            craft.tubeFiringArm.put(i, 20);
            craft.tubeFiringArmed.put(i, true);
            craft.tubeFiringHeading.put(i, targetCraft.rotation);
            craft.tubeFiringAuto.put(i, true);
            craft.tubeFiringRudder.put(i, 0);
            craft.lastFireControlSet.put(i, System.currentTimeMillis());
        }
        if (OneCannon.findandOpenTube(craft, i, null, true)) {

            if ((craft.lastFireControlFired.containsKey(i) && System.currentTimeMillis() > craft.lastFireControlFired.get(i) + 15000) || !craft.lastFireControlFired.containsKey(i)) {
                OneCannon.findandFireTube(craft, i, null);
                craft.lastFireControlFired.put(i, System.currentTimeMillis());
            }
        }
    }

    public static void fireWeapon(Craft c, Craft tc, int distance)
    {
        for (Sign sign : c.fireControlSigns.keySet()) {
        new Thread() {
            @Override
            public void run()
            {

                setPriority(Thread.MIN_PRIORITY);
                try
                {
                        sleep(2500);
                        NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, new fireWeapon(sign, c, tc, distance));

                } catch (InterruptedException | ConcurrentModificationException e)
                {
                }
            }
        }.start();
    }
    }
}
