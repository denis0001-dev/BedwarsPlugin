package ru.morozovit.plugin.bedfight;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.mikigal.config.ConfigAPI;
import pl.mikigal.config.style.CommentStyle;
import pl.mikigal.config.style.NameStyle;
import ru.morozovit.plugin.bedfight.commands.ChangeStat;
import ru.morozovit.plugin.bedfight.commands.Requeue;
import ru.morozovit.plugin.bedfight.events.*;

import java.util.logging.Logger;

public final class BedfightPlugin extends JavaPlugin implements Listener {
    public static BedfightConfig config;

    public final PlayerDeath playerDeathListener = new PlayerDeath();
    public final PlayerRespawn playerRespawnListener = new PlayerRespawn();
    public final PlayerMove playerMoveListener = new PlayerMove();
    public final BlockBreak blockBreakListener = new BlockBreak();
    public final PlayerJoin playerJoinListener = new PlayerJoin();

    public final Logger logger = getLogger();

    @Override
    public void onEnable() {
        config = ConfigAPI.init(
            BedfightConfig.class,
            NameStyle.UNDERSCORE,
            CommentStyle.INLINE,
            true,
            this
        );
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(playerDeathListener, this);
        pm.registerEvents(playerRespawnListener, this);
        pm.registerEvents(playerMoveListener, this);
        pm.registerEvents(blockBreakListener, this);
        pm.registerEvents(playerJoinListener, this);

        logger.info("Plugin loaded");
    }

    @Override
    public void onDisable() {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        switch (cmd.getName()) {
            case "stat":
                return ChangeStat.run(sender, cmd, label, args);
            case "requeue":
                return Requeue.run(sender);

            default:
                sender.sendMessage(ChatColor.RED+"Unknown command.");
        }
        return false;
    }
}