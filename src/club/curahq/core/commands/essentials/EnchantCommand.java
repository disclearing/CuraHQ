package club.curahq.core.commands.essentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.core.BaseCommand;
import club.curahq.core.util.core.BaseConstants;

public class EnchantCommand
  extends BaseCommand
{
  public EnchantCommand()
  {
    super("enchant", "Adds enchantment to items.");
    setUsage("/(command) <enchantment> <level> [playerName]");
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 2)
    {
      sender.sendMessage(getUsage());
      return true;
    }
    Player target;
    if ((args.length > 2) && (sender.hasPermission(command.getPermission() + ".others")))
    {
      target = BukkitUtils.playerWithNameOrUUID(args[2]);
    }
    else
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage(getUsage(label));
        return true;
      }
      target = (Player)sender;
    }
    if ((target == null) || (!BaseCommand.canSee(sender, target)))
    {
      sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
      return true;
    }
    Enchantment enchantment = Enchantment.getByName(args[0]);
    if (enchantment == null)
    {
      sender.sendMessage(ChatColor.RED + "No enchantment named '" + args[0] + "' found.");
      return true;
    }
    org.bukkit.inventory.ItemStack stack = target.getItemInHand();
    if ((stack == null) || (stack.getType() == Material.AIR))
    {
      sender.sendMessage(ChatColor.RED + target.getName() + " is not holding an item.");
      return true;
    }
    Integer level = JavaUtils.tryParseInt(args[1]);
    if (level == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
      return true;
    }
    int maxLevel = enchantment.getMaxLevel();
    if ((level.intValue() > maxLevel) && (!sender.hasPermission(command.getPermission() + ".abovemaxlevel")))
    {
      sender.sendMessage(ChatColor.RED + "The maximum enchantment level for " + enchantment.getName() + " is " + maxLevel + '.');
      return true;
    }
    if ((!enchantment.canEnchantItem(stack)))
    {
      sender.sendMessage(ChatColor.RED + "Enchantment " + enchantment.getName() + " cannot be applied to that item.");
      return true;
    }
    stack.addUnsafeEnchantment(enchantment, level.intValue());
    String itemName;
    try
    {
      itemName = CraftItemStack.asNMSCopy(stack).getName();
    }
    catch (Error ex)
    {
      itemName = stack.getType().name();
    }
    Command.broadcastCommandMessage(sender, ChatColor.GRAY + "Enchanted " + enchantment.getName() + " at level " + level + " onto " + itemName + " of " + target.getName() + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    switch (args.length)
    {
    case 1: 
      Enchantment[] enchantments = Enchantment.values();
      ArrayList<String> results = new ArrayList<String>(enchantments.length);
      Enchantment[] arrayOfEnchantment1;
      int j = (arrayOfEnchantment1 = enchantments).length;
      for (int i = 0; i < j; i++)
      {
        Enchantment enchantment = arrayOfEnchantment1[i];
        results.add(enchantment.getName());
      }
      return BukkitUtils.getCompletions(args, results);
    case 3: 
      return null;
    }
    return Collections.emptyList();
  }
}
