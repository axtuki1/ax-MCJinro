package io.github.axtuki1.jinro;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help {
    public static void guideAdmin(CommandSender sender, String commandLabel, String[] args) {
        if(args.length == 1){
            sender.sendMessage(ChatColor.RED + "========== " + Jinro.getPrefix() + ChatColor.RED + "==========");
            if( Utility.getPlayer( sender.getName() ).hasPermission("axtuki1.Jinro.GameMaster") ){
                sender.sendMessage(new String[]{
                        ChatColor.GREEN + "ゲームマスター向けのガイドはこちら",
                        ChatColor.AQUA + "/jinro_ad guide cycle "+ ChatColor.GREEN + "流れについての解説です。"
                });
            }
        } else if(args[1].equalsIgnoreCase("cycle")){
            if(args.length == 2){
                CycleGuideAdmin(sender);
            } else if(args.length == 3){
                switch(args[2]){
                    case "1":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "1. 前準備",
                                ChatColor.GREEN + "まずは初期化してください。",
                                CmdColor("/jinro_ad initialization"),
                                ChatColor.GREEN + "これで必要なものが生成され、不要なものは削除されます。",
                                ChatColor.GREEN + "次に各プレイヤーに役を振っていきます。",
                                CmdColor("/jinro_ad yakusyoku <役職> <プレイヤー>"),
                                ChatColor.GREEN + "役職に使用できる文字は、"+CmdColor("/jinro_ad yakusyoku"),
                                ChatColor.GREEN+"で確認できます。"
                        });
                        break;
                    case "2":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "2. 夜",
                                ChatColor.GREEN + "GMには全プレイヤーのチャット、役職が確認できます。",
                                ChatColor.DARK_RED + "[人狼] <" + sender.getName() + "> 人狼の場合はこのように表示され、狼が鳴きます。",
                                ChatColor.WHITE + "[村人] <" + sender.getName() + "> 村人の場合はこのように表示されます。",
                                ChatColor.GREEN + "夜時間は人狼同士、共有者同士は会話でき、それ以外の役職は独り言のように",
                                ChatColor.GREEN + "自分のチャット画面のみに表示されます。",
                                ChatColor.GREEN + "この時間、GMにやることはありません。"
                        });
                        break;
                    case "3":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "3. 朝(議論時間)",
                                ChatColor.GREEN + "夜が明け、朝になると議論の時間になります。",
                                ChatColor.GREEN + "チャットは参加者と同じように表示されます。",
                                ChatColor.GREEN + "GMは代理でカミングアウトコマンドを打つことができます。",
                                CmdColor("/jinro_ad co <プレイヤー> <役職>"),
                                ChatColor.GREEN + "カミングアウトの解除もできます。",
                                CmdColor("/jinro_ad co <プレイヤー> del"),
                                ChatColor.GREEN + "カミングアウトと役職の確認もできます。",
                                CmdColor("/jinro_ad co <プレイヤー>"),
                        });
                        break;
                    case "4":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "4. 投票時間",
                                ChatColor.GREEN + "投票時間ではチャットは夜時間と同じように表示されます。",
                                ChatColor.GREEN + "プレイヤーが投票した時に、",
                                Jinro.getPrefix() + ChatColor.GREEN + sender.getName() + " -> " + sender.getName() + " (1票)",
                                ChatColor.GREEN + "このように表示されます。",
                                ChatColor.GREEN + "投票結果の開票は",
                                CmdColor("/jinro_ad touhyou open"),
                                ChatColor.GREEN + "投票してない人がいても強制的に開票するには",
                                CmdColor("/jinro_ad touhyou open force"),
                                ChatColor.GREEN + "同数票が多かった場合に外部で決めた時に受刑者を指定するときは",
                                CmdColor("/jinro_ad touhyou kill <プレイヤー>"),
                        });
                        break;
                    case "5":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "5. 処刑",
                                ChatColor.GREEN + "GMは任意の方法で処刑(殺害)してください。",
                                ChatColor.GREEN + "死亡した時点で処理がされます。",
                                ChatColor.GREEN + "自動で進行はしないので、",
                                CmdColor("/jinro_ad next"),
                                ChatColor.GREEN + "を実行してください。",
                                ChatColor.GREEN + "コマンドが実行されると勝利判定の検証がされます。"
                        });
                        break;
                    case "6":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "6. 勝敗",
                                ChatColor.GREEN + "村人陣営が役職[人狼]より同数、もしくはそれ以下になった場合に、",
                                ChatColor.GREEN + "人狼側の勝利となります。",
                                ChatColor.GREEN + "役職[人狼]が全滅した場合に",
                                ChatColor.GREEN + "村人側の勝利となります。",
                                ChatColor.GREEN + "そして上記の条件を満たした状態で役職[妖狐]が生存していれば、",
                                ChatColor.GREEN + "妖狐の一人勝ちとなります。",
                                ChatColor.GREEN + "この判定は処刑後の"+CmdColor("/jinro_ad next") + ChatColor.GREEN + "後に行われ、",
                                ChatColor.GREEN + "当てはまらない場合は夜に戻り、このサイクルが繰り返されます。",
                        });
                        break;
                    case "7":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "7. 後始末",
                                ChatColor.GREEN + "ゲーム終了後はチャットがもとに戻ります。",
                                ChatColor.GREEN + "初期化も忘れずにしておきましょう。",
                                CmdColor("/jinro_ad initialization"),
                        });
                        break;
                    case "8":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "8. 進行上の注意",
                                ChatColor.GREEN + "コマンド連打によるミスに注意しましょう。",
                                CmdColor("/jinro_ad next"),
                                ChatColor.GREEN + "コマンドには投票時間をスキップしない安全装置がついています。",
                                ChatColor.GREEN + "スキップする場合は",
                                CmdColor("/jinro_ad next force"),
                                ChatColor.GREEN + "を実行してください。",
                        });
                        break;
                        //                                ChatColor.GREEN + "",
                    default:
                        CycleGuideAdmin(sender);
                        break;
                }
            }
        }

    }

    public static String CmdColor(String s){
        return ChatColor.AQUA+s;
    }

    private static void CycleGuideAdmin(CommandSender sender){
        sender.sendMessage(new String[]{
                ChatColor.RED + "==================================",
                ChatColor.GREEN + "ゲームの進行についてのガイドです。",
                ChatColor.AQUA + "/jinro_ad guide cycle <項目番号[半角]>",
                ChatColor.GREEN + "1. 前準備",
                ChatColor.GREEN + "2. 夜",
                ChatColor.GREEN + "3. 朝(議論時間)",
                ChatColor.GREEN + "4. 投票時間",
                ChatColor.GREEN + "5. 処刑",
                ChatColor.GREEN + "6. 勝敗",
                ChatColor.GREEN + "7. 後始末",
                ChatColor.GREEN + "8. 進行上の注意",
                ChatColor.GREEN + "ゲームマスターをGMと表記します。",
        });
    }

    public static void guide(CommandSender sender, String commandLabel, String[] args) {
        if(args.length == 1){
            sender.sendMessage(ChatColor.RED + "========== " + Jinro.getPrefix() + ChatColor.RED + "==========");
            sender.sendMessage(new String[]{
                    ChatColor.GREEN + "参加者向けのガイドはこちら",
                    ChatColor.AQUA + "/jinro guide cycle "+ ChatColor.GREEN + "流れについての解説です。"
            });
        } else if(args[1].equalsIgnoreCase("yakusyoku")){
            if(args.length == 2){
                YakusyokuGuide(sender);
            } else if(args.length == 3) {
                switch (Yakusyoku.getNameToYaku(args[2])) {
                    case 村人:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "村人",
                                ChatColor.GREEN + "役特有の能力はありません。",
                                ChatColor.GREEN + "あなたが持っている推察力とトークスキルだけが頼りです。",
                                ChatColor.GREEN + "積極的に議論に参加して、村を勝利に導きましょう。",
                                ChatColor.GREEN + "目標は村人側の勝利です。",
                        });
                        break;
                    case 占い師:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "占い師",
                                ChatColor.GREEN + "夜時間の間に生きているプレイヤーを占う事ができます。",
                                ChatColor.GREEN + "生存者の役職が判定できるのはこの役職のみです。",
                                ChatColor.GREEN + "これを利用して人狼/狂人は場を乱してきます。注意しましょう。",
                                ChatColor.GREEN + "目標は村人側の勝利です。",
                        });
                        break;
                    // ChatColor.GREEN + "",
                    case 霊能者:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "霊能者",
                                ChatColor.GREEN + "処刑されたプレイヤーの役職がわかります。",
                                ChatColor.GREEN + "占い師の発言と照らし合わせながら発言しましょう。",
                                ChatColor.GREEN + "これを利用して人狼/狂人は場を乱してきます。注意しましょう。",
                                ChatColor.GREEN + "目標は村人側の勝利です。",
                        });
                        break;
                    case 爆弾魔:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "爆弾魔",
                                ChatColor.GREEN + "処刑時に誰かをランダムで道連れにします。",
                                ChatColor.GREEN + "また人狼に噛まれた場合はその噛んだ人狼を道連れにします。",
                                ChatColor.GREEN + "目標は村人側の勝利です。",
                        });
                        break;
                    case 狩人:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "狩人",
                                ChatColor.GREEN + "人狼による夜の襲撃から誰かを守ることができます。",
                                ChatColor.GREEN + "しかし自分自身は守れないことを注意してください。",
                                ChatColor.GREEN + "目標は村人側の勝利です",
                        });
                        break;
                    case 共有者:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "共有者",
                                ChatColor.GREEN + "主に議論のまとめ役を務めます。",
                                ChatColor.GREEN + "基本的に2名指定され、",
                                ChatColor.GREEN + "共有者同士では夜の間チャットすることができます。",
                                ChatColor.GREEN + "目標は村人側の勝利です",
                        });
                        break;
                    case 人狼:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "人狼",
                                ChatColor.GREEN + "初日以外の夜の間に誰か一人噛み殺す事ができます。",
                                ChatColor.GREEN + "噛み殺しが成功したか否かは、翌日の朝に判明します。",
                        });
                        break;
                    case 狂人:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "狂人",
                                ChatColor.GREEN + "人であるくせに人狼の味方をするよくわかんねぇ奴です。",
                                ChatColor.GREEN + "基本的に騙って村を混乱させます。",
                                ChatColor.GREEN + "自分が死んだとしても、人狼が勝利すれば勝利となります。",
                        });
                        break;
                    case 妖狐:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "妖狐",
                                ChatColor.GREEN + "第三陣営と呼ばれる人狼/村人のどちらでもない立場です。",
                                ChatColor.GREEN + "人狼に噛まれても死亡しません。(通知はされない)",
                                ChatColor.GREEN + "どちらかの勝利条件を満たした段階で生存していれば勝利です。",
                                ChatColor.GREEN + "しかし占い師に占われると死亡してしまいます。",
                        });
                        break;
                    case ニワトリ:
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "ニワトリ",
                                ChatColor.GREEN + "コマンドによる能力の使用はできませんが、",
                                ChatColor.GREEN + "夜が明けるとニワトリが鳴くメッセージが出ます。それだけです。",
                                ChatColor.GREEN + "村人側の勝利を目指しましょう。",
                        });
                        break;
                    case 人形使い:
                        sender.sendMessage(new String[]{
                            ChatColor.RED + "==================================",
                            ChatColor.YELLOW + "人形使い [オリジナル職]",
                            ChatColor.GREEN + "人狼による襲撃から一度だけ人形を使用して身を守る事ができます。",
                            ChatColor.GREEN + "なお狩人による護衛があった場合はそれが優先されます。",
                            ChatColor.GREEN + "そのほかは村人と同じです。村人側の勝利を目指しましょう。",
                        });
                        break;
                    case コスプレイヤー:
                        sender.sendMessage(new String[]{
                            ChatColor.RED + "==================================",
                            ChatColor.YELLOW + "コスプレイヤー",
                            ChatColor.GREEN + "狩人と同様に夜に誰か一人だけ護衛できます。",
                            ChatColor.GREEN + "しかし占い/霊能による判定からは人狼と判定されます。",
                            ChatColor.GREEN + "村人側の勝利を目指しましょう。",
                        });
                        break;
                    /*
                    case :
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "",
                                ChatColor.GREEN + "",
                                ChatColor.GREEN + "",
                                ChatColor.GREEN + "",
                                ChatColor.GREEN + "",
                        });
                        break;
                     */
                }
            }
        } else if(args[1].equalsIgnoreCase("cycle")){
            if(args.length == 2){
                CycleGuide(sender);
            } else if(args.length == 3){
                switch(args[2]){
                    case "1":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "1. 前準備",
                                ChatColor.GREEN + "メモ帳などのテキストエディタ系を予め起動しておき、",
                                ChatColor.GREEN + "気になる点を素早くまとめられるようにしておくと有利になるかもしれません",

                        });
                        break;
                    case "2":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "2. 夜",
                                ChatColor.GREEN + "夜時間ではチャットは全体には聞こえません。",
                                ChatColor.GREEN + "ただし、人狼と共有者の役職ではお互いにチャットが聞こえます。",
                                ChatColor.GREEN + "人狼、狩人、占い師はこの時間に行動ができます。",
                                ChatColor.GREEN + "人狼は初日は噛むことができません。",
                        });
                        break;
                    case "3":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "3. 朝(議論時間)",
                                ChatColor.GREEN + "夜が明け、朝になると議論の時間になります。",
                                ChatColor.GREEN + "\"CO\"という単語が入っていると音がなります。(設定で変更可)",
                                ChatColor.GREEN + "カミングアウトは",
                                CmdColor("/jinro co <役職>"),
                                ChatColor.GREEN + "を実行するとできます。",
                                ChatColor.GREEN + "COすると、プレイヤーリスト、チャットにCOした役職が表示されます(表示非表示を変更可)",
                        });
                        break;
                    case "4":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "4. 投票時間",
                                ChatColor.GREEN + "投票時間ではチャットは夜時間と同じように表示されます。",
                                ChatColor.GREEN + "投票は紙を手に持ち、",
                                CmdColor("/jinro touhyou <プレイヤー>"),
                                ChatColor.GREEN + "を実行することで可能です。",
                                ChatColor.GREEN + "このあとはGMが進行するまでお待ち下さい。",
                        });
                        break;
                    case "5":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "5. 処刑",
                                ChatColor.GREEN + "投票結果によって決定されたプレイヤーは処刑されなければなりません。",
                                ChatColor.GREEN + "処刑の方法はGMに委ねられています。",
                                ChatColor.GREEN + "最期を見届けましょう。",
                        });
                        break;
                    case "6":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "6. 勝敗",
                                ChatColor.GREEN + "役職[村人]が役職[人狼]より同数、もしくはそれ以下になった場合に、",
                                ChatColor.GREEN + "人狼側の勝利となります。",
                                ChatColor.GREEN + "役職[人狼]が全滅した場合に",
                                ChatColor.GREEN + "村人側の勝利となります。",
                                ChatColor.GREEN + "そして上記の条件を満たした状態で役職[妖狐]が生存していれば、",
                                ChatColor.GREEN + "妖狐の一人勝ちとなります。",
                                ChatColor.GREEN + "この判定は処刑後の"+CmdColor("GMのコマンド") + ChatColor.GREEN + "後に行われ、",
                                ChatColor.GREEN + "当てはまらない場合は夜に戻り、このサイクルが繰り返されます。",
                        });
                        break;
                    case "7":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "7. 進行上の注意",
                                ChatColor.GREEN + "コマンドミス、ミスタイプに注意しましょう。",
                        });
                        break;
                    //                                ChatColor.GREEN + "",
                    default:
                        CycleGuide(sender);
                        break;
                }
            }
        } else if(args[1].equalsIgnoreCase("word")) {
            if (args.length == 2) {
                WordGuide(sender);
            } else if (args.length == 3) {
                switch (args[2]) {
                    case "1":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "カミングアウト (CO)",
                                ChatColor.GREEN + "自分が能力者で有ることを公表すること。",
                                ChatColor.GREEN + "ただし、任意の役職でCOできるため議論の中で見極める事が重要。",

                        });
                        break;
                    case "2":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "白/黒",
                                ChatColor.GREEN + "占い/霊能の判定結果を示す。",
                                ChatColor.GREEN + "村人であれば白、人狼であれば黒となる。",
                                ChatColor.GREEN + "ただし、白の中には狂人なども含まれるため発言から判断する必要がある。",
                        });
                        break;
                    case "3":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "吊り",
                                ChatColor.GREEN + "投票結果による処刑のこと。",
                        });
                        break;
                    case "4":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "グレーランダム (グレラン)",
                                ChatColor.GREEN + "誰を投票するかを決める際に使われる戦略のこと。",
                                ChatColor.GREEN + "COや占われていない所謂グレーを各自判断で投票する。",
                        });
                        break;
                    case "5":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "対抗CO",
                                ChatColor.GREEN + "他人のCOに対抗してCOすること。",
                                ChatColor.GRAY + "没文章",
                                ChatColor.GRAY + "名乗り出た能力者に対して「さてはおめー偽物だろ」と挑戦すること。",
                        });
                        break;
                    case "6":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "パンダ",
                                ChatColor.GREEN + "複数の占い師に占われていて、白と黒の両方の判定が出た人のこと。",
                        });
                        break;
                    case "7":
                        sender.sendMessage(new String[]{
                                ChatColor.RED + "==================================",
                                ChatColor.YELLOW + "スライド",
                                ChatColor.GREEN + "CO後に他の役職でCOすること。",
                        });
                        break;
                    //                                ChatColor.GREEN + "",
                    default:
                        WordGuide(sender);
                        break;
                }
            }
        }
    }

    private static void YakusyokuGuide(CommandSender sender) {
        sender.sendMessage(new String[]{
                ChatColor.RED + "==================================",
                ChatColor.GREEN + "役職についてのガイドです。",
                ChatColor.AQUA + "/jinro guide yakusyoku <役職名>",
                ChatColor.GREEN + "村人:   murabito",
                ChatColor.GREEN + "占い師: uranai",
                ChatColor.GREEN + "霊能者: reinou",
                ChatColor.GREEN + "狩人:   kariudo",
                ChatColor.GREEN + "共有者: kyouyu",
                ChatColor.GREEN + "爆弾魔: bakudan",
                ChatColor.GREEN + "人狼:   jinro",
                ChatColor.GREEN + "狂人:   kyoujin",
                ChatColor.GREEN + "妖狐:   yoko",
        });
    }

    private static void WordGuide(CommandSender sender){
        sender.sendMessage(new String[]{
                ChatColor.RED + "==================================",
                ChatColor.GREEN + "用語についてのガイドです。",
                ChatColor.AQUA + "/jinro guide word <項目番号[半角]>",
                ChatColor.GREEN + "1. カミングアウト(CO)",
                ChatColor.GREEN + "2. 白/黒",
                ChatColor.GREEN + "3. 吊り",
                ChatColor.GREEN + "4. グレーランダム (グレラン)",
                ChatColor.GREEN + "5. 対抗CO",
                ChatColor.GREEN + "6. パンダ",
                ChatColor.GREEN + "7. スライド",
                ChatColor.GREEN + "ゲームマスターをGMと表記します。",
        });
    }

    private static void CycleGuide(CommandSender sender){
        sender.sendMessage(new String[]{
                ChatColor.RED + "==================================",
                ChatColor.GREEN + "ゲームの進行についてのガイドです。",
                ChatColor.AQUA + "/jinro guide cycle <項目番号[半角]>",
                ChatColor.GREEN + "1. 前準備",
                ChatColor.GREEN + "2. 夜",
                ChatColor.GREEN + "3. 朝(議論時間)",
                ChatColor.GREEN + "4. 投票時間",
                ChatColor.GREEN + "5. 処刑",
                ChatColor.GREEN + "6. 勝敗",
                ChatColor.GREEN + "7. 進行上の注意",
                ChatColor.GREEN + "ゲームマスターをGMと表記します。",
        });
    }

}
