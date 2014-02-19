/**
 * 
 */
package de.gmx.worldsbegin.mhmu.client.renderer;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.client.model.exObj.MHModelObject;
import de.gmx.worldsbegin.mhmu.items.MHItem;

/**
 * @author Carbon
 * 
 * @version 0.0.1a_02.08.2013
 */
@SideOnly(Side.CLIENT)
public class ItemModelManager implements IResourceManagerReloadListener {
	private static ItemModelManager instance;

	private static String getItemId(Item item) {
		return Item.itemRegistry.getNameForObject(item);
	}

	public static ItemModelManager instance() {
		if (instance == null) {
			instance = new ItemModelManager();
		}
		return instance;
	}
	private HashMap<String, MHModelObject[]> objMap;

	private IResourceManager resManager;

	private ItemModelManager() {
		this.objMap = new HashMap<String, MHModelObject[]>();
		this.resManager = Minecraft.getMinecraft().getResourceManager();
	}

	public MHModelObject getModelForItemStack(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (!(item instanceof MHItem))
			return null;
		MHItem itemMH = (MHItem) item;

		String itemId = getItemId(item);
		MHModelObject[] itemArray = this.objMap.get(itemId);

		if (itemArray == null) {
			ResourceLocation[] itemsRes = (itemMH)
					.getObjResourceLocationArray();
			itemArray = new MHModelObject[itemsRes.length];

			for (int i = 0; i < itemsRes.length; ++i) {
				itemArray[i] = new MHModelObject(this.resManager, itemsRes[i]);
			}
			this.objMap.put(itemId, itemArray);
		}
		try {
			return itemArray[itemMH.getObjResourceLocationIndex(itemStack)];
		} catch (IndexOutOfBoundsException ioobe) {
			return null;
		}
	}

	/**
	 * @see net.minecraft.client.resources.ResourceManagerReloadListener#func_110549_a(net.minecraft.client.resources.ResourceManager)
	 */
	@Override
	public void onResourceManagerReload(IResourceManager resourcemanager) {
		this.resManager = resourcemanager;
		this.objMap.clear();
	}
}
