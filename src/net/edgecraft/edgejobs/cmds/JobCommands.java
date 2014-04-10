package net.edgecraft.edgejobs.cmds;

import net.edgecraft.edgecore.command.CommandHandler;
import net.edgecraft.edgejobs.jobs.Broker;
import net.edgecraft.edgejobs.jobs.Criminal;
import net.edgecraft.edgejobs.jobs.Doctor;
import net.edgecraft.edgejobs.jobs.Firefighter;
import net.edgecraft.edgejobs.jobs.Policeman;

public class JobCommands extends CommandHandler {

	private static final JobCommands instance = new JobCommands();
	
	private JobCommands() {
		super.registerCommand( JobCommand.getInstance() );
		super.registerCommand( Doctor.HealCommand.getInstance() );
		super.registerCommand( Firefighter.FireCommand.getInstance() );
		super.registerCommand( Criminal.CocaineCommand.getInstance() );
		super.registerCommand( Broker.HabitatUpgradeCommand.getInstance() );
		super.registerCommand( Policeman.ArrestCommand.getInstance() );
		super.registerCommand( Policeman.ReleaseCommand.getInstance() );
		super.registerCommand( Policeman.WantedCommand.getInstance() );
	}
	
	public static final JobCommands getInstance() {
		return instance;
	}
}
