package cjminecraft.core.util.registries;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Level;

import cjminecraft.core.CJCore;
import cjminecraft.core.util.registries.Register;
import cjminecraft.core.util.registries.Register.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * An automatic registry system which utilises the {@link Register} annotation.
 * Place the <code>@Register</code> annotation on a class which contains the
 * objects you would like to register
 * 
 * @author CJMinecraft
 *
 */
public class AutomaticRegistrar {

	/**
	 * A list of all the classes with the <code>@Registry</code> annotation
	 * present
	 */
	private static HashMap<String, Class> registryClasses = new HashMap<String, Class>();

	/**
	 * Uses the {@link ASMDataTable} to get all of the classes with the
	 * <code>@Registry</code> annotation present. For use only in {@link CJCore}
	 * 
	 * @param dataTable
	 *            The {@link ASMDataTable} to get the information from
	 */
	public static void addRegistryClasses(ASMDataTable dataTable) {
		CJCore.logger.info("Searching for registrar classes");
		for (ASMData data : dataTable.getAll(Register.class.getName())) {
			try {
				registryClasses.put((String) data.getAnnotationInfo().get("modid"), Class.forName(data.getClassName()));
				CJCore.logger.info("Found registrar class: " + data.getClassName());
			} catch (Exception e) {
				CJCore.logger.error("Unable add registrar class: " + data.getClassName() + "! An error occurred:");
				CJCore.logger.catching(Level.ERROR, e);
			}
		}
	}

