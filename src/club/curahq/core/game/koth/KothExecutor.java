package club.curahq.core.game.koth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import club.curahq.core.Core;
import club.curahq.core.game.koth.argument.KothSetCapDelayArgument;
import club.curahq.core.game.koth.argument.KothShowArgument;
import club.curahq.core.util.command.ArgumentExecutor;

public class KothExecutor
  extends ArgumentExecutor
{
  
  public KothExecutor(Core plugin)
  {
    super("koth");
    addArgument(new KothShowArgument());
    addArgument(new KothSetCapDelayArgument(plugin));
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 1)
    {
      return true;
    }
    return super.onCommand(sender, command, label, args);
  }
}
