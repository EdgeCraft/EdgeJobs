package net.edgecraft.edgejobs.events;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecore.user.UserManager;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgejobs.EdgeJobsAPI;
import net.edgecraft.edgejobs.api.JobManager;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;

public class HandleItemEvents implements Listener {

	private static final UserManager users = EdgeCoreAPI.userAPI();
	private static final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	private static final JobManager jobs = EdgeJobsAPI.jobsAPI();
	
	@EventHandler
	public void handleInventoryOpen( InventoryOpenEvent e )
	{
		final Player p = (Player) e.getPlayer();
		
		if( !jobs.isWorking( p ) ) return;
		
		final InventoryHolder holder = e.getInventory().getHolder();
		
		if( holder instanceof Chest || holder instanceof DoubleChest )
		{
			e.setCancelled( true );
		}
	}
	
	@EventHandler
	public void handleItemDrop( PlayerDropItemEvent e )
	{
		if( jobs.isWorking( e.getPlayer() ) ) e.setCancelled( true );
	}
	
	@EventHandler
	public void handleBlockBreak( BlockBreakEvent e )
	{
		final Player p = e.getPlayer();
		final User u = users.getUser( p.getName() );
		final CuboidType t = CuboidType.getType( Cuboid.getCuboid( p.getLocation() ).getType().getTypeId() );
		
		if( p == null || u == null || t == null ) return;
		
		if( t.equals( CuboidType.SURVIVAL ) && !jobs.isWorking( p ) )
		{
			p.sendMessage( lang.getColoredMessage( u.getLanguage(), "cuboid_nopermission" ) );
			e.setCancelled( true );
		}
	}
	
	@EventHandler
	public void handleBlockPlace( BlockPlaceEvent e )
	{
		final Player p = e.getPlayer();
		final User u = users.getUser( p.getName() );
		final CuboidType t = CuboidType.getType( Cuboid.getCuboid( p.getLocation() ).getType().getTypeId() );
		
		if( p == null || u == null || t == null ) return;
		
		if( t.equals( CuboidType.SURVIVAL ) && !jobs.isWorking( p ) )
		{
			p.sendMessage( lang.getColoredMessage( u.getLanguage(), "cuboid_nopermission" ) );
			e.setCancelled( true );
		}
	}
	
}
