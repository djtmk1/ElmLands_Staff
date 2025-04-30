package org.djtmk.Elmlandsstaff.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.djtmk.Elmlandsstaff.ElmLands_STAFF;

public class ReloadCommand implements CommandExecutor {
    private final ElmLands_STAFF plugin;

    public ReloadCommand(ElmLands_STAFF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("elmlands.staff.reload")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        plugin.reloadConfig();
        sender.sendMessage("Configuration reloaded.");
        return true;
    }
}