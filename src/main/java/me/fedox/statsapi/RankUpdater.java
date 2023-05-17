package me.fedox.statsapi;


import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class RankUpdater
{
    StatsAPI m;
    Channel c;
    ArrayList<UUID> ranks;

    public RankUpdater(final Channel c) {
        this.ranks = new ArrayList<UUID>();
        this.m = c.plugin;
        this.c = c;
    }

    public void updateRank(final String key) {
        this.m.getServer().getScheduler().scheduleAsyncDelayedTask((Plugin)this.m, (Runnable)new Runnable() {
            @Override
            public void run() {
                try {
                    Bukkit.getConsoleSender().sendMessage("[StatsAPI] Received a request to update the ranks in the channel [" + RankUpdater.this.c.name + "] related to key [" + key + "]");
                    RankUpdater.this.ranks = new ArrayList<UUID>();
                    final Statement s = RankUpdater.this.m.c.createStatement();
                    final ResultSet r = s.executeQuery("SELECT * FROM " + RankUpdater.this.c.name + " ORDER BY " + key + ";");
                    while (r.next()) {
                        RankUpdater.this.ranks.add(UUID.fromString(r.getString("UUID")));
                    }
                    Collections.reverse(RankUpdater.this.ranks);
                    Bukkit.getConsoleSender().sendMessage("[StatsAPI] Succesfully updated " + RankUpdater.this.ranks.size() + " ranks");
                }
                catch (Exception ex) {}
            }
        }, 0L);
    }

    public int getRank(final UUID u) {
        return this.ranks.indexOf(u) + 1;
    }

    public UUID getRank(final int u) {
        return this.ranks.get(u - 1);
    }
}