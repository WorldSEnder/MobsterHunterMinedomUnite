package de.gmx.worldsbegin.mhmu.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import de.gmx.worldsbegin.mhmu.CommonProxy;
import de.gmx.worldsbegin.mhmu.MobsterHunterMinedomUnite.AdditionalInfo;
import de.gmx.worldsbegin.mhmu.client.gui.MHHunterHUDGUI;
import de.gmx.worldsbegin.mhmu.client.renderer.ItemModelManager;

public class ClientProxy extends CommonProxy {
	public Side side = Side.CLIENT;

	@Override
	public void initSided(AdditionalInfo info, FMLInitializationEvent fpostie) {
		// HUD
		FMLCommonHandler.instance().bus().register(MHHunterHUDGUI.instance());

		// Models
		((IReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager()).registerReloadListener(ItemModelManager
				.instance());
	}
}
