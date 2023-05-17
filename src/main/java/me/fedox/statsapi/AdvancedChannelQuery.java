package me.fedox.statsapi;

import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AdvancedChannelQuery
{
    Channel c;
    StatsAPI plugin;

    public AdvancedChannelQuery(final StatsAPI api, final Channel c) {
        this.c = c;
        this.plugin = api;
    }

    public ArrayList<String> querryUsersWhere(final String key, final String equals) {
        try {
            if (this.plugin.c.isClosed()) {
                this.plugin.reconnect();
            }
        }
        catch (SQLException ex) {}
        final ArrayList<String> returnl = new ArrayList<String>();
        if (this.c.hasKey(key)) {
            try {
                final Statement s = this.plugin.c.createStatement();
                final ResultSet result = s.executeQuery("SELECT " + key + " FROM " + this.c.name + " WHERE " + key + "='" + equals + "';");
                while (result.next()) {
                    returnl.add(result.getString(key));
                }
                return returnl;
            }
            catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't get Key " + key + "!");
            }
        }
        return returnl;
    }
}