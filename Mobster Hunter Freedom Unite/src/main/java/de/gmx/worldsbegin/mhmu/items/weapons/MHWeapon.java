package de.gmx.worldsbegin.mhmu.items.weapons;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.client.model.exObj.MHModelObject;
import de.gmx.worldsbegin.mhmu.client.multiplayer.MHPlayerControllerMP;
import de.gmx.worldsbegin.mhmu.client.renderer.IItemRendererMHWeapon;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;
import de.gmx.worldsbegin.mhmu.items.MHItem;
import de.gmx.worldsbegin.mhmu.items.MHRarity;
import de.gmx.worldsbegin.mhmu.items.tabs.CreativeTabs;
import de.gmx.worldsbegin.mhmu.util.Strings;

public abstract class MHWeapon extends ItemSword implements MHItem {
	public enum ATTACKS {

	}

	@SideOnly(Side.CLIENT)
	private ResourceLocation[] resourceLocations;

	protected float MHdamage;
	protected HashMap<MHElement, Float> elementalDamage;
	protected HashMap<MHStatusEffects, Float> statusDamage;

	public final MHRarity weaponRarity;
	public final HashMap<MHSharpness, Integer> sharpnessDuration;
	public final MHWeaponType weaponType;

	public final String damageType = Strings.rawDmgIden;
	public final UUID damageUuid = UUID
			.fromString("A0F0A0EB-EB5A-420D-9A98-904CB27B4E81");

	/**
	 * 
	 * @param id
	 * @param MHdamage
	 * @param type
	 * @param sharpnessDuration
	 *            - put here how long/how many hits you can do with the given
	 *            sharpness if the sharpness is not in the map, 0 will be
	 *            assumed
	 * */
	protected MHWeapon(float MHdamage, MHWeaponType type,
			HashMap<MHSharpness, Integer> sharpnessDuration, MHRarity rarity) {
		super(rarity == null ? Item.ToolMaterial.WOOD : rarity.toolMaterial);
		MinecraftForgeClient.registerItemRenderer(this,
				new IItemRendererMHWeapon());
		this.setNoRepair();
		this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabAllSearch);
		this.MHdamage = MHdamage;
		this.weaponType = type;

		int sharpness = 0;
		for (Integer value : sharpnessDuration.values()) {
			sharpness += value;
		}
		this.setMaxDamage(sharpness + 1);
		this.sharpnessDuration = sharpnessDuration;

