package net.edgecraft.edgejobs.cmds;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgejobs.EdgeJobsAPI;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.JobManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand extends AbstractCommand {

	private static final JobManager jobs = EdgeJobsAPI.jobsAPI();
	
	private static final JobCommand instance = new JobCommand();
	
	private JobCommand() { /* ... */ }
	
	public static final JobCommand getInstance() { return instance; }
	
	@Override
	public Level getLevel() {
		return Level.USER;
	}

	@Override
	public String[] getNames() {
		return new String[]{ "job" };
	}

	@Override
	public boolean runImpl( Player p, User u, String[] args ) {
		
		final String userLang = u.getLang();
		
		if( args[1].equalsIgnoreCase( "join" ) ) {
			if( args.length > 3 ) {
				sendUsage( p );
				return true;
			}
			
			if(!JobManager.getInstance().hasJob(p)){
				p.sendMessage(lang.getColoredMessage(userLang, "job_nojob"));
				return true;
			}
			
			AbstractJob job = jobs.getJob( u );
			
			if( args.length == 3 )
				job = jobs.getJob( args[2] );
			
			if( job == null ) {
				p.sendMessage( lang.getColoredMessage( userLang, "job_notfound" ) );
				return true;
			}
			
			boolean joined = job.join( p );
			
			String key = "";
			if( joined ) key = "job_join_success";
			else key = "job_noregion";
			
			p.sendMessage( lang.getColoredMessage( userLang, key ) );
			return joined;
		}
		
		if( args[1].equalsIgnoreCase( "leave" ) && args.length == 2 ) {
			
			final AbstractJob job = jobs.getJob( u );
			
			if(job == null){
				p.sendMessage(lang.getColoredMessage(userLang, "job_nojob"));
				return true;
			}
			
			boolean quit = job.leave( p );
			
			String key = "";
			
			if( quit ) key = "job_quit_success";
			else key = "job_quit_failure";
			
			p.sendMessage( lang.getColoredMessage( userLang, key) );
			
			return quit;
			
		}
		
		if( args[1].equalsIgnoreCase( "setjob" ) && args.length == 4 ) {
			
			final User target = users.getUser( args[2] );
			final AbstractJob job = jobs.getJob( args[3] );
			
			if(target == null){
				p.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			if(job == null){
				p.sendMessage(lang.getColoredMessage(userLang, "job_notfound"));
				return true;
			}
			
			if( !Level.canUse( target, Level.SUPPORTER ) ) {
				p.sendMessage( lang.getColoredMessage( userLang, "job_setjob_noperm" ) );
				return false;
			}
			
			boolean set = jobs.registerWorker( u.getPlayer(), job );
			
			String key = "";
			
			if( set ) key = "job_setjob_success";
			else key = "job_setjob_failure";
			
			p.sendMessage( lang.getColoredMessage( userLang, key ) );
			JobManager.getInstance().syncJobs();
			return set;
		}
		
		if( args[1].equalsIgnoreCase( "getjob" ) && args.length == 3 ) {
			
			JobManager.getInstance().syncJobs();
			
			final User target = users.getUser( args[2] );
			
			if(target == null){
				p.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			if( !jobs.hasJob( target ) ) {
				p.sendMessage( lang.getColoredMessage( userLang, "job_nojob" ) );
				return false;
			}
			
			p.sendMessage( lang.getColoredMessage( userLang, "job_getjob" ).replace( "[0]", target.getName()).replace( "[1]", jobs.getJob( target ).getName() ) );
			return true;
		}
		
		sendUsage( p );
		return false;
	}

	@Override
	public void sendUsageImpl( CommandSender sender ) {
		sender.sendMessage( EdgeCore.usageColor + "/job join [job]" );
		sender.sendMessage( EdgeCore.usageColor + "/job leave" );
		
		if( !Level.canUse( users.getUser( sender.getName()), Level.MODERATOR ) ) return;
		sender.sendMessage( EdgeCore.usageColor + "/job setjob <user> <job>" );
		sender.sendMessage( EdgeCore.usageColor + "/job getjob <user>" );
		return;
	}

	@Override
	public boolean validArgsRange( String[] args ) {
		return ( args.length >= 2 && args.length <= 4 );
	}

}
