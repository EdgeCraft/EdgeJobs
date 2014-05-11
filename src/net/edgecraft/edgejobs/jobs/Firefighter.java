package net.edgecraft.edgejobs.jobs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboidAPI;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgejobs.EdgeJobs;
import net.edgecraft.edgejobs.api.AbstractJob;

public class Firefighter extends AbstractJob {

	private static final Firefighter instance = new Firefighter();
	
	private Firefighter() {
		super( "Firefighter", AbstractJob.default_pay );
	}
	
	public static final Firefighter getInstance() {
		return instance;
	}
	
	public static class FireCommand extends AbstractCommand {

		private static final FireCommand instance = new FireCommand();
		
		private boolean enabled = true;
		private static final CuboidHandler cuboids = EdgeCuboidAPI.cuboidAPI();
		
		private FireCommand() { /* ... */ }
		
		public static final FireCommand getInstance() { return instance; }
		
		@Override
		public Level getLevel() {
			return Level.DEVELOPER;
		}

		@Override
		public String[] getNames() {
			return new String[]{ "fire" };
		}

		@Override
		public boolean runImpl( Player p, User u, String[] args ) {
			
			final String userLang = u.getLang();
			
			if( args[1].equalsIgnoreCase( "enable" ) )
			{
				enabled = true;	
				p.sendMessage( lang.getColoredMessage(  userLang, "job_firecmd_enabled" ) );
				return true;
			}
			
			if( args[1].equalsIgnoreCase( "disable" ) )
			{
				enabled = false;
				p.sendMessage( lang.getColoredMessage(  userLang, "job_firecmd_disabled" ) );
				return true;
			}
			
			if( args[1].equalsIgnoreCase( "status" ) )
			{
				p.sendMessage( lang.getColoredMessage( userLang, "job_firecmd_isEnabled" ).replace("[0]", Boolean.toString( enabled ) ) );
				return true;
			}
			
			sendUsage( p );
			return true;
		}
		
		private void setRandomFire()
		{
			final Cuboid random = cuboids.getCuboid( CuboidType.SURVIVAL, false );
			
			random.getMinLocation().add(0, 1, 0).getBlock().setType(Material.FIRE);
			random.getMaxLocation().add(0, 1, 0).getBlock().setType(Material.FIRE);
			random.getCenter().add(0, 1, 0).getBlock().setType(Material.FIRE);
			random.getSpawn().add(0, 1, 0).getBlock().setType(Material.FIRE);
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage("[DEBUG] Random Fire has been set!");
			}
		}
		
		public void fire()
		{
			Bukkit.getScheduler().runTaskTimer( EdgeJobs.getInstance(), new Runnable() {

				@Override
				public void run() {
					if( enabled )
						setRandomFire();
				}
				
			}, 20L, 20L * 60 );
		}

		@Override
		public void sendUsageImpl( CommandSender sender ) {
			sender.sendMessage( EdgeCore.usageColor + "/fire enable" );
			sender.sendMessage( EdgeCore.usageColor + "/fire disable" );
			sender.sendMessage( EdgeCore.usageColor + "/fire status" );
		}

		@Override
		public boolean validArgsRange( String[] args ) {
			return ( args.length == 2 );
		}
		
	}
	
	@Override
	public CuboidType whereToStart() {
		return CuboidType.FIRESTATION;
	}
	
}
