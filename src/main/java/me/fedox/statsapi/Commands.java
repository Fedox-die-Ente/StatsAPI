package me.fedox.statsapi;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class Commands implements CommandExecutor {

    StatsAPI api;

    public Commands(StatsAPI api) {
        this.api = api;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!sender.hasPermission("statsapi.info")) {
            sender.sendMessage("§cKeine Rechte!");
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "[STATSAPI]" + ChatColor.RESET + " Coded by Fedox [ Version " + this.api.getDescription().getVersion() + " ]");
        for (String s : this.api.hooks.keySet())
            sendHookerMessage(sender, s);
        return true;
    }

    public void sendHookerMessage(CommandSender sender, String p) {
        ArrayList<String> hooks = this.api.hooks.get(p);
        String result = "";
        for (String s : hooks) {
            if (hooks.indexOf(s) == hooks.size() - 1) {
                result = String.valueOf(result) + s;
                break;
            }
            result = String.valueOf(result) + s + ", ";
        }
        sender.sendMessage(ChatColor.GREEN + p + ChatColor.RESET + ": " + result);
    }

}
