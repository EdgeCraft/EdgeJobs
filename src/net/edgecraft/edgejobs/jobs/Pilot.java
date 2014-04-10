package net.edgecraft.edgejobs.jobs;

import net.edgecraft.edgejobs.api.AbstractJob;

public class Pilot extends AbstractJob {

	private static final Pilot instance = new Pilot();
	
	private Pilot() {
		super( "Pilot", AbstractJob.default_pay );
	}
	
	public static final Pilot getInstance() {
		return instance;
	}
	
}
