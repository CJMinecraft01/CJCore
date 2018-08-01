package cjminecraft.core.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScaledResolution {
	private int		scaledWidth;
	private int		scaledHeight;
	private double	scaledWidthD;
	private double	scaledHeightD;
	private int		scaleFactor;

	public ScaledResolution(Minecraft mc, int width, int height) {
		this.scaledWidth = width;
		this.scaledHeight = height;
		this.scaleFactor = 1;
		boolean flag = mc.getLanguageManager().isCurrentLocaleUnicode() || mc.gameSettings.forceUnicodeFont;
		int scale = mc.gameSettings.guiScale;

		if (scale == 0) {
			scale = 1000;
		}

		while (this.scaleFactor < scale && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
			++this.scaleFactor;
		}

		if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
			--this.scaleFactor;
		}

		this.scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
		this.scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
		this.scaledWidth = MathHelper.ceil(this.scaledWidthD);
		this.scaledHeight = MathHelper.ceil(this.scaledHeightD);
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public int getScaledHeight() {
		return this.scaledHeight;
	}

	public double getScaledWidth_double() {
		return this.scaledWidthD;
	}

	public double getScaledHeight_double() {
		return this.scaledHeightD;
	}

	public int getScaleFactor() {
		return this.scaleFactor;
	}
}