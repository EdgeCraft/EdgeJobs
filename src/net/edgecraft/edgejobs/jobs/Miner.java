package net.edgecraft.edgejobs.jobs;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.edgecraft.edgejobs.api.AbstractJob;

public class Miner extends AbstractJob {

	private static final Miner instance = new Miner();
	
	private final ItemStack _pickaxe = new ItemStack( Material.IRON_PICKAXE );
	
	private Miner() {
		super( "Miner", AbstractJob.default_pay );
	}
	
	public static final Miner getInstance() {
		return instance;
	}
	
	@Override
	public void equipPlayerImpl( Player p ) 
	{
		p.getInventory().addItem( _pickaxe );
	}
	
	@Override
	public boolean onJobQuit( Player p ) 
	{
		if( p == null ) return false;
		
		final PlayerInventory inv = p.getInventory();
		final ArrayList<ItemStack> stuff = new ArrayList<>();
		
		for( ItemStack stack : inv.getContents() )
		{
			if( take( stack ) )
			{
				stuff.add( stack );
				inv.remove( stack );
				continue;
			}
		}
		
		return true;
//		final Partition partition = PartitionManager.getPartition( p.getName() );
//		
//		if( partition == null ) return;
//		
//		for( ItemStack stack : stuff )
//		{
//			partition.getStore().addItem( new EdgeItemStack( stack ), 0 );
//		}
//		return;	
		
	}

	private boolean take( ItemStack stack )
	{
		if( stack == null ) return false;
		
		final Material m = stack.getType();
		
		if( m.equals( Material.COBBLESTONE ) ) return true;
		if( m.equals( Material.IRON_ORE ) ) return true;
		if( m.equals( Material.COAL_ORE ) ) return true;
		if( m.equals( Material.DIAMOND ) ) return true;
		if( m.equals( Material.DIRT ) ) return true;
		if( m.equals( Material.EMERALD_ORE ) ) return true;
		if( m.equals( Material.GOLD_ORE ) ) return true;
		if( m.equals( Material.LAPIS_ORE ) ) return true;
		if( m.equals( Material.QUARTZ_ORE ) ) return true;
		
		return false;
	}
	
}
