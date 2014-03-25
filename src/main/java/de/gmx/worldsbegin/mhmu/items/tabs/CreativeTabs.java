/**
 * 
 */
package de.gmx.worldsbegin.mhmu.items.tabs;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemSword;
import de.gmx.worldsbegin.mhmu.util.MOD_INFO;

/**
 * @author Carbon
 * 
 * @version 0.0.1a_02.08.2013
 */
public class CreativeTabs {
	public static final net.minecraft.creativetab.CreativeTabs ItemTab = new net.minecraft.creativetab.CreativeTabs(
			"Mobster Hunter Items") {
		@Override
		public Item getTabIconItem() {
			return new ItemFirework();
		}

		@Override
		public String getTranslatedTabLabel() {
			return "itemGroup." + MOD_INFO.modid + ".itemTab";
		}
	};
	public static final net.minecraft.creativetab.CreativeTabs WeaponTab = new net.minecraft.creativetab.CreativeTabs(
			"Mobster Hunter Weapons") {
		@Override
		public Item getTabIconItem() {
			return new ItemSword(ToolMaterial.IRON);
		}

		@Override
		public String getTranslatedTabLabel() {
			return "itemGroup." + MOD_INFO.modid + ".weaponTab";
		}
	};
	public static final net.minecraft.creativetab.CreativeTabs ArmorTab = new net.minecraft.creativetab.CreativeTabs(
			"Mobster Hunter Armor") {

		@Override
		public Item getTabIconItem() {
			return new ItemArmor(ArmorMaterial.IRON, 2, 1);
		}

		@Override
		public String getTranslatedTabLabel() {
			return "itemGroup." + MOD_INFO.modid + ".armorTab";
		}
	};
}
