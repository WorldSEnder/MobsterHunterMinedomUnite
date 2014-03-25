package de.gmx.worldsbegin.mhmu.client.renderer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.client.model.ModelMinedom;

@SideOnly(Side.CLIENT)
public class RenderMinedom extends RenderLiving {
	ModelMinedom model;

	public RenderMinedom(ModelMinedom model) {
		super(model, 1F);
		this.model = model;
	}

	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1) {
		super.doRender(entity, d0, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return new ResourceLocation("textures/entity/steve.png"); // Doppelpunkt
																	// verwendbar
	}

	@Override
	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase,
			float par2) {
		this.model.partialTicks = par2;
	}
}
