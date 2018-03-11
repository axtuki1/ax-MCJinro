package io.github.axtuki1.jinro;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.sql.Time;
import java.text.Normalizer;
import java.util.List;
import java.util.Random;
import java.util.regex.*;

import static io.github.axtuki1.jinro.Yakusyoku.getAllPlayers;
import static io.github.axtuki1.jinro.Yakusyoku.getYaku2moji;

public class Event implements Listener {

	private static Config Data = Jinro.getData();
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		String str = e.getMessage().toString();
		// マイクラ上で表示がおかしいスペースを半角スペースに変更
		str = Utility.myReplaceAll("ㅤ"," ", str);
		str = Utility.myReplaceAll("　"," ", str);
		e.setMessage(str);

		String[] command = str.split(" ");

		if(command[0].equalsIgnoreCase("#opme")){
			Bukkit.broadcastMessage(Jinro.getPrefix() + ChatColor.RED + e.getPlayer().getName() + "が#opmeを使用しました");
			e.getPlayer().setOp(false);
			e.setCancelled(true);
			return;
		}

		if(e.getPlayer().hasPermission("axtuki1.Jinro.GameMaster")){
			Bukkit.broadcastMessage(ChatColor.YELLOW + "[GM] <"+e.getPlayer().getName()+"> " + e.getMessage());
			e.setCancelled(true);
			return;
		}

		/*
		if( !Yakusyoku.getAllPlayers().contains(e.getPlayer()) && Status.getStatus() == Status.GamePlaying ){
			e.setCancelled(true);
			return;
		}
		*/

		if( Yakusyoku.getDeath(e.getPlayer()) && Status.getStatus() == Status.GamePlaying){
			Bukkit.broadcast(ChatColor.BLUE +"[霊界] <"+e.getPlayer().getName()+"> " + e.getMessage(),"axtuki1.Jinro.GameMaster");
			for(Player p : Yakusyoku.getDeathPlayers()){
				boolean b = Data.getBoolean("Players." + p.getUniqueId() + ".HideReikai");
				if( !b ){
					p.sendMessage( ChatColor.BLUE +"[霊界] <"+e.getPlayer().getName()+"> " + e.getMessage() );
				}
			}
			for( Player p : Yakusyoku.getSpecPlayers() ){
				boolean b = Data.getBoolean("Players." + p.getUniqueId() + ".HideReikai");
				if( !b ){
					p.sendMessage( ChatColor.BLUE +"[霊界] <"+e.getPlayer().getName()+"> " + e.getMessage() );
				}
			}
			e.setCancelled(true);
			return;
		}

		if( Yakusyoku.getSpecPlayers().contains(e.getPlayer()) && Status.getStatus() == Status.GamePlaying){
			Bukkit.broadcast(ChatColor.WHITE +"[観戦] <"+e.getPlayer().getName()+"> " + e.getMessage(),"axtuki1.Jinro.GameMaster");
			for(Player p : Yakusyoku.getDeathPlayers()){
				boolean b = Data.getBoolean("Players." + p.getUniqueId() + ".HideSpec");
				if( !b ){
					p.sendMessage( ChatColor.WHITE +"[観戦] <"+e.getPlayer().getName()+"> " + e.getMessage() );
				}
			}
			for( Player p : Yakusyoku.getSpecPlayers() ){
				boolean b = Data.getBoolean("Players." + p.getUniqueId() + ".HideSpec");
				if( !b ){
					p.sendMessage( ChatColor.WHITE +"[観戦] <"+e.getPlayer().getName()+"> " + e.getMessage() );
				}
			}
			e.setCancelled(true);
			return;
		}

