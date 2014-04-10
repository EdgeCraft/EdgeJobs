package net.edgecraft.edgejobs;

import net.edgecraft.edgejobs.api.JobManager;

public class EdgeJobsAPI {

	private static final JobManager _jobsAPI = EdgeJobs.getJobs();
	
	public static final JobManager jobsAPI() {
		return _jobsAPI;
	}
	
}
