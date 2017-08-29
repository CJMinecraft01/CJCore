package cjminecraft.core.util;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.client.FMLClientHandler;

/**
 * A utility class for sounds
 * 
 * @author CJMinecraft
 *
 */
public class SoundUtils {

	public static final SoundHandler soundManager = FMLClientHandler.instance().getClient().getSoundHandler();

	public static void playSound(ISound sound) {
		soundManager.playSound(sound);
	}
	
	public static void playSound(ResourceLocation name, SoundCategory category, float x, float y, float z, float volume, float pitch) {
		playSound(new SoundBase(name, category, volume, pitch, x, y, z));
	}
	
}
