package me.noibecoded.xpultimate.commands;

import me.noibecoded.xpultimate.XpUltimate;
import me.noibecoded.xpultimate.data.XpDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XpBankCommand implements CommandExecutor {

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
            case "deposit":
            case "d":
                return handleDeposit(player, args);
            case "withdraw":
            case "w":
                return handleWithdraw(player, args);
            case "transfer":
            case "t":
            case "transferir":
                return handleTransfer(player, args);
            case "balance":
            case "b":
            case "saldo":
                return handleBalance(player);
            case "interest":
            case "i":
            case "interes":
                return handleInterest(player);
            case "reset":
            case "r":
                return handleReset(player, args);
            case "help":
            case "h":
            case "?":
                showHelp(player);
                return true;
            default:
                player.sendMessage("§cUnknown command. Use /xpbank for help.");
                return true;
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("§a--- XpBank ---");
        player.sendMessage("§e/xpbank deposit <amount> §7- Deposit XP to bank");
        player.sendMessage("§e/xpbank withdraw <amount> §7- Withdraw XP from bank");
        player.sendMessage("§e/xpbank transfer <player> <amount> §7- Transfer XP to player");
        player.sendMessage("§e/xpbank balance §7- Check bank balance");
        player.sendMessage("§e/xpbank interest §7- View interest rates");
        player.sendMessage("§e/xpbank reset [player] §7- Reset XP (admin)");
        player.sendMessage("§7");
        player.sendMessage("§7Interest rates:");
        player.sendMessage("§7  Online: " + XpUltimate.getInstance().getConfigManager().getOnlineInterest() + "%");
        player.sendMessage("§7  Offline: " + XpUltimate.getInstance().getConfigManager().getOfflineInterest() + "%");
    }

    private boolean handleDeposit(Player player, String[] args) {
        if (!player.hasPermission("xpbank.deposit")) {
            player.sendMessage("§cYou don't have permission to deposit XP.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /xpbank deposit <amount>");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage("§cAmount must be greater than 0.");
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

            XpDataManager.addXp(player, amount);

            int newBalance = XpDataManager.getTotalXp(player);
            player.sendMessage("§aDeposited " + amount + " XP to bank!");
            player.sendMessage("§7Bank Balance: §e" + newBalance + " XP");
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount.");
        }

        return true;
    }

    private boolean handleWithdraw(Player player, String[] args) {
        if (!player.hasPermission("xpbank.withdraw")) {
            player.sendMessage("§cYou don't have permission to withdraw XP.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /xpbank withdraw <amount>");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage("§cAmount must be greater than 0.");
                return true;
            }

            int bankXp = XpDataManager.getTotalXp(player);

            if (bankXp < amount) {
                player.sendMessage("§cYou only have " + bankXp + " XP in bank.");
                return true;
            }

            XpDataManager.removeXp(player, amount);
            player.giveExp(amount);

            int newBalance = XpDataManager.getTotalXp(player);
            player.sendMessage("§aWithdrew " + amount + " XP from bank!");
            player.sendMessage("§7Bank Balance: §e" + newBalance + " XP");
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount.");
        }

        return true;
    }

    private boolean handleTransfer(Player player, String[] args) {
        if (!player.hasPermission("xpbank.transfer")) {
            player.sendMessage("§cYou don't have permission to transfer XP.");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage("§cUsage: /xpbank transfer <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cYou can't transfer XP to yourself.");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                player.sendMessage("§cAmount must be greater than 0.");
                return true;
            }

            int bankXp = XpDataManager.getTotalXp(player);

            if (bankXp < amount) {
                player.sendMessage("§cYou only have " + bankXp + " XP in bank.");
                return true;
            }

            XpDataManager.removeXp(player, amount);
            XpDataManager.addXp(target, amount);

            int newBalance = XpDataManager.getTotalXp(player);
            player.sendMessage("§aTransferred " + amount + " XP to " + target.getName() + "!");
            player.sendMessage("§7Your Balance: §e" + newBalance + " XP");
            target.sendMessage("§aYou received " + amount + " XP from " + player.getName() + "!");
            target.sendMessage("§7Your Balance: §e" + XpDataManager.getTotalXp(target) + " XP");
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount.");
        }

        return true;
    }

    private boolean handleBalance(Player player) {
        int bankXp = XpDataManager.getTotalXp(player);
        int playerXp = player.getTotalExperience();
        int playerLevel = player.getLevel();

        player.sendMessage("§a--- XP Bank Balance ---");
        player.sendMessage("§7Bank XP: §e" + bankXp);
        player.sendMessage("§7Your XP: §e" + playerXp + " §7(Level: " + playerLevel + ")");

        double onlineRate = XpUltimate.getInstance().getConfigManager().getOnlineInterest();
        double offlineRate = XpUltimate.getInstance().getConfigManager().getOfflineInterest();
        player.sendMessage("§7");
        player.sendMessage("§7Interest Rates:");
        player.sendMessage("§7  Online: §e" + onlineRate + "% §7per cycle");
        player.sendMessage("§7  Offline: §e" + offlineRate + "% §7per cycle");

        return true;
    }

    private boolean handleInterest(Player player) {
        double onlineRate = XpUltimate.getInstance().getConfigManager().getOnlineInterest();
        double offlineRate = XpUltimate.getInstance().getConfigManager().getOfflineInterest();
        boolean enabled = XpUltimate.getInstance().getConfigManager().isInterestEnabled();
        long interval = XpUltimate.getInstance().getConfigManager().getInterestInterval();

        player.sendMessage("§a--- Interest Rates ---");
        player.sendMessage("§7Status: " + (enabled ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("§7Online Rate: §e" + onlineRate + "%");
        player.sendMessage("§7Offline Rate: §e" + offlineRate + "%");
        player.sendMessage("§7Interval: §e" + interval + " seconds");

        return true;
    }

    private boolean handleReset(Player player, String[] args) {
        if (!player.hasPermission("xpbank.reset")) {
            player.sendMessage("§cYou don't have permission to reset XP.");
            return true;
        }

        Player target;
        if (args.length >= 2) {
            if (!player.hasPermission("xpbank.reset.others")) {
                player.sendMessage("§cYou can't reset other players' XP.");
                return true;
            }
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
        } else {
            target = player;
        }

        int previousBankXp = XpDataManager.getTotalXp(target);
        XpDataManager.setXp(target, 0);

        player.sendMessage("§aReset XP bank for " + target.getName() + " (was: " + previousBankXp + " XP)");
        if (!target.equals(player)) {
            target.sendMessage("§cYour XP bank has been reset by an admin.");
        }

        return true;
    }
}
