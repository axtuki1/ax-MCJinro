package io.github.axtuki1.jinro;

import java.util.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

public enum Yakusyoku {
	村人, 人狼, 占い師, 霊能者, 狂人, 聴狂人, 狂信者, 狩人, 共有者, 妖狐, 爆弾魔, コスプレイヤー, 人形使い, ニワトリ, 白, 黒;
	
	// プレイヤーの役職等 内部処理担当	

	private static Config Data = Jinro.getData();
	
	private static Player Execution = null;
	
	private static String[] YakuList = new String[]{"murabito","uranai","reinou","kariudo","kyouyu","bakudan","cosplayer","ningyou","niwatori","jinro","kyoujin","tyoukyoujin","kyousinja","yoko"};
	private static String[] COList = new String[]{"murabito","uranai","reinou","kariudo","kyouyu","bakudan","cosplayer","ningyou","niwatori","jinro","kyoujin","tyoukyoujin","kyousinja","yoko","siro","kuro"};

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
		switch(GameMode.getGameMode()){
			case OneNightJinro:
				return OneNightYakusyoku.getYakuList();
			case MinecraftJinro:
				return YakuList;
		}
		return YakuList;
	}

	public static String[] getCOList(){
		switch(GameMode.getGameMode()){
			case OneNightJinro:
				return OneNightYakusyoku.getCOList();
			case MinecraftJinro:
				return COList;
		}
		return COList;
	}

	public static Player getExecution(){
		return Execution;
	}

	public static void setSyoniti(Yakusyoku y) {
		Data.set("NPCs.syoniti.yaku", getYakuToName(y).toString());
		
		return;
	}

	public static void removeSyoniti() {
		Data.set("NPCs.syoniti.yaku", null);
		return;
	}

	public static Yakusyoku getSyoniti() {
		String a = Data.getString("NPCs.syoniti.yaku");
		if(a == null){
			return null;
		}
		return Yakusyoku.getNameToYaku(a);
	}

	public static void setMorningDeath(Player p) {
		List<String> n = Data.getStringList("Status.MorningDeath."+Timer.getDay());
		if(n == null){
			n = new ArrayList<String>();
		}
		n.add(p.getName());
		Data.set("Status.MorningDeath."+Timer.getDay(), n);
		
		return;
	}

	public static Player getMorningDeath(int day) {
		String name = Data.getString("Status.MorningDeath."+day);
		Player p = null;
		if(name != null){
			p = Utility.getPlayer( name );
		}
		return p;
	}

	public static void setGoei(Player me, Player p) {
		List<String> n = Data.getStringList("Status.Goei."+Timer.getDay());
		String name = Data.getString("Players." + me.getUniqueId() + ".goei");
		if(n == null){
			n = new ArrayList<String>();
		}
		n.remove( Utility.getPlayer( name ) );
		n.add(p.getName());
		Data.set("Players." + me.getUniqueId() + ".goei", p.getName());
		Data.set("Status.Goei."+Timer.getDay(), n);
		
		return;
	}

	public static boolean getGoei(int day, Player p) {
		List<String> name = Data.getStringList("Status.Goei."+day);
		if(name == null){
			name = new ArrayList<String>();
		}
		return name.contains(p.getName());
	}

	public static Player getGoei_sender(Player p) {
		for(Player sender : Yakusyoku.getAlivePlayers()){
			String name = Data.getString("Players." + sender.getUniqueId() + ".goei");
			if( name != null ){
				if( name.equalsIgnoreCase(p.getName()) ){
					return sender;
				}
			}
		}
		return null;
	}

	public static List<String> getMorningDeathPlayers(int day){
		List<String> name = Data.getStringList("Status.MorningDeath."+day);
		return name;
	}

	public static void addYaku(Player p, Yakusyoku y){
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
		Data.set("Status.Execution." + Timer.getDay() , p.getName());
		
		Execution = p;
		return;
	}

	public static Player getExecution(int day) {
		String a = Data.getString("Status.Execution." + day);
		if( a == null ){
			return null;
		}
		return Utility.getPlayer(a);
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

	public static Yakusyoku getYaku(Player p){
		if(p == null){
			throw new NullPointerException("'getYaku'関数にnullを渡すことは許されません。(半ギレ)");
		}
		String yaku = Data.getString("Players."+p.getUniqueId()+".yaku");
		Yakusyoku y = null;
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
		Yakusyoku yaku = getNameToYaku( s );
		String t = null;
		if(yaku != null){
			t = getYakuColor( yaku ) + yaku.toString();
		}
		return  t;
	}

	public static String getYakuNameC(Yakusyoku y){
		return getYakuColor( y ) + y.toString() ;
	}
	
	public static ChatColor getYakuColor(Player p){
		return getYakuColor( getNameToYaku( Data.getString("Players."+p.getUniqueId()+".yaku") ) );
	}
	
	public static ChatColor getYakuColor(Yakusyoku yaku){
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
			case 霊能者:
				out = ChatColor.AQUA;
				break;
			case 狂人:
				out = ChatColor.BLUE;
				break;
			case 聴狂人:
				out = ChatColor.BLUE;
				break;
			case 狂信者:
				out = ChatColor.BLUE;
				break;
			case 共有者:
				out = ChatColor.GOLD;
				break;
			case 妖狐:
				out = ChatColor.LIGHT_PURPLE;
				break;
			case 爆弾魔:
				out = ChatColor.DARK_PURPLE;
				break;
			case 狩人:
				out = ChatColor.GRAY;
				break;
			case コスプレイヤー:
				out = ChatColor.YELLOW;
				break;
            case 人形使い:
                out = ChatColor.DARK_GREEN;
                break;
			case ニワトリ:
				out = ChatColor.DARK_AQUA;
				break;
		}
		return out;
	}
	
	
	public static Yakusyoku getNameToYaku(String name){
		Yakusyoku yaku = null;
		if(name == null){
			yaku = null;
		} else if(name.equalsIgnoreCase("murabito")){
			yaku = 村人;
		} else if(name.equalsIgnoreCase("jinro")){
			yaku = 人狼;
		} else if(name.equalsIgnoreCase("uranai")){
			yaku = 占い師;
		} else if(name.equalsIgnoreCase("reinou")){
			yaku = 霊能者;
		} else if(name.equalsIgnoreCase("kyoujin")){
			yaku = 狂人;
		} else if(name.equalsIgnoreCase("tyoukyoujin")){
			yaku = 聴狂人;
		} else if(name.equalsIgnoreCase("kyousinja")){
			yaku = 狂信者;
		} else if(name.equalsIgnoreCase("kariudo")){
			yaku = 狩人;
		} else if(name.equalsIgnoreCase("kyouyu")){
			yaku = 共有者;
		} else if(name.equalsIgnoreCase("yoko")){
			yaku = 妖狐;
		} else if(name.equalsIgnoreCase("bakudan")){
			yaku = 爆弾魔;
		} else if(name.equalsIgnoreCase("cosplayer")){
            yaku = コスプレイヤー;
        } else if(name.equalsIgnoreCase("ningyou")) {
			yaku = 人形使い;
		} else if(name.equalsIgnoreCase("niwatori")){
			yaku = ニワトリ;
        } else if(name.equalsIgnoreCase("siro")){
			yaku = 白;
		} else if(name.equalsIgnoreCase("kuro")){
			yaku = 黒;
		}
		return yaku;
	}

	public static String getYakuToName(Yakusyoku name){
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
			case 霊能者:
				out = "reinou";
				break;
			case 狂人:
				out = "kyoujin";
				break;
			case 聴狂人:
				out = "tyoukyoujin";
				break;
			case 狂信者:
				out = "kyousinja";
				break;
			case 共有者:
				out = "kyouyu";
				break;
			case 妖狐:
				out = "yoko";
				break;
			case 爆弾魔:
				out = "bakudan";
				break;
			case 狩人:
				out = "kariudo";
				break;
			case コスプレイヤー:
                out = "cosplayer";
                break;
            case 人形使い:
                out = "ningyou";
                break;
			case ニワトリ:
				out = "niwatori";
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

	public static String getYaku2moji(Yakusyoku name){
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
			case 霊能者:
				out = "霊能";
				break;
			case 狂人:
				out = "狂人";
				break;
			case 聴狂人:
				out = "聴狂";
				break;
			case 狂信者:
				out = "狂信";
				break;
			case 共有者:
				out = "共有";
				break;
			case 妖狐:
				out = "妖狐";
				break;
			case 爆弾魔:
				out = "爆弾";
				break;
			case 狩人:
				out = "狩人";
				break;
			case コスプレイヤー:
				out = "ｺｽﾌﾟﾚ";
				break;
            case 人形使い:
                out = "人形";
                break;
			case ニワトリ:
				out = "ﾆﾜﾄﾘ";
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

	public static ArrayList<Player> getPlayers(Yakusyoku y){
		ArrayList<Player> pl = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			Yakusyoku ya = getYaku(p);
			if(y == ya){
				pl.add(p);
			}
		}
		return pl;
	}


	/**
	 * @param attacker Player
	 * @param hiAttack Player
	 * @param UnknownFlag boolean
	 * @return 襲撃成功
	 */
	public static boolean Kami(Player attacker, Player hiAttack, boolean UnknownFlag) {

		Player p = hiAttack;
		if (p == null) {
			Jinro.sendMessage(attacker, "プレイヤーが見つかりませんでした。", LogLevel.ERROR);
			return true;
		}
		if (p.getName() == attacker.getName()) {
			Jinro.sendMessage(attacker, "自分を噛むことはできません。", LogLevel.ERROR);
			return true;
		}
		if (Yakusyoku.getDeath(Utility.getPlayer(attacker.getName()))) {
			Jinro.sendMessage(attacker, "死亡しています。", LogLevel.ERROR);
			return true;
		}
		if (Cycle.getStatus() != Cycle.Night) {
			Jinro.sendMessage(attacker, "夜ではありません。", LogLevel.ERROR);
			return true;
		}
		if (Timer.getGameElapsedTime() <= 15) {
			attacker.sendMessage(Jinro.getPrefix() + "最初の15秒間は噛むことができません。");
			return true;
		}
		if (Data.getConfig().getBoolean("Status.kami." + Timer.getDay())) {
			Jinro.sendMessage(attacker, "今夜は既に噛んでいます。", LogLevel.ERROR);
			return true;
		}
		if (Yakusyoku.getYaku(p) == Yakusyoku.人狼) {
			Jinro.sendMessage(attacker, "味方を噛むことはできません。", LogLevel.ERROR);
			return true;
		}
		if (Timer.getDay() == 1) {
			Jinro.sendMessage(attacker, "初日は噛むことができません。", LogLevel.ERROR);
			return true;
		}

		for (Player pj : Yakusyoku.getAllPlayers()) {
			if (Yakusyoku.getYaku(pj) == Yakusyoku.人狼) {
				pj.sendMessage(Jinro.getPrefix() + ChatColor.RED + attacker.getName() + " が " + p.getName() + " の家を襲いました...");
			}
		}
		Data.set("Status.kami." + Timer.getDay(), true);
		int i = Stats.getAction(attacker, Stats.action.Kami);
		Stats.setAction(attacker, Stats.action.Kami, (i + 1));

		Yakusyoku yaku = Yakusyoku.getYaku(p);
		boolean kami = true;

		if (Yakusyoku.getGoei(Timer.getDay(), p)) {
			// 護衛
			kami = false;
			int a = Stats.getAction(Yakusyoku.getGoei_sender(p), Stats.action.Goei_Success);
			Stats.setAction(Yakusyoku.getGoei_sender(p), Stats.action.Goei_Success, (a + 1));
			a = Stats.getAction(attacker, Stats.action.Kami_Fail);
			Stats.setAction(attacker, Stats.action.Kami_Fail, (a + 1));
			Bukkit.broadcast(Jinro.getPrefix() + ChatColor.GRAY + "[噛み:護衛成功]" + attacker.getName() + " -->✘[" + Yakusyoku.getGoei_sender(p).getName() + "] " + p.getName() + "(" + Yakusyoku.getYakuColor(yaku) + yaku.toString() + ChatColor.GRAY + ")", "axtuki1.Jinro.GameMaster");
		} else if (Yakusyoku.getYaku(p) == Yakusyoku.人形使い && !Data.getBoolean("Players." + p.getUniqueId() + ".Ningyou.Use")) {
			// 人形を身代わりに
			kami = false;
			Data.set("Players." + p.getUniqueId() + ".Ningyou.Use", true);
			int a = Stats.getAction(attacker, Stats.action.Kami_Fail);
			Stats.setAction(attacker, Stats.action.Kami_Fail, (a + 1));
			Bukkit.broadcast(Jinro.getPrefix() + ChatColor.GRAY + "[噛み:身代わり]" + attacker.getName() + " -->✘ " + p.getName() + ChatColor.GRAY + "(" + Yakusyoku.getYakuColor(yaku) + yaku.toString() + ChatColor.GRAY + ")", "axtuki1.Jinro.GameMaster");
			if (Jinro.getMain().getConfig().getBoolean("ShowUseNingyou")) {
				p.sendMessage(ChatColor.YELLOW + "何者かの襲撃を受けましたが、");
				p.sendMessage(ChatColor.YELLOW + "人形を身代わりにしてなんとかなりました....");
			}
		} else if (Yakusyoku.getYaku(p) == Yakusyoku.妖狐) {
			kami = false;
		} else {
			i = Stats.getAction(attacker, Stats.action.Kami_Success);
			Stats.setAction(attacker, Stats.action.Kami_Success, (i + 1));
			Player goei_sender = Yakusyoku.getGoei_sender(p);
			if (goei_sender != null) {
				int a = Stats.getAction(goei_sender, Stats.action.Goei_Fail);
				Stats.setAction(goei_sender, Stats.action.Goei_Fail, (a + 1));
			}
			Bukkit.broadcast(Jinro.getPrefix() + ChatColor.RED + "[噛み]" + attacker.getName() + " -> " + p.getName() + "(" + Yakusyoku.getYakuColor(yaku) + yaku.toString() + ChatColor.GRAY + ")", "axtuki1.Jinro.GameMaster");
			if (Yakusyoku.getYaku(p) == Yakusyoku.爆弾魔) {
				for (Player pj : Yakusyoku.getAlivePlayers()) {
					if (Yakusyoku.getYaku(pj) == Yakusyoku.人狼) {
						pj.sendMessage(Jinro.getPrefix() + attacker.getName() + " が爆発に巻き込まれて死にました。");
					}
				}
				Yakusyoku.setMorningDeath(attacker);
				Stats.setDeath(attacker, Stats.death.Explosion, (Stats.getDeath(attacker, Stats.death.Explosion) + 1));
				Bukkit.broadcast(Jinro.getPrefix() + ChatColor.RED + "[噛み:爆死]" + p.getName() + "(" + Yakusyoku.getYakuColor(yaku) + yaku.toString() + ChatColor.GRAY + ")" + " -> " + attacker.getName(), "axtuki1.Jinro.GameMaster");
			}
		}
		if (kami) {
			if( UnknownFlag ){
				p.sendMessage(ChatColor.RED + "あなたは何者かの襲撃により殺害されました....");
			} else {
				p.sendMessage(ChatColor.RED + "あなたは噛み殺されました....");
			}
			p.sendMessage(ChatColor.RED + "翌日霊界へご案内します。");
			Data.set("Status.kami." + Timer.getDay(), true);
			Yakusyoku.setMorningDeath(p);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param attacker Player
	 * @return 襲撃成功
	 */
	public static boolean Kami(Player attacker, Player hiAttack){
		return Kami(attacker, hiAttack, false);
	}

	public static void setYakuFromTool(JSONArray players, Yakusyoku y) {
		Jinro.getMain().getLogger().info( "===================["+y.toString()+"]" );
		for(Object a : players){
			//System.out.print(p);
			String p = a.toString();

			if(p.equalsIgnoreCase("初日犠牲者")){
				setSyoniti(y);
				Jinro.getMain().getLogger().info( p );
				return;
			}
			Player pp = Bukkit.getPlayer(p);
			if (pp != null) {
				pp.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + Yakusyoku.getYakuColor(y) + "[" + y.name() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");
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
		Yakusyoku yaku = null;
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
					yaku = Yakusyoku.getSyoniti();
					String Death = "";
					if(yaku != null){
						sender.sendMessage(ChatColor.GREEN + "初日犠牲者 : " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.toString() + "]"+ ChatColor.GRAY +"(死亡)");
					} else {
						sender.sendMessage(ChatColor.GREEN + "初日犠牲者 : [なし]"+ ChatColor.GRAY +"(死亡)");
					}
					for(Player pa : Yakusyoku.getAllPlayers()){
						yaku = Yakusyoku.getYaku(pa);
						Death = "";
						if(Yakusyoku.getDeath(pa)){
							Death = ChatColor.GRAY + "(死亡)";
						}
						if(yaku != null){
							sender.sendMessage(ChatColor.GREEN + pa.getName() + " : " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.toString() + "]"+Death);
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



			if(args[2].equalsIgnoreCase("初日犠牲者") || args[2].equalsIgnoreCase("syoniti##") ){
				if(args[1].equalsIgnoreCase("del") ){
					Jinro.sendMessage(sender, "初日犠牲者の役職を削除しました。", LogLevel.SUCCESSFUL);
					removeSyoniti();
					return true;
				}
				yaku = getNameToYaku(args[1]);
				if(yaku == null){
					sendYakuHelp(sender);
					return true;
				}
				if( ( yaku == Yakusyoku.人狼 || yaku == Yakusyoku.妖狐 ) && !Jinro.getDebug() ){
					Jinro.sendMessage(sender, "初日犠牲者には役職[ " + yaku.name() + " ]を設定できません。", LogLevel.ERROR);
					return true;
				}
				Yakusyoku.setSyoniti(yaku);
				Jinro.sendMessage(sender, "初日犠牲者 を役職[ " + yaku.name() + " ]に設定しました。", LogLevel.SUCCESSFUL);
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
				Yakusyoku.addYaku(pr, yaku);
				if (pr != null) {
					pr.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.name() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");
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
			Yakusyoku.addYaku(p, yaku);
			Jinro.sendMessage(sender, p.getName() + " を役職[ " + yaku.name() + " ]に設定しました。", LogLevel.SUCCESSFUL);
			if (p instanceof Player) {
				p.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.name() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");
			}
			return true;
		}
		return true;
	}

	public static void sendYakuHelp(CommandSender sender) {
		Jinro.sendMessage(sender, "役職指定に使用できる文字は以下の通りです。", LogLevel.INFO);
		Jinro.sendMessage(sender, "村人:murabito 占い師:uranai 霊能者:reinou", LogLevel.INFO);
		Jinro.sendMessage(sender, "狩人:kariudo 共有者:kyouyu 爆弾魔:bakudan", LogLevel.INFO);
		Jinro.sendMessage(sender, "ｺｽﾌﾟﾚｲﾔｰ:cosplayer 人形使い:ningyou 妖狐:yoko ", LogLevel.INFO);
		Jinro.sendMessage(sender, "ニワトリ:niwatori 人狼:jinro 狂人:kyoujin", LogLevel.INFO);
		Jinro.sendMessage(sender, "聴狂人:tyoukyoujin 狂信者:kyousinja", LogLevel.INFO);
	}

	public static void sendYakuHelp( Player sender ){
		sendYakuHelp( ((CommandSender) sender) );
	}
}