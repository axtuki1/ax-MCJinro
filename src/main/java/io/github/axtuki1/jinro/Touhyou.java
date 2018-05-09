package io.github.axtuki1.jinro;

import java.util.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Touhyou extends JavaPlugin {

	/*
	 * 投票
	 */

	private static Config Data = Jinro.getData();

	public static boolean Admin(CommandSender sender, String commandLabel, String[] args) {
		if(args.length == 1){
			sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou open " + ChatColor.GREEN + "投票を開示します。");
			sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou check " + ChatColor.GREEN + "投票が完了できているか確認します。");
			sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou set <投票者> <投票先> " + ChatColor.GREEN + "代理投票をします。");
			sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou kill <Player> " + ChatColor.GREEN + "受刑者を指定します。(投票は全票になります。本人はランダムです。)");
			return true;
		}
		if(Status.getStatus() != Status.GamePlaying && !Jinro.getDebug()){
			sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "ゲームが開始されていません。");
			return true;
		}
		if(Cycle.getStatus() != Cycle.Vote && !Jinro.getDebug()){
			sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "投票の時間ではありません。");
			return true;
		}
		ArrayList<Player> alive = Yakusyoku.getAlivePlayers();
		alive = Yakusyoku.getAlivePlayers();
		ArrayList<Player> pl = new ArrayList<Player>();
		switch(args[1].toLowerCase()){
			case "kill":
				if(args.length == 2){
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou open " + ChatColor.GREEN + "投票を開示します。");
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou check " + ChatColor.GREEN + "投票が完了できているか確認します。");
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou set <投票者> <投票先> " + ChatColor.GREEN + "代理投票をします。");
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou kill <Player> " + ChatColor.GREEN + "受刑者を指定します。(投票は全票になります。本人はランダムです。)");
					return true;
				}
				// 変数 pk : 処刑対象
				Player pk = Utility.getPlayer( args[2] );
				if(pk == null){
					sender.sendMessage(Jinro.getPrefix() + ChatColor.RED +"プレイヤーが見つかりませんでした。");
					return true;
				}
				alive.remove(pk);
				for(Player p : alive){
					// 変数 p : 全プレイヤーのループ
					setTouhyou(p, pk);
				}
				int random = new Random().nextInt(alive.size());
				Player pr = (Player) alive.toArray()[random];
				setTouhyou( pk, pr );
			case "open":
				if(alive.size() == 0){
					sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "生存者がいません。");
					return true;
				}
				if(args.length == 3){
					if(!args[2].equalsIgnoreCase("force")){
						// 投票check
						switch (GameMode.getGameMode()){
//							case OneNightJinro:
//								for(Player p : alive){
//									if( getTouhyou( p ) == null && !p.hasPermission("axtuki1.Jinro.GameMaster")){
//										pl.add(p);
//									}
//								}
//								break;
							case MinecraftJinro:
								for(Player p : alive){
									if( !Yakusyoku.getDeath(p) && getTouhyou( p ) == null && !p.hasPermission("axtuki1.Jinro.GameMaster")){
										pl.add(p);
									}
								}
								break;
						}
						if(pl.size() != 0){
							for(Player p : pl){
								sender.sendMessage(Jinro.getPrefix() + p.getName());
							}
							sender.sendMessage(Jinro.getPrefix() + "が投票していません。");
							sender.sendMessage(Jinro.getPrefix() + "投票を開示したい場合は");
							sender.sendMessage(Jinro.getPrefix() + "/jinro_ad touhyou open force を実行してください。");
							return true;
						}
						// 投票check おわり
					}
				}

				if(pl.size() != 0){
					for(Player p : pl){
						sender.sendMessage(Jinro.getPrefix() + p.getName());
					}
					sender.sendMessage(Jinro.getPrefix() + "が投票していません。");
					sender.sendMessage(Jinro.getPrefix() + "投票を開示したい場合は");
					sender.sendMessage(Jinro.getPrefix() + "/jinro_ad touhyou open force を実行してください。");
					return true;
				}

				Map<Player, String> touhyou = new HashMap<Player, String>();
				Map<Player, Integer> touhyouC = new HashMap<Player, Integer>();
				int max = 0;

				Bukkit.broadcastMessage(ChatColor.RED + "============[投票結果]============");

				for(Player p : alive){
					Player touhyoup = getTouhyou(p);
					if(touhyoup == null){
						continue;
					}
					touhyou.put(p, touhyoup.getName());
				}

				List<Map.Entry<Player, String>> entries = new ArrayList<Map.Entry<Player, String>>( touhyou.entrySet() );
				Collections.sort(entries, new Comparator<Map.Entry<Player, String>>() {
					//比較関数
					@Override
					public int compare(Map.Entry<Player, String> o1, Map.Entry<Player, String> o2) {
						return o2.getValue().compareTo(o1.getValue());    //降順
					}
				});
				for (Map.Entry<Player, String> e : entries) {
					Bukkit.broadcastMessage(ChatColor.GREEN + e.getKey().getName() + " -> " + e.getValue());
					touhyouC.put(Utility.getPlayer( e.getValue() ), getHiTouhyou( Utility.getPlayer( e.getValue() ) ));
				}


				Bukkit.broadcastMessage(ChatColor.RED + "==================================");

				Player syokei = null;

				List<Map.Entry<Player, Integer>> entriesa = new ArrayList<Map.Entry<Player, Integer>>(touhyouC.entrySet());
				Collections.sort(entriesa, new Comparator<Map.Entry<Player, Integer>>() {
					//比較関数
					@Override
					public int compare(Map.Entry<Player, Integer> o1, Map.Entry<Player, Integer> o2) {
						return o2.getValue().compareTo(o1.getValue());    //降順
					}
				});
				boolean max_found = false;
				for (Map.Entry<Player, Integer> e : entriesa) {
					Bukkit.broadcastMessage(ChatColor.GREEN + e.getKey().getName() + " (" + e.getValue() + "票)");
					max = Math.max(max, e.getValue());
					if(!max_found){
						max_found = true;
						syokei = e.getKey();
					}
				}

				max_found = false;

				Bukkit.broadcastMessage(ChatColor.RED + "==================================");

				boolean touhyou_again = false;
				for(int a : touhyouC.values()){
					if(a == max){
						if(max_found && !touhyou_again) {
							Bukkit.broadcastMessage(ChatColor.YELLOW + "---- 同数票のため、再投票になります。 ----");
							touhyou_again = true;
						} else {
							max_found = true;
						}
					}
				}
				if(max_found && !touhyou_again) {
					Bukkit.broadcastMessage(ChatColor.YELLOW + "投票の結果、" + syokei.getName() + " が処刑されます。");
					Bukkit.broadcastMessage(ChatColor.AQUA + "チャットが全体に聞こえるようになりました。");
					syokei.setGlowing(true);
					Cycle.setStatus(Cycle.Vote);
					Yakusyoku.setExecution(syokei);
					Timer.NextCycle();
				}
				Bukkit.broadcastMessage(ChatColor.RED + "==================================");
				if(touhyou_again){
					Cycle.setStatus(Cycle.VoteAgain);
				}
				return true;
			case "check":
				pl = new ArrayList<Player>();
				touhyou = new HashMap<Player, String>();
				touhyouC = new HashMap<Player, Integer>();
				max = 0;

				sender.sendMessage(ChatColor.RED + "============[投票結果]============");

				for(Player p : alive){
					Player touhyoup = getTouhyou(p);
					if(touhyoup == null){
						continue;
					}
					touhyou.put(p, touhyoup.getName());
				}

				entries = new ArrayList<Map.Entry<Player, String>>( touhyou.entrySet() );
				Collections.sort(entries, new Comparator<Map.Entry<Player, String>>() {
					//比較関数
					@Override
					public int compare(Map.Entry<Player, String> o1, Map.Entry<Player, String> o2) {
						return o2.getValue().compareTo(o1.getValue());    //降順
					}
				});
				for (Map.Entry<Player, String> e : entries) {
					sender.sendMessage(ChatColor.GREEN + e.getKey().getName() + " -> " + e.getValue());
					touhyouC.put(Utility.getPlayer( e.getValue() ), getHiTouhyou( Utility.getPlayer( e.getValue() ) ));
				}


				sender.sendMessage(ChatColor.RED + "==================================");

				syokei = null;

				entriesa = new ArrayList<Map.Entry<Player, Integer>>(touhyouC.entrySet());
				Collections.sort(entriesa, new Comparator<Map.Entry<Player, Integer>>() {
					//比較関数
					@Override
					public int compare(Map.Entry<Player, Integer> o1, Map.Entry<Player, Integer> o2) {
						return o2.getValue().compareTo(o1.getValue());    //降順
					}
				});
				max_found = false;
				for (Map.Entry<Player, Integer> e : entriesa) {
					sender.sendMessage(ChatColor.GREEN + e.getKey().getName() + " (" + e.getValue() + "票)");
					max = Math.max(max, e.getValue());
					if(!max_found){
						max_found = true;
						syokei = e.getKey();
					}
				}
				for(Player p : alive){
					if( !Yakusyoku.getDeath(p) && getTouhyou( p ) == null && !p.hasPermission("axtuki1.Jinro.GameMaster")){
						pl.add(p);
					}
				}
				if(pl.size() != 0){
					StringBuilder non_vote = new StringBuilder();
					for(Player p : pl){
						non_vote.append(p.getName()).append(", ");
					}
					sender.sendMessage(Jinro.getPrefix() + non_vote.toString().substring(0, non_vote.length() - 2));
					sender.sendMessage(Jinro.getPrefix() + "が投票していません。");
					sender.sendMessage(Jinro.getPrefix() + "強制的に開票する場合は、");
					sender.sendMessage(Jinro.getPrefix() + Help.CmdColor("/jinro_ad touhyou open force") + " を実行してください。");
				} else {
					sender.sendMessage(Jinro.getPrefix() + "投票していない人はいません。");
				}
				return true;
			case "set":
				if(args.length == 2){
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou open " + ChatColor.GREEN + "投票を開示します。");
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou check " + ChatColor.GREEN + "投票が完了できているか確認します。");
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou set <投票者> <投票先> " + ChatColor.GREEN + "代理投票をします。");
					sender.sendMessage(ChatColor.AQUA + "/jinro_ad touhyou kill <Player> " + ChatColor.GREEN + "受刑者を指定します。(投票は全票になります。本人はランダムです。)");
					return true;
				} else if(args.length == 3){
					sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "投票先を指定してください。");
					return true;
				}
				Player ps = Utility.getPlayer( args[2] );
				pk = Utility.getPlayer( args[3] );
				if(ps == null){
					sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "投票する人が見つかりませんでした。");
					return true;
				}
				if(pk == null){
					sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "投票先が見つかりませんでした。");
					return true;
				}
				setTouhyou(ps, pk);
				sender.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + ps.getName() + " を " + pk.getName() + " に投票させました。");
				return true;
		}
		return false;
	}

	public static Integer getHiTouhyou(Player p) {
		int a = Data.getInt("Players." + p.getUniqueId() + ".hitouhyou");
		return a;
	}

	public static Player getTouhyou(Player p) {
		Data.reloadConfig();
		String a = Data.getString("Players." + p.getUniqueId() + ".touhyou");
		if(a==null){
			return null;
		}
		return Bukkit.getPlayer( UUID.fromString(a) );
	}

	public static void setTouhyou(Player p, Player hp) {
		String a = Data.getString("Players." + p.getUniqueId() + ".touhyou");
		if(a != null){
			Data.set("Players." + a + ".hitouhyou", Data.getInt("Players." + a + ".hitouhyou") - 1);
		}
		Data.set("Players." + p.getUniqueId() + ".touhyou", hp.getUniqueId().toString());
		Data.set("Players." + hp.getUniqueId() + ".hitouhyou", Data.getInt("Players." + hp.getUniqueId() + ".hitouhyou") + 1);
		Data.saveConfig();
		return;
	}

	public static void removeComingOut(Player p) {
		Data.set("Players." + p.getUniqueId() + ".touhyou", null);
		Data.saveConfig();
		return;
	}

}
