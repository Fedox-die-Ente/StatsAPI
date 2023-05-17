package me.fedox.statsapi;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class Channel
{
    StatsAPI plugin;
    String name;
    ArrayList<String> created;

    public Channel(final StatsAPI p, String name) {
        this.created = new ArrayList<String>();
        this.plugin = p;
        name = name.toUpperCase();
        this.name = name;
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        if (this.plugin.isDummy()) {
            Bukkit.getConsoleSender().sendMessage("[StatsAPI] " + ChatColor.RED + "Running in Dummymode. No changes will occure!");
        }
        else {
            try {
                final Statement s = this.plugin.c.createStatement();
                s.execute("CREATE TABLE IF NOT EXISTS " + name + " (UUID VARCHAR(36) UNIQUE)");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasKey(final String key) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        try {
            final Statement s = this.plugin.c.createStatement();
            final ResultSet keys = s.executeQuery("SHOW columns FROM " + this.name + ";");
            final ArrayList<String> keylist = new ArrayList<String>();
            while (keys.next()) {
                keylist.add(keys.getString("Field"));
            }
            for (final String str : keylist) {
                if (str.equalsIgnoreCase(key)) {
                    return true;
                }
            }
            return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StatsAPI] Created key " + key + "!");
            return false;
        }
    }

    private void createKey(final String key, final boolean str) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        if (!this.hasKey(key)) {
            try {
                final Statement s = this.plugin.c.createStatement();
                if (str) {
                    s.execute("ALTER TABLE `" + this.name + "` ADD COLUMN `" + key + "` TEXT");
                }
                else {
                    s.execute("ALTER TABLE `" + this.name + "` ADD COLUMN `" + key + "` INT NOT NULL DEFAULT 0");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("[StatsAPI] Created key " + key + "!");
            }
        }
    }

    public void setKey(final UUID uuid, final String key, final int to) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        if (!this.created.contains(key)) {
            this.createKey(key, false);
            this.created.add(key);
        }
        try {
            Statement s = this.plugin.c.createStatement();
            s.execute("INSERT INTO " + this.name + " (UUID) VALUES ('" + uuid + "') ON DUPLICATE KEY UPDATE UUID=UUID;");
            s = this.plugin.c.createStatement();
            s.execute("UPDATE " + this.name + " " + "SET " + key + "=" + to + " " + "WHERE UUID='" + uuid.toString() + "';");
        }
        catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't create Key!");
        }
    }

    public int getKey(final UUID uuid, final String key) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        if (this.hasKey(key)) {
            try {
                final Statement s = this.plugin.c.createStatement();
                final ResultSet result = s.executeQuery("SELECT " + key + " FROM " + this.name + " WHERE UUID='" + uuid + "';");
                if (result.next()) {
                    return result.getInt(key);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't get Key " + key + "!");
            }
        }
        return 0;
    }

    public void addToKey(final UUID uuid, final String key, final int incr) {
        final int old = this.getKey(uuid, key);
        this.setKey(uuid, key, old + incr);
    }

    public void setStringKey(final UUID uuid, final String key, final String str) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        if (!this.created.contains(key)) {
            this.createKey(key, true);
            this.created.add(key);
        }
        try {
            Statement s = this.plugin.c.createStatement();
            s.execute("INSERT INTO " + this.name + " (UUID) VALUES ('" + uuid + "') ON DUPLICATE KEY UPDATE UUID=UUID;");
            s = this.plugin.c.createStatement();
            s.execute("UPDATE " + this.name + " " + "SET " + key + "='" + str + "' " + "WHERE UUID='" + uuid.toString() + "';");
        }
        catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't create Key!");
        }
    }

    public AdvancedChannelQuery getAdvancedChannelQuery() {
        return new AdvancedChannelQuery(this.plugin, this);
    }

    public String getStringKey(final UUID uuid, final String key) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        if (this.hasKey(key)) {
            try {
                final Statement s = this.plugin.c.createStatement();
                final ResultSet result = s.executeQuery("SELECT " + key + " FROM " + this.name + " WHERE UUID='" + uuid + "';");
                result.next();
                return result.getString(key);
            }
            catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't get Key " + key + "!");
            }
        }
        return null;
    }

    public HashMap<String, String> getAllKeys(final UUID uuid) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (Exception ex) {}
        try {
            final HashMap<String, String> returnmap = new HashMap<String, String>();
            final Statement s = this.plugin.c.createStatement();
            final ResultSet result = s.executeQuery("SELECT * FROM " + this.name + " WHERE UUID='" + uuid + "';");
            final ResultSetMetaData rsmd = result.getMetaData();
            final int columnCount = rsmd.getColumnCount();
            result.next();
            for (int i = 1; i <= columnCount; ++i) {
                final String name = rsmd.getColumnName(i);
                returnmap.put(name, result.getString(i));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StatsAPI] Failed to get all keys of UUID " + uuid);
        }
        return null;
    }
}