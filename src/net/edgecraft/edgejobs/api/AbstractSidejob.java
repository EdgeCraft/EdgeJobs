package net.edgecraft.edgejobs.api;

public abstract class AbstractSidejob extends AbstractJob {

	public AbstractSidejob( String name, double pay ) {
		super(name, pay);
	}
	
	public abstract boolean hasDoneWork();

}
