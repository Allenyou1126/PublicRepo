package wang.allenyou.publicrepo.repo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class RepoManager {
	private static boolean initialized = false;
	private static Repo defaultRepo = null;

	private static InventoryMonitor monitor = null;
	public static InventoryMonitor getMonitor() {
		return monitor;
	}

	public static void init() {
		ConfigurationSerialization.registerClass(Repo.class);
		defaultRepo = new Repo();
		monitor = new InventoryMonitor();
		initialized = true;
	}

	public static void loadConfig(FileConfiguration conf) {
		Repo rep = (Repo) conf.get("repos.default");
		if (rep != null) {
			defaultRepo = rep;
		}
	}

	public static boolean isInitialized() {
		return initialized;
	}

	private static boolean openRepoIn(Repo re, Player player) {
		if (!re.lock()) return false;
		ItemStack[] items = re.getItems();
		int size = re.isEnlarged() ? 54 : 27;
		Inventory inv = Bukkit.createInventory(player, size, re.getDisplayName());
		for (int index = 0; index < size; ++index) {
			if (items[index] == null) {
				continue;
			}
			inv.setItem(index, items[index]);
		}
		class RepoRecall implements InventoryMonitor.Recall {
			private final Repo caller;

			public RepoRecall(Repo pCaller) {
				this.caller = pCaller;
			}

			@Override
			public void recall(Inventory in) {
				int size = caller.isEnlarged() ? 54 : 27;
				ItemStack[] newItems = new ItemStack[size];
				for (int index = 0; index < size; ++index) {
					newItems[index] = in.getItem(index);
				}
				caller.modifyItems(newItems);
				caller.unlock();
			}
		}
		monitor.add(re.getDisplayName(), new RepoRecall(re));
		player.openInventory(inv);
		return true;
	}

	public static class InventoryMonitor implements Listener {
		public interface Recall {
			void recall(Inventory in);

		}

		private final Map<String, Recall> mp;

		public InventoryMonitor() {
			mp = new HashMap<>();
		}

		public void add(String name, Recall recall) {
			if (mp.containsKey(name)) mp.replace(name, recall);
			else mp.put(name, recall);
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent e) {
			Player player = (Player) e.getPlayer();
			InventoryView inv = player.getOpenInventory();
			Recall recall = mp.getOrDefault(inv.getTitle(), null);
			if (recall != null) {
				mp.remove(inv.getTitle());
				recall.recall(inv.getTopInventory());
			}
		}
	}

	public static boolean openRepo(Player player) {
		if (!isInitialized()) return false;
		return openRepoIn(defaultRepo, player);
	}

	public static void saveData(FileConfiguration conf) {
		String name = "repos." + defaultRepo.getName();
		conf.set(name, defaultRepo);
	}
}
