package de.gmx.worldsbegin.mhmu;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import de.gmx.worldsbegin.mhmu.MobsterHunterMinedomUnite.AdditionalInfo;
import de.gmx.worldsbegin.mhmu.items.MHRarity;
import de.gmx.worldsbegin.mhmu.items.weapons.MHSharpness;
import de.gmx.worldsbegin.mhmu.items.weapons.MHWeaponLongsword;
import de.gmx.worldsbegin.mhmu.network.HunterTracker;
import de.gmx.worldsbegin.mhmu.village.quest.QuestProvider;
import de.gmx.worldsbegin.mhmu.world.MonsterHunterWorld;

@SuppressWarnings("static-method")
public class CommonProxy {
	private static void registerBlocks(AdditionalInfo info) {

	}

	private static void registerCustomBiomesAndDimensions(AdditionalInfo info) {
		int dimensionId = DimensionManager.getNextFreeDimId();

		DimensionManager.registerProviderType(dimensionId,
				MonsterHunterWorld.class, false);
		DimensionManager.registerDimension(dimensionId, dimensionId);
	}

	private static void registerCustomCrafting(AdditionalInfo info) {
		// TODO puh all them combinations them combinations... Str-C, Str-V
	}

	private static void registerCustomMobs(AdditionalInfo info) {
		// Quests
		((IReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager())
				.registerReloadListener(QuestProvider.instance);
		// try {
		// TODO this.registerMobsterHunterMinedomEntity(EntityFelyne.class,
		// Strings.felyne_internal, 0xffffff, 0x000000,
		// new RenderMinedom(new ModelFelyne()));
		// } catch (ModelFormatException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// this.registerMonsterHunterMinedomLiving(EntityMHVillager.class,
		// Strings.armordude_internal, 0xff00ff, 0xff,
		// new XXXRenderMHVillager(new ModelBiped(), 1F));
	}

	private static void registerCustomSmelting(AdditionalInfo info) {

	}

	private static void registerGUIStuff(AdditionalInfo info) {
		NetworkRegistry.INSTANCE.registerGuiHandler(
				MobsterHunterMinedomUnite.instance(), new MHGUIHandler());
	}

	private static void registerItems(AdditionalInfo info) {
		// TODO all of them items... that are many :)
		// TODO them armory and weapons puha
		HashMap<MHSharpness, Integer> testLS2SharpnessMapping = new HashMap<MHSharpness, Integer>(
				0);
		testLS2SharpnessMapping.put(MHSharpness.RED, 10);
		testLS2SharpnessMapping.put(MHSharpness.ORANGE, 20);
		testLS2SharpnessMapping.put(MHSharpness.GREEN, 10);
		testLS2SharpnessMapping.put(MHSharpness.WHITE, 30);
		MHWeaponLongsword testLongsword2 = new MHWeaponLongsword(40,
				testLS2SharpnessMapping, MHRarity.TWO);
		testLongsword2.setUnlocalizedName("testlongsword2");
		GameRegistry.registerItem(testLongsword2, "testLongsword2");

		HashMap<MHSharpness, Integer> testLS3SharpnessMapping = new HashMap<MHSharpness, Integer>(
				0);
		testLS3SharpnessMapping.put(MHSharpness.RED, 10);
		testLS3SharpnessMapping.put(MHSharpness.ORANGE, 10);
		testLS3SharpnessMapping.put(MHSharpness.GREEN, 20);
		testLS3SharpnessMapping.put(MHSharpness.WHITE, 30);
		info.testLongsword3 = new MHWeaponLongsword(40,
				testLS3SharpnessMapping, MHRarity.THREE);
		info.testLongsword3.setUnlocalizedName("testlongsword3");
		GameRegistry.registerItem(info.testLongsword3, "testLongsword3");

		HashMap<MHSharpness, Integer> testLS4SharpnessMapping = new HashMap<MHSharpness, Integer>(
				0);
		testLS4SharpnessMapping.put(MHSharpness.RED, 20);
		testLS4SharpnessMapping.put(MHSharpness.ORANGE, 10);
		testLS4SharpnessMapping.put(MHSharpness.GREEN, 10);
		testLS4SharpnessMapping.put(MHSharpness.WHITE, 30);
		info.testLongsword4 = new MHWeaponLongsword(40,
				testLS4SharpnessMapping, MHRarity.FOUR);
		info.testLongsword4.setUnlocalizedName("testlongsword4");
		GameRegistry.registerItem(info.testLongsword4, "testLongsword4");

		HashMap<MHSharpness, Integer> testLS5SharpnessMapping = new HashMap<MHSharpness, Integer>(
				0);
		testLS5SharpnessMapping.put(MHSharpness.RED, 10);
		testLS5SharpnessMapping.put(MHSharpness.ORANGE, 10);
		testLS5SharpnessMapping.put(MHSharpness.GREEN, 10);
		testLS5SharpnessMapping.put(MHSharpness.WHITE, 30);
		info.testLongsword5 = new MHWeaponLongsword(40,
				testLS5SharpnessMapping, MHRarity.FIVE);
		info.testLongsword5.setUnlocalizedName("testlongsword5");
		GameRegistry.registerItem(info.testLongsword5, "testLongsword5");

		HashMap<MHSharpness, Integer> testLS6SharpnessMapping = new HashMap<MHSharpness, Integer>(
				0);
		testLS6SharpnessMapping.put(MHSharpness.RED, 10);
		testLS6SharpnessMapping.put(MHSharpness.ORANGE, 10);
		testLS6SharpnessMapping.put(MHSharpness.GREEN, 10);
		testLS6SharpnessMapping.put(MHSharpness.WHITE, 60);
		info.testLongsword6 = new MHWeaponLongsword(40,
				testLS6SharpnessMapping, MHRarity.SIX);
		info.testLongsword6.setUnlocalizedName("testlongsword6");
		GameRegistry.registerItem(info.testLongsword6, "testLongsword6");
	}

	private static void registerListeners(AdditionalInfo info) {
		// Localization deprecated, gets loaded automatically
		// ((IReloadableResourceManager) Minecraft.getMinecraft()
		// .getResourceManager())
		// .registerReloadListener(LanguageLocalization.instance());

		// HunterTracker
		FMLCommonHandler.instance().bus().register(HunterTracker.instance());
	}

	public Side side = Side.SERVER;

	public final void init(AdditionalInfo info, FMLInitializationEvent fie) {
		CommonProxy.registerCustomSmelting(info);
		CommonProxy.registerCustomCrafting(info);
		CommonProxy.registerGUIStuff(info);
	}

	public void initSided(AdditionalInfo info, FMLInitializationEvent fpostie) {

	}

	public final void postInit(AdditionalInfo info,
			FMLPostInitializationEvent fpostie) {

	}

	public void postInitSided(AdditionalInfo info,
			FMLPostInitializationEvent fpostie) {

	}

	public final void preInit(AdditionalInfo info,
			FMLPreInitializationEvent fpreie) {
		info.config = new Configuration(fpreie.getSuggestedConfigurationFile());
		info.config.load();

		CommonProxy.registerListeners(info);
		CommonProxy.registerBlocks(info);
		CommonProxy.registerItems(info);
		CommonProxy.registerCustomBiomesAndDimensions(info);
		CommonProxy.registerCustomMobs(info);
	}

	public void preInitSided(
			de.gmx.worldsbegin.mhmu.MobsterHunterMinedomUnite.AdditionalInfo info,
			FMLPreInitializationEvent fpreie) {
	}
}