package club.curahq.core.util.chat;

import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class TextUtil
{
  public static Text join(Collection<Text> textCollection, String delimiter)
  {
    Text result = new Text();
    Text prefix = new Text();
    for (Text text : textCollection)
    {
      result.append(prefix).append(text);
      prefix = new Text(", ");
    }
    return result;
  }
  
  public static Text joinItemList(Collection<ItemStack> collection, String delimiter, boolean showQuantity)
  {
    Text text = new Text();
    for (ItemStack stack : collection) {
      if (stack != null)
      {
        text.append(new Text(delimiter));
        if (showQuantity) {
          text.append(new Text("[").setColor(ChatColor.YELLOW));
        }
        text.appendItem(stack);
        if (showQuantity) {
          text.append(new Text(" x" + stack.getAmount()).setColor(ChatColor.YELLOW)).append(new Text("]").setColor(ChatColor.YELLOW));
        }
      }
    }
    return text;
  }
}
