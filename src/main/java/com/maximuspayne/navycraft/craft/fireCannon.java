package com.maximuspayne.navycraft.craft;

import com.maximuspayne.aimcannon.AimCannon;
import com.maximuspayne.aimcannon.OneCannon;
import com.maximuspayne.navycraft.NavyCraft;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class fireCannon extends BukkitRunnable
{
    OneCannon cannon;
    int cBearing;
    Craft c;

    public fireCannon(Craft craft, int bearing, OneCannon onec) {
        cannon = onec;
        cBearing = bearing;
        c = craft;
    }
    @Override
    public void run()
    {

        if ((cBearing == 0 || (cBearing > 0 && cBearing <= 45) || (cBearing > 315 && cBearing < 360)) && (!c.lastCannonFired.containsKey(cannon) || (c.lastCannonFired.containsKey(cannon) && System.currentTimeMillis() > (c.lastCannonFired.get(cannon) + 7000))) && !cannon.isIgnite() && cannon.isValidCannon(cannon.loc.getBlock())) {
            cannon.Action(null);
            c.lastCannonFired.put(cannon, System.currentTimeMillis());
        }
    }

    public static void fireCannon(Craft craft, Craft targetCraft, int dist)
    {
        for (OneCannon onec : AimCannon.cannons) {
            if ((onec.cannonType == 0 || onec.cannonType == 1 || onec.cannonType == 6) && !targetCraft.sinking && !targetCraft.isDestroying && onec.ownerCraft == craft) {
                onec.isValidCannon(onec.loc.getBlock());
                onec.getTargets(craft);
                int onecBearing = onec.targetCraft.get(targetCraft);
                if (onec.checkLever()) {
                    if (onecBearing == 90)
                        onec.turnCannon(true, null);
                    else if (onecBearing == 270)
                        onec.turnCannon(false, null);
                    onecBearing = onec.targetCraft.get(targetCraft);
                }
                Vector v;
                Player targetPlayer = null;
                Location targetLoc = null;
                if (targetCraft.captainName != null)
                    targetPlayer = NavyCraft.instance.getServer().getPlayer(targetCraft.captainName);
                if (!targetCraft.crewNames.isEmpty()) {
                    for (String name : targetCraft.crewNames) {
                        if ((name != null && !craft.isOnCraft(name, true)) && (!craft.isOnCraft(targetPlayer, true) && targetPlayer == null)) {
                            targetPlayer = NavyCraft.instance.getServer().getPlayer(name);
                            break;
                        }
                    }
                }

                if (targetPlayer != null)
                    targetLoc = targetPlayer.getLocation();
                else
                    targetLoc = targetCraft.getLocation();

                double dX = targetLoc.getX() - onec.loc.getX();
                double dY = targetLoc.getY() - (onec.loc.getY() + 1);
                double dZ = targetLoc.getZ() - onec.loc.getZ();

                v = new Vector(dX, dY, dZ);
                int y = (targetCraft.minY + (targetCraft.sizeY / 2));
                v.normalize();
                if (y <= 64 && !targetCraft.type.canDive)
                    v.add(new Vector(0, .2, 0));
                onec.updateVector(v);

                if (!onec.isCharged())
                onec.Charge(null, false);

                if (dist > 100)
                    onec.setCharge(4);
                else if (dist <= 100 && dist > 25)
                    onec.setCharge(3);
                else if (dist <= 25)
                    onec.setCharge(2);

                final int fBearing = onecBearing;
        new Thread() {
            @Override
            public void run()
            {

                setPriority(Thread.MIN_PRIORITY);
                try
                {
                            sleep(2500);
                            NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, new fireCannon(craft, fBearing, onec));

                } catch (InterruptedException e)
                {
                }
            }
        }.start();
            }
        }
    }
}