		this.elementalDamage = new HashMap<MHElement, Float>();
		this.statusDamage = new HashMap<MHStatusEffects, Float>();
		this.weaponRarity = rarity;
	}

	@Override
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		if (par1ItemStack.getItem() instanceof MHWeapon)
			return ((MHWeapon) par1ItemStack.getItem()).weaponRarity.MHcolor;
		return 0xffffff;
	}

	/**
	 * @see net.minecraft.item.Item#getCreativeTab()
	 */
	@Override
	public net.minecraft.creativetab.CreativeTabs getCreativeTab() {
		return CreativeTabs.WeaponTab;
	}

	@Override
	public float getDigSpeed(ItemStack par1ItemStack, Block par2Block, int meta) {
		return 0.0F;
	}

	/**
	 * @see net.minecraft.item.ItemSword#getItemAttributeModifiers()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Multimap getItemAttributeModifiers() {
		@SuppressWarnings("unchecked")
		Multimap<String, AttributeModifier> map = super
				.getItemAttributeModifiers();
		map.removeAll(SharedMonsterAttributes.attackDamage
				.getAttributeUnlocalizedName());// All with normal attack
		map.put(this.damageType, new AttributeModifier(this.damageUuid,
				"Raw Damage", this.MHdamage, 0));
		for (Entry<MHElement, Float> e : this.elementalDamage.entrySet()) {
			MHElement key = e.getKey();
			map.put(key.damageType, new AttributeModifier(key.uuid,
					key.clearName, e.getValue(), 0));
		}
		for (Entry<MHStatusEffects, Float> e : this.statusDamage.entrySet()) {
			MHStatusEffects key = e.getKey();
			map.put(key.damageType, new AttributeModifier(key.uuid,
					key.clearName, e.getValue(), 0));
		}
		return map;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.block;
	}

	@Override
	public MHRarity getMHRarity(ItemStack par1ItemStack) {
		return this.weaponRarity;
	}

	@Override
	public ResourceLocation[] getObjResourceLocationArray() {
		return this.resourceLocations;
	}

	@Override
	public int getObjResourceLocationIndex(ItemStack par1ItemStack) {
		return 0;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		if (par1ItemStack.getItem() instanceof MHWeapon) {
			MHWeapon weaponItem = (MHWeapon) par1ItemStack.getItem();
			if (weaponItem.weaponRarity == null)
				return EnumRarity.common;
			return weaponItem.weaponRarity.enumRarity;
		}
		return EnumRarity.common;
	}

	public MHSharpness getSharpness(ItemStack itemStack) {
		if (this.sharpnessDuration == null)
			return MHSharpness.RED;
		int lookUp = 0;
		for (int i = 0; i < MHSharpness.values().length; i++) {
			Integer thisDuration = this.sharpnessDuration.get(MHSharpness
					.values()[i]);
			if (thisDuration == null) {
				thisDuration = Integer.valueOf(0);
			}
			if (thisDuration < 0) {
				thisDuration = Integer.valueOf(0);
			}
			lookUp += thisDuration;
			if (lookUp >= this.getSharpnessLeft(itemStack))
				return MHSharpness.values()[i];
		}
		return MHSharpness.PURPLE;//
	}

	public int getSharpnessLeft(ItemStack itemStack) {
		return this.getMaxDamage() - itemStack.getItemDamage() - 1;
	}

	/**
	 * @param par1ItemStack
	 * @param par2World
	 * @param par3Entity
	 * @return
	 */
	protected abstract double getWeaponReachDistance(ItemStack par1ItemStack,
			World par2World, Entity par3Entity);

	public MHWeaponType getWeaponType(ItemStack itemStack) {
		return this.weaponType;
	}

	@Override
	public boolean hitEntity(ItemStack par1ItemStack,
			EntityLivingBase par2EntityLiving, EntityLivingBase par3EntityLiving) {
		boolean hasBeenReflected = false;
		if (par2EntityLiving instanceof EntityMinedom) {
			hasBeenReflected = false; // DEBUG
										// par2EntityLiving.getSharpnessRequiredToDamage(par2EntityLiving,
										// par3EntityLiving, ItemStack
										// par1ItemStack).ordinal>this.getSharpness().ordinal;

		}
		if (par1ItemStack.getItem() instanceof MHWeapon) {
			if (((MHWeapon) par1ItemStack.getItem())
					.getSharpnessLeft(par1ItemStack) > 0) {
				par1ItemStack.damageItem(hasBeenReflected ? 2 : 1,
						par3EntityLiving); // DEBUG real sharpness-hurt values
											// later on
			}
		}
		return true;
	}

	@Override
	public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
		return false;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {
		return false;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (this.onWeaponBlock(par1ItemStack, par2World, par3EntityPlayer)) {
			super.onItemRightClick(par1ItemStack, par2World, par3EntityPlayer);
		}
		return par1ItemStack;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity par1Entity) {
		// TODO we need a method to attack the entity with elemental damage
		return true;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) { // I think we can find
															// better mthod for
															// this... only when
															// it gets in and
															// out of the hand
		double reach = this.getWeaponReachDistance(par1ItemStack, par2World,
				par3Entity);

		if (par3Entity instanceof EntityClientPlayerMP) {
			Minecraft mc = Minecraft.getMinecraft();
			ItemStack heldItemStack = ((EntityClientPlayerMP) par3Entity)
					.getHeldItem();
			if (heldItemStack != null
					&& heldItemStack.getItem() instanceof MHWeapon) {
				if (mc.playerController instanceof MHPlayerControllerMP) {
					((MHPlayerControllerMP) mc.playerController)
							.setReachDistance((float) reach);
				}
			} else { // If not in hand
				if (mc.playerController instanceof MHPlayerControllerMP) {
					((MHPlayerControllerMP) mc.playerController)
							.setReachDistance(5.0F);
				}
			}
		} else if (par3Entity instanceof EntityPlayerMP) {
			ItemStack heldItemStack = ((EntityPlayerMP) par3Entity)
					.getHeldItem();
			if (heldItemStack != null
					&& heldItemStack.getItem() instanceof MHWeapon) {
				((EntityPlayerMP) par3Entity).theItemInWorldManager
						.setBlockReachDistance(reach);
			} else {
				((EntityPlayerMP) par3Entity).theItemInWorldManager
						.setBlockReachDistance(5.0F);
			}
		}
	}

	/**
	 * Return true if this weapon should block like a normal sword. This is just
	 * a callback
	 * 
	 * @param par1ItemStack
	 * @param par2World
	 * @param par3Player
	 * @return normal block?
	 */
	public abstract boolean onWeaponBlock(ItemStack par1ItemStack,
			World par2World, EntityPlayer par3Player);

	public void setElementalDamage(MHElement element, float damage) {
		if (damage <= 0 || element == null)
			return;
		this.elementalDamage.put(element, damage);
	}

	/**
	 * Sets the ResourceLocation to the {@link MHModelObject} to render this
	 * item. This method only takes one argument as
	 * {@link #getObjResourceLocationIndex(ItemStack)} will always return 0 if
	 * you don't override it in your class. In that case you could override
	 * {@link #getObjResourceLocationArray()}, too, which makes this method
	 * useless.
	 * 
	 * @param resourceLocations
	 *            the {@link ResourceLocation} to the {@link MHModelObject}
	 */
	public void setExtObjResourceLocation(ResourceLocation resourceLocations) {
		this.resourceLocations = new ResourceLocation[]{resourceLocations};
	}

	public void setRawDamage(float damage) {
		if (damage <= 0)
			return;
		this.MHdamage = damage;
	}

	public void setStatusDamage(MHStatusEffects status, float effectiveness) {
		if (effectiveness <= 0 || status == null)
			return;
		this.statusDamage.put(status, effectiveness);
	}
}
