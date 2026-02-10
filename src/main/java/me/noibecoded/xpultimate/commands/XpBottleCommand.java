package me.noibecoded.xpultimate.commands;

import me.noibecoded.xpultimate.XpUltimate;
import me.noibecoded.xpultimate.data.XpDataManager;
import me.noibecoded.xpultimate.gui.XpBottleGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XpBottleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
            case "c":
                return handleCreate(player, args);
            case "gui":
            case "g":
                return handleGui(player);
            case "help":
            case "h":
            case "?":
                showHelp(player);
                return true;
            default:
                player.sendMessage("§cUnknown command. Use /xpbottles for help.");
                return true;
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("§a--- XpBottles ---");
        player.sendMessage("§e/xpbottles create <amount> §7- Create XP bottle from your XP");
        player.sendMessage("§e/xpbottles gui §7- Open XP bottle crafting GUI");
        player.sendMessage("§7");
        player.sendMessage("§7Tip: Use the custom crafting table for advanced bottle creation!");
    }

    private boolean handleCreate(Player player, String[] args) {
        if (!player.hasPermission("xpbottles.create")) {
            player.sendMessage("§cYou don't have permission to create XP bottles.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /xpbottles create <amount>");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage("§cAmount must be greater than 0.");
                return true;
            }

            int maxXp = XpUltimate.getInstance().getConfigManager().getMaxXpPerBottle();
            if (amount > maxXp) {
                player.sendMessage("§cMaximum XP per bottle is " + maxXp + ".");
                return true;
            }

            int playerXp = player.getTotalExperience();

            if (playerXp < amount) {
                player.sendMessage("§cYou need at least " + amount + " XP. You have: " + playerXp);
                return true;
            }

            int remainingXp = playerXp - amount;

            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0);

            if (remainingXp > 0) {
                player.giveExp(remainingXp);
            }

            XpDataManager.giveXpBottle(player, amount);

            player.sendMessage("§aCreated XP bottle with " + amount + " XP!");
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount.");
        }

        return true;
    }

    private boolean handleGui(Player player) {
        if (!player.hasPermission("xpbottles.gui")) {
            player.sendMessage("§cYou don't have permission to open the GUI.");
            return true;
        }

        XpBottleGui.openGui(player);
        return true;
    }
}
