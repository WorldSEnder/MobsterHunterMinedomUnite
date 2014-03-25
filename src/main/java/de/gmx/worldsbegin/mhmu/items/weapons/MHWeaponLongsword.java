package de.gmx.worldsbegin.mhmu.items.weapons;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.items.MHRarity;
import de.gmx.worldsbegin.mhmu.util.MOD_INFO;
import de.gmx.worldsbegin.mhmu.util.Strings;

public class MHWeaponLongsword extends MHWeapon {
	public enum ATTACKS {
		// IDEA some attacks, maybe this will not maintain like this
	}

	private static IIcon icon;

	public MHWeaponLongsword(final int MHdamage,
			final HashMap<MHSharpness, Integer> sharpnessDuration,
			final MHRarity rarity) {
		super(MHdamage, MHWeaponType.LONGSWORD, sharpnessDuration, rarity);
	}
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1) {
		return MHWeaponLongsword.icon;
	}
	/**
	 * @see de.gmx.worldsbegin.mhmu.items.weapons.MHWeapon#getWeaponReachDistance(net.minecraft.item.ItemStack,
	 *      net.minecraft.world.World, net.minecraft.entity.Entity)
	 */
	@Override
	protected double getWeaponReachDistance(ItemStack par1ItemStack,
			World par2World, Entity par3Entity) {
		return 1.7D;
	}
	/*
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @Override public Icon getIconFromDamageForRenderPass(int par1, int par2)
	 * { return getIconFromDamage(par1); }
	 * 
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @Override public Icon getIcon(ItemStack stack, int pass) { return
	 * getIconFromDamage(1);//1 is a placeholder }
	 * 
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @Override public Icon getIcon(ItemStack stack, int renderPass,
	 * EntityPlayer player, ItemStack usingItem, int useRemaining) { return
	 * getIconFromDamage(1);//1 is a placeholder }
	 */
	@Override
	public boolean onWeaponBlock(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3Player) {
		return false;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		MHWeaponLongsword.icon = par1IconRegister.registerIcon(MOD_INFO.modid
				+ ":" + Strings.weaponsfolder + Strings.longswordIconName);
		this.setExtObjResourceLocation(new ResourceLocation(MOD_INFO.modid
				+ ":" + Strings.modelfolder + "items/" + Strings.weaponsfolder
				+ "tmp.mhmdl"));
	}
}
