package org.djtmk.Elmlandsstaff.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.djtmk.Elmlandsstaff.ElmLands_STAFF;

public class PlayerPickupItemHandler implements Listener {
    private final ElmLands_STAFF plugin;

    public PlayerPickupItemHandler(ElmLands_STAFF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getVanishedPlayers().contains(player)) {
            event.setCancelled(true);
        }
    }
}