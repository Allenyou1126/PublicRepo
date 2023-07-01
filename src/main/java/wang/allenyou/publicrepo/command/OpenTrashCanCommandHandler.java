package wang.allenyou.publicrepo.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

public class OpenTrashCanCommandHandler implements CommandExecutor {
	@Override
	@ParametersAreNonnullByDefault
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (!(commandSender instanceof Player player)) return false;
		player.openInventory(Bukkit.createInventory(player, 27, "Trash Can"));
		return true;
	}
}
