package de.gmx.worldsbegin.mhmu;

import static de.gmx.worldsbegin.mhmu.util.MOD_INFO.fullname;
import static de.gmx.worldsbegin.mhmu.util.MOD_INFO.modid;
import static de.gmx.worldsbegin.mhmu.util.MOD_INFO.version;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.Side;
import de.gmx.worldsbegin.mhmu.client.gui.MHHunterHUDGUI;
import de.gmx.worldsbegin.mhmu.client.model.ModelFelyne;
import de.gmx.worldsbegin.mhmu.client.renderer.ItemModelManager;
import de.gmx.worldsbegin.mhmu.client.renderer.RenderMinedom;
import de.gmx.worldsbegin.mhmu.client.renderer.XXXRenderMHVillager;
import de.gmx.worldsbegin.mhmu.entity.EntityFelyne;
import de.gmx.worldsbegin.mhmu.entity.EntityMHVillager;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom.Rank;
import de.gmx.worldsbegin.mhmu.entity.helper.ExtendedSpawnProperties;
import de.gmx.worldsbegin.mhmu.items.MHRarity;
import de.gmx.worldsbegin.mhmu.items.weapons.MHSharpness;
import de.gmx.worldsbegin.mhmu.items.weapons.MHWeaponLongsword;
import de.gmx.worldsbegin.mhmu.network.HunterTracker;
import de.gmx.worldsbegin.mhmu.util.Strings;
import de.gmx.worldsbegin.mhmu.village.quest.ActiveQuest;
import de.gmx.worldsbegin.mhmu.village.quest.Quest;
import de.gmx.worldsbegin.mhmu.world.MonsterHunterWorld;

@Mod(modid = modid, name = fullname, version = version)
@MCVersion(value = "1.7.2")
@SuppressWarnings("static-method")
public class MobsterHunterMinedomUnite {
	private static Class<? extends EntityMinedom>[] registeredMonsters;
	private static Class<? extends EntityLiving>[] registeredLivings;

	@Instance
	private static MobsterHunterMinedomUnite instance;

	/**
	 * @return the instance
	 */
	public static MobsterHunterMinedomUnite instance() {
		return instance;
	}

