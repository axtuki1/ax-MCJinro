package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Challenge implements Listener {

    public static void openInventory(Player p, String targetp) {
        Config Challenge = new Config("challenge.yml");
        Challenge.reloadConfig();
        Inventory inv = Bukkit.getServer().createInventory(null, 9 * Challenge.getInt("Settings.InventoryRows"), ChatColor.DARK_RED + targetp + "の実績一覧");
        HashMap<String, HashMap<String, Object>> Challenges = getChallengeList();
        UUID target = null;
        try {
            target = UUIDFetcher.getUUIDOf(targetp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if( targetp == null ){
            Jinro.sendMessage(p, "指定されたプレイヤーは存在しません。", LogLevel.ERROR);
        }
        for (String key : Challenges.keySet()) {
            HashMap<String, Object> data = Challenges.get(key);
            HashMap<String, Object> open = (HashMap<String, Object>) Challenges.get(key).get("Open");
            ItemStack item;
            if (Stats.getChallenge(target, key)) {
                if(!p.getName().equalsIgnoreCase(targetp)){
                    short b;
                    if (data.get("DisplayItemDamage") == null) {
                        b = 0;
                    } else {
                        b = Short.parseShort(data.get("DisplayItemDamage").toString());
                    }
                    if(Boolean.valueOf(data.get("Hide").toString())){
                        item = new ItemStack(Material.BEDROCK, 1);
                    } else if (data.get("DisplayItemType") != null) {
                        item = new ItemStack((Material) data.get("DisplayItemType"), 1, b);
                    } else {
                        item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
                    }
                    ItemMeta meta = item.getItemMeta();
                    meta.addEnchant(Enchantment.LUCK, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    if( Boolean.valueOf(data.get("Hide").toString()) ){
                        meta.setDisplayName(ChatColor.GRAY + "????");
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        List<String> lore = new ArrayList<String>();
                        lore.add(ChatColor.GRAY + "条件は隠されています。");
                        lore.add(ChatColor.GREEN + "達成済");
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    } else {
                        meta.setDisplayName(data.get("Title").toString());
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        List<String> lore = new ArrayList<String>((ArrayList<String>) data.get("Description"));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                } else {
                    short b;
                    if (data.get("DisplayItemDamage") == null) {
                        b = 0;
                    } else {
                        b = Short.parseShort(data.get("DisplayItemDamage").toString());
                    }
                    if (data.get("DisplayItemType") != null) {
                        item = new ItemStack((Material) data.get("DisplayItemType"), 1, b);
                    } else {
                        item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
                    }
                    ItemMeta meta = item.getItemMeta();
                    meta.addEnchant(Enchantment.LUCK, 1, true);
                    meta.setDisplayName(data.get("Title").toString());
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    List<String> lore = new ArrayList<String>((ArrayList<String>) data.get("Description"));
                    lore.add(ChatColor.GREEN + "達成！");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            } else {
                short b;
                if (data.get("DisplayItemDamage") == null) {
                    b = 0;
                } else {
                    b = Short.parseShort(data.get("DisplayItemDamage").toString());
                }
                if(Boolean.valueOf(data.get("Hide").toString())){
                    item = new ItemStack(Material.BEDROCK, 1);
                } else if (data.get("DisplayItemType") != null) {
                    item = new ItemStack((Material) data.get("DisplayItemType"), 1, b);
                } else {
                    item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
                }
                ItemMeta meta = item.getItemMeta();
                if( Boolean.valueOf(data.get("Hide").toString()) ){
                    meta.setDisplayName(ChatColor.GRAY + "????");
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    List<String> lore = new ArrayList<String>();
                    lore.add(ChatColor.GRAY + "条件は隠されています。");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(data.get("Title").toString());
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    List<String> lore = new ArrayList<String>((ArrayList<String>) data.get("Description"));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }


            }
            inv.setItem((int) data.get("Slot"), item);
        }
        /*
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "← 戻る");
        item.setItemMeta(meta);
        inv.setItem((9 * getInt("Settings.InventoryRows")) - 1, item);
        */
        p.openInventory(inv);
    }

    public static void openInventory(Player p){
        openInventory(p, p.getName());
    }



    /*
     *  実績の開放チェックを行う。
     */
    public static void CheckOpen(Player p){
        Config Challenge = new Config("challenge.yml");
        // すべての実績を取得
        HashMap<String, HashMap<String, Object>> Challenges = getChallengeList();
        // 実績一つづつ検証。
        for(String key : Challenges.keySet()){
            // 処理前の状態を記録。
            boolean before = Stats.getChallenge(p, key);
            final boolean[] after = new boolean[1];
            HashMap<String, Object> data = Challenges.get(key);
            HashMap<String, Object> openall = (HashMap<String, Object>)Challenges.get(key).get("Open");
            after[0] = true;
            if(Challenge.getConfig().get("Challenges."+key+".Open") != null) {
                Challenge.getConfig().getConfigurationSection("Challenges." + key + ".Open").getKeys(false).forEach(k -> {
                    HashMap<String, Object> open = (HashMap<String, Object>) openall.get(k);
                    if (open.get("Key") != null && open.get("Name") != null && open.get("Count") != null && after[0]) {
                        //System.out.print(Stats.valueOf(open.get("Key").toString()) + " " + open.get("Name").toString() + " " + Stats.getStatsInt(p, Stats.valueOf(open.get("Key").toString()), open.get("Name").toString()) + " >= " + open.get("Count"));
                        if (Stats.getStatsInt(p, Stats.valueOf(open.get("Key").toString()), open.get("Name").toString()) >= (int) open.get("Count")) {
                        } else {
                            after[0] = false;
                        }
                    }
                });
            } else {
                after[0] = false;
            }
            if( after[0] != before && after[0]){
                Jinro.sendMessage(p,ChatColor.WHITE +"実績 "+ ChatColor.GREEN + "[" + data.get("Title") + ChatColor.GREEN +"] " + ChatColor.WHITE +"を達成しました！", LogLevel.SUCCESSFUL);
                Stats.setChallenge(p, key,true);
                for(Player pall : Bukkit.getOnlinePlayers()){
                    if(pall != p){
                        Jinro.sendMessage(pall,ChatColor.WHITE + p.getName() +" が実績 "+ ChatColor.GREEN + "[" + data.get("Title") + ChatColor.GREEN +"] " + ChatColor.WHITE +"を達成しました！", LogLevel.SUCCESSFUL);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();
        if(e.getInventory().getTitle().contains("実績") ) {
            e.setCancelled(true);
            p.updateInventory();
            return;
        }
    }

    public static HashMap<String, HashMap<String, Object>> getChallengeList(){
        Config Challenge = new Config("challenge.yml");
        Challenge.reloadConfig();
        HashMap<String, HashMap<String, Object>> out = new HashMap<>();
        Challenge.getConfig().getConfigurationSection("Challenges").getKeys(false).forEach((String key) -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put("ID", key);
            data.put("Title", Challenge.getConfig().getString("Challenges."+key+".Title"));
            data.put("Description", Challenge.getConfig().getStringList("Challenges."+key+".Description"));
            data.put("DisplayItemType", Material.getMaterial(Challenge.getConfig().getInt("Challenges."+key+".DisplayItemType")));
            data.put("DisplayItemDamage", Challenge.getConfig().getString("Challenges."+key+".DisplayItemDamage"));
            data.put("Slot", Challenge.getConfig().getInt("Challenges."+key+".Slot"));
            data.put("Hide", Challenge.getConfig().getBoolean("Challenges."+key+".Hide"));
            HashMap<String, Object> openall = new HashMap<>();
            if(Challenge.getConfig().get("Challenges."+key+".Open") != null){
                Challenge.getConfig().getConfigurationSection("Challenges."+key+".Open").getKeys(false).forEach(k -> {
                    HashMap<String, Object> open = new HashMap<>();
                    open.put("Name", Challenge.getConfig().getString("Challenges."+key+".Open."+k+".Name"));
                    open.put("Key", Challenge.getConfig().getString("Challenges."+key+".Open."+k+".Key"));
                    open.put("Count", Challenge.getConfig().getInt("Challenges."+key+".Open."+k+".Count"));
                    openall.put(k, open);
                });
            }
            data.put("Open", openall);
            out.put(key, data);
        });
        //System.out.println(out);
        return out;
    }

}
