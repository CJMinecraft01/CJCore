package cjminecraft.core.util.registries;

import net.minecraft.item.ItemBlock;

/**
 * States that the block uses a custom {@link ItemBlock}. Ensure to set
 * {@link Register.RegisterItemBlock#customItemBlock()} correctly.
 * 
 * @author CJMinecraft
 *
 */
public interface ICustomItemBlock {

	/**
	 * @return The custom {@link ItemBlock} to get registered
	 */
	ItemBlock getCustomItemBlock();

}
