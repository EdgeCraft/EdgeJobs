package net.edgecraft.edgejobs.jobs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecore.user.UserManager;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.AbstractJobCommand;

public class Policeman extends AbstractJob {

	private static final Policeman instance = new Policeman();
	
	private final HashMap<User, User> _arrested;
	
	private Policeman() {
		super( "Policeman", AbstractJob.default_pay );
		_arrested = new HashMap<>();
	}
	
	public static final Policeman getInstance() {
		return instance;
	}
	
	@Override
	public CuboidType whereToStart() {
		return CuboidType.POLICESTATION;
	}
	
	public final void arrest( User target, User officer ) {
		
		if( target == null || officer == null  || _arrested.containsKey( target ) || !jobs.getJob( officer ).equals( this ) ) return;
		
		_arrested.put( target, officer );
		target.getPlayer().teleport( cuboids.getNearestCuboid(CuboidType.JAIL, target.getPlayer().getLocation() ).getSpawn() );
	}
	
	public final void arrest( String target, String officer ) {
		
		final UserManager users = EdgeCoreAPI.userAPI();
		
		arrest( users.getUser( target ), users.getUser( officer ) );
	}
	
	public final void release( User target ) {
		if( target == null || !_arrested.containsKey( target )) return;
		
		_arrested.remove( target );	
		target.getPlayer().teleport( cuboids.getNearestCuboid( CuboidType.POLICESTATION, target.getPlayer().getLocation() ).getSpawn() );
	}
	
	public final void release( String name ) {
		 release( EdgeCoreAPI.userAPI().getUser( name ) );
	}
	
	public boolean isArrested( String name ) {
		return isArrested( EdgeCoreAPI.userAPI().getUser( name ) );
	}
	
	public boolean isArrested( User u ) {
		if( u == null || !_arrested.containsKey(u) ) return false;
		
		return true;
	}
	
	public User arrestedBy( User target ) {
		if( target == null || !_arrested.containsKey(target) ) return null;
		
		return _arrested.get( target );
	}
	
	public static class ArrestCommand extends AbstractJobCommand {
		private static final ArrestCommand instance = new ArrestCommand();
		
		private ArrestCommand() {
			super( Policeman.getInstance() );
		}

		public static final ArrestCommand getInstance() { return instance; }

		@Override
		public boolean runImpl( Player player, User user, String[] args ) 
		{
			final User target = users.getUser( args[1] );
			
			final Policeman police = Policeman.getInstance();
			
			if( target == null ) 
			{
				player.sendMessage( lang.getColoredMessage( user.getLanguage(), "notfound" ) );
				return false;
			}
			
			final Player targetPlayer = target.getPlayer();
			
			if( args.length == 2 ) 
			{
				if( !targetPlayer.isOnline() ) 
				{
					player.sendMessage( lang.getColoredMessage( user.getLanguage(), "notfound" ) );
					return true;
				}
				
				if( player.getLocation().distanceSquared( targetPlayer.getLocation() ) <= 3 ) 
				{ 
					Policeman.getInstance().arrest( target, user );
					return true;
				}
				
				sendUsage( player );
				return true;
			}
			
			if( args.length == 3 && args[1].equalsIgnoreCase( "protocol" ) ) 
			{
				player.sendMessage( lang.getColoredMessage( user.getLanguage(), "job_police_arrested_by" ).replace( "[0]", target.getName() ).replace( "[1]", police.arrestedBy( target ).getName() ) );
				return true;
			}
			
			sendUsage( player );
			return true;
		}

		@Override
		public String[] getNames() 
		{
			return new String[]{ "arrest" };
		}

		@Override
		public void sendUsageImpl( CommandSender sender ) 
		{
				sender.sendMessage( EdgeCore.usageColor + "/arrest <target>" );
				sender.sendMessage( EdgeCore.usageColor + "/arrest protocol <target>" );
				return;
		}

		@Override
		public boolean validArgsRange( String[] args ) 
		{
			return ( args.length == 2 || args.length == 3 );
		}
	}
	
	public static class ReleaseCommand extends AbstractJobCommand 
	{
		private static final ReleaseCommand instance = new ReleaseCommand();
		
		private ReleaseCommand() 
		{
			super( Policeman.getInstance() );
		}
		
		public static final ReleaseCommand getInstance() 
		{
			return instance;
		}

		@Override
		public String[] getNames() 
		{
			return new String[]{ "release" };
		}

