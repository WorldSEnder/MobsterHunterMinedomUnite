/**
 * 
 */
package de.gmx.worldsbegin.mhmu.events.village.farm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Cancelable;
import de.gmx.worldsbegin.mhmu.events.ILootingEvent;
import de.gmx.worldsbegin.mhmu.events.LootingSource;
import de.gmx.worldsbegin.mhmu.events.MHEvent;

/**
 * @author Carbon
 * 
 * @version 0.0.1a_26.07.2013
 */
public abstract class FarmingEvent extends MHEvent implements ILootingEvent {
	public class Post extends FarmingEvent {
		public Post(EntityPlayer player, LootingSource type,
				ItemStack[] farmedItems) {
			super(player, type, farmedItems);
		}
	}

	@Cancelable
	public class Pre extends FarmingEvent {
		public Pre(EntityPlayer player, LootingSource type,
				ItemStack[] farmedItems) {
			super(player, type, farmedItems);
		}
	}

	public final EntityPlayer player;
	public final LootingSource source;
	public ItemStack[] farmedItems;

	public FarmingEvent(EntityPlayer player, LootingSource type,
			ItemStack[] farmedItems) {
		super();
		this.player = player;
		this.source = type;
		this.farmedItems = farmedItems;
	}

	@Override
	public ItemStack[] getLootedItems() {
		return this.farmedItems;
	}

	@Override
	public EntityPlayer getLootingPlayer() {
		return this.player;
	}

	@Override
	public LootingSource getLootingSource() {
		return this.source;
	}
}
