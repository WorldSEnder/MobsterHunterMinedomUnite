/**
 * 
 */
package de.gmx.worldsbegin.mhmu.client.multiplayer;

import java.lang.reflect.Field;

import net.minecraft.client.multiplayer.PlayerControllerMP;

/**
 * @author Carbon
 * 
 * @version 0.0.4a_12.08.2013
 */
// distance
public class MHPlayerControllerMP extends PlayerControllerMP {
	private float reachDistance = 5.0F;

	/**
	 * @param par1Minecraft
	 * @param par2NetClientHandler
	 */
	public MHPlayerControllerMP(PlayerControllerMP pcMP) {
		super(null, null);
		// Is null ?? wtf
		this.copyFrom(pcMP);
	}

	protected void copyFrom(PlayerControllerMP pcMP) {
		for (Field field : PlayerControllerMP.class.getDeclaredFields()) {
			try {
				boolean accessible = field.isAccessible();
				if (!accessible) {
					field.setAccessible(true);
				}
				Object fieldValue = null;
				try {
					fieldValue = field.get(pcMP);
				} catch (NullPointerException npe) { // If content of field is
														// null
					// fieldValue = null;
				}
				field.set(this, fieldValue);
				field.setAccessible(accessible);
			} catch (SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
				// hush, I hope not, no security manager :) yeah
			}
		}
	}

	@Override
	public float getBlockReachDistance() {
		return this.isInCreativeMode()
				? this.reachDistance
				: this.reachDistance * 0.9F;
	}

	public void setReachDistance(float par1) {
		this.reachDistance = par1;
	}
}
