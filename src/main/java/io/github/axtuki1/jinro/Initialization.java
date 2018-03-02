package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public class Initialization extends JavaPlugin {

	private static Config Data = Jinro.getData();

	public static boolean Admin() {
	    Scoreboard board = ScoreBoard.getScoreboard();
	    // サイドバー: 情報
	    Objective Obj = ScoreBoard.getInfoObj();
	    if(Obj == null){
	    	Obj = board.registerNewObjective("MinecraftJinro", "dummy");
	    	Obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	    	Obj.setDisplayName("情報");
	    } else {
	    	Obj.unregister();
	    	Obj = board.registerNewObjective("MinecraftJinro", "dummy");
	    	Obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	    	Obj.setDisplayName("情報");
	    }
	    //Jinro.getMain().getLogger().info("スコアボード: 情報 - 初期化完了");
	    // プレイヤーリスト: 発言カウンター
		Objective Obj_C = ScoreBoard.getChatCounterObj();
		if(Obj_C == null){
	    } else {
	    	Obj_C.unregister();
	    }
		Obj_C = board.registerNewObjective("ChatCounter", "dummy");
	    if(Jinro.getMain().getConfig().getBoolean("ChatCounterEnable")){
			Obj_C.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
		Obj_C.setDisplayName("発言カウンター");
		//Jinro.getMain().getLogger().info("スコアボード: 発言カウンター - 初期化完了");
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.setPlayerListName(p.getName());
			if (p.hasPermission("axtuki1.Jinro.GameMaster")) {
				p.setPlayerListName(ChatColor.YELLOW + "[GM] " + p.getName() + " ");
			} else {
				if (p.getGameMode().equals(GameMode.SPECTATOR)) {
					p.setGameMode(GameMode.ADVENTURE);
					Jinro.TeleportToRespawn(p);
				}
			}
			p.setGlowing(false);
			if ( Yakusyoku.getDeath(p) ) {
				Jinro.TeleportToRespawn(p);
			}
		}
		//Jinro.getMain().getLogger().info("プレイヤーリスト - 初期化完了");
		Yakusyoku.resetYaku();
		//Jinro.getMain().getLogger().info("役職 - 初期化完了");
		if(Timer.getTimerStatus()) {
			Timer.setTimerStopFlag(true);
		}
		Data.set("Players" , null);
		Data.set("Status", null);
		Data.set("NPCs", null);
		Data.set("Server_is_Alive", true);
		//Jinro.getMain().getLogger().info("ゲームデータ - 初期化完了");
		Jinro.getCurrentWorld().setTime(6000);
		Timer.setDay(1);
		Timer.setGameStopFlag(false);
		Timer.setGameEndFlag(false);
		Timer.setTimerPauseFlag(false);
		Timer.setCyclePauseFlag(false);
		Status.setStatus(Status.GameStandby);
		Cycle.setStatus(Cycle.Standby);
		//Jinro.getMain().getLogger().info("各種フラグ - 初期化完了");
		if(Jinro.getDebug()){
			ScoreBoard.getInfoObj().getScore( ChatColor.RED + "===[DEBUG MODE]===").setScore(0);
		} else {
			ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===§c");
			ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===");
		}
		return true;
	}
	
	public static boolean Admin(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(Jinro.getPrefix() + "初期化します。");
		Admin();
	    sender.sendMessage(Jinro.getPrefix() + "初期化しました。");
		return true;
	}

	
	
}
