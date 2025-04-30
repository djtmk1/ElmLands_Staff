package org.djtmk.Elmlandsstaff.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.djtmk.Elmlandsstaff.ElmLands_STAFF;

public class TestingPlayers implements Listener {
    private final ElmLands_STAFF plugin;

    public TestingPlayers(ElmLands_STAFF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Handle CPS testing
        if (plugin.getCpsTests().containsKey(player.getUniqueId()) && event.getAction().isLeftClick()) {
            plugin.getCpsTests().merge(player.getUniqueId(), 1, Integer::sum);
        }
        // Handle frozen players
        if (plugin.getFrozenPlayers().contains(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are frozen and cannot interact!");
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        // Handle CPS testing
        if (plugin.getCpsTests().containsKey(player.getUniqueId())) {
            plugin.getCpsTests().merge(player.getUniqueId(), 1, Integer::sum);
        }
        // Handle frozen players
        if (plugin.getFrozenPlayers().contains(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are frozen and cannot interact!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFrozenPlayers().contains(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to == null || from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                event.setTo(from);
                player.sendMessage(ChatColor.RED + "You are frozen and cannot move!");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFrozenPlayers().contains(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are frozen and cannot break blocks!");
        }
    }
}