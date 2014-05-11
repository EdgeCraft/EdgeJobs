package net.edgecraft.edgejobs.events;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.UserManager;
import net.edgecraft.edgecuboid.EdgeCuboidAPI;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgejobs.EdgeJobsAPI;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.JobManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HandlePlayerEvents implements Listener {

	private static final UserManager users = EdgeCoreAPI.userAPI();
	private static final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	private static final CuboidHandler cuboids = EdgeCuboidAPI.cuboidAPI();
	private static final JobManager jobs = EdgeJobsAPI.jobsAPI();
	
	@EventHandler
	public void handlePlayerJoin( PlayerJoinEvent e ) {
		jobs.setWorking( e.getPlayer(), false );
	}
	
	@EventHandler
	public void handlePlayerQuit( PlayerQuitEvent e ) {
		
		final Player quit = e.getPlayer();
		
		if( !jobs.isWorking( quit ) ) return;
		
		jobs.setWorking( quit, false );
		jobs.getJob( quit ).unequipPlayer( quit );
	}
	
	@EventHandler
	public void handlePlayerRespawn( PlayerRespawnEvent e )
	{
		final Player p = e.getPlayer();
		
		if( !jobs.isWorking( p ) ) return;
		
		final AbstractJob job = jobs.getJob( p );
		if( job == null ) return;
		
		p.addPotionEffect( new PotionEffect( PotionEffectType.WEAKNESS, 100, 2 ) );
		p.setHealth( p.getMaxHealth() / 10 );
		p.teleport( cuboids.getNearestCuboid( job.whereToStart(), p.getLocation() ).getSpawn() );
		job.unequipPlayer( p );
	}
	
	@EventHandler
	public void handlePlayerDamage( EntityDamageEvent e )
	{
		if( !( e.getEntity() instanceof Player ) ) return;
		
		final Player p = (Player) e.getEntity();
		
		if( jobs.isWorking( p ) && e.getDamage() >= p.getHealth() )
		{
			e.setCancelled( true );
			
			p.teleport( cuboids.getNearestCuboid( CuboidType.HOSPITAL, p.getLocation()).getSpawn() );
			//p.getInventory().setContents( AbstractJob.getOldPlayerInventory(p).getContents() );
			jobs.setWorking( p, false );
			jobs.getJob( p ).unequipPlayer( p );
			//AbstractJob.clearOldPlayerInventory( p );
			p.sendMessage( lang.getColoredMessage( users.getUser(p.getName()).getLanguage(), "job_died") );
			return;
		}
	}
}
