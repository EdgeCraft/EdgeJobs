package net.edgecraft.edgejobs.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.edgecraft.edgeconomy.EdgeConomyAPI;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.transactions.TransactionManager;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecuboid.EdgeCuboidAPI;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.shop.ShopHandler;
import net.edgecraft.edgejobs.EdgeJobsAPI;

public abstract class AbstractJobCommand extends AbstractCommand {

	protected static final JobManager jobs = EdgeJobsAPI.jobsAPI();
	protected static final TransactionManager transactions = EdgeConomyAPI.transactionAPI();
	protected static final Economy economy = EdgeConomyAPI.economyAPI();
	protected static final CuboidHandler cuboids = EdgeCuboidAPI.cuboidAPI();
	protected static final ShopHandler shops = EdgeCuboidAPI.shopAPI();
	
	private AbstractJob _job;
	
	public AbstractJobCommand( AbstractJob job ) {
		setJob( job );
	}
	
	@Override
	public Level getLevel() { return Level.USER; }
	
	@Override
	public boolean run( CommandSender sender, String[] args ) throws Exception {
		
		final AbstractJob job = jobs.getJob( users.getUser( sender.getName() ) );
		
		if(job == null){
			sender.sendMessage(lang.getColoredMessage(LanguageHandler.getDefaultLanguage(), "job_nojob"));
			return false;
		}
		
		if( job.equals( _job ) ) return super.run( sender, args );
		
		sender.sendMessage( lang.getColoredMessage( LanguageHandler.getDefaultLanguage(), "job_wrongjob" ) );
		return true;
	}
	
	@Override
	public void sendUsage( CommandSender sender ) {
		
		if( !(sender instanceof Player) ) super.sendUsage( sender );
		
		AbstractJob job = jobs.getJob( (Player)sender );
		
		if( job == null ) return;
		
		if( !(job.equals( getJob() ) ) ) return;
		else super.sendUsage( sender );
			
	}
	
	@Override
	public boolean sysAccess( CommandSender sender, String[] args ) {
			sender.sendMessage( lang.getColoredMessage( LanguageHandler.getDefaultLanguage(), "noconsole" ) );
			return true;
	}
	
	private void setJob( AbstractJob job ) {
		if( job == null ) return;
		
		_job = job;
	}
	
	public AbstractJob getJob() {
		return _job;
	}
	
}
