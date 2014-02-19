/**
 * 
 */
package de.gmx.worldsbegin.mhmu.village.quest;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;

/**
 * @author Carbon
 */
public class ActiveQuest {
	private Quest masterPlan;
	public final int activeID;

	protected ActiveQuest(Quest quest, int id)
	{
		this.masterPlan = quest;
		this.activeID = id;
	}

	public EntityPlayer[] getPartyMembers()
	{
		//TODO getPartyMembers()
		return new EntityPlayer[4];
	}

	public void onDeathOf(EntityMinedom entityMinedom)
	{
		//TODO onDeathof()
	}

	/**
	 * Returns a string following this convention:
	 * <br> Every {@link Quest} has an unique UUID.
	 * <br> Every {@link ActiveQuest} has an unique id.
	 * 
	 * @return {@link UUID#toString()} + "_" + {@link #activeID}
	 */
	@Override
	public String toString()
	{
		return this.masterPlan.uuid.toString() + "_" + this.activeID;
	}
}
