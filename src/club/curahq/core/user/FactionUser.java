package club.curahq.core.user;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import club.curahq.core.deathban.Deathban;
import club.curahq.core.util.GenericUtils;


public class FactionUser implements ConfigurationSerializable {
	private final Set<UUID> factionChatSpying = new HashSet<UUID>();
	private final Set<String> shownScoreboardScores = new HashSet<String>();
	private final UUID userUUID;
	private boolean capzoneEntryAlerts;
	private boolean showClaimMap;
	private boolean showLightning = true;
	private Deathban deathban;
	private long lastFactionLeaveMillis;
	private int kills;
	private int diamondsMined;
	private int deaths;
	

	public FactionUser(UUID userUUID) {
		this.userUUID = userUUID;
	}

	public FactionUser(Map<String, Object> map) {
		this.shownScoreboardScores.addAll(
				GenericUtils.createList((Object) map.get("shownScoreboardScores"), (Class<String>) String.class));
		;
		this.userUUID = UUID.fromString((String) map.get("userUUID"));
		this.capzoneEntryAlerts = (Boolean) map.get("capzoneEntryAlerts");
		this.showLightning = (Boolean) map.get("showLightning");
		this.deathban = (Deathban) map.get("deathban");
		this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
		this.diamondsMined = (int) map.get("diamonds");
	    this.kills = (int) map.get("kills");
		this.deaths = (int) map.get("deaths");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map serialize() {
		LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		map.put("shownScoreboardScores", new ArrayList<String>(this.shownScoreboardScores));
		map.put("faction-chat-spying",
				this.factionChatSpying.stream().map(UUID::toString).collect(Collectors.toList()));
		map.put("userUUID", this.userUUID.toString());
		map.put("diamonds", this.diamondsMined);
		map.put("capzoneEntryAlerts", this.capzoneEntryAlerts);
		map.put("showClaimMap", this.showClaimMap);
		map.put("showLightning", this.showLightning);
		map.put("deathban", this.deathban);
		map.put("lastFactionLeaveMillis", Long.toString(this.lastFactionLeaveMillis));
		map.put("kills", this.kills);
		map.put("deaths", this.deaths);

       return map;
	}

	public boolean isShowClaimMap() {
		return this.showClaimMap;
	}

	public boolean isCapzoneEntryAlerts() {
		return this.capzoneEntryAlerts;
	}

	public void setShowClaimMap(boolean showClaimMap) {
		this.showClaimMap = showClaimMap;
	}

    public int getKills() {
        return this.kills;
    }
    
    public void setKills(final int kills) {
        this.kills = kills;
    }

	public int getDiamondsMined() {
		return this.diamondsMined;
	}

	public void setDiamondsMined(int diamondsMined) {
		this.diamondsMined = diamondsMined;
	}

    
    public void setDeaths(final Integer deaths) {
        this.deaths = deaths;
    }
    
    public int getDeaths() {
        return this.deaths;
    }

    public Deathban getDeathban() {
        return this.deathban;
    }
    
    public void setDeathban(final Deathban deathban) {
        this.deathban = deathban;
    }
    
    public void removeDeathban() {
        this.deathban = null;
    }

	public long getLastFactionLeaveMillis() {
		return this.lastFactionLeaveMillis;
	}

	public void setLastFactionLeaveMillis(long lastFactionLeaveMillis) {
		this.lastFactionLeaveMillis = lastFactionLeaveMillis;
	}

	public boolean isShowLightning() {
		return this.showLightning;
	}

	public void setShowLightning(boolean showLightning) {
		this.showLightning = showLightning;
	}

	public Set<UUID> getFactionChatSpying() {
		return this.factionChatSpying;
	}

	public Set<String> getShownScoreboardScores() {
		return this.shownScoreboardScores;
	}

	public UUID getUserUUID() {
		return this.userUUID;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer((UUID) this.userUUID);
	}

	public boolean isCobblestone() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setCobblestone(boolean b) {
		// TODO Auto-generated method stub

	}
}