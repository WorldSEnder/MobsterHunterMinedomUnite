package de.gmx.worldsbegin.mhmu.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import de.gmx.worldsbegin.mhmu.MHGUIHandler;
import de.gmx.worldsbegin.mhmu.MobsterHunterMinedomUnite;

public class EntityMHVillager extends EntityLiving {

	public EntityMHVillager(World par1World) {
		super(par1World);
	}

	@Override
	public String getCommandSenderName() {
		return "$3" + this.getCustomNameTag();
	}

	@Override
	public boolean interact(EntityPlayer player) {
		player.openGui(MobsterHunterMinedomUnite.instance(),
				MHGUIHandler.WEAPON_CHEST, this.worldObj, this.getEntityId(),
				0, 0);
		return true;
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}
}
