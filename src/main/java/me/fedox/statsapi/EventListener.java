package me.fedox.statsapi;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class EventListener implements Listener
{
    StatsAPI api;
    Channel namedb;

    public EventListener(final StatsAPI api) {
        this.namedb = api.hookChannel((Plugin)api, "STATSAPI_NAMEDB");
        this.api = api;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        Bukkit.getConsoleSender().sendMessage("[StatsAPI] Updated " + e.getPlayer().getName() + " in the Database!");
        this.namedb.setStringKey(e.getPlayer().getUniqueId(), "NAME", e.getPlayer().getName());
        this.api.namecache.put(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }
}