package org.djtmk.Elmlandsstaff;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.djtmk.Elmlandsstaff.commands.ReloadCommand;
import org.djtmk.Elmlandsstaff.events.*;

import java.util.*;

public class ElmLands_STAFF extends JavaPlugin implements Listener {
    private final List<Player> onlinePlayers = new ArrayList<>();
    private final List<Player> vanishedPlayers = new ArrayList<>();
    private final List<Player> frozenPlayers = new ArrayList<>();
    private final Map<UUID, Integer> cpsTests = new HashMap<>();
    private final Queue<Player> teleportQueue = new LinkedList<>();
    private boolean chatMuted = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("ELMLANDS-STAFF HAS ENABLED!");

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AntiVPN(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractHandler(this), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupItemHandler(this), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatHandler(this), this);
        getServer().getPluginManager().registerEvents(new CropTrampleHandler(this), this);
        getServer().getPluginManager().registerEvents(new MobAttackHandler(this), this);
        getServer().getPluginManager().registerEvents(new TestingPlayers(this), this);

        // Register reload command
        PluginCommand reloadCmd = getCommand("elmlandsstaffreload");
        if (reloadCmd != null) {
            reloadCmd.setExecutor(new ReloadCommand(this));
        }

        // Start vanished action bar task
        startVanishedActionBarTask();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        String cmdName = cmd.getName().toLowerCase();

