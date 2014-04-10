package net.edgecraft.edgejobs.jobs;

import net.edgecraft.edgejobs.api.AbstractJob;

public class Sailor extends AbstractJob {

	private static final Sailor instance = new Sailor();
	
	private Sailor() {
		super( "Sailor", AbstractJob.default_pay );
	}
	
	public static final Sailor getInstance() {
		return instance;
	}
}
