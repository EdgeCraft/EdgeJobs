package net.edgecraft.edgejobs.api;

import org.bukkit.entity.Player;

import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecuboid.EdgeCuboidAPI;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgejobs.EdgeJobsAPI;

public abstract class AbstractJob {

	protected static final JobManager jobs = EdgeJobsAPI.jobsAPI();
	protected static final CuboidHandler cuboids = EdgeCuboidAPI.cuboidAPI();

	public static final String payhour = "17";
	public static final double default_pay = 1500;
	
	private String _name;
	private double _pay;
	
	public AbstractJob( String name, double pay ) {
		setName( name );
		setPay( pay );
	}
	
	public final String getName() {
		return _name;
	}
	
	public final double getPay() {
		return _pay;
	}
	
	private final void setName( String name ) {
		if( name == null || name.trim().length() == 0 ) return;
		
		_name = name;
	}
	
	private final void setPay( double pay ) {
		if( pay < 0 ) return;
		
		_pay = pay;
	}
	
	public AbstractCommand[] getCommands() { return null; }
	
	public CuboidType whereToStart() { return null; }
	
	public boolean onJobQuit( Player p ) { return true; }
	
	public void equipPlayerImpl( Player p ) { return; }
	
	public final void equipPlayer( Player p ) {
		if( p == null ) return; // TODO
		equipPlayerImpl( p );
	}
	
	public final void unequipPlayer( Player p ) {
		if( p == null ) return;
		
		p.getInventory().clear(); //TODO:
	}
	
	public boolean join( Player p ) {
		
		if(whereToStart() != null){
			
			if(Cuboid.getCuboid(p.getLocation()) == null || Cuboid.getCuboid(p.getLocation()).getType() != whereToStart()){
				
				return false;
			}
			
		}
		
		this.equipPlayer( p );
		return jobs.setWorking( p, true );
	}
	
	public boolean leave( Player p ) {
		if( !jobs.isWorking( p ) ) return false;
		
		this.unequipPlayer( p );
		return jobs.setWorking( p, false );
	}
	
	@Override
	public boolean equals( Object another )
	{
		if( this == another ) return true;
		if( another == null ) return false;
		if( this.getClass() != another.getClass() ) return false;
		
		final AbstractJob job = (AbstractJob) another;
		if( _name.equals( job.getName() ) && _pay == job.getPay() ) return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return _name.hashCode() * (int)_pay - (int)_pay;
	}
}
