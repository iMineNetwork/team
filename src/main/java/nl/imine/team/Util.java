package nl.imine.team;

import org.bukkit.ChatColor;

/**
 * @author Dennis
 */
public class Util {

    public static String replaceColorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
