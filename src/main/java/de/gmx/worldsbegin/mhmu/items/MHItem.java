package de.gmx.worldsbegin.mhmu.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import de.gmx.worldsbegin.mhmu.client.model.exObj.MHModelObject;

public interface MHItem {
	/**
	 * This should return the rarity of this item. In some inventories this is used to
	 * sort the items.
	 * @param par1ItemStack makes this whole thing itemStack sensitve
	 * @return the {@link MHRarity} this item has.
	 */
	public MHRarity getMHRarity(ItemStack par1ItemStack);
	/**
	 * Should you decide to use the default MHItemRenderer than this should return an array of
	 * {@link ResourceLocation}s of all used {@link MHModelObject} which describes how the item
	 * is rendered.
	 * @return the {@link ResourceLocation}s of all the {@link MHModelObject}s used by this item
	 */
	public ResourceLocation[] getObjResourceLocationArray();
	/**
	 * Return the index of the {@link ResourceLocation} in the array given by {@link MHItem#getObjResourceLocationArray()}
	 * This method is {@link ItemStack} sensitive so make use of it.
	 */
	public int getObjResourceLocationIndex(ItemStack par1ItemStack);
}