	/**
	 * 
	 * @param id
	 *            the id of the monster to spawn. This is given back in
	 *            {@link #registerMobsterHunterMinedomEntity(Class, String, int, int, Render)}
	 * @param world
	 *            the world to spawn the monster in, normally the dimension of
	 *            MHMU
	 * @param rank
	 *            the rank of the monster. see {@link Rank} for more info
	 * @param quest
	 *            the quest to spawn this monster in
	 * @param size
	 *            an additional size of the monster. Can be any and is (mostly)
	 *            relative to default. Could be ignored
	 * @param brokenParts
	 *            maybe you want to spawn the monster with some broken parts
	 *            from beginning
	 * @param posX
	 * @param posY
	 * @param posZ
	 * @param additionalData
	 *            you can give any additional data if you want. This can be
	 *            accessed through
	 *            {@link ExtendedSpawnProperties#customProperties}
	 */
	public static void spawnMonsterEntityForQuest(int id, World world,
			Rank rank, ActiveQuest quest, float size, byte brokenParts,
			int posX, int posY, int posZ, Map<String, Object> additionalData) {
		if (world == null)
			return;
		if (rank == null) {
			rank = Rank.LOWELDER;
		}
		if (id >= 256 || id < 0)
			return;
		if (posY < 0) {
			posY = world.getHeightValue(posX, posZ);
		}
		ExtendedSpawnProperties props = ExtendedSpawnProperties.instance;
		props.quest = quest;
		props.rank = rank;
		props.size = size;
		props.brokenParts = brokenParts;
		props.customProperties.putAll(additionalData);
		Class<? extends EntityMinedom> entityClass = MobsterHunterMinedomUnite.registeredMonsters[id];
		try {
			EntityMinedom entity = entityClass.cast(entityClass.getConstructor(
					World.class).newInstance(world));
			world.spawnEntityInWorld(entity);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		props.reset();
	}

	private Side side;

	private Configuration config;

	private final Logger logger = FMLCommonHandler.instance().getFMLLogger();

	private HashMap<MHSharpness, Integer> testLS2SharpnessMapping;

	private MHWeaponLongsword testLongsword2;

	private HashMap<MHSharpness, Integer> testLS3SharpnessMapping;
	private MHWeaponLongsword testLongsword3;

	private HashMap<MHSharpness, Integer> testLS4SharpnessMapping;
	private MHWeaponLongsword testLongsword4;

	private HashMap<MHSharpness, Integer> testLS5SharpnessMapping;
	private MHWeaponLongsword testLongsword5;

	private HashMap<MHSharpness, Integer> testLS6SharpnessMapping;
	private MHWeaponLongsword testLongsword6;

	private int dimensionId;

	public MobsterHunterMinedomUnite() throws InstanceAlreadyExistsException {
		if (instance != null)
			throw new InstanceAlreadyExistsException(
					"Why would you try to create"
							+ " a second instance of MHMU??");
	}

	private int findUniqueEntityIdAndRegister(
			Class<? extends EntityMinedom> entity) throws IllegalStateException {
		int freeId;
		for (freeId = 0; freeId < registeredMonsters.length; freeId++) {
			if (registeredMonsters[freeId] == null) {
				break;
			}
		}
		if (freeId == 256)
			throw new IllegalStateException(
					"There are already 256 entities registered. More are not allowed");
		registeredMonsters[freeId] = entity;
		return freeId;
	}

	private int findUniqueLivingIdAndRegister(
			Class<? extends EntityLiving> entity) throws IllegalStateException {
		int freeId;
		for (freeId = 0; freeId < registeredLivings.length; freeId++) {
			if (registeredLivings[freeId] == null) {
				break;
			}
		}
		if (freeId == 256)
			throw new IllegalStateException(
					"There are already 256 entities registered. More are not allowed");
		registeredLivings[freeId] = entity;
		return freeId;
	}

	public Configuration getConfig() {
		return this.config;
	}

	/**
	 * @return the dimensionId
	 */
	public int getDimensionId() {
		return this.dimensionId;
	}

	/**
	 * 
	 * @param entityClass
	 * @return the local mod id (is shifted by 256) or -1 if the entityclass is
	 *         not registered yet
	 */
	public int getLocalLivingEntityId(Class<? extends EntityLiving> entityClass) {
		int i;
		for (i = registeredLivings.length - 1; i >= 0; i--) {
			if (entityClass == registeredLivings[i]) {
				break;
			}
		}
		if (i < 0)
			return -1;
		return i + 256;
	}

	/**
	 * 
	 * @param entityClass
	 * @return the local mod id or -1 if the entityclass is not registered yet
	 */
	public int getLocalMonsterEntityId(
			Class<? extends EntityMinedom> entityClass) {
		int i;
		for (i = registeredMonsters.length - 1; i >= 0; i--) {
			if (entityClass == registeredMonsters[i]) {
				break;
			}
		}
		return i;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * @return the side
	 */
	public Side getSide() {
		return this.side;
	}

	@EventHandler
	private void initMod(FMLInitializationEvent fie) {
		this.registerCustomSmelting();
		this.registerCustomCrafting();
		this.registerGUIStuff();

		if (this.side == Side.CLIENT) {
			this.registerClientOnly();
		} else {
			this.registerServerOnly();
		}

		this.config.save();
	}
	// Save actions
	@EventHandler
	private void onShutdown(FMLServerStoppingEvent fsse) {
		this.config.save();
	}

	// Communicate with other mods etc.
	@EventHandler
	private void postInit(FMLPostInitializationEvent fpostie) {
	}

	// Load config etc.
	@EventHandler
	private void preInitMod(FMLPreInitializationEvent fpreie) {
		System.out.println("OpenGL version: "
				+ GL11.glGetString(GL11.GL_VERSION));
		registeredMonsters = new Class[256];
		registeredLivings = new Class[256];

		this.side = FMLLaunchHandler.side();

		this.config = new Configuration(fpreie.getSuggestedConfigurationFile());
		this.config.load();

		this.registerListeners();
		this.registerBlocks();
		this.registerItems();
		this.registerCustomBiomesAndDimensions();
		this.registerCustomMobs();
	}

	private void registerBlocks() {
	}

	private void registerClientOnly() {
		// HUD
		FMLCommonHandler.instance().bus().register(MHHunterHUDGUI.instance());

		// Models
		((IReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager()).registerReloadListener(ItemModelManager
				.instance());
	}

	private void registerCustomBiomesAndDimensions() {
		this.dimensionId = DimensionManager.getNextFreeDimId();
		DimensionManager.registerProviderType(this.dimensionId,
				MonsterHunterWorld.class, false);
		DimensionManager.registerDimension(this.dimensionId, this.dimensionId);
	}

	private void registerCustomCrafting() {
		// TODO puh all them combinations them combinations... Str-C, Str-V
	}

	private void registerCustomMobs() {
		// Quests
		((IReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager()).registerReloadListener(Quest.reloader);

		try {
			this.registerMobsterHunterMinedomEntity(EntityFelyne.class,
					Strings.felyne_internal, 0xffffff, 0x000000,
					new RenderMinedom(new ModelFelyne()));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.registerMonsterHunterMinedomLiving(EntityMHVillager.class,
				Strings.armordude_internal, 0xff00ff, 0xff,
				new XXXRenderMHVillager(new ModelBiped(), 1F));
	}

	private void registerCustomSmelting() {
	}

	private void registerGUIStuff() {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MHGUIHandler());
	}

	private void registerItems() {
		// TODO all of them items... that are many :)
		// TODO them armory and weapons puha
		this.testLS2SharpnessMapping = new HashMap<MHSharpness, Integer>(0);
		this.testLS2SharpnessMapping.put(MHSharpness.RED, 10);
		this.testLS2SharpnessMapping.put(MHSharpness.ORANGE, 20);
		this.testLS2SharpnessMapping.put(MHSharpness.GREEN, 10);
		this.testLS2SharpnessMapping.put(MHSharpness.WHITE, 30);
		this.testLongsword2 = new MHWeaponLongsword(40,
				this.testLS2SharpnessMapping, MHRarity.TWO);
		this.testLongsword2.setUnlocalizedName("testlongsword2");
		GameRegistry.registerItem(this.testLongsword2, "testLongsword2");

		this.testLS3SharpnessMapping = new HashMap<MHSharpness, Integer>(0);
		this.testLS3SharpnessMapping.put(MHSharpness.RED, 10);
		this.testLS3SharpnessMapping.put(MHSharpness.ORANGE, 10);
		this.testLS3SharpnessMapping.put(MHSharpness.GREEN, 20);
		this.testLS3SharpnessMapping.put(MHSharpness.WHITE, 30);
		this.testLongsword3 = new MHWeaponLongsword(40,
				this.testLS3SharpnessMapping, MHRarity.THREE);
		this.testLongsword3.setUnlocalizedName("testlongsword3");
		GameRegistry.registerItem(this.testLongsword3, "testLongsword3");

		this.testLS4SharpnessMapping = new HashMap<MHSharpness, Integer>(0);
		this.testLS4SharpnessMapping.put(MHSharpness.RED, 20);
		this.testLS4SharpnessMapping.put(MHSharpness.ORANGE, 10);
		this.testLS4SharpnessMapping.put(MHSharpness.GREEN, 10);
		this.testLS4SharpnessMapping.put(MHSharpness.WHITE, 30);
		this.testLongsword4 = new MHWeaponLongsword(40,
				this.testLS4SharpnessMapping, MHRarity.FOUR);
		this.testLongsword4.setUnlocalizedName("testlongsword4");
		GameRegistry.registerItem(this.testLongsword4, "testLongsword4");

		this.testLS5SharpnessMapping = new HashMap<MHSharpness, Integer>(0);
		this.testLS5SharpnessMapping.put(MHSharpness.RED, 10);
		this.testLS5SharpnessMapping.put(MHSharpness.ORANGE, 10);
		this.testLS5SharpnessMapping.put(MHSharpness.GREEN, 10);
		this.testLS5SharpnessMapping.put(MHSharpness.WHITE, 30);
		this.testLongsword5 = new MHWeaponLongsword(40,
				this.testLS5SharpnessMapping, MHRarity.FIVE);
		this.testLongsword5.setUnlocalizedName("testlongsword5");
		GameRegistry.registerItem(this.testLongsword5, "testLongsword5");

		this.testLS6SharpnessMapping = new HashMap<MHSharpness, Integer>(0);
		this.testLS6SharpnessMapping.put(MHSharpness.RED, 10);
		this.testLS6SharpnessMapping.put(MHSharpness.ORANGE, 10);
		this.testLS6SharpnessMapping.put(MHSharpness.GREEN, 10);
		this.testLS6SharpnessMapping.put(MHSharpness.WHITE, 60);
		this.testLongsword6 = new MHWeaponLongsword(40,
				this.testLS6SharpnessMapping, MHRarity.SIX);
		this.testLongsword6.setUnlocalizedName("testlongsword6");
		GameRegistry.registerItem(this.testLongsword6, "testLongsword6");
	}

	private void registerListeners() {
		// Localization deprecated, gets loaded automatically
		// ((IReloadableResourceManager) Minecraft.getMinecraft()
		// .getResourceManager())
		// .registerReloadListener(LanguageLocalization.instance());

		// HunterTracker
		FMLCommonHandler.instance().bus().register(HunterTracker.instance());
	}

	/**
	 * <t>This is used to register an entity as a MHMU-moster. This is need as
	 * you can only spawn creatures using the static method
	 * {@link #spawnMonsterEntityForQuest(int, World, Rank, Quest, float, byte, int, int, int, Map)}
	 * by using the internal id handed back by this method. Also Quests only
	 * take those ids. <br>
	 * <br>
	 * A list of the default monster-ids can be found here: //TODO add
	 * hyper-link to monsterIds
	 * 
	 * @param entityClass
	 *            the entityclass
	 * @param name
	 *            the name of the entity to be used
	 * @param colorForeground
	 *            give -1 if you don't want spawn eggs.
	 * @param colorBackground
	 *            give -1 if you don't want spawn eggs.
	 * @return the two registeredIds. <br>
	 *         [0] - global id <br>
	 *         [1] - local id
	 */
	public <K extends EntityMinedom> int[] registerMobsterHunterMinedomEntity(
			Class<K> entityClass, String name, int colorForeground,
			int colorBackground, Render renderer) {

		if (entityClass == null)
			throw new IllegalArgumentException(
					"There needs to be a class given in oder to register one...!");

		int globalId = EntityRegistry.findGlobalUniqueEntityId();
		if (name == "" || name.isEmpty()) {
			name = "minedom" + globalId;
		}

		// global
		if (colorForeground >= 0 && colorBackground >= 0) {
			EntityRegistry.registerGlobalEntityID(entityClass, name, globalId,
					colorBackground, colorForeground);
		} else {
			EntityRegistry.registerGlobalEntityID(entityClass, name, globalId);
		}

		// local
		int localId = this.findUniqueEntityIdAndRegister(entityClass);
		EntityRegistry.registerModEntity(entityClass, name, localId, this, 128,
				3, true);
		// name them all
		LanguageRegistry.instance().addStringLocalization(
				"entity." + name + ".name", name);

		if (renderer != null && this.side == Side.CLIENT) {
			RenderingRegistry.registerEntityRenderingHandler(entityClass,
					renderer);
		}
		return new int[]{globalId, localId};
	}

	public <L extends EntityLiving> int[] registerMonsterHunterMinedomLiving(
			Class<L> entityClass, String name, int colorForeground,
			int colorBackground, Render renderer) {

		if (EntityMinedom.class.isAssignableFrom(entityClass))
			return this.registerMobsterHunterMinedomEntity(
					(Class<EntityMinedom>) entityClass, name, colorForeground,
					colorBackground, renderer);

		if (entityClass == null)
			throw new IllegalArgumentException(
					"There needs to be a class given in oder to register one...!");

		int globalId = EntityRegistry.findGlobalUniqueEntityId();
		if (name == "" || name.isEmpty()) {
			name = "minedom" + globalId;
		}

		// global
		if (colorForeground >= 0 && colorBackground >= 0) {
			EntityRegistry.registerGlobalEntityID(entityClass, name, globalId,
					colorBackground, colorForeground);
		} else {
			EntityRegistry.registerGlobalEntityID(entityClass, name, globalId);
		}

		// local
		int localId = this.findUniqueLivingIdAndRegister(entityClass);
		EntityRegistry.registerModEntity(entityClass, name, localId + 256,
				this, 128, 3, true);

		// name them all
		LanguageRegistry.instance().addStringLocalization(
				"entity." + name + ".name", name);

		if (renderer != null && this.side == Side.CLIENT) {
			RenderingRegistry.registerEntityRenderingHandler(entityClass,
					renderer);
		}
		return new int[]{globalId, localId};
	}

	private void registerServerOnly() {

	}
}