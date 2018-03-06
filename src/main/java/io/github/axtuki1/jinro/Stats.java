package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public enum Stats {
    Death, Action, Win, Play;

    public enum death{
        All, Kami, Execution, Explosion, Curse, GM;
    }

    public enum action{
        Kami, Kami_Fail, Kami_Success, Uranai, Goei, Goei_Fail, Goei_Success
    }

    public enum win{
        All, murabito, jinro, uranai, reinou, kyoujin, kariudo, kyouyu, yoko, bakudan;
    }

    public enum play{
        All, murabito, jinro, uranai, reinou, kyoujin, kariudo, kyouyu, yoko, bakudan;
    }


    public String getPath(death d){
        return "Deash." + d.toString();
    }

    public String getPath(action d){
        return "Action." + d.toString();
    }

    public String getPath(win d){
        return "Win." + d.toString();
    }

    // 実績を運用する上での統計を追加する。

    public static void setWin(Yakusyoku yaku){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(Yakusyoku.getYaku(p) == yaku){
                int win = getWin(p, Yakusyoku.getYakuToName(yaku));
                int allwin = getWin(p);
                setWin(p, Yakusyoku.getYakuToName(yaku), (win + 1), (allwin + 1));
            }
        }
    }

    public static void setWin(Player p, String yaku, int win, int allwin){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Win`WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserStats-Win` (`UUID`) values ('" + p.getUniqueId().toString() + "') on duplicate key update `UUID`='" + p.getUniqueId().toString() + "';");
                }
                Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Win` SET " +
                        "`"+yaku+"`='"+win+"'," +
                        "`All`='"+allwin+"'" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "setWin(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            }
        } else {
            set("Stats."+ p.getUniqueId() +".Win."+yaku, win);
            set("Stats."+ p.getUniqueId() +".Win.All", allwin);
        }
    }

    public static void setWin(Player p, String yaku, int win){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Win`WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserStats-Win` (`UUID`) values ('" + p.getUniqueId().toString() + "') on duplicate key update `UUID`='" + p.getUniqueId().toString() + "';");
                }
                Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Win` SET " +
                        "`"+yaku+"`='"+win+"'" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "setWin(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            }
        } else {
            set("Stats."+ p.getUniqueId() +".Win."+yaku, win);
        }
    }

    public static int getWin(Player p, String yaku) {
        int win = 0;
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID`,`"+yaku+"` FROM `Jinro-UserStats-Win` WHERE `UUID`='"+p.getUniqueId()+"';");
                while (result.next()) {
                    win = result.getInt(yaku);
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "getWin(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");            }
        } else {
            win = getInt("Stats." + p.getUniqueId() + ".Win." + yaku);
        }
        return win;
    }

    public static int getWin(Player p) {
        int win = 0;
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID`,`All` FROM `Jinro-UserStats-Win` WHERE `UUID`='"+p.getUniqueId()+"';");
                while (result.next()) {
                    win = result.getInt("All");
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "getWin(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");            }
        } else {
            win = getInt("Stats." + p.getUniqueId() + ".Win.All");
        }
        return win;
    }


    public static void setPlay(Yakusyoku yaku){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(Yakusyoku.getYaku(p) == yaku){
                int win = getPlay(p, Yakusyoku.getYakuToName(yaku));
                int allwin = getPlay(p);
                setPlay(p, Yakusyoku.getYakuToName(yaku), (win + 1), (allwin + 1));
            }
        }
    }

    public static void setPlay(Player p, String yaku, int win, int allwin){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Play` WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserStats-Play` (`UUID`) values ('" + p.getUniqueId().toString() + "') on duplicate key update `UUID`='" + p.getUniqueId().toString() + "';");
                }
                Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Play` SET " +
                        "`"+yaku+"`='"+win+"'," +
                        "`All`='"+allwin+"'" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "setWin(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            }
        } else {
            set("Stats."+ p.getUniqueId() +".Play."+yaku, win);
            set("Stats."+ p.getUniqueId() +".Play.All", allwin);
        }
    }

    public static void setPlay(Player p, String yaku, int win){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Play` WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserStats-Play` (`UUID`) values ('" + p.getUniqueId().toString() + "') on duplicate key update `UUID`='" + p.getUniqueId().toString() + "';");
                }
                Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Play` SET " +
                        "`"+yaku+"`='"+win+"'" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "setPlay(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            }
        } else {
            set("Stats."+ p.getUniqueId() +".Play."+yaku, win);
        }
    }

    public static int getPlay(Player p, String yaku) {
        int win = 0;
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID`,`"+yaku+"` FROM `Jinro-UserStats-Play` WHERE `UUID`='"+p.getUniqueId()+"';");
                while (result.next()) {
                    win = result.getInt(yaku);
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "getPlay(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");            }
        } else {
            win = getInt("Stats." + p.getUniqueId() + ".Play." + yaku);
        }
        return win;
    }

    public static int getPlay(Player p) {
        int win = 0;
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID`,`All` FROM `Jinro-UserStats-Play` WHERE `UUID`='"+p.getUniqueId()+"';");
                while (result.next()) {
                    win = result.getInt("All");
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "getPlay(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");            }
        } else {
            win = getInt("Stats." + p.getUniqueId() + ".Play.All");
        }
        return win;
    }


    public static int getDeath(Player p, death d){
        int Death = 0;
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID`,`"+ d.toString() +"` FROM `Jinro-UserStats-Death` WHERE `UUID`='"+p.getUniqueId()+"';");
                while (result.next()) {
                    Death = result.getInt(d.toString());
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "SQLException: ["+e.getErrorCode()+"]" + e.getSQLState() + " " + e.getLocalizedMessage() , "axtuki1.Jinro.GameMaster");
            }
        } else {
            Death = getInt("Stats." + p.getUniqueId() + ".Death." + d.toString());
        }
        return Death;
    }

    public static void setDeath(Player p, death d, int de){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Death` WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserStats-Death` (`UUID`) values ('"+p.getUniqueId().toString()+"') on duplicate key update `UUID`='"+ p.getUniqueId().toString() +"';");
                }
                int all = getDeath(p, Stats.death.All);
                if( !d.equals(death.GM) ){
                    all++;
                }
                Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Death` SET " +
                        "`"+d.toString()+"`='"+de+"'," +
                        "`All`='"+ all +"'" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "SQLException: ["+e.getErrorCode()+"]" + e.getSQLState() + " " + e.getLocalizedMessage() , "axtuki1.Jinro.GameMaster");
            }
        } else {
            set("Stats."+ p.getUniqueId() +".Death."+d.toString(), de);
            set("Stats."+ p.getUniqueId() +".Death.All", getInt("Stats."+ p.getUniqueId() +".Death.All") + 1);
        }
    }


    public static int getAction(Player p, action d){
        int Death = 0;
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID`,`"+ d.toString() +"` FROM `Jinro-UserStats-Action` WHERE `UUID`='"+p.getUniqueId()+"';");
                while (result.next()) {
                    Death = result.getInt(d.toString());
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "SQLException: ["+e.getErrorCode()+"]" + e.getSQLState() + " " + e.getLocalizedMessage() , "axtuki1.Jinro.GameMaster");
            }
        } else {
            Death = getInt("Stats." + p.getUniqueId() + ".Action." + d.toString());
        }
        return Death;
    }

    public static void setAction(Player p, action d, int de){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Action` WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserStats-Action` (`UUID`) values ('"+p.getUniqueId().toString()+"') on duplicate key update `UUID`='"+ p.getUniqueId().toString() +"';");
                }
                Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Action` SET " +
                        "`"+d.toString()+"`='"+de+"'" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "SQLException: ["+e.getErrorCode()+"]" + e.getSQLState() + " " + e.getLocalizedMessage() , "axtuki1.Jinro.GameMaster");
            }
        } else {
            set("Stats."+ p.getUniqueId() +".Action."+d.toString(), de);
        }
    }

    public static boolean getChallenge(UUID uuid, String d){
        boolean Death = false;
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID`,`"+ d +"` FROM `Jinro-UserStats-Challenge` WHERE `UUID`='"+uuid+"';");
                while (result.next()) {
                    Death = result.getBoolean(d);
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "getChallenge(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            }
        } else {
            Death = getBoolean("Stats." + uuid + ".Challenges." + d);
        }
        return Death;
    }

    public static boolean getChallenge(Player p, String d){
        boolean out = false;
        if(Jinro.getSQLEnable()){
            out = getChallenge(p.getUniqueId(), d);
        } else {
            out = getBoolean("Stats." + p.getUniqueId() + ".Challenges." + d);
        }
        return out;
    }

    public static void setChallenge(Player p, String d, boolean de){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Challenge` WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserStats-Challenge` (`UUID`) values ('"+p.getUniqueId().toString()+"') on duplicate key update `UUID`='"+ p.getUniqueId().toString() +"';");
                }
                if(de){
                    Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Challenge` SET " +
                            "`"+d+"`='1'" +
                            " WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
                } else {
                    Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Challenge` SET " +
                            "`"+d+"`='0'" +
                            " WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
                }

            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "setChallenge(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
                Bukkit.broadcastMessage("UPDATE `Jinro-UserStats-Challenge` SET " +
                        "`"+d+"`="+de+"" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            }
        } else {
            set("Stats."+ p.getUniqueId() +".Challenges."+d, de);
        }
    }

    public static void setPlayerData(Player p){
        if(Jinro.getSQLEnable()){
            try {
                ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserData` WHERE `UUID`='"+p.getUniqueId()+"';");
                if(re.getRow() == 0) {
                    Jinro.getSQL().Update("insert into `Jinro-UserData` (`UUID`) values ('"+p.getUniqueId().toString()+"') on duplicate key update `UUID`='"+ p.getUniqueId().toString() +"';");
                }
                Jinro.getSQL().Update("UPDATE `Jinro-UserData` SET " +
                        "`Name`='"+ p.getName() +"'" +
                        "WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "SQLException: ["+e.getErrorCode()+"]" + e.getSQLState() + " " + e.getLocalizedMessage() , "axtuki1.Jinro.GameMaster");
            }
        } else {
            return;
        }
    }

    public static void CheckChallengeChat(Player p,String msg){
        if(Jinro.getSQLEnable()){
            //Jinro.getMain().getLogger().info("Check");
            Config Challenge = new Config("challenge.yml");
            Challenge.reloadConfig();
            if(Challenge.get("ChatRecording") == null){
                return;
            }
            Challenge.getConfig().getConfigurationSection("ChatRecording").getKeys(false).forEach((String key) -> {
                if(Challenge.get("ChatRecording."+key+".target") == null){
                    return;
                }
                List<String> l = Challenge.getStringList("ChatRecording."+key+".target");
                if(l==null){
                    return;
                }
                for(String k : l){
                    //Jinro.getMain().getLogger().info(msg + " == " + k);
                    if( msg.equalsIgnoreCase(k) ) {
                        try {
                            ResultSet re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Chat` WHERE `UUID`='"+p.getUniqueId()+"';");
                            if(re.getRow() == 0) {
                                Jinro.getSQL().Update("insert into `Jinro-UserStats-Chat` (`UUID`) values ('"+p.getUniqueId().toString()+"') on duplicate key update `UUID`='"+ p.getUniqueId().toString() +"';");
                            }
                            re = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserStats-Chat` WHERE `UUID`='"+p.getUniqueId()+"';");
                            int i = 0;
                            while (re.next()){
                                i = re.getInt(key);
                            }
                            Jinro.getSQL().Update("UPDATE `Jinro-UserStats-Chat` SET " +
                                        "`"+key+"`='"+i+"'" +
                                        " WHERE `UUID` = '"+p.getUniqueId().toString()+"'");
                            break;
                        } catch(SQLException e) {
                            Bukkit.broadcast(Jinro.getPrefix() + "SQLException: ["+e.getErrorCode()+"]" + e.getSQLState() + " " + e.getLocalizedMessage() , "axtuki1.Jinro.GameMaster");
                        }
                    }
                }
            });
        } else {
            return;
        }
    }

    public static String getPlayerName(String UUID){
        String out = "";
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `Name` FROM `Jinro-UserData` WHERE `UUID`='"+UUID+"';");
                while (result.next()) {
                    out = result.getString("Name");
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "getPlayerName(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            }
            return out;
        } else {
            return null;
        }
    }

    public static String getUUID(String Name){
        String out = "";
        if(Jinro.getSQLEnable()){
            try {
                ResultSet result = Jinro.getSQL().Query("SELECT `UUID` FROM `Jinro-UserData` WHERE `Name`='"+Name+"';");
                while (result.next()) {
                    out = result.getString("UUID");
                }
            } catch(SQLException e) {
                Bukkit.broadcast(Jinro.getPrefix() + "getUUID(): SQLException: ["+e.getErrorCode()+"] " + e.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            }
            return out;
        } else {
            return null;
        }
    }

    public static int getStatsInt(Player p, Stats key, String name) {
        int data = 0;
        if(Jinro.getSQLEnable()){
            if(key == Stats.Action){
                data = getAction(p, action.valueOf(name));
            }
            if(key == Stats.Death){
                data = getDeath(p, death.valueOf(name));
            }
            if(key == Stats.Win){
                data = getWin(p, name);
            }
            if(key == Stats.Play){
                data = getPlay(p, name);
            }
        } else {
            data = getInt("Stats."+ p.getUniqueId() +"."+ key.toString() +"."+ name);
        }
        return data;
    }


    private static FileConfiguration customConfig = null;
    private static File customConfigFile = null;
    private static Jinro main = Jinro.getMain();
    private static String FileName = "stats.yml";

    public static void reloadConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(main.getDataFolder(), FileName);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(main.getResource(FileName), "UTF8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }

    public static FileConfiguration getConfig() {
        if (customConfig == null) {
            reloadConfig();
        }
        return customConfig;
    }

    public static void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            main.getLogger().log(Level.SEVERE, customConfigFile + "に保存出来ませんでした。", ex);
        }
    }

    public static void saveDefaultConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(main.getDataFolder(), FileName);
        }
        if (!customConfigFile.exists()) {
            main.saveResource(FileName, false);
        }
    }

    public static void set(String path, Object value) {
        getConfig().set(path, value);
        saveConfig();
    }

    public static void addStringList(String path, String value) {
        reloadConfig();
        List<String> str = getStringList(path);
        if (str == null) {
            str = new ArrayList<String>();
        }
        str.add(value);
        getConfig().set(path, value);
        saveConfig();
    }

    public static Object get(String path) {
        reloadConfig();
        return getConfig().get(path);
    }

    public static String getString(String path) {
        reloadConfig();
        return getConfig().getString(path);
    }

    public static int getInt(String path) {
        reloadConfig();
        return getConfig().getInt(path);
    }

    public static boolean getBoolean(String path) {
        reloadConfig();
        return getConfig().getBoolean(path);
    }

    public static List<String> getStringList(String path) {
        reloadConfig();
        return getConfig().getStringList(path);
    }

}
