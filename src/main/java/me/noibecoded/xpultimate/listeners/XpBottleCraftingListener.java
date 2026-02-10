package me.noibecoded.xpultimate.listeners;

import me.noibecoded.xpultimate.XpUltimate;
import me.noibecoded.xpultimate.data.XpDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.Lectern;

import java.util.ArrayList;
import java.util.List;

public class XpBottleCraftingListener implements Listener {

    private final XpUltimate plugin;

    public XpBottleCraftingListener(XpUltimate plugin) {
        this.plugin = plugin;
    }

    public static void registerRecipes(XpUltimate plugin) {
        registerCraftingTableItem(plugin);
    }

    private static void registerCraftingTableItem(XpUltimate plugin) {
        ItemStack craftingTable = createCraftingTableItem();

        NamespacedKey key = new NamespacedKey(plugin, "xp_bottle_crafting_table");
        ShapedRecipe recipe = new ShapedRecipe(key, craftingTable);

        recipe.shape(
            "ABC",
            "D E",
            "FGH"
        );

        recipe.setIngredient('A', Material.GLASS);
        recipe.setIngredient('B', Material.GLASS);
        recipe.setIngredient('C', Material.GLASS);
        recipe.setIngredient('D', Material.CRYING_OBSIDIAN);
        recipe.setIngredient('E', Material.LECTERN);
        recipe.setIngredient('F', Material.CRYING_OBSIDIAN);
        recipe.setIngredient('G', Material.CRYING_OBSIDIAN);
        recipe.setIngredient('H', Material.CRYING_OBSIDIAN);

        Bukkit.addRecipe(recipe);
    }

    private static ItemStack createCraftingTableItem() {
        ItemStack table = new ItemStack(Material.CRAFTING_TABLE, 1);

        ItemMeta meta = table.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6XP Bottle Crafting Table");
            List<String> lore = new ArrayList<>();
            lore.add("§7Special crafting table for XP bottles");
            lore.add("§7Place empty bottles on top and add XP");
            meta.setLore(lore);
            table.setItemMeta(meta);
        }

        return table;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getType() != InventoryType.WORKBENCH) {
            return;
        }

        if (event.getSlotType() != org.bukkit.event.inventory.InventorySlotType.RESULT) {
            return;
        }

        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType() != Material.EXPERIENCE_BOTTLE) {
            return;
        }

        ItemMeta cursorMeta = cursor.getItemMeta();
        if (cursorMeta != null && cursorMeta.hasDisplayName() &&
            cursorMeta.getDisplayName().equals("§7Empty XP Bottle")) {
            event.setCancelled(true);
            openXpCraftingInterface(player);
        }
    }

    private void openXpCraftingInterface(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, "§6XP Bottle Crafting");

        ItemStack emptySlot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta emptyMeta = emptySlot.getItemMeta();
        if (emptyMeta != null) {
            emptyMeta.setDisplayName(" ");
            emptySlot.setItemMeta(emptyMeta);
        }

        for (int i = 0; i < 27; i++) {
            if (i != 4 && i != 13 && i != 22) {
                gui.setItem(i, emptySlot);
            }
        }

        ItemStack bottlePlaceholder = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        ItemMeta bottleMeta = bottlePlaceholder.getItemMeta();
        if (bottleMeta != null) {
            bottleMeta.setDisplayName("§7Empty Bottle");
            List<String> lore = new ArrayList<>();
            lore.add("§8Place your empty XP bottle here");
            bottleMeta.setLore(lore);
            bottlePlaceholder.setItemMeta(bottleMeta);
        }
        gui.setItem(4, bottlePlaceholder);

        ItemStack xpSource = XpDataManager.createEmptyBottle();
        ItemMeta sourceMeta = xpSource.getItemMeta();
        if (sourceMeta != null) {
            sourceMeta.setDisplayName("§7XP Source Bottle");
            List<String> lore = new ArrayList<>();
            lore.add("§8Place a bottle containing XP here");
            lore.add("§8XP will be transferred to new bottles");
            sourceMeta.setLore(lore);
            xpSource.setItemMeta(sourceMeta);
        }
        gui.setItem(13, xpSource);

        ItemStack resultPlaceholder = new ItemStack(Material.GLASS, 1);
        ItemMeta resultMeta = resultPlaceholder.getItemMeta();
        if (resultMeta != null) {
            resultMeta.setDisplayName("§7Result");
            List<String> lore = new ArrayList<>();
            lore.add("§8Filled bottles will appear here");
            resultMeta.setLore(lore);
            resultPlaceholder.setItemMeta(resultMeta);
        }
        gui.setItem(22, resultPlaceholder);

        player.openInventory(gui);
    }
}
