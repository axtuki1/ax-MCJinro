package io.github.axtuki1.jinro;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Setup extends JavaPlugin {

	public static boolean Admin(CommandSender sender, String commandLabel, String[] args) {
		if(args.length == 1){
			Jinro.sendCmdHelp(sender, "/jinro_ad setup spawn","スポーンポイントを設定します。", false);
			Jinro.sendCmdHelp(sender, "/jinro_ad setup reikai","霊界のスポーンポイントを設定します。", false);
			return true;
		}
		Player p = (Player)sender;
		switch(args[1]){
		case "spawn":
			Jinro.set("spawnpoint.x", p.getLocation().getX());
			Jinro.set("spawnpoint.y", p.getLocation().getY());
			Jinro.set("spawnpoint.z", p.getLocation().getZ());
			Jinro.set("spawnpoint.yaw", p.getLocation().getYaw());
			Jinro.set("spawnpoint.pitch", p.getLocation().getPitch());
			try {
				Jinro.getMain().getConfig().save(Jinro.getMain().getDataFolder() + File.separator + "config.yml");
			} catch (IOException e) {
				e.printStackTrace();
			}
			Jinro.setRespawnLoc(p.getLocation());
			Jinro.sendMessage(sender, "この座標にスポーンポイントを設定しました。", LogLevel.SUCCESSFUL);
			return true;
		case "reikai":
			Jinro.set("reikai.x", p.getLocation().getX());
			Jinro.set("reikai.y", p.getLocation().getY());
			Jinro.set("reikai.z", p.getLocation().getZ());
			Jinro.set("reikai.yaw", p.getLocation().getYaw());
			Jinro.set("reikai.pitch", p.getLocation().getPitch());
			try {
				Jinro.getMain().getConfig().save(Jinro.getMain().getDataFolder() + File.separator + "config.yml");
			} catch (IOException e) {
				e.printStackTrace();
			}
			Jinro.setRespawnLoc(p.getLocation());
			Jinro.sendMessage(sender, "この座標に霊界のスポーンポイントを設定しました。", LogLevel.SUCCESSFUL);
			return true;
		}
		return true;
	}
}
