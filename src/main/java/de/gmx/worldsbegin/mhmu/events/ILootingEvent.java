/**
 * 
 */
package de.gmx.worldsbegin.mhmu.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author Carbon
 * 
 * @version 0.0.1a_26.07.2013
 */
public abstract class ILootingEvent<T> extends MHEvent {
	public static ILootingEvent<?> getLootingEventFor(Object object) {
		if (object == null)
			return null;
		return null;
	}

	public final LootingTarget<T> target;

	public final EntityPlayer lootingPlayer;

	public ILootingEvent(EntityPlayer lootingPlayer, LootingTarget<T> target) {
		this.target = target;
		this.lootingPlayer = lootingPlayer;
	}

	public abstract ItemStack[] getLootedItems();
}
