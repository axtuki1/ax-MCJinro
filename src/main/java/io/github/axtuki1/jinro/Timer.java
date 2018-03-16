package io.github.axtuki1.jinro;

import io.github.theluca98.textapi.ActionBar;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class Timer extends BukkitRunnable {

    private static int a = 0;
	private static int t = 0;
	private static int elapse = 0;
    private static int day = 1;
	private static boolean GameStopFlag = false;
    private static boolean GameEndFlag = false;
	private static boolean TimerStopFlag = false;
	private static boolean TimerPauseFlag = false;
	private static boolean CyclePauseFlag = false;
    private static boolean isTimer = false;
	private static World w = Jinro.getCurrentWorld();
    private static Config Data = Jinro.getData();

    public static void init(){
        setDay(1);
        setGameStopFlag(false);
        setGameEndFlag(false);
        setTimerPauseFlag(false);
        setCyclePauseFlag(false);
    }

	@Override
	public void run() {

	    isTimer = true;


        int Alive = Data.getInt("Status.Alive");
        int Death = Data.getInt("Status.Death");
        ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人");
        ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ (Death - 1) +"人");
        ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
        ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
        ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ (Alive + 1) +"人");
        ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ (Death + 1) +"人");
        ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ Alive +"人").setScore(2);
        ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ Death +"人").setScore(1);

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
                Player pe = Yakusyoku.getExecution( day );
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
                if( Yakusyoku.getYaku(p) == Yakusyoku.人狼){
                    if(getDay() != 1) {
                        if (!Data.getBoolean("Status.kami." + getDay())) {
                            bar.setText(ChatColor.GREEN + "能力を使用できます。");
                            bar.send(p);
                            continue;
                        } else {
                            bar.setText(ChatColor.GRAY + "能力は使用済みです。");
                            bar.send(p);
                            continue;
                        }
                    } else {
                        bar.setText(ChatColor.GRAY + "初日は能力を使用できません。");
                        bar.send(p);
                        continue;
                    }
                } else if(Yakusyoku.getYaku(p) == Yakusyoku.占い師 ) {
                    if(!Data.getBoolean("Status.uranai."+ p.getUniqueId() +"." + getDay())) {
                        bar.setText(ChatColor.GREEN + "能力を使用できます。");
                        bar.send(p);
                        continue;
                    } else {
                        bar.setText(ChatColor.GRAY + "能力は使用済みです。");
                        bar.send(p);
                        continue;
                    }
                } else if(Yakusyoku.getYaku(p) == Yakusyoku.狩人) {
                    if(Data.getString("Players." + p.getUniqueId() + ".goei") == null) {
                        bar.setText(ChatColor.GREEN + "能力を使用できます。");
                        bar.send(p);
                        continue;
                    } else {
                        bar.setText(ChatColor.GRAY + "能力は使用済みです。");
                        bar.send(p);
                        continue;
                    }
                }
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
        ScoreBoard.getScoreboard().resetScores(getTimeColor(300)  + "残り時間: 300秒");
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
        if(Cycle.getStatus() == Cycle.Execution || Cycle.getStatus() == Cycle.Night) {
            WinnerCheck();
        }

        if(Status.getStatus() == Status.GameEnd || getGameEndFlag()){
            setDay(getDay() + 1);
            return;
        }

        ScoreBoard.getScoreboard().resetScores(getTimeColor(t - 1)  + "残り時間: "+( t - 1 )+"秒");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(t)  + "残り時間: "+t+"秒");
        ScoreBoard.getScoreboard().resetScores(getTimeColor(t + 1)  + "残り時間: "+( t + 1 )+"秒");
        ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: 0人");
	    if(Cycle.getStatus() == Cycle.Night){
	        Cycle.setStatus(Cycle.Discussion);
	        setGameTime(Jinro.getMain().getConfig().getInt("DiscussionTime"));
            w.setTime(6000);
            setDay(getDay() + 1);
            ScoreBoard.getScoreboard().resetScores(getTimeColor(1)  + "残り時間: 1秒");
            ScoreBoard.getScoreboard().resetScores(getTimeColor(0)  + "残り時間: 0秒");
            ScoreBoard.getInfoObj().getScore(getTimeColor(t)  + "残り時間: 300秒").setScore(3);
	        Bukkit.broadcastMessage(ChatColor.RED + "===============["+day+"日目の朝になりました]===============");
            Bukkit.broadcastMessage(ChatColor.AQUA + "中央の広場にお集まりください。");
            Bukkit.broadcastMessage(ChatColor.GREEN + "/jinro でコマンドの詳細を確認できます。");
            Bukkit.broadcastMessage(ChatColor.GREEN + "チャットに「@」(全角半角問わない)をつけると文章を強調できます。");
            if(day == 2){
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "初日犠牲者 さんが無残な姿で発見されました。");
                int Alive = Data.getInt("Status.Alive");
                int  Death = Data.getInt("Status.Death");
                Data.set("Status.Alive", Alive - 1);
                Data.set("Status.Death", Death + 1);
                ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
                ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
                ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人").setScore(2);
                ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ (Death + 1) +"人").setScore(1);
            }
            List<String> kami = Yakusyoku.getMorningDeathPlayers(day - 1);
            if(kami != null){
                for(String a : kami){
                    Player p = Utility.getPlayer( a );
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + a + " さんが無残な姿で発見されました。");
                    Jinro.TeleportToReikai( p );
                    Yakusyoku.setDeath( p );
                    Stats.setDeath(p, Stats.death.Kami, Stats.getDeath(p, Stats.death.Kami) + 1);
                    int Alive = Data.getInt("Status.Alive");
                    int Death = Data.getInt("Status.Death");
                    Data.set("Status.Alive", Alive - 1);
                    Data.set("Status.Death", Death + 1);
                    ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
                    ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
                    ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人").setScore(2);
                    ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ (Death + 1) +"人").setScore(1);
                }
                WinnerCheck();
            }
            boolean niwatori_found = false;
            for( Player p : Yakusyoku.getAlivePlayers()){
                Yakusyoku yaku = Yakusyoku.getYaku(p);
                if( yaku == Yakusyoku.ニワトリ ){
                    niwatori_found = true;
                }
            }
            if(niwatori_found){
                int rnd = new Random().nextInt(11);
                if( rnd > 8 ){
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "コケコッコー！");
                } else if( rnd > 4 ) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "コケッ！");
                } else if( rnd > 2 ) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "コケッ！コケコッ！");
                } else if( rnd > 1 ) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "コケッッッッッッッコー！！！！");
                } else if( rnd == 0 ) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "ｸｩｫｹｺｯｺｩ！！！！");
                } else {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "コケコケコッコッコ");
                }
            }
        } else if(Cycle.getStatus() == Cycle.Discussion || Cycle.getStatus() == Cycle.VoteAgain){
            Cycle.setStatus(Cycle.Vote);
            CyclePauseFlag = true;
            setGameTime(0);
            Bukkit.broadcastMessage(ChatColor.RED + "===============[投票の時間になりました]===============");
            Bukkit.broadcastMessage(ChatColor.AQUA + "紙を手に持って投票してください");
            Bukkit.broadcastMessage(ChatColor.AQUA + "投票は「/jinro touhyou <Player>」で行えます");
            Bukkit.broadcastMessage(ChatColor.AQUA + "投票時間中のチャットはGMにしか聞こえません。");
            w.setTime(12250);
            List<Player> pl = Yakusyoku.getAlivePlayers();
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

        } else if(Cycle.getStatus() == Cycle.Execution || Cycle.getStatus() == Cycle.Standby){
            CyclePauseFlag = false;
            Cycle.setStatus(Cycle.Night);
            Player ex = Yakusyoku.getExecution(day);
            setGameTime(Jinro.getMain().getConfig().getInt("NightTime"));
            w.setTime(15000);
            Bukkit.broadcastMessage(ChatColor.RED + "===============["+ day +"日目の夜になりました]===============");
            Bukkit.broadcastMessage(ChatColor.AQUA + "夜に出歩くと狼に襲われる可能性があります！");
            List<Player> pl = Yakusyoku.getAlivePlayers();
            if(pl.size() != 0) {
                for (Player p : pl) {
                    if (p == null) {
                        continue;
                    }
                    Yakusyoku y = Yakusyoku.getYaku(p);
                    if(y == null){
                        continue;
                    }
                    ChatColor yc = Yakusyoku.getYakuColor(y);
                    switch (y) {
                        case 人狼:
                            p.sendMessage(yc + "あなたは 人狼 です。");
                            if (day == 1) {
                                p.sendMessage(yc + "初日は噛み殺すことができません。");
                            } else {
                                p.sendMessage(yc + "毎晩一人、噛み殺すことができます。");
                                p.sendMessage(yc + "「/jinro kami <Player>」で噛み殺すことができます。");
                            }
                            p.sendMessage(yc + "目標: 人狼の勝利");
                            if (day == 1) {
                                p.sendMessage(yc + "====[今回の人狼は]====");
                                for (Player a : Yakusyoku.getPlayers(Yakusyoku.人狼)) {
                                    p.sendMessage(yc + "[ " + a.getName() + " ]");
                                }
                            }
                            break;
                        case 占い師:
                            p.sendMessage(yc + "あなたは 占い師 です。");
                            p.sendMessage(yc + "毎晩一人を占うことができます。");
                            p.sendMessage(yc + "「/jinro uranai <Player>」で占えます。");
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
                            p.sendMessage(yc + "人狼の会話を聴くことができます。");
                            p.sendMessage(yc + "空気を読む力とトークスキルで人狼を勝利へ導いてください。");
                            p.sendMessage(yc + "目標: 人狼の勝利");
                            if (day == 1) {
                                p.sendMessage(ChatColor.RED + "====[今回の人狼は]====");
                                for (Player a : Yakusyoku.getPlayers(Yakusyoku.人狼)) {
                                    p.sendMessage(ChatColor.RED + "[ " + a.getName() + " ]");
                                }
                            }
                            break;
                        case 狂信者:
                            p.sendMessage(yc + "あなたは 狂信者 です。");
                            p.sendMessage(yc + "能力は人狼のメンバーを把握することのみです。");
                            p.sendMessage(yc + "空気を読む力とトークスキルで人狼を勝利へ導いてください。");
                            p.sendMessage(yc + "目標: 人狼の勝利");
                            if (day == 1) {
                                p.sendMessage(ChatColor.RED + "====[今回の人狼は]====");
                                for (Player a : Yakusyoku.getPlayers(Yakusyoku.人狼)) {
                                    p.sendMessage(ChatColor.RED + "[ " + a.getName() + " ]");
                                }
                            }
                            break;
                        case 妖狐:
                            p.sendMessage(yc + "あなたは 妖狐 です。");
                            p.sendMessage(yc + "人狼に襲われても死にませんが占い師に占われると死んでしまいます。");
                            p.sendMessage(yc + "推理力とトークスキルで最後まで生存しましょう。");
                            p.sendMessage(yc + "目標: 最後まで生存");
                            break;
                        case 狩人:
                            p.sendMessage(yc + "あなたは 狩人 です。");
                            p.sendMessage(yc + "毎晩一人、人狼の襲撃から守ることができます。");
                            p.sendMessage(yc + "「/jinro goei <Player>」で守る人を決めることができます。");
                            p.sendMessage(yc + "なお、護衛先を変えることはできません。");
                            p.sendMessage(yc + "※ 15秒以内に対象を決めないと噛み殺される可能性があります。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            Data.set("Players." + p.getUniqueId() + ".goei", null);
                            break;
                        case 霊能者:
                            p.sendMessage(yc + "あなたは 霊能者 です。");
                            p.sendMessage(yc + "毎晩処刑された人の役職が確認できます。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            if(ex != null){
                                Yakusyoku eyaku = Yakusyoku.getYaku( ex );
                                String ey = "";
                                if(eyaku == Yakusyoku.妖狐 || eyaku == Yakusyoku.狂人){
                                    ey = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
                                    if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
                                        ey = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
                                    } else {
                                        ey = ChatColor.WHITE + "村人";
                                    }
                                } else {
                                    if( Jinro.getMain().getConfig().getBoolean("ShowYakusyoku") ){
                                        ey = Yakusyoku.getYakuColor( eyaku ) + eyaku.toString();
                                    } else if( eyaku == Yakusyoku.人狼 ){
                                        ey = ChatColor.GRAY + "人狼";
                                    } else {
                                        ey = ChatColor.WHITE + "村人";
                                    }
                                }

//                                if(eyaku == Yakusyoku.妖狐 || eyaku == Yakusyoku.狂人){
//                                    ey = Yakusyoku.getYakuColor( Yakusyoku.村人 ) + Yakusyoku.村人.toString();
//                                } else {
//                                    ey = Yakusyoku.getYakuColor( eyaku ) + eyaku.toString();
//                                }
                                p.sendMessage(yc + ex.getName() +" は " + ey + ChatColor.GREEN + " でした。");
                            }
                            break;
                        case 共有者:
                            p.sendMessage(yc + "あなたは 共有者 です。");
                            p.sendMessage(yc + "夜中、もう一人の共有者と会話ができます。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            if (day == 1) {
                                p.sendMessage(yc + "====[今回の共有者は]====");
                                for (Player a : Yakusyoku.getPlayers(Yakusyoku.共有者)) {
                                    p.sendMessage(yc + "[ " + a.getName() + " ]");
                                }
                            }
                            break;
                        case 爆弾魔:
                            p.sendMessage(yc + "あなたは 爆弾魔 です。");
                            p.sendMessage(yc + "人狼に襲われた場合、襲った人狼も道連れにします。");
                            p.sendMessage(yc + "処刑された場合、生存者の中からランダムで道連れにします。");
                            p.sendMessage(yc + "目標: 人狼の勝利");
                            break;
                        case コスプレイヤー:
                            p.sendMessage(yc + "あなたは コスプレイヤー です。");
                            p.sendMessage(yc + "占い師や霊能者からは人狼と判定されますが村人のために戦います。");
                            p.sendMessage(yc + "毎晩一人、人狼の襲撃から守ることができます。");
                            p.sendMessage(yc + "「/jinro goei <Player>」で守る人を決めることができます。");
                            p.sendMessage(yc + "なお、護衛先を変えることはできません。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            break;
                        case 人形使い:
                            p.sendMessage(yc + "あなたは 人形使い です。");
                            p.sendMessage(yc + "占い師や霊能者からは村人と判定されます。");
                            p.sendMessage(yc + "夜の時間に自分が襲われても1回だけ人形を身代わりにできます。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            break;
                        case ニワトリ:
                            p.sendMessage(yc + "あなたは ニワトリ です。");
                            p.sendMessage(yc + "議論時間になると鳴きます。");
                            p.sendMessage(yc + "ただそれだけです。");
                            p.sendMessage(yc + "目標: 村人の勝利");
                            break;
                    }
                }
            }

        }
    }

    public static void WinnerCheck(){
	    if( Jinro.getDebug() ){
	        Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.YELLOW + "[DEBUG] 判定スキップ");
	        return;
        }
        Jinro.getMain().reloadConfig();
        Data.reloadConfig();
        int JinroC = 0;
        int MuraC = 0;
        for(Player p : Yakusyoku.getAlivePlayers()){
            Yakusyoku p_yaku = Yakusyoku.getYaku(p);
            if( p_yaku.equals(Yakusyoku.人狼) ){
                JinroC++;
            } else if( p_yaku.equals(Yakusyoku.村人) || p_yaku.equals(Yakusyoku.狩人)
                    || p_yaku.equals(Yakusyoku.占い師) || p_yaku.equals(Yakusyoku.共有者) || p_yaku.equals(Yakusyoku.人形使い)
                    || p_yaku.equals(Yakusyoku.霊能者) || p_yaku.equals(Yakusyoku.コスプレイヤー)
                    || p_yaku.equals(Yakusyoku.ニワトリ) ){
                MuraC++;
            }
        }

        if(JinroC == 0 && MuraC == 0){
            if(Cycle.getStatus() ==  Cycle.Night){
                setDay(getDay() + 1);
                Bukkit.broadcastMessage(ChatColor.RED + "===============["+day+"日目の朝になりました]===============");
                w.setTime(6000);
                List<String> kami = Yakusyoku.getMorningDeathPlayers(day - 1);
                if(kami != null){
                    for(String a : kami){
                        Player p = Utility.getPlayer( a );
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + a + " さんが無残な姿で発見されました。");
                        Jinro.TeleportToReikai( p );
                        Yakusyoku.setDeath( p );
                        int Alive = Data.getInt("Status.Alive");
                        int Death = Data.getInt("Status.Death");
                        Data.set("Status.Alive", Alive - 1);
                        Data.set("Status.Death", Death + 1);
                        ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
                        ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
                        ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人").setScore(2);
                        ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ (Death + 1) +"人").setScore(1);
                    }
                }
            }
            if(Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")){
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.sendTitle(ChatColor.GRAY + "==== 村には誰もいなくなりました ====", null);
                }
            }
            Bukkit.broadcastMessage(ChatColor.GRAY + "====[村には誰もいなくなりました]====");
            setGameEndFlag(true);
            return;
        } else if(JinroC == 0){
            if(Cycle.getStatus() ==  Cycle.Night){
                setDay(getDay() + 1);
                Bukkit.broadcastMessage(ChatColor.RED + "===============["+day+"日目の朝になりました]===============");
                w.setTime(6000);
                List<String> kami = Yakusyoku.getMorningDeathPlayers(day - 1);
                if(kami != null){
                    for(String a : kami){
                        Player p = Utility.getPlayer( a );
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + a + " さんが無残な姿で発見されました。");
                        Jinro.TeleportToReikai( p );
                        Yakusyoku.setDeath( p );
                        int Alive = Data.getInt("Status.Alive");
                        int Death = Data.getInt("Status.Death");
                        Data.set("Status.Alive", Alive - 1);
                        Data.set("Status.Death", Death + 1);
                        ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
                        ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
                        ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人").setScore(2);
                        ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ (Death + 1) +"人").setScore(1);
                    }
                }
            }
            boolean yokok = false;
            for( Player yoko : Yakusyoku.getAlivePlayers() ){
                if(Yakusyoku.getYaku(yoko) == Yakusyoku.妖狐){
                    yokok = true;
                }
            }
            if(yokok){
                if(Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle(ChatColor.LIGHT_PURPLE + "==== 妖狐が勝利しました ====", null);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "====[妖狐が勝利しました]====");
                Stats.setWin(Yakusyoku.妖狐);
            } else {
                if(Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle(ChatColor.AQUA + "==== 村人が勝利しました ====", null);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.AQUA + "====[村人が勝利しました]====");
                Stats.setWin(Yakusyoku.村人);
                Stats.setWin(Yakusyoku.共有者);
                Stats.setWin(Yakusyoku.霊能者);
                Stats.setWin(Yakusyoku.占い師);
                Stats.setWin(Yakusyoku.狩人);
                Stats.setWin(Yakusyoku.コスプレイヤー);
                Stats.setWin(Yakusyoku.人形使い);
                Stats.setWin(Yakusyoku.ニワトリ);
            }
            setGameEndFlag(true);
            return;
        } else if(JinroC >= MuraC){
            if(Cycle.getStatus() ==  Cycle.Night){
                setDay(getDay() + 1);
                Bukkit.broadcastMessage(ChatColor.RED + "===============["+day+"日目の朝になりました]===============");
                w.setTime(6000);
                List<String> kami = Yakusyoku.getMorningDeathPlayers(day - 1);
                if(kami != null){
                    for(String a : kami){
                        Player p = Utility.getPlayer( a );
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + a + " さんが無残な姿で発見されました。");
                        Jinro.TeleportToReikai( p );
                        Yakusyoku.setDeath( p );
                        int Alive = Data.getInt("Status.Alive");
                        int Death = Data.getInt("Status.Death");
                        Data.set("Status.Alive", Alive - 1);
                        Data.set("Status.Death", Death + 1);
                        ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: "+ Alive +"人");
                        ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: "+ Death +"人");
                        ScoreBoard.getInfoObj().getScore( ChatColor.AQUA + "生存人数: "+ (Alive - 1) +"人").setScore(2);
                        ScoreBoard.getInfoObj().getScore( ChatColor.RED + "死亡人数: "+ (Death + 1) +"人").setScore(1);
                    }
                }
            }
            boolean yokok = false;
            for( Player yoko : Yakusyoku.getAlivePlayers() ){
                if(Yakusyoku.getYaku(yoko) == Yakusyoku.妖狐){
                    yokok = true;
                }
            }
            if(yokok){
                if(Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle(ChatColor.LIGHT_PURPLE + "==== 妖狐が勝利しました ====", null);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "====[妖狐が勝利しました]====");
                Stats.setWin(Yakusyoku.妖狐);
            } else {
                if(Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.sendTitle(ChatColor.DARK_RED + "==== 人狼が勝利しました ====", null);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "====[人狼が勝利しました]====");
                Stats.setWin(Yakusyoku.人狼);
                Stats.setWin(Yakusyoku.狂人);
                Stats.setWin(Yakusyoku.狂信者);
                Stats.setWin(Yakusyoku.聴狂人);
                Stats.setWin(Yakusyoku.爆弾魔);
            }
            setGameEndFlag(true);
            return;
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

    public static void setDay(int d) {
        day = d;
    }

    public static int getDay(){
        return day;
    }
}
