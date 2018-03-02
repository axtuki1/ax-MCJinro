package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public class ScoreBoard extends JavaPlugin {
	
	private static Scoreboard board;
	private static Objective Info;
	private static Objective ChatCounter;

	public static Scoreboard getScoreboard() {
		board = Bukkit.getScoreboardManager().getMainScoreboard();
		return board;
	}
	
	public static Objective getInfoObj() {
		Info = getScoreboard().getObjective(getInfoName());
		return Info;
	}
	
	public static Objective getChatCounterObj() {
		ChatCounter = getScoreboard().getObjective(getChatCounterName());
		return ChatCounter;
	}
	
	public static String getInfoName() {
		String InfoName = Jinro.getMain().getConfig().getString("InfoObjectiveName");
		return InfoName;
	}
	
	public static String getChatCounterName() {
		String ChatCounterName = Jinro.getMain().getConfig().getString("ChatCounterObjectiveName");
		return ChatCounterName;
	}

}
