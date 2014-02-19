package de.gmx.worldsbegin.mhmu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import de.gmx.worldsbegin.mhmu.client.gui.MHItemChestGUI;
import de.gmx.worldsbegin.mhmu.entity.EntityMHVillager;
import de.gmx.worldsbegin.mhmu.inventory.MHItemChestContainer;
;

/**
 * This is the MHGUIHandler.
 * Access the GUI by calling the static finals in this class
 * 			 2 - the GUI to display at the village-quester TODO all the GUIs
 * 			 3 - the GUI to display at Nekoth
 * 			 4 - the GUI to display
 * 			 .......
 * @author Carbon
 *
 */
public class MHGUIHandler implements IGuiHandler {
	public static final int WEAPON_CHEST = 0;
	public static final int ITEM_CHEST = 1;
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID)
		{
			case WEAPON_CHEST:
				if(!(world.getEntityByID(x) instanceof EntityMHVillager)) {
					return null;
				}
				return new MHItemChestGUI(player, MHItemChestContainer.WEAPON_INV, (EntityMHVillager) world.getEntityByID(x), y);
			case ITEM_CHEST:
				if(!(world.getEntityByID(x) instanceof EntityMHVillager)) {
					return null;
				}
				return new MHItemChestGUI(player, MHItemChestContainer.ITEM_INV, (EntityMHVillager) world.getEntityByID(x), y);
			default:
				return null;
		}
	}

	/**
	 * 
	 */
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID)
		{
			case WEAPON_CHEST:
				if(!(world.getEntityByID(x) instanceof EntityMHVillager)) {
					return null;
				}
				return new MHItemChestContainer(player, MHItemChestContainer.WEAPON_INV, (EntityMHVillager) world.getEntityByID(x), y);
			case ITEM_CHEST:
				if(!(world.getEntityByID(x) instanceof EntityMHVillager)) {
					return null;
				}
				return new MHItemChestContainer(player, MHItemChestContainer.ITEM_INV, (EntityMHVillager) world.getEntityByID(x), y);
			default:
				return null;
		}
	}

}
