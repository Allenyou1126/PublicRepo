package wang.allenyou.publicrepo.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wang.allenyou.publicrepo.repo.RepoManager;

import javax.annotation.ParametersAreNonnullByDefault;

public class OpenRepoCommandHandler implements CommandExecutor {

	@Override
	@ParametersAreNonnullByDefault
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (!(commandSender instanceof Player)) return false;
		if (!RepoManager.openRepo(((Player) commandSender).getPlayer())) {
			commandSender.sendMessage("There is someone occupying this repo...Please try again later.");
		}
		return true;
	}
}
