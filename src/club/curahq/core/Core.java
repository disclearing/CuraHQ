package club.curahq.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import club.curahq.core.balance.EconomyCommand;
import club.curahq.core.balance.EconomyManager;
import club.curahq.core.balance.FlatFileEconomyManager;
import club.curahq.core.balance.PayCommand;
import club.curahq.core.balance.ShopSignListener;
import club.curahq.core.classes.PvpClassManager;
import club.curahq.core.classes.archer.ArcherClass;
import club.curahq.core.classes.bard.BardRestorer;
import club.curahq.core.classes.type.RogueClass;
import club.curahq.core.combatlog.CombatLogListener;
import club.curahq.core.combatlog.CustomEntityRegistration;
import club.curahq.core.commands.chat.ClearChatCommand;
import club.curahq.core.commands.chat.MessageCommand;
import club.curahq.core.commands.chat.ReplyCommand;
import club.curahq.core.commands.chat.ToggleMessageCommand;
import club.curahq.core.commands.essentials.CoordsCommand;
import club.curahq.core.commands.essentials.DonatorReviveCommand;
import club.curahq.core.commands.essentials.EnchantCommand;
import club.curahq.core.commands.essentials.EndPortalCommand;
import club.curahq.core.commands.essentials.FightCommand;
import club.curahq.core.commands.essentials.GoppleCommand;
import club.curahq.core.commands.essentials.HelpCommand;
import club.curahq.core.commands.essentials.LFFCommand;
import club.curahq.core.commands.essentials.ListCommand;
import club.curahq.core.commands.essentials.LogoutCommand;
import club.curahq.core.commands.essentials.OreStatsCommand;
import club.curahq.core.commands.essentials.PanicCommand;
import club.curahq.core.commands.essentials.PingCommand;
import club.curahq.core.commands.essentials.PlayTimeCommand;
import club.curahq.core.commands.essentials.PvpTimerCommand;
import club.curahq.core.commands.essentials.RandomCommand;
import club.curahq.core.commands.essentials.ResetCommand;
import club.curahq.core.commands.essentials.SkullCommand;
import club.curahq.core.commands.essentials.StatsCommand;
import club.curahq.core.commands.essentials.SudoCommand;
import club.curahq.core.commands.essentials.TLCommand;
import club.curahq.core.commands.essentials.TeamspeakCommand;
import club.curahq.core.commands.essentials.ToggleSidebarCommand;
import club.curahq.core.commands.note.NoteCommand;
import club.curahq.core.commands.staff.BroadCastCommand;
import club.curahq.core.commands.staff.ClearCommand;
import club.curahq.core.commands.staff.CrowbarCommand;
import club.curahq.core.commands.staff.FeedCommand;
import club.curahq.core.commands.staff.FixCommand;
import club.curahq.core.commands.staff.FlyCommand;
import club.curahq.core.commands.staff.FreezeCommand;
import club.curahq.core.commands.staff.GameModeCommand;
import club.curahq.core.commands.staff.GiveCommand;
import club.curahq.core.commands.staff.GodCommand;
import club.curahq.core.commands.staff.HealCommand;
import club.curahq.core.commands.staff.InvSeeCommand;
import club.curahq.core.commands.staff.KillCommand;
import club.curahq.core.commands.staff.LagCommand;
import club.curahq.core.commands.staff.MiscCommands;
import club.curahq.core.commands.staff.MoreCommand;
import club.curahq.core.commands.staff.RefundCommand;
import club.curahq.core.commands.staff.RenameCommand;
import club.curahq.core.commands.staff.SetBorderCommand;
import club.curahq.core.commands.staff.SetCommand;
import club.curahq.core.commands.staff.SpawnCommand;
import club.curahq.core.commands.staff.SpawnerCommand;
import club.curahq.core.commands.staff.StaffModeCommand;
import club.curahq.core.commands.staff.VanishCommand;
import club.curahq.core.commands.staff.WhoisCommand;
import club.curahq.core.commands.teleport.TeleportAllCommand;
import club.curahq.core.commands.teleport.TeleportCommand;
import club.curahq.core.commands.teleport.TeleportHereCommand;
import club.curahq.core.commands.teleport.TopCommand;
import club.curahq.core.commands.teleport.WorldCommand;
import club.curahq.core.deathban.Deathban;
import club.curahq.core.deathban.DeathbanListener;
import club.curahq.core.deathban.DeathbanManager;
import club.curahq.core.deathban.FlatFileDeathbanManager;
import club.curahq.core.deathban.lives.LivesExecutor;
import club.curahq.core.deathban.lives.LivesListener;
import club.curahq.core.deathban.lives.StaffReviveCommand;
import club.curahq.core.faction.FactionExecutor;
import club.curahq.core.faction.FactionManager;
import club.curahq.core.faction.FactionMember;
import club.curahq.core.faction.FlatFileFactionManager;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.claim.ClaimHandler;
import club.curahq.core.faction.claim.ClaimWandListener;
import club.curahq.core.faction.claim.Subclaim;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.EndPortalFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.faction.type.RoadFaction;
import club.curahq.core.faction.type.SpawnFaction;
import club.curahq.core.game.CaptureZone;
import club.curahq.core.game.EventExecutor;
import club.curahq.core.game.conquest.ConquestExecutor;
import club.curahq.core.game.eotw.EOTWHandler;
import club.curahq.core.game.eotw.EotwCommand;
import club.curahq.core.game.eotw.EotwListener;
import club.curahq.core.game.faction.CapturableFaction;
import club.curahq.core.game.faction.ConquestFaction;
import club.curahq.core.game.faction.KothFaction;
import club.curahq.core.game.koth.KothExecutor;
import club.curahq.core.kitmap.SelectorListener;
import club.curahq.core.listener.AutoSmeltOreListener;
import club.curahq.core.listener.BookDeenchantListener;
import club.curahq.core.listener.BorderListener;
import club.curahq.core.listener.BottledExpListener;
import club.curahq.core.listener.ChatListener;
import club.curahq.core.listener.CombatBuildListener;
import club.curahq.core.listener.CrowbarListener;
import club.curahq.core.listener.DeathListener;
import club.curahq.core.listener.DeathMessageListener;
import club.curahq.core.listener.ElevatorListener;
import club.curahq.core.listener.EnderPearlFix;
import club.curahq.core.listener.ExpMultiplierListener;
import club.curahq.core.listener.FoundDiamondsListener;
import club.curahq.core.listener.FurnaceSmeltSpeederListener;
import club.curahq.core.listener.GodListener;
import club.curahq.core.listener.ItemStatTrackingListener;
import club.curahq.core.listener.LoginEvent;
import club.curahq.core.listener.PearlGlitch;
import club.curahq.core.listener.PlayTimeManager;
import club.curahq.core.listener.PotionListener;
import club.curahq.core.listener.SignSubclaimListener;
import club.curahq.core.listener.UnRepairableListener;
import club.curahq.core.listener.WorldListener;
import club.curahq.core.listener.core.CoreListener;
import club.curahq.core.listener.factions.FactionListener;
import club.curahq.core.listener.factions.FactionsCoreListener;
import club.curahq.core.listener.fixes.BeaconStrengthFixListener;
import club.curahq.core.listener.fixes.BlockHitFixListener;
import club.curahq.core.listener.fixes.BlockJumpGlitchFixListener;
import club.curahq.core.listener.fixes.BoatGlitchFixListener;
import club.curahq.core.listener.fixes.DupeGlitchFix;
import club.curahq.core.listener.fixes.EnchantLimitListener;
import club.curahq.core.listener.fixes.EnderChestRemovalListener;
import club.curahq.core.listener.fixes.HungerFixListener;
import club.curahq.core.listener.fixes.InfinityArrowFixListener;
import club.curahq.core.listener.fixes.KnockbackListener;
import club.curahq.core.listener.fixes.NaturalMobSpawnFixListener;
import club.curahq.core.listener.fixes.PearlGlitchListener;
import club.curahq.core.listener.fixes.PortalListener;
import club.curahq.core.listener.fixes.VoidGlitchFixListener;
import club.curahq.core.listener.fixes.WeatherFixListener;
import club.curahq.core.listener.staff.NoteListener;
import club.curahq.core.listener.staff.SkullListener;
import club.curahq.core.listener.staff.StaffModeListener;
import club.curahq.core.listener.staff.VanishListener;
import club.curahq.core.prefix.PrefixCommand;
import club.curahq.core.prefix.PrefixMenu;
import club.curahq.core.scoreboard.ScoreboardHandler;
import club.curahq.core.signs.EventSignListener;
import club.curahq.core.signs.KitSignListener;
import club.curahq.core.sotw.SotwCommand;
import club.curahq.core.sotw.SotwListener;
import club.curahq.core.sotw.SotwTimer;
import club.curahq.core.tablist.TablistManager;
import club.curahq.core.tablist.tablist.TablistAdapter;
import club.curahq.core.timer.TimerExecutor;
import club.curahq.core.timer.TimerManager;
import club.curahq.core.user.ConsoleUser;
import club.curahq.core.user.FactionUser;
import club.curahq.core.user.UserManager;
import club.curahq.core.util.SignHandler;
import club.curahq.core.util.config.PlayerData;
import club.curahq.core.util.config.PotionLimiterData;
import club.curahq.core.util.config.WorldData;
import club.curahq.core.util.core.BasePlugins;
import club.curahq.core.util.core.ConfigUtil;
import club.curahq.core.util.core.Cooldowns;
import club.curahq.core.util.core.DateTimeFormats;
import club.curahq.core.util.core.Message;
import club.curahq.core.util.core.ServerHandler;
import club.curahq.core.util.itemdb.ItemDb;
import club.curahq.core.util.itemdb.SimpleItemDb;
import club.curahq.core.visualise.ProtocolLibHook;
import club.curahq.core.visualise.VisualiseHandler;
import club.curahq.core.visualise.WallBorderListener;
import lombok.Getter;
import lombok.Setter;
import me.apache.commons.lang3.time.DurationFormatUtils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.google.common.base.Joiner;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@Getter
@Setter
public class Core extends JavaPlugin implements CommandExecutor {

