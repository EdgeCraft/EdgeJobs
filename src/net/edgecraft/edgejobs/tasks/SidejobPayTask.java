package net.edgecraft.edgejobs.tasks;

import net.edgecraft.edgeconomy.EdgeConomyAPI;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.transactions.TransactionManager;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecore.user.UserManager;
import net.edgecraft.edgejobs.EdgeJobsAPI;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.AbstractSidejob;
import net.edgecraft.edgejobs.api.JobManager;

import org.bukkit.scheduler.BukkitRunnable;

public class SidejobPayTask extends BukkitRunnable {

	private static final JobManager jobs = EdgeJobsAPI.jobsAPI();
	private static final UserManager users = EdgeCoreAPI.userAPI();
	private static final Economy economy = EdgeConomyAPI.economyAPI();
	private static final TransactionManager transactions = EdgeConomyAPI.transactionAPI();
	
	@Override
	public void run() {
		
		for( String name : jobs.getWorkers().keySet() ) {
			
			final User u = users.getUser( name );
			final AbstractJob job = jobs.getJob( u );
			
			if( u == null || !( job instanceof AbstractSidejob) ) continue;
			
			if( ((AbstractSidejob) job).hasDoneWork() ) {
				final BankAccount state = economy.getAccount(0);
				final BankAccount user = economy.getAccount(u.getUUID());
				final String message = EdgeCoreAPI.languageAPI().getColoredMessage( u.getLanguage(), "job_transaction");
				
				transactions.addTransaction(state, user, job.getPay(), message);
			}
		}
		
	}

}
