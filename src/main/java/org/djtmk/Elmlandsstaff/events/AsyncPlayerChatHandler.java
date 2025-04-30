package org.djtmk.Elmlandsstaff.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.djtmk.Elmlandsstaff.ElmLands_STAFF;

public class AsyncPlayerChatHandler implements Listener {
    private final ElmLands_STAFF plugin;

    public AsyncPlayerChatHandler(ElmLands_STAFF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.isChatMuted() && !player.hasPermission("elmlands.staff.bypassmute")) {
            player.sendMessage(ChatColor.RED + "Chat is currently muted.");
            event.setCancelled(true);
        }
    }
}