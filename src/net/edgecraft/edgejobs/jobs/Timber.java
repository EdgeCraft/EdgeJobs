package net.edgecraft.edgejobs.jobs;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.edgecraft.edgejobs.api.AbstractJob;

public class Timber extends AbstractJob {

	private static final Timber instance = new Timber();
	
	private final ItemStack _axe = new ItemStack( Material.IRON_AXE );
	
	private Timber() {
		super( "Timber", AbstractJob.default_pay );
	}
	
	public static final Timber getInstance() {
		return instance;
	}
	
	@Override
	public void equipPlayerImpl( Player p ) 
	{
		if( p == null ) return;
		p.getInventory().addItem( _axe );
	}
	
	@Override
	public boolean onJobQuit( Player p ) 
	{
		final PlayerInventory inv = p.getInventory();
		
		final ArrayList<ItemStack> wood = new ArrayList<>();
		
		for( ItemStack stack : inv.getContents() ) 			
			if( isWood(stack) ) 
			{
				wood.add(stack);
				inv.remove(stack);
			}
		
		return true;
//		final Partition partition = PartitionManager.getPartition( p.getName() );
//		
//		if( partition == null ) return;
//		
//		for( ItemStack stack : wood )
//		{
//			partition.getStore().addItem( new EdgeItemStack( stack ), 0 );
//		}
//		return;		
	}
	
	private boolean isWood( ItemStack stack ) 
	{
		return isWood( stack.getType() );
	}
	private boolean isWood( Material m ) 
	{
		
		if( m == null ) return false;
		
		if( m.equals(Material.WOOD ) ) return true;
		
		return false;
	}
}
