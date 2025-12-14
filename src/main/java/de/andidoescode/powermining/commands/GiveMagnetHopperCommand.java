package de.andidoescode.powermining.commands;

import de.andidoescode.powermining.PowerMining;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GiveMagnetHopperCommand implements CommandExecutor, TabCompleter {

    private final PowerMining plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public GiveMagnetHopperCommand(PowerMining plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("powermining.give.magnethopper")) {
            String noPermMsg = plugin.getConfig().getString("messages.no-permission", "<red>You don't have permission to do that!</red>");
            sender.sendMessage(miniMessage.deserialize(noPermMsg));
            return true;
        }

        Player target;
        int radius = plugin.getConfig().getInt("magnet-hopper.default-radius", 8);

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(miniMessage.deserialize("<red>Console must specify a player!</red>"));
                return true;
            }
            target = player;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(miniMessage.deserialize("<red>Player not found: " + args[0] + "</red>"));
                return true;
            }
            
            if (args.length >= 2) {
                try {
                    radius = Integer.parseInt(args[1]);
                    int maxRadius = plugin.getConfig().getInt("magnet-hopper.max-radius", 32);
                    if (radius < 1 || radius > maxRadius) {
                        sender.sendMessage(miniMessage.deserialize("<red>Radius must be between 1 and " + maxRadius + "!</red>"));
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(miniMessage.deserialize("<red>Invalid radius: " + args[1] + "</red>"));
                    return true;
                }
            }
        }

        var magnetHopper = plugin.getMagnetHopperManager().createMagnetHopper(radius);
        target.getInventory().addItem(magnetHopper);
        
        String givenMsg = plugin.getConfig().getString("messages.magnet-hopper-given", 
            "<green>You received a <gold>Magnet Hopper</gold> with radius <yellow>{radius}</yellow>!</green>");
        target.sendMessage(miniMessage.deserialize(givenMsg.replace("{radius}", String.valueOf(radius))));
        
        if (sender != target) {
            sender.sendMessage(miniMessage.deserialize("<green>Gave Magnet Hopper to " + target.getName() + "!</green>"));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            completions = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partial))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            completions.add("8");
            completions.add("16");
            completions.add("24");
            completions.add("32");
        }
        
        return completions;
    }
}
