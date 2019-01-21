package club.curahq.core.commands.staff;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseCommand;
import club.curahq.core.util.core.BaseConstants;

public class FixCommand
  extends BaseCommand
{
  public FixCommand()
  {
    super("fix", "Allows repairing of damaged tools for a player.");
    setUsage("/(command) <playerName> [all]");
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    Player target;
    if (args.length > 0)
    {
      target = BukkitUtils.playerWithNameOrUUID(args[0]);
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
    Set<ItemStack> toRepair = new HashSet<ItemStack>();
    if ((args.length >= 1) && (args[1].equalsIgnoreCase("all")))
    {
      PlayerInventory targetInventory = target.getInventory();
      toRepair.addAll(Arrays.asList(targetInventory.getContents()));
      toRepair.addAll(Arrays.asList(targetInventory.getArmorContents()));
    }
    else
    {
      toRepair.add(target.getItemInHand());
    }
    for (ItemStack stack : toRepair) {
      if ((stack != null) && (stack.getType() != Material.AIR)) {
        stack.setDurability((short)0);
      }
    }
    sender.sendMessage(ChatColor.GRAY + "Repaired " + (toRepair.size() > 1 ? "all" : "item in hand") + " of " + target.getName() + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 1 ? null : Collections.emptyList();
  }
}
