package de.gmx.worldsbegin.mhmu.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.entity.EntityMHVillager;
import de.gmx.worldsbegin.mhmu.inventory.containers.MHItemChestContainer;

@SideOnly(Side.CLIENT)
public class MHItemChestGUI extends GuiContainer {
	public MHItemChestGUI(EntityPlayer player, int invType,
			EntityMHVillager interactPerson, int page) {
		super(new MHItemChestContainer(player, invType, interactPerson, page));
		this.width = 200;
		this.height = 210;
	}

	@Override
	public void drawBackground(int mode) { // really mode? I don't know what to
											// do
		super.drawBackground(mode);
		this.fontRendererObj.drawString(
				((MHItemChestContainer) this.inventorySlots).getPageOn() + 1
						+ "", 20, 40, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		return;
	}

	@Override
	public void drawWorldBackground(int p_146270_1_) {
		return; // we don't need no background
	}

	@Override
	protected void keyTyped(char charTyped, int id) {
		if (id == 203) {
			((MHItemChestContainer) this.inventorySlots)
					.setPageOn((((MHItemChestContainer) this.inventorySlots)
							.getPageOn() + 9) % 10);
		}
		if (id == 205) {
			((MHItemChestContainer) this.inventorySlots)
					.setPageOn((((MHItemChestContainer) this.inventorySlots)
							.getPageOn() + 1) % 10);
		}
		super.keyTyped(charTyped, id);
	}
}
