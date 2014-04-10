package net.edgecraft.edgejobs.jobs;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.cuboid.Habitat;
import net.edgecraft.edgecuboid.cuboid.Upgrade;
import net.edgecraft.edgecuboid.cuboid.Upgrade.UpgradeType;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.AbstractJobCommand;

public class Broker extends AbstractJob {

	private static final Broker instance = new Broker();
	
	private Broker() {
		super( "Broker", AbstractJob.default_pay );
	}
	
	public static final Broker getInstance() { return instance; }
	
	public static class HabitatUpgradeCommand extends AbstractJobCommand {
		
		private static final HabitatUpgradeCommand instance = new HabitatUpgradeCommand();
		
		private HabitatUpgradeCommand() {
			super( Broker.getInstance() );
		}
		
		public static final HabitatUpgradeCommand getInstance() { return instance; }

		@Override
		public String[] getNames() 
		{
			return new String[]{ "property" };
		}

		@Override
		public boolean runImpl( Player p, User u, String[] args ) 
		{
			final String userLang = u.getLang();
			
			if( args.length == 2 && args[1].equalsIgnoreCase( "list" ) )
			{
				final StringBuilder sb = new StringBuilder();
				
				for( UpgradeType upgrade : Upgrade.UpgradeType.values() ) {
					if (sb.length() > 0)
						sb.append(", ");
					
					sb.append(ChatColor.GOLD + upgrade.getName());
				}
				
				p.sendMessage(lang.getColoredMessage(userLang, "upgrade_list").replace("[0]", sb.toString()));
				
				return true;
			}
			
			final Habitat h = cuboids.getHabitat( args[2] );
			
			if( h == null )
			{
				p.sendMessage( lang.getColoredMessage( userLang, "unknownhabitat").replace( "[0]", args[2] ) );
				return false;
			}
			
			if( args.length == 3 && args[1].equalsIgnoreCase( "list" ) )
			{
				final StringBuilder sb = new StringBuilder();
				
				for( Map.Entry<Upgrade, Integer> entry : cuboids.getHabitat( args[2] ).getUpgrades().entrySet() )
				{
					
					if( sb.length() > 0 )
						sb.append( ", " );
					sb.append( entry.getKey().toString() );
				}
				
				p.sendMessage( sb.toString() );
				return true;
			}
			
			if( args.length != 4 )
			{
				sendUsage( p );
				return false;
			}
			
			final Upgrade upgr = new Upgrade( UpgradeType.valueOf( args[2].toUpperCase() ) );
			
			if( args[1].equalsIgnoreCase( "add-upgrade" ) )
			{	
				if ( h.getUpgrades().containsKey( upgr ) && !upgr.multipleUsage() ) {
					p.sendMessage( lang.getColoredMessage( userLang, "admin_upgrade_unlock_notmultiple" ) );
					return true;
				}
				
				final BankAccount state = economy.getAccount( 0 );
				final BankAccount broker = economy.getAccount( p.getName() );
				final BankAccount user = economy.getAccount( h.getOwner() );
				
				transactions.addTransaction( user, state, upgr.getPrice() / 100 * 75.0, "Habitat-Upgrade." );
				transactions.addTransaction( user, broker, upgr.getPrice() / 100 * 25.0, "Habitat-Upgrade." );
				
				h.getUpgrades().put( upgr, h.getUpgrades().get(upgr) + 1 );
				p.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_unlock_success").replace("[0]", upgr.getTypeName())
						.replace("[1]", h.getCuboid().getName())
						.replace("[2]", h.getUpgrades().get( upgr ) + ""));
				
				return true;
			}
			
			if( args[1].equalsIgnoreCase( "remove-upgrade" ) )
			{
				if ( !h.getUpgrades().containsKey( upgr ) ) {
					p.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_remove_notunlocked"));
					return true;
				}
				
				if ( h.getUpgrades().get( upgr ) == 0 ) {
					h.getUpgrades().remove( upgr );
					p.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_unlock_success").replace("[0]", upgr.getTypeName())
																									.replace("[1]", h.getCuboid().getName())
																									.replace("[2]", h.getUpgrades().get(upgr) + ""));
					h.getUpgrades().put( upgr, h.getUpgrades().get( upgr ) - 1);
					return true;
				}
				return true;
			}
			
			sendUsage( p );
			return false;
		}

		@Override
		public void sendUsageImpl( CommandSender sender ) 
		{
			sender.sendMessage( EdgeCore.usageColor + "/property add-upgrade <habitat> <upgrade>" );
			sender.sendMessage( EdgeCore.usageColor + "/property remove-upgrade <habitat> <upgrade>" );
			sender.sendMessage( EdgeCore.usageColor + "/property list [<habitat>]" );
		}

		@Override
		public boolean validArgsRange( String[] args ) 
		{
			return ( args.length >= 2 && args.length <= 4 );
		}
		
	}
	
}
