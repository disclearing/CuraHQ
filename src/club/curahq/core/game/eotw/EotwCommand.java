package club.curahq.core.game.eotw;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.plugin.Plugin;

import club.curahq.core.Core;

import java.util.Collections;
import java.util.List;

public class EotwCommand implements CommandExecutor, TabCompleter {
    private final ConversationFactory factory;

    public EotwCommand(final Core plugin) {
        this.factory = new ConversationFactory((Plugin) plugin).withFirstPrompt((Prompt) new EotwPrompt()).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Command Console only.");
            return true;
        }
        final Conversable conversable = (Conversable) sender;
        conversable.beginConversation(this.factory.buildConversation(conversable));
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }

    private static final class EotwPrompt extends StringPrompt {
        public String getPromptText(final ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? The server will be in EOTW mode, If EOTW mode is active, all claims whilst making Spawn a KOTH. " + "You will still have " + EOTWHandler.EOTW_WARMUP_WAIT_SECONDS + " seconds to cancel this using the same command though. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }

        public Prompt acceptInput(final ConversationContext context, final String string) {
            if(string.equalsIgnoreCase("yes")) {
                final boolean newStatus = !Core.getPlugin().getEotwHandler().isEndOfTheWorld(false);
                final Conversable conversable = context.getForWhom();
                if(conversable instanceof CommandSender) {
                    Command.broadcastCommandMessage((CommandSender) conversable, ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
                } else {
                    conversable.sendRawMessage(ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
                }
                Core.getPlugin().getEotwHandler().setEndOfTheWorld(newStatus);
            } else if(string.equalsIgnoreCase("no")) {
                context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of setting EOTW mode.");
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of toggling EOTW mode has been cancelled.");
            }
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