		@Override
		public boolean runImpl( Player player, User user, String[] args ) 
		{
			
			final User target = users.getUser( args[1] );
			
			final Policeman police = Policeman.getInstance();
			
			if( target == null || !target.getPlayer().isOnline() ) 
			{
				player.sendMessage( lang.getColoredMessage( user.getLanguage(), "notfound" ) );
				return false;
			}
			
			if( !police.isArrested( target ) ) 
			{
				player.sendMessage( lang.getColoredMessage( user.getLanguage(), "job_police_notarrested" ) );
				return true;
			}
			
			police.release( target );
			player.sendMessage( lang.getColoredMessage( user.getLanguage(), "job_police_released" ) );
			
			sendUsage( player );
			return true;
		}

		@Override
		public void sendUsageImpl( CommandSender sender )
		{
			sender.sendMessage( EdgeCore.usageColor + "/release <target>" );
		}

		@Override
		public boolean validArgsRange(String[] args) 
		{
			return ( args.length == 2 );
		}
	}
	
	public static class WantedCommand extends AbstractJobCommand 
	{
		private static final WantedCommand instance = new WantedCommand();
		
		private final Map<User, SearchLevel> wanted;
		private final UserManager users = EdgeCoreAPI.userAPI();
		
		private WantedCommand() 
		{
			super( Policeman.getInstance() );
			wanted = new HashMap<User, SearchLevel>();
		}

		public static final WantedCommand getInstance() 
		{
			return instance;
		}
		
		@Override
		public String[] getNames() 
		{
			return new String[]{ "wanted" };
		}

		@Override
		public boolean runImpl( Player player, User user, String[] args ) 
		{
				if( args.length >= 2 && args.length <= 3 && args[1].equalsIgnoreCase("list") ) 
				{
					SearchLevel to = null;
					String prefix = "Top Wanted: ";
					
					if( args.length == 3 ) 
					{
						try
						{
							to = SearchLevel.getInstance( Integer.valueOf( args[2] ) );
							prefix = "Wanted[" + args[2] + "]";
						} 
						catch( NumberFormatException e ) 
						{
							sendUsage( player );
							return true;
						}
					}
						
					if( to == null ) to = SearchLevel.FIVE;
					
					final StringBuilder sb = new StringBuilder();
						
					for( User u : wanted.keySet() ) 
					{
						if( wanted.get( u ).equals( to ) ) 
						{
							if( sb.length() == 0 ) sb.append( "[ " + u.getName() );
							else sb.append( ", " + u.getName() );
						}
					}
					sb.append( " ]" );
						
					player.sendMessage( prefix + sb.toString() );	
				}
				
				if( args.length == 4 && args[1].equalsIgnoreCase( "set" ) ) 
				{
					User targetUser = null;
					SearchLevel targetLevel = null;
					
					try 
					{
						targetUser = users.getUser( args[2] );
						targetLevel = SearchLevel.getInstance( Integer.valueOf( args[3] ) );
					} 
					catch( NumberFormatException e ) 
					{
						sendUsage( player );
						return true;
					}
					
					if( targetUser == null || targetLevel == null ) 
					{
						sendUsage( player );
						return true;
					}
					
					wanted.put( targetUser, targetLevel );
					return true;
				}
			
				sendUsage( player );
				return true;
		}

		
		@Override
		public void sendUsageImpl( CommandSender sender ) 
		{
			sender.sendMessage( EdgeCore.usageColor + "/wanted set <target> <lvl>" );
			sender.sendMessage( EdgeCore.usageColor + "/wanted list [<num>]" );
			return;
		}

		@Override
		public boolean validArgsRange( String[] args ) 
		{
			return( args.length >= 2 && args.length <= 4 );
		}
		
	}
	
	public enum SearchLevel 
	{
		ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);
		
		private final int lvl;
		
		private SearchLevel( int lvl ) 
		{
			if( lvl >= 0 ) this.lvl = lvl;
			else this.lvl = 0;
		}
		
		public final int getLevel() 
		{
			return lvl;
		}
		
		public static final SearchLevel[] getSearchLevels() 
		{
			return new SearchLevel[]{ SearchLevel.ZERO, SearchLevel.ONE, SearchLevel.TWO, SearchLevel.THREE, SearchLevel.FOUR, SearchLevel.FIVE };
		}
		
		public static final SearchLevel getInstance( int lvl ) 
		{
			final SearchLevel[] levels = getSearchLevels();
			
			for( int i = 0; i < levels.length; i++ ) {
				if( lvl == levels[i].getLevel() ) {
					return levels[i];
				}
			}
			
			return SearchLevel.ZERO;
		}
		
	}

}
