/**
 * 
 */
package de.gmx.worldsbegin.mhmu.village;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.collect.ImmutableMap;

import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;
import de.gmx.worldsbegin.mhmu.inventory.InventoryMHItemChest;

/**
 * @author Carbon
 */
public class PlayerHunterStats implements IExtendedEntityProperties {
	public static final String identifier = "hunterStats";

	private EntityPlayer thePlayer; // For backreference

	private InventoryMHItemChest items;
	private InventoryMHItemChest armory;
	private InventoryPlayer hunterInv;

	private HashMap<String, Integer> killCountMap;
	private HashMap<String, ArrayList<Float>> killedSizes;

	public PlayerHunterStats(EntityPlayer player) {
		this.thePlayer = player;
		this.items = new InventoryMHItemChest(false);
		this.items.thePlayer = this.thePlayer;
		this.armory = new InventoryMHItemChest(true);
		this.hunterInv = new InventoryPlayer(player); // DEBUG redesign this
														// thing with 5 armor
														// etc.
		this.armory.thePlayer = this.thePlayer;
		// All monsters
		this.killCountMap = new HashMap<String, Integer>(60);
		this.killedSizes = new HashMap<String, ArrayList<Float>>(60);
	}

	public void addKillStat(EntityMinedom killedEntity) {
		String unlocalizedName = EntityList.getEntityString(killedEntity);
		Integer killCountBefore = this.killCountMap.get(unlocalizedName);
		int killCountAfter = killCountBefore == null ? 1 : killCountBefore + 1;
		this.killCountMap.put(unlocalizedName, killCountAfter);

		float entityLength = killedEntity.getDefaultSizeInMeter()
				* killedEntity.getDataWatcher().getWatchableObjectFloat(
						EntityMinedom.DW_SIZE);
		ArrayList<Float> existingLengths = this.killedSizes
				.get(unlocalizedName);
		if (existingLengths == null) {
			existingLengths = new ArrayList<Float>(15);
			this.killedSizes.put(unlocalizedName, existingLengths);
		}
		int indexToPut = 0;
		for (int i = 0; i < existingLengths.size(); i++) {
			if (existingLengths.get(i) < entityLength) {
				indexToPut++;
			} else {
				break;
			}
		}
		existingLengths.add(indexToPut, entityLength);
		// TODO HIGHPRIOR detectAndSendChanges()
	}

	public InventoryMHItemChest getArmoryInventory() {
		return this.armory;
	}

	public InventoryMHItemChest getItemInventory() {
		return this.items;
	}

	/**
	 * 
	 * @param entityID
	 *            Retrieve from {@link EntityList}
	 * @return
	 */
	public int getKillCountForEntity(String entityID) {
		Integer value = this.killCountMap.get(entityID);
		return value == null ? 0 : value;
	}

	public ImmutableMap<String, Integer> getKillCountMap() {
		return ImmutableMap.copyOf(this.killCountMap);
	}

	/**
	 * @see net.minecraftforge.common.IExtendedEntityProperties#init(net.minecraft.entity.Entity,
	 *      net.minecraft.world.World)
	 */
	@Override
	public void init(Entity entity, World world) {

	}

	/**
	 * @see net.minecraftforge.common.IExtendedEntityProperties#loadNBTData(net.minecraft.nbt.NBTTagCompound)
	 */
	@Override
	public void loadNBTData(NBTTagCompound entityNBT) {
		NBTTagCompound nbtTagToReadFrom = null;
		nbtTagToReadFrom = entityNBT.getCompoundTag("MobsterHunterStats");

		this.items = InventoryMHItemChest
				.getInvFromNBTTagCompound(nbtTagToReadFrom
						.getCompoundTag("items"));
		this.armory = InventoryMHItemChest
				.getInvFromNBTTagCompound(nbtTagToReadFrom
						.getCompoundTag("weapons"));
		this.items.thePlayer = this.thePlayer;
		this.armory.thePlayer = this.thePlayer;
		this.killCountMap = new HashMap<String, Integer>(60);
		this.killedSizes = new HashMap<String, ArrayList<Float>>(15);
	}

	/**
	 * @see net.minecraftforge.common.IExtendedEntityProperties#saveNBTData(net.minecraft.nbt.NBTTagCompound)
	 */
	@Override
	public void saveNBTData(NBTTagCompound entityTag) {
		NBTTagCompound nbtTagToWriteTo = new NBTTagCompound();

		NBTTagCompound itemsInv = new NBTTagCompound();
		this.items.writeToNBTTagCompound(itemsInv);
		nbtTagToWriteTo.setTag("items", itemsInv);

		NBTTagCompound weaponsInv = new NBTTagCompound();
		this.armory.writeToNBTTagCompound(weaponsInv);
		nbtTagToWriteTo.setTag("weapons", weaponsInv);
		// TODO write and read killCount and killedSizes

		entityTag.setTag("MobsterHunterStats", nbtTagToWriteTo);
	}
}
