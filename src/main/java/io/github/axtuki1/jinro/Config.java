package io.github.axtuki1.jinro;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {
    FileConfiguration customConfig = null;
    File customConfigFile = null;
    Jinro main = Jinro.getMain();
    String FileName;

    Config(String FileName){
        this.customConfig = null;
        this.customConfigFile = null;
        this.FileName = FileName;
    }

    void reloadConfig() {
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

    FileConfiguration getConfig() {
        if (customConfig == null) {
            reloadConfig();
        }
        return customConfig;
    }

    void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            main.getLogger().log(Level.SEVERE, customConfigFile + "に保存出来ませんでした。", ex);
        }
    }

    void saveDefaultConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(main.getDataFolder(), FileName);
        }
        if (!customConfigFile.exists()) {
            main.saveResource(FileName, false);
        }
    }

    void set(String path, Object value) {
        getConfig().set(path, value);
        saveConfig();
    }

    void addStringList(String path, String value) {
        reloadConfig();
        List<String> str = getStringList(path);
        if (str == null) {
            str = new ArrayList<String>();
        }
        str.add(value);
        getConfig().set(path, value);
        saveConfig();
    }

    Object get(String path) {
        reloadConfig();
        return getConfig().get(path);
    }

    String getString(String path) {
        reloadConfig();
        return getConfig().getString(path);
    }

    int getInt(String path) {
        reloadConfig();
        return getConfig().getInt(path);
    }

    boolean getBoolean(String path) {
        reloadConfig();
        return getConfig().getBoolean(path);
    }

    List<String> getStringList(String path) {
        reloadConfig();
        return getConfig().getStringList(path);
    }
}
