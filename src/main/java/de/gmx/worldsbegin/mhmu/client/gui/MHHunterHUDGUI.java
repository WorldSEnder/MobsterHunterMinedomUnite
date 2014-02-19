package de.gmx.worldsbegin.mhmu.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiSnooper;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.MobsterHunterMinedomUnite;
import de.gmx.worldsbegin.mhmu.items.weapons.MHSharpness;
import de.gmx.worldsbegin.mhmu.items.weapons.MHWeapon;

@SideOnly(Side.CLIENT)
public class MHHunterHUDGUI extends Gui {
	private static MHHunterHUDGUI instance;

	private static String guiEnabledIden = "guiEnabled";

	public static MHHunterHUDGUI instance() {
		if (instance == null) {
			instance = new MHHunterHUDGUI(Minecraft.getMinecraft());
		}
		return instance;
	}

	private boolean isEnabled;
	private KeyBinding keyBinding;

	private Minecraft mc;

	private MHHunterHUDGUI(Minecraft theMC) {
		this.mc = theMC;
		this.isEnabled = MobsterHunterMinedomUnite.instance().getConfig()
				.get(Configuration.CATEGORY_GENERAL, guiEnabledIden, true)
				.getBoolean(true);
		this.keyBinding = new KeyBinding("key.toggleHunterGui", 69,
				"key.categories.misc");
	}

	@SubscribeEvent
	public void onKeyStroke(KeyInputEvent kie) {
		if (GameSettings.isKeyDown(this.keyBinding)) {
			if (this.mc.currentScreen == null) {
				MHHunterHUDGUI.this.isEnabled = !MHHunterHUDGUI.this.isEnabled;
			}
		}
	}

	@SubscribeEvent
	public void preOverlay(RenderGameOverlayEvent.Pre preRenderEvent) {
		if (preRenderEvent.type == ElementType.HELMET) {
			this.render(preRenderEvent);
		}
	}

	protected void render(RenderGameOverlayEvent.Pre renderEvent) {
		GL11.glPushMatrix();
		if (this.shouldBeActive()) {
			EntityClientPlayerMP theplayer = this.mc.thePlayer;
			ItemStack itemStackInHand = null;
			if (theplayer != null) {
				itemStackInHand = this.mc.thePlayer.getCurrentEquippedItem();
				Item itemInHand = null;
				if (itemStackInHand != null) {
					itemInHand = itemStackInHand.getItem();
					MHWeapon weaponInHand;
					if (itemInHand instanceof MHWeapon) {
						weaponInHand = (MHWeapon) itemInHand;
						int color = weaponInHand.weaponRarity.MHcolor;
						short alpha = 0xFF;

						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glColor4f((color >> 16 & 0xFF) / 255F,
								(color >> 8 & 0xFF) / 255F,
								(color & 0xFF) / 255F, alpha / 255F);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
								GL11.GL_ONE_MINUS_SRC_ALPHA);
						GL11.glEnable(GL11.GL_BLEND);

						// GL11.glBegin();
						// TODO write rendering
						// GL11.glEnd();

						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glBegin(GL11.GL_QUADS);
						float offsetX = 20F;
						float offsetY = 6F;
						float overallX = 85F;
						float passedSharp = offsetX;
						int overallSharp = weaponInHand.getMaxDamage() - 1;
						for (int i = 0; i < MHSharpness.values().length; i++) {
							float length;
							try {
								length = weaponInHand.sharpnessDuration
										.get(MHSharpness.values()[i]);
							} catch (NullPointerException nul) {
								length = 0;
							}
							length *= overallX / overallSharp * 1F;
							int sharpColor = MHSharpness.values()[i].color;
							GL11.glColor4f((sharpColor >> 16 & 0xFF) / 255F,
									(sharpColor >> 8 & 0xFF) / 255F,
									(sharpColor & 0xFF) / 255F, alpha / 255F);
							GL11.glVertex3f(passedSharp, offsetY, 0F);
							GL11.glVertex3f(passedSharp, offsetY + 10F, 0F);
							GL11.glVertex3f(passedSharp + length,
									offsetY + 10F, 0F);
							GL11.glVertex3f(passedSharp + length, offsetY, 0F);
							passedSharp += length;
						}
						GL11.glColor4f(0F, 0F, 0F, 1F);
						GL11.glVertex3f(
								offsetX
										+ weaponInHand
												.getSharpnessLeft(itemStackInHand)
										* overallX / overallSharp - 1F,
								offsetY, 0F);
						GL11.glVertex3f(
								offsetX
										+ weaponInHand
												.getSharpnessLeft(itemStackInHand)
										* overallX / overallSharp - 1F,
								offsetY + 10F, 0F);
						GL11.glVertex3f(
								offsetX
										+ weaponInHand
												.getSharpnessLeft(itemStackInHand)
										* overallX / overallSharp,
								offsetY + 10F, 0F);
						GL11.glVertex3f(
								offsetX
										+ weaponInHand
												.getSharpnessLeft(itemStackInHand)
										* overallX / overallSharp, offsetY, 0F);
						GL11.glEnd();

						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GL11.glDisable(GL11.GL_BLEND);
						this.mc.fontRenderer
								.drawString(
										weaponInHand
												.getSharpnessLeft(itemStackInHand)
												+ "",
										20,
										20,
										weaponInHand
												.getSharpness(itemStackInHand).color,
										false);
					}
				}
			}
		}
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPopMatrix();
	}

	private boolean shouldBeActive() {
		return Minecraft.isGuiEnabled()
				&& this.mc.isIntegratedServerRunning()
				&& this.isEnabled
				&& (this.mc.currentScreen == null || !(this.mc.currentScreen instanceof GuiControls
						|| this.mc.currentScreen instanceof GuiScreenResourcePacks
						|| this.mc.currentScreen instanceof GuiLanguage
						|| this.mc.currentScreen instanceof GuiSnooper || this.mc.currentScreen instanceof GuiStats));
	}
}
