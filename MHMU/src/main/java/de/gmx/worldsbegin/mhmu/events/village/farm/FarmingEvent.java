/**
 * 
 */
package de.gmx.worldsbegin.mhmu.events.village.farm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Cancelable;
import de.gmx.worldsbegin.mhmu.events.ILootingEvent;
import de.gmx.worldsbegin.mhmu.events.LootingTarget;

/**
 * @author Carbon
 * 
 * @version 0.0.1a_26.07.2013
 */
public abstract class FarmingEvent extends ILootingEvent {
	public class Post extends FarmingEvent {
		public Post(EntityPlayer player, LootingTarget type,
				ItemStack[] farmedItems) {
			super(player, type, farmedItems);
		}
	}

	@Cancelable
	public class Pre extends FarmingEvent {
		public Pre(EntityPlayer player, LootingTarget type,
				ItemStack[] farmedItems) {
			super(player, type, farmedItems);
		}
	}
	public ItemStack[] farmedItems;

	public FarmingEvent(EntityPlayer player, LootingTarget lootingTarget,
			ItemStack[] farmedItems) {
		super(player, lootingTarget);
		this.farmedItems = farmedItems;
	}

	@Override
	public ItemStack[] getLootedItems() {
		return this.farmedItems;
	}
}
