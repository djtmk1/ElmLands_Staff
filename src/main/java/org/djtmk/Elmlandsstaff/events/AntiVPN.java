package org.djtmk.Elmlandsstaff.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

public class AntiVPN implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public AntiVPN(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = loadConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String ipAddress = event.getPlayer().getAddress().getAddress().getHostAddress();
        List<String> blacklistedISPs = config.getStringList("blacklisted_isps");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String isp = getISP(ipAddress);
                if (isp != null && blacklistedISPs.contains(isp)) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ipban " + event.getPlayer().getName() + " Proxies are not allowed on this server."));
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to check ISP for IP: " + ipAddress, e);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Unexpected error while checking ISP for IP: " + ipAddress, e);
            }
        });
    }

    private String getISP(String ipAddress) throws IOException {
        String apiResponse = sendGetRequest("http://ip-api.com/json/" + ipAddress);
        try {
            JSONObject jsonResponse = new JSONObject(apiResponse);
            return jsonResponse.has("isp") ? jsonResponse.getString("isp") : null;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to parse JSON response for IP: " + ipAddress, e);
            return null;
        }
    }

    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error during AntiVPN request to " + urlString, e);
            throw e;
        }
    }

    private FileConfiguration loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }
}