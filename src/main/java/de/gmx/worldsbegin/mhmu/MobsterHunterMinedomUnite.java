package de.gmx.worldsbegin.mhmu;

import static de.gmx.worldsbegin.mhmu.util.MOD_INFO.fullname;
import static de.gmx.worldsbegin.mhmu.util.MOD_INFO.modid;
import static de.gmx.worldsbegin.mhmu.util.MOD_INFO.version;

import javax.management.InstanceAlreadyExistsException;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.Side;
import de.gmx.worldsbegin.mhmu.items.weapons.MHWeaponLongsword;

@Mod(modid = modid, name = fullname, version = version)
@MCVersion(value = "1.7.2")
public class MobsterHunterMinedomUnite {
	public static class AdditionalInfo {
		public Configuration config = null;
		public Logger logger = null;

		public MHWeaponLongsword testLongsword2;
		public MHWeaponLongsword testLongsword3;
		public MHWeaponLongsword testLongsword4;
		public MHWeaponLongsword testLongsword5;
		public MHWeaponLongsword testLongsword6;

		public int dimensionId = 0;
	}

	@Instance
	private static MobsterHunterMinedomUnite instance;

	/**
	 * @return the instance
	 */
	public static MobsterHunterMinedomUnite instance() {
		return instance;
	}

	@SidedProxy(serverSide = "de.gmx.worldsbegin.mhmu.CommonProxy", clientSide = "de.gmx.worldsbegin.mhmu.client.ClientProxy")
	private CommonProxy sidedProxy;

	private AdditionalInfo additionalInfo;

	public MobsterHunterMinedomUnite() throws InstanceAlreadyExistsException {
		if (instance != null)
			throw new InstanceAlreadyExistsException(
					"Why would you try to create"
							+ " a second instance of MHMU??");
		this.additionalInfo = new AdditionalInfo();
	}

	public Configuration getConfig() {
		return this.additionalInfo.config;
	}

	/**
	 * @return the dimensionId
	 */
	public int getDimensionId() {
		return this.additionalInfo.dimensionId;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return this.additionalInfo.logger;
	}

	/**
	 * @return the side
	 */
	public Side getSide() {
		return this.sidedProxy.side;
	}

	@EventHandler
	private void initMod(FMLInitializationEvent fie) {
		this.sidedProxy.init(this.additionalInfo, fie);
		this.sidedProxy.initSided(this.additionalInfo, fie);
	}

	// Save actions
	@EventHandler
	private void onShutdown(FMLServerStoppingEvent fsse) {
		this.getConfig().save();
	}

	// Communicate with other mods etc.
	@EventHandler
	private void postInit(FMLPostInitializationEvent fpostie) {
		this.sidedProxy.postInit(this.additionalInfo, fpostie);
		this.sidedProxy.postInitSided(this.additionalInfo, fpostie);
	}

	// Load config etc.
	@EventHandler
	private void preInitMod(FMLPreInitializationEvent fpreie) {
		this.sidedProxy.preInit(this.additionalInfo, fpreie);
		this.sidedProxy.preInitSided(this.additionalInfo, fpreie);
	}
}