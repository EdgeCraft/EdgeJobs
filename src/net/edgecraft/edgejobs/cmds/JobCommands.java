package net.edgecraft.edgejobs.cmds;

import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.CommandHandler;
import net.edgecraft.edgejobs.jobs.Broker;
import net.edgecraft.edgejobs.jobs.Criminal;
import net.edgecraft.edgejobs.jobs.Doctor;
import net.edgecraft.edgejobs.jobs.Firefighter;
import net.edgecraft.edgejobs.jobs.Policeman;

public class JobCommands {

	private static final JobCommands instance = new JobCommands();
	
	private JobCommands() {
		registerCommand( JobCommand.getInstance() );
		registerCommand( JobsCommand.getInstance() );
		registerCommand( Doctor.HealCommand.getInstance() );
		registerCommand( Firefighter.FireCommand.getInstance() );
		registerCommand( Criminal.CocaineCommand.getInstance() );
		registerCommand( Broker.HabitatUpgradeCommand.getInstance() );
		registerCommand( Policeman.ArrestCommand.getInstance() );
		registerCommand( Policeman.ReleaseCommand.getInstance() );
		registerCommand( Policeman.WantedCommand.getInstance() );
	}
	
	public static final JobCommands getInstance() {
		return instance;
	}
	
	private void registerCommand(AbstractCommand c){
		CommandHandler.getInstance().registerCommand(c);
	}
	
}
