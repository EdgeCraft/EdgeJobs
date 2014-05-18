package net.edgecraft.edgejobs.api;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecore.user.UserManager;

import org.bukkit.entity.Player;

public class JobManager {

	private static final JobManager instance = new JobManager();
	
	private final ArrayList<AbstractJob> _jobs = new ArrayList<>();
	private final ArrayList<AbstractSidejob> _sidejobs = new ArrayList<>();
	private final ArrayList<String> _working = new ArrayList<>();
	private final HashMap<String, AbstractJob> _workers = new HashMap<>();
	
	private JobManager() { /* ... */ }
	
	
	public boolean registerJob( AbstractJob job ) {
		if( job == null ) return false;
		
		if( job instanceof AbstractSidejob )
			return registerSidejobByInstance( (AbstractSidejob) job );
		else
			return registerJobByInstance( job );
	}
	
	public boolean registerJobByInstance( AbstractJob job ) {
		if( job == null || job instanceof AbstractSidejob ) return false;
		
		return _jobs.add( job );
	}
	
	public boolean deleteJobByInstance( AbstractJob job ) {
		return _jobs.remove( job );
	}
	
	public boolean registerSidejobByInstance( AbstractSidejob job ) {
		if( job == null ) return false;
		
		return _sidejobs.add( job );
	}
	
	public boolean deleteSidejobByInstance( AbstractSidejob job ) {
		return _sidejobs.remove( job );
	}
	
	public boolean registerWorker( String name, AbstractJob job ) {
		if( name == null || name.trim().length() == 0 || job == null ) return false;
		
		if( getJob( job ) == null ) registerJob( job );
		
		_workers.put( name, job );
		return true;
	}
	
	public boolean registerWorker( Player p, AbstractJob job ) {
		return registerWorker( p.getName(), job );
	}
	
	public boolean registerWorker( User u, AbstractJob job ) {
		return registerWorker( u.getName(), job );
	}
	
	public boolean deleteWorker( String name ) {
		if( name == null || name.trim().length() == 0 ) return false;
		
		return( (_workers.remove( name ) == null ) ? false : true );
	}
	
	public boolean deleteWorker( Player p ) {
		if( p == null ) return false;
		
		return deleteWorker( p.getName() );
	}
	
	public boolean deleteWorker( User u ) {
		if( u == null ) return false;
		
		return deleteWorker( u.getName() );
	}
	
	public boolean setWorking( String name, boolean status ) {
		if( name == null || name.trim().length() == 0 ) return false;
		
		if( status )
			return _working.add( name );
		else
			return _working.remove( name );
	}
	
	public boolean setWorking( Player p, boolean status ) {
		if( p == null ) return false;
		
		return setWorking( p.getName(), status );
	}
	
	public boolean isWorking( String name ) {
		return _working.contains( name );
	}
	
	public boolean isWorking( Player p ) {
		return isWorking( p.getName() );
	}
	
	public AbstractJob getJob( AbstractJob job ) {
		if( job == null ) return null;
		
		if( job instanceof AbstractSidejob ) return getSidejobByInstance( (AbstractSidejob) job );
		else return getJobByInstance( job );
	}
	
	public AbstractJob getJob( String name ) {
		if( name == null || name.trim().length() == 0 ) return null;
		
		AbstractJob job = getJobByName( name );
		if( job == null ) job = getSidejobByName( name );
		
		return job;
	}

	public AbstractJob getJob( Player p ) {
		if( !_workers.containsKey( p.getName() ) ) return null;
		
		return _workers.get( p.getName() );
	}
	
	public AbstractJob getJob( User u ) {
		if( !_workers.containsKey( u.getName() ) ) return null;
		
		return _workers.get( u.getName() );
	}
	
	public AbstractJob getJobByInstance( AbstractJob job ) {
		if( job == null ) return null;
		
		return _jobs.get( _jobs.indexOf( job ) );
	}
	
	public AbstractSidejob getSidejobByInstance( AbstractSidejob job ) {
		if( job == null ) return null;
		
		return _sidejobs.get( _sidejobs.indexOf( job ) ); 
	}
	
	public AbstractJob getJobByName( String name ) {
		if( name == null || name.trim().length() == 0 ) return null;
		
		for( AbstractJob job : _jobs )
			if( job.getName().equals( name ) ) return job;
		
		return null;
	}
	
	public AbstractJob getSidejobByName( String name ) {
		if( name == null || name.trim().length() == 0 ) return null;
		
		for( AbstractSidejob job : _sidejobs )
			if( job.getName().equals( name ) ) return job;
		
		return null;
	}
	
	public boolean hasJob( String name ) {
		return ( _workers.get( name ) != null );
	}
	
	public boolean hasJob( Player p ) {
		return hasJob( p.getName() );
	}
	
	public boolean hasJob( User u ) {
		return hasJob( u.getName() );
	}
	
	public ArrayList<AbstractJob> getJobs() { return _jobs; }
	public ArrayList<AbstractSidejob> getSidejobs() { return _sidejobs; }
	public ArrayList<String> getWorking() { return _working; }
	public HashMap<String, AbstractJob> getWorkers() { return _workers; }
	
	public void syncJobs(){

		try {
			
			List<Map<String, Object>> result = EdgeCoreAPI.databaseAPI().getResults("SELECT * FROM edgejobs_jobs;");
			
			for(Map<String, Object> r : result){
				
				AbstractJob job = JobManager.getInstance().getJob((String) r.get("job"));
				UUID uuid = UUID.fromString((String) r.get("uuid"));
				
				JobManager.getInstance().registerWorker(UserManager.getInstance().getUser(uuid), JobManager.getInstance().getJob(job));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean containsDB(UUID uuid){
		List<Map<String, Object>> result = null;
		try {
			result = EdgeCoreAPI.databaseAPI().getResults("SELECT * FROM edgejobs_jobs;");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(result == null) return false;
		
		for(Map<String, Object> r : result){
			
			UUID search = UUID.fromString((String) r.get("uuid"));
			
			if(search != null){
				
				if(search == uuid) return true;
			}
			
		}
		return false;
	}
	
	public boolean setJobDB(UUID uuid, AbstractJob job){
		try {
			
			if(!containsDB(uuid)){
				PreparedStatement ps = EdgeCoreAPI.databaseAPI().prepareStatement("INSERT INTO edgejobs_jobs (uuid,job) VALUES (?,?);");
				ps.setString(1, uuid.toString());
				ps.setString(2, job.getName());
				ps.executeUpdate();
			}
			else {
				PreparedStatement ps = EdgeCoreAPI.databaseAPI().prepareStatement("UPDATE edgejobs_jobs set job = ? where uuid = ?;");
				ps.setString(1, job.getName());
				ps.setString(2, uuid.toString());
				ps.executeUpdate();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static final JobManager getInstance() { return instance; }
}
