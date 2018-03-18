package io.github.axtuki1.jinro;

import io.github.theluca98.textapi.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class OneNightTimer extends BukkitRunnable {

    private static int a = 0;
	private static int t = 0;
	private static int elapse = 0;
	private static boolean GameStopFlag = false;
    private static boolean GameEndFlag = false;
	private static boolean TimerStopFlag = false;
	private static boolean TimerPauseFlag = false;
	private static boolean CyclePauseFlag = false;
    private static boolean isTimer = false;
	private static World w = Jinro.getCurrentWorld();
    private static Config Data = Jinro.getData();

    public static void init(){
        setGameStopFlag(false);
        setGameEndFlag(false);
        setTimerPauseFlag(false);
        setCyclePauseFlag(false);
    }

	@Override
	public void run() {

	    isTimer = true;



        /*
        あくしょんばー
        */
        ActionBar bar = new ActionBar("");
        for(Player p : Bukkit.getOnlinePlayers()){
            if(Cycle.getStatus() == Cycle.Vote){
                Player b = Touhyou.getTouhyou(p);
                if(p.hasPermission("axtuki1.Jinro.GameMaster")){
                    bar.setText("");
                    bar.send(p);
                    continue;
                } else if(b == null){
                    bar.setText(ChatColor.GREEN + "紙を手に持って投票してください。");
                    bar.send(p);
                    continue;
                } else {
                    bar.setText(ChatColor.GRAY + "GMの開票を待っています...");
                    bar.send(p);
                    continue;
                }
            } else if(Cycle.getStatus() == Cycle.VoteAgain){
                bar.setText(ChatColor.GREEN + "投票の結果: " + ChatColor.AQUA + "再投票");
                bar.send(p);
                continue;
            } else if(Cycle.getStatus() == Cycle.Discussion){
                if(getGameElapsedTime() < 5){
                    bar.setText(ChatColor.RED + "まだ発言できません。");
                    bar.send(p);
                    continue;
                }
            } else if(Cycle.getStatus() == Cycle.Execution){
                Player pe = OneNightYakusyoku.getExecution();
                String s = "";
                if(pe == null){
                    s = "なし";
                } else {
                    s = pe.getName();
                }
                bar.setText(ChatColor.GREEN + "投票の結果: " + ChatColor.RED + s );
                bar.send(p);
                continue;
            } else if(Cycle.getStatus() == Cycle.Night){
                // 夜のアクションバーややこし杉
                // ほんとだるい
            } else {
                bar.setText("");
                bar.send(p);
            }
            bar.setText("");
            bar.send(p);
        }

        /*
		Jinro.getMain().getLogger().info(getTimeColor(tb)  + "残り時間: "+ tb +"秒");
        Jinro.getMain().getLogger().info(getTimeColor(t)  + "残り時間: "+ t +"秒");
        */
        ScoreBoard.getScoreboard().resetScores(getTimeColor(1)  + "残り時間: 1秒");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(0)  + "残り時間: 0秒");
        int defaulta = Jinro.getMain().getConfig().getInt("DiscussionTime");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(300)  + "残り時間: 300秒");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(defaulta)  + "残り時間: "+defaulta+"秒");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(t)  + "残り時間: "+ t +"秒");
        if(!CyclePauseFlag && t == 0 && a == 10){
            NextCycle();
        }
        if(GameEndFlag){
            Status.setStatus(Status.GameEnd);
            Cycle.setStatus(Cycle.Standby);
            for(Player p : Bukkit.getOnlinePlayers()){
                Challenge.CheckOpen(p);
            }
            cancel();
        }
		if(GameStopFlag){
			Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN +"ゲームを強制終了しました。");
            GameStopFlag = false;
			for(Player p : Bukkit.getOnlinePlayers()){
				Jinro.TeleportToRespawn(p);
			}
            Status.setStatus(Status.GameEnd);
            Cycle.setStatus(Cycle.Standby);
			cancel();
		}
		if(TimerStopFlag){
            cancel();
        }
        if(Status.getStatus() == Status.GamePlaying && t > 0 && !TimerPauseFlag && a >= 4){
            t--;
            elapse++;

        }
        if(Status.getStatus() == Status.GamePlaying && t > 0 && a >= 4){
            a = 0;
        }
        a++;
        ScoreBoard.getInfoObj().getScore(getTimeColor(t)  + "残り時間: "+ t +"秒").setScore(3);
	}

	public static void NextCycle() {
        w = Jinro.getCurrentWorld();
        resetGameElapsedTime();
	    // 勝利判定
        if(Cycle.getStatus() == Cycle.Execution ) {
            WinnerCheck();
        }
        ScoreBoard.getScoreboard().resetScores(getTimeColor(t - 1)  + "残り時間: "+( t - 1 )+"秒");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(t)  + "残り時間: "+t+"秒");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(t + 1)  + "残り時間: "+( t + 1 )+"秒");
	    if(Cycle.getStatus() == Cycle.Night){
	        Cycle.setStatus(Cycle.Discussion);
	        setGameTime(Jinro.getMain().getConfig().getInt("DiscussionTime"));
            w.setTime(6000);
            ScoreBoard.getScoreboard().resetScores(getTimeColor(1)  + "残り時間: 1秒");
            ScoreBoard.getScoreboard().resetScores(getTimeColor(0)  + "残り時間: 0秒");
            ScoreBoard.getInfoObj().getScore(getTimeColor(t)  + "残り時間: " + t + "秒").setScore(3);
	        Bukkit.broadcastMessage(ChatColor.RED + "===============[朝になりました]===============");
            Bukkit.broadcastMessage(ChatColor.AQUA + "中央の広場にお集まりください。");
            Bukkit.broadcastMessage(ChatColor.GREEN + "/jinro でコマンドの詳細を確認できます。");
            Bukkit.broadcastMessage(ChatColor.GREEN + "チャットに「@」(全角半角問わない)をつけると文章を強調できます。");
        } else if(Cycle.getStatus() == Cycle.Discussion || Cycle.getStatus() == Cycle.VoteAgain){
            Cycle.setStatus(Cycle.Vote);
            CyclePauseFlag = true;
            setGameTime(0);
            Bukkit.broadcastMessage(ChatColor.RED + "===============[投票の時間になりました]===============");
            Bukkit.broadcastMessage(ChatColor.AQUA + "紙を手に持って投票してください");
            Bukkit.broadcastMessage(ChatColor.AQUA + "投票は「/jinro touhyou <Player>」で行えます");
            Bukkit.broadcastMessage(ChatColor.AQUA + "投票時間中のチャットはGMにしか聞こえません。");
            w.setTime(12250);
            List<Player> pl = OneNightYakusyoku.getAlivePlayers();
            if(pl.size() != 0) {
                for (Player p : pl) {
                    p.getInventory().addItem(new ItemStack(Material.PAPER));
                }
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                Data.set("Players."+p.getUniqueId()+".hitouhyou",null);
                Data.set("Players."+p.getUniqueId()+".touhyou",null);
            }
        } else if(Cycle.getStatus() == Cycle.Vote){
            Cycle.setStatus(Cycle.Execution);
        } else if( Cycle.getStatus() == Cycle.Standby){
            CyclePauseFlag = false;
            Cycle.setStatus(Cycle.Night);
            Player ex = OneNightYakusyoku.getExecution();
            setGameTime(Jinro.getMain().getConfig().getInt("NightTime"));
            w.setTime(15000);
            Bukkit.broadcastMessage(ChatColor.RED + "===============[夜になりました]===============");
//            Bukkit.broadcastMessage(ChatColor.AQUA + "");
            List<Player> pl = OneNightYakusyoku.getAlivePlayers();
            if(pl.size() != 0) {
                for (Player p : pl) {
                    if (p == null) {
                        continue;
                    }
                    OneNightYakusyoku y = OneNightYakusyoku.getYaku(p);
                    if(y == null){
                        continue;
                    }
                    ChatColor yc = OneNightYakusyoku.getYakuColor(y);
                    switch (y) {
                        case 人狼:
                            p.sendMessage(yc + "あなたは 人狼 です。");
                            p.sendMessage(yc + "人狼のプレイヤーの確認ができます。");
                            p.sendMessage(yc + "人狼が一人でも処刑されてしまうと人狼は負けになってしまいます。");
                            p.sendMessage(yc + "目標: 人狼の勝利");
                            p.sendMessage(yc + "====[今回の人狼は]====");
                            for (Player a : OneNightYakusyoku.getPlayers(OneNightYakusyoku.人狼)) {
                                p.sendMessage(yc + "[ " + a.getName() + " ]");
                            }
                            break;
                        case 占い師:
                            p.sendMessage(yc + "あなたは 占い師 です。");
                            p.sendMessage(yc + "一人もしくは余っている役を占うことができます。");
                            p.sendMessage(yc + "「/jinro uranai <Player>」で占えます。");
                            p.sendMessage(yc + "「/jinro uranai amari##」で余っている役を占えます。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            break;
                        case 村人:
                            p.sendMessage(yc + "あなたは 村人 です。");
                            p.sendMessage(yc + "能力は持っていません。");
                            p.sendMessage(yc + "推理力とトークスキルで村を勝利へ導いてください。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            break;
                        case 狂人:
                            p.sendMessage(yc + "あなたは 狂人 です。");
                            p.sendMessage(yc + "能力は持っていません。");
                            p.sendMessage(yc + "推理力とトークスキルで人狼を勝利へ導いてください。");
                            p.sendMessage(yc + "目標: 人狼の勝利");
                            break;
                        case 聴狂人:
                            p.sendMessage(yc + "あなたは 聴狂人 です。");
                            p.sendMessage(yc + "人狼のプレイヤーを確認できます。");
                            p.sendMessage(yc + "空気を読む力とトークスキルで人狼を勝利へ導いてください。");
                            p.sendMessage(yc + "目標: 人狼の勝利");
                            p.sendMessage(ChatColor.RED + "====[今回の人狼は]====");
                            for (Player a : OneNightYakusyoku.getPlayers(OneNightYakusyoku.人狼)) {
                                p.sendMessage(ChatColor.RED + "[ " + a.getName() + " ]");
                            }
                            break;
//                        case 狩人:
//                            p.sendMessage(yc + "あなたは 狩人 です。");
//                            p.sendMessage(yc + "処刑される際に誰か一人を指名して道連れにすることができます。");
//                            //p.sendMessage(yc + "「/jinro assign <Player>」で指名できます");
//                            p.sendMessage(yc + "目標: 村人の勝利");
//                            Data.set("Players." + p.getUniqueId() + ".goei", null);
//                            break;
                        case 怪盗:
                            p.sendMessage(yc + "あなたは 怪盗 です。");
                            p.sendMessage(yc + "誰か一人と役を交換できます。");
                            p.sendMessage(yc + "「/jinro change <Player>」で交換できます。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            break;
                    }
                }
            }
        } else if(Cycle.getStatus() == Cycle.Execution ) {

        }
    }

    public static void WinnerCheck(){
        Jinro.getMain().reloadConfig();
        Data.reloadConfig();

        Player ex = OneNightYakusyoku.getExecution();
        OneNightYakusyoku exy = OneNightYakusyoku.getYaku(ex);

        switch (exy) {
            case 人狼:
                if (Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(ChatColor.AQUA + "==== 村人が勝利しました ====", null);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.AQUA + "====[村人が勝利しました]====");
//                Stats.setWin(OneNightYakusyoku.村人);
//                Stats.setWin(OneNightYakusyoku.占い師);
//                Stats.setWin(OneNightYakusyoku.狩人);
//                Stats.setWin(OneNightYakusyoku.怪盗);
                setGameEndFlag(true);
                break;
            case 怪盗:
            case 村人:
            case 狂人:
            case 占い師:
            case 聴狂人:
                if (Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(ChatColor.DARK_RED + "==== 人狼が勝利しました ====", null);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "====[人狼が勝利しました]====");
//                Stats.setWin(OneNightYakusyoku.人狼);
//                Stats.setWin(OneNightYakusyoku.狂人);
//                Stats.setWin(OneNightYakusyoku.聴狂人);
                setGameEndFlag(true);
                break;
        }
    }

    public static ChatColor getTimeColor(int time) {
	    ChatColor c = ChatColor.GREEN;
        if(time > 60){
            c = ChatColor.GREEN;
        } else if(time <= 60 && time > 30) {
            c = ChatColor.YELLOW;
        } else if(time <= 30 && time >= 0){
            c = ChatColor.RED;
        }
        return c;
    }



    public static void setGameEndFlag(boolean b) {
	    GameEndFlag = b;
    }

    public static boolean getGameEndFlag() {
        return GameEndFlag;
    }

    public static void setGameStopFlag(boolean flag) {
        GameStopFlag = flag;
    }

    public static boolean getGameStopFlag() {
        return GameStopFlag;
    }

    public static void setCyclePauseFlag(boolean flag) {
        CyclePauseFlag = flag;
    }

    public static boolean getCyclePauseFlag() {
        return CyclePauseFlag;
    }


    public static void setTimerPauseFlag(boolean flag) {
		TimerPauseFlag = flag;
	}

    public static boolean getTimerPauseFlag() {
        return TimerPauseFlag;
    }

    public static void setTimerStopFlag(boolean flag) {
        TimerStopFlag = flag;
    }

    public static boolean getTimerStopFlag() {
        return TimerStopFlag;
    }

    public static boolean getTimerStatus(){
        return isTimer;
    }

	public static int getGameTime() {
		return t;
	}

	public static void setGameTime(int time) {
		t = time;
	}

	public static int getGameElapsedTime(){
        return elapse;
    }

    public static void resetGameElapsedTime(){
	    elapse = 0;
        return;
    }
}
