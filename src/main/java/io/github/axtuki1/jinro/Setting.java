package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class Setting {

    private static Config Data = Jinro.getData();


    public static void Command(CommandSender sender, String commandLabel, String[] args) {
        if(args.length == 1) {
            sender.sendMessage(ChatColor.RED + "===================================");
            sender.sendMessage(ChatColor.GOLD + "ルールセット: " + ChatColor.YELLOW + getPreset().toString());
            sender.sendMessage(ChatColor.AQUA + "夜時間: " + ChatColor.YELLOW + Jinro.getMain().getConfig().getString("NightTime") + "秒" + ChatColor.GREEN +
                    " 議論時間: " + ChatColor.YELLOW + Jinro.getMain().getConfig().getString("DiscussionTime") + "秒");
            sender.sendMessage(ChatColor.GRAY + "===================================");
            sender.sendMessage(
                    Jinro.TextToggle(
                            ChatColor.YELLOW + "カミングアウトの表示",
                            Jinro.getMain().getConfig().getBoolean("ShowComingOut")
                    )
            );
            sender.sendMessage(
                    Jinro.TextToggle(
                            ChatColor.YELLOW + "チャットカウンター",
                            Jinro.getMain().getConfig().getBoolean("ChatCounterEnable")
                    )
            );
            sender.sendMessage(
                    Jinro.TextToggle(
                            ChatColor.YELLOW + "COを含む発言の通知",
                            Jinro.getMain().getConfig().getBoolean("NoticeComingOut")
                    )
            );
            sender.sendMessage(
                    Jinro.TextToggle(
                            ChatColor.YELLOW + "占い/霊能時の判定",
                            Jinro.getMain().getConfig().getBoolean("ShowYakusyoku"),
                            ChatColor.GREEN + "役職",
                            ChatColor.WHITE + "村人" + ChatColor.YELLOW + "/"  + ChatColor.RED + "人狼"
                    )
            );
            sender.sendMessage(
                    Jinro.TextToggle(
                            ChatColor.YELLOW + "人形使いの人形使用の通知",
                            Jinro.getMain().getConfig().getBoolean("ShowUseNingyou")
                    )
            );
            sender.sendMessage(
                    Jinro.TextToggle(
                            ChatColor.YELLOW + "勝利判定の表示方法",
                            Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle"),
                            ChatColor.GOLD + "タイトル",
                            ChatColor.AQUA + "チャット"
                    )
            );
            sender.sendMessage(
                    Jinro.TextToggle(
                            ChatColor.YELLOW + "途中観戦モード",
                            Jinro.getMain().getConfig().getBoolean("LoginSpectatorMode")
                    )
            );
            sender.sendMessage(ChatColor.RED + "===================================");
            return;
        }

        if(args[1].equalsIgnoreCase("Preset")) {
            if(args.length == 2){
                sender.sendMessage(ChatColor.RED + "======== [ルールセット一覧] ========");
                StringBuilder preBuilder = new StringBuilder();
                for(RulePreset r : RulePreset.values()){
                    if(r != RulePreset.Custom){
                        preBuilder.append(r.toString()).append(", ");
                    }
                }
                String pre = preBuilder.toString();
                sender.sendMessage(ChatColor.AQUA + pre.substring(0, (pre.length() - 2)));
            } else {
                try{
                    setPreset( RulePreset.valueOf(args[2]) );
                } catch (Exception e){
                    Jinro.sendMessage(sender, "ルールセットが存在しません。", LogLevel.ERROR, true);
                }
                Jinro.sendMessage(sender, "ゲーム設定にルールセット'"+args[2]+"'を適用しました。", LogLevel.SUCCESSFUL, true);
            }
            return;
        }

        if(args[1].equalsIgnoreCase("ShowComingOut")){
            if(args.length == 2){
                if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
                    Jinro.getMain().getConfig().set("ShowComingOut", false);
                    Jinro.getMain().saveConfig();

                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.setPlayerListName(p.getName() + " ");
                        if(Yakusyoku.getDeath(p.getPlayer())){
                            p.setPlayerListName(ChatColor.BLACK + "[霊界] "+ p.getName() + " ");
                        }
                        if(p.hasPermission("axtuki1.Jinro.GameMaster")){
                            p.setPlayerListName(ChatColor.YELLOW + "[GM] "+p.getName()+" ");
                        }
                    }

                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "カミングアウトの表示を無効化しました。");
                } else {
                    Jinro.getMain().getConfig().set("ShowComingOut", true);
                    Jinro.getMain().saveConfig();

                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.setPlayerListName(p.getName() + " ");
                        Yakusyoku y = ComingOut.getComingOut(p);
                        if(y != null){
                            if( y == Yakusyoku.黒 ){
                                p.setPlayerListName("[●] " + p.getName() + " ");
                            } else if( y == Yakusyoku.白 ){
                                p.setPlayerListName("[○] " + p.getName() + " ");
                            } else {
                                p.setPlayerListName(Yakusyoku.getYakuColor(y) + "[" + y.toString() + "] " + p.getName() + " ");
                            }
                        }
                        if(Yakusyoku.getDeath(p.getPlayer())){
                            p.setPlayerListName(ChatColor.BLACK + "[霊界] "+ p.getName() + " ");
                        }
                        if(p.hasPermission("axtuki1.Jinro.GameMaster")){
                            p.setPlayerListName(ChatColor.YELLOW + "[GM] "+p.getName()+" ");
                        }
                    }

                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "カミングアウトの表示を有効化しました。");
                }
                return;
            }
            if(args[2].equalsIgnoreCase("on")){
                Jinro.getMain().getConfig().set("ShowComingOut", true);
                Jinro.getMain().saveConfig();

                for(Player p : Bukkit.getOnlinePlayers()){
                    p.setPlayerListName(p.getName() + " ");
                    Yakusyoku y = ComingOut.getComingOut(p);
                    if(y != null){
                        if( y == Yakusyoku.黒 ){
                            p.setPlayerListName("[●] " + p.getName() + " ");
                        } else if( y == Yakusyoku.白 ){
                            p.setPlayerListName("[○] " + p.getName() + " ");
                        } else {
                            p.setPlayerListName(Yakusyoku.getYakuColor(y) + "[" + y.toString() + "] " + p.getName() + " ");
                        }
                    }
                    if(Yakusyoku.getDeath(p.getPlayer())){
                        p.setPlayerListName(ChatColor.BLACK + "[霊界] "+ p.getName() + " ");
                    }
                    if(p.hasPermission("axtuki1.Jinro.GameMaster")){
                        p.setPlayerListName(ChatColor.YELLOW + "[GM] "+p.getName()+" ");
                    }
                }

                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "カミングアウトの表示を有効化しました。");
                return;
            } else if(args[2].equalsIgnoreCase("off")){
                Jinro.getMain().getConfig().set("ShowComingOut", false);
                Jinro.getMain().saveConfig();

                for(Player p : Bukkit.getOnlinePlayers()){
                    p.setPlayerListName(p.getName() + " ");
                    if(Yakusyoku.getDeath(p.getPlayer())){
                        p.setPlayerListName(ChatColor.BLACK + "[霊界] "+ p.getName() + " ");
                    }
                    if(p.hasPermission("axtuki1.Jinro.GameMaster")){
                        p.setPlayerListName(ChatColor.YELLOW + "[GM] "+p.getName()+" ");
                    }
                }

                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "カミングアウトの表示を無効化しました。");
                return;
            }
        }


        if(args[1].equalsIgnoreCase("ChatCounter")){
            if(args.length == 2){
                if(Jinro.getMain().getConfig().getBoolean("ChatCounterEnable")){
                    Jinro.getMain().getConfig().set("ChatCounterEnable", false);
                    Jinro.getMain().saveConfig();
                    Objective Obj_C = ScoreBoard.getChatCounterObj();
                    if(Obj_C != null){
                        Obj_C.setDisplaySlot(null);
                    }
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "チャットカウンターを無効化しました。");
                } else {
                    Jinro.getMain().getConfig().set("ChatCounterEnable", true);
                    Jinro.getMain().saveConfig();
                    Objective Obj_C = ScoreBoard.getChatCounterObj();
                    if(Obj_C != null){
                        Obj_C.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                    }
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "チャットカウンターを有効化しました。");
                }
                return;
            }
            if(args[2].equalsIgnoreCase("on")){
                Jinro.getMain().getConfig().set("ChatCounterEnable", true);
                Jinro.getMain().saveConfig();
                Objective Obj_C = ScoreBoard.getChatCounterObj();
                if(Obj_C != null){
                    Obj_C.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                }
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "チャットカウンターを有効化しました。");
                return;
            } else if(args[2].equalsIgnoreCase("off")){
                Jinro.getMain().getConfig().set("ChatCounterEnable", false);
                Jinro.getMain().saveConfig();
                Objective Obj_C = ScoreBoard.getChatCounterObj();
                if(Obj_C != null){
                    Obj_C.setDisplaySlot(null);
                }
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "チャットカウンターを無効化しました。");
                return;
            }
        }



        if(args[1].equalsIgnoreCase("NoticeComingOut")){
            if(args.length == 2){
                if(Jinro.getMain().getConfig().getBoolean("NoticeComingOut")){
                    Jinro.getMain().getConfig().set("NoticeComingOut", false);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "COを含む発言のサウンド通知を無効化しました。");
                } else {
                    Jinro.getMain().getConfig().set("NoticeComingOut", true);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "COを含む発言のサウンド通知を有効化しました。");
                }
                return;
            }
            if(args[2].equalsIgnoreCase("on")){
                Jinro.getMain().getConfig().set("NoticeComingOut", true);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "COを含む発言のサウンド通知を有効化しました。");
                return;
            } else if(args[2].equalsIgnoreCase("off")){
                Jinro.getMain().getConfig().set("NoticeComingOut", false);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "COを含む発言のサウンド通知を無効化しました。");
                return;
            }
        }

        if(args[1].equalsIgnoreCase("ShowUseNingyou")){
            if(args.length == 2){
                if(Jinro.getMain().getConfig().getBoolean("ShowUseNingyou")){
                    Jinro.getMain().getConfig().set("ShowUseNingyou", false);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "人形使いの人形使用の通知を無効化しました。");
                } else {
                    Jinro.getMain().getConfig().set("ShowUseNingyou", true);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "人形使いの人形使用の通知を有効化しました。");
                }
                return;
            }
            if(args[2].equalsIgnoreCase("on")){
                Jinro.getMain().getConfig().set("ShowUseNingyou", true);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "人形使いの人形使用の通知を有効化しました。");
                return;
            } else if(args[2].equalsIgnoreCase("off")){
                Jinro.getMain().getConfig().set("ShowUseNingyou", false);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "人形使いの人形使用の通知を無効化しました。");
                return;
            }
        }

        if(args[1].equalsIgnoreCase("LoginSpectatorMode")){
            if(args.length == 2){
                if(Jinro.getMain().getConfig().getBoolean("LoginSpectatorMode")){
                    Jinro.getMain().getConfig().set("LoginSpectatorMode", false);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "途中観戦モードを無効化しました。");
                } else {
                    Jinro.getMain().getConfig().set("LoginSpectatorMode", true);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "途中観戦モードを有効化しました。");
                }
                return;
            }
            if(args[2].equalsIgnoreCase("on")){
                Jinro.getMain().getConfig().set("LoginSpectatorMode", true);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "途中観戦モードを有効化しました。");
                return;
            } else if(args[2].equalsIgnoreCase("off")){
                Jinro.getMain().getConfig().set("LoginSpectatorMode", false);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "途中観戦モードを無効化しました。");
                return;
            }
        }

        if(args[1].equalsIgnoreCase("WinnerMsgUse")){
            if(args.length == 2){
                if(Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle")){
                    Jinro.getMain().getConfig().set("WinnerMsgUseTitle", false);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "勝利判定はチャットで発表します。");
                } else {
                    Jinro.getMain().getConfig().set("WinnerMsgUseTitle", true);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "勝利判定はタイトルで発表します。");
                }
                return;
            }
            if(args[2].equalsIgnoreCase("title")){
                Jinro.getMain().getConfig().set("WinnerMsgUseTitle", true);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "勝利判定はタイトルで発表します。");
                return;
            } else if(args[2].equalsIgnoreCase("chat")){
                Jinro.getMain().getConfig().set("WinnerMsgUseTitle", false);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "勝利判定はチャットで発表します。");
                return;
            }
        }

        if(args[1].equalsIgnoreCase("ShowYakusyoku")){
            if(args.length == 2){
                if(Jinro.getMain().getConfig().getBoolean("ShowYakusyoku")){
                    Jinro.getMain().getConfig().set("ShowYakusyoku", false);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "占い/霊能の結果は村人と人狼で表示します。");
                } else {
                    Jinro.getMain().getConfig().set("ShowYakusyoku", true);
                    Jinro.getMain().saveConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "占い/霊能の結果は[狂人/妖狐]以外は正しく表示します。");
                }
                return;
            }
            if(args[2].equalsIgnoreCase("Yakusyoku")){
                Jinro.getMain().getConfig().set("ShowYakusyoku", true);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "占い/霊能の結果は[狂人/妖狐]以外は正しく表示します。");
                return;
            } else if(args[2].equalsIgnoreCase("Murakami")){
                Jinro.getMain().getConfig().set("ShowYakusyoku", false);
                Jinro.getMain().saveConfig();
                Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "占い/霊能の結果は村人と人狼で表示します。");
                return;
            }
        }

        if(args[1].equalsIgnoreCase("SetGameTime")){
            if(args.length == 2){
                Jinro.sendCmdHelp(sender,"/jinro_ad option SetGameTime <Night | Discussion> <秒数>" ,"ゲーム内で使用する時間の設定をします。");
                return;
            }
            if(args.length == 3){
                if(args[2].equalsIgnoreCase("Night")){
                    Jinro.sendMessage(sender, "現在の夜の時間は "+ Jinro.getMain().getConfig().getInt("NightTime") +" 秒です。", LogLevel.INFO);
                    return;
                } else if(args[2].equalsIgnoreCase("Discussion")){
                    Jinro.sendMessage(sender, "現在の議論の時間は "+ Jinro.getMain().getConfig().getInt("DiscussionTime") +" 秒です。", LogLevel.INFO);
                    return;
                }
            }
            if(args.length == 4){
                if(args[2].equalsIgnoreCase("Night")){
                    Jinro.getMain().getConfig().set("NightTime" , Integer.parseInt(args[3]));
                    Jinro.getMain().saveConfig();
                    Jinro.getMain().reloadConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "夜時間は "+ Jinro.getMain().getConfig().getInt("NightTime") +" 秒です。");
                    return;
                } else if(args[2].equalsIgnoreCase("Discussion")){
                    Jinro.getMain().getConfig().set("DiscussionTime" , Integer.parseInt(args[3]));
                    Jinro.getMain().saveConfig();
                    Jinro.getMain().reloadConfig();
                    Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.GREEN + "議論時間は "+ Jinro.getMain().getConfig().getInt("DiscussionTime") +" 秒です。");                    return;
                }
            }
        }

        SendHelpMsg(sender);
        return;
    }

    private static void SendHelpMsg(CommandSender sender) {
        Jinro.sendCmdHelp(sender,"... SetGameTime <Night | Discussion> <秒数>" ,"ゲーム内で使用する時間の設定をします。");
        Jinro.sendCmdHelp(sender,"... ChatCounter <on | off> " ,"チャットカウンターの有効/無効を変更します。");
        Jinro.sendCmdHelp(sender,"... ShowComingOut <on | off> " , "カミングアウトの表示の有効/無効を変更します。");
        Jinro.sendCmdHelp(sender,"... NoticeComingOut <on | off> " , "COを含む発言のサウンド通知の有効/無効を変更します。");
        Jinro.sendCmdHelp(sender,"... WinnerMsgUse <chat | title> " , "勝利判定の発表に使用する方法を変更します。");
        Jinro.sendCmdHelp(sender,"... ShowYakusyoku <yakusyoku | murakami> " , "占い/霊能の能力で役職を表示するか変更します。");
        Jinro.sendCmdHelp(sender,"... ShowUseNingyou <on | off> " , "人形使いの人形使用通知の有効/無効を変更します。");
        Jinro.sendCmdHelp(sender,"... LoginSpectatorMode <on | off>", "プレイヤー以外のログイン時に観戦モードにする機能を切り替えます。");
        return;
    }

    enum RulePreset{
        MinecraftJinro,
        Custom
    }

    public static RulePreset getPreset(){
        RulePreset out = RulePreset.Custom;
        int NightTime = Jinro.getMain().getConfig().getInt("NightTime"),
                DiscussionTime = Jinro.getMain().getConfig().getInt("DiscussionTime");
        boolean ShowComingOut = Jinro.getMain().getConfig().getBoolean("ShowComingOut"),
                ChatCounter = Jinro.getMain().getConfig().getBoolean("ChatCounterEnable"),
                SoundComingOut = Jinro.getMain().getConfig().getBoolean("NoticeComingOut"),
                ShowYakusyoku = Jinro.getMain().getConfig().getBoolean("ShowYakusyoku"),
                ShowUseNingyou = Jinro.getMain().getConfig().getBoolean("ShowUseNingyou"),
                WinnerMsgUseTitle = Jinro.getMain().getConfig().getBoolean("WinnerMsgUseTitle");

        // MinecraftJinro
        if(NightTime == 180 && DiscussionTime == 300 && ShowComingOut && ChatCounter && SoundComingOut
                && !ShowYakusyoku && !ShowUseNingyou){
            out = RulePreset.MinecraftJinro;
        }
        return out;
    }

    public static void setPreset(RulePreset r){
        if(r == RulePreset.MinecraftJinro){
            Jinro.getMain().getConfig().set("NightTime", 180);
            Jinro.getMain().getConfig().set("DiscussionTime", 300);
            Jinro.getMain().getConfig().set("ShowComingOut", true);
            Jinro.getMain().getConfig().set("ChatCounterEnable", true);
            Jinro.getMain().getConfig().set("NoticeComingOut", true);
            Jinro.getMain().getConfig().set("ShowYakusyoku", false);
            Jinro.getMain().getConfig().set("ShowUseNingyou", false);
        }
        Jinro.getMain().saveConfig();
        Jinro.getMain().reloadConfig();
    }
}
