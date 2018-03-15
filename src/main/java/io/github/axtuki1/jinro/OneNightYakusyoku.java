package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum OneNightYakusyoku {
	村人, 人狼, 占い師, 狂人, 聴狂人, 怪盗, 白, 黒;
	
	// プレイヤーの役職等 内部処理担当	

	private static Config Data = Jinro.getData();
	
	private static Player Execution = null;
	
	private static String[] YakuList = new String[]{
			"murabito","uranai","kaitou","kariudo","jinro","kyoujin","tyoukyoujin","yoko"
	};
	private static String[] COList = new String[]{
			"murabito","uranai","kaitou","kariudo","jinro","kyoujin","tyoukyoujin","yoko","siro","kuro"
	};

	public static List<String> getPlayingPlayersName(){
		ArrayList<String> out = new ArrayList<>();
		out.add("null回避だよ・。・");
		if( Data.getConfig().get("Players") != null ){
			Data.getConfig().getConfigurationSection("Players").getKeys(false).forEach((String key) -> {
				out.add(key);
			});
		}
		return out;
	}

	public static String[] getYakuList(){
		return YakuList;
	}

	public static String[] getCOList(){
		return COList;
	}

	public static Player getExecution(){
		return Execution;
	}

	public static void setAmari(int i, OneNightYakusyoku y1) {
		Data.set("NPCs.amari.No" + i, getYakuToName(y1).toString());
		return;
	}

	public static void removeAmari(int i) {
		if( i > 2 ){
			throw new NullPointerException("は？3以上指定するとかなめてんの？");
		}
		Data.set("NPCs.amari.No" + i, null);
		return;
	}

	public static OneNightYakusyoku getAmari(int i) {
		if( i > 2 ){
			throw new NullPointerException("は？3以上指定するとかなめてんの？");
		}
		String a = Data.getString("NPCs.amari.No" + i);
		if(a == null){
			return null;
		}
		return OneNightYakusyoku.getNameToYaku(a);
	}

	public static void addYaku(Player p, OneNightYakusyoku y){
		Data.set("Players."+p.getUniqueId()+".yaku", getYakuToName(y).toString());
		return;
	}
	
	public static void removeYaku(Player p){
		Data.set("Players." + p.getUniqueId() + ".yaku", null);
		
		return;
	}
	
	public static void resetYaku(){
		for(Player p : Bukkit.getOnlinePlayers()) {
			Data.set("Players." + p.getUniqueId() + ".yaku", null);
		}
		
		return;
	}

	public static ArrayList<Player> getAlivePlayers() {
		ArrayList<Player> out = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()){
			boolean a = getDeath(p);
			if( !Data.getBoolean("Players." + p.getUniqueId() + ".Spectator") && !a && !p.hasPermission("axtuki1.Jinro.GameMaster")){
				out.add(p);
			}
		}
		return out;
	}

	public static ArrayList<Player> getDeathPlayers() {
		ArrayList<Player> out = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()){
			boolean a = getDeath(p);
			if( !Data.getBoolean("Players." + p.getUniqueId() + ".Spectator") && a && !p.hasPermission("axtuki1.Jinro.GameMaster")){
				out.add(p);
			}
		}
		return out;
	}

	public static ArrayList<Player> getAllPlayers() {
		ArrayList<Player> out = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()){
			if( !Data.getBoolean("Players." + p.getUniqueId() + ".Spectator") && !p.hasPermission("axtuki1.Jinro.GameMaster")){
				out.add(p);
			}
		}
		return out;
	}

	public static ArrayList<Player> getNEETPlayers() {
		ArrayList<Player> out = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()){
			if(!p.hasPermission("axtuki1.Jinro.GameMaster")){
				if( getYaku(p) == null && !Data.getBoolean("Players." + p.getUniqueId() + ".Spectator") ){
					out.add(p);
				}
			}
		}
		return out;
	}

	public static ArrayList<Player> getSpecPlayers() {
		ArrayList<Player> out = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()){
			if(!p.hasPermission("axtuki1.Jinro.GameMaster")){
				if( Data.getBoolean("Players." + p.getUniqueId() + ".Spectator") ){
					out.add(p);
				}
			}
		}
		return out;
	}

	public static void setExecution(Player p){
		Execution = p;
		return;
	}

	public static boolean getDeath(Player p) {
		return Data.getBoolean("Players." + p.getUniqueId() + ".death");
	}

	public static void setDeath(Player p) {
		Data.set("Players." + p.getUniqueId() + ".death", true);
		p.setPlayerListName(ChatColor.BLACK + "[霊界] "+ p.getName() + " ");
		return;
	}

	public static void removeDeath(Player p) {
		Data.set("Players." + p.getUniqueId() + ".death", false);
		
		return;
	}

	public static OneNightYakusyoku getYaku(Player p){
		if(p == null){
			throw new NullPointerException("'getYaku'関数にnullを渡すことは許されません。(半ギレ)");
		}
		String yaku = Data.getString("Players."+p.getUniqueId()+".yaku");
		OneNightYakusyoku y = null;
		if(yaku != null){
			y = getNameToYaku( yaku );
		}
		return y;
	}
	
	public static String getYakuName(Player p){
		String yaku = Data.getString("Players."+p.getUniqueId()+".yaku");
		if(yaku != null){
			yaku = getNameToYaku( yaku ).toString();
		}
		return yaku;
	}
	
	public static String getYakuNameC(Player p){
		String s = Data.getString("Players."+p.getUniqueId()+".yaku");
		if(s == null){
			return null;
		}
		OneNightYakusyoku yaku = getNameToYaku( s );
		String t = null;
		if(yaku != null){
			t = getYakuColor( yaku ) + yaku.toString();
		}
		return  t;
	}

	public static String getYakuNameC(OneNightYakusyoku y){
		return getYakuColor( y ) + y.toString() ;
	}
	
	public static ChatColor getYakuColor(Player p){
		return getYakuColor( getNameToYaku( Data.getString("Players."+p.getUniqueId()+".yaku") ) );
	}
	
	public static ChatColor getYakuColor(OneNightYakusyoku yaku){
		ChatColor out = null;
		switch(yaku){
			case 村人:
			case 白:
			case 黒:
				out = ChatColor.WHITE;
				break;
			case 人狼:
				out = ChatColor.RED;
				break;
			case 占い師:
				out = ChatColor.GREEN;
				break;
			case 狂人:
				out = ChatColor.BLUE;
				break;
			case 聴狂人:
				out = ChatColor.BLUE;
				break;
			case 怪盗:
				out = ChatColor.DARK_GREEN;
				break;
		}
		return out;
	}
	
	
	public static OneNightYakusyoku getNameToYaku(String name){
		OneNightYakusyoku yaku = null;
		if(name == null){
			yaku = null;
		} else if(name.equalsIgnoreCase("murabito")){
			yaku = 村人;
		} else if(name.equalsIgnoreCase("jinro")){
			yaku = 人狼;
		} else if(name.equalsIgnoreCase("uranai")){
			yaku = 占い師;
		} else if(name.equalsIgnoreCase("kyoujin")){
			yaku = 狂人;
		} else if(name.equalsIgnoreCase("tyoukyoujin")){
			yaku = 聴狂人;
		} else if(name.equalsIgnoreCase("kaitou")){
			yaku = 怪盗;
		} else if(name.equalsIgnoreCase("siro")){
			yaku = 白;
		} else if(name.equalsIgnoreCase("kuro")){
			yaku = 黒;
		}
		return yaku;
	}

	public static String getYakuToName(OneNightYakusyoku name){
		String out = null;
		switch(name){
			case 村人:
				out = "murabito";
				break;
			case 人狼:
				out = "jinro";
				break;
			case 占い師:
				out = "uranai";
				break;
			case 狂人:
				out = "kyoujin";
				break;
			case 聴狂人:
				out = "tyoukyoujin";
				break;
			case 怪盗:
				out = "kaitou";
				break;
			case 白:
				out = "siro";
				break;
			case 黒:
				out = "kuro";
				break;
		}
		return out;
	}

	public static String getYaku2moji(OneNightYakusyoku name){
		String out = null;
		switch(name){
			case 村人:
				out = "村人";
				break;
			case 人狼:
				out = "人狼";
				break;
			case 占い師:
				out = "占い";
				break;
			case 狂人:
				out = "狂人";
				break;
			case 聴狂人:
				out = "聴狂";
				break;
			case 怪盗:
				out = "怪盗";
				break;
			case 白:
				out = "○";
				break;
			case 黒:
				out = "●";
				break;
		}
		return out;
	}

	public static ArrayList<Player> getPlayers(OneNightYakusyoku y){
		ArrayList<Player> pl = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			OneNightYakusyoku ya = getYaku(p);
			if(y == ya){
				pl.add(p);
			}
		}
		return pl;
	}

	public static void SwapYaku(Player p1, Player p2){
		OneNightYakusyoku p1y = getYaku(p1);
		OneNightYakusyoku p2y = getYaku(p2);
		Data.set("Players."+p1.getUniqueId()+".yaku", getYakuToName(p2y));
		Data.set("Players."+p1.getUniqueId()+".beforeyaku", getYakuToName(p1y));
		Data.set("Players."+p2.getUniqueId()+".yaku", getYakuToName(p1y));
		Data.set("Players."+p2.getUniqueId()+".beforeyaku", getYakuToName(p2y));
	}


	public static void setYakuFromTool(JSONArray players, OneNightYakusyoku y) {
		Jinro.getMain().getLogger().info( "===================["+y.toString()+"]" );
		for(Object a : players){
			//System.out.print(p);
			String p = a.toString();
			if( p.equals("amari##") ){
				if( getAmari(1) == null ){
					setAmari(1, y);
				} else if( getAmari(2) == null ){
					setAmari(2, y);
				}
				continue;
			}
			Player pp = Bukkit.getPlayer(p);
			if (pp != null) {
				pp.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + OneNightYakusyoku.getYakuColor(y) + "[" + y.name() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");
				addYaku(pp,y);
				Jinro.getMain().getLogger().info( p );
			} else {
				Jinro.getMain().getLogger().info( p + " [Not Found]" );
				//Jinro.getMain().getLogger().log(Level.WARNING,p + "というプレイヤーは存在しません");
			}
		}
		return;
	}

	public static boolean Admin(CommandSender sender, String commandLabel, String[] args) {
		// jinro_ad yakusyoku 
		Player p = null;
		OneNightYakusyoku yaku = null;
		/*
		for(String a : args){
			sender.sendMessage(a);
		}
		*/
		if( !Status.getStatus().equals(Status.GameStandby) ){
			Jinro.sendMessage(sender, "現在役を振ることはできません。", LogLevel.ERROR );
			return true;
		}

		if (args.length <= 2) {
			sendYakuHelp(sender);
			return true;
		} else {
			if(args[1].equalsIgnoreCase("ToolImport") ){
				if(args.length >= 3){
					JSONObject jsonObject = new JSONObject(Utility.CommandText(args, 2));
					for( String k : YakuList ){
						setYakuFromTool(jsonObject.getJSONArray(k), getNameToYaku(k));
					}
					sender.sendMessage(ChatColor.RED + "===================================");
					sender.sendMessage(ChatColor.GREEN + "役は以下のようにセットされました。");
					String Death = "";
					// ここはてぬき
					StringBuilder amari = new StringBuilder();
					yaku = OneNightYakusyoku.getAmari(1);
					if(yaku != null){
						amari.append(OneNightYakusyoku.getYakuColor(yaku)).append("[").append(yaku.toString()).append("]");
					} else {
						amari.append(ChatColor.GREEN + "[なし]");
					}
					yaku = OneNightYakusyoku.getAmari(2);
					if(yaku != null){
						amari.append(OneNightYakusyoku.getYakuColor(yaku)).append("[").append(yaku.toString()).append("]");
					} else {
						amari.append(ChatColor.GREEN + "[なし]");
					}
					sender.sendMessage(ChatColor.GREEN + "余り : " + amari);
					for(Player pa : OneNightYakusyoku.getAllPlayers()){
						yaku = OneNightYakusyoku.getYaku(pa);
						Death = "";
						if(OneNightYakusyoku.getDeath(pa)){
							Death = ChatColor.GRAY + "(死亡)";
						}
						if(yaku != null){
							sender.sendMessage(ChatColor.GREEN + pa.getName() + " : " + OneNightYakusyoku.getYakuColor(yaku) + "[" + yaku.toString() + "]"+Death);
						} else {
							sender.sendMessage(ChatColor.GREEN + pa.getName() + " : [なし]"+Death);
						}
					}
					sender.sendMessage(ChatColor.RED + "===================================");
					return true;
				}
				Jinro.sendMessage(sender, "フォーマットが不正です。", LogLevel.ERROR);
				return true;
			}
			if(args[2].equalsIgnoreCase("余り") || args[2].equalsIgnoreCase("amari##") ){
				if(args[1].equalsIgnoreCase("del") ){
					Jinro.sendMessage(sender, "余りの役職を削除しました。", LogLevel.SUCCESSFUL);
					removeAmari(1);
					removeAmari(2);
					return true;
				}
				yaku = getNameToYaku(args[1]);
				if(yaku == null){
					sendYakuHelp(sender);
					return true;
				}
				if( getAmari(1) == null ){
					setAmari(1, yaku);
				} else if( getAmari(2) == null ){
					setAmari(2, yaku);
				} else {
					Jinro.sendMessage(sender, "これ以上設定できません。", LogLevel.ERROR);
					return true;
				}
				Jinro.sendMessage(sender, "役職[ " + yaku.name() + " ]を余りに設定しました。", LogLevel.SUCCESSFUL);
				return true;
			}
			p = Utility.getPlayer(args[2]);
			yaku = getNameToYaku(args[1]);
		}
		if(args[1].equalsIgnoreCase("del") ){
			if( getYaku(p) == null ){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.AQUA + p.getName() + "の役職を削除しました。");
				return true;
			}
			removeYaku(p);
			Jinro.sendMessage(p, "あなたの役職は取り消されました", LogLevel.INFO);
			Jinro.sendMessage(sender, p.getName() + "の役職を削除しました。", LogLevel.SUCCESSFUL);
			return true;
		} else if( args[1].equalsIgnoreCase("random") ){
			yaku = getNameToYaku(args[2]);
			ArrayList<Player> out = new ArrayList<Player>();
			int max;
			if(args[3] != null){
				max = Integer.parseInt(args[3]);
			} else {
				max = 1;
			}
			ArrayList<Player> neetplayers = getNEETPlayers();
			if(neetplayers.size() == 0){
				Jinro.sendMessage(sender, "待機プレイヤーがいません。", LogLevel.ERROR);
				return true;
			}
			if(neetplayers.size() < max){
                Jinro.sendMessage(sender, "数が合いません。", LogLevel.ERROR);
                return true;
            }
			for( int i = 0; i < max; i++ ){
				int random = new Random().nextInt(neetplayers.size());
				Player pr = (Player) neetplayers.toArray()[random];
				OneNightYakusyoku.addYaku(pr, yaku);
				if (pr != null) {
					pr.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + OneNightYakusyoku.getYakuColor(yaku) + "[" + yaku.name() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");
				}
				out.add(pr);
				neetplayers.remove(random);
			}
			StringBuilder text = new StringBuilder();
			for(Player pr : out){
				if(text.toString().equalsIgnoreCase("")){
					text = new StringBuilder(pr.getName());
				} else {
					text.append(" ,").append(pr.getName());
				}
			}
			Jinro.sendMessage(sender, text + " を役職[ " + yaku.name() + " ]に設定しました。", LogLevel.SUCCESSFUL);
		} else {
			if (p == null) {
				Jinro.sendMessage(sender, "プレイヤーが見つかりませんでした。", LogLevel.ERROR);
				return true;
			}
			if (yaku == null) {
				sendYakuHelp(sender);
				return true;
			}
			// [DEBUG] sender.sendMessage(p.toString());
			OneNightYakusyoku.addYaku(p, yaku);
			Jinro.sendMessage(sender, p.getName() + " を役職[ " + yaku.name() + " ]に設定しました。", LogLevel.SUCCESSFUL);
			p.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + OneNightYakusyoku.getYakuColor(yaku) + "[" + yaku.name() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");
			return true;
		}
		return true;
	}

	public static void sendYakuHelp(CommandSender sender) {
		Jinro.sendMessage(sender, "役職指定に使用できる文字は以下の通りです。", LogLevel.INFO);
		Jinro.sendMessage(sender, "村人:murabito 占い師:uranai 怪盗:kaitou", LogLevel.INFO);
		Jinro.sendMessage(sender, "狩人:kariudo 共有者:kyouyu 爆弾魔:bakudan", LogLevel.INFO);
		Jinro.sendMessage(sender, "人狼:jinro 狂人:kyoujin 聴狂人:tyoukyoujin", LogLevel.INFO);
	}

	public static void sendYakuHelp( Player sender ){
		sendYakuHelp( ((CommandSender) sender) );
	}
}