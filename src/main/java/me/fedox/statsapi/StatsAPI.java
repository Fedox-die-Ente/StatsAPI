package me.fedox.statsapi;

import com.huskehhh.mysql.mysql.MySQL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class StatsAPI extends JavaPlugin
{
    MySQL mysql;
    Connection c;
    boolean dummy;
    Channel namedb;
    HashMap<String, ArrayList<String>> hooks;
    HashMap<UUID, String> namecache;

    public StatsAPI() {
        this.mysql = null;
        this.c = null;
        this.dummy = false;
        this.hooks = new HashMap<String, ArrayList<String>>();
        this.namecache = new HashMap<UUID, String>();
    }

    public void onEnable() {
        this.getCommand("statsapi").setExecutor((CommandExecutor)new Commands(this));
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("mysql.host", (Object)"127.0.0.1");
        this.getConfig().addDefault("mysql.port", (Object)"3306");
        this.getConfig().addDefault("mysql.user", (Object)"root");
        this.getConfig().addDefault("mysql.database", (Object)"stats");
        this.getConfig().addDefault("mysql.password", (Object)"123");
        this.saveConfig();
        this.mysql = new MySQL(this.getConfig().getString("mysql.host"), this.getConfig().getString("mysql.port"), this.getConfig().getString("mysql.database"), this.getConfig().getString("mysql.user"), this.getConfig().getString("mysql.password"));
        Bukkit.getConsoleSender().sendMessage("[StatsAPI] Trying to connect to the Database ...");
        this.reconnect();
        Bukkit.getPluginManager().registerEvents((Listener)new EventListener(this), (Plugin)this);
        this.namedb = this.hookChannel((Plugin)this, "STATSAPI_NAMEDB");
    }

    public boolean isDummy() {
        return this.dummy;
    }

    public void reconnect() {
        try {
            this.c = this.mysql.openConnection();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.dummy = true;
            Bukkit.getConsoleSender().sendMessage("[StatsAPI] Error whilst connecting to the Database. Running in Dummymode!");
        }
    }

    public Channel hookChannel(final Plugin p, final String str) {
        if (p != this) {
            if (!this.hooks.containsKey(p.getName())) {
                this.hooks.put(p.getName(), new ArrayList<String>());
            }
            if (!this.hooks.get(p.getName()).contains(str)) {
                this.hooks.get(p.getName()).add(str);
            }
        }
        final Channel ch = new Channel(this, str);
        return ch;
    }

    public String getNameFromUUID(final UUID s) {
        if (!this.namecache.containsKey(s)) {
            this.namecache.put(s, this.namedb.getStringKey(s, "NAME"));
        }
        return this.namecache.get(s);
    }

    public String getUUIDFromName(final String s) {
        final AdvancedChannelQuery acq = this.namedb.getAdvancedChannelQuery();
        return acq.querryUsersWhere("NAME", s).get(0);
    }
}
