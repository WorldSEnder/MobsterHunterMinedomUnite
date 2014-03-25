package de.gmx.worldsbegin.mhmu.entity;

import java.util.HashMap;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom.Rank;
import de.gmx.worldsbegin.mhmu.village.quest.ActiveQuest;

public class SpawnControl {
	/**
	 * This class is a usefull tool to set up some custom properties before a
	 * new entity spawns. They can then read from here and set themselfs up
	 * (several problems in their constructor). As you may know any entity
	 * features only a constructor with one arg of type {@link World}. You are
	 * normally unable to do something about finals you have declared and want
	 * to set up in the {@link EntityLiving#entityInit()} which is the new
	 * initEntity-method. You can't access any arguments there but this could
	 * have crucial results on the spawned entity. This class is thought as a
	 * helper to solve this. You put your args in here and read them out. <br>
	 * <br>
	 * Note: this class is used in
	 * {@link SpawnControl#spawnMonsterEntityForQuest(String, World, SpawnInformationSetup)}
	 * for example.
	 * 
	 * @author Carbon
	 * @version 0.0.1a_25.07.2013
	 */
	public static class ExtendedSpawnProperties {
		protected static ExtendedSpawnProperties instance = new ExtendedSpawnProperties();

		public ActiveQuest quest;
		public Rank rank;
		public float size;
		public byte brokenParts;
		public HashMap<String, Object> customProperties;

		protected ExtendedSpawnProperties() {
			this.quest = null;
			this.rank = Rank.LOWELDER;
			this.size = 1F;
			this.brokenParts = 0;
			this.customProperties = new HashMap<String, Object>(0);
		}

		/**
		 * Use this method to clear all data which means the entity will spawn
		 * "normally" without any extended properties (if you didn't tweak
		 * anything)
		 */
		public void reset() {
			this.quest = null;
			this.rank = Rank.LOWELDER;
			this.size = 1F;
			this.brokenParts = 0;
			this.customProperties.clear();
		}
	}

	public static interface SpawnInformationSetup {
		public void setupInformation(ExtendedSpawnProperties esp);
	}

	/**
	 * 
	 * @param id
	 *            the id of the monster to spawn. This is given back in
	 *            {@link #registerMobsterHunterMinedomEntity(Class, String, int, int, Render)}
	 * @param world
	 *            the world to spawn the monster in, normally the dimension of
	 *            MHMU
	 * @param rank
	 *            the rank of the monster. see {@link Rank} for more info
	 */
	public static Entity spawnMonsterEntityForQuest(String entityId,
			World world, SpawnInformationSetup setup) {
		try {
			setup.setupInformation(ExtendedSpawnProperties.instance);
			return EntityList.createEntityByName(entityId, world);
		} finally {
			ExtendedSpawnProperties.instance.reset();
		}
	}
}
