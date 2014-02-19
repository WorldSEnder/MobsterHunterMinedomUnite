/**
 * 
 */
package de.gmx.worldsbegin.mhmu.events.entity;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;

/**
 * The problem with {@link EntityMinedom}s is that they "die" 2 times.
 * First time when they die they spawn a lootingEntity which then dies again.
 * Problem solved by introducing this event which only occurs when the real monster dies.
 * @author Carbon
 */
public class MinedomLivingDeathEvent extends LivingDeathEvent {

	/**
	 * @param entity
	 * @param source
	 */
	public MinedomLivingDeathEvent(EntityMinedom entity, DamageSource source) {
		super(entity, source);
	}
}
