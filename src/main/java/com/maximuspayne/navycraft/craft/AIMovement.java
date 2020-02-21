package com.maximuspayne.navycraft.craft;

import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
public class AIMovement extends BukkitRunnable
{
    private Craft craft;
    private int autoType = 0;
    private boolean useWeapons = false;
    private Location target;
    private HashMap<Integer, Location> targetLocations = new HashMap<>();
    private int currentLocation = 1;
    private Long lastRun;

//written by solmex, used for moving craft automatically to locations
    public AIMovement(Craft testCraft, int type, boolean weapons, Location targetLoc, HashMap<Integer, Location> tlocs)
    {

        targetLocations = tlocs;
        craft = testCraft;
        autoType = type;
        useWeapons = weapons;
        target = targetLoc;

    }

    @Override
    public void run() {
        if (targetLocations != null && !targetLocations.isEmpty()) {
            for (int i : targetLocations.keySet()) {
                if (i == currentLocation) {
                    Location targetLoc = targetLocations.get(i);
                    float xDist = targetLoc.getBlockX() - (craft.minX + (craft.sizeX / 2.0f));
                    float zDist = targetLoc.getBlockZ() - (craft.minZ + (craft.sizeZ / 2.0f));
                    int dist = (int) Math.round(Math.sqrt((xDist * xDist) + (zDist * zDist)));
                    if (dist < 30)
                        currentLocation += 1;
                    else
                        target = targetLoc;
                    boolean doLoop = ConfigManager.getRouteData().getBoolean("Routes." + craft.customName + ".doLoop");
                    if (currentLocation > targetLocations.size() && doLoop)
                        currentLocation = 1;
                }
            }
        }
        Location targetLoc1 = null;
        int targetBearing = -1;
        Craft c = null;
        HashMap<Integer, Craft> cdistances = new HashMap<>();
        if (!craft.targetCraft.isEmpty()) {
            for (Craft tc : craft.targetCraft.keySet()) {
                if (!tc.isDestroying && !tc.sinking) {
                    Location cLoc = new Location(tc.world, (tc.minX + (tc.sizeX / 2.0f)), (tc.minY + (tc.sizeY / 2.0f)), (tc.minZ + (tc.sizeZ / 2.0f)));
                    float xDist = cLoc.getBlockX() - (craft.minX + (craft.sizeX / 2.0f));
                    float zDist = cLoc.getBlockZ() - (craft.minZ + (craft.sizeZ / 2.0f));
                    int dist = (int) Math.round(Math.sqrt((xDist * xDist) + (zDist * zDist)));
                    cdistances.put(dist, tc);
                }
            }
        }
        if (!cdistances.isEmpty()) {
                int dist = Utils.getSmallest(cdistances.keySet().toArray());
                c = cdistances.get(dist);
                Location cLoc = new Location(c.world, (c.minX + (c.sizeX / 2.0f)), (c.minY + (c.sizeY / 2.0f)), (c.minZ + (c.sizeZ / 2.0f)));
                if (target == null) {
                    targetBearing = craft.targetCraft.get(c);
                    targetLoc1 = cLoc;
                }
        }
        if (targetLoc1 == null) {
            targetLoc1 = target;
            if (targetLoc1 != null)
            targetBearing = Utils.calculateBearing(craft, targetLoc1);
        }
        if (targetLoc1 != null) {
            float xDist = targetLoc1.getBlockX() - (craft.minX + (craft.sizeX / 2.0f));
            float yDist = targetLoc1.getBlockY() - (craft.minY + (craft.sizeY / 2.0f));
            float zDist = targetLoc1.getBlockZ() - (craft.minZ + (craft.sizeZ / 2.0f));
            int dist = (int) Math.round(Math.sqrt((xDist * xDist) + (zDist * zDist)));

            NavyCraft.instance.DebugMessage(String.valueOf(targetBearing), 1);
            NavyCraft.instance.DebugMessage(targetLoc1.toString(), 1);

            if (craft.leftSafeDock && targetBearing != -1)
                if (targetBearing == 90)
                craft.rudderChange(null, 1, true);
            else if (targetBearing == 180)
                craft.rudderChange(null, -1, true);
            else if (targetBearing == 270)
                craft.rudderChange(null, -1, true);
            else if ((targetBearing > 0 && targetBearing < 90) || (targetBearing > 90 && targetBearing <= 135) || (targetBearing > 135 && targetBearing < 180))
                craft.rudderChange(null, 1, false);
            else if ((targetBearing > 225 && targetBearing < 270) || (targetBearing > 270 && targetBearing <= 315) || (targetBearing > 315 && targetBearing < 360))
                craft.rudderChange(null, -1, false);
            else if (targetBearing == 0)
                craft.rudder = 0;


            if (dist < 100 && craft.blockCountStart <= 2500) {
                if (ConfigManager.routeData.getBoolean("Routes." + craft.customName + ".doRam")) {
                    craft.gear = craft.type.maxForwardGear;
                    craft.setSpeed = craft.type.maxSpeed;
                    craft.enginesOn = true;
                } else if (dist > 25) {
                    craft.gear = 1;
                    craft.setSpeed = 0;
                    craft.enginesOn = false;
                } else {
                    craft.gear = craft.type.maxForwardGear;
                    craft.setSpeed = craft.type.maxSpeed;
                    craft.enginesOn = true;
                }
            } else if (dist < 100 && craft.blockCountStart > 2500) {
                if (ConfigManager.routeData.getBoolean("Routes." + craft.customName + ".doRam")) {
                    craft.gear = craft.type.maxForwardGear;
                    craft.setSpeed = craft.type.maxSpeed;
                    craft.enginesOn = true;
                } else {
                    craft.gear = 1;
                    craft.setSpeed = 0;
                    craft.enginesOn = false;
                }
            } else {
                craft.gear = craft.type.maxForwardGear;
                craft.setSpeed = craft.type.maxSpeed;
                craft.enginesOn = true;
            }


            if (craft.type.canFly || craft.type.canDive) {
                if (yDist > 15) {
                    craft.vertPlanes = -1;
                } else if (yDist < -15) {
                    craft.vertPlanes = 1;
                } else if (yDist == 15) {
                    craft.vertPlanes = 0;
                }
            }

            if (craft.type.canFly) {
                if(craft.onGround) {
                    craft.gear = craft.type.maxForwardGear;
                    craft.setSpeed = craft.type.maxSpeed;
                    craft.enginesOn = true;
                    craft.vertPlanes = 1;
                }
            }

            craft.adRadarOn = true;
            craft.sonarOn = true;
            craft.hfOn = true;
            craft.radarOn = true;

            if (dist <= 250 && c != null && useWeapons) {
                final Craft ftc = c;
                final Craft fcraft = craft;
                fireCannon.fireCannon(craft, c, dist);
                fireWeapon.fireWeapon(craft, c, dist);
                new Thread() {
                    @Override
                    public void run()
                    {

                        setPriority(Thread.MIN_PRIORITY);
                        try {
                            for (int c = 0; c < 6; c++) {
                                fireMachineGun.fireMachineGun(fcraft, ftc);
                            sleep(450);
                        }
                        } catch (InterruptedException e)
                        {
                        }
                    }
                }.start();
            }
            if (craft.sinking || craft.isDestroying) {
                this.cancel();
                NavyCraft.AITasks.remove(craft);
            }
        }
    }
}