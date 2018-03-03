package io.github.axtuki1.jinro;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.error.YAMLException;

public class Jinro extends JavaPlugin {
	
	/*
	 * MinecraftJinro
	 * まじうんこーど
	 */
	
	private static Jinro main;
	private static World world;
	private static boolean DEBUGFLAG;
	static Config Data, Pattern;
	Config challenge;
	//Config Stats;
	private String url, username, password;
	private int port;
	private static SQL sql;
	private static boolean SQLEnable = false;

	@Override
    public void onEnable() {
		main = this;
		world = Bukkit.getWorlds().get(0);

		Data = new Config("data.yml");
		challenge = new Config("challenge.yml");
		Pattern = new Config("ruleset.yml");

		this.saveDefaultConfig();
		Data.saveDefaultConfig();
		Stats.saveDefaultConfig();
		challenge.saveDefaultConfig();
		//Pattern.saveDefaultConfig();

		this.reloadConfig();
		Data.reloadConfig();
		Stats.reloadConfig();
		challenge.reloadConfig();
		//Pattern.reloadConfig();

		SQLEnable = this.getConfig().getBoolean("sql.enable");

		if(SQLEnable){
			url = this.getConfig().getString("sql.url");
			username = this.getConfig().getString("sql.user");
			password = this.getConfig().getString("sql.password");
			sql = new SQL(url,username,password);
			sql.AsyncOpenConnection(this);
			this.getLogger().info("MySQL Connection: true");
		}

		Yakusyoku.resetYaku();
		//Bukkit.broadcastMessage(ChatColor.AQUA + "MinecraftJinro Loaded.");
		getServer().getPluginManager().registerEvents(new Event(), this);
		getServer().getPluginManager().registerEvents(new Challenge(), this);
		if(Jinro.getMain().getConfig().get("spawnpoint.x") == null ||
		Jinro.getMain().getConfig().get("spawnpoint.y") == null ||
				Jinro.getMain().getConfig().get("spawnpoint.z") == null) {
			setRespawnLoc( world.getSpawnLocation() );
			this.getConfig().set("spawnpoint.x", world.getSpawnLocation().getX() );
			this.getConfig().set("spawnpoint.y", world.getSpawnLocation().getY() );
			this.getConfig().set("spawnpoint.z", world.getSpawnLocation().getZ() );
			this.saveConfig();
		} else if( Jinro.getMain().getConfig().get("spawnpoint.yaw") == null ||
		Jinro.getMain().getConfig().get("spawnpoint.pitch") == null ) {
			setRespawnLoc(new Location((World) Bukkit.getWorlds().get(0), Jinro.getMain().getConfig().getDouble("spawnpoint.x"),
					Jinro.getMain().getConfig().getDouble("spawnpoint.y"),
					Jinro.getMain().getConfig().getDouble("spawnpoint.z")
			));
		} else {
			setRespawnLoc(new Location((World) Bukkit.getWorlds().get(0), Jinro.getMain().getConfig().getDouble("spawnpoint.x"),
					Jinro.getMain().getConfig().getDouble("spawnpoint.y"),
					Jinro.getMain().getConfig().getDouble("spawnpoint.z"),
					Float.parseFloat(Jinro.getMain().getConfig().getString("spawnpoint.yaw")),
					Float.parseFloat(Jinro.getMain().getConfig().getString("spawnpoint.pitch"))
			));
		}

		if( Jinro.getMain().getConfig().get("reikai.yaw") == null ||
				Jinro.getMain().getConfig().get("reikai.pitch") == null ) {
			setReikaiLoc(new Location((World)Bukkit.getWorlds().get(0), Jinro.getMain().getConfig().getDouble("reikai.x"),
					Jinro.getMain().getConfig().getDouble("reikai.y"),
					Jinro.getMain().getConfig().getDouble("reikai.z")
			));
		} else {
			setReikaiLoc(new Location((World)Bukkit.getWorlds().get(0), Jinro.getMain().getConfig().getDouble("reikai.x"),
					Jinro.getMain().getConfig().getDouble("reikai.y"),
					Jinro.getMain().getConfig().getDouble("reikai.z"),
					Float.parseFloat(Jinro.getMain().getConfig().getString("reikai.yaw")),
					Float.parseFloat(Jinro.getMain().getConfig().getString("reikai.pitch"))
			));
		}


		Initialization.Admin();
		//Bukkit.broadcastMessage(ChatColor.AQUA + "MinecraftJinro Initialization complete.");
		DEBUGFLAG = this.getConfig().getBoolean("Debug");
		if(getDebug()){
			Bukkit.broadcastMessage(ChatColor.RED + "MinecraftJinro is Debug Mode.");
			ScoreBoard.getInfoObj().getScore( ChatColor.RED + "===[DEBUG MODE]===").setScore(0);
		} else {
			ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===§c");
			ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===");
		}
		// デバッグ
		String status = Jinro.getMain().getConfig().getString("BootStatus"); 
		if(status.equalsIgnoreCase("Playing")){
			Status.setStatus(Status.GamePlaying);
		} else if(status.equalsIgnoreCase("Pause")){
			Status.setStatus(Status.GamePause);
		} else if(status.equalsIgnoreCase("End")){
			Status.setStatus(Status.GameEnd);
		} else {
			Status.setStatus(Status.GameStandby);
		}
		String cycle = Jinro.getMain().getConfig().getString("BootCycle");
		if(cycle.equalsIgnoreCase("Night")){
			Cycle.setStatus(Cycle.Night);
		} else if(cycle.equalsIgnoreCase("Discussion")){
			Cycle.setStatus(Cycle.Discussion);
		} else if(cycle.equalsIgnoreCase("Vote")){
			Cycle.setStatus(Cycle.Vote);
		} else if(cycle.equalsIgnoreCase("Execution")){
			Cycle.setStatus(Cycle.Execution);
		} else {
			Cycle.setStatus(Cycle.Standby);
		}


    }
 
    @Override
    public void onDisable() {
		getSQL().Disconnect();
	}


    public static SQL getSQL(){
		return sql;
	}

	public static boolean getSQLEnable(){
    	return SQLEnable;
	}
    
    public static Jinro getMain() {
	    return main;
	}
    
    public static World getCurrentWorld() {
	    return world;
	}

	public static Config getData(){ return Data; }

	public static Config getRandom(){ return Pattern; }

    public static boolean getDebug(){
		return DEBUGFLAG;
	}

