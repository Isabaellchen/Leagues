package com.bukkit.isabaellchen.leagues;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * Groupy for Bukkit
 *
 * @author Isabaellchen
 */
public class Leagues extends JavaPlugin {

    private final LeaguesPlayerListener playerListener;
    private final LeaguesBlockListener blockListener;
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    public static ExecutorService WORKER;
    public static DBOperations DB;

    public Leagues(PluginLoader pluginLoader, Server instance,
            PluginDescriptionFile desc, File folder, File plugin,
            ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        int poolsize = (16 / Config.WORKLOAD_FACTOR) + Config.MINIMUM_THREADS;

        String url = "jdbc:mysql://127.0.0.1:3306/leagues";
        String user = "leagues";
        String password = "UcRqTGzmjUhArmmn";
        String driver = "com.mysql.jdbc.Driver";

        WORKER = Executors.newFixedThreadPool(poolsize);
        DB = new DBOperations(url, user, password, driver, poolsize);

        playerListener = new LeaguesPlayerListener(this);
        blockListener = new LeaguesBlockListener(this);
    }

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

        // Register our events
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener , Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener , Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener , Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener , Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener , Priority.Normal, this);


        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}
