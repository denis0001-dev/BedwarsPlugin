package ru.morozovit.plugin.bedfight.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import ru.morozovit.plugin.bedfight.game.GameManager;
import ru.morozovit.plugin.bedfight.game.Teams;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static org.bukkit.ChatColor.*;
import static ru.morozovit.plugin.bedfight.Stats.getStatKey;
import static ru.morozovit.plugin.bedfight.Stats.setStatKey;
import static ru.morozovit.plugin.bedfight.game.BedDefense.blue_bed;
import static ru.morozovit.plugin.bedfight.game.BedDefense.red_bed;
import static ru.morozovit.plugin.bedfight.game.Teams.getTeam;

public class BlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.RED_BED) {
            event.getBlock().getLocation().getWorld().strikeLightningEffect(event.getBlock().getLocation());
            Set<String> players = GameManager.getRunningPlayers();
            String bedTeam;

            if (Arrays.asList(blue_bed).contains(event.getBlock().getLocation())) {
                bedTeam = Teams.BLUE;
            } else if (Arrays.asList(red_bed).contains(event.getBlock().getLocation())) {
                bedTeam = Teams.RED;
            } else {
                return;
            }

            for (String player : players) {
                if (Objects.equals(getTeam(player), bedTeam) && !getStatKey(player, "bed").equals("false")) {
                    setStatKey(player, "bed", "false");
                    try {
                        Bukkit.getPlayer(player).sendTitle(BOLD + "" + RED + "BED DESTROYED!", WHITE + "You will no longer respawn!", 3, 20, 3);
                        Bukkit.getPlayer(player).playSound(Bukkit.getPlayer(player).getLocation(), Sound.ENTITY_WITHER_DEATH, SoundCategory.MASTER, 1, 1);
                    } catch (NullPointerException _) {}
                }
            }
        }
    }
}