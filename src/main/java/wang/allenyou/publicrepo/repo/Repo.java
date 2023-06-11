package wang.allenyou.publicrepo.repo;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import wang.allenyou.publicrepo.exception.WrongRepoSizeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Repo implements ConfigurationSerializable {
	private String name = null;
	private String displayName = null;
	private ItemStack[] items;
	private final boolean enlarged;
	private final AtomicBoolean locked;

	public Repo(String pName, String pDisplayName, boolean pIsEnlarged, ItemStack[] pItems) {
		// Set Basic Information
		this.setName(pName);
		this.setDisplayName(pDisplayName);
		this.enlarged = pIsEnlarged;
		// Deal with pItems
		if (pItems == null) {
			// Initialize new array of ItemStack;
			if (pIsEnlarged) {
				items = new ItemStack[54];
			} else {
				items = new ItemStack[27];
			}
		} else {
			// Is it a Large Chest ?
			int expectedSize = pIsEnlarged ? 54 : 27;
			// If parameter don't have an expected size then throw an Exception
			if (pItems.length != expectedSize) {
				throw new WrongRepoSizeException();
			}
			// Copy pItems array to items
			items = pItems.clone();
		}
		// Deal with lock
		locked = new AtomicBoolean(false);
	}

	public Repo() {
		this("default", "Default repo", false, null);
	}

	public static Repo deserialize(Map<String, Object> args) {
		String name = (String) args.getOrDefault("name", "default");
		String displayName = (String) args.getOrDefault("displayname", "Default Repo");
		boolean enlarged = (boolean) args.getOrDefault("enlarged", false);
		List<ItemStack> lst = (ArrayList<ItemStack>) args.getOrDefault("items", null);
		ItemStack[] items = new ItemStack[enlarged ? 54 : 27];
		if (lst != null) {
			int size = lst.size();
			for (int index = 0; index < size; ++index) {
				if (lst.get(index) == null) continue;
				items[index] = lst.get(index);
			}
		}
		return new Repo(name, displayName, enlarged, items);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		// If name isn't null or empty and name contains character beyond of lower case character and number then do nothing
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isEnlarged() {
		return enlarged;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public void modifyItems(ItemStack[] pItems) {
		if (pItems == null) {
			return;
		}
		int expectedSize = this.isEnlarged() ? 54 : 27;
		if (pItems.length != expectedSize) throw new WrongRepoSizeException();
		this.items = pItems.clone();
	}

	public boolean lock() {
		return !locked.compareAndExchange(false, true);
	}

	public boolean unlock() {
		return locked.compareAndExchange(true, false);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> mp = new HashMap<>();
		mp.put("name", this.name);
		mp.put("displayname", this.displayName);
		mp.put("items", this.items);
		mp.put("enlarged", this.enlarged);
		return mp;
	}
}
