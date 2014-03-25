/**
 * 
 */
package de.gmx.worldsbegin.mhmu.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import de.gmx.worldsbegin.mhmu.items.MHItem;
import de.gmx.worldsbegin.mhmu.items.armor.MHArmor;
import de.gmx.worldsbegin.mhmu.items.weapons.MHWeapon;

/**
 * @author Carbon
 * 
 * @version 0.0.1a_22.07.2013
 */
public class SlotMHItem extends Slot {
	private boolean isWeaponsSlot;

	public SlotMHItem(IInventory par1iInventory, int par2, int par3, int par4,
			boolean isWeaponsSlot) {
		super(par1iInventory, par2, par3, par4);
		this.isWeaponsSlot = isWeaponsSlot;
	}

	@Override
	public int getSlotStackLimit() {
		return this.isWeaponsSlot ? 1 : 99;
	}

	/**
	 * @see net.minecraft.inventory.Slot#isItemValid(net.minecraft.item.ItemStack)
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		Item item = itemstack.getItem();
		boolean weaponOrArmor = item instanceof MHWeapon
				|| item instanceof MHArmor;
		return this.isWeaponsSlot
				? weaponOrArmor
				: (item instanceof MHItem && !weaponOrArmor);
	}
}
