package de.gmx.worldsbegin.mhmu.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item.ToolMaterial;

public enum MHRarity {
	ONE(EnumRarity.common, 0xffffff, ToolMaterial.WOOD),
	TWO(EnumRarity.common, 0xffffff, ToolMaterial.WOOD),
	THREE(EnumRarity.common, 0xffffff, ToolMaterial.WOOD),
	FOUR(EnumRarity.uncommon, 0x5fe479, ToolMaterial.STONE),
	FIVE(EnumRarity.uncommon, 0xc47588, ToolMaterial.STONE),
	SIX(EnumRarity.rare, 0x8585f0, ToolMaterial.IRON),
	SEVEN(EnumRarity.rare, 0xf1b752, ToolMaterial.IRON),
	EIGHT(EnumRarity.rare, 0xea2b2b, ToolMaterial.IRON),
	NINE(EnumRarity.epic, 0xeee95b, ToolMaterial.EMERALD),
	TEN(EnumRarity.epic, 0xa722c5, ToolMaterial.EMERALD);
	
	/**
	 * The associated rarity for displaying the item-name
	 */
	public final EnumRarity enumRarity;
	public final int MHcolor;
	public final ToolMaterial toolMaterial;
	private MHRarity(EnumRarity rarity, int MHcolor, ToolMaterial toolMaterial)
	{
		this.enumRarity = rarity;
		this.MHcolor = MHcolor;
		this.toolMaterial = toolMaterial;
	}
}
