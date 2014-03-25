package de.gmx.worldsbegin.mhmu.village.quest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import de.gmx.worldsbegin.mhmu.util.MOD_INFO;
import de.gmx.worldsbegin.mhmu.util.reader.ConfigObject;
import de.gmx.worldsbegin.mhmu.util.reader.ConfigObject.EndOfMapException;

public class QuestProvider implements IResourceManagerReloadListener {
	public static final QuestProvider instance;

	static {
		instance = new QuestProvider();
		try {
			instance.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method doesn't throw on invalid numbers,
	 * {@link #getActiveQuestFromId(int)} does.
	 * 
	 * @param id
	 *            - the id of the requested id
	 * @return the {@link ActiveQuest} associated with the given id
	 */
	public static ActiveQuest getActiveQuestById(int id) {
		if (id < 0)
			return null;
		try {
			return instance.getActiveQuestFromId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public static ActiveQuest requestNewActiveQuest(Quest quest) {
		return instance.newActiveQuest(quest);
	}

	private IResourceManager resManager;
	private HashMap<UUID, Quest> generatedQuests;
	private HashMap<Quest, List<ActiveQuest>> activeInstances;
	private List<ActiveQuest> allQuests;

	private int nextId;

	protected QuestProvider() {
		this.generatedQuests = new HashMap<UUID, Quest>();
		this.resManager = Minecraft.getMinecraft().getResourceManager();
		this.nextId = 0;
	}

	public ActiveQuest getActiveQuestFromId(int id) {
		return this.allQuests.get(id);
	}

	private void load() throws IOException {
		InputStream questSrcStream = null;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT) {
			try {
				questSrcStream = this.resManager.getResource(
						new ResourceLocation("mhmu:config/quests.yml"))
						.getInputStream();
			} catch (Exception e) {

			}
		} else {
			try {
				questSrcStream = Quest.class.getResource(
						"/assets/mhmu/config/quests.yml").openStream();
			} catch (IOException e) {

			}
		}
		if (questSrcStream != null) {
			ConfigObject questReader;

			try {
				questReader = new ConfigObject(new BufferedReader(
						new InputStreamReader(questSrcStream)));
			} catch (EndOfMapException e) {
				questReader = e.lastElement;
			}

			Map<String, ConfigObject> questsAsObjectList = questReader.getMap();

			int i = 0;
			for (String key : questsAsObjectList.keySet()) {
				++i;
				ConfigObject questAsObject = questsAsObjectList.get(key);

				// If it's a map then it maps Strings to objects
				Map<String, ConfigObject> questMap = questAsObject.getMap();
				try {
					if (!questMap.containsKey("uuid"))
						throw new IllegalArgumentException("uuid");
				} catch (IllegalArgumentException iae) {
					if (iae.getMessage() == "uuid") {
						FMLLog.getLogger()
								.info(MOD_INFO.modid,
										Level.SEVERE,
										"Quest nbr. "
												+ "%d doesn't contain UUID. Ignoring it.",
										i);
					}
				}
			}
			questSrcStream.close();
		}
	}

	public ActiveQuest newActiveQuest(Quest quest) {
		List<ActiveQuest> activeList = this.activeInstances.get(quest);
		if (activeList == null) {
			activeList = new ArrayList<ActiveQuest>();
			this.activeInstances.put(quest, activeList);
		}
		ActiveQuest activeQuest = new ActiveQuest(quest, this.nextId);
		this.nextId++;
		activeList.add(activeQuest);
		return activeQuest;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.resManager = resourceManager;
		try {
			this.load();
		} catch (IOException e) {
			e.printStackTrace();
			this.generatedQuests.clear();
			Iterator<Entry<Quest, List<ActiveQuest>>> iter = this.activeInstances
					.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Quest, List<ActiveQuest>> activeEntry = iter.next();
				for (ActiveQuest activeQuest : activeEntry.getValue()) {
					activeQuest.forceTerminate();
				}
				iter.remove();
			}
			// this.activeInstances.clear();
		}
	}
}
