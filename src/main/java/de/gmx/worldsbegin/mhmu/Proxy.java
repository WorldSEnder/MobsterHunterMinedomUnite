package de.gmx.worldsbegin.mhmu;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLiving;

public interface Proxy {

	public void registerEntityRenderer(
			Class<? extends EntityLiving> entityClass, Render render);
}
