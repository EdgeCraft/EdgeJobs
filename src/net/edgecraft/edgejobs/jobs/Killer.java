package net.edgecraft.edgejobs.jobs;

import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.AbstractSidejob;

public class Killer extends AbstractSidejob {

	private static final Killer instance = new Killer();
	
	private Killer() {
		super( "Killer", AbstractJob.default_pay );
	}
	
	public static final Killer getInstance() {
		return instance;
	}
	
	@Override
	public boolean hasDoneWork() { return false; }

}