        switch (cmdName) {
            case "staffmode":
                return false; // Not implemented
            case "v":
            case "vanish":
                if (player.hasPermission("elmlands.staff.vanish")) {
                    toggleVanish(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            case "turn":
                if (player.hasPermission("elmlands.staff.turn")) {
                    if (args.length == 1) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null) {
                            turnPlayer(target);
                            player.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been turned around.");
                        } else {
                            player.sendMessage(ChatColor.RED + "Player not found.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /turn <player>");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            case "mutechat":
                if (player.hasPermission("elmlands.staff.mutechat")) {
                    chatMuted = !chatMuted;
                    String status = chatMuted ? "muted" : "unmuted";
                    Bukkit.broadcastMessage(ChatColor.RED + "Chat has been " + status + " by " + player.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            case "staffrtp":
                if (player.hasPermission("elmlands.staff.staffrtp")) {
                    if (player.hasMetadata("vanished")) {
                        if (!onlinePlayers.isEmpty()) {
                            staffTeleport(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "There are no players online.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You must be vanished to use this command!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            case "onlinestaff":
                if (player.hasPermission("elmlands.staff.onlinestaff")) {
                    player.sendMessage(ChatColor.GREEN + "Online staff members:");
                    for (Player onlinePlayer : onlinePlayers) {
                        if (!onlinePlayer.hasMetadata("vanished") && !onlinePlayer.getInventory().contains(Material.REDSTONE_TORCH)) {
                            player.sendMessage(ChatColor.GREEN + "- " + onlinePlayer.getName());
                        } else if (player.hasPermission("elmlands.staff.vanish.see")) {
                            player.sendMessage(ChatColor.GREEN + "- " + ChatColor.ITALIC + onlinePlayer.getName());
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            case "cpstest":
                if (player.hasPermission("elmlands.staff.cpstest")) {
                    if (args.length == 1) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null) {
                            startCpsTest(player, target);
                            player.sendMessage(ChatColor.GREEN + "Started CPS test for " + target.getName() + ".");
                        } else {
                            player.sendMessage(ChatColor.RED + "Player not found.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /cpstest <player>");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            case "freeze":
                if (player.hasPermission("elmlands.staff.freeze")) {
                    if (args.length == 1) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null) {
                            toggleFreeze(player, target);
                        } else {
                            player.sendMessage(ChatColor.RED + "Player not found.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /freeze <player>");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            default:
                return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        onlinePlayers.add(player);

        // Hide vanished players from the joining player
        for (Player vanishedPlayer : vanishedPlayers) {
            player.hidePlayer(this, vanishedPlayer);
        }

        // Restore vanished state or add to teleport queue
        if (player.hasMetadata("vanished") && player.getMetadata("vanished").get(0).asBoolean()) {
            handleVanishedPlayer(player);
        } else {
            teleportQueue.add(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        onlinePlayers.remove(player);
        vanishedPlayers.remove(player);
        frozenPlayers.remove(player);
        cpsTests.remove(player.getUniqueId());
        teleportQueue.remove(player);
    }

    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public List<Player> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public List<Player> getFrozenPlayers() {
        return frozenPlayers;
    }

    public Map<UUID, Integer> getCpsTests() {
        return cpsTests;
    }

    public Queue<Player> getTeleportQueue() {
        return teleportQueue;
    }

    public boolean isStaffModeEnabled() {
        return true; // Assume staff mode is enabled
    }

    public boolean isChatMuted() {
        return chatMuted;
    }

    public void toggleVanish(Player player) {
        if (vanishedPlayers.contains(player)) {
            vanishedPlayers.remove(player);
            player.setMetadata("vanished", new FixedMetadataValue(this, false));
            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(this, player));
            player.sendMessage(ChatColor.GREEN + "You are now visible.");
        } else {
            vanishedPlayers.add(player);
            player.setMetadata("vanished", new FixedMetadataValue(this, true));
            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(this, player));
            player.sendMessage(ChatColor.GREEN + "You are now vanished.");
        }
    }

    public void turnPlayer(Player target) {
        Location loc = target.getLocation();
        loc.setYaw(loc.getYaw() + 180.0F);
        target.teleport(loc);
        target.sendMessage(ChatColor.GREEN + "You have been turned around!");
    }

    public void toggleNightVision(Player player) {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(ChatColor.GREEN + "Night vision disabled.");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            player.sendMessage(ChatColor.GREEN + "Night vision enabled.");
        }
    }

    public void handleVanishedPlayer(Player player) {
        player.setMetadata("vanished", new FixedMetadataValue(this, true));
        vanishedPlayers.add(player);
        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(this, player));
        player.sendMessage(ChatColor.GREEN + "You are currently vanished.");
    }

    public void staffTeleport(Player player) {
        if (!player.hasPermission("elmlands.staff.staffrtp")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return;
        }
        if (teleportQueue.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no players to teleport to.");
            return;
        }
        Player target = teleportQueue.poll();
        player.teleport(target);
        player.sendMessage(ChatColor.GREEN + "You have been teleported to " + target.getName());
        teleportQueue.add(target);
    }

    public void startCpsTest(Player sender, Player target) {
        if (cpsTests.containsKey(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already undergoing a CPS test.");
            return;
        }
        cpsTests.put(target.getUniqueId(), 0);
        target.sendMessage(ChatColor.YELLOW + "A CPS test has started. Click as fast as you can for 5 seconds!");
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Integer clicks = cpsTests.remove(target.getUniqueId());
            if (clicks != null) {
                double cps = clicks / 5.0;
                sender.sendMessage(ChatColor.GREEN + target.getName() + " achieved " + clicks + " clicks (" + String.format("%.2f", cps) + " CPS).");
                target.sendMessage(ChatColor.GREEN + "CPS test completed: " + clicks + " clicks (" + String.format("%.2f", cps) + " CPS).");
            }
        }, 100L); // 5 seconds = 100 ticks
    }

    public void toggleFreeze(Player sender, Player target) {
        if (frozenPlayers.contains(target)) {
            frozenPlayers.remove(target);
            target.removePotionEffect(PotionEffectType.BLINDNESS);
            target.setMetadata("frozen", new FixedMetadataValue(this, false));
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unfrozen.");
            target.sendMessage(ChatColor.GREEN + "You are no longer frozen.");
        } else {
            frozenPlayers.add(target);
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false));
            target.setMetadata("frozen", new FixedMetadataValue(this, true));
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been frozen.");
            target.sendMessage(ChatColor.RED + "You have been frozen by a staff member!");
        }
    }

    private void startVanishedActionBarTask() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : vanishedPlayers) {
                if (player.isOnline()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY + "You are in vanish mode"));
                }
            }
        }, 0L, 20L);
    }
}