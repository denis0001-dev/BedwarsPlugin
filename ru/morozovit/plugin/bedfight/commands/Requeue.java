package ru.morozovit.plugin.bedfight.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.morozovit.plugin.bedfight.game.GameManager;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static ru.morozovit.plugin.bedfight.Stats.getStatKey;
import static ru.morozovit.plugin.bedfight.Stats.setStatKey;

public class Requeue {
    public static boolean run(CommandSender sender) {
        if (sender instanceof Player player) {
            setStatKey(player,"inGame","false");
            setStatKey(player,"spectator","false");
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(GameManager.waiting_room);
            @SuppressWarnings("MismatchedReadAndWriteOfArray")
            String[] runningPlayers = GameManager.getRunningPlayers().toArray(new String[]{});
            sender.sendMessage(GREEN+"Requeuing...");

            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if (getStatKey(player1,"spectator").equals("true")) {
                    return true;
                }
            }

            if (runningPlayers.length==0 && Bukkit.getOnlinePlayers().toArray().length==2) {
                GameManager.startGame();
            }


            return true;
        } else {
            sender.sendMessage(RED+"You must be a player to execute this command!");
            return false;
        }
    }
}
