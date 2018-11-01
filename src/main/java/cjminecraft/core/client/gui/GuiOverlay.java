package cjminecraft.core.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import cjminecraft.core.client.gui.overlay.OverlayBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Base class for use with the {@link RenderGameOverlayEvent} which holds
 * multiple overlays. Make your class extend this and make sure to register the
 * event using {@link MinecraftForge#EVENT_BUS}
 * 
 * @author CJMinecraft
 *
 */
public class GuiOverlay extends GuiCore {

	protected List<OverlayBase> overlays = new ArrayList<OverlayBase>();

	protected EntityPlayerSP player;

	/**
	 * Initialises the container to have no slots
	 */
	public GuiOverlay() {
		super(new Container() {

			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return player != null;
			}
		});
		setWorldAndResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackgroundLayer();
	}

	/**
	 * Used for the actual event
	 * 
	 * @param event
	 *            The event
	 */
	@SubscribeEvent(receiveCanceled = true)
	public void drawOverlay(RenderGameOverlayEvent.Pre event) {
		if (Minecraft.getMinecraft().currentScreen != null)
			return;
		this.player = Minecraft.getMinecraft().player;
		if (this.player == null)
			return;
		this.width = event.getResolution().getScaledWidth();
		this.height = event.getResolution().getScaledHeight();
		if (event.getType() == ElementType.ALL) {
			drawScreen();
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		this.overlays.clear();
	}

	/**
	 * Draw the actual screen. Should override this
	 */
	public void drawScreen() {
		updateElementInformation();

		drawBackgroundLayer();
		drawForegroundLayer();

		updateOverlays();
	}

	/**
	 * Draw the foreground layer
	 */
	protected void drawForegroundLayer() {
		drawOverlays(true);
	}

	/**
	 * Draw the background layer
	 */
	protected void drawBackgroundLayer() {
		drawOverlays(false);
	}

	/**
	 * Handle when anything changes regarding the mouse
	 */
	@Override
	public void handleMouseInput() throws IOException {
		int wheelMovement = Mouse.getEventDWheel();

		if (wheelMovement != 0) {
			for (int i = this.overlays.size(); i-- > 0;) {
				OverlayBase overlay = this.overlays.get(i);
				if (!overlay.isVisible() || !overlay.isEnabled())
					continue;
				if (overlay.onMouseWheel(wheelMovement))
					return;
			}
			if (onMouseWheel(wheelMovement))
				return;
		}

		super.handleMouseInput();
	}

	/**
	 * Handle mouse wheel movement
	 * 
	 * @param wheelMovement
	 *            How much the wheel has moved
	 * @return Whether to overrule all other handlers
	 */
	protected boolean onMouseWheel(int wheelMovement) {
		return false;
	}

	/**
	 * Update all of the element information
	 */
	protected void updateElementInformation() {
		for (int i = this.overlays.size(); i-- > 0;) {
			OverlayBase overlay = this.overlays.get(i);
			overlay.updateElementInformation();
		}
	}

	/**
	 * Update all of the overlays
	 */
	protected void updateOverlays() {
		for (int i = this.overlays.size(); i-- > 0;) {
			OverlayBase overlay = this.overlays.get(i);
			if (overlay.isEnabled())
				overlay.update();
		}
	}

	/**
	 * Draw all of the overlays
	 * 
	 * @param foreground
	 *            Whether it is the foreground or not
	 */
	protected void drawOverlays(boolean foreground) {
		if (foreground) {
			for (int i = this.overlays.size(); i-- > 0;) {
				OverlayBase overlay = this.overlays.get(i);
				if (overlay.isVisible() && overlay.isEnabled())
					overlay.drawForeground();
			}
		} else {
			for (int i = this.overlays.size(); i-- > 0;) {
				OverlayBase overlay = this.overlays.get(i);
				if (overlay.isVisible() && overlay.isEnabled())
					overlay.drawBackground();
			}
		}
	}

	/**
	 * Add the given {@link OverlayBase} to the list of overlays to be handled
	 * 
	 * @param overlay
	 *            The {@link OverlayBase} to add
	 * @return The {@link OverlayBase} which was added
	 */
	public OverlayBase addOverlay(OverlayBase overlay) {
		this.overlays.add(overlay);
		return overlay;
	}

	/**
	 * @return All the overlays in the gui
	 */
	public List<OverlayBase> getOverlays() {
		return overlays;
	}

}
