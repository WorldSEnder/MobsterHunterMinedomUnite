package de.gmx.worldsbegin.mhmu.client.renderer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.client.model.exObj.MHModelObject;
import de.gmx.worldsbegin.mhmu.items.weapons.MHWeapon;

@SideOnly(Side.CLIENT)
public class IItemRendererMHWeapon implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED
				|| type == ItemRenderType.INVENTORY
				|| type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	// data: wenn type == EQUIPPED: data[0]: RenderBlocks instance
	// data[1]: EntityLiving playerToRender
	// INVENTORY: data[0]: RenderBlocks
	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack,
			Object... data) {
		Item item = itemStack.getItem();
		int color = 0xFFFFFF;
		if (item instanceof MHWeapon) {
			color = ((MHWeapon) item).weaponRarity.MHcolor;
		}
		GL11.glColor4f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F,
				(color & 0xFF) / 255F, 1F);
		if (type == ItemRenderType.EQUIPPED
				|| type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			MHModelObject toRender = ItemModelManager.instance()
					.getModelForItemStack(itemStack);
			if (toRender != null) {
				toRender.renderAll();
			} else {
				IIcon icon = item.getIcon(itemStack, 0);
				float f = icon.getMinU();
				float f1 = icon.getMaxU();
				float f2 = icon.getMinV();
				float f3 = icon.getMaxV();

				GL11.glRotatef(15F, 0F, 0F, 1F);

				GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(f, f2);
				GL11.glVertex3f(0F, 0F, 0F);
				GL11.glTexCoord2f(f, f3);
				GL11.glVertex3f(0F, 1F, 0F);
				GL11.glTexCoord2f(f1, f3);
				GL11.glVertex3f(1F, 1F, 0F);
				GL11.glTexCoord2f(f1, f2);
				GL11.glVertex3f(1F, 0F, 0F);
				GL11.glEnd();
			}
		} else if (type == ItemRenderType.INVENTORY) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			IIcon icon = item.getIcon(itemStack, 0);
			float f = icon.getMinU();
			float f1 = icon.getMaxU();
			float f2 = icon.getMinV();
			float f3 = icon.getMaxV();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(f, f2);
			GL11.glVertex3f(0F, 0F, 0F);
			GL11.glTexCoord2f(f, f3);
			GL11.glVertex3f(0F, 16F, 0F);
			GL11.glTexCoord2f(f1, f3);
			GL11.glVertex3f(16F, 16F, 0F);
			GL11.glTexCoord2f(f1, f2);
			GL11.glVertex3f(16F, 0F, 0F);
			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

}
