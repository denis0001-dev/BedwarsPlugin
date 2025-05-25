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

    private static final World overworld = getWorlds().getFirst();

    public static final Location waiting_room = new Location(overworld,143.5,27,34.5);

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
        Runnable task = () -> {
            for (int i = 5; i > 0; i--) {
                target.sendTitle((i <= 3 ? RED : GOLD) + String.valueOf(i), "", 3, 20, 3);
                target.playNote(target.getLocation(), Instrument.STICKS,new Note(12));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _) {
                }
            }
        };
        Thread t = new Thread(task);
        t.start();
    }

    public static void putPlayerToPlace(@NotNull Player target) {
        target.teleport(
                switch (getTeam(target)) {
                    case Teams.BLUE -> BLUE_SPAWN;
                    case Teams.RED -> RED_SPAWN;
                    default -> waiting_room;
                }
        );
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

    public static void resetMap() throws IOException {
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
        ClipboardReader reader = format.getReader(new FileInputStream(schem));
        Clipboard clipboard = reader.read();

        try {
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(overworld);

            @SuppressWarnings("deprecation")
            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,-1);

            double x = 146;
            double y = 15;
            double z = 49;

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
        for (Player player : players) {
            runningPlayers.add(player.getName());

            try {
                resetMap();
                generateBedDefense(overworld,blue_bed[0]);
                generateBedDefense(overworld,red_bed[0]);
            } catch (IOException e) {
                Bukkit.broadcastMessage(RED+"An error occurred wlilist resetting");
            }
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
