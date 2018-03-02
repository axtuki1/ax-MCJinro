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
				sender.sendMessage(Jinro.getPrefix() + ChatColor.AQUA + "役職指定に使用できる文字は以下の通りです。");
				sender.sendMessage(ChatColor.AQUA + "村人:murabito 人狼:jinro 占い師:uranai");
				sender.sendMessage(ChatColor.AQUA + "狂人:kyoujin 狩人:kariudo 共有者:kyouyu");
				sender.sendMessage(ChatColor.AQUA + "霊能者:reinou 妖狐:yoko 爆弾魔:bakudan");
				sender.sendMessage(ChatColor.AQUA + "コスプレイヤー:cosplayer 人形使い:ningyou");
				sender.sendMessage(ChatColor.AQUA + "ニワトリ:niwatori");
				sender.sendMessage(ChatColor.AQUA + "白[○]:siro 黒[●]:kuro");
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
					Yakusyoku yaku = Yakusyoku.getNameToYaku(args[1]);
					if(yaku == null){
						sender.sendMessage(Jinro.getPrefix() + ChatColor.AQUA + "役職指定に使用できる文字は以下の通りです。");
						sender.sendMessage(ChatColor.AQUA + "村人:murabito 人狼:jinro 占い師:uranai");
						sender.sendMessage(ChatColor.AQUA + "狂人:kyoujin 狩人:kariudo 共有者:kyouyu");
						sender.sendMessage(ChatColor.AQUA + "霊能者:reinou 妖狐:yoko 爆弾魔:bakudan");
						sender.sendMessage(ChatColor.AQUA + "コスプレイヤー:cosplayer 人形使い:ningyou");
						sender.sendMessage(ChatColor.AQUA + "ニワトリ:niwatori");
						sender.sendMessage(ChatColor.AQUA + "白[○]:siro 黒[●]:kuro");
						return true;
					}
					setComingOut(p, yaku);
					Bukkit.broadcastMessage(Yakusyoku.getYakuColor(yaku) + p.getName() + "が" + yaku.toString() + "COしました。");
					if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
						p.setPlayerListName(Yakusyoku.getYakuColor(yaku) + "[" + Yakusyoku.getYaku2moji(yaku) + "] " + ChatColor.WHITE + p.getName() + " ");
					}
				}
			} else {
				sender.sendMessage(Jinro.getPrefix() + ChatColor.AQUA + "役職指定に使用できる文字は以下の通りです。");
				sender.sendMessage(ChatColor.AQUA + "村人:murabito 人狼:jinro 占い師:uranai");
				sender.sendMessage(ChatColor.AQUA + "狂人:kyoujin 狩人:kariudo 共有者:kyouyu");
				sender.sendMessage(ChatColor.AQUA + "霊能者:reinou 妖狐:yoko 爆弾魔:bakudan");
				sender.sendMessage(ChatColor.AQUA + "コスプレイヤー:cosplayer 人形使い:ningyou");
				sender.sendMessage(ChatColor.AQUA + "ニワトリ:niwatori");
				sender.sendMessage(ChatColor.AQUA + "白[○]:siro 黒[●]:kuro");
			}
		} else {
			sender.sendMessage(Jinro.getPrefix() + "現在カミングアウトは出来ません。");
			return true;
		}
		return true;
	}

	public static Yakusyoku getComingOut(Player p) {
		String y = Data.getString("Players." + p.getName() + ".co");
		Yakusyoku yaku = null;
		if(y != null){
			yaku = Yakusyoku.getNameToYaku( y );
		}

		return yaku;
	}

	public static void setComingOut(Player p, Yakusyoku y) {
		Data.set("Players." + p.getName() + ".co", Yakusyoku.getYakuToName(y));
		Data.saveConfig();
		return;
	}

	public static void removeComingOut(Player p) {
		Data.set("Players." + p.getName() + ".co", null);
		Data.saveConfig();
		return;
	}

}
