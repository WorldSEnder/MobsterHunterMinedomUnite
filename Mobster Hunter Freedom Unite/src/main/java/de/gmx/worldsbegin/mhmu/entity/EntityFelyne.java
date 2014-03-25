package de.gmx.worldsbegin.mhmu.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityFelyne extends EntityMinedom {
	public EntityFelyne(World par1World) {
		super(par1World);
		this.boundingBox.setBounds(0, 0, 0, 0.5D, 1.525D, 0.5D);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setBaseValue(100.0D);
	}

	@Override
	protected void damageEntity(DamageSource par1DamageSource, float par2) {
		super.damageEntity(par1DamageSource, par2);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		float size = 0.25F + this.rand.nextFloat() * 1.5F;
		this.setSize(size);
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		return this.boundingBox;
	}

	/**
	 * @see de.gmx.worldsbegin.mhmu.entity.EntityMinedom#getDefaultSizeInMeter()
	 */
	@Override
	public float getDefaultSizeInMeter() {
		return 1F;
	}

	@Override
	protected Entity getLootingEntity() {
		return new EntityMinecartEmpty(this.worldObj);
	}

	@Override
	protected Entity postInitLootingEntity(Entity entity) {
		return entity;
	}

	@Override
	protected MonsterAttack[] setUpAttacks() {
		MonsterAttack attack1 = new MonsterAttack() {

			@Override
			public void execute() {

			}

			@Override
			public String getAnimationId() {
				return "testAni";
			}

			@Override
			public int getWeight() {
				return 10;
			}

			@Override
			public boolean isFinished() {
				return EntityFelyne.this.getDataWatcher()
						.getWatchableObjectInt(20) > 18;
			}

			@Override
			public void updateExecutionalProcess() {

			}
		};
		MonsterAttack attack2 = new MonsterAttack() {

			@Override
			public void execute() {

			}

			@Override
			public String getAnimationId() {
				return "testAni2";
			}

			@Override
			public int getWeight() {
				return 20;
			}

			@Override
			public boolean isFinished() {
				return EntityFelyne.this.getDataWatcher()
						.getWatchableObjectInt(20) > 35;
			}

			@Override
			public void updateExecutionalProcess() {

			}
		};
		return new MonsterAttack[]{attack1, attack2};
	}
}
