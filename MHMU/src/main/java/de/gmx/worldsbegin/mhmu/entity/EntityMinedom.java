package de.gmx.worldsbegin.mhmu.entity;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.gmx.worldsbegin.mhmu.entity.SpawnControl.ExtendedSpawnProperties;
import de.gmx.worldsbegin.mhmu.entity.ai.EntityAIMonsterHunterCommon;
import de.gmx.worldsbegin.mhmu.events.entity.MinedomLivingDeathEvent;
import de.gmx.worldsbegin.mhmu.events.world.FlashBombEvent;
import de.gmx.worldsbegin.mhmu.events.world.SonicBombEvent;
import de.gmx.worldsbegin.mhmu.village.PlayerHunterStats;
import de.gmx.worldsbegin.mhmu.village.quest.ActiveQuest;
import de.gmx.worldsbegin.mhmu.village.quest.Quest;
import de.gmx.worldsbegin.mhmu.village.quest.QuestProvider;

/**
 * Own Entities for Minedom should always extend this. An entityAI is
 * automatically added. The attacks are provided with
 * {@link EntityMinedom#setUpAttacks()}. Return <strong>null</strong> if you
 * don't want it to do anything.
 * 
 * this.dataWatcher[16] enragedTimeLeft;</br> this.dataWatcher[17]
 * flashedTimeLeft;</br> this.dataWatcher[18] sonicTimeLeft;</br>
 * this.dataWatcher[19] attackEnum;</br> this.dataWatcher[20]
 * currentAttackFrame;</br> this.dataWatcher[21] size;</br> this.dataWatcher[22]
 * rank;</br> this.dataWatcher[23] brokenParts;</br>
 * 
 * @author Carbon
 * 
 */
public abstract class EntityMinedom extends EntityLiving {
	// enum
	/**
	 * @author Carbon
	 */
	public enum Rank {
		LOWELDER, HIGHELDER, LOWRANK, HIGHRANK, GRANK;
	}

	public static final int DW_ENRAGED_TIME_LEFT = 16;
	public static final int DW_FLASHED_TIME_LEFT = 17;
	public static final int DW_SONIC_TIME_LEFT = 18;
	public static final int DW_ATTACK_ENUM = 19;
	public static final int DW_CURRENTATTACKFRAME = 20;
	public static final int DW_SIZE = 21;
	public static final int DW_RANK_ENUM = 22;
	public static final int DW_BROKENPARTS_AS_BYTE = 23;

	public static final int DW_QUEST_ID = 24;
	private float prevSize = 1F;
	/**
	 * Set this to something different than null and the next time the AI gets
	 * called this attack will be executed instantly;
	 */
	public MonsterAttack forcedAttack;
	/**
	 * <b>ALL</b> attacks that can be performed.
	 */
	public final MonsterAttack[] performableAttacks;

	/**
	 * 
	 */
	public EntityMinedom(World par1World) {
		this(par1World, Rank.LOWELDER, null);
	}

	/**
	 * @param par1World
	 *            - the world the entity spawns in
	 * @param rank
	 *            - the {@link Rank} this monster should have
	 * @param targetQuest
	 *            - the quest this monster has been spawned from if any. If the
	 *            monster is spawned in any other way this would be {@code null}
	 */
	public EntityMinedom(World par1World, Rank rank, Quest targetQuest) {
		super(par1World);
		this.performableAttacks = this.setUpAttacks();
		this.tasks.addTask(0, new EntityAIMonsterHunterCommon(this, this.rand));

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
		// TODO we need a method to attack the entity with elemental damage
		return super.attackEntityFrom(par1DamageSource, par2);
	}

	/**
	 * Damages the entity after it has been attacked with the given amount of
	 * attack value (par2) and by the given {@link DamageSource}
	 * (par1DamageSource)
	 * 
	 * @param par1DamageSource
	 *            the damage source
	 * @param par2
	 *            the attack value
	 */
	@Override
	protected void damageEntity(DamageSource par1DamageSource, float par2) {
		super.damageEntity(par1DamageSource, par2);// DEBUG shouldn't this be a
													// multipartentity?
	}

