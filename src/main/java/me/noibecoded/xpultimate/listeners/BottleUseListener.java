package me.noibecoded.xpultimate.listeners;

import me.noibecoded.xpultimate.XpUltimate;
import me.noibecoded.xpultimate.data.XpDataManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BottleUseListener implements Listener {

    private static final Pattern XP_PATTERN = Pattern.compile("Contains: §e(\\d+) XP");
    private final XpUltimate plugin;

    public BottleUseListener(XpUltimate plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.EXPERIENCE_BOTTLE) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String displayName = meta.getDisplayName();
        if (!displayName.equals("§6XP Bottle") && !displayName.equals("§6XP Bottle (Filled)")) {
            return;
        }

        if (!meta.hasLore() || meta.getLore().isEmpty()) {
            return;
        }

        String lore = meta.getLore().get(0);
        Matcher matcher = XP_PATTERN.matcher(lore);

        if (matcher.find()) {
            int xpAmount = Integer.parseInt(matcher.group(1));

            player.giveExp(xpAmount);
            item.setAmount(item.getAmount() - 1);

            player.sendMessage("§aYou used XP Bottle! §e+" + xpAmount + " XP");
            event.setCancelled(true);
        }
    }
}
