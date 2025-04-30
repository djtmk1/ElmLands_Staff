package org.djtmk.Elmlandsstaff.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.MetadataValue;
import org.djtmk.Elmlandsstaff.ElmLands_STAFF;

public class CropTrampleHandler implements Listener {
    private final ElmLands_STAFF plugin;

    public CropTrampleHandler(ElmLands_STAFF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Player player = getPlayerCause(event);
        if (player != null && plugin.getVanishedPlayers().contains(player)) {
            event.setCancelled(true);
        }
    }

    private Player getPlayerCause(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            return (Player) event.getEntity();
        }
        for (MetadataValue value : event.getEntity().getMetadata("PLAYER_CAUSED")) {
            if (value.getOwningPlugin() == plugin && value.value() instanceof Player) {
                return (Player) value.value();
            }
        }
        return null;
    }
}