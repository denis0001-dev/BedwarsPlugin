package ru.morozovit.plugin.bedfight;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.morozovit.plugin.bedfight.BedfightPlugin.config;

public class Stats {
    public static Object getStatKey(@NotNull Player player, String key) {
        return getStatKey(player.getName(),key);
    }
    public static Object getStatKey(@NotNull String playerName, String key) {
        try {
            return Objects.requireNonNullElseGet(
                    config.getPlayers().get(playerName).get(key),
                    Object::new
            );
        } catch (NullPointerException e) {
            return new Object();
        }
    }


    public static void setStatKey(@NotNull Player player, String key, Object value) {
        setStatKey(player.getName(),key,value);
    }

    public static void setStatKey(String playerName, String key, Object value) {
        Map<String, Map<String, Object>> map = config.getPlayers();
        Map<String,Object> playerData = map.get(playerName);
        if (playerData==null) {
            playerData = new HashMap<>();
        }
        playerData.put(key,value);
        map.put(playerName,playerData);
        config.setPlayers(map);
    }
}
