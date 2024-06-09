package me.zeroseven.island.nms;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import me.zeroseven.island.IslandPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class IslandSchematic {
    private IslandPlugin plugin;

    private String schematicFileName;
    private Location location;
    private Player player;

    public IslandSchematic(String schematicFileName, Location location, Player player) {
        this.plugin = (IslandPlugin) Bukkit.getPluginManager().getPlugin("zIsland");
        this.schematicFileName = schematicFileName;
        this.location = location;
        this.player = player;
    }

    public void pasteAndRotate(int degrees){

        File schematicFile = new File(plugin.getDataFolder(), "schematics/" + schematicFileName);
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);

        if (clipboardFormat == null) {
            plugin.getLogger().warning("Formato de arquivo não suportado: " + schematicFileName);
            return;
        }

        try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
            Clipboard clipboard = clipboardReader.read();
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            holder.setTransform(new AffineTransform().rotateY(degrees));
            holder.createPaste(clipboard);

            BlockVector3 origin = clipboard.getOrigin();
            BlockVector3 pastePosition = BlockVector3.at(location.getX(), location.getY(), location.getZ());

            plugin.getLogger().info("Carregando schematic: " + schematicFileName);
            plugin.getLogger().info("Origem do schematic: " + origin);
            plugin.getLogger().info("Posição de colagem: " + pastePosition);

            clipboard.getRegion().forEach(blockVector3 -> {
                BaseBlock blockState = clipboard.getFullBlock(blockVector3);
                Material material = mapBlockTypeToMaterial(blockState.getBlockType());

                if (material != null && material != Material.AIR) {
                    BlockVector3 relativePosition = blockVector3.subtract(origin).add(pastePosition);
                    Location blockLocation = new Location(location.getWorld(), relativePosition.getX(), relativePosition.getY(), relativePosition.getZ());
                    sendBlockChange(player, blockLocation, material);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void paste(){

        File schematicFile = new File(plugin.getDataFolder(), "schematics/" + schematicFileName);
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);

        if (clipboardFormat == null) {
            plugin.getLogger().warning("Formato de arquivo não suportado: " + schematicFileName);
            return;
        }

        try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
            Clipboard clipboard = clipboardReader.read();
            BlockVector3 origin = clipboard.getOrigin();
            BlockVector3 pastePosition = BlockVector3.at(location.getX(), location.getY(), location.getZ());

            plugin.getLogger().info("Carregando schematic: " + schematicFileName);
            plugin.getLogger().info("Origem do schematic: " + origin);
            plugin.getLogger().info("Posição de colagem: " + pastePosition);

            clipboard.getRegion().forEach(blockVector3 -> {
                BaseBlock blockState = clipboard.getFullBlock(blockVector3);
                Material material = mapBlockTypeToMaterial(blockState.getBlockType());

                if (material != null && material != Material.AIR) {
                    BlockVector3 relativePosition = blockVector3.subtract(origin).add(pastePosition);
                    Location blockLocation = new Location(location.getWorld(), relativePosition.getX(), relativePosition.getY(), relativePosition.getZ());
                    sendBlockChange(player, blockLocation, material);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendBlockChange(Player player, Location location, Material material) {
        PacketContainer packet = plugin.getProtocolManager().createPacket(com.comphenix.protocol.PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new com.comphenix.protocol.wrappers.BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        packet.getBlockData().write(0, WrappedBlockData.createData(material));

        try {
            plugin.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Material mapBlockTypeToMaterial(BlockType blockType) {
        String blockTypeId = blockType.getId();
        String[] parts = blockTypeId.split(":");
        if (parts.length == 2) {
            String materialName = parts[1].toUpperCase();
            try {
                return Material.valueOf(materialName);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Material não encontrado: " + materialName);
            }
        }
        return null;
    }

}
