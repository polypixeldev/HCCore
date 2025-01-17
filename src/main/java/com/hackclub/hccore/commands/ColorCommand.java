package com.hackclub.hccore.commands;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class ColorCommand implements TabExecutor {

  private final HCCorePlugin plugin;

  public ColorCommand(HCCorePlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
      @NotNull String alias, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(text("You must be a player to use this").color(RED));
      return true;
    }

    if (args.length == 0) {
      return false;
    }

    PlayerData data = this.plugin.getDataManager().getData(player);

    // Validate selected color
    TextColor newColor = null;
    if (args.length > 1) {
      newColor = NamedTextColor.NAMES.keyToValue().get(args[1]);
      if (newColor == null) {
        newColor = TextColor.fromCSSHexString(args[1]);
      }
      if (newColor == null) {
        newColor = TextColor.fromHexString(args[1]);
      }

      if (newColor == null) {
        sender.sendMessage(text("Invalid color specified").color(RED));
      }


    }

    switch (args[0].toLowerCase()) {
      // /color chat [color]
      case "chat" -> {
        if (args.length == 1) {
          data.setMessageColor(null);
          sender.sendMessage("Your chat color has been reset");
          break;
        }
        data.setMessageColor(newColor);
        sender.sendMessage(
            text("Your chat color has been set to ").append(text("this color").color(newColor)));
      }
      // /color name [color]
      case "name" -> {
        if (args.length == 1) {
          data.setNameColor(null);
          sender.sendMessage("Your name color has been reset");
          break;
        }
        data.setNameColor(newColor);
        sender.sendMessage(
            text("Your name color has been set to ").append(text("this color").color(newColor)));
      }
      default -> {
        return false;
      }
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
      @NotNull String alias, String[] args) {
    if (!(sender instanceof Player)) {
      return null;
    }

    List<String> completions = new ArrayList<>();
    switch (args.length) {
      // Complete subcommand
      case 1 -> {
        List<String> subcommands = Arrays.asList("chat", "name");
        StringUtil.copyPartialMatches(args[0], subcommands, completions);
      }
      // Complete color name for /color chat and /color name
      case 2 -> {
        if (!(args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("name"))) {
          break;
        }
        StringUtil.copyPartialMatches(args[1], NamedTextColor.NAMES.keys(), completions);
      }
    }

    Collections.sort(completions);
    return completions;
  }
}
