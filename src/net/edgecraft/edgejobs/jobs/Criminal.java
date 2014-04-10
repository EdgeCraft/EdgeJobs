package net.edgecraft.edgejobs.jobs;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.AbstractJobCommand;

public class Criminal extends AbstractJob {

	private static final Criminal instance = new Criminal();
	
	private Criminal() {
		super( "Criminal", AbstractJob.default_pay );
	}
	
	public static final Criminal getInstance() { return instance; }
	
	public static class CocaineCommand extends AbstractJobCommand {

		private static final CocaineCommand instance = new CocaineCommand();
		
		private CocaineCommand() {
			super( Criminal.getInstance() );
		}
		
		public static final CocaineCommand getInstance(){ return instance; }

		@Override
		public String[] getNames() {
			return new String[]{ "cocaine" };
		}

		@Override
		public boolean runImpl( Player p, User u, String[] args ) {
			
			final ItemStack sucre = p.getItemInHand();
			
			if( !( sucre.getType().equals( Material.SUGAR ) ) )
			{
				p.sendMessage(lang.getColoredMessage( u.getLang(), "job_criminal_onlysugar"));
				return true;
			}
			
			sucre.getItemMeta().setDisplayName("Cocaine");
			return true;
		}

		@Override
		public void sendUsageImpl( CommandSender sender ) {
			sender.sendMessage( EdgeCore.usageColor + "/cocaine" );
		}

		@Override
		public boolean validArgsRange( String[] args ) {
			return ( args.length == 1 );
		}
	}
	
	public static class DrugHandler implements Listener {
		
		@EventHandler
		public void onDrug( PlayerInteractEvent e ) {
			
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				final ItemStack item = e.getItem();
				
				if( item == null ) return;
				
					
				if( item.getType().equals( Material.SUGAR ) && item.getItemMeta().getDisplayName().equalsIgnoreCase( "Cocaine" )){
						e.getPlayer().addPotionEffect( new PotionEffect( PotionEffectType.SPEED, 300, 20 ) );
				}
			}
		}
	}
}
