package ru.morozovit.plugin.bedfight.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerMove implements Listener {
    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (event.getPlayer().getLocation().getY() <= -8) {
            event.getPlayer().setLastDamageCause(new EntityDamageEvent(event.getPlayer(),EntityDamageEvent.DamageCause.VOID,100));
            event.getPlayer().setHealth(0);
        }
    }
}