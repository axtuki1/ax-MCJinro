package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class JinroMap {

    public static void AdminCmdHelp(CommandSender sender){
        Jinro.sendMessage(sender, Jinro.getHeader(), LogLevel.Notice);
        Jinro.sendCmdHelp(sender, "/jinro_ad map list", "利用可能なマップを表示します。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map info <MapName>", "マップの情報を表示します。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map set <MapName>", "マップを切り替えます。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map tp <MapName> <spawn | reikai>", "各スポーンポイントにテレポートします。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map setup <...>", "利用可能なマップを表示します。");
    }

    public static void Admin(CommandSender sender, String commandLabel, String[] args) {
        if (Status.getStatus().equals(Status.GamePlaying)) {
            Jinro.sendMessage(sender, "ゲーム中はこの機能を使用できません。", LogLevel.ERROR, true);
            return;
        }
        if( args.length == 1 ){
            // jinro_ad map
            AdminCmdHelp(sender);
            return;
        }
        if( args[1].equalsIgnoreCase("list") ){
            if( args.length == 3 && args[2].equalsIgnoreCase("full") ){
                Jinro.sendMessage(sender, "利用可能なマップは以下の通りです。", LogLevel.INFO, true);
                for( JinroMap m : JinroMap.getMaps() ){
                    if((m.getReikaiSpawnPoint() != null) && (m.getSpawnPoint() != null)) {
                        Jinro.sendMessage(sender, ChatColor.GREEN + m.getDisplayName() + ChatColor.GRAY + "(" + m.getName() + ")", LogLevel.Notice, true);
                        Location s = m.getSpawnPoint();
                        Jinro.sendMessage(sender, ChatColor.AQUA + "スポーンポイント:", LogLevel.Notice, true);
                        if (s != null) {
                            Jinro.sendMessage(sender, ChatColor.BLUE + " X    : " + ChatColor.YELLOW + s.getX(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Y    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Z    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Yaw  : " + ChatColor.YELLOW + s.getYaw(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Pitch: " + ChatColor.YELLOW + s.getPitch(), LogLevel.Notice, true);
                        } else {
                            Jinro.sendMessage(sender, ChatColor.RED + " まだ設定されていません。", LogLevel.Notice, true);
                        }
                        s = m.getReikaiSpawnPoint();
                        Jinro.sendMessage(sender, ChatColor.AQUA + "霊界のスポーンポイント:", LogLevel.Notice, true);
                        if (s != null) {
                            Jinro.sendMessage(sender, ChatColor.BLUE + " X    : " + ChatColor.YELLOW + s.getX(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Y    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Z    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Yaw  : " + ChatColor.YELLOW + s.getYaw(), LogLevel.Notice, true);
                            Jinro.sendMessage(sender, ChatColor.BLUE + " Pitch: " + ChatColor.YELLOW + s.getPitch(), LogLevel.Notice, true);
                        } else {
                            Jinro.sendMessage(sender, ChatColor.RED + " まだ設定されていません。", LogLevel.Notice, true);
                        }
                        Jinro.sendMessage(sender, ChatColor.RED + "=====================================", LogLevel.Notice, true);
                    }
                }
            } else if( args.length == 3 && args[2].equalsIgnoreCase("all") ){
                StringBuilder out = new StringBuilder();
                for( JinroMap m : JinroMap.getMaps() ){
                    if (m.getDisplayName() != null ){
                        out.append(m.getName()).append(", ");
                    }
                }
                Jinro.sendMessage(sender, "存在しているマップは以下の通りです。", LogLevel.INFO, true);
                Jinro.sendMessage(sender, out.toString().substring(0, out.length() - 2), LogLevel.INFO, true);
            } else {
                StringBuilder out = new StringBuilder();
                for( JinroMap m : JinroMap.getMaps() ){
                    if((m.getReikaiSpawnPoint() != null) && (m.getSpawnPoint() != null)){
                        out.append(m.getName()).append(", ");
                    }
                }
                Jinro.sendMessage(sender, "利用可能なマップは以下の通りです。", LogLevel.INFO, true);
                Jinro.sendMessage(sender, out.toString().substring(0, out.length() - 2), LogLevel.INFO, true);
            }
        } else if( args[1].equalsIgnoreCase("set") ){
            if( args.length == 2 ){
                Jinro.sendMessage(sender, "マップを指定してください。", LogLevel.ERROR, true);
                return;
            } else if( args.length == 3 ) {
                JinroMap map = new JinroMap( args[2] );
                if( map.getDisplayName() == null ) {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                    return;
                }
                if( (map.getReikaiSpawnPoint() == null) && (map.getSpawnPoint() == null) ){
                    Jinro.sendMessage(sender, "このマップはスポーンポイントが不十分です。", LogLevel.ERROR, true);
                    return;
                }
                Jinro.setReikaiLoc(map.getReikaiSpawnPoint());
                Jinro.setRespawnLoc(map.getSpawnPoint());
//                Jinro.sendMessage(sender, "各スポーンポイントをマップ「" + args[2] + "」の値に変更しました。", LogLevel.SUCCESSFUL, true);
                Bukkit.broadcastMessage( Jinro.getPrefix() + ChatColor.GREEN + "マップが「" + map.getDisplayName() + "」に変更されました。" );
                for( Player p : Bukkit.getOnlinePlayers() ){
                    Jinro.TeleportToRespawn(p);
                }
            } else {
                AdminCmdHelp(sender);
            }
        } else if( args[1].equalsIgnoreCase("info") ){
            if( args.length == 2 ){
                Jinro.sendMessage(sender, "マップを指定してください。", LogLevel.ERROR, true);
                return;
            } else if( args.length == 3 ) {
                JinroMap m = new JinroMap(args[2]);
                if(m.getDisplayName() != null){
                    Jinro.sendMessage(sender, ChatColor.RED + "=====================================", LogLevel.Notice, true);
                    Jinro.sendMessage(sender, ChatColor.GREEN + m.getDisplayName() + ChatColor.GRAY + "(" + m.getName() + ")", LogLevel.Notice, true);
                    Location s = m.getSpawnPoint();
                    Jinro.sendMessage(sender, ChatColor.AQUA + "スポーンポイント:", LogLevel.Notice, true);
                    if (s != null) {
                        Jinro.sendMessage(sender, ChatColor.BLUE + " X    : " + ChatColor.YELLOW + s.getX(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Y    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Z    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Yaw  : " + ChatColor.YELLOW + s.getYaw(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Pitch: " + ChatColor.YELLOW + s.getPitch(), LogLevel.Notice, true);
                    } else {
                        Jinro.sendMessage(sender, ChatColor.RED + " まだ設定されていません。", LogLevel.Notice, true);
                    }
                    s = m.getReikaiSpawnPoint();
                    Jinro.sendMessage(sender, ChatColor.AQUA + "霊界のスポーンポイント:", LogLevel.Notice, true);
                    if (s != null) {
                        Jinro.sendMessage(sender, ChatColor.BLUE + " X    : " + ChatColor.YELLOW + s.getX(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Y    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Z    : " + ChatColor.YELLOW + s.getY(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Yaw  : " + ChatColor.YELLOW + s.getYaw(), LogLevel.Notice, true);
                        Jinro.sendMessage(sender, ChatColor.BLUE + " Pitch: " + ChatColor.YELLOW + s.getPitch(), LogLevel.Notice, true);
                    } else {
                        Jinro.sendMessage(sender, ChatColor.RED + " まだ設定されていません。", LogLevel.Notice, true);
                    }
                    Jinro.sendMessage(sender, ChatColor.RED + "=====================================", LogLevel.Notice, true);
                } else {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                }
            } else {
                AdminCmdHelp(sender);
            }
        } else if( args[1].equalsIgnoreCase("tp") ){
            if( args.length == 2 ){
                // jinro_ad map tp
                Jinro.sendMessage(sender, "マップを指定してください。", LogLevel.ERROR, true);
                return;
            } else if( args.length == 3 || (args.length == 4 && args[3].equalsIgnoreCase("spawn")) ){
                // jinro_ad map tp name spawn
                JinroMap map = new JinroMap( args[2] );
                if( map.getDisplayName() == null ) {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                    return;
                }
                if( map.getSpawnPoint() == null ){
                    Jinro.sendMessage(sender, "このマップにはスポーンポイントがありません。", LogLevel.ERROR, true);
                    return;
                }
                ((Player) sender).teleport(map.getSpawnPoint());
                Jinro.sendMessage(sender, "マップ「"+args[2]+"」のスポーンポイントにテレポートしました。", LogLevel.SUCCESSFUL, true);
            } else if( args.length == 4 && args[3].equalsIgnoreCase("reikai") ){
                // jinro_ad map tp name reikai
                JinroMap map = new JinroMap( args[2] );
                if( map.getDisplayName() == null ) {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                    return;
                }
                if( map.getReikaiSpawnPoint() == null ){
                    Jinro.sendMessage(sender, "このマップには霊界のスポーンポイントがありません。", LogLevel.ERROR, true);
                    return;
                }
                ((Player) sender).teleport(map.getReikaiSpawnPoint());
                Jinro.sendMessage(sender, "マップ「"+args[2]+"」の霊界のスポーンポイントにテレポートしました。", LogLevel.SUCCESSFUL, true);
            } else {
                AdminCmdHelp(sender);
            }
        } else if( args[1].equalsIgnoreCase("setup") ){
            MapSetup(sender, commandLabel, args);
        } else {
            AdminCmdHelp(sender);
        }
    }

    public static void MapSetupCmdHelp(CommandSender sender) {
        Jinro.sendMessage(sender, Jinro.getHeader(), LogLevel.Notice, false);
        Jinro.sendCmdHelp(sender, "/jinro_ad map setup add <Name>", "マップ<Name>を追加します。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map setup remove <Name>", "マップ<Name>を削除します。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map setup <Name> DisplayName <value>", "マップ<Name>の表示名を設定します。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map setup <Name> Spawn", "マップ<Name>のスポーンポイントを設定します。");
        Jinro.sendCmdHelp(sender, "/jinro_ad map setup <Name> Reikai", "マップ<Name>の霊界のスポーンポイントを設定します。");
    }

    private static void MapSetup(CommandSender sender, String commandLabel, String[] args) {
        if (args.length >= 3 && args[2].equalsIgnoreCase("add")) {
           if (args.length == 4) {
                if( args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("remove") ){
                    Jinro.sendMessage(sender, "この名前はコマンドで使用するため利用できません。", LogLevel.ERROR, true);
                    return;
                }
                JinroMap map = new JinroMap(args[3]);
                if (map.getDisplayName() != null) {
                    Jinro.sendMessage(sender, "このマップは既に存在しています。", LogLevel.ERROR, true);
                    return;
                }
                map.setDisplayName(args[3]);
                Jinro.sendMessage(sender, "マップ「" + args[3] + "」を追加しました。", LogLevel.SUCCESSFUL, true);
                return;
            } else if (args.length == 5) {
                if( args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("remove") ){
                    Jinro.sendMessage(sender, "この名前はコマンドで使用するため利用できません。", LogLevel.ERROR, true);
                    return;
                }
                JinroMap map = new JinroMap(args[3]);
                if (map.getDisplayName() != null) {
                    Jinro.sendMessage(sender, "このマップは既に存在しています。", LogLevel.ERROR, true);
                    return;
                }
                map.setDisplayName(args[4]);
                Jinro.sendMessage(sender, "マップ「" + args[4] + "」" + ChatColor.GRAY + "(" + args[3] + ")" + ChatColor.GREEN + "を追加しました。", LogLevel.SUCCESSFUL, true);
                return;
            } else {
               Jinro.sendMessage(sender, Jinro.getHeader(), LogLevel.Notice, false);
               Jinro.sendCmdHelp(sender, "/jinro_ad map setup add <Name>", "マップ<Name>を追加します。");
               Jinro.sendCmdHelp(sender, "/jinro_ad map setup add <Name> <DisplayName>", "マップ<Name>を表示名<DisplayName>で追加します。");
           }
        } else if (args.length >= 3 && args[2].equalsIgnoreCase("remove")) {
            if (args.length == 4) {
                JinroMap map = new JinroMap(args[3]);
                if (map.getDisplayName() == null) {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                    return;
                }
                map.Delete();
                Jinro.sendMessage(sender, "マップ「" + args[3] + "」を削除しました。", LogLevel.SUCCESSFUL, true);
                return;
            } else {
                Jinro.sendMessage(sender, Jinro.getHeader(), LogLevel.Notice, false);
                Jinro.sendCmdHelp(sender, "/jinro_ad map setup remove <Name>", "マップ<Name>を削除します。");
            }
        } else if (args.length >= 4 && args[3].equalsIgnoreCase("spawn")) {
            if (args.length == 4) {
                JinroMap map = new JinroMap(args[2]);
                if (map.getDisplayName() == null) {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                    return;
                }
                map.setSpawnPoint(((Player) sender).getLocation());
                Jinro.sendMessage(sender, "立っている座標にマップ「" + args[2] + "」のスポーンポイントを設定しました。", LogLevel.SUCCESSFUL, true);
                return;
            }
        } else if (args.length >= 4 && args[3].equalsIgnoreCase("Reikai")) {
            if (args.length == 4) {
                JinroMap map = new JinroMap(args[2]);
                if (map.getDisplayName() == null) {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                    return;
                }
                map.setReikaiSpawnPoint(((Player) sender).getLocation());
                Jinro.sendMessage(sender, "立っている座標にマップ「" + args[2] + "」の霊界のスポーンポイントを設定しました。", LogLevel.SUCCESSFUL, true);
                return;
            }
        } else if (args.length >= 4 && args[3].equalsIgnoreCase("DisplayName")) {
            if (args.length == 5) {
                JinroMap map = new JinroMap(args[2]);
                if (map.getDisplayName() == null) {
                    Jinro.sendMessage(sender, "このマップは存在しません。", LogLevel.ERROR, true);
                    return;
                }
                map.setDisplayName(args[4]);
                Jinro.sendMessage(sender, "マップ「" + args[2] + "」の表示名を「" + args[4] + "」に設定しました。", LogLevel.SUCCESSFUL, true);
                return;
            }
        }
        MapSetupCmdHelp(sender);
    }

    public static ArrayList<JinroMap> getMaps(){
        ArrayList<JinroMap> out = new ArrayList<JinroMap>();
        try {
            for( String name : Jinro.getMap().getConfig().getConfigurationSection("Map").getKeys(false) ){
                out.add(new JinroMap(name));
            }
        } catch ( NullPointerException e ){
            return null;
        }
        return out;
    }

    private String MapName = "", DisplayName = "";
    private Location SpawnPoint, ReikaiSpawnPoint;

    JinroMap(String MapName) {
        this.MapName = MapName;
        this.DisplayName = Jinro.getMap().getString("Map." + MapName + ".DisplayName");
        World w;
        try {
            w = Bukkit.getWorld(Jinro.getMap().getString("Map." + MapName + ".spawn.world"));
            if (w == null) {
                w = Bukkit.getWorlds().get(0);
            }
        } catch (IllegalArgumentException e) {
            w = Bukkit.getWorlds().get(0);
        }

        try {
            this.SpawnPoint = new Location(w,
                    Double.parseDouble(Jinro.getMap().getString("Map." + MapName + ".spawn.x")),
                    Double.parseDouble(Jinro.getMap().getString("Map." + MapName + ".spawn.y")),
                    Double.parseDouble(Jinro.getMap().getString("Map." + MapName + ".spawn.z")),
                    Float.parseFloat(Jinro.getMap().getString("Map." + MapName + ".spawn.yaw")),
                    Float.parseFloat(Jinro.getMap().getString("Map." + MapName + ".spawn.pitch"))
            );
        } catch (NullPointerException e) {
            this.SpawnPoint = null;
        }

        w = null;
        try {
            w = Bukkit.getWorld(Jinro.getMap().getString("Map." + MapName + ".reikai.world"));
            if (w == null) {
                w = Bukkit.getWorlds().get(0);
            }
        } catch (IllegalArgumentException e) {
            w = Bukkit.getWorlds().get(0);
        }
        try {
            this.ReikaiSpawnPoint = new Location(w,
                    Double.parseDouble(Jinro.getMap().getString("Map." + MapName + ".reikai.x")),
                    Double.parseDouble(Jinro.getMap().getString("Map." + MapName + ".reikai.y")),
                    Double.parseDouble(Jinro.getMap().getString("Map." + MapName + ".reikai.z")),
                    Float.parseFloat(Jinro.getMap().getString("Map." + MapName + ".reikai.yaw")),
                    Float.parseFloat(Jinro.getMap().getString("Map." + MapName + ".reikai.pitch"))
            );
        } catch (NullPointerException e) {
            this.ReikaiSpawnPoint = null;
        }
    }

    String getName() {
        return this.MapName;
    }

    String getDisplayName() {
        return this.DisplayName;
    }

    public void setDisplayName(String displayName) {
        this.DisplayName = displayName;
        Jinro.getMap().set("Map."+MapName+".DisplayName", displayName);
    }

    Location getSpawnPoint() {
        return this.SpawnPoint;
    }

    void setSpawnPoint(Location loc) {
        String MapName = this.MapName;
        Jinro.getMap().set("Map."+MapName+".spawn.world", loc.getWorld().getName());
        Jinro.getMap().set("Map."+MapName+".spawn.x", loc.getX());
        Jinro.getMap().set("Map."+MapName+".spawn.y", loc.getY());
        Jinro.getMap().set("Map."+MapName+".spawn.z", loc.getZ());
        Jinro.getMap().set("Map."+MapName+".spawn.yaw", loc.getYaw());
        Jinro.getMap().set("Map."+MapName+".spawn.pitch", loc.getPitch());
        this.SpawnPoint = loc;
    }

    Location getReikaiSpawnPoint() {
        return this.ReikaiSpawnPoint;
    }

    void setReikaiSpawnPoint(Location loc) {
        String MapName = this.MapName;
        Jinro.getMap().set("Map."+MapName+".reikai.world", loc.getWorld().getName());
        Jinro.getMap().set("Map."+MapName+".reikai.x", loc.getX());
        Jinro.getMap().set("Map."+MapName+".reikai.y", loc.getY());
        Jinro.getMap().set("Map."+MapName+".reikai.z", loc.getZ());
        Jinro.getMap().set("Map."+MapName+".reikai.yaw", loc.getYaw());
        Jinro.getMap().set("Map."+MapName+".reikai.pitch", loc.getPitch());
        this.ReikaiSpawnPoint = loc;
    }

    void Delete() {
        Jinro.getMap().set("Map."+this.MapName, null);
        this.DisplayName = null;
        this.MapName = null;
        this.ReikaiSpawnPoint = null;
        this.SpawnPoint = null;
    }


    void SyncData(){
        World w;
        try {
            w = Bukkit.getWorld(Jinro.getMain().getConfig().getString("Map."+this.MapName+".spawn.world"));
            if (w == null) {
                w = Bukkit.getWorlds().get(0);
            }
        } catch (IllegalArgumentException e) {
            w = Bukkit.getWorlds().get(0);
        }
        this.DisplayName = Jinro.getMap().getString("Map."+MapName+".DisplayName");
        this.SpawnPoint = new Location(w,
                Double.parseDouble(Jinro.getMap().getString("Map."+this.MapName+".spawn.x")),
                Double.parseDouble(Jinro.getMap().getString("Map."+this.MapName+".spawn.y")),
                Double.parseDouble(Jinro.getMap().getString("Map."+this.MapName+".spawn.z")),
                Float.parseFloat(Jinro.getMap().getString("Map."+this.MapName+".spawn.yaw")),
                Float.parseFloat(Jinro.getMap().getString("Map."+this.MapName+".spawn.pitch"))
        );
        w = null;
        try {
            w = Bukkit.getWorld(Jinro.getMain().getConfig().getString("Map."+this.MapName+".reikai.world"));
            if (w == null) {
                w = Bukkit.getWorlds().get(0);
            }
        } catch (IllegalArgumentException e) {
            w = Bukkit.getWorlds().get(0);
        }
        this.ReikaiSpawnPoint = new Location(w,
                Double.parseDouble(Jinro.getMap().getString("Map."+this.MapName+".reikai.x")),
                Double.parseDouble(Jinro.getMap().getString("Map."+this.MapName+".reikai.y")),
                Double.parseDouble(Jinro.getMap().getString("Map."+this.MapName+".reikai.z")),
                Float.parseFloat(Jinro.getMap().getString("Map."+this.MapName+".reikai.yaw")),
                Float.parseFloat(Jinro.getMap().getString("Map."+this.MapName+".reikai.pitch"))
        );
    }
}
