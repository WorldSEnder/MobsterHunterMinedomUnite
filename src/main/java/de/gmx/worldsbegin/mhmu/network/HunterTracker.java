/**
 * 
 */
package de.gmx.worldsbegin.mhmu.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import de.gmx.worldsbegin.mhmu.client.multiplayer.MHPlayerControllerMP;
import de.gmx.worldsbegin.mhmu.village.PlayerHunterStats;

/**
 * @author Carbon
 * 
 * @version 0.0.10a_10.01.2014
 */
public class HunterTracker {
	private static HunterTracker instance;

	public static HunterTracker instance() {
		if (instance == null) {
			instance = new HunterTracker();
		}
		return instance;
	}

	private HunterTracker() {
	}

	// CLIENT has connected, insert our own PlayerController for extended reach
	@SubscribeEvent
	public void clientConnectedClient(ClientConnectedToServerEvent cctse) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null) {
			PlayerControllerMP controller = mc.playerController;
			if (!(controller instanceof MHPlayerControllerMP)) {
				try {
					mc.playerController = new MHPlayerControllerMP(controller);
				} catch (SecurityException | IllegalArgumentException e) {
					// hush, I hope there is no security manager in the future
				}
			}
		}
	}

	// Player created, register properties
	@SubscribeEvent
	public void onPlayerCreation(EntityConstructing playerConstruct) {
		Entity constructed = playerConstruct.entity;
		if (constructed instanceof EntityPlayer) {
			String ident = PlayerHunterStats.identifier;
			if (ident != constructed.registerExtendedProperties(ident,
					new PlayerHunterStats((EntityPlayer) constructed)))
				throw new IllegalStateException(
						"Please do not register an extendedProperty for this string: "
								+ ident);
		}
	}

	// TODO onClientLoggedIntoWorld
}
