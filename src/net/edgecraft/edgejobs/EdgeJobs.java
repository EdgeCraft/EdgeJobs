package net.edgecraft.edgejobs;

import java.util.logging.Logger;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgejobs.api.JobManager;
import net.edgecraft.edgejobs.cmds.JobCommands;
import net.edgecraft.edgejobs.events.HandleItemEvents;
import net.edgecraft.edgejobs.events.HandlePlayerEvents;
import net.edgecraft.edgejobs.jobs.Broker;
import net.edgecraft.edgejobs.jobs.Criminal;
import net.edgecraft.edgejobs.jobs.Doctor;
import net.edgecraft.edgejobs.jobs.Farmer;
import net.edgecraft.edgejobs.jobs.Firefighter;
import net.edgecraft.edgejobs.jobs.Miner;
import net.edgecraft.edgejobs.jobs.Timber;
import net.edgecraft.edgejobs.tasks.DBSyncJobTask;
import net.edgecraft.edgejobs.tasks.JobPayTask;
import net.edgecraft.edgejobs.tasks.SidejobPayTask;

import org.bukkit.plugin.java.JavaPlugin;

public class EdgeJobs extends JavaPlugin {

	public static final Logger log = EdgeCore.log;
	private static EdgeJobs instance;
	private static final String jobsTable = "edgejobs_jobs";
	
	private static final JobManager _jobs = JobManager.getInstance();
	
	@Override
	public void onLoad() {
		instance = this;
		
		// Database
		// Check for jobs table | (c) by Panjab
		try {
			EdgeCoreAPI
					.databaseAPI()
					.prepareStatement(
							"CREATE TABLE IF NOT EXISTS " + EdgeJobs.jobsTable
									+ " (uuid VARCHAR(36) NOT NULL, "
									+ "job VARCHAR(16) NOT NULL DEFAULT '');")
					.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onEnable() {
		
		registerJobs();
		registerCommands();
		registerTasks();
		registerHandlers();
		
		Firefighter.FireCommand.getInstance().fire();
		
		new DBSyncJobTask().runTaskTimer(this, 10L, 20L * 60 * 25);
		
		log.info( "[EdgeJobs] enabled." );
	}
	
	@Override
	public void onDisable() {
		
		log.info( "[EdgeJobs] disabled.");
	}
	
	private void registerTasks() {
		
		getServer().getScheduler().runTaskTimerAsynchronously( this, new JobPayTask(), 40L, 200L);
		getServer().getScheduler().runTaskTimerAsynchronously( this, new SidejobPayTask(), 40L, 250L);
	}
	
	private void registerCommands() {
		JobCommands.getInstance();
	}
	
	private void registerHandlers() {
		getServer().getPluginManager().registerEvents( new HandleItemEvents(), this );
		getServer().getPluginManager().registerEvents( new HandlePlayerEvents(), this );
		getServer().getPluginManager().registerEvents( new Criminal.DrugHandler(), this );
	}
	
	private void registerJobs() {
		_jobs.registerJob( Doctor.getInstance() );
		_jobs.registerJob( Firefighter.getInstance() );
		_jobs.registerJob( Criminal.getInstance() );
		_jobs.registerJob( Broker.getInstance() );
		_jobs.registerJob( Farmer.getInstance() );
		_jobs.registerJob( Miner.getInstance() );
		_jobs.registerJob( Timber.getInstance() );
		return;
	}
	
	public static final JobManager getJobs() {
		return _jobs;
	}
	
	public static EdgeJobs getInstance()
	{
		return instance;
	}
	
}
