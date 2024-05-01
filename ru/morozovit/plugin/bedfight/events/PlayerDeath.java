package ru.morozovit.plugin.bedfight.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ru.morozovit.plugin.bedfight.BedfightPlugin;
import ru.morozovit.plugin.bedfight.game.GameManager;
import ru.morozovit.plugin.bedfight.game.Teams;

import static org.bukkit.ChatColor.*;
import static ru.morozovit.plugin.bedfight.Stats.getStatKey;
import static ru.morozovit.plugin.bedfight.Stats.setStatKey;

public class PlayerDeath implements Listener {
    @EventHandler
    public void onPlayerDeath(@NotNull org.bukkit.event.entity.PlayerDeathEvent event) {
        event.setKeepLevel(false);
        event.setKeepInventory(false);
        event.getEntity().getInventory().clear();
        String deathMsg;
        ChatColor eventPlayerTeamColor = switch (getStatKey(event.getEntity().getPlayer(),"team").toString()) {
            case Teams.BLUE -> BLUE;
            case Teams.RED -> RED;
            default -> GRAY;
        };
        ChatColor killerTeamColor;

        EntityDamageEvent.DamageCause cause;
        try {
            cause = event.getEntity().getLastDamageCause().getCause();
        } catch (NullPointerException e) {
            cause = null;
        }


        if (event.getEntity().getKiller()==null) {
            if (cause== EntityDamageEvent.DamageCause.VOID) {
                deathMsg = eventPlayerTeamColor+event.getEntity().getName()+GRAY+" fell into the void.";
            } else if (cause==EntityDamageEvent.DamageCause.BLOCK_EXPLOSION||cause==EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                deathMsg = eventPlayerTeamColor+event.getEntity().getName()+GRAY+" exploded.";
            } else {
                deathMsg = eventPlayerTeamColor+event.getEntity().getName()+GRAY+" died.";
            }
        } else {
            killerTeamColor = switch (getStatKey(event.getEntity().getKiller().getPlayer(),"team").toString()) {
                case Teams.BLUE -> BLUE;
                case Teams.RED -> RED;
                default -> GRAY;
            };
            if (cause== EntityDamageEvent.DamageCause.VOID) {
                deathMsg = eventPlayerTeamColor+event.getEntity().getName()+GRAY+" was hit into the void by "+killerTeamColor+event.getEntity().getKiller().getName()+GRAY+".";
            } else {
                deathMsg = eventPlayerTeamColor+event.getEntity().getName()+GRAY+" was killed by "+killerTeamColor+event.getEntity().getKiller().getName()+GRAY+".";
            }
        }

        if (getStatKey(event.getEntity(),"bed").equals("false")) {
            deathMsg = deathMsg+" "+BOLD+AQUA+"FINAL KILL!";
            setStatKey(event.getEntity(),"spectator","true");
            setStatKey(event.getEntity(), "inGame","false");

            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                killer.sendTitle(BOLD+""+GOLD+"VICTORY!","",3,20,3);
                BukkitRunnable post = new BukkitRunnable() {
                    @Override
                    public void run() {
                        setStatKey(killer,"inGame","false");
                        killer.getInventory().clear();
                        killer.teleport(GameManager.waiting_room);
                    }
                };
                post.runTaskLater(JavaPlugin.getPlugin(BedfightPlugin.class),40);
            }

        }
        event.setDeathMessage(deathMsg);
    }
}