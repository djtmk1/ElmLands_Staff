package org.djtmk.Elmlandsstaff.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.djtmk.Elmlandsstaff.ElmLands_STAFF;

public class PlayerInteractHandler implements Listener {
    private final ElmLands_STAFF plugin;

    public PlayerInteractHandler(ElmLands_STAFF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType().isAir()) return;

        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) return;

        String displayName = meta.getDisplayName();
        if (!plugin.isStaffModeEnabled()) return;

        if (displayName.equals(ChatColor.GREEN + "Toggle Vanish")) {
            if (player.hasPermission("elmlands.staff.vanish")) {
                plugin.toggleVanish(player);
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            event.setCancelled(true);
        } else if (displayName.equals(ChatColor.GREEN + "Staff Teleport")) {
            if (player.hasPermission("elmlands.staff.staffrtp")) {
                if (player.hasMetadata("vanished")) {
                    if (!plugin.getOnlinePlayers().isEmpty()) {
                        plugin.staffTeleport(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "There are no players online.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You must be vanished to use this command!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            event.setCancelled(true);
        } else if (displayName.equals(ChatColor.GREEN + "Toggle Nightvision")) {
            if (player.hasPermission("elmlands.staff.nightvision")) {
                plugin.toggleNightVision(player);
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            event.setCancelled(true);
        }
    }
}