    public static String getPrefix() {
    	FileConfiguration config = main.getConfig();
    	String Prefix = config.getString("Prefix");
    	Prefix = Prefix+"§r ";
  	  	return Prefix;
	}


	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	/*
    	if( DEBUGFLAG ){
    		sender.sendMessage("sender: " + sender.getName());
    		sender.sendMessage("Command: " + cmd);
    		sender.sendMessage("CommandLabel: "+ commandLabel);
			sender.sendMessage("args: "+ args.length);
    		for( int i = 0; i<args.length ;i++){
    			sender.sendMessage("args["+i+"]: "+ args[i]);
			}
		}
		*/
		if(cmd.getName().equalsIgnoreCase("jinro")){
			boolean rep;
			rep = JinroCmd(sender,commandLabel, args);
			return rep;
		}
		if(cmd.getName().equalsIgnoreCase("jinro_ad")){
			boolean rep;
			rep = JinroAdminCmd(sender,commandLabel, args);
			return rep;
		}
		return false;
	}
    
    private static String[] AdminCmdList = new String[]{
    	"start" /* ,"stop" MEMO:誤爆防止 */,"pause","initialization","touhyou","yakusyoku","co","reload","debug","option","tp","next","open","list","spec","challenge"};
	
	private static String[] getAdminCmdList(){
		return AdminCmdList;
	}
	
	private static String[] CmdList = new String[]{"co","touhyou","uranai","kami","goei","chat","list"};
	
	public static String[] getCmdList(){
		return CmdList;
	}

	private static ArrayList<String> getCmdList(Yakusyoku yaku) {
		ArrayList<String> out = new ArrayList<String>();
		if(yaku == Yakusyoku.人狼){
			out.add("kami");
		}
		if(yaku == Yakusyoku.占い師){
			out.add("uranai");
		}
		if(yaku == Yakusyoku.狩人 || yaku == Yakusyoku.コスプレイヤー){
			out.add("goei");
		}
		out.add("chat");
		out.add("co");
		out.add("touhyou");
		out.add("challenge");
		out.add("list");
		out.add("stats");
		out.add("option");
		out.add("about");
		return out;
	}

	private boolean JinroAdminCmd(CommandSender sender, String commandLabel, String[] args) {
		String arg0 = "";
		if(!sender.hasPermission("axtuki1.Jinro.GameMaster")){
			sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "このコマンドを実行する権限がありません。");
			return false;
		}
		if (args.length == 0) {
			arg0 = "";
		} else {
			arg0 = args[0];
		}
		if(arg0.equalsIgnoreCase("start")){
			if(Status.getStatus() == Status.GameStandby){
				if( Yakusyoku.getSyoniti() == null ){
					sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "初日犠牲者に役を振っていません。");
					return true;
				}
				for(Player p : Bukkit.getOnlinePlayers()){
					if( Yakusyoku.getYaku( p ) == null && !p.hasPermission("axtuki1.Jinro.GameMaster") && !Data.getBoolean("Players." + p.getName() + ".Spectator")){
						sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "まだ役を振っていない人がいます。");
						return true;
					}
				}
				if(SQLEnable){
					//getSQL().AsyncOpenConnection(this);
					//this.getLogger().info("MySQL Connection: true");
				}
				Status.setStatus(Status.GamePlaying);
				Timer.setTimerStopFlag(false);
				new Timer().runTaskTimer(this, 5, 5);
                getCurrentWorld().setTime(15000);
                int playerC = 1;
                for(Player p : Bukkit.getOnlinePlayers()){
                	if(!p.hasPermission("axtuki1.Jinro.GameMaster")){
						if( Data.getBoolean("Players." + p.getName() + ".Spectator") ){
							p.sendMessage(ChatColor.GREEN + "観戦モードです。");
							p.sendMessage(ChatColor.GREEN + "チャットは一切できません。");
							p.setGameMode(GameMode.SPECTATOR);
						} else {
							playerC++;
						}
					} else {
						Data.set("Players." + p.getName() + ".GameMaster", true);
					}
					Stats.setPlayerData(p);
				}
				for( Yakusyoku y : Yakusyoku.values() ){
					Stats.setPlay(y);
				}
				Data.set("Status.Alive", playerC);
				Data.set("Status.Death", 0);
				if(DEBUGFLAG){
					ScoreBoard.getInfoObj().getScore( ChatColor.RED + "===[DEBUG MODE]===§c").setScore(4);
					ScoreBoard.getInfoObj().getScore( ChatColor.RED + "===[DEBUG MODE]===").setScore(0);
				} else {
					ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===§c");
					ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===");
				}
				ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ playerC +"人").setScore(2);
				ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: 0人").setScore(1);

				sender.sendMessage(Jinro.getPrefix() + "ゲームを開始しました。");
				Timer.NextCycle();
			} else if(Status.getStatus() == Status.GameEnd){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED +"ゲームは既に終了しています。");
			} else {
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED +"既にゲームは開始されています。");
			}
			return true;
		} else if(arg0.equalsIgnoreCase("stop")){
			if(Status.getStatus() == Status.GameStandby){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED +"ゲームはまだ開始されていません。");
			} else if(Status.getStatus() == Status.GameEnd) {
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED +"ゲームは既に終了しました。");
			} else {
				Timer.setGameStopFlag(true);
			}
			return true;
		} else if(arg0.equalsIgnoreCase("pause")) {
            if(args.length == 1){
                sender.sendMessage("/jinro_ad pause timer | タイマーの一時停止を切り替えます。");
                sender.sendMessage("/jinro_ad pause cycle | 進行の一時停止を切り替えます。(タイマーは止まりません)");
            } else if(args.length == 2){
                if(args[1].equalsIgnoreCase("timer")){
                    if( Timer.getTimerPauseFlag() ){
                        sender.sendMessage(getPrefix() + "タイマーの一時停止を解除しました。");
                        Timer.setTimerPauseFlag(false);
                    } else {
                        sender.sendMessage(getPrefix() + "タイマーを一時停止しました。");
                        Timer.setTimerPauseFlag(true);
                    }
                } else if(args[1].equalsIgnoreCase("cycle")){
                    if( Cycle.getStatus() == Cycle.Vote ){
                        sender.sendMessage(getPrefix() + "投票時間は編集できません。");
                        return true;
                    }
                    if( Timer.getCyclePauseFlag() ){
                        sender.sendMessage(getPrefix() + "進行の一時停止を解除しました。");
                        Timer.setCyclePauseFlag(false);
                    } else {
                        sender.sendMessage(getPrefix() + "進行を一時停止しました。");
                        Timer.setCyclePauseFlag(true);
                    }
                } else {
                    if( Timer.getTimerPauseFlag() ){
                        sender.sendMessage(getPrefix() + "タイマーの一時停止を解除しました。");
                        Timer.setTimerPauseFlag(false);
                    } else {
                        sender.sendMessage(getPrefix() + "タイマーを一時停止しました。");
                        Timer.setTimerPauseFlag(true);
                    }
                }
            }
            return true;
		} else if(arg0.equalsIgnoreCase("initialization")){
			boolean rep = Initialization.Admin(sender,commandLabel, args);
			return rep;
		} else if(arg0.equalsIgnoreCase("Spec")){
			//Data.set("Players." + p.getName() + ".Spectator", true);
			if( args.length == 1 ){
				sendMessage(sender, "プレイヤー名を指定してください。", LogLevel.ERROR, true);
				return true;
			}
			Player p = Bukkit.getPlayer(args[1]);
			if( p == null ){
				sendMessage(sender, "プレイヤーはオフラインです。", LogLevel.ERROR, true);
				return true;
			}
			if( !Status.getStatus().equals( Status.GameStandby ) ){
				sendMessage(sender, "現在設定できません。", LogLevel.ERROR, true);
				return true;
			}
			if( args.length == 3 ){
				if( args[2].equalsIgnoreCase("join") ){
					sendMessage(sender, ""+p.getName()+" を参加者モードに設定しました。", LogLevel.SUCCESSFUL, true);
					p.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "参加モードになりました。");
					Data.set("Players." + p.getName() + ".Spectator", null);
					return true;
				} else if( args[2].equalsIgnoreCase("spec") ){
					sendMessage(sender, ""+p.getName()+" を観戦モードに設定しました。", LogLevel.SUCCESSFUL, true);
					p.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "観戦モードになりました。");
					Data.set("Players." + p.getName() + ".Spectator", true);
					return true;
				}
			} else {
				boolean spec = Data.getBoolean("Players." + p.getName() + ".Spectator");
				if( spec ){
					sendMessage(sender, ""+p.getName()+" を参加者モードに設定しました。", LogLevel.SUCCESSFUL, true);
					p.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "参加モードになりました。");
					Data.set("Players." + p.getName() + ".Spectator", null);
					return true;
				} else {
					sendMessage(sender, ""+p.getName()+" を観戦モードに設定しました。", LogLevel.SUCCESSFUL, true);
					p.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "観戦モードになりました。");
					Data.set("Players." + p.getName() + ".Spectator", true);
					return true;
				}
			}
			return true;
		} else if(arg0.equalsIgnoreCase("next")){
			if(Status.getStatus() == Status.GameStandby){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED +"まだゲームが開始されていません。");
				return true;
			}
			if(Status.getStatus() == Status.GameEnd){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED +"ゲームは既に終了しています。");
				return true;
			}
			if(args.length == 2){
				if(Cycle.getStatus() == Cycle.Vote && !args[1].equalsIgnoreCase("force")){
					sender.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "現在、投票時間です。");
					sender.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "投票をスキップする場合は、以下のコマンドを実行してください");
					sender.sendMessage(Jinro.getPrefix() + ChatColor.AQUA + "/jinro_ad next force");
					return true;
				}
				if(args[1].equalsIgnoreCase("force") && Cycle.getStatus() == Cycle.Vote){
					// 投票スキップ
					Cycle.setStatus(Cycle.Execution);
				}
			}
			if(Cycle.getStatus() == Cycle.Vote){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "現在、投票時間です。");
				sender.sendMessage(Jinro.getPrefix() + ChatColor.GREEN + "投票をスキップする場合は、以下のコマンドを実行してください");
				sender.sendMessage(Jinro.getPrefix() + ChatColor.AQUA + "/jinro_ad next force");
				return true;
			}
			Timer.NextCycle();
			return true;
		} else if(arg0.equalsIgnoreCase("co")){
			if(args.length == 1){
				sender.sendMessage(ChatColor.AQUA + "/jinro_ad co <Player> "+ ChatColor.GREEN +"指定したプレイヤーの役職を確認します。");
				sender.sendMessage(ChatColor.AQUA + "/jinro_ad co <Player> <Yakusyoku> "+ ChatColor.GREEN +"指定したプレイヤーを強制的にCOさせます。");
				sender.sendMessage(ChatColor.AQUA + "/jinro_ad co <Player> del "+ ChatColor.GREEN +"指定したプレイヤーのCOを解除します。");
			} else if(args.length == 2){
				Player p = Utility.getPlayer(args[1]);
				if(p == null){
					sender.sendMessage(getPrefix()  + ChatColor.RED +  "指定したプレイヤーが見つかりません。");
					return true;
				}
				sender.sendMessage(args[1] + "の情報");
				Yakusyoku yaku = ComingOut.getComingOut(p);
				String yakut =  "";
				if(yaku == null){
					yakut = "なし";
				}
				sender.sendMessage("カミングアウト: " + yakut);
				yaku = Yakusyoku.getYaku(p);
				if(yaku == null){
					yakut = "なし";
				}
				sender.sendMessage("本来の役職: " + yakut);
			} else if(args.length == 3){
				Player p = Utility.getPlayer(args[1]);
				if(p == null){
					sender.sendMessage(getPrefix() + ChatColor.RED +  "指定したプレイヤーが見つかりません。");
					return true;
				}
				if(args[2].equalsIgnoreCase("del")){
					ComingOut.removeComingOut(p);
					p.setPlayerListName(p.getName() + " ");
					sender.sendMessage(getPrefix() + args[1] + "のカミングアウトを取り消しました。");
				} else {
					if(Yakusyoku.getNameToYaku(args[2]) == null){
						ComingOut.sendCOHelp(sender);
					} else if(args[2].equalsIgnoreCase("siro")) {
						if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")) {
							p.setPlayerListName("[○] " + p.getName() + " ");
						}
						sender.sendMessage(Jinro.getPrefix() + "白をつけました。");
						ComingOut.setComingOut(p,Yakusyoku.白);
					} else if(args[2].equalsIgnoreCase("kuro")){
						if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")) {
							p.setPlayerListName("[●] " + p.getName() + " ");
						}
						sender.sendMessage(Jinro.getPrefix() + "黒をつけました。");
						ComingOut.setComingOut(p,Yakusyoku.黒);
					} else {
						Yakusyoku yaku = Yakusyoku.getNameToYaku(args[2]);
						ComingOut.setComingOut(p, yaku);
						Bukkit.broadcastMessage(Yakusyoku.getYakuColor(yaku) + p.getName() + "が" + yaku.toString() + "COしました。");
						if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
							p.setPlayerListName(Yakusyoku.getYakuColor(yaku) + "[" + Yakusyoku.getYaku2moji(yaku) + "] " + ChatColor.WHITE + p.getName() + " ");
						}
					}
				}
			}
			return true;
		} else if(arg0.equalsIgnoreCase("touhyou")){
			boolean rep = Touhyou.Admin(sender,commandLabel, args);
			return rep;
		} else if(arg0.equalsIgnoreCase("yakusyoku")){
			boolean rep = Yakusyoku.Admin(sender,commandLabel, args);
			return rep;
		} else if(arg0.equalsIgnoreCase("reload")){
			this.reloadConfig();
			Data.reloadConfig();
			DEBUGFLAG = this.getConfig().getBoolean("Debug");
			SQLEnable = this.getConfig().getBoolean("sql.enable");
			if(getDebug()){
				Bukkit.broadcastMessage(ChatColor.RED + "MinecraftJinro is Debug Mode.");
				ScoreBoard.getInfoObj().getScore( ChatColor.RED + "===[DEBUG MODE]===").setScore(0);
			} else {
				ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===§c");
				ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "===[DEBUG MODE]===");
			}
			if(SQLEnable){
				url = this.getConfig().getString("sql.url");
				username = this.getConfig().getString("sql.user");
				password = this.getConfig().getString("sql.password");
				sql = new SQL(url,username,password);
				sql.AsyncOpenConnection(this);
				this.getLogger().info("MySQL Connection: true");
			}
			sender.sendMessage(Jinro.getPrefix() + "Configを再読込しました。");
			return true;
		} else if(arg0.equalsIgnoreCase("kill")){
			if(args.length == 1){
				sender.sendMessage(getPrefix() + ChatColor.RED + "プレイヤーを指定してください。");
			} else if(args.length == 2){
				Player p = Bukkit.getPlayer(args[1]);
				if(p == null){
					if(Data.get("Players." + args[1]) == null){
						sender.sendMessage(getPrefix() + ChatColor.RED + "このプレイヤーは参加していません。");
						return true;
					}
					Bukkit.broadcastMessage(ChatColor.DARK_RED + p.getName() + " さんが無残な姿で発見されました。");
					Data.set("Players." + args[1] + ".death", true);
					int Alive = Data.getConfig().getInt("Status.Alive");
					int Death = Data.getConfig().getInt("Status.Death");
					Data.set("Status.Alive", Alive - 1);
					Data.set("Status.Death", Death + 1);
					ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
					ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
					ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人").setScore(2);
					ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ (Death + 1) +"人").setScore(1);
					Timer.WinnerCheck();
					return true;
				}
				if(Yakusyoku.getDeath(p)) {
					sender.sendMessage(getPrefix() + ChatColor.RED + "指定したプレイヤーは既に死亡しています。");
					return true;
				}
				Bukkit.broadcastMessage(ChatColor.DARK_RED + p.getName() + " さんが無残な姿で発見されました。");
				Yakusyoku.setDeath(p);
				int Alive = Data.getConfig().getInt("Status.Alive");
				int Death = Data.getConfig().getInt("Status.Death");
				Data.set("Status.Alive", Alive - 1);
				Data.set("Status.Death", Death + 1);
				ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
				ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
				ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人").setScore(2);
				ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ (Death + 1) +"人").setScore(1);
				Timer.WinnerCheck();
				TeleportToReikai(p);
			}
			return true;
		} else if(arg0.equalsIgnoreCase("debug")){
			boolean debugrep = Debug.Admin(sender,commandLabel, args);
			return debugrep;
		} else if(arg0.equalsIgnoreCase("setup")){
			boolean debugrep = Setup.Admin(sender,commandLabel, args);
			return debugrep;
		} else if(arg0.equalsIgnoreCase("option")){
			Setting.Command(sender, commandLabel, args);
			return true;
		} else if(arg0.equalsIgnoreCase("head")){
			if (args.length == 1) {
				sendMessage(sender, "プレイヤーを指定してください。", LogLevel.ERROR);
			} else if (args.length >= 2) {
				Player p = (Player) sender;
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta)item.getItemMeta();
				meta.setDisplayName(ChatColor.RESET + args[1] + " の頭");
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.RESET.toString() + ChatColor.RED.toString() + "良い心地はしないな....");
				meta.setLore(lore);
				meta.setOwner(args[1]);
				item.setItemMeta(meta);
				p.getInventory().addItem(item);
				p.updateInventory();
			}
			return true;
		} else if(arg0.equalsIgnoreCase("tp")){
			if(args[1].equalsIgnoreCase("reikai")){
				Jinro.TeleportToReikai(Utility.getPlayer(sender.getName()));
				sendMessage(sender, "霊界へテレポートしました。", LogLevel.INFO);
			}
			if(args[1].equalsIgnoreCase("spawn")){
				Jinro.TeleportToRespawn(Utility.getPlayer(sender.getName()));
				sendMessage(sender, "スポーンポイントへテレポートしました。", LogLevel.INFO);
			}
			if(args[1].equalsIgnoreCase("all")){
				Player me = Utility.getPlayer( sender.getName() );
				for(Player p : Bukkit.getOnlinePlayers()){
					if( p == me ){
						continue;
					}
					p.teleport( me );
				}
				sendMessage(sender, "全員をあなたにテレポートしました。", LogLevel.INFO);
			}
			return true;
		} else if(arg0.equalsIgnoreCase("guide")){
			Help.guideAdmin(sender, commandLabel, args);
			return true;
		} else if(arg0.equalsIgnoreCase("check")){
			Player p = (Player)sender;
			sender.sendMessage( p.getInventory().getItemInMainHand().getType().toString() );
			return true;
		} else if(arg0.equalsIgnoreCase("test")){
			Player p = (Player) sender;
			try {
				Class<?> CPClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftPlayer");
				Object CraftPlayer = CPClass.cast(p);
				for( Field f : CraftPlayer.getClass().getDeclaredFields() ){
					f.setAccessible(true);
					sender.sendMessage( f.getName() + " -> " + f.get(CraftPlayer) );
				}
				sender.sendMessage(p.getListeningPluginChannels().toString());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			p.sendRawMessage("");
			return true;
		} else if(arg0.equalsIgnoreCase("ping")){
			if(args.length == 1){
				sendMessage(sender, "Ping: " + getPing( (Player)sender ) + "ms", LogLevel.INFO,true);
			} else {
				Player p = Bukkit.getPlayer(args[1]);
				if(p == null){
					sendMessage(sender, "指定したプレイヤーはオフラインです。", LogLevel.ERROR,true);
					return true;
				}
				sendMessage(sender, p.getName()+"のPing: " + getPing( p ) + "ms", LogLevel.INFO,true);
			}
			return true;
		} else if(arg0.equalsIgnoreCase("login")){
			if(Jinro.getMain().getConfig().getBoolean("LoginAttention.enable")){
				if(!Jinro.getMain().getConfig().getString("LoginAttention.title").equalsIgnoreCase("") && Jinro.getMain().getConfig().getString("LoginAttention.title") != null){
					sender.sendMessage(Jinro.getMain().getConfig().getString("LoginAttention.title"));
				}
				sender.sendMessage(Jinro.getMain().getConfig().getString("LoginAttention.msg"));
			}
			return true;
		} else if(arg0.equalsIgnoreCase("open")){
			if(Status.getStatus() != Status.GameEnd){
				sendMessage(sender, "ゲームが終了されていません。", LogLevel.ERROR);
				return true;
			}
			Bukkit.broadcastMessage(ChatColor.RED + "===================================");
			Bukkit.broadcastMessage(ChatColor.GREEN + "本ゲームの役職は以下の通りです。");
			Yakusyoku yaku = Yakusyoku.getSyoniti();
			String Death = "";
			if(yaku != null){
				Bukkit.broadcastMessage(ChatColor.GREEN + "初日犠牲者 : " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.toString() + "]"+ ChatColor.GRAY +"(死亡)");
			} else {
				Bukkit.broadcastMessage(ChatColor.GREEN + "初日犠牲者 : [なし]"+ ChatColor.GRAY +"(死亡)");
			}
			for(Player p : Yakusyoku.getAllPlayers()){
				yaku = Yakusyoku.getYaku(p);
				Death = "";
				if(Yakusyoku.getDeath(p)){
					Death = ChatColor.GRAY + "(死亡)";
				}
				if(yaku != null){
					Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " : " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.toString() + "]"+Death);
				} else {
					Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " : [なし]"+Death);
				}
			}
			Bukkit.broadcastMessage(ChatColor.RED + "===================================");
			return true;
		} else if(arg0.equalsIgnoreCase("challenge")) {
			if(args.length == 1){
				return true;
			} else if(args.length == 3){
				HashMap<String, HashMap<String, Object>> a = Challenge.getChallengeList();
				for(String key : a.keySet()) {
					if (key.toString().equalsIgnoreCase(args[2])){
						Player p = Bukkit.getPlayerExact(args[1]);
						boolean is = Stats.getChallenge(p, args[2]);
						if (is) {
							Stats.setChallenge(p, args[2], false);
							sendMessage(sender, p.getName() + " の実績 " + ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN +"] を剥奪しました。", LogLevel.SUCCESSFUL);
						} else {
							Jinro.sendMessage(p, ChatColor.WHITE + "実績 " + ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN + "] " + ChatColor.WHITE + "を達成しました！", LogLevel.SUCCESSFUL);
							Stats.setChallenge(p, key, true);
							for (Player pall : Bukkit.getOnlinePlayers()) {
								if (pall != p) {
									Jinro.sendMessage(pall, ChatColor.WHITE + p.getName() + " が実績 " + ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN + "] " + ChatColor.WHITE + "を達成しました！", LogLevel.SUCCESSFUL);
								}
							}
							Stats.setChallenge(p, args[2], true);
							sendMessage(sender, p.getName() + " の実績 " + ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN + "] を付与しました。", LogLevel.SUCCESSFUL);
						}
					}
				}
				return true;
			} else if(args.length == 4){
				HashMap<String, HashMap<String, Object>> a = Challenge.getChallengeList();
				for(String key : a.keySet()) {
					if (key.toString().equalsIgnoreCase(args[2])){
						Player p = Bukkit.getPlayerExact(args[1]);
						boolean is = Boolean.valueOf(args[3]);
						if (!is) {
							Stats.setChallenge(p, args[2], false);
							sendMessage(sender, p.getName() + " の実績 " + ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN +"] を剥奪しました。", LogLevel.SUCCESSFUL);
						} else {
							if(!Stats.getChallenge(p, args[2])){
								Jinro.sendMessage(p,ChatColor.WHITE +"実績 "+ ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN +"] " + ChatColor.WHITE +"を達成しました！", LogLevel.SUCCESSFUL);
								Stats.setChallenge(p, key,true);
								for(Player pall : Bukkit.getOnlinePlayers()){
									if(pall != p){
										Jinro.sendMessage(pall,ChatColor.WHITE + p.getName() +" が実績 "+ ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN +"] " + ChatColor.WHITE +"を達成しました！", LogLevel.SUCCESSFUL);
									}
								}
							}
							Stats.setChallenge(p, args[2], true);
							sendMessage(sender, p.getName() + " の実績 " + ChatColor.GREEN + "[" + a.get(key).get("Title") + ChatColor.GREEN +"] を付与しました。", LogLevel.SUCCESSFUL);
						}
					}
				}
				return true;
			}
			return true;
		} else if(arg0.equalsIgnoreCase("list")) {
			sender.sendMessage(ChatColor.RED + "================================================");
			sender.sendMessage(ChatColor.GOLD + "[Status]");
			Data.reloadConfig();
			int Alivec = Data.getInt("Status.Alive");
			int Deathc = Data.getInt("Status.Death");
			if( Status.getStatus().equals(Status.GameStandby) ){
				// do nothing.
			} else if( Timer.getDay() == 1 ){
				Alivec--;
			} else {
				Deathc--;
			}
			int JinroC = 0;
			int JinroKyoC = 0;
			int MuraC = 0;
			int otherC = 0;
			int AllC = 0;
			Yakusyoku yaku = Yakusyoku.getSyoniti();
			for(Player p : Yakusyoku.getAlivePlayers()){
				Yakusyoku p_yaku = Yakusyoku.getYaku(p);
				if( p_yaku == null ){
				} else if( p_yaku.equals(Yakusyoku.人狼) ){
					JinroC++;
					JinroKyoC++;
					AllC++;
				} else if( p_yaku.equals(Yakusyoku.村人) || p_yaku.equals(Yakusyoku.狩人)
						|| p_yaku.equals(Yakusyoku.占い師) || p_yaku.equals(Yakusyoku.共有者) || p_yaku.equals(Yakusyoku.人形使い)
						|| p_yaku.equals(Yakusyoku.爆弾魔) || p_yaku.equals(Yakusyoku.霊能者) || p_yaku.equals(Yakusyoku.コスプレイヤー)
						|| p_yaku.equals(Yakusyoku.ニワトリ) ){
					MuraC++;
					AllC++;
				} else if( p_yaku.equals(Yakusyoku.狂人) ) {
					JinroKyoC++;
					AllC++;
				} else if( p_yaku.equals(Yakusyoku.妖狐) ){
					otherC++;
					AllC++;
				}
			}
			sender.sendMessage(ChatColor.GOLD + "総参加人数: " + ChatColor.YELLOW + AllC + "人 " + ChatColor.AQUA + "生存人数: " + ChatColor.YELLOW + Alivec + "人 " +
					ChatColor.RED + "死亡人数: " + ChatColor.YELLOW + Deathc  + "人 ");
			sender.sendMessage(ChatColor.GREEN + "生存村人陣営: " + ChatColor.YELLOW + MuraC + "人 " +
					ChatColor.RED + "生存人狼陣営: " + ChatColor.YELLOW + JinroKyoC + "人 " +
					ChatColor.LIGHT_PURPLE + "生存第三陣営: " + ChatColor.YELLOW + otherC + "人 ");
			int nawa = 0;
			nawa = (Alivec - 1) / 2;
			String nawa_s = "";
			if( nawa == 0 ){
				nawa_s = "N/A";
			} else {
				nawa_s = nawa + "";
			}
			sender.sendMessage(ChatColor.GOLD + "推定縄数: " + ChatColor.YELLOW + nawa_s + "");
			sender.sendMessage(ChatColor.GOLD + "[Players]");
			boolean ready = true;
			String Death = "";
			if(yaku != null){
				sender.sendMessage(ChatColor.GREEN + "初日犠牲者 : " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.toString() + "]"+ ChatColor.GRAY +"(死亡)");
			} else {
				sender.sendMessage(ChatColor.GREEN + "初日犠牲者 : [なし]"+ ChatColor.GRAY +"(死亡)");
				ready = false;
			}
			for(Player p : Yakusyoku.getAllPlayers()){
				yaku = Yakusyoku.getYaku(p);
				Death = "";
				if(Yakusyoku.getDeath(p)){
					Death = ChatColor.GRAY + "(死亡)";
				}
				if(yaku != null){
					sender.sendMessage(ChatColor.GREEN + p.getName() + " : " + Yakusyoku.getYakuColor(yaku) + "[" + yaku.toString() + "]"+Death);
				} else {
					sender.sendMessage(ChatColor.GREEN + p.getName() + " : [なし]"+Death);
					ready = false;
				}
			}
			if( Status.getStatus().equals(Status.GamePlaying) ){
				sender.sendMessage(ChatColor.GREEN + "村人陣営が " + ( MuraC - JinroC ) + "人 死亡したら人狼の勝利");
			} else {
				if(ready){
					sender.sendMessage(ChatColor.GOLD + "/jinro_ad start でゲームを開始できます。");
					if( AllC <= 4 ){
						sender.sendMessage(ChatColor.RED + "クソゲーの予感がする....");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "役を振っていないプレイヤーがいます。");
				}
			}
			sender.sendMessage(ChatColor.RED + "================================================");
			return true;
		} else if( arg0.equalsIgnoreCase("help") || arg0.equalsIgnoreCase("?") ) {
			sender.sendMessage(ChatColor.RED + "========== " + getPrefix() + ChatColor.RED + "==========");
			sendCmdHelp(sender, "/jinro_ad start", "ゲームを開始します。");
			sendCmdHelp(sender, "/jinro_ad pause", "ゲームタイマーを一時停止させます。");
			sendCmdHelp(sender, "/jinro_ad stop", "ゲームを強制終了させます。");
			sendCmdHelp(sender, "/jinro_ad next", "次へ飛ばします。");
			sendCmdHelp(sender, "/jinro_ad initialization", "初期化します。");
			sendCmdHelp(sender, "/jinro_ad kill <Player>", "突然死させます。(ログアウト等ゲームが進行できなくなった場合等)");
			sendCmdHelp(sender, "/jinro_ad yakusyoku <...>", "役職に関するコマンドです。");
			sendCmdHelp(sender, "/jinro_ad list", "状況を表示します。");
			sendCmdHelp(sender, "/jinro_ad touhyou <...>", "投票に関するコマンドです。");
			sendCmdHelp(sender, "/jinro_ad co <...>", "カミングアウトに関するコマンドです。");
			sendCmdHelp(sender, "/jinro_ad setup <reikai | spawn>", "スポーンポイントの設定をします。");
			sendCmdHelp(sender, "/jinro_ad tp <reikai | spawn | all>", "テレポートします。");
			sendCmdHelp(sender, "/jinro_ad option <...>", "ゲームルールについての設定をします。");
			sendCmdHelp(sender, "/jinro_ad head <Player>", "プレイヤーの頭を取得します。");
			sendCmdHelp(sender, "/jinro_ad open", "[ゲーム終了後] 直前のゲームの役職を発表します。");
			sendCmdHelp(sender, "/jinro_ad reload", "全ファイルの再読込を行います。");
			sendMessage(sender, "stop, setupはTab補完がありません。", LogLevel.INFO, false);
			return true;
		} else {
			sendMessage(sender, "そのコマンドは使えないみたいです。", LogLevel.ERROR, true);
			sendMessage(sender, "'/jinro_ad help'で確認してください。", LogLevel.ERROR, true);
			return true;
		}
	}

	private boolean JinroCmd(CommandSender sender, String commandLabel, String[] args) {
		this.reloadConfig();
		Data.reloadConfig();
		String arg0 = "";
		if (args.length == 0) {
			arg0 = "";
		} else {
			arg0 = args[0];
		}
		if(arg0.equalsIgnoreCase("touhyou")){
			if(Status.getStatus() != Status.GamePlaying && Cycle.getStatus() != Cycle.Execution && !Jinro.getDebug()){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "まだ使用できません。");
				return true;
			}
			if(args.length == 1){
				sendMessage(sender, "プレイヤーを指定してください。", LogLevel.ERROR);
			} else if(args.length == 2) {
				Player p = Utility.getPlayer( args[1] );
				if(p == null){
					sendMessage(sender, "プレイヤーが見つかりませんでした。", LogLevel.ERROR);
					return true;
				}
				if(sender.getName() == p.getName() && !DEBUGFLAG){
					sendMessage(sender, "自分自身に投票できません。", LogLevel.ERROR);
					return true;
				}
				if(Yakusyoku.getDeath( Utility.getPlayer(sender.getName()) )){
					sendMessage(sender, "死亡しています。", LogLevel.ERROR);
					return true;
				}
				if(Utility.getPlayer( sender.getName() ).getInventory().getItemInMainHand().getType() == Material.PAPER){
					if(!p.hasPermission("axtuki1.Jinro.GameMaster")) {
						Touhyou.setTouhyou(Utility.getPlayer(sender.getName()), p);
						Utility.getPlayer(sender.getName()).getInventory().setItemInMainHand(new ItemStack(Material.AIR));
						Utility.getPlayer(sender.getName()).updateInventory();
						sendMessage(sender,  p.getName() +" に投票しました。", LogLevel.SUCCESSFUL);
						Bukkit.broadcast(getPrefix() + ChatColor.GREEN + sender.getName() + " -> " + p.getName() + " ("+ Touhyou.getHiTouhyou(p) +"票)", "axtuki1.Jinro.GameMaster");
					} else {
						sender.sendMessage(getPrefix() + ChatColor.RED + "ゲームマスターに投票はできません。");
					}
				} else {
					sender.sendMessage(getPrefix() + ChatColor.RED + "紙を持ってから実行してください。");
				}
			}
			return true;
		} else if(arg0.equalsIgnoreCase("co")){
			if(Status.getStatus() != Status.GamePlaying && Cycle.getStatus() != Cycle.Discussion && !Jinro.getDebug()){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "まだ使用できません。");
				return true;
			}
			if(Yakusyoku.getDeath( Utility.getPlayer(sender.getName()) )){
				sendMessage(sender, "死亡しています。", LogLevel.ERROR);
				return true;
			}
			return ComingOut.Player(sender,commandLabel, args);
		} else if(arg0.equalsIgnoreCase("log")){
			if(Status.getStatus() != Status.GamePlaying && !Jinro.getDebug()){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "まだ使用できません。");
				return true;
			}
			if(Yakusyoku.getDeath( Utility.getPlayer(sender.getName()) )){
				sendMessage(sender, "死亡しています。", LogLevel.ERROR);
				return true;
			}
			return Book.Player(sender,commandLabel, args);
		} else if(arg0.equalsIgnoreCase("kami")){
			if(Status.getStatus() != Status.GamePlaying && Cycle.getStatus() != Cycle.Night && !Jinro.getDebug()){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "まだ使用できません。");
				return true;
			}
			if(Yakusyoku.getYaku( (Player) sender ) != Yakusyoku.人狼){
				sendMessage(sender, "あなたは人狼ではありません。", LogLevel.ERROR);
				return true;
			}
			if (args.length == 1) {
				sendMessage(sender, "プレイヤーを指定してください。", LogLevel.ERROR);
			} else {
				Player p = Bukkit.getPlayer(args[1]);
				Yakusyoku.Kami((Player) sender, p);
			}
			return true;
		} else if(arg0.equalsIgnoreCase("uranai")){
			if(Status.getStatus() != Status.GamePlaying && Cycle.getStatus() != Cycle.Night && !Jinro.getDebug()){
				sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "まだ使用できません。");
				return true;
			}
			if(Yakusyoku.getYaku( Utility.getPlayer( sender.getName() ) ) != Yakusyoku.占い師){
				sendMessage(sender, "あなたは占い師ではありません。", LogLevel.ERROR);
				return true;
			}
			if (args.length == 1) {
				sendMessage(sender, "プレイヤーを指定してください。", LogLevel.ERROR);
			} else if(args[1].equalsIgnoreCase("syoniti##")) {
				if(Cycle.getStatus() != Cycle.Night){
					sendMessage(sender, "夜ではありません。", LogLevel.ERROR);
					return true;
				}
				if(Yakusyoku.getDeath( Utility.getPlayer(sender.getName()) )){
					sendMessage(sender, "死亡しています。", LogLevel.ERROR);
					return true;
				}
				if( Data.getConfig().getBoolean("Status.uranai."+ sender.getName() +"." + Timer.getDay()) ){
					sendMessage(sender, "もう既に占っています。", LogLevel.ERROR);
					return true;
				}
				Yakusyoku yaku = Yakusyoku.getSyoniti();
				String y;
				if(yaku == null){
					sendMessage(sender, "初日犠牲者を占えませんでした....", LogLevel.SUCCESSFUL);
				} else {
					if(yaku == Yakusyoku.妖狐 || yaku == Yakusyoku.狂人){
						y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
						if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
							y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
						} else {
							y = y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
						}
					} else {
						if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
							y = Yakusyoku.getYakuColor( yaku ) + yaku.toString();
						} else if( yaku == Yakusyoku.人狼 ){
							y = Yakusyoku.getYakuColor( Yakusyoku.人狼 ) + Yakusyoku.人狼.toString();
						} else {
							y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
						}
					}
					sendMessage(sender, "占いの結果、初日犠牲者は " + y + ChatColor.GREEN + " でした。", LogLevel.SUCCESSFUL);
					int i = Stats.getAction((Player) sender, Stats.action.Uranai);
					Stats.setAction((Player) sender, Stats.action.Uranai, (i + 1));
					Data.set("Status.uranai."+ sender.getName() +"." + Timer.getDay(), true);
					Bukkit.broadcast(getPrefix() + ChatColor.GREEN + "[占い]" + sender.getName() + " -> 初日犠牲者("+ Yakusyoku.getYakuColor( yaku ) + yaku.toString() + ChatColor.GREEN +")" , "axtuki1.Jinro.GameMaster");
				}
			} else {
				Player p = Utility.getPlayer( args[1] );
				if(sender.getName() == p.getName()){
					sendMessage(sender, "プレイヤーを指定してください。", LogLevel.ERROR);
				}
				if(p == null){
					sendMessage(sender, "プレイヤーが見つかりませんでした。", LogLevel.ERROR);
					return true;
				}
				if(p.getName() == sender.getName()){
					sendMessage(sender, "自分を占うことはできません。", LogLevel.ERROR);
					return true;
				}
				if(Cycle.getStatus() != Cycle.Night){
					sendMessage(sender, "夜ではありません。", LogLevel.ERROR);
					return true;
				}
				if(Yakusyoku.getDeath( Utility.getPlayer(sender.getName()) )){
					sendMessage(sender, "死亡しています。", LogLevel.ERROR);
					return true;
				}
				if( Data.getConfig().getBoolean("Status.uranai."+ sender.getName() +"." + Timer.getDay()) ){
					sendMessage(sender, "もう既に占っています。", LogLevel.ERROR);
					return true;
				}
				Yakusyoku yaku = Yakusyoku.getYaku( p );
				String y;
				if(yaku == null){
					sendMessage(sender, args[1] + "を占えませんでした....", LogLevel.ERROR);
				} else {
					if(yaku == Yakusyoku.妖狐 ){
						if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
							y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
						} else {
							y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
						}
						p.sendMessage(ChatColor.RED + "あなたは呪い殺されました....");
						p.sendMessage(ChatColor.RED + "翌日霊界へご案内します。");
						Stats.setDeath(p, Stats.death.Curse, (Stats.getDeath(p, Stats.death.Curse) + 1));
						Yakusyoku.setMorningDeath(p);
					} else if(yaku == Yakusyoku.狂人){
                        if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
                            y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
                        } else {
                            y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
                        }
                    } else if(yaku == Yakusyoku.コスプレイヤー){
                        if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
                            y = Yakusyoku.getYakuColor( Yakusyoku.人狼 ) + Yakusyoku.人狼.toString();
                        } else {
                            y = Yakusyoku.getYakuColor( Yakusyoku.人狼 ) + Yakusyoku.人狼.toString();
                        }
                    } else {
						if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
							y = Yakusyoku.getYakuColor( yaku ) + yaku.toString();
						} else if( yaku == Yakusyoku.人狼 ){
							y = Yakusyoku.getYakuColor( Yakusyoku.人狼 ) + Yakusyoku.人狼.toString();
						} else {
							y = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
						}
					}
					sendMessage(sender, "占いの結果、" + p.getName() + " は " + y + ChatColor.GREEN + " でした。" , LogLevel.SUCCESSFUL);
					int i = Stats.getAction((Player) sender, Stats.action.Uranai);
					Stats.setAction((Player) sender, Stats.action.Uranai, (i + 1));
					Data.set("Status.uranai."+ sender.getName() +"." + Timer.getDay(), true);
					String co = "";
					Yakusyoku coy = ComingOut.getComingOut(p);
					if( coy != null ){
						co = Yakusyoku.getYakuColor(coy) + "[" + Yakusyoku.getYaku2moji(coy) + "]" + ChatColor.GREEN;
					}
					Bukkit.broadcast(getPrefix() + ChatColor.GREEN + "[占い]" + sender.getName() + " -> " + co + p.getName() + "("+ Yakusyoku.getYakuColor( yaku ) + yaku.toString() + ChatColor.GREEN +")" , "axtuki1.Jinro.GameMaster");
				}
			}
			return true;
		} else if(arg0.equalsIgnoreCase("goei")){
			if(Status.getStatus() != Status.GamePlaying && Cycle.getStatus() != Cycle.Night && !Jinro.getDebug()){
				sendMessage(sender, "まだ使用できません。", LogLevel.ERROR);
				return true;
			}
			Yakusyoku t = Yakusyoku.getYaku( Utility.getPlayer( sender.getName() ) );
			if(t != Yakusyoku.狩人 && t != Yakusyoku.コスプレイヤー){
				sendMessage(sender, "あなたはこのコマンドを使用できません。" , LogLevel.ERROR);
				return true;
			}
			if (args.length == 1) {
				sendMessage(sender, "プレイヤーを指定してください。", LogLevel.ERROR);
			} else {
				Player p = Utility.getPlayer( args[1] );
				if(p == null){
					sendMessage(sender, "プレイヤーが見つかりませんでした。", LogLevel.ERROR);
					return true;
				}
				if(Yakusyoku.getDeath( Utility.getPlayer(sender.getName()) )){
					sendMessage(sender, "死亡しています。", LogLevel.ERROR);
					return true;
				}
				if(p.getUniqueId().equals( ((Player) sender).getUniqueId() )){
					sendMessage(sender, "自分を護衛することはできません。", LogLevel.ERROR);
					return true;
				}
				if(Cycle.getStatus() != Cycle.Night){
					sendMessage(sender, "夜ではありません。", LogLevel.ERROR);
					return true;
				}
				if( Data.getConfig().getString("Players." + sender.getName() + ".goei") != null ){
					sendMessage(sender, "護衛先を変えることはできません。", LogLevel.ERROR);
					return true;
				}
				Yakusyoku yaku = Yakusyoku.getYaku( p );
				if(yaku == null){
					sendMessage(sender, args[1] + " を護衛できませんでした...(役が振られてない)", LogLevel.INFO);
				} else {
					Yakusyoku.setGoei( Utility.getPlayer( sender.getName() ), p );
					int i = Stats.getAction((Player) sender, Stats.action.Goei);
					Stats.setAction((Player) sender, Stats.action.Goei, (i + 1));
					Yakusyoku yak = Yakusyoku.getYaku(p);
					String y = "";
					if(yak != null){
						y = yak.toString();
					}
					sendMessage(sender, args[1] + " を護衛します。", LogLevel.SUCCESSFUL);
					String co = "";
					Yakusyoku coy = ComingOut.getComingOut(p);
					if( coy != null ){
						co = Yakusyoku.getYakuColor(coy) + "[" + Yakusyoku.getYaku2moji(coy) + "]" + ChatColor.GRAY;
					}
					Bukkit.broadcast(getPrefix() + ChatColor.GRAY + "[護衛]" + sender.getName() + " -> " + co + p.getName() + "("+Yakusyoku.getYakuColor( yaku ) + yaku.toString() + ChatColor.GRAY+")" , "axtuki1.Jinro.GameMaster");
				}
			}
			return true;
		} else if(arg0.equalsIgnoreCase("guide")){
			Help.guide(sender, commandLabel, args);
			return true;
		} else if(arg0.equalsIgnoreCase("ping")){
			sendMessage(sender, "Ping: " + getPing( (Player)sender ) + "ms", LogLevel.INFO,true);
			return true;
		} else if(arg0.equalsIgnoreCase("chat")){
			if(args.length == 1){
				sendCmdHelp(sender, "/jinro chat reikai", "霊界のチャットの\"表示/非表示\"を切り替えます。", false);
				return true;
			} else if(args.length == 2){
				if(args[1].equalsIgnoreCase("reikai")){
					if(Data.getBoolean("Players." + sender.getName() + ".HideReikai")){
						Data.set("Players." + sender.getName() + ".HideReikai", false);
						sendMessage(sender, "霊界のチャットを\"表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					} else {
						Data.set("Players." + sender.getName() + ".HideReikai", true);
						sendMessage(sender, "霊界のチャットを\"非表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					}
				} else if(args[1].equalsIgnoreCase("spec")){
					if(Data.getBoolean("Players." + sender.getName() + ".HideSpec")){
						Data.set("Players." + sender.getName() + ".HideSpec", false);
						sendMessage(sender, "観戦者のチャットを\"表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					} else {
						Data.set("Players." + sender.getName() + ".HideSpec", true);
						sendMessage(sender, "観戦者のチャットを\"非表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					}
				}
			} else if(args.length == 3){
				if(args[1].equalsIgnoreCase("reikai")){
					if(args[2].equalsIgnoreCase("on")){
						Data.set("Players." + sender.getName() + ".HideReikai", false);
						sendMessage(sender, "観戦者のチャットを\"表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					} else if(args[2].equalsIgnoreCase("off")) {
						Data.set("Players." + sender.getName() + ".HideReikai", true);
						sendMessage(sender, "観戦者のチャットを\"非表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					}
				} else if(args[1].equalsIgnoreCase("spec")){
					if(args[2].equalsIgnoreCase("on")){
						Data.set("Players." + sender.getName() + ".HideSpec", false);
						sendMessage(sender, "観戦者のチャットを\"表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					} else if(args[2].equalsIgnoreCase("off")) {
						Data.set("Players." + sender.getName() + ".HideSpec", true);
						sendMessage(sender, "観戦者のチャットを\"非表示\"にしました。", LogLevel.SUCCESSFUL);
						return true;
					}
				}
			}
			sendCmdHelp(sender, "/jinro chat reikai", "霊界のチャットの\"表示/非表示\"を切り替えます。", false);
			return true;
		} else if(arg0.equalsIgnoreCase("Challenge")){
			if(args.length == 1){
				Challenge.openInventory((Player)sender);
			} else if(args.length == 2){
				Challenge.openInventory((Player)sender, args[1]);
			}
			return true;
		} else if(arg0.equalsIgnoreCase("Stats")){
			if( Status.getStatus().equals(Status.GamePlaying) ){
				sendMessage(sender, "ゲーム中にこのコマンドは使用できません。", LogLevel.ERROR, true);
				return true;
			}
			if(args.length == 1){
				sender.sendMessage(ChatColor.RED + "===================================");
				sender.sendMessage("総参加回数: " + ChatColor.YELLOW + Stats.getPlay( (Player)sender ) +"回");
				sender.sendMessage("総死亡回数: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.All ) +"回");
				sender.sendMessage("総勝利回数: " + ChatColor.YELLOW + Stats.getWin( (Player)sender ) +"回");
				sender.sendMessage(ChatColor.AQUA + "/jinro stats Death : 死亡回数");
				sender.sendMessage(ChatColor.AQUA + "/jinro stats Play : 参加/勝利回数");
				sender.sendMessage(ChatColor.AQUA + "/jinro stats Action : 行動回数");
				sender.sendMessage(ChatColor.RED + "===================================");
			} else if(args.length == 2){
				if(args[1].equalsIgnoreCase("Death")){
					sender.sendMessage(ChatColor.RED + "===================================");
					sender.sendMessage("死亡回数: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.All ) +"回");
					sender.sendMessage("処刑: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.Execution ) +"回 " +
							ChatColor.WHITE + "噛み: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.Kami ) +"回");
					sender.sendMessage("爆死: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.Explosion ) +"回 " +
							ChatColor.WHITE + "呪殺: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.Curse ) +"回");
					sender.sendMessage(ChatColor.RED + "===================================");
				} else if(args[1].equalsIgnoreCase("play")){
					sender.sendMessage(ChatColor.RED + "===========================================");
					sender.sendMessage("総参加回数: " + ChatColor.YELLOW + Stats.getPlay((Player)sender) +"回 (P)" + ChatColor.GRAY + " | " + ChatColor.WHITE +"総勝利回数: " + ChatColor.YELLOW + Stats.getWin( (Player)sender ) +"回 (W)");
					int r = -1;
					ArrayList<String> out = new ArrayList<String>();
					String tmp = "";
					for( String yn : Yakusyoku.getYakuList() ){
						Yakusyoku y = Yakusyoku.getNameToYaku(yn);
						if( r >= 2 ) {
							r = 0;
							out.add(tmp);
							tmp = "";
						} else {
							r++;
						}
//						Jinro.getMain().getLogger().info("===========");
//						Jinro.getMain().getLogger().info(yn);
//						Jinro.getMain().getLogger().info(y.toString());
						tmp = tmp + ChatColor.WHITE + Yakusyoku.getYaku2moji(y) + ": " + ChatColor.YELLOW + "P:" + Stats.getPlay((Player)sender, Yakusyoku.getYakuToName(y)) + "回 W:" + Stats.getWin( (Player)sender, Yakusyoku.getYakuToName(y) ) +"回 ";
					}
					for( String o : out ){
						sender.sendMessage(o);
					}
					sender.sendMessage(tmp);
					sender.sendMessage(ChatColor.RED + "===========================================");
				} else if(args[1].equalsIgnoreCase("Action")){
					sender.sendMessage(ChatColor.RED + "===================================");
					sender.sendMessage("噛み: " + ChatColor.YELLOW + Stats.getAction( (Player)sender, Stats.action.Kami ) + "回(成功: " + Stats.getAction( (Player)sender, Stats.action.Kami_Success ) + "回,失敗: " + Stats.getAction( (Player)sender, Stats.action.Kami_Fail ) + "回)");
					sender.sendMessage("護衛: " + ChatColor.YELLOW + Stats.getAction( (Player)sender, Stats.action.Goei ) + "回(成功: " + Stats.getAction( (Player)sender, Stats.action.Goei_Success ) + "回,失敗: " + Stats.getAction( (Player)sender, Stats.action.Goei_Fail ) + "回)");
					sender.sendMessage("占い: " + ChatColor.YELLOW + Stats.getAction( (Player)sender, Stats.action.Uranai ) + "回");
					sender.sendMessage(ChatColor.RED + "===================================");
				} else {
					sender.sendMessage(ChatColor.RED + "===================================");
					sender.sendMessage("総参加回数: " + ChatColor.YELLOW + Stats.getPlay( (Player)sender ) +"回");
					sender.sendMessage("総死亡回数: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.All ) +"回");
					sender.sendMessage("総勝利回数: " + ChatColor.YELLOW + Stats.getWin( (Player)sender ) +"回");
					sender.sendMessage(ChatColor.AQUA + "/jinro stats Death : 死亡回数");
					sender.sendMessage(ChatColor.AQUA + "/jinro stats Play : 参加/勝利回数");
					sender.sendMessage(ChatColor.AQUA + "/jinro stats Action : 行動回数");
					sender.sendMessage(ChatColor.RED + "===================================");
				}
			} else if(args.length == 3){
				sender.sendMessage(ChatColor.RED + "===================================");
				sender.sendMessage("総参加回数: " + ChatColor.YELLOW + Stats.getPlay( (Player)sender ) +"回");
				sender.sendMessage("総死亡回数: " + ChatColor.YELLOW + Stats.getDeath( (Player)sender, Stats.death.All ) +"回");
				sender.sendMessage("総勝利回数: " + ChatColor.YELLOW + Stats.getWin( (Player)sender ) +"回");
				sender.sendMessage(ChatColor.RED + "===================================");
			}
			return true;
		} else if(arg0.equalsIgnoreCase("list")){
			if(Status.getStatus() == Status.GameStandby){
				sender.sendMessage(getPrefix() + ChatColor.RED + "ゲームが開始してから使用できます。");
				return true;
			}
			HashMap<String, Integer> out = new HashMap<String, Integer>();
			Yakusyoku yaku =  Yakusyoku.getSyoniti();
			out = setCount(yaku, out);
			for( Player p : Yakusyoku.getAllPlayers() ){
				yaku = Yakusyoku.getYaku(p);
				if(yaku == null){
					continue;
				}
				out = setCount( yaku, out );
			}
			sender.sendMessage(ChatColor.RED + "===================================");
			if(out.size() == 1 && out.get("none") != null){
				sender.sendMessage(ChatColor.RED + "まだ役は振られていません。");
			} else {
				sender.sendMessage(ChatColor.GREEN + "本ゲームの役職は以下の通りです。");
			}
			for( String key : out.keySet() ){
				if(key.equalsIgnoreCase("none")){
					continue;
				}
				sender.sendMessage( Yakusyoku.getYakuNameC( Yakusyoku.getNameToYaku(key) ) + ChatColor.GREEN + ": " + ChatColor.YELLOW + out.get(key) + "人");
			}
			sender.sendMessage(ChatColor.RED + "===================================");
			return true;
		} else if(arg0.equalsIgnoreCase("option")){
			sender.sendMessage(ChatColor.RED + "===================================");
			sender.sendMessage(ChatColor.GOLD + "ルールセット: " + ChatColor.YELLOW + Setting.getPreset().toString());
			sender.sendMessage(ChatColor.AQUA + "夜時間: " + ChatColor.YELLOW + Jinro.getMain().getConfig().getString("NightTime") + "秒" + ChatColor.GREEN +
					" 議論時間: " + ChatColor.YELLOW + Jinro.getMain().getConfig().getString("DiscussionTime") + "秒");
			sender.sendMessage(ChatColor.GRAY + "===================================");
			sender.sendMessage(
					TextToggle(
							ChatColor.YELLOW + "カミングアウトの表示",
							Jinro.getMain().getConfig().getBoolean("ShowComingOut")
					)
			);
			sender.sendMessage(
					TextToggle(
							ChatColor.YELLOW + "チャットカウンター",
							Jinro.getMain().getConfig().getBoolean("ChatCounterEnable")
					)
			);
			sender.sendMessage(
					TextToggle(
							ChatColor.YELLOW + "COを含む発言の通知",
							Jinro.getMain().getConfig().getBoolean("NoticeComingOut")
					)
			);
			sender.sendMessage(
					TextToggle(
							ChatColor.YELLOW + "占い/霊能時の判定",
							Jinro.getMain().getConfig().getBoolean("ShowYakusyoku"),
							ChatColor.GREEN + "役職",
							ChatColor.WHITE + "村人" + ChatColor.YELLOW + "/"  + ChatColor.RED + "人狼"
					)
			);
			sender.sendMessage(
					TextToggle(
							ChatColor.YELLOW + "人形使いの人形使用の通知",
							Jinro.getMain().getConfig().getBoolean("ShowUseNingyou")
					)
			);
			sender.sendMessage(
					Jinro.TextToggle(
							ChatColor.YELLOW + "途中観戦モード",
							Jinro.getMain().getConfig().getBoolean("LoginSpectatorMode")
					)
			);
			sender.sendMessage(ChatColor.RED + "===================================");
			return true;
		} else if(arg0.equalsIgnoreCase("about")){
			sender.sendMessage(ChatColor.RED     + "===================================");
			sender.sendMessage(ChatColor.GOLD    + "MinecraftJinro");
			sender.sendMessage(ChatColor.AQUA    + "Original Game: Mafia");
			sender.sendMessage(ChatColor.AQUA    + "Base Game: Are You a Werewolf?");
			sender.sendMessage(ChatColor.AQUA    + "Respected by MinecraftJinro (by Midorikun)");
			sender.sendMessage(ChatColor.GREEN   + "Author: axtuki1");
			sender.sendMessage(ChatColor.GREEN   + "Development started: 2017/05/25");
			sender.sendMessage(ChatColor.RED     + "===================================");
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "========== " + getPrefix() + ChatColor.RED + "==========");
			sendMessage(sender, "あなたの役職で使用可能なコマンドはこちらです。", LogLevel.INFO, false);
			Player p = Utility.getPlayer(sender.getName());
			Yakusyoku yaku = null;
			if(p != null){
				yaku = Yakusyoku.getYaku( p );
			}
			if(yaku == Yakusyoku.人狼){
				sendCmdHelp(sender ,"/jinro kami <Player>" ,"[夜のみ] 指定したプレイヤーを襲撃します。", false);
			}
			if(yaku == Yakusyoku.占い師){
				sendCmdHelp(sender ,"/jinro uranai <Player>" ,"[夜のみ] 指定したプレイヤーを占います。", false);
				sendCmdHelp(sender ,"/jinro uranai syoniti##" ,"[夜のみ] 初日犠牲者を占います。", false);
			}
			if(yaku == Yakusyoku.狩人){
				sendCmdHelp(sender ,"/jinro goei <Player>" ,"[夜のみ] 指定したプレイヤーを人狼の襲撃から守ります。", false);
			}
			sendCmdHelp(sender ,"/jinro co <役職>" ,"[議論中のみ] カミングアウトします。", false);
			if(!Jinro.getMain().getConfig().getBoolean("ShowComingOut")) {
				sender.sendMessage(ChatColor.RED + "※現在のルールではCOした役職が表示されません。");
			}
			sendCmdHelp(sender ,"/jinro touhyou <プレイヤー>" ,"[投票時間のみ] 投票します(紙を手に持ってから実行)", false);
			sendCmdHelp(sender ,"/jinro chat <...>" ,"チャットに関する設定をします。", false);
			sendCmdHelp(sender ,"/jinro list" ,"使用されている役職の人数を確認できます。", false);
			sendCmdHelp(sender ,"/jinro stats [...]" ,"これまでの成績を表示します。", false);
			sendCmdHelp(sender ,"/jinro challenge" ,"実績を表示します。", false);
			sendCmdHelp(sender ,"/jinro option" ,"現在のゲーム設定を表示します。", false);
			return true;
		}
	}

	private HashMap<String, Integer> setCount(Yakusyoku yaku, HashMap<String, Integer> out) {
		if(yaku == null){
			out.merge("none", 1, (a, b) -> (a + b));
		} else {
			out.merge(Yakusyoku.getYakuToName(yaku), 1, (a, b) -> (a + b));
		}
		return out;
	}

	public static String TextToggle(String text, boolean flag){
		return TextToggle(text,flag,ChatColor.GREEN + "有効",ChatColor.RED + "無効");
	}

	public static String TextToggle(String text, boolean flag, String on, String off){
		String out = "";
		if(flag){
			out = text + ": " + on;
		} else {
			out = text + ": " + off;
		}
		return out;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
		List<String> view = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("jinro")){
			//sender.sendMessage("args.length = " + args.length);
			if(args.length == 1){
				// jinro
				Player p = Utility.getPlayer(sender.getName());
				Yakusyoku yaku = null;
				if(p != null){
					yaku = Yakusyoku.getYaku( p );
				}
				String arg = args[0].toLowerCase();
				for ( String name : Jinro.getCmdList( yaku ) ) {
					if ( name.toLowerCase().startsWith(arg) ) {
						view.add(name);
					}
				}
				return view;
			} else if(args[0].equalsIgnoreCase("co")) {
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : Yakusyoku.getCOList() ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("uranai")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Yakusyoku.getAlivePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							if( sender.getName() != player.getName() ){
								view.add(player.getName());
							}
						}
					}
					if("syoniti##".toLowerCase().startsWith(arg)){
						view.add("syoniti##");
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("kami")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Yakusyoku.getAlivePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							if( sender.getName() != player.getName() ){
								view.add(player.getName());
							}
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("touhyou")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Yakusyoku.getAlivePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							if( sender.getName() != player.getName() ){
								view.add(player.getName());
							}
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("goei")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Yakusyoku.getAlivePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							if( sender.getName() != player.getName() ){
								view.add(player.getName());
							}
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("chat")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"reikai","spec"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				} else if(args.length == 3){
					String arg = args[2].toLowerCase();
					for ( String name : new String[]{"on", "off"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("stats")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"Play", "Death", "Action"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("guide")) {
				// jinro gu c 1
				if(args.length == 2 ){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"cycle"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				} else if(args.length == 3 ){
					if(args[1].equalsIgnoreCase("cycle")){
						String arg = args[2].toLowerCase();
						for ( String name : new String[]{"1","2","3","4","5","6","7"}  ) {
							if ( name.toLowerCase().startsWith(arg) ) {
								view.add(name);
							}
						}
						return view;
					}
				}
				return view;
			} else if(args[0].equalsIgnoreCase("ping")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Bukkit.getOnlinePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							view.add(player.getName());
						}
					}
					return view;
				}
				for(Player player : Bukkit.getOnlinePlayers()){
					view.add(player.getName());
				}
				return view;
			} else if(args[0].equalsIgnoreCase("challenge")) {
				if (args.length == 2) {
					String arg = args[1].toLowerCase();
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getName().toLowerCase().startsWith(arg)) {
							view.add(player.getName());
						}
					}
					return view;
				}
			}


			return view;
		}
		
		// ========================================================================
		
		if(cmd.getName().equalsIgnoreCase("jinro_ad")){

			if(args.length == 1){
				String arg = args[0].toLowerCase();
				for ( String name : Jinro.getAdminCmdList() ) {
					if ( name.toLowerCase().startsWith(arg) ) {
						view.add(name);
					}
				}
			} else if(args[0].equalsIgnoreCase("start")){
				view.add("");
				return view;
			} else if(args[0].equalsIgnoreCase("Pause")) {
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"timer","cycle"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
				return view;
			} else if(args[0].equalsIgnoreCase("stop")) {
				view.add("");
				return view;
			} else if(args[0].equalsIgnoreCase("skip")) {
				view.add("");
				return view;
			} else if(args[0].equalsIgnoreCase("reload")) {
				view.add("");
				return view;
			} else if(args[0].equalsIgnoreCase("initialization")) {
				view.add("");
				return view;
			} else if(args[0].equalsIgnoreCase("open")) {
				view.add("");
				return view;
			} else if(args[0].equalsIgnoreCase("kill")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Bukkit.getOnlinePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							view.add(player.getName());
						}
					}
					return view;
				}
				for(Player player : Bukkit.getOnlinePlayers()){
					view.add(player.getName());
				}
				return view;
			} else if(args[0].equalsIgnoreCase("ping")) {
				if (args.length == 2) {
					String arg = args[1].toLowerCase();
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getName().toLowerCase().startsWith(arg)) {
							view.add(player.getName());
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("spec")) {
				if (args.length == 2) {
					String arg = args[1].toLowerCase();
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getName().toLowerCase().startsWith(arg)) {
							view.add(player.getName());
						}
					}
					return view;
				} else if (args.length == 3) {
					String arg = args[2].toLowerCase();
					for ( String name : new String[]{"join","spec"} ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("co")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Bukkit.getOnlinePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							view.add(player.getName());
						}
					}
					return view;
				} else if(args.length == 3){
					String arg = args[2].toLowerCase();
					ArrayList<String> a = new ArrayList<String>();
					Collections.addAll(a, Yakusyoku.getCOList());
					a.add("del");
					for ( String name : a ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
				}
				return view;
			} else if(args[0].equalsIgnoreCase("yakusyoku")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					ArrayList<String> a = new ArrayList<String>();
					Collections.addAll(a, Yakusyoku.getYakuList());
					a.add("del");
					for ( String name : a ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				} else if(args.length == 3){
					if(args[1].equalsIgnoreCase("random")) {
						String arg = args[2].toLowerCase();
						for ( String name : Yakusyoku.getYakuList() ) {
							if ( name.toLowerCase().startsWith(arg) ) {
								view.add(name);
							}
						}
						return view;
					} else {
						String arg = args[2].toLowerCase();
						for(Player player : Bukkit.getOnlinePlayers()){
							if ( player.getName().toLowerCase().startsWith(arg) ) {
								view.add(player.getName());
							}
						}
						if("syoniti##".toLowerCase().startsWith(arg)){
							view.add("syoniti##");
						}
						return view;
					}
				}
				return view;
			} else if(args[0].equalsIgnoreCase("head")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Bukkit.getOnlinePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							view.add(player.getName());
						}
					}
					return view;
				}
			} else if(args[0].equalsIgnoreCase("touhyou")) {
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"open","set","kill","check"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				} else if(args.length == 3){
					if(args[1].equalsIgnoreCase("open")){
						view.add("force");
						return view;
					}
				} else if(args.length == 4){
					if(args[1].equalsIgnoreCase("open")) {
						String arg = args[1].toLowerCase();
						for (String name : new String[]{"force"}) {
							if (name.toLowerCase().startsWith(arg)) {
								view.add(name);
							}
						}
						return view;
					}
				}
				return view;
			} else if(args[0].equalsIgnoreCase("tp")) {
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"reikai","spawn","all"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
				return view;
			} else if(args[0].equalsIgnoreCase("setup")) {
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"spawn","reikai"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
				return view;
			} else if(args[0].equalsIgnoreCase("option")) {
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"LoginSpectatorMode", "ChatCounter","ShowComingOut","NoticeComingOut","WinnerMsgUse", "ShowYakusyoku", "SetGameTime", "ShowUseNingyou"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}

				} else if(args.length == 3){
					if(args[1].equalsIgnoreCase("WinnerMsgUse")){
						String arg = args[2].toLowerCase();
						for ( String name : new String[]{"Chat","Title"}  ) {
							if ( name.toLowerCase().startsWith(arg) ) {
								view.add(name);
							}
						}
						return view;
					} else if(args[1].equalsIgnoreCase("ShowYakusyoku")){
						String arg = args[2].toLowerCase();
						for ( String name : new String[]{"Yakusyoku","MuraKami"}  ) {
							if ( name.toLowerCase().startsWith(arg) ) {
								view.add(name);
							}
						}
						return view;
					} else if(args[1].equalsIgnoreCase("SetGameTime")){
						String arg = args[2].toLowerCase();
						for ( String name : new String[]{"Night","Discussion"}  ) {
							if ( name.toLowerCase().startsWith(arg) ) {
								view.add(name);
							}
						}
						return view;
					} else {
						String arg = args[2].toLowerCase();
						for ( String name : new String[]{"on","off"}  ) {
							if ( name.toLowerCase().startsWith(arg) ) {
								view.add(name);
							}
						}
						return view;
					}
				}
				return view;
			} else if(args[0].equalsIgnoreCase("next")) {
				if(args.length == 2 ){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"force"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
				return view;
			} else if(args[0].equalsIgnoreCase("guide")) {
				// jinro gu c 1
				if(args.length == 2 ){
					String arg = args[1].toLowerCase();
					for ( String name : new String[]{"cycle"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				} else if(args.length == 3 ){
					if(args[1].equalsIgnoreCase("cycle")){
						String arg = args[2].toLowerCase();
						for ( String name : new String[]{"1","2","3","4","5","6","7","8"}  ) {
							if ( name.toLowerCase().startsWith(arg) ) {
								view.add(name);
							}
						}
						return view;
					}
				}
				return view;
			} else if(args[0].equalsIgnoreCase("challenge")){
				if(args.length == 2){
					String arg = args[1].toLowerCase();
					for(Player player : Bukkit.getOnlinePlayers()){
						if ( player.getName().toLowerCase().startsWith(arg) ) {
							view.add(player.getName());
						}
					}
					return view;
				} else if(args.length == 3){
					HashMap<String, ?> a = Challenge.getChallengeList();
					String arg = args[2].toLowerCase();
					for(String s : a.keySet()){
						if ( s.toLowerCase().startsWith(arg) ) {
							view.add(s);
						}
					}
					return view;
				} else if(args.length == 4){
					String arg = args[3].toLowerCase();
					for ( String name : new String[]{"true","false"}  ) {
						if ( name.toLowerCase().startsWith(arg) ) {
							view.add(name);
						}
					}
					return view;
				}
			}
		}
		return view;
	}

	private static Location ReikaiLoc = null;
	private static Location RespawnLoc = null;

	static void TeleportToReikai(Player p){
		p.teleport(getReikaiLoc());
	}
	
	static void TeleportToRespawn(Player p){
		p.teleport(getRespawnLoc());
	}
	
	static void setReikaiLoc(Location loc){
		ReikaiLoc = loc;
	}
	
	static void setRespawnLoc(Location loc){
		RespawnLoc = loc;
	}
	
	static Location getReikaiLoc(){
		return ReikaiLoc;
	}
	
	static Location getRespawnLoc(){
		return RespawnLoc;
	}

	public static void set(String path, Object value){
		getMain().getConfig().set(path, value);
		getMain().saveConfig();
	}

	public static void sendMessage(Player p, String msg, LogLevel lv, boolean Prefix){
		String send = "";
		if(Prefix){
			send = Jinro.getPrefix();
		}
		switch(lv){
			case INFO:
				send = send + ChatColor.AQUA;
				break;
			case SUCCESSFUL:
				send = send + ChatColor.GREEN;
				break;
			case ERROR:
			case FATAL:
				send = send + ChatColor.RED;
				break;
		}
		p.sendMessage(send + msg);
	}

	public static void sendMessage(Player p, String msg, LogLevel lv){
		sendMessage(p, msg, lv, true);
	}

	public static void sendMessage(CommandSender p, String msg, LogLevel lv, boolean Prefix){
		String send = "";
		if(Prefix){
			send = Jinro.getPrefix();
		}
		switch(lv){
			case INFO:
				send = send + ChatColor.AQUA;
				break;
			case SUCCESSFUL:
				send = send + ChatColor.GREEN;
				break;
			case ERROR:
			case FATAL:
				send = send + ChatColor.RED;
				break;
		}
		p.sendMessage(send + msg);
	}

	public static void sendMessage(CommandSender p, String msg, LogLevel lv){
		sendMessage( p, msg, lv, true);
	}


	public static void sendCmdHelp(Player p, String cmd, String help, boolean Prefix){
		String send = "";
		if(Prefix){
			send = Jinro.getPrefix();
		}
		p.sendMessage(send + CmdColor(cmd) + " " + ChatColor.GREEN + help);
	}

	public static void sendCmdHelp(Player p, String cmd, String help){
		sendCmdHelp(p, cmd, help, false);
	}

	public static void sendCmdHelp(CommandSender p, String cmd, String help, boolean Prefix){
		String send = "";
		if(Prefix){
			send = Jinro.getPrefix();
		}
		p.sendMessage(send + CmdColor(cmd) + " " + ChatColor.GREEN + help);
	}

	public static void sendCmdHelp(CommandSender p, String cmd, String help){
		sendCmdHelp(p, cmd, help, false);
	}


	public static String CmdColor(String s){
		return ChatColor.AQUA+s;
	}

	public String CommandText(String[] args, int start){
		StringBuilder out = new StringBuilder();
		for(int i = start; i < args.length ; i++){
			out.append(args[i]).append(" ");
		}
		return out.toString();
	}

	String serverName  = Bukkit.getServer().getClass().getPackage().getName(),
			serverVersion = serverName.substring(serverName.lastIndexOf(".") + 1, serverName.length());

	public int getPing(Player p) {
		try {
			Class<?> CPClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftPlayer");
			Object CraftPlayer = CPClass.cast(p);

			Method getHandle = CraftPlayer.getClass().getMethod("getHandle", new Class[0]);
			Object EntityPlayer = getHandle.invoke(CraftPlayer, new Object[0]);

			Field ping = EntityPlayer.getClass().getDeclaredField("ping");

			return ping.getInt(EntityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
