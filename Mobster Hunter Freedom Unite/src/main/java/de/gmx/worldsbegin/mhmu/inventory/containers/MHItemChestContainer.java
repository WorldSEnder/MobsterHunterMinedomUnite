package de.gmx.worldsbegin.mhmu.inventory.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import de.gmx.worldsbegin.mhmu.entity.EntityMHVillager;
import de.gmx.worldsbegin.mhmu.inventory.InventoryMHItemChest;
import de.gmx.worldsbegin.mhmu.inventory.slots.SlotMHItem;
import de.gmx.worldsbegin.mhmu.village.PlayerHunterStats;

public class MHItemChestContainer extends Container {
	public static final int WEAPON_INV = 0;
	public static final int ITEM_INV = 1;

	private EntityMHVillager interactPerson;
	private Slot[] slots;
	private int currentPage;

	public MHItemChestContainer(EntityPlayer thePlayer, int invType,
			EntityMHVillager interactPerson, int page) {
		this.interactPerson = interactPerson;
		this.slots = new Slot[1000];
		String ident = PlayerHunterStats.identifier;
		PlayerHunterStats toBind = (PlayerHunterStats) thePlayer
				.getExtendedProperties(ident);
		if (invType == WEAPON_INV) {
			this.bindPlayerMHItemInv(toBind.getArmoryInventory(), true);
		} else if (invType == ITEM_INV) {
			this.bindPlayerMHItemInv(toBind.getItemInventory(), false);
		} else
			throw new IllegalArgumentException(
					"Select one of the static final variables in this class and nothing else.");
		this.bindPlayerMHItemInv(thePlayer.inventory, false);
		this.setPageOn(page);
	}

	public void bindPlayerMHItemInv(IInventory inv, boolean isWeaponInv) {
		if (inv instanceof InventoryMHItemChest) {
			for (int pageOn = 0; pageOn < 10; pageOn++) {
				for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 10; j++) {
						this.slots[100 * pageOn + 10 * i + j] = this
								.addSlotToContainer(new SlotMHItem(inv, pageOn
										* 100 + 10 * i + j, pageOn * 1000 + 17
										+ j * 20, 0 + i * 18, isWeaponInv));
					}
				}
			}
			this.currentPage = 0;
		} else if (inv instanceof InventoryPlayer) {
			for (int i = 0; i < 9; i++) {
				this.addSlotToContainer(new Slot(inv, i, i * 20 + 17, 184));
			}
		}
	}

	/**
	 * @see net.minecraft.inventory.Container#canDragIntoSlot(net.minecraft.inventory.Slot)
	 */
	@Override
	public boolean canDragIntoSlot(Slot par1Slot) {
		return false;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return entityplayer.getDistanceSq(this.interactPerson.posX,
				this.interactPerson.posY, this.interactPerson.posZ) < 64;
	}

	public int getPageOn() {
		return this.currentPage;
	}

	public void setPageOn(int page) {
		page %= 10;
		for (int i = 0; i < 1000; i++) {
			this.slots[i].xDisplayPosition = (i / 100 - page) * 1000 + 17 + i
					% 10 * 20;
		}
		this.currentPage = page;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 < 1000) {
				if (!this.mergeItemStack(itemstack1, 1000,
						this.inventorySlots.size(), false))
					return null;
			} else if (!this.slots[0].isItemValid(itemstack)
					|| !this.mergeItemStack(itemstack1, 0, 1000, false))
				return null;

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}
}
