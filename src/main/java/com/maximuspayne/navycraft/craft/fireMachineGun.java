package com.maximuspayne.navycraft.craft;

import com.maximuspayne.aimcannon.explosiveEgg;
import com.maximuspayne.navycraft.NavyCraft;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class fireMachineGun extends BukkitRunnable
{
    Sign s;
    Craft c;
    Vector firingVector;

    public fireMachineGun(Craft craft, Vector vec, Sign sign) {
        s = sign;
        c = craft;
        firingVector = vec;
    }
    @Override
    public void run()
    {
        Egg newEgg = s.getWorld().spawn(s.getLocation().add(0, 2, 0), Egg.class);
        newEgg.setVelocity(firingVector.normalize().add(new Vector(0, .05, 0)));
        newEgg.setVelocity(newEgg.getVelocity().multiply(3.0f));
        NavyCraft.explosiveEggsList.add(new explosiveEgg(newEgg, 1.8));
        s.getWorld().playEffect(s.getLocation(), Effect.SMOKE, 0);
        CraftMover.playWeaponSound(s.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f,
                1.70f);
    }

    public static void fireMachineGun(Craft craft, Craft targetCraft)
    {
        for (Sign sign : craft.mgList) {
                Vector v;
                Player targetPlayer = null;
                Location targetLoc = null;
                if (targetCraft.captainName != null)
                    targetPlayer = NavyCraft.instance.getServer().getPlayer(targetCraft.captainName);
                if (!targetCraft.crewNames.isEmpty()) {
                    for (String name : targetCraft.crewNames) {
                        if (name != null && targetPlayer == null) {
                            targetPlayer = NavyCraft.instance.getServer().getPlayer(name);
                            break;
                        }
                    }
                }

                if (targetPlayer != null)
                    targetLoc = targetPlayer.getLocation();
                else
                    targetLoc = null;

                if (targetLoc != null) {
                    double dX = targetLoc.getX() - sign.getX();
                    double dY = targetLoc.getY() - (sign.getY() + 2);
                    double dZ = targetLoc.getZ() - sign.getZ();

                    v = new Vector(dX, dY, dZ);
                    NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, new fireMachineGun(craft, v, sign));
                }
            }
        }
    }
