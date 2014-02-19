/**
 * 
 */
package de.gmx.worldsbegin.mhmu.entity.helper;

import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import de.gmx.worldsbegin.mhmu.MobsterHunterMinedomUnite;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom.Rank;
import de.gmx.worldsbegin.mhmu.village.quest.ActiveQuest;
import de.gmx.worldsbegin.mhmu.village.quest.Quest;

/**
 * This class is a usefull tool to set up some custom properties before a new entity
 * spawns. They can then read from here and set themselfs up (several problems in their
 * constructor). As you may know any entity features only a constructor with one arg
 * of type {@link World}. You are normally unable to do something about finals you have declared
 * and want to set up in the {@link EntityLiving#func_110147_ax()} which is the new
 * initEntity-method. You can't access any arguments there but this could have crucial
 * results on the spawned entity. This class is thought as an helper to solve this.
 * You put your args in here and read them out.
 * <br><br><b>WARNING!!!</b> Do not forget to call {@link ExtendedSpawnProperties#reset()} after
 * you are done.
 * <br><br>Note: this class is used in {@link MobsterHunterMinedomUnite#spawnMonsterEntityForQuest(int, World, Rank, Quest, int, int, int, int, String...)}
 * for example.
 * @author Carbon
 * @version 0.0.1a_25.07.2013
 */
public class ExtendedSpawnProperties {
	public final static ExtendedSpawnProperties instance = new ExtendedSpawnProperties();

	public ActiveQuest quest;
	public Rank rank;
	public float size;
	public byte brokenParts;
	public HashMap<String, Object> customProperties;

	protected ExtendedSpawnProperties() {
		this.quest = Quest.questFromUUIDAndId("");
		this.rank = Rank.LOWELDER;
		this.size = 1F;
		this.brokenParts = 0;
		this.customProperties = new HashMap<String, Object>(0);
	}

	/**
	 * Use this method to clear all data which means the entity will spawn "normally"
	 * without any extended properties (if you didn't tweak anything)
	 */
	public void reset()
	{
		this.quest = Quest.questFromUUIDAndId("");
		this.rank = Rank.LOWELDER;
		this.size = 1F;
		this.brokenParts = 0;
		this.customProperties.clear();
	}
}
