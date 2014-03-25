package de.gmx.worldsbegin.mhmu.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.client.model.exObj.MHModelObject;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;

/**
 * @author Carbon
 * 
 */
@SideOnly(Side.CLIENT)
public abstract class ModelMinedom extends ModelBase {
	public float partialTicks;
	protected MHModelObject usedModel;

	public ModelMinedom(MHModelObject modelObject) {
		this.usedModel = modelObject;
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4,
			float par5, float par6, float par7) {
		GL11.glPushMatrix();
		// transform and rotate.
		if (par1Entity instanceof EntityMinedom) {
			EntityMinedom entityAsMinedom = (EntityMinedom) par1Entity;
			float size = entityAsMinedom.getDataWatcher()
					.getWatchableObjectFloat(EntityMinedom.DW_SIZE);
			GL11.glTranslatef(0F, 1.5F, 0F);
			GL11.glScalef(size, size, size);
			String[] renderGroups = entityAsMinedom.getRenderGroups();
			if (renderGroups == null) {
				this.usedModel.renderAll();
			} else {
				for (String s : renderGroups) {
					this.usedModel.renderPart(s);
				}
			}
		}
		GL11.glPopMatrix();
	}
}
