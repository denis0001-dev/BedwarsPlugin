package ru.morozovit.plugin.bedfight.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import ru.morozovit.plugin.bedfight.game.GameManager;

import java.util.List;

import static org.bukkit.ChatColor.*;
import static ru.morozovit.plugin.bedfight.Stats.getStatKey;
import static ru.morozovit.plugin.bedfight.Stats.setStatKey;
import static ru.morozovit.plugin.bedfight.game.GameManager.waiting_room;
import static ru.morozovit.plugin.bedfight.game.Teams.setRandomTeam;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        event.setJoinMessage("");
        setStatKey(event.getPlayer(),"inGame","false");
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        int playerCount = players.length;

        for (Player player : players) {
            if (!getStatKey(player,"inGame").equals("true")) {
                player.sendMessage(GREEN+event.getPlayer().getName()+YELLOW+" joined! "+GRAY+"("+playerCount+"/"+AQUA+"2"+GRAY+")");
            } else {
                player.sendMessage(GREEN+event.getPlayer().getName()+AQUA+" joined the server!");
            }
        }
        event.getPlayer().teleport(waiting_room);

        setRandomTeam(event.getPlayer());
        List<Player> notRunningPlayers = new java.util.ArrayList<>(Bukkit.getOnlinePlayers().stream().toList());
        for (Player player : players) {
            if (getStatKey(player,"inGame").equals("true")) {
                try {
                    notRunningPlayers.remove(player);
                } catch (UnsupportedOperationException _) {}
            }
        }

        if (notRunningPlayers.toArray().length == 2) {
            GameManager.startGame();
        }
    }
}
