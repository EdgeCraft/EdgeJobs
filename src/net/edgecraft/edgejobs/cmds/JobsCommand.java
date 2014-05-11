package net.edgecraft.edgejobs.cmds;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgejobs.api.AbstractJob;
import net.edgecraft.edgejobs.api.JobManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobsCommand extends AbstractCommand {

	private static final AbstractCommand instance = new JobsCommand();

	@Override
	public Level getLevel() {
		return Level.USER;
	}

	@Override
	public String[] getNames() {
		return new String[] { "jobs" };
	}

	@Override
	public boolean runImpl(Player p, User user, String[] args) throws Exception {
		
		p.sendMessage(lang.getColoredMessage("de", "job_list_table"));
		
		for(AbstractJob job : JobManager.getInstance().getJobs())
			p.sendMessage(lang.getColoredMessage("de", "job_list").replaceAll("[0]", job.getName()));
		
		return true;
	}

	@Override
	public void sendUsageImpl(CommandSender sender) {
		sender.sendMessage(EdgeCore.errorColor + "/jobs");
	}

	@Override
	public boolean validArgsRange(String[] args) {
		return true;
	}

	public static AbstractCommand getInstance() {
		return instance;
	}

}
