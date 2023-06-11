package wang.allenyou.publicrepo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import wang.allenyou.publicrepo.command.OpenRepoCommandHandler;
import wang.allenyou.publicrepo.repo.RepoManager;

public class PublicRepoPlugin extends JavaPlugin {
	public static PublicRepoPlugin instance = null;
	@Override
	public void onEnable() {
		instance = this;
		super.onEnable();
		saveDefaultConfig();
		// Initialize RepoManager
		RepoManager.init();
		RepoManager.loadConfig(this.getConfig());
		Bukkit.getPluginManager().registerEvents(RepoManager.getMonitor(), this);

		// Register Command /openrepo with command handler
		if (Bukkit.getPluginCommand("openrepo") != null) {
			Bukkit.getPluginCommand("openrepo").setExecutor(new OpenRepoCommandHandler());
		}
	}
	@Override
	public void onDisable() {
		super.onDisable();
		RepoManager.saveData(this.getConfig());
		saveConfig();
	}
}
