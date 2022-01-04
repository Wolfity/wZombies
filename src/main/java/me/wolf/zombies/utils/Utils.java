package me.wolf.zombies.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Utils {

    private Utils() {
    }

    public static String colorize(final String input) {
        return input == null ? "Null value" : ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String[] colorize(String... messages) {
        final String[] colorized = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            colorized[i] = ChatColor.translateAlternateColorCodes('&', messages[i]);
        }
        return colorized;
    }

    public static List<String> colorize(final List<String> list) {
        return list.stream().map(Utils::colorize).collect(Collectors.toList());
    }

    public static ItemStack createItem(final Material material, final String name, final int amount) {

        ItemStack is = new ItemStack(material, amount);
        ItemMeta meta = is.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Utils.colorize(name));

        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItem(final Material material, final String name, final String lore) {

        final ItemStack is = new ItemStack(material);
        final ItemMeta meta = is.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Utils.colorize(name));
        meta.setLore(Utils.colorize(Collections.singletonList(lore)));

        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItem(final Material icon,final String name) {
        final ItemStack is = new ItemStack(icon);
        final ItemMeta itemMeta = is.getItemMeta();

        itemMeta.setDisplayName(colorize(name));
        is.setItemMeta(itemMeta);

        return is;
    }
}
