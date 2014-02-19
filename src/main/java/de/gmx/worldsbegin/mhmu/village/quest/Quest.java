package de.gmx.worldsbegin.mhmu.village.quest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

public class Quest implements IResourceManagerReloadListener {
	private static IResourceManager resManager;
	private static HashMap<UUID, Quest> generatedQuests;

	public static final Quest reloader = new Quest();

	static {
		generatedQuests = new HashMap<UUID, Quest>();
		resManager = Minecraft.getMinecraft().getResourceManager();
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void load() throws IOException {
		InputStream questSrcStream = null;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT) {
			try {
				questSrcStream = resManager.getResource(
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

	public static ActiveQuest questFromUUIDAndId(String string) {
		if (string == "")
			return null;
		try {
			UUID uuid = UUID.fromString(string.substring(0,
					string.indexOf("_") - 1));
			return generatedQuests.get(uuid)
					.getActiveQuestNbr(
							Integer.parseInt(string.substring(string
									.indexOf("_") + 1)));
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}

	protected UUID uuid;

	/** The region this quest takes place in. */
	private MHregions region;
	private ArrayList<ActiveQuest> activeInstances;

	private Quest() {
	} // For instance creation

	protected Quest(UUID uuid, HashMap<String, Object> config) {
		if (generatedQuests.containsKey(uuid))
			return;
		this.activeInstances = new ArrayList<ActiveQuest>(3);
		generatedQuests.put(uuid, this);
	}

	private ActiveQuest getActiveQuestNbr(int nbr) {
		return this.activeInstances.get(nbr);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		Quest.resManager = resourceManager;
		try {
			Quest.load();
		} catch (IOException e) {
			e.printStackTrace();
			generatedQuests = new HashMap<UUID, Quest>();
		}
	}

	public ActiveQuest requestNewActiveQuest() {
		int index = this.activeInstances.indexOf(null);
		index = index == -1 ? this.activeInstances.size() : index;
		ActiveQuest activeQuest = new ActiveQuest(this, index);
		this.activeInstances.set(index, activeQuest);
		return activeQuest;
	}
}
