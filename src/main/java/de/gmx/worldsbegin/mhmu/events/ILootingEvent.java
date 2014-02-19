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
public interface ILootingEvent {
	public ItemStack[] getLootedItems();
	public EntityPlayer getLootingPlayer();

	public LootingSource getLootingSource();
}
