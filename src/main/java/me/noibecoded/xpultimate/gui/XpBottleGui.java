package me.noibecoded.xpultimate.gui;

import me.noibecoded.xpultimate.XpUltimate;
import me.noibecoded.xpultimate.data.XpDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class XpBottleGui implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;
    private static final int SLOT_BOTTLE = 4;
    private static final int SLOT_XP_SOURCE = 13;
    private static final int SLOT_RESULT = 22;

    public XpBottleGui(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, "§6XP Bottle Crafting");
    }

    public static void openGui(Player player) {
        XpBottleGui gui = new XpBottleGui(player);
        gui.setupGui();
        player.openInventory(gui.getInventory());
    }

    private void setupGui() {
        ItemStack bottleSlot = createGuiItem(Material.EXPERIENCE_BOTTLE, "§7Empty Bottle Slot",
            "§8Place an empty XP bottle here", "§8to start crafting");

        ItemStack xpSourceSlot = createGuiItem(Material.BOOKSHELF, "§7XP Source",
            "§8Place a bottle with XP here", "§8to use as source");

        ItemStack resultSlot = createGuiItem(Material.GLASS, "§7Result",
            "§8Created bottles will appear here");

        ItemStack infoItem = createGuiItem(Material.PAPER, "§6Instructions",
            "§71. Place empty bottle in left slot",
            "§72. Place XP source bottle in center",
            "§73. Click result slot to collect",
            "",
            "§7Note: The XP from source bottle",
            "§7will be transferred to new bottles");

        ItemStack closeItem = createGuiItem(Material.BARRIER, "§cClose",
            "§8Click to close");

        ItemStack xpInfo = createGuiItem(Material.EXP_BOTTLE, "§6Your XP: §e" + player.getTotalExperience(),
            "§7Level: §e" + player.getLevel());

        inventory.setItem(SLOT_BOTTLE, bottleSlot);
        inventory.setItem(SLOT_XP_SOURCE, xpSourceSlot);
        inventory.setItem(SLOT_RESULT, resultSlot);
        inventory.setItem(26, closeItem);
        inventory.setItem(8, xpInfo);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add(line);
            }
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static Inventory getCraftingResult(Player player, ItemStack emptyBottle, ItemStack xpSource) {
        Inventory result = Bukkit.createInventory(null, 9, "§6Crafting Result");

        if (emptyBottle == null || emptyBottle.getType() != Material.EXPERIENCE_BOTTLE) {
            return result;
        }

        if (xpSource == null) {
            return result;
        }

        int xpAmount = XpDataManager.extractXpFromBottle(xpSource);
        if (xpAmount <= 0) {
            return result;
        }

        ItemStack filledBottle = XpDataManager.createXpBottle(xpAmount);
        result.setItem(4, filledBottle);

        return result;
    }

    public static void processCrafting(Player player, ItemStack emptyBottle, ItemStack xpSource) {
        if (emptyBottle == null || xpSource == null) {
            return;
        }

        int xpAmount = XpDataManager.extractXpFromBottle(xpSource);
        if (xpAmount <= 0) {
            return;
        }

        int playerXp = player.getTotalExperience();
        int maxXp = XpUltimate.getInstance().getConfigManager().getMaxXpPerBottle();
        int xpToStore = Math.min(xpAmount, maxXp);

        if (playerXp < xpToStore) {
            player.sendMessage("§cYou need at least " + xpToStore + " XP to create a bottle.");
            return;
        }

        int remainingXp = playerXp - xpToStore;

        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);

        if (remainingXp > 0) {
            player.giveExp(remainingXp);
        }

        ItemStack newBottle = XpDataManager.createXpBottle(xpToStore);

        if (xpSource.getAmount() > 1) {
            xpSource.setAmount(xpSource.getAmount() - 1);
        } else {
            xpSource.setType(Material.AIR);
        }

        if (emptyBottle.getAmount() > 1) {
            emptyBottle.setAmount(emptyBottle.getAmount() - 1);
        } else {
            emptyBottle.setType(Material.AIR);
        }

        player.getInventory().addItem(newBottle);

        player.sendMessage("§aCreated XP bottle with " + xpToStore + " XP!");
    }
}
