package org.djtmk.Elmlandsstaff.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.djtmk.Elmlandsstaff.ElmLands_STAFF;

public class MobAttackHandler implements Listener {
    private final ElmLands_STAFF plugin;

    public MobAttackHandler(ElmLands_STAFF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player && plugin.getVanishedPlayers().contains(player)) {
            event.setCancelled(true);
        }
    }
}