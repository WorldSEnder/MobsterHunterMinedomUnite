package de.gmx.worldsbegin.mhmu.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryMHItemChest extends InventoryBasic {
	public static InventoryMHItemChest getInvFromNBTTagCompound(
			NBTTagCompound toReadFrom) {
		InventoryMHItemChest returnInv = new InventoryMHItemChest(false);
		returnInv.readFromNBTTagCompound(toReadFrom);
		return returnInv;
	}

	public EntityPlayer thePlayer = null;
	private boolean holdsWeapons;

	public InventoryMHItemChest(boolean holdsWeapons) {
		super("container.MHItems", false, 1000);
		this.holdsWeapons = holdsWeapons;
	}

	@Override
	public void closeInventory() {
		super.closeInventory();
	}

	@Override
	public int getInventoryStackLimit() {
		return 99;
	}

	@Override
	// Is not been called at all... normally.
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// return itemstack == null || itemstack.getItem() instanceof MHItem
		// && !this.holdsWeapons
		// || itemstack.getItem() instanceof MHWeapon
		// || itemstack.getItem() instanceof MHArmor && this.holdsWeapons;

		return false; // No Block after all... so there is no hopper that can
						// push any items into the inventory. For any other
						// possible operation we return false
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return !entityplayer.isDead;
	}

	public void readFromNBTTagCompound(NBTTagCompound nbtTagToReadFrom) {
		this.holdsWeapons = nbtTagToReadFrom.getBoolean("isWeaponChest");
		NBTTagList invList = nbtTagToReadFrom.getTagList("inventory", 10);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound itemStackCompound = invList.getCompoundTagAt(i);
			if (itemStackCompound instanceof NBTTagCompound) {
				int slot = itemStackCompound.getInteger("idInInv");
				if (slot < 0 || slot >= this.getSizeInventory()) {
					continue;
				}
				this.setInventorySlotContents(slot,
						ItemStack.loadItemStackFromNBT(itemStackCompound));
			}
		}
	}

	public void writeToNBTTagCompound(NBTTagCompound nbtTagToWriteTo) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (this.getStackInSlot(i) == null) {
				continue;
			}
			NBTTagCompound itemStackCompound = new NBTTagCompound();
			this.getStackInSlot(i).writeToNBT(itemStackCompound);
			itemStackCompound.setInteger("idInInv", i);
			itemList.appendTag(itemStackCompound);
		}
		nbtTagToWriteTo.setTag("inventory", itemList);
		nbtTagToWriteTo.setBoolean("isWeaponChest", this.holdsWeapons);
	}
}
