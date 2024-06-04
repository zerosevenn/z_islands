package me.zeroseven.island.utils;

import org.bukkit.entity.Player;

public class StringUtils {

    public static String formatMessage(String message){
        return message.replace("&", "§")
                .replace("[", "")
                .replace("]", "");
    }

    public static String formatMessage(String message, int value){
        return message.replace("&", "§")
                .replace("[", "")
                .replace("]", "")
                .replace("<value>", String.valueOf(value));
    }


    public static String formatMessage(Player player, String message){
        return formatMessage(message).replace("<player>", player.getName()
                .replace("[", "")
                .replace("]", ""));
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String firstLetter = input.substring(0, 1).toUpperCase();
        String restOfString = input.substring(1);

        return firstLetter + restOfString;
    }


}