package de.gmx.worldsbegin.mhmu.client.renderer;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class XXXRenderMHVillager extends RenderBiped {

	public XXXRenderMHVillager(ModelBiped par1ModelBiped, float par2) {
		super(par1ModelBiped, par2);
	}

	public XXXRenderMHVillager(ModelBiped par1ModelBiped, float par2, float par3) {
		super(par1ModelBiped, par2, par3);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return new ResourceLocation("textures/entity/steve.png");
	}
}
