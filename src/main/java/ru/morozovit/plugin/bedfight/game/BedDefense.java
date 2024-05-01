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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.bukkit.Bukkit.getWorlds;

public class BedDefense {
    private static final World overworld = getWorlds().getFirst();

    public static final Location[] blue_bed = new Location[] {
            new Location(overworld,117.0,8.0,38.0),
            new Location(overworld,118.0,8.0,38.0)
    };
    public static final Location[] red_bed = new Location[] {
            new Location(overworld,162.0,8.0,30.0),
            new Location(overworld,163.0,8.0,30.0)
    };

    public static void generateBedDefense(World world, @NotNull Location coords) throws IOException {
        File schem = new File(Bukkit.getServer().getPluginManager().getPlugin("BedfightPlugin").getDataFolder().getAbsolutePath() + "/bed_defense.schem");
        ClipboardFormat format = ClipboardFormats.findByFile(schem);

        ClipboardReader reader = format.getReader(new FileInputStream(schem));
        Clipboard clipboard = reader.read();

        try {
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);

            @SuppressWarnings("deprecation")
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,-1);

            double x = coords.getX()-1;
            double y = coords.getY()+3;
            double z = coords.getZ();


            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(x,y,z)).ignoreAirBlocks(true).build();

            Operations.complete(operation);
            //noinspection deprecation
            editSession.flushSession();

        } catch (WorldEditException e) {
            e.printStackTrace();
        }


    }
}
