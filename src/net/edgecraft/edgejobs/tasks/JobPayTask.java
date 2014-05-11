package net.edgecraft.edgejobs.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.edgecraft.edgeconomy.EdgeConomyAPI;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.transactions.TransactionManager;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecore.user.UserManager;
import net.edgecraft.edgejobs.EdgeJobsAPI;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.AbstractSidejob;
import net.edgecraft.edgejobs.api.JobManager;

import org.bukkit.scheduler.BukkitRunnable;

public class JobPayTask extends BukkitRunnable {

	private static final JobManager jobs = EdgeJobsAPI.jobsAPI();
	private static final UserManager users = EdgeCoreAPI.userAPI();
	private static final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	private static final Economy economy = EdgeConomyAPI.economyAPI();
	private static final TransactionManager transactions = EdgeConomyAPI.transactionAPI();
	
	@Override
	public void run() {
		
		final String time = new SimpleDateFormat( "HH" ).format( new Date( System.currentTimeMillis() ) );
		
		if( time.equals( AbstractJob.payhour ) ) {
			
			for( String name : jobs.getWorkers().keySet() ) {

				final User u = users.getUser( name );
				final AbstractJob job = jobs.getJob( u );
				
				if( job == null || job instanceof AbstractSidejob ) continue;
				
				final BankAccount state = economy.getAccount( 0 );
				final BankAccount target = economy.getAccount( u.getUUID() );
				final String message = lang.getColoredMessage( u.getLang(), "job_transaction" );
				
				transactions.addTransaction( state, target, job.getPay(), message );
			}
			
		}
		
	}

}
