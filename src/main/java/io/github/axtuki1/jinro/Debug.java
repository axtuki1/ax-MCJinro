package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.axtuki1.jinro.Jinro.getPrefix;
import static io.github.axtuki1.jinro.Jinro.sendMessage;

public class Debug extends JavaPlugin {

	private static Config Data = Jinro.getData();

	public static boolean Admin(CommandSender sender, String commandLabel, String[] args) {
		if(args[1].equalsIgnoreCase("set")){
			if(args[2].equalsIgnoreCase("Cycle")){
				if(args[3].equalsIgnoreCase("Night")){
					Cycle.setStatus(Cycle.Night);
				} else if(args[3].equalsIgnoreCase("Discussion")){
					Cycle.setStatus(Cycle.Discussion);
				} else if(args[3].equalsIgnoreCase("Vote")){
					Cycle.setStatus(Cycle.Vote);
				} else if(args[3].equalsIgnoreCase("Execution")){
					Cycle.setStatus(Cycle.Execution);
				} else if(args[3].equalsIgnoreCase("Standby")){
					Cycle.setStatus(Cycle.Standby);
				}
				sender.sendMessage("Set Cycle: " + Cycle.getStatus().toString());
			} else if(args[2].equalsIgnoreCase("Status")){
				if(args[3].equalsIgnoreCase("Standby")){
					Status.setStatus(Status.GameStandby);
				} else if(args[3].equalsIgnoreCase("Playing")){
					Status.setStatus(Status.GamePlaying);
				} else if(args[3].equalsIgnoreCase("Pause")){
					Status.setStatus(Status.GamePause);
				} else if(args[3].equalsIgnoreCase("End")){
					Status.setStatus(Status.GameEnd);
				}
				sender.sendMessage("Set Status: " + Status.getStatus().toString());
			} else if(args[2].equalsIgnoreCase("syokei")){
				Yakusyoku.setExecution(Utility.getPlayer(args[3]));
				sender.sendMessage("Set syokei: " + Yakusyoku.getExecution());
			}
		}
		if(args[1].equalsIgnoreCase("get")){
			Player p = Utility.getPlayer( sender.getName() );
			if(args[2].equalsIgnoreCase("Cycle")){
				sender.sendMessage(Cycle.getStatus().toString());
			} else if(args[2].equalsIgnoreCase("Status")){
				sender.sendMessage(Status.getStatus().toString());
			} else if(args[2].equalsIgnoreCase("syokei")){
				sender.sendMessage("Set syokei: " + Yakusyoku.getExecution());
			}
		}
		if(args[1].equalsIgnoreCase("kami")){
			if(Status.getStatus() != Status.GamePlaying && Cycle.getStatus() != Cycle.Night && !Jinro.getDebug()){
				sender.sendMessage(getPrefix() + ChatColor.RED + "まだ使用できません。");
				return true;
			}
			if (args.length == 2) {
				sendMessage(sender, "プレイヤーを指定してください。", LogLevel.ERROR);
			} else {
				Player p = Utility.getPlayer(args[2]);
				if (p == null) {
					sendMessage(sender, "プレイヤーが見つかりませんでした。", LogLevel.ERROR);
					return true;
				}
				if (p.getName() == sender.getName()) {
					sendMessage(sender, "自分を噛むことはできません。", LogLevel.ERROR);
					return true;
				}
				if (Cycle.getStatus() != Cycle.Night) {
					sendMessage(sender, "夜ではありません。", LogLevel.ERROR);
					return true;
				}
				if (Timer.getGameElapsedTime() <= 15) {
					sender.sendMessage(getPrefix() + "最初の15秒間は噛むことができません。");
					return true;
				}
				if (Data.getConfig().getBoolean("Status.kami." + Timer.getDay())) {
					sendMessage(sender, "今夜は既に噛んでいます。", LogLevel.ERROR);
					return true;
				}
				if (Yakusyoku.getYaku(p) == Yakusyoku.人狼) {
					sendMessage(sender, "味方を噛むことはできません。", LogLevel.ERROR);
					return true;
				}
				if (Timer.getDay() == 1) {
					sendMessage(sender, "初日は噛むことができません。", LogLevel.ERROR);
					return true;
				}

				sender.sendMessage(getPrefix() + ChatColor.RED + sender.getName() + " が " + args[2] + " の家を襲いました...");
				Data.set("Status.kami." + Timer.getDay(), true);
				Yakusyoku yaku = Yakusyoku.getYaku(p);
				boolean kami = true;

				if (Yakusyoku.getGoei(Timer.getDay(), p)) {
					kami = false;
					Bukkit.broadcast(getPrefix() + ChatColor.GRAY + "[噛み:護衛成功]" + sender.getName() + " -->✘[" + Yakusyoku.getGoei_sender(p).getName() + "] " + p.getName() + "(" + Yakusyoku.getYakuColor(yaku) + yaku.toString() + ChatColor.GRAY + ")", "axtuki1.Jinro.GameMaster");
				} else if( Yakusyoku.getYaku(p) == Yakusyoku.人形使い && !Data.getBoolean("Players." + p.getUniqueId() + ".Ningyou.Use") ){
					kami = false;
					Data.set("Players." + p.getUniqueId() + ".Ningyou.Use", true);
					Bukkit.broadcast(getPrefix() + ChatColor.GRAY + "[噛み:身代わり]" + sender.getName() + " -->✘ " + p.getName() + ChatColor.GRAY + "(" + Yakusyoku.getYakuColor(yaku) + yaku.toString() + ChatColor.GRAY + ")", "axtuki1.Jinro.GameMaster");
				} else if(Yakusyoku.getYaku(p) == Yakusyoku.妖狐){
					kami = false;
				} else {
					Bukkit.broadcast(getPrefix() + ChatColor.RED + "[噛み]" + sender.getName() + " -> " + p.getName() + "("+ Yakusyoku.getYakuColor( yaku ) + yaku.toString() + ChatColor.GRAY +")"  , "axtuki1.Jinro.GameMaster");
					if(Yakusyoku.getYaku(p) == Yakusyoku.爆弾魔){
						sender.sendMessage(getPrefix() + sender.getName() + " が爆発に巻き込まれて死にました。");
						Yakusyoku.setMorningDeath( Utility.getPlayer( sender.getName() ) );
						Stats.setDeath(Utility.getPlayer( sender.getName() ), Stats.death.Explosion, (Stats.getDeath(Utility.getPlayer( sender.getName() ), Stats.death.Explosion) + 1));
						Bukkit.broadcast(getPrefix() + ChatColor.RED + "[噛み:爆死]" + p.getName()  + "("+ Yakusyoku.getYakuColor( yaku ) + yaku.toString() + ChatColor.GRAY +")" + " -> " + sender.getName(), "axtuki1.Jinro.GameMaster");
					}
				}
				if( kami ){
					p.sendMessage(ChatColor.RED + "あなたは噛み殺されました....");
					p.sendMessage(ChatColor.RED + "翌日霊界へご案内します。");
					Data.set("Status.kami." + Timer.getDay(), true);
					Yakusyoku.setMorningDeath(p);
				}
			}
			return true;
		}
		return true;
	}

}
