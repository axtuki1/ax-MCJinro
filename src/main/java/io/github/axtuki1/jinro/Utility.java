package io.github.axtuki1.jinro;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.*;

public class Utility {

	public static Player getPlayer(String Name) {
		if( Name == null ){
			return null;
		}
		return Bukkit.getPlayerExact(Name);
	}

	public static Player getUUIDPlayer(String UUID) {
		if( UUID == null ){
			return null;
		}
		return Bukkit.getPlayer(UUID);
	}

	/**
	 * 大文字小文字を区別せずにreplaceAllします
	 *
	 * @param regex 置き換えたい文字列
	 * @param reql  置換後文字列
	 * @param text  置換対象文字列
	 */
	public static String myReplaceAll(String regex, String reql, String text) {
		String retStr = "";
		retStr = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).replaceAll(reql);
		return retStr;
	}

	/**
	 * MD2
	 * MD5
	 * SHA-1
	 * SHA-256
	 * SHA-384
	 * SHA-512
	 * @param algorithmName アルゴリズム名
	 * @param value ハッシュ化したい文字列
	 * @return ハッシュ化された文字列
	 */
	private String toEncryptedHashValue(String algorithmName, String value) {
		MessageDigest md = null;
		StringBuilder sb = null;
		try {
			md = MessageDigest.getInstance(algorithmName);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(value.getBytes());
		sb = new StringBuilder();
		for (byte b : md.digest()) {
			String hex = String.format("%02x", b);
			sb.append(hex);
		}
		return sb.toString();
	}

	public static String CommandText(String[] args, int start){
		StringBuilder out = new StringBuilder();
		for(int i = start; i < args.length ; i++){
			out.append(args[i]).append(" ");
		}
		return out.toString();
	}
}
