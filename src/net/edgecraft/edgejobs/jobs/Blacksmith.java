package net.edgecraft.edgejobs.jobs;

import net.edgecraft.edgejobs.api.AbstractJob;

public class Blacksmith extends AbstractJob {

	private static final Blacksmith instance = new Blacksmith();
	
	private Blacksmith() {
		super( "Blacksmith", AbstractJob.default_pay );
	}
	
	public static final Blacksmith getInstance() {
		return instance;
	}

}