	@Override
	protected void despawnEntity() {
		if (this.getDataWatcher().getWatchableObjectInt(DW_QUEST_ID) < 0) {
			super.despawnEntity();
		}
	}

	@Override
	public void entityInit() {
		super.entityInit();
		ExtendedSpawnProperties exprops = ExtendedSpawnProperties.instance;
		// timeEnragedLeft
		this.dataWatcher.addObject(DW_ENRAGED_TIME_LEFT, Integer.valueOf(0));
		// durationFlashedLeft
		this.dataWatcher.addObject(DW_FLASHED_TIME_LEFT, Integer.valueOf(0));
		// durationSonicLeft (by sonic grenade e.g.)
		this.dataWatcher.addObject(DW_SONIC_TIME_LEFT, Integer.valueOf(0));
		// current Attack
		this.dataWatcher.addObject(DW_ATTACK_ENUM, Integer.valueOf(0));
		// current attackframe
		this.dataWatcher.addObject(DW_CURRENTATTACKFRAME, Integer.valueOf(0));
		// size of the mob
		this.dataWatcher.addObject(DW_SIZE, Float.valueOf(exprops.size));
		// the rank of the monster
		this.dataWatcher.addObject(DW_RANK_ENUM,
				Integer.valueOf(exprops.rank.ordinal()));
		// the broken Parts
		this.dataWatcher.addObject(DW_BROKENPARTS_AS_BYTE,
				Byte.valueOf(exprops.brokenParts));
		// currentQuest
		this.dataWatcher.addObject(DW_QUEST_ID, exprops.quest == null
				? -1
				: exprops.quest.activeID);
	}

	@Override
	public float getCollisionBorderSize() {
		return 0F;
	}

	public abstract float getDefaultSizeInMeter();

	/**
	 * Normally the looting entity features one animation(a dying-"attack") that
	 * is been played on dying and has the same model and renderer as the entity
	 * dying. The dying-"attack" should never end but an
	 * {@link Entity#setDead()} should be called sometime when the corpse is to
	 * be removed.
	 * 
	 * @return the entity to spawn upon the death of this one.
	 */
	protected abstract Entity getLootingEntity();

	/**
	 * Override this method to only render some parts of the entity. This is
	 * normally based on the current broken parts but can be used in other ways
	 * too. If you return null here, all groups are rendered. In every other
	 * case only the listed ones are.
	 */
	public String[] getRenderGroups() {
		return null;
	}

	@Override
	public float getShadowSize() {
		return this.dataWatcher.getWatchableObjectFloat(DW_SIZE);
	}

