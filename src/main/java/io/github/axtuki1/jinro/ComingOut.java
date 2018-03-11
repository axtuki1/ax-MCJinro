package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ComingOut extends JavaPlugin {

	/*
	 * カミングアウト
	 */

	private static Config Data = new Config("data.yml");
	
	public static boolean Admin(CommandSender sender, String commandLabel, String[] args) {
		
		return false;
	}

	public static boolean Player(CommandSender sender, String commandLabel, String[] args) {
		if(Status.getStatus() == Status.GamePlaying && Cycle.getStatus() == Cycle.Discussion){
			if(args.length == 1){
				// jinro co
				sendCOHelp(sender);
			} else if(args.length == 2){
				// jinro co いえｊんぎうぇごんｇ
				Player p = Utility.getPlayer(sender.getName());
				if(args[1].equalsIgnoreCase("siro")) {
					if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")) {
						p.setPlayerListName("[○] " + p.getName() + " ");
					}
					p.sendMessage(Jinro.getPrefix() + "白をつけました。");
					setComingOut(p,Yakusyoku.白);
				} else if(args[1].equalsIgnoreCase("kuro")){
					if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")) {
						p.setPlayerListName("[●] " + p.getName() + " ");
					}
					p.sendMessage(Jinro.getPrefix() + "黒をつけました。");
					setComingOut(p,Yakusyoku.黒);
				} else {
					switch (GameMode.getGameMode()) {
						case MinecraftJinro:
							Yakusyoku yaku = Yakusyoku.getNameToYaku(args[1]);
							if(yaku == null){
								sendCOHelp(sender);
								return true;
							}
							setComingOut(p, yaku);
							Bukkit.broadcastMessage(Yakusyoku.getYakuColor(yaku) + p.getName() + "が" + yaku.toString() + "COしました。");
							if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
								p.setPlayerListName(Yakusyoku.getYakuColor(yaku) + "[" + Yakusyoku.getYaku2moji(yaku) + "] " + ChatColor.WHITE + p.getName() + " ");
							}
							break;
						case OneNightJinro:
							OneNightYakusyoku yak = OneNightYakusyoku.getNameToYaku(args[1]);
							if(yak == null){
								sendCOHelp(sender);
								return true;
							}
							setComingOut(p, yak);
							Bukkit.broadcastMessage(OneNightYakusyoku.getYakuColor(yak) + p.getName() + "が" + yak.toString() + "COしました。");
							if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
								p.setPlayerListName(OneNightYakusyoku.getYakuColor(yak) + "[" + OneNightYakusyoku.getYaku2moji(yak) + "] " + ChatColor.WHITE + p.getName() + " ");
							}
					}
				}
			} else {
				sendCOHelp(sender);
			}
		} else {
			sender.sendMessage(Jinro.getPrefix() + "現在カミングアウトは出来ません。");
			return true;
		}
		return true;
	}

	public static Yakusyoku getComingOut(Player p) {
		String y = Data.getString("Players." + p.getUniqueId() + ".co");
		Yakusyoku yaku = null;
		if(y != null){
			yaku = Yakusyoku.getNameToYaku( y );
		}
		return yaku;
	}

	public static OneNightYakusyoku getOneComingOut(Player p) {
		String y = Data.getString("Players." + p.getUniqueId() + ".co");
		OneNightYakusyoku yaku = null;
		if(y != null){
			yaku = OneNightYakusyoku.getNameToYaku( y );
		}
		return yaku;
	}

	public static void setComingOut(Player p, Yakusyoku y) {
		Data.set("Players." + p.getUniqueId() + ".co", Yakusyoku.getYakuToName(y));
		Data.saveConfig();
		return;
	}

	public static void setComingOut(Player p, OneNightYakusyoku y) {
		Data.set("Players." + p.getUniqueId() + ".co", OneNightYakusyoku.getYakuToName(y));
		Data.saveConfig();
		return;
	}

	public static void removeComingOut(Player p) {
		Data.set("Players." + p.getUniqueId() + ".co", null);
		Data.saveConfig();
		return;
	}

	public static void sendCOHelp(CommandSender sender) {
		Yakusyoku.sendYakuHelp(sender);
		Jinro.sendMessage(sender, "白[○]:siro 黒[●]:kuro", LogLevel.INFO);
	}

	public static void sendCOHelp( Player sender ){
		sendCOHelp( ((CommandSender) sender) );
	}

}