	public static void registerItems() {
		CJCore.logger.info("Searching for items to register");
		int registeredItems = 0;
		for (Entry<String, Class> entry : registryClasses.entrySet()) {
			for (Method method : entry.getValue().getMethods()) {
				if (method.isAnnotationPresent(RegisterItemInit.class)) {
					try {
						method.invoke(null);
					} catch (Exception e) {
						CJCore.logger.error("Unable to initialise items using init method: " + method.getName()
								+ "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
			}
			for (Field field : entry.getValue().getDeclaredFields()) {
				if (field.isAnnotationPresent(RegisterItem.class)) {
					try {
						RegisterItem details = field.getAnnotation(RegisterItem.class);
						Item item = (Item) field.get(null);
						if (item == null) {
							item = (Item) field.getType().newInstance();
							field.set(null, item);
						}
						if (item.getRegistryName() == null)
							item.setRegistryName(new ResourceLocation(entry.getKey(), details.registryName()));
						if (details.setUnlocalizedName()) {
							if (!details.unlocalizedName().isEmpty())
								item.setUnlocalizedName(details.unlocalizedName());
							else
								item.setUnlocalizedName(details.registryName());
						}
						ForgeRegistries.ITEMS.register(item);
						registeredItems++;
					} catch (Exception e) {
						CJCore.logger.error(
								"Unable to register item: " + field.getName() + "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
				if (field.isAnnotationPresent(RegisterItemBlock.class)) {
					try {
						RegisterItemBlock details = field.getAnnotation(RegisterItemBlock.class);
						Block block = (Block) field.get(null);
						if (block == null) {
							block = (Block) field.getType().newInstance();
							field.set(null, block);
						}

						ItemBlock item = null;
						if (!details.customItemBlock()) {
							item = new ItemBlock(block);
						} else if (block instanceof ICustomItemBlock) {
							ICustomItemBlock customItemBlock = (ICustomItemBlock) block;
							item = customItemBlock.getCustomItemBlock();
						} else {
							CJCore.logger.error(
									"Tried to register custom item block but none was found! Please ensure the block is an instance of cjminecraft.industrialtech.utils.registries.ICustomItemBlock");
							continue;
						}

						if (item.getRegistryName() == null)
							item.setRegistryName(new ResourceLocation(entry.getKey(), details.registryName()));
						if (details.setUnlocalizedName()) {
							if (!details.unlocalizedName().isEmpty())
								item.setUnlocalizedName(details.unlocalizedName());
							else
								item.setUnlocalizedName(details.registryName());
						}
						ForgeRegistries.ITEMS.register(item);
						registeredItems++;
					} catch (Exception e) {
						CJCore.logger.error("Unable to register item block: " + field.getName()
								+ "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
			}
		}
		CJCore.logger.info("Successfully registered " + registeredItems + " items!");
	}

	public static void registerBlocks() {
		CJCore.logger.info("Searching for blocks and tiles to register");
		int registeredBlocks = 0;
		int registeredTiles = 0;
		for (Entry<String, Class> entry : registryClasses.entrySet()) {
			for (Method method : entry.getValue().getDeclaredMethods()) {
				if (method.isAnnotationPresent(RegisterBlockInit.class)) {
					try {
						method.invoke(null);
					} catch (Exception e) {
						CJCore.logger.error("Unable to initialise items using init method: " + method.getName()
								+ "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
			}
			for (Field field : entry.getValue().getDeclaredFields()) {
				if (field.isAnnotationPresent(RegisterBlock.class)) {
					try {
						RegisterBlock details = field.getAnnotation(RegisterBlock.class);
						Block block = (Block) field.get(null);
						if (block == null) {
							block = (Block) field.getType().newInstance();
							field.set(null, block);
						}

						if (block.getRegistryName() == null)
							block.setRegistryName(new ResourceLocation(entry.getKey(), details.registryName()));
						if (details.setUnlocalizedName()) {
							if (!details.unlocalizedName().isEmpty())
								block.setUnlocalizedName(details.unlocalizedName());
							else
								block.setUnlocalizedName(details.registryName());
						}
						ForgeRegistries.BLOCKS.register(block);
						registeredBlocks++;
					} catch (Exception e) {
						CJCore.logger.error(
								"Unable to register block: " + field.getName() + "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
				if (field.isAnnotationPresent(RegisterTileEntity.class)) {
					try {
						RegisterTileEntity details = field.getAnnotation(RegisterTileEntity.class);
						GameRegistry.registerTileEntity(details.tileEntityClass(), details.key());
						registeredTiles++;
					} catch (Exception e) {
						CJCore.logger.error("Unable to register tile entity: " + field.getName()
								+ "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
			}
		}

		CJCore.logger.info("Successfully registered " + registeredBlocks + " blocks!");
		CJCore.logger.info("Successfully registered " + registeredTiles + " tiles!");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		CJCore.logger.info("Searching for items to register renders for");
		int registeredItems = 0;
		int registeredBlocks = 0;
		int registeredTESRs = 0;
		for (Entry<String, Class> entry : registryClasses.entrySet()) {
			for (Field field : entry.getValue().getDeclaredFields()) {
				if (field.isAnnotationPresent(RegisterRender.class)) {
					try {
						RegisterRender details = field.getAnnotation(RegisterRender.class);
						if (field.get(null) instanceof Item) {
							Item item = (Item) field.get(null);
							if (item != null) {
								if (details.hasVariants()) {
									ResourceLocation[] names = new ResourceLocation[details.variants().length];
									for (int i = 0; i < details.variants().length; i++)
										names[i] = new ResourceLocation(entry.getKey(), details.variants()[i]);
									ModelLoader.registerItemVariants(item, names);
									for (int meta = 0; meta < names.length; meta++) {
										ModelLoader.setCustomModelResourceLocation(item, meta,
												new ModelResourceLocation(names[meta], "inventory"));
									}
								} else
									ModelLoader.setCustomModelResourceLocation(item, 0,
											new ModelResourceLocation(
													new ResourceLocation(entry.getKey(), item.getUnlocalizedName()),
													"inventory"));
								registeredItems++;
							} else {
								CJCore.logger.error("Unable to register renders for item: " + field.getName()
										+ "! The item cannot be null!");
							}
						} else if (field.get(null) instanceof Block) {
							Block block = (Block) field.get(null);
							if (block != null) {
								Item item = Item.getItemFromBlock(block);
								if (details.hasVariants()) {
									ResourceLocation[] names = new ResourceLocation[details.variants().length];
									for (int i = 0; i < details.variants().length; i++)
										names[i] = new ResourceLocation(entry.getKey(), details.variants()[i]);
									ModelLoader.registerItemVariants(item, names);
									for (int meta = 0; meta < names.length; meta++) {
										ModelLoader.setCustomModelResourceLocation(item, meta,
												new ModelResourceLocation(names[meta], "inventory"));
									}
								} else
									ModelLoader.setCustomModelResourceLocation(item, 0,
											new ModelResourceLocation(
													new ResourceLocation(entry.getKey(), item.getUnlocalizedName()),
													"inventory"));
								registeredBlocks++;
							} else {
								CJCore.logger.error("Unable to register renders for block: " + field.getName()
										+ "! The block cannot be null!");
							}
						}
					} catch (Exception e) {
						CJCore.logger.error("Unable to register renders for: " + field.getName()
								+ "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
				if (field.isAnnotationPresent(RegisterTESR.class)) {
					try {
						RegisterTESR details = field.getAnnotation(RegisterTESR.class);
						ClientRegistry.bindTileEntitySpecialRenderer(details.tileEntityClass(),
								(TileEntitySpecialRenderer<? super TileEntity>) details.renderClass().newInstance());
						registeredTESRs++;
					} catch (Exception e) {
						CJCore.logger.error("Unable to register TESR for block: " + field.getName()
								+ "! The following error was thrown:");
						CJCore.logger.catching(Level.ERROR, e);
					}
				}
			}
		}
		CJCore.logger.info("Successfully registered renders for " + registeredItems + " items!");
		CJCore.logger.info("Successfully registered renders for " + registeredBlocks + "  item blocks!");
		CJCore.logger.info("Successfully registered " + registeredTESRs + " TESRs!");
	}

}
