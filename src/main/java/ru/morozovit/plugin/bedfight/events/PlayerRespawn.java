package ru.morozovit.plugin.bedfight.events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ru.morozovit.plugin.bedfight.BedfightPlugin;
import ru.morozovit.plugin.bedfight.game.GameManager;
import ru.morozovit.plugin.bedfight.game.Teams;

import static org.bukkit.ChatColor.*;
import static ru.morozovit.plugin.bedfight.Stats.getStatKey;
import static ru.morozovit.plugin.bedfight.game.GameManager.applyKit;

public class PlayerRespawn implements Listener {
    @EventHandler
    public void onPlayerRespawn(@NotNull org.bukkit.event.player.PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        System.out.printf("%s died.\n", p.getName());
        Location respawnLocation = event.getRespawnLocation();
        p.teleport(respawnLocation);

        if (getStatKey(p,"bed").equals("false")) {
            p.getInventory().clear();
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(RED+"You were eliminated!");
            return;
        }

        p.setGameMode(GameMode.SPECTATOR);
        p.setAllowFlight(true);
        p.setFlying(true);

        Runnable task = () -> {
            for (int i=5;i>0;i--) {
                p.sendTitle(BOLD+""+RED+"YOU DIED!",YELLOW+"You will respawn in %s seconds!".formatted(RED+""+ i +YELLOW),3,20,3);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _) {
                }
            }
            p.sendTitle(BOLD+""+GREEN+"RESPAWNED!","",3,40,3);
            p.setFlying(false);
            p.setAllowFlight(false);
        };
        Thread t = new Thread(task);
        t.start();
        BukkitRunnable post = new BukkitRunnable() {
            @Override
            public void run() {
                p.setGameMode(GameMode.SURVIVAL);
                p.teleport(switch (getStatKey(event.getPlayer(),"team").toString()) {
                    case Teams.BLUE -> Teams.BLUE_SPAWN;
                    case Teams.RED -> Teams.RED_SPAWN;
                    default -> GameManager.waiting_room;
                });
                applyKit(p);
            }
        };
        post.runTaskLater(JavaPlugin.getPlugin(BedfightPlugin.class),100);
    }
}