	public void onEnable() {
		
			plugin = this;
			BasePlugins.getPlugin().init(this);
			config = getConfig();
			config.options().copyDefaults(true);
	        new TablistManager(this, new TablistAdapter(this), TimeUnit.SECONDS.toMillis((long) 0.5));

	        MinecraftServer.getServer().setMotd("§6§lCuraHQ §7 §fThe HCFactions Network"
	        		+ "\n"
	        		+ "§8» §6§lHCFactions §fin Development");
	        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------------------------------------------------------------------------");
	        // ====================================== //
	        // 					Config 				  //
	        // ====================================== //
	        log("&7Registering config files...");
			saveConfig();
			conf = new File(getDataFolder(), "config.yml");
			registerConfiguration();

			WorldData.getInstance().setup(this);
			PlayerData.getInstance().setup(this);
			PotionLimiterData.getInstance().setup(this);
			log("&aSuccess!");

	        // ====================================== //
	        // 					Hooks 				  //
	        // ====================================== //
			log("&7Registering hooks...");
			ProtocolLibHook.hook(this);
			Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
			this.worldEdit = (((wep instanceof WorldEditPlugin)) && (wep.isEnabled()) ? (WorldEditPlugin) wep : null);
			log("&aSucces!");
			
			CustomEntityRegistration.registerCustomEntities();
			
	        // ====================================== //
	        // 					Commands 			  //
	        // ====================================== //
			
	        log("&7Registering config commands...");
			registerCommands();
			log("&aSuccess!");
			
	        // ====================================== //
	        // 					Manager 			  //
	        // ====================================== //
			
			log("&7Registering managers...");
			registerManagers();
			log("&aSuccess!");
			
	        // ====================================== //
	        // 					Listeners 		      //
	        // ====================================== //
			
			log("&7Registering listeners...");
			registerListeners();
			log("&aSuccess!");

	        // ====================================== //
	        // 					Classes 		      //
	        // ====================================== //
			
			log("&7Registering timers...");
			Cooldowns.createCooldown("Assassin_item_cooldown");
			Cooldowns.createCooldown("Archer_item_cooldown");
			Cooldowns.createCooldown("revive_cooldown");
			Cooldowns.createCooldown("report_cooldown");
			Cooldowns.createCooldown("helpop_cooldown");
			Cooldowns.createCooldown("rogue_cooldown");
		    Cooldowns.createCooldown("lff_cooldown");
			log("&aSuccess!");
	        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------------------------------------------------------------------------");

			new BukkitRunnable()

			{
				public void run() {
					Core.this.saveData();
				}
			}.runTaskTimerAsynchronously(plugin, TimeUnit.SECONDS.toMillis(20L), TimeUnit.SECONDS.toMillis(20L));
			this.setupBroadcast();
	}

