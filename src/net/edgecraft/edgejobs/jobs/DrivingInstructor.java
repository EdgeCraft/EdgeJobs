package net.edgecraft.edgejobs.jobs;

import net.edgecraft.edgejobs.api.AbstractJob;

public class DrivingInstructor extends AbstractJob {

	private static final DrivingInstructor instance = new DrivingInstructor();
	
	private DrivingInstructor() {
		super( "Driving Instructor", AbstractJob.default_pay );
	}
	
	public static final DrivingInstructor getInstance() { return instance; }
	
}