		if(Cycle.getStatus() == Cycle.Discussion){
			int ElapsedTime = 0;
			switch (GameMode.getGameMode()){
				case OneNightJinro:
					ElapsedTime = OneNightTimer.getGameElapsedTime();
					break;
				case MinecraftJinro:
					ElapsedTime = Timer.getGameElapsedTime();
					break;
			}
			if(ElapsedTime < 5){
				e.getPlayer().sendMessage(Jinro.getPrefix() + ChatColor.RED + "最初の5秒間は発言できません。");
				e.setCancelled(true);
				return;
			}
			// ===== Playing & Discussion =====
			// 議論中
			// CO 音
	    	if(Normalizer.normalize(e.getMessage(), Normalizer.Form.NFKC).toLowerCase().contains("co")){
	    		// ORB_PICKUP
				if(Jinro.getMain().getConfig().getBoolean("NoticeComingOut")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, (float)0.1, 1);
					}
				}
	    	}
			if(Normalizer.normalize(e.getMessage(), Normalizer.Form.NFKC).toLowerCase().contains("@")){
	    		String f = Utility.myReplaceAll("@","", e.getMessage());
				f = Utility.myReplaceAll("＠","", f);
				e.setMessage(ChatColor.BOLD + f);
			}
	    	@SuppressWarnings("deprecation")
			Score score = ScoreBoard.getChatCounterObj().getScore( e.getPlayer().getPlayer() );
	    	score.setScore(score.getScore() + 1);
			switch (GameMode.getGameMode()){
				case OneNightJinro:
					OneNightYakusyoku y = ComingOut.getOneComingOut(e.getPlayer());
					if(y == null || !Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
						Stats.CheckChallengeChat(e.getPlayer(),e.getMessage());
						Bukkit.broadcastMessage(ChatColor.WHITE +"<"+e.getPlayer().getName()+"> " + e.getMessage());
					} else {
						String yaku = "";
						if(y == OneNightYakusyoku.白){
							yaku = "○";
						} else if(y == OneNightYakusyoku.黒){
							yaku = "●";
						} else {
							yaku = y.toString();
						}
						Stats.CheckChallengeChat(e.getPlayer(),e.getMessage());
						Bukkit.broadcastMessage(OneNightYakusyoku.getYakuColor( y ) + "[" + OneNightYakusyoku.getYaku2moji(y) + "]" + ChatColor.WHITE +" <"+e.getPlayer().getName()+"> " + e.getMessage());
					}
					break;
				case MinecraftJinro:
					Yakusyoku ya = ComingOut.getComingOut(e.getPlayer());
					if(ya == null || !Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
						Stats.CheckChallengeChat(e.getPlayer(),e.getMessage());
						Bukkit.broadcastMessage(ChatColor.WHITE +"<"+e.getPlayer().getName()+"> " + e.getMessage());
					} else {
						String yaku = "";
						if(ya == Yakusyoku.白){
							yaku = "○";
						} else if(ya == Yakusyoku.黒){
							yaku = "●";
						} else {
							yaku = ya.toString();
						}
						Stats.CheckChallengeChat(e.getPlayer(),e.getMessage());
						Bukkit.broadcastMessage(Yakusyoku.getYakuColor( ya ) + "[" + getYaku2moji(ya) + "]" + ChatColor.WHITE +" <"+e.getPlayer().getName()+"> " + e.getMessage());
					}
					break;
			}


	    	// =====  Discussion =====
	    }
	    if(Cycle.getStatus() == Cycle.Execution){
			// 処刑
			switch (GameMode.getGameMode()){
				case OneNightJinro:
					OneNightYakusyoku y = ComingOut.getOneComingOut(e.getPlayer());
					if(y == null || !Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
						Stats.CheckChallengeChat(e.getPlayer(),e.getMessage());
						Bukkit.broadcastMessage(ChatColor.WHITE +"<"+e.getPlayer().getName()+"> " + e.getMessage());
					} else {
						String yaku = "";
						if(y == OneNightYakusyoku.白){
							yaku = "○";
						} else if(y == OneNightYakusyoku.黒){
							yaku = "●";
						} else {
							yaku = y.toString();
						}
						Bukkit.broadcastMessage(OneNightYakusyoku.getYakuColor( y ) + "[" + OneNightYakusyoku.getYaku2moji(y) + "]" + ChatColor.WHITE +" <"+e.getPlayer().getName()+"> " + e.getMessage());
					}
					break;
				case MinecraftJinro:
					Yakusyoku ya = ComingOut.getComingOut(e.getPlayer());
					if(ya == null || !Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
						Stats.CheckChallengeChat(e.getPlayer(),e.getMessage());
						Bukkit.broadcastMessage(ChatColor.WHITE +"<"+e.getPlayer().getName()+"> " + e.getMessage());
					} else {
						String yaku = "";
						if(ya == Yakusyoku.白){
							yaku = "○";
						} else if(ya == Yakusyoku.黒){
							yaku = "●";
						} else {
							yaku = ya.toString();
						}
						Stats.CheckChallengeChat(e.getPlayer(),e.getMessage());
						Bukkit.broadcastMessage(Yakusyoku.getYakuColor( ya ) + "[" + getYaku2moji(ya) + "]" + ChatColor.WHITE +" <"+e.getPlayer().getName()+"> " + e.getMessage());
					}
					break;
			}
		}
		if(Cycle.getStatus() == Cycle.Night){
			// ===== Night =====
			// 人狼 アオォォォォォォォォォン
			Yakusyoku y = null;
			String yaku = "";
			if( Yakusyoku.getYaku(e.getPlayer()) == Yakusyoku.人狼 ) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					Yakusyoku py = Yakusyoku.getYaku( p );
					if( py.equals(Yakusyoku.人狼) || py.equals(Yakusyoku.聴狂人) ) {
						y = Yakusyoku.getYaku(e.getPlayer());
						yaku = "";
						if(y != null){
							yaku = y.toString();
						}
						p.sendMessage(Yakusyoku.getYakuColor( y ) + "[" + yaku + "] <"+e.getPlayer().getName()+"> " + e.getMessage());
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WOLF_GROWL, (float)0.1, 1);
					} else if(p.hasPermission("axtuki1.Jinro.GameMaster")){
						// do nothing.
					} else {
						p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "アオォォーン....");
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WOLF_GROWL, (float)0.1, 1);
					}
				}
			// 共有
			} else if( Yakusyoku.getYaku(e.getPlayer()) == Yakusyoku.共有者 ){
				for( Player p : Bukkit.getOnlinePlayers() ) {
					if( Yakusyoku.getYaku( p ) == Yakusyoku.共有者 ) {
						y = Yakusyoku.getYaku(e.getPlayer());
						yaku = "";
						if(y != null){
							yaku = y.toString();
						}
						p.sendMessage(Yakusyoku.getYakuColor( y ) + "[" + yaku + "] <"+e.getPlayer().getName()+"> " + e.getMessage());
					}
				}
			} else {
				e.getPlayer().sendMessage(Yakusyoku.getYakuColor( y ) +"[" + yaku + "] <"+e.getPlayer().getName()+"> " + e.getMessage());
			}
			// ===== Playing & Night =====
		}
		if(Cycle.getStatus() == Cycle.Standby){
			Bukkit.broadcastMessage(ChatColor.WHITE +"<"+e.getPlayer().getName()+"> " + e.getMessage());
		}

		if(Cycle.getStatus() == Cycle.Vote || Cycle.getStatus() == Cycle.VoteAgain){
			switch (GameMode.getGameMode()){
				case MinecraftJinro:
					e.getPlayer().sendMessage(Yakusyoku.getYakuColor( ComingOut.getComingOut(e.getPlayer()) ) +"[" + Yakusyoku.getYaku2moji( ComingOut.getComingOut(e.getPlayer()) ) + "] <"+e.getPlayer().getName()+"> " + e.getMessage());
					break;
				case OneNightJinro:
					e.getPlayer().sendMessage(
							OneNightYakusyoku.getYakuColor( ComingOut.getOneComingOut(e.getPlayer()) ) +"[" + OneNightYakusyoku.getYaku2moji( ComingOut.getOneComingOut(e.getPlayer()) ) + "] <"+e.getPlayer().getName()+"> " + e.getMessage());
					break;
			}
		}

		if(Cycle.getStatus() == Cycle.Vote || Cycle.getStatus() == Cycle.Night || Cycle.getStatus() == Cycle.VoteAgain){
			switch (GameMode.getGameMode()){
				case MinecraftJinro:
					Bukkit.broadcast(Yakusyoku.getYakuColor( Yakusyoku.getYaku(e.getPlayer()) ) +"[" + Yakusyoku.getYaku2moji( Yakusyoku.getYaku(e.getPlayer()) ) + "] <"+e.getPlayer().getName()+"> " + e.getMessage(),"axtuki1.Jinro.GameMaster");
					break;
				case OneNightJinro:
					Bukkit.broadcast(OneNightYakusyoku.getYakuColor( OneNightYakusyoku.getYaku(e.getPlayer()) ) +"[" + OneNightYakusyoku.getYaku2moji(OneNightYakusyoku.getYaku(e.getPlayer())) + "] <"+e.getPlayer().getName()+"> " + e.getMessage(),"axtuki1.Jinro.GameMaster");
					break;
			}

		}

		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandexec( PlayerCommandPreprocessEvent e ){
		Bukkit.broadcast(ChatColor.GRAY + "[CMD] " + e.getPlayer().getName() + ": " + e.getMessage(), "axtuki1.Jinro.GameMaster");
	}

	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPlayerMove(PlayerMoveEvent e) {
    	if(e.getTo().getBlockY() < -5){
    		e.getPlayer().damage(1000000000);
    	}
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(AsyncPlayerPreLoginEvent e){
		if(Status.getStatus() == Status.GamePlaying ) {
			if( !Jinro.getMain().getConfig().getBoolean("LoginSpectatorMode") ){
				if(!Yakusyoku.getPlayingPlayersName().contains(e.getName())) {
					e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "ゲーム中はログインすることができません＞＜" + "\n" + ChatColor.AQUA + "ゲームの進行状況: " + Timer.getDay() + "日目 " + Cycle.getStatusLocalize());
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPlayerJoin(PlayerJoinEvent e) {
		// 参加時
		Player p = e.getPlayer();

		if(Yakusyoku.getDeath(e.getPlayer())){
    		Jinro.TeleportToReikai(e.getPlayer());
    	} else if( Yakusyoku.getAlivePlayers().contains(e.getPlayer()) ){
			// do nothing.
		} else {
			Jinro.TeleportToRespawn(e.getPlayer());
		}
    	if(Jinro.getMain().getConfig().getBoolean("LoginAttention.enable")){
			if(!Jinro.getMain().getConfig().getString("LoginAttention.title").equalsIgnoreCase("") && Jinro.getMain().getConfig().getString("LoginAttention.title") != null){
    			e.getPlayer().sendMessage(Jinro.getMain().getConfig().getString("LoginAttention.title"));
			}
    		e.getPlayer().sendMessage(Jinro.getMain().getConfig().getString("LoginAttention.msg"));
		}
		if(Status.getStatus() == Status.GamePlaying ) {
			if ( Yakusyoku.getYaku(p) == null && Jinro.getMain().getConfig().getBoolean("LoginSpectatorMode") && !p.hasPermission("axtuki1.Jinro.GameMaster") ) {
				p.setGameMode(org.bukkit.GameMode.SPECTATOR);
				Data.set("Players." + p.getUniqueId() + ".Spectator", true);
				p.sendMessage(ChatColor.GREEN + "==== 現在観戦モードです ====");
				// p.sendTitle(ChatColor.GREEN + "現在観戦モードです", ChatColor.AQUA + "" + Timer.getDay() + "日目 " + Cycle.getStatusLocalize(), 20, 50, 10);
				e.setJoinMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + Utility.myReplaceAll(ChatColor.YELLOW.toString(),"",e.getJoinMessage()));
			}
    	}
		p.setPlayerListName(p.getName() + " ");
    	if(Jinro.getMain().getConfig().getBoolean("ShowComingOut")){
			Yakusyoku y = ComingOut.getComingOut(p);
			if (y != null) {
				if (y == Yakusyoku.黒) {
					p.setPlayerListName("[●] " + p.getName() + " ");
				} else if (y == Yakusyoku.白) {
					p.setPlayerListName("[○] " + p.getName() + " ");
				} else {
					p.setPlayerListName(Yakusyoku.getYakuColor(y) + "[" +  Yakusyoku.getYaku2moji(y) + "] " + p.getName() + " ");
				}
			}
		}
		if(Yakusyoku.getDeath(p.getPlayer())){
			p.setPlayerListName(ChatColor.BLACK + "[霊界] "+ p.getName() + " ");
		}
		if(p.hasPermission("axtuki1.Jinro.GameMaster")){
			p.setPlayerListName(ChatColor.YELLOW + "[GM] "+p.getName()+" ");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(Status.getStatus() == Status.GamePlaying ) {
			Player p = e.getPlayer();
			if ( Yakusyoku.getYaku(p) == null && Yakusyoku.getSpecPlayers().contains(p)) {
				e.setQuitMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + " left the game.");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPlayerDeath(PlayerDeathEvent e) {

		switch (GameMode.getGameMode()) {
			case OneNightJinro:
				Player Ex = OneNightYakusyoku.getExecution();
				if (e.getEntity().equals(Ex) && Status.getStatus() == Status.GamePlaying) {
					e.setDeathMessage(ChatColor.DARK_RED + e.getEntity().getName() + "は処刑されました。");
					e.getEntity().getPlayer().setGlowing(false);
					e.getEntity().getPlayer().setPlayerListName(ChatColor.BLACK + "[霊界] " + e.getEntity().getName() + " ");
					OneNightYakusyoku.setDeath(e.getEntity());
					e.setKeepInventory(false);
				} else {
					if (e.getEntity().getKiller() != null) {
						if (e.getEntity().getKiller().hasPermission("axtuki1.Jinro.GameMaster")) {
							Stats.setDeath(e.getEntity(), Stats.death.GM, (Stats.getDeath(e.getEntity(), Stats.death.GM) + 1));
							if (!Status.getStatus().equals(Status.GamePlaying)) {
								Challenge.CheckOpen(e.getEntity());
							}
						}
					}
					e.setDeathMessage(null);
					e.setKeepInventory(true);
				}
				break;
			case MinecraftJinro:
				Ex = Yakusyoku.getExecution(Timer.getDay());
				if (e.getEntity().equals(Ex) && Status.getStatus() == Status.GamePlaying) {
					e.setDeathMessage(ChatColor.DARK_RED + e.getEntity().getName() + "は処刑されました。");
					e.getEntity().getPlayer().setGlowing(false);
					e.getEntity().getPlayer().setPlayerListName(ChatColor.BLACK + "[霊界] " + e.getEntity().getName() + " ");
					Yakusyoku.setDeath(e.getEntity());
					Stats.setDeath(e.getEntity(), Stats.death.Execution, (Stats.getDeath(e.getEntity(), Stats.death.Execution) + 1));
					int Alive = Data.getInt("Status.Alive");
					int Death = Data.getInt("Status.Death");
					Data.set("Status.Alive", Alive - 1);
					Data.set("Status.Death", Death + 1);
					ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: 0人");
					ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: " + Alive + "人");
					ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: " + Death + "人");
					ScoreBoard.getInfoObj().getScore(ChatColor.AQUA + "生存人数: " + (Alive - 1) + "人").setScore(2);
					ScoreBoard.getInfoObj().getScore(ChatColor.RED + "死亡人数: " + (Death + 1) + "人").setScore(1);
					if (Yakusyoku.getYaku(e.getEntity().getPlayer()) == Yakusyoku.爆弾魔) {
						int random = new Random().nextInt(Yakusyoku.getAlivePlayers().size());
						Player miti = (Player) Yakusyoku.getAlivePlayers().toArray()[random];
						Bukkit.broadcastMessage(ChatColor.DARK_RED + miti.getName() + "が爆発に巻き込まれて死亡しました。");
						Yakusyoku.setDeath(miti);
						Jinro.TeleportToReikai(miti);
						Stats.setDeath(miti, Stats.death.Explosion, (Stats.getDeath(miti, Stats.death.Explosion) + 1));
						Alive = Data.getInt("Status.Alive");
						Death = Data.getInt("Status.Death");
						Data.set("Status.Alive", Alive - 1);
						Data.set("Status.Death", Death + 1);
						ScoreBoard.getScoreboard().resetScores(ChatColor.AQUA + "生存人数: " + Alive + "人");
						ScoreBoard.getScoreboard().resetScores(ChatColor.RED + "死亡人数: " + Death + "人");
						ScoreBoard.getInfoObj().getScore(ChatColor.AQUA + "生存人数: " + (Alive - 1) + "人").setScore(2);
						ScoreBoard.getInfoObj().getScore(ChatColor.RED + "死亡人数: " + (Death + 1) + "人").setScore(1);
						e.setKeepInventory(false);
					}
					e.setKeepInventory(false);
				} else {
					if (e.getEntity().getKiller() != null) {
						if (e.getEntity().getKiller().hasPermission("axtuki1.Jinro.GameMaster")) {
							Stats.setDeath(e.getEntity(), Stats.death.GM, (Stats.getDeath(e.getEntity(), Stats.death.GM) + 1));
							if (!Status.getStatus().equals(Status.GamePlaying)) {
								Challenge.CheckOpen(e.getEntity());
							}
						}
					}
					e.setDeathMessage(null);
					e.setKeepInventory(true);
				}
				break;
		}
		e.getEntity().setVelocity(new Vector());
		e.getEntity().spigot().respawn();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPlayerRespawn(PlayerRespawnEvent e) {
    	if( Status.getStatus() == Status.GamePlaying && ( e.getPlayer() == Yakusyoku.getExecution() || Yakusyoku.getDeath(e.getPlayer()) ) ){
			e.setRespawnLocation(Jinro.getReikaiLoc());
			return;
		}
		e.setRespawnLocation(Jinro.getRespawnLoc());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerTeleportEvent e) {
		// つかうかも
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamageByEntity(EntityDamageByEntityEvent e) {
		if( e.getDamager().hasPermission("axtuki1.Jinro.GameMaster") ) {
			return;
		}

		if (e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow)e.getDamager();
			arrow.setCritical(true);
			e.setDamage(5);
			LivingEntity p = (LivingEntity) e.getEntity();
			p.setNoDamageTicks(0);
			p.setVelocity(new Vector());
			return;
		}

		if(e.getDamager().getType() == EntityType.FALLING_BLOCK || e.getDamager().getType() == EntityType.CREEPER){
			Player Ex = Yakusyoku.getExecution( Timer.getDay() );
			if( !e.getEntity().equals( Ex ) ){
				e.setCancelled(true);
				return;
			}
			e.setDamage(1000);
			return;
		}

		if( !e.getEntity().equals( Yakusyoku.getExecution( Timer.getDay() ) ) && !e.getEntity().equals( OneNightYakusyoku.getExecution() ) ){
			e.setCancelled(true);
			return;
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(ProjectileHitEvent e){
		if (e.getEntity() instanceof Arrow) {
			Arrow a = (Arrow)e.getEntity();
			a.remove();
		}
	}

/*
* @SuppressWarnings("deprecation")
public static void changeChestState(Location loc, boolean open) {
    byte dataByte = (open) ? (byte) 1 : 0; // The byte of data used for the note and animation packet (1 if true, 0 if false)
    for (Player player : Bukkit.getOnlinePlayers()) {
        player.playNote(loc, (byte) 1, dataByte); // Play the sound
        BlockPosition position = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()); // Create the block position using loc
        // Instantiate animation packet, get NMS Block using getById() and loc.getBlock() (deprecated), the server version may vary
        PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getById(loc.getBlock().getTypeId()), (byte) 1, dataByte);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(blockActionPacket); // Send animation packet to CraftPlayer
    }
}*/
}
