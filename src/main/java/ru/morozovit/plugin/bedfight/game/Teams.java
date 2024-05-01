package ru.morozovit.plugin.bedfight.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.morozovit.plugin.bedfight.Stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Bukkit.getWorlds;
import static ru.morozovit.plugin.bedfight.Stats.getStatKey;
import static ru.morozovit.plugin.bedfight.Stats.setStatKey;
import static ru.morozovit.util.Random.randomNumber;

public class Teams {
    private static final World overworld = getWorlds().getFirst();

    public static final String BLUE = "blue";
    public static final String RED = "red";

    public static final Location BLUE_SPAWN = new Location(overworld, 114.5,8.0,34.5);
    public static final Location RED_SPAWN = new Location(overworld, 168.5,8.0,34.5);

    public static void setRandomTeam(Player player) {
        switch (randomNumber(1,2)) {
            case 1:
                Stats.setStatKey(player,"team","red");
                break;
            case 2:
                Stats.setStatKey(player,"team","blue");
                break;
            default:
                throw new RuntimeException("RNG went wrong");
        }
    }

    public static String getTeam(@NotNull Player player) {
        return getTeam(player.getName());
    }

    public static @Nullable String getTeam(String playerName) {
        return switch (getStatKey(playerName, "team").toString()) {
            case BLUE -> BLUE;
            case RED -> RED;
            case null -> null;
            default -> throw new RuntimeException("Invalid team");
        };
    }


    public static void allocateTeams() {
        List<? extends Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers().stream().toList());
        Collections.shuffle(players);

        Player[] blue_team = new Player[4];
        Player[] red_team = new Player[4];

        Player[] playersArray = players.toArray(new Player[]{});

        //              (src         | src-offset     | dest    | offset  | count                )
        System.arraycopy(playersArray,0        ,blue_team,0,players.size()/2);
        System.arraycopy(playersArray,blue_team.length,red_team ,0,players.size()/2);

        for (Player player : blue_team) {
            setStatKey(player,"team",Teams.BLUE);
        }
        for (Player player : red_team) {
            setStatKey(player,"team",Teams.RED);
        }
    }
}
