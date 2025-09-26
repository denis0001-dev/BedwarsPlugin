package ru.morozovit.plugin.bedfight.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ru.morozovit.plugin.bedfight.BedfightPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Bukkit.getWorlds;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.*;
import static ru.morozovit.plugin.bedfight.Stats.getStatKey;
import static ru.morozovit.plugin.bedfight.Stats.setStatKey;
import static ru.morozovit.plugin.bedfight.game.BedDefense.*;
import static ru.morozovit.plugin.bedfight.game.Teams.*;

public class GameManager {
    @SuppressWarnings("FieldMayBeFinal")
    private static Set<String> runningPlayers = new HashSet<>();

    private static final World overworld = getWorlds().get(0);

    public static final Location waiting_room = new Location(overworld,143.5,27,34.5);

    public static void ensureChunkLoaded(@NotNull Location location) {
        org.bukkit.Chunk chunk = location.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load(true);
        }
    }

    public static Set<String> getRunningPlayers() {
        updatePlayers();
        return runningPlayers;
    }
    private static void updatePlayers() {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

        for (Player player : players) {
            if (getStatKey(player,"inGame").equals("true")) {
                runningPlayers.add(player.getName());
            } else {
                runningPlayers.remove(player.getName());
            }
        }
    }

    public static void gameCountdown(Player target) {
        new BukkitRunnable() {
            int secondsRemaining = 5;

            @Override
            public void run() {
                if (secondsRemaining <= 0) {
                    cancel();
                    return;
                }
                target.sendTitle((secondsRemaining <= 3 ? RED : GOLD) + String.valueOf(secondsRemaining), "", 3, 20, 3);
                target.playNote(target.getLocation(), Instrument.STICKS,new Note(12));
                secondsRemaining--;
            }
        }.runTaskTimer(JavaPlugin.getPlugin(BedfightPlugin.class), 0L, 20L);
    }

    public static void putPlayerToPlace(@NotNull Player target) {
        Location destination = switch (getTeam(target)) {
            case Teams.BLUE -> BLUE_SPAWN;
            case Teams.RED -> RED_SPAWN;
            default -> waiting_room;
        };
        ensureChunkLoaded(destination);
        target.teleport(destination);
    }

    public static void applyKit(@NotNull Player target) {
        PlayerInventory inv = target.getInventory();

        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        ItemStack wool = new ItemStack(
                switch (getTeam(target)) {
                    case Teams.BLUE -> Material.BLUE_WOOL;
                    case Teams.RED -> Material.RED_WOOL;
                    default -> Material.GRAY_WOOL;
                }, 64
        );
        ItemStack pick = new ItemStack(Material.WOODEN_PICKAXE);
        ItemStack axe = new ItemStack(Material.WOODEN_AXE);
        ItemStack shears = new ItemStack(Material.SHEARS);

        inv.clear();
        inv.addItem(sword,wool,pick,axe,shears);
    }

    public static void setInitialProps(Player target) {
        setStatKey(target, "bed", "true");
        setStatKey(target,"spectator","false");
    }

    private static Clipboard cachedMapClipboard;
    public static synchronized void resetMap() throws IOException {
        if (cachedMapClipboard == null) {
            @SuppressWarnings("DataFlowIssue")
            File schem = new File(
                    Bukkit
                            .getServer()
                            .getPluginManager()
                            .getPlugin("BedwarsPlugin")
                            .getDataFolder()
                            .getAbsolutePath() + "/map-1.schem"
            );
            ClipboardFormat format = ClipboardFormats.findByFile(schem);
            assert format != null;
            try (FileInputStream fis = new FileInputStream(schem); ClipboardReader reader = format.getReader(fis)) {
                cachedMapClipboard = reader.read();
            }
        }
        Clipboard clipboard = cachedMapClipboard;

        try {
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(overworld);

            double x = 146;
            double y = 15;
            double z = 49;

            overworld.getChunkAt((int) x >> 4, (int) z >> 4).load(true);

            @SuppressWarnings("deprecation")
            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,-1);

            Operation operation = new ClipboardHolder(clipboard).createPaste(session).to(BlockVector3.at(x,y,z)).ignoreAirBlocks(false).build();

            Operations.complete(operation);

            //noinspection deprecation
            session.flushSession();
        } catch (WorldEditException e) {
            Bukkit.broadcastMessage(RED+"Couldn't reset the map.");
        }
    }

    public static void startGame() {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

        // Teams.allocateTeams();
        try {
            resetMap();
            generateBedDefense(overworld,blue_bed[0]);
            generateBedDefense(overworld,red_bed[0]);
        } catch (IOException e) {
            Bukkit.broadcastMessage(RED+"An error occurred while resetting");
        }

        for (Player player : players) {
            runningPlayers.add(player.getName());
            player.setGameMode(GameMode.SURVIVAL);
            gameCountdown(player);
            BukkitRunnable post = new BukkitRunnable() {
                @Override
                public void run() {
                    setInitialProps(player);
                    putPlayerToPlace(player);
                    applyKit(player);
                    setStatKey(player,"inGame","true");
                    player.sendMessage(GREEN+"Game started!");
                }
            };
            post.runTaskLater(JavaPlugin.getPlugin(BedfightPlugin.class),100);
        }
    }
}
