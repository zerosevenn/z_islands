package me.zeroseven.island.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtils {


    public static ItemStack createItemStack(Material material, String displayName, String description) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta isMeta = itemStack.getItemMeta();
        isMeta.setDisplayName(displayName);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(description);
        isMeta.setLore(lore);
        itemStack.setItemMeta(isMeta);
        return itemStack;
    }



    public static ItemStack getItemFromConfiguration(Player player, Character character, String configPath, FileConfiguration configuration) {
        if (!configuration.contains(configPath)) {
            return null;
        }

        String materialName = configuration.getString(configPath + ".material");
        Material material = Material.matchMaterial(materialName);

        if (material == null) {
            return null;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (configuration.contains(configPath + ".displayName")) {
            String displayName = StringUtils.formatMessage(player, StringUtils.formatMessage(configuration.getString(configPath + ".displayName"), character));
            ((ItemMeta) itemMeta).setDisplayName(displayName);
        }

        if (configuration.contains(configPath + ".lore")) {
            List<String> loreList = configuration.getStringList(configPath + ".lore");
            List<String> formattedLore = new ArrayList<>();

            for (String lore : loreList) {
                formattedLore.add(StringUtils.formatMessage(player, StringUtils.formatMessage(lore, character)));
            }

            itemMeta.setLore(formattedLore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public static ItemStack getItemFromConfiguration(String configPath, FileConfiguration configuration) {
        if (!configuration.contains(configPath)) {
            return null;
        }

        String materialName = configuration.getString(configPath + ".material");
        Material material = Material.matchMaterial(materialName);

        if (material == null) {
            return null;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (configuration.contains(configPath + ".displayName")) {
            String displayName = StringUtils.formatMessage(configuration.getString(configPath + ".displayName"));
            ((ItemMeta) itemMeta).setDisplayName(displayName);
        }

        if (configuration.contains(configPath + ".lore")) {
            List<String> loreList = configuration.getStringList(configPath + ".lore");
            List<String> formattedLore = new ArrayList<>();

            for (String lore : loreList) {
                formattedLore.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            itemMeta.setLore(formattedLore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }



}