	private void saveData() {
		this.deathbanManager.saveDeathbanData();
		this.economyManager.saveEconomyData();
		this.factionManager.saveFactionData();
		this.playTimeManager.savePlaytimeData();
		this.userManager.saveUserData();
		this.signHandler.cancelTasks(null);


		PlayerData.getInstance().saveConfig();
	}

	public void onDisable() {
		this.pvpClassManager.onDisable();
		CustomEntityRegistration.unregisterCustomEntities();
		CombatLogListener.removeCombatLoggers();
		this.scoreboardHandler.clearBoards();
		this.deathbanManager.saveDeathbanData();
		this.economyManager.saveEconomyData();
		this.factionManager.saveFactionData();
		this.playTimeManager.savePlaytimeData();
		this.userManager.saveUserData();
		StaffModeCommand.onDisableMod();
		saveData();
		plugin = null;
	}
	
	   @SuppressWarnings("deprecation")
	public void setupBroadcast() {
	        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
	            List<String> players = new ArrayList<>();
	            for (Player player : Bukkit.getOnlinePlayers()) {
	                String primary = PermissionsEx.getPermissionManager().getUser(player).getGroups()[0].getName();
	                if (primary != null && primary.equalsIgnoreCase(ConfigUtil.TOP_RANK)) {
	                    players.add(player.getName());
	                }
	            }
	            String message = ConfigUtil.GOLD +  "Online " + ConfigUtil.TOP_RANK + " Donators " +  ChatColor.DARK_GRAY +  " » " + ConfigUtil.GRAY + Joiner.on(ConfigUtil.GRAY + ", ").join(players);
	            if (!players.isEmpty()) {
	                Bukkit.getScheduler().runTask(Core.this, () -> {
	                    Bukkit.broadcastMessage(message);
	                    Bukkit.broadcastMessage(ConfigUtil.YELLOW + "You can buy this rank at " + ConfigUtil.WHITE + ConfigUtil.STORE);

	                });
	            }
	        }, (20 * 60 * 5) + 45, 20 * 60 * 5);
	}
	   
	private void log(String string) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "HCF » " + ChatColor.DARK_GRAY + " " + string.replace("&", "§"));
	}

	private void registerConfiguration() {
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(ConsoleUser.class);
		ConfigurationSerialization.registerClass(Subclaim.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(FactionUser.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(ConquestFaction.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
	}

	private void registerListeners() {
		PluginManager manager = getServer().getPluginManager();
		manager.registerEvents(new KnockbackListener(), this);
		manager.registerEvents(new NoteListener(), this);
		manager.registerEvents(new OreStatsCommand(), this);
		manager.registerEvents(new GodListener(), this);
		manager.registerEvents(new VanishListener(), this);
		manager.registerEvents(new ArcherClass(this), this);
		manager.registerEvents(new RogueClass(this), this);
		manager.registerEvents(new PotionListener(), this);
		manager.registerEvents(new LoginEvent(), this);
		manager.registerEvents(new DupeGlitchFix(), this);
		manager.registerEvents(new PortalListener(this), this);
		manager.registerEvents(new WeatherFixListener(), this);
		manager.registerEvents(new NaturalMobSpawnFixListener(), this);
		manager.registerEvents(new AutoSmeltOreListener(), this);
		manager.registerEvents(new BlockHitFixListener(), this);
		manager.registerEvents(new BlockJumpGlitchFixListener(), this);
		manager.registerEvents(new BoatGlitchFixListener(), this);
		manager.registerEvents(new BookDeenchantListener(), this);
		manager.registerEvents(new BorderListener(), this);
		manager.registerEvents(new ChatListener(this), this);
		manager.registerEvents(new ClaimWandListener(this), this);
		manager.registerEvents(new BottledExpListener(), this);
		manager.registerEvents(new CombatLogListener(this), this);
		manager.registerEvents(new CoreListener(this), this);
		manager.registerEvents(new CrowbarListener(this), this);
		manager.registerEvents(new DeathListener(this), this);
		manager.registerEvents(new ElevatorListener(), this);
		manager.registerEvents(new DeathMessageListener(this), this);
		manager.registerEvents(new RefundCommand(this), this);
		manager.registerEvents(new DeathbanListener(this), this);
		manager.registerEvents(new EnchantLimitListener(), this);
		manager.registerEvents(new EnderChestRemovalListener(), this);
		manager.registerEvents(new FlatFileFactionManager(this), this);
		manager.registerEvents(new EotwListener(this), this);
		manager.registerEvents(new EventSignListener(), this);
		manager.registerEvents(new LivesListener(), this);
		manager.registerEvents(new ExpMultiplierListener(), this);
		manager.registerEvents(new FactionListener(this), this);
		manager.registerEvents(new FoundDiamondsListener(this), this);
		manager.registerEvents(new FurnaceSmeltSpeederListener(), this);
		manager.registerEvents(new InfinityArrowFixListener(), this);
		manager.registerEvents(new ItemStatTrackingListener(), this);
		manager.registerEvents(new HungerFixListener(), this);
		manager.registerEvents(new PearlGlitchListener(this), this);
		manager.registerEvents(new FactionsCoreListener(this), this);
		manager.registerEvents(new PearlGlitch(this), this);
		manager.registerEvents(new EnderPearlFix(this), this);
		manager.registerEvents(new SignSubclaimListener(this), this);
		manager.registerEvents(new EndPortalCommand(getPlugin()), this);
		manager.registerEvents(new ShopSignListener(this), this);
		manager.registerEvents(new SkullListener(), this);
		manager.registerEvents(new BeaconStrengthFixListener(), this);
		manager.registerEvents(new VoidGlitchFixListener(), this);
		manager.registerEvents(new WallBorderListener(this), this);
		manager.registerEvents(this.playTimeManager, this);
		manager.registerEvents(new WorldListener(this), this);
		manager.registerEvents(new UnRepairableListener(), this);
		manager.registerEvents(new StaffModeListener(), this);
		manager.registerEvents(new SotwListener(this), this);
	    manager.registerEvents(new KitSignListener(), this);
	    manager.registerEvents(new CombatBuildListener(), this);
	    manager.registerEvents(new PrefixMenu(), this);
	    manager.registerEvents(new PrefixCommand(), this);
	    
	    if(ConfigUtil.KIT_MAP == true) {
	    	manager.registerEvents(new SelectorListener(), this);
	    }
	    
	}

	private void registerCommands() {

		getCommand("prefix").setExecutor(new PrefixCommand());
		getCommand("note").setExecutor(new NoteCommand());
		getCommand("top").setExecutor(new TopCommand());
		getCommand("list").setExecutor(new ListCommand());
		getCommand("setborder").setExecutor(new SetBorderCommand());
		getCommand("world").setExecutor(new WorldCommand());
		getCommand("endportal").setExecutor(new EndPortalCommand(getPlugin()));
		getCommand("fix").setExecutor(new FixCommand());
		getCommand("enchant").setExecutor(new EnchantCommand());
		getCommand("freeze").setExecutor(new FreezeCommand(this));
		getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
		getCommand("lag").setExecutor(new LagCommand());
		getCommand("broadcast").setExecutor(new BroadCastCommand());
		getCommand("togglemessage").setExecutor(new ToggleMessageCommand());
		getCommand("reply").setExecutor(new ReplyCommand());
		getCommand("message").setExecutor(new MessageCommand());
		getCommand("feed").setExecutor(new FeedCommand());
		getCommand("setspawn").setExecutor(new SpawnCommand());
		getCommand("ping").setExecutor(new PingCommand());
		getCommand("togglemessage").setExecutor(new ToggleMessageCommand());
		getCommand("clearchat").setExecutor(new ClearChatCommand());
		getCommand("togglesidebar").setExecutor(new ToggleSidebarCommand(this));
		getCommand("teleportall").setExecutor(new TeleportAllCommand());
		getCommand("teleporthere").setExecutor(new TeleportHereCommand());
		getCommand("give").setExecutor(new GiveCommand());
		getCommand("gamemode").setExecutor(new GameModeCommand());
		getCommand("fly").setExecutor(new FlyCommand());
		getCommand("invsee").setExecutor(new InvSeeCommand(this));
		getCommand("god").setExecutor(new GodCommand());
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("sotw").setExecutor(new SotwCommand(this));
		getCommand("random").setExecutor(new RandomCommand(this));
		getCommand("conquest").setExecutor(new ConquestExecutor(this));
		getCommand("crowbar").setExecutor(new CrowbarCommand());
		getCommand("economy").setExecutor(new EconomyCommand(this));
		getCommand("eotw").setExecutor(new EotwCommand(this));
		getCommand("event").setExecutor(new EventExecutor(this));
		getCommand("faction").setExecutor(new FactionExecutor(this));
		getCommand("playtime").setExecutor(new PlayTimeCommand(this));
		getCommand("gopple").setExecutor(new GoppleCommand(this));
		getCommand("koth").setExecutor(new KothExecutor(this));
		getCommand("lives").setExecutor(new LivesExecutor(this));
		getCommand("logout").setExecutor(new LogoutCommand(this));
		getCommand("more").setExecutor(new MoreCommand());
		getCommand("panic").setExecutor(new PanicCommand());
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("pay").setExecutor(new PayCommand(this));
		getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
		getCommand("refund").setExecutor(new RefundCommand(this));
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("timer").setExecutor(new TimerExecutor(this));
		getCommand("kill").setExecutor(new KillCommand());
		getCommand("ores").setExecutor(new OreStatsCommand());
		getCommand("help").setExecutor(new HelpCommand());
		getCommand("rename").setExecutor(new RenameCommand());
		getCommand("teamspeak").setExecutor(new TeamspeakCommand());
		getCommand("coords").setExecutor(new CoordsCommand());
		getCommand("fsay").setExecutor(new MiscCommands());
		getCommand("medic").setExecutor(new DonatorReviveCommand(this));
		getCommand("staffmode").setExecutor(new StaffModeCommand());
		getCommand("spawner").setExecutor(new SpawnerCommand());
		getCommand("set").setExecutor(new SetCommand(this));
		getCommand("stats").setExecutor(new StatsCommand()); 
		getCommand("ci").setExecutor(new ClearCommand());
		getCommand("copyinv").setExecutor(new MiscCommands());
		getCommand("slowstop").setExecutor(new MiscCommands());

		getCommand("teleport").setExecutor(new TeleportCommand());
		getCommand("skull").setExecutor(new SkullCommand());
		getCommand("reset").setExecutor(new ResetCommand());
		getCommand("sudo").setExecutor(new SudoCommand());
		getCommand("lff").setExecutor(new LFFCommand());
		getCommand("lff").setExecutor(new LFFCommand());
		getCommand("whois").setExecutor(new WhoisCommand(this));
		getCommand("fight").setExecutor(new FightCommand());
		getCommand("tl").setExecutor(new TLCommand());

		Map<String, Map<String, Object>> map = getDescription().getCommands();
		for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			PluginCommand command = getCommand((String) entry.getKey());
			command.setPermission("command." + (String) entry.getKey());
			command.setPermissionMessage(ConfigUtil.PREFIX + ChatColor.RED.toString() + "You lack the sufficient permissions to execute this command.");
		}
	}

	private void registerManagers() {
		this.claimHandler = new ClaimHandler(this);
		this.deathbanManager = new FlatFileDeathbanManager(this);
		this.economyManager = new FlatFileEconomyManager(this);
		this.eotwHandler = new EOTWHandler(this);
		this.factionManager = new FlatFileFactionManager(this);
		this.itemDb = new SimpleItemDb(this);
		this.playTimeManager = new PlayTimeManager(this);
		this.pvpClassManager = new PvpClassManager(this);
		this.timerManager = new TimerManager(this);
		this.scoreboardHandler = new ScoreboardHandler(this);
		this.userManager = new UserManager(this);
		this.visualiseHandler = new VisualiseHandler();
		this.sotwTimer = new SotwTimer();
		this.message = new Message(this);
		this.signHandler = new SignHandler(this);
		new BardRestorer(this);
	}

	public boolean checkLicense() {
		try {
			URL url = new URL("http://alphadev.pw/license/admin/api.php?plugin=zHub&license=" + this.getConfig().getString("License"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			if ((reader.lines().count() != 0)) {
				reader.close();
				return true;
			} else {
				reader.close();
				return false;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public Core() {
		this.random = new Random();
	}


	public static Core getPlugin() {
		return plugin;
	}

	public static Core getInstance() {
		return instance;
	}

	public static String getReaming(long millis) {
		return getRemaining(millis, true, true);
	}
	
	public static String getRemaining(long millis, boolean milliseconds) {
		return getRemaining(millis, milliseconds, true);
	}

	public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
		if ((milliseconds) && (duration < MINUTE)) {
			return ((DecimalFormat) (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING
					: DateTimeFormats.REMAINING_SECONDS).get()).format(duration * 0.001D) + 's';
		}
		return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
	}
	
	public static String getCombatRemaining(long duration, boolean milliseconds, boolean trail) {
	
		return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
	}
	
	public static String getCombatRemaining(long millis, boolean milliseconds) {
		return getCombatRemaining(millis, milliseconds, true);
		
	}
	
	



    
	public static File conf;
	public static FileConfiguration config;
	public static Core instance;
	private ConfigUtil configuration;
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
	private static final long HOUR = TimeUnit.HOURS.toMillis(1L);
	private static Core plugin;
	public static Plugin pl;
	private ServerHandler serverHandler;
	public BukkitRunnable clearEntityHandler;
	public BukkitRunnable announcementTask;
	private Message message;
	
	public static final Joiner SPACE_JOINER = Joiner.on(' ');
	public static final Joiner COMMA_JOINER = Joiner.on(", ");
	private Random random;
	private PlayTimeManager playTimeManager;
	private WorldEditPlugin worldEdit;
	private ClaimHandler claimHandler;
	private ItemDb itemDb;

	private DeathbanManager deathbanManager;
	private EconomyManager economyManager;
	private EOTWHandler eotwHandler;
	private FactionManager factionManager;
	private PvpClassManager pvpClassManager;
	private VanishListener vanish;
	private ScoreboardHandler scoreboardHandler;
	private SotwTimer sotwTimer;
	private TimerManager timerManager;
	private UserManager userManager;
	private VisualiseHandler visualiseHandler;
	private SignHandler signHandler;
	





}
