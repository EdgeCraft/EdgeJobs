package net.edgecraft.edgejobs.jobs;

import net.edgecraft.edgejobs.api.AbstractJob;

public class Reporter extends AbstractJob {

	private static final Reporter instance = new Reporter();
	
	private Reporter() {
		super( "Reporter", AbstractJob.default_pay );
	}
	
	public static final Reporter getInstance() {
		return instance;
	}
}
