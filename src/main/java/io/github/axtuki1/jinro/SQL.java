package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SQL {

    Connection connection;
    Statement statement;
    String url, username, password;

    SQL(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }


    void openConnection(Plugin pl) throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            this.Query("SELECT 1 as one");
            Jinro.getMain().getLogger().info("Connection...");
            return;
        }

        synchronized (pl) {
            if (connection != null && !connection.isClosed()) {
                Jinro.getMain().getLogger().info("Connection...");
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(this.url, this.username, this.password);
        }
    }

    void AsyncOpenConnection(Plugin pl){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Jinro.getMain().getLogger().info("MySQL Connection...");
                    openConnection(pl);
                    statement = connection.createStatement();
                    CheckTable();
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(pl);
        BukkitRunnable ra = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Query("SELECT 1 FROM `Jinro-UserStats-Death`");
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        ra.runTaskTimerAsynchronously(pl, 10 * 20, 10 * 20);
    }

    void CheckTable(){
        try {
            ResultSet result = Query("SHOW TABLES;");
            List<String> t = new ArrayList<String>();
            while (result.next()) {
                String name = result.getString("Tables_in_minecraftjinro");
                t.add(name);
            }
            if (!t.contains("Jinro-UserStats-Death")) {
                Update("CREATE TABLE `Jinro-UserStats-Death` (" +
                        "`id` INT(255) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "`UUID` varchar(40) UNIQUE NOT NULL," +
                        "`All` INT(255)," +
                        "`Execution` INT(255)," +
                        "`Kami` INT(255)," +
                        "`Explosion` INT(255)," +
                        "`Curse` INT(255)," +
                        "`GM` INT(255)" +
                        ");");
                Jinro.getMain().getLogger().info("Create Table \"Jinro-UserStats-Death\"");
            }
            if (!t.contains("Jinro-UserStats-Action")) {
                Update("CREATE TABLE `Jinro-UserStats-Action` (" +
                        "`id` INT(255) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "`UUID` varchar(40) UNIQUE NOT NULL," +
                        "`Kami` INT(255)," +
                        "`Kami_Fail` INT(255)," +
                        "`Kami_Success` INT(255)," +
                        "`Goei` INT(255)," +
                        "`Goei_Fail` INT(255)," +
                        "`Goei_Success` INT(255)," +
                        "`Uranai` INT(255)" +
                        ");");
                Jinro.getMain().getLogger().info("Create Table \"Jinro-UserStats-Action\"");
            }
            if (!t.contains("Jinro-UserStats-Win")) {
                Update("CREATE TABLE `Jinro-UserStats-Win` (" +
                        "`id` INT(255) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "`UUID` varchar(40) UNIQUE NOT NULL," +
                        "`All` INT(255)," +
                        "`murabito` INT(255)," +
                        "`jinro` INT(255)," +
                        "`reinou` INT(255)," +
                        "`uranai` INT(255)," +
                        "`kyoujin` INT(255)," +
                        "`kariudo` INT(255)," +
                        "`kyouyu` INT(255)," +
                        "`yoko` INT(255)," +
                        "`bakudan` INT(255)," +
                        "`cosplayer` INT(255)," +
                        "`ningyou` INT(255)," +
                        "`niwatori` INT(255)" +
                        ");");
                Jinro.getMain().getLogger().info("Create Table `Jinro-UserStats-Win`");
            }
            if(!t.contains("Jinro-UserStats-Play")){
                Update("CREATE TABLE `Jinro-UserStats-Play` (" +
                        "`id` INT(255) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "`UUID` varchar(40) UNIQUE NOT NULL," +
                        "`All` INT(255)," +
                        "`murabito` INT(255)," +
                        "`jinro` INT(255)," +
                        "`reinou` INT(255)," +
                        "`uranai` INT(255)," +
                        "`kyoujin` INT(255)," +
                        "`kariudo` INT(255)," +
                        "`kyouyu` INT(255)," +
                        "`yoko` INT(255)," +
                        "`bakudan` INT(255)," +
                        "`cosplayer` INT(255)," +
                        "`ningyou` INT(255)," +
                        "`niwatori` INT(255)" +
                        ");");
                Jinro.getMain().getLogger().info("Create Table `Jinro-UserStats-Play`");
            }
            if (!t.contains("Jinro-UserData")) {
                Update("CREATE TABLE `Jinro-UserData` (" +
                        "`id` INT(255) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "`UUID` varchar(40) UNIQUE NOT NULL," +
                        "`Name` LONGTEXT" +
                        ");");
                Jinro.getMain().getLogger().info("Create Table `Jinro-UserData`");
            }

            if (!t.contains("Jinro-UserStats-Challenge")) {
                String sql = "";
                for (String key : Challenge.getChallengeList().keySet()) {
                    sql = sql + ", `" + key + "` BOOLEAN";
                }
                Update("CREATE TABLE `Jinro-UserStats-Challenge` (" +
                        "`id` INT(255) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "`UUID` varchar(40) UNIQUE NOT NULL" + sql + ");");
                Jinro.getMain().getLogger().info("Create Table `Jinro-UserStats-Challenge`");
            }
            final StringBuilder[] sql = {null};
            final boolean[] isexec = {false};
            result = Query("show columns from `Jinro-UserStats-Challenge`;");
            t = new ArrayList<String>();
            while (result.next()) {
                String name = result.getString("Field");
                t.add(name);
            }
            Set<String> CList = Challenge.getChallengeList().keySet();
            for (String key : CList) {
                if (!t.contains(key)) {
                    if (sql[0] == null) {
                        sql[0] = new StringBuilder("`" + key + "` BOOLEAN");
                    } else {
                        sql[0].append(", `").append(key).append("` BOOLEAN");
                    }
                    isexec[0] = true;
                }
            }
            if (isexec[0]) {
                Update("ALTER TABLE `Jinro-UserStats-Challenge` ADD COLUMN " + sql[0]);
                Jinro.getMain().getLogger().info("Added Column");
            }
            isexec[0] = false;
            sql[0] = null;
            for (String key : t) {
                if(key.equalsIgnoreCase("id") || key.equalsIgnoreCase("UUID")){
                    continue;
                }
                if (!CList.contains(key)) {
                    if (sql[0] == null) {
                        sql[0] = new StringBuilder("`" + key + "`");
                    } else {
                        sql[0].append(", `").append(key).append("`");
                    }
                    isexec[0] = true;
                }
            }
            if (isexec[0]) {
                Update("ALTER TABLE `Jinro-UserStats-Challenge` DROP COLUMN " + sql[0]);
                Jinro.getMain().getLogger().info("Removed Column");
            }

            Config Challenge_C = new Config("challenge.yml");
            Challenge_C.reloadConfig();
            result = Query("SHOW TABLES;");
            t = new ArrayList<String>();
            while (result.next()) {
                String name = result.getString("Tables_in_minecraftjinro");
                t.add(name);
            }
            if (!t.contains("Jinro-UserStats-Chat")) {
                sql[0] = new StringBuilder();
                HashMap<String, HashMap<String, Object>> out = new HashMap<>();
                StringBuilder finalSql = sql[0];
                Challenge_C.getConfig().getConfigurationSection("ChatRecording").getKeys(false).forEach((String key) -> {
                    finalSql.append(", `").append(key).append("` INT(255)");
                    /*
                    HashMap<String, Object> target = new HashMap<>();
                    target.put("target", Challenge_C.getStringList("ChatRecording."+key+".target"));
                    out.put(key, target);
                    */
                });
                Update("CREATE TABLE `Jinro-UserStats-Chat` (" +
                        "`id` INT(255) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "`UUID` varchar(40) UNIQUE NOT NULL" + finalSql + ");");
                Jinro.getMain().getLogger().info("Create Table `Jinro-UserStats-Chat`");
            }
            sql[0] = null;
            isexec[0] = false;
            result = Query("show columns from `Jinro-UserStats-Chat`;");
            t = new ArrayList<String>();
            while (result.next()) {
                String name = result.getString("Field");
                t.add(name);
            }
            final StringBuilder[] SQL = {null};
            List<String> finalT = t;
            Challenge_C.getConfig().getConfigurationSection("ChatRecording").getKeys(false).forEach((String key) -> {
                if (!finalT.contains(key)) {
                    if (SQL[0] == null) {
                        SQL[0] = new StringBuilder("`" + key + "` INT(255)");
                    } else {
                        SQL[0].append(", `").append(key).append("` INT(255)");
                    }
                    isexec[0] = true;
                }
            });
            if (isexec[0]) {
                Update("ALTER TABLE `Jinro-UserStats-Chat` ADD COLUMN " + SQL[0]);
                Jinro.getMain().getLogger().info("Added Column");
            }
            isexec[0] = false;
            SQL[0] = null;
            for (String key : t) {
                if(key.equalsIgnoreCase("id") || key.equalsIgnoreCase("UUID")){
                    continue;
                }
                if (!finalT.contains(key)) {
                    if (SQL[0] == null) {
                        SQL[0] = new StringBuilder("`" + key + "`");
                    } else {
                        SQL[0].append(", `").append(key).append("`");
                    }
                    isexec[0] = true;
                }
            }
            if (isexec[0]) {
                Update("ALTER TABLE `Jinro-UserStats-Chat` DROP COLUMN " + SQL[0]);
                Jinro.getMain().getLogger().info("Removed Column");
            }
        } catch(SQLException e) {
            e.printStackTrace();
            Bukkit.broadcast(Jinro.getPrefix() + "CheckTable(): SQLException: ["+e.getErrorCode()+"] " + ErrorCode(e.getErrorCode()), "axtuki1.Jinro.GameMaster");
        }
    }

    Statement getStatement(){
        return this.statement;
    }

    void Disconnect(){
        try { //using a try catch to catch connection errors (like wrong sql password...)
            if (connection!=null && !connection.isClosed()){ //checking if connection isn't null to
                //avoid receiving a nullpointer
                connection.close(); //closing the connection field variable.
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    ResultSet Query(String SQL) throws SQLException {
        return getStatement().executeQuery(SQL);
    }

    void Update(String SQL) throws SQLException {
        getStatement().executeUpdate(SQL);
    }

    public String ErrorCode(int code){
        switch (code){
            case 1039:
                return "ファイル読み取り中に予期せずファイルの終端に達しました。";
            case 1041:
                return "メモリーが不足しています。設定ファイルを見直してみてください。";
            case 1044:
                return "ユーザーによるデータベースへのアクセスは拒否されました。";
            case 1045:
                return "ユーザーのアクセスは拒否されました。";
            case 1050:
                return "テーブルが既に存在しています。";
            case 1054:
                return "存在しないカラムです。";
            case 1064:
                return "構文エラーです。SQL文を見直してみてください。";
            default:
                return "不明なエラーコードです。";
        }
    }

    // insert into table_name (id, name ) values ('1000', 'MasK') on duplicate key update id='1000', name='hoge';

}
