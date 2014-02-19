package de.gmx.worldsbegin.mhmu.events.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

public class SonicBombEvent extends WorldEvent {
	public class Exploded extends SonicBombEvent {
		public Exploded(World world, EntityPlayer player, double posX,
				double posY, double posZ) {
			super(world, player, posX, posY, posZ);
		}
	}

	@Cancelable
	public class Thrown extends SonicBombEvent {
		public Thrown(World world, EntityPlayer player, double posX,
				double posY, double posZ) {
			super(world, player, posX, posY, posZ);
		}
	}

	public double posX;
	public double posY;

	public double posZ;

	/**
	 * The player that has thrown the sonicbomb
	 */
	public EntityPlayer player;

	public SonicBombEvent(World world, EntityPlayer player, double posX,
			double posY, double posZ) {
		super(world);
		this.player = player;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}
}
