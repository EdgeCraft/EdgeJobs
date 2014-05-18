package net.edgecraft.edgejobs.tasks;

import net.edgecraft.edgejobs.EdgeJobs;
import net.edgecraft.edgejobs.api.JobManager;

import org.bukkit.scheduler.BukkitRunnable;

public class DBSyncJobTask extends BukkitRunnable {

	@Override
	public void run() {
		
		EdgeJobs.log.info("[EdgeJobs] Starte automatische Jobs-Synchronisation..");
		JobManager.getInstance().syncJobs();
		EdgeJobs.log.info("[EdgeJobs] Automatische Jobs-Synchronisation abgeschlossen.");
		
	}

}