	/**
	 * Gets the Quest this entity is spawn in. This is not only used to ensure
	 * that the monster doesn't despawn but also to make a call to the quest
	 * when this monster dies.
	 */
	public ActiveQuest getTargetQuest() {
		return QuestProvider.getActiveQuestById(this.getDataWatcher()
				.getWatchableObjectInt(DW_QUEST_ID));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void knockBack(Entity par1Entity, float par2, double par3,
			double par5) {

	}

	@Override
	public void onDeath(DamageSource par1DamageSource) {
		if (ForgeHooks.onLivingDeath(this, par1DamageSource))
			return;

		Entity killer = par1DamageSource.getEntity();
		EntityLivingBase entityliving = this.func_94060_bK();

		if (this.scoreValue >= 0 && entityliving != null) {
			entityliving.addToPlayerScore(this, this.scoreValue);
		}

		if (killer != null) {
			killer.onKillEntity(this);
		}

		this.dead = true;
		{
			Entity lootingEntity = this.getLootingEntity();
			if (lootingEntity != null && !this.worldObj.isRemote) {
				if (lootingEntity instanceof EntityLiving) {
					EntityLiving toSpawn = (EntityLiving) lootingEntity;
					toSpawn.setLocationAndAngles(this.posX, this.posY,
							this.posZ, this.rotationYaw, this.rotationPitch);
					toSpawn.rotationYawHead = lootingEntity.rotationYaw;
					toSpawn.renderYawOffset = lootingEntity.rotationYaw;
					this.postInitLootingEntity(toSpawn);
					this.worldObj.spawnEntityInWorld(toSpawn);
				} else {
					lootingEntity.setLocationAndAngles(this.posX, this.posY,
							this.posZ, this.rotationYaw, this.rotationPitch);
					this.postInitLootingEntity(lootingEntity);
					this.worldObj.spawnEntityInWorld(lootingEntity);
				}
			}
		}
		ActiveQuest quest = this.getTargetQuest();
		if (quest != null) {
			quest.onDeathOf(this);
			EntityPlayer[] party = quest.party.getPartyMembers();
			for (int i = 0; i < party.length; i++) {
				EntityPlayer player = party[i];
				if (player != null) {
					String ident = PlayerHunterStats.identifier;
					((PlayerHunterStats) player.getExtendedProperties(ident))
							.addKillStat(this);
				}
			}
		} else {
			if (killer != null && killer instanceof EntityPlayer) {
				String ident = PlayerHunterStats.identifier;
				((PlayerHunterStats) killer.getExtendedProperties(ident))
						.addKillStat(this);
			}
		}
		MinecraftForge.EVENT_BUS.post(new MinedomLivingDeathEvent(this,
				par1DamageSource));
		this.worldObj.setEntityState(this, (byte) 3);
		this.setDead();
	}

	/**
	 * Called when a flash bomb explodes in the world. Check where it exploded
	 * and do your stuff.
	 */
	@SubscribeEvent(receiveCanceled = false)
	protected void onFlashBomb(FlashBombEvent fbe) {

	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.prevSize != this.dataWatcher.getWatchableObjectFloat(DW_SIZE)) {
			this.setSize(this.dataWatcher.getWatchableObjectFloat(DW_SIZE));
		}
		this.dataWatcher
				.updateObject(
						DW_ENRAGED_TIME_LEFT,
						this.dataWatcher
								.getWatchableObjectInt(DW_ENRAGED_TIME_LEFT) > 0
								? this.dataWatcher
										.getWatchableObjectInt(DW_ENRAGED_TIME_LEFT) - 1
								: 0);
		this.dataWatcher
				.updateObject(
						DW_FLASHED_TIME_LEFT,
						this.dataWatcher
								.getWatchableObjectInt(DW_FLASHED_TIME_LEFT) > 0
								? this.dataWatcher
										.getWatchableObjectInt(DW_FLASHED_TIME_LEFT) - 1
								: 0);
		this.dataWatcher.updateObject(
				DW_SONIC_TIME_LEFT,
				this.dataWatcher.getWatchableObjectInt(DW_SONIC_TIME_LEFT) > 0
						? this.dataWatcher
								.getWatchableObjectInt(DW_SONIC_TIME_LEFT) - 1
						: 0);
	}

	/**
	 * Called when a sonic bomb explodes in the world. Check where it exploded
	 * and stuff.
	 */
	@SubscribeEvent(receiveCanceled = false)
	protected void onSonicBomb(SonicBombEvent sbe) {

	}

	protected abstract Entity postInitLootingEntity(Entity entity);

	@Override
	public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readEntityFromNBT(par1nbtTagCompound);
		this.dataWatcher.updateObject(DW_ENRAGED_TIME_LEFT,
				par1nbtTagCompound.getInteger("enragedTimeLeft"));
		this.dataWatcher.updateObject(DW_FLASHED_TIME_LEFT,
				par1nbtTagCompound.getInteger("flashedTimeLeft"));
		this.dataWatcher.updateObject(DW_SONIC_TIME_LEFT,
				par1nbtTagCompound.getInteger("sonicTimeLeft"));
		this.dataWatcher.updateObject(DW_ATTACK_ENUM,
				par1nbtTagCompound.getInteger("attackEnum"));
		this.dataWatcher.updateObject(DW_CURRENTATTACKFRAME,
				par1nbtTagCompound.getInteger("currentAttackFrame"));
		this.dataWatcher.updateObject(DW_SIZE,
				par1nbtTagCompound.getFloat("size"));
		this.dataWatcher.updateObject(DW_RANK_ENUM,
				par1nbtTagCompound.getInteger("rank"));
		this.dataWatcher.updateObject(DW_BROKENPARTS_AS_BYTE,
				par1nbtTagCompound.getByte("brokenParts"));
		this.dataWatcher.updateObject(DW_QUEST_ID,
				par1nbtTagCompound.getInteger("questId"));
	}

	@Override
	public void setPosition(double par1, double par2, double par3) {
		AxisAlignedBB b = this.boundingBox;
		double boxSX = b.maxX - b.minX;
		double boxSY = b.maxY - b.minY;
		double boxSZ = b.maxZ - b.minZ;
		this.boundingBox.setBB(AxisAlignedBB.getAABBPool().getAABB(
				this.posX - boxSX / 2D, this.posY, this.posZ - boxSZ / 2D,
				this.posX + boxSX / 2D, this.posY + boxSY,
				this.posZ + boxSZ / 2D));
	}

	/**
	 * Sets the size in percent
	 */
	public void setSize(float f) {
		float scaling;
		if (this.prevSize == 0) {
			scaling = f;
		} else {
			scaling = f / this.prevSize;
		}
		double boxSX = this.boundingBox.maxX - this.boundingBox.minX;
		double boxSY = this.boundingBox.maxY - this.boundingBox.minY;
		double boxSZ = this.boundingBox.maxZ - this.boundingBox.minZ;
		boxSX *= scaling;
		boxSY *= scaling;
		boxSZ *= scaling;
		this.boundingBox.setBB(AxisAlignedBB.getAABBPool().getAABB(0, 0, 0,
				boxSX, boxSY, boxSZ));
		this.setPosition(this.posX, this.posY, this.posZ);
		this.dataWatcher.updateObject(DW_SIZE, f);
		this.prevSize = f;
	}

	/**
	 * Sets the quest this monster is a part from. For example after a quest has
	 * been returned all miniature-creatures should get a
	 * {@code setTargetQuest(null)} in order that they are added into the normal
	 * despawn queue. You COULD also set the targetQuest by a call to
	 * {@link DataWatcher#getWatchableObjectString(int)} and then delivering the
	 * value you get to {@link Quest#ququestFromUUIDing)} but this method is way
	 * more convenient.
	 * 
	 * @param targetQuest
	 */
	public void setTargetQuest(ActiveQuest quest) {
		this.dataWatcher.updateObject(DW_QUEST_ID, quest == null
				? -1
				: quest.activeID);
	}

	/**
	 * Override this!! Return the array of {@link MonsterAttack} you want this
	 * Monster to be able to perform. You should include an meta-attack in form
	 * of an default position. This "attack" should contain one frame and should
	 * also be always executable. In case you don't the monster could look
	 * really stupid/weird if no attack gets selected to be executed next by the
	 * AI.
	 */
	protected abstract MonsterAttack[] setUpAttacks();

	@Override
	public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeEntityToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setInteger("enragedTimeLeft",
				this.dataWatcher.getWatchableObjectInt(DW_ENRAGED_TIME_LEFT));
		par1nbtTagCompound.setInteger("flashedTimeLeft",
				this.dataWatcher.getWatchableObjectInt(DW_FLASHED_TIME_LEFT));
		par1nbtTagCompound.setInteger("sonicTimeLeft",
				this.dataWatcher.getWatchableObjectInt(DW_SONIC_TIME_LEFT));
		par1nbtTagCompound.setInteger("attackEnum",
				this.dataWatcher.getWatchableObjectInt(DW_ATTACK_ENUM));
		par1nbtTagCompound.setInteger("currentAttackFrame",
				this.dataWatcher.getWatchableObjectInt(DW_CURRENTATTACKFRAME));
		par1nbtTagCompound.setFloat("size",
				this.dataWatcher.getWatchableObjectFloat(DW_SIZE));
		par1nbtTagCompound.setInteger("rank",
				this.dataWatcher.getWatchableObjectInt(DW_RANK_ENUM));
		par1nbtTagCompound
				.setByte("brokenParts", this.dataWatcher
						.getWatchableObjectByte(DW_BROKENPARTS_AS_BYTE));
		par1nbtTagCompound.setInteger("questId",
				this.dataWatcher.getWatchableObjectInt(DW_QUEST_ID));
	}
}
