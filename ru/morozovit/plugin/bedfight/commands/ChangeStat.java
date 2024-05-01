package ru.morozovit.plugin.bedfight.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static ru.morozovit.plugin.bedfight.Stats.setStatKey;

public class ChangeStat {
    public static boolean run(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length!=3) {
            sender.sendMessage(RED+"Invalid number of arguments!");
            sender.sendMessage(RED+"Usage: "+cmd.getUsage());
            return false;
        }

        String target;
        if (args[0].equals(".")) {
            if (sender instanceof Player) {
                target = sender.getName();
            } else {
                sender.sendMessage(RED+"You must be a player to reference yourself!");
                return false;
            }
        } else {
            target = args[0];
        }

        setStatKey(target,args[1],args[2]);
        sender.sendMessage(GREEN+"Successfully set value of "+RED+args[1]+GREEN+" to "+RED+"\""+args[2]+"\""+GREEN+".");
        return true;

    }
}
