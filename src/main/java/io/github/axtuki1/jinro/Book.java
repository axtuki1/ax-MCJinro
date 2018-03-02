package io.github.axtuki1.jinro;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Book {

    public static boolean Player(CommandSender sender, String commandLabel, String[] args) {
        /*
         * args[0] = log
         */
        if(args.length == 1){
            sender.sendMessage("** ヘルプメッセージ **");
            return true;
        }
        if(args[1].equalsIgnoreCase("vote")){
            if(Timer.getDay() <= 2){
                sender.sendMessage(Jinro.getPrefix() + ChatColor.RED + "まだ記録がありません。");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "======" + ChatColor.GREEN + "[" + ChatColor.AQUA + "投票結果" + ChatColor.GREEN + "]" + ChatColor.RED + "======");
            for(int i = 2; Timer.getDay() >= i; i++){
                Player a = Yakusyoku.getExecution(i);
                String out = "";
                if( a == null ){
                    out = "なし";
                } else {
                    out = a.getName();
                }
                sender.sendMessage(ChatColor.GREEN + "" + i +"日目: " + out);
            }
            return true;
        } else if(args[1].equalsIgnoreCase("player")){

        }

        return true;
    }

}
