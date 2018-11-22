package cjminecraft.core.util.registries;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import cjminecraft.core.CJCore;
import cjminecraft.core.util.EnumUtils;
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
@Mod.EventBusSubscriber(modid = CJCore.MODID)
public class AutomaticRegistrar {

	/**
	 * A list of all the classes with the <code>@Registry</code> annotation
	 * present
	 */
	private static HashMap<String, List<Class>> registryClasses = new HashMap<>();
	/**
	 * A list of all the {@link TileEntity}'s to register
	 */
	private static List<Pair<String, Class>> tiles = new ArrayList<>();
	/**
	 * A list of all the {@link TileEntitySpecialRenderer}'s to register
	 */
	private static List<Pair<Class, Class>> tesrs = new ArrayList<>();

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
				String modid = (String) data.getAnnotationInfo().get("modid");
				if (!registryClasses.containsKey(modid))
					registryClasses.put(modid, new ArrayList<Class>());
				registryClasses.get(modid).add(Class.forName(data.getClassName()));
				CJCore.logger.info("Found registrar class: " + data.getClassName());
			} catch (Exception e) {
				CJCore.logger.error("Unable add registrar class: " + data.getClassName() + "! An error occurred:");
				CJCore.logger.catching(Level.ERROR, e);
			}
		}
		for (ASMData data : dataTable.getAll(RegisterTileEntity.class.getName())) {
			try {
				if (Class.forName(data.getClassName()).isInstance(TileEntity.class))
					tiles.add(
							Pair.of((String) data.getAnnotationInfo().get("key"), Class.forName(data.getClassName())));
				else
					CJCore.logger.warn("Found tile entity marker on non tile entity class: " + data.getClassName());
			} catch (Exception e) {
				CJCore.logger.error("Unable to add tile entity: " + data.getClassName()
						+ " to the list of tile entities to register! An error occurred:");
				CJCore.logger.catching(Level.ERROR, e);
			}
		}
		for (ASMData data : dataTable.getAll(RegisterTESR.class.getName())) {
			try {
				if (Class.forName(data.getClassName()).isInstance(TileEntitySpecialRenderer.class))
					tesrs.add(Pair.of((Class) data.getAnnotationInfo().get("tileEntityClass"),
							Class.forName(data.getClassName())));
				else
					CJCore.logger
							.warn("Found tile entity special renderer marker on non tile entity special renderer class: "
									+ data.getClassName());
			} catch (Exception e) {
				CJCore.logger.error("Unable to add tile entity: " + data.getClassName()
						+ " to the list of tile entities to register! An error occurred:");
				CJCore.logger.catching(Level.ERROR, e);
			}
		}
	}

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		CJCore.logger.info("Searching for items to register");
		int registeredItems = 0;
		for (Entry<String, List<Class>> entry : registryClasses.entrySet()) {
			Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get(entry.getKey()));
			for (Class clazz : entry.getValue()) {
				for (Method method : clazz.getMethods()) {
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
				for (Field field : clazz.getDeclaredFields()) {
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
									item.setTranslationKey(details.unlocalizedName());
								else
									item.setTranslationKey(details.registryName());
							}
							event.getRegistry().register(item);
							registeredItems++;
						} catch (Exception e) {
							CJCore.logger.error("Unable to register item: " + field.getName()
									+ "! The following error was thrown:");
							CJCore.logger.catching(Level.ERROR, e);
						}
					}
					if (field.isAnnotationPresent(RegisterBlock.class)) {
						try {
							RegisterBlock details = field.getAnnotation(RegisterBlock.class);
							if (details.registerItemBlock()) {
								Block block = (Block) field.get(null);
								if (block == null) {
									block = (Block) field.getType().newInstance();
									field.set(null, block);
								}

								ItemBlock item = null;
								if (block instanceof ICustomItemBlock) {
									ICustomItemBlock customItemBlock = (ICustomItemBlock) block;
									item = customItemBlock.getCustomItemBlock();
								} else {
									item = new ItemBlock(block);
								}

								if (item.getRegistryName() == null)
									item.setRegistryName(new ResourceLocation(entry.getKey(), details.registryName()));
								if (details.setUnlocalizedName()) {
									if (!details.unlocalizedName().isEmpty())
										item.setTranslationKey(details.unlocalizedName());
									else
										item.setTranslationKey(details.registryName());
								}
								event.getRegistry().register(item);
								registeredItems++;
							}
						} catch (Exception e) {
							CJCore.logger.error("Unable to register item block: " + field.getName()
									+ "! The following error was thrown:");
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
							if (block instanceof ICustomItemBlock) {
								ICustomItemBlock customItemBlock = (ICustomItemBlock) block;
								item = customItemBlock.getCustomItemBlock();
							} else {
								item = new ItemBlock(block);
							}

							if (item.getRegistryName() == null)
								item.setRegistryName(new ResourceLocation(entry.getKey(), details.registryName()));
							if (details.setUnlocalizedName()) {
								if (!details.unlocalizedName().isEmpty())
									item.setTranslationKey(details.unlocalizedName());
								else
									item.setTranslationKey(details.registryName());
							}
							event.getRegistry().register(item);
							registeredItems++;
						} catch (Exception e) {
							CJCore.logger.error("Unable to register item block: " + field.getName()
									+ "! The following error was thrown:");
							CJCore.logger.catching(Level.ERROR, e);
						}
					}
				}
			}

		}
		CJCore.logger.info("Successfully registered " + registeredItems + " items!");
		Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get(CJCore.MODID));
	}

	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		CJCore.logger.info("Searching for blocks and tiles to register");
		int registeredBlocks = 0;
		int registeredTiles = 0;
		for (Entry<String, List<Class>> entry : registryClasses.entrySet()) {
			Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get(entry.getKey()));
			for (Class clazz : entry.getValue()) {
				for (Method method : clazz.getDeclaredMethods()) {
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
				for (Field field : clazz.getDeclaredFields()) {
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
									block.setTranslationKey(details.unlocalizedName());
								else
									block.setTranslationKey(details.registryName());
							}
							event.getRegistry().register(block);
							registeredBlocks++;
						} catch (Exception e) {
							CJCore.logger.error("Unable to register block: " + field.getName()
									+ "! The following error was thrown:");
							CJCore.logger.catching(Level.ERROR, e);
						}
					}
				}
			}
		}
		for (Pair<String, Class> tileData : tiles) {
			try {
				TileEntity.register(tileData.getLeft(), tileData.getRight());
				registeredTiles++;
			} catch (Exception e) {
				CJCore.logger.error(
						"Unable to register tile entity: " + tileData.getLeft() + "! The following error was thrown:");
				CJCore.logger.catching(Level.ERROR, e);
			}
		}

		CJCore.logger.info("Successfully registered " + registeredBlocks + " blocks!");
		CJCore.logger.info("Successfully registered " + registeredTiles + " tiles!");
		Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get(CJCore.MODID));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		CJCore.logger.info("Searching for items to register renders for");
		int registeredItems = 0;
		int registeredBlocks = 0;
		int registeredTESRs = 0;
		for (Entry<String, List<Class>> entry : registryClasses.entrySet()) {
			for (Class clazz : entry.getValue()) {
				for (Field field : clazz.getDeclaredFields()) {
					if (field.isAnnotationPresent(RegisterRender.class)) {
						try {
							RegisterRender details = field.getAnnotation(RegisterRender.class);
							if (field.get(null) instanceof Item) {
								Item item = (Item) field.get(null);
								if (item != null) {
									if (details.variants().length != 0) {
										ResourceLocation[] names = new ResourceLocation[details.variants().length];
										for (int i = 0; i < details.variants().length; i++)
											names[i] = new ResourceLocation(entry.getKey(), details.variants()[i]);
										ModelLoader.registerItemVariants(item, names);
										for (int meta = 0; meta < names.length; meta++) {
											ModelLoader.setCustomModelResourceLocation(item, meta,
													new ModelResourceLocation(names[meta], "inventory"));
										}
									} else if (details.variantEnum() != Class.class) {
										ResourceLocation[] names = generateModelVariants(entry.getKey(),
												details.variantPrefix(), details.variantSuffix(),
												details.variantEnum());
										ModelLoader.registerItemVariants(item, names);
										for (int meta = 0; meta < names.length; meta++) {
											ModelLoader.setCustomModelResourceLocation(item, meta,
													new ModelResourceLocation(names[meta], "inventory"));
										}
									} else
										ModelLoader.setCustomModelResourceLocation(item, 0,
												new ModelResourceLocation(item.getRegistryName(), "inventory"));
									if (item instanceof IItemColor)
										Minecraft.getMinecraft().getItemColors()
												.registerItemColorHandler((IItemColor) item, item);
									registeredItems++;
								} else {
									CJCore.logger.error("Unable to register renders for item: " + field.getName()
											+ "! The item cannot be null!");
								}
							} else if (field.get(null) instanceof Block) {
								Block block = (Block) field.get(null);
								if (block != null) {
									Item item = Item.getItemFromBlock(block);
									if (details.variants().length != 0) {
										ResourceLocation[] names = new ResourceLocation[details.variants().length];
										for (int i = 0; i < details.variants().length; i++)
											names[i] = new ResourceLocation(entry.getKey(), details.variants()[i]);
										ModelLoader.registerItemVariants(item, names);
										for (int meta = 0; meta < names.length; meta++) {
											ModelLoader.setCustomModelResourceLocation(item, meta,
													new ModelResourceLocation(names[meta], "inventory"));
										}
									} else if (details.variantEnum() != Class.class) {
										ResourceLocation[] names = generateModelVariants(entry.getKey(),
												details.variantPrefix(), details.variantSuffix(),
												details.variantEnum());
										ModelLoader.registerItemVariants(item, names);
										for (int meta = 0; meta < names.length; meta++) {
											ModelLoader.setCustomModelResourceLocation(item, meta,
													new ModelResourceLocation(names[meta], "inventory"));
										}
									} else
										ModelLoader.setCustomModelResourceLocation(item, 0,
												new ModelResourceLocation(item.getRegistryName(), "inventory"));
									if (block instanceof IItemColor)
										Minecraft.getMinecraft().getItemColors()
												.registerItemColorHandler((IItemColor) block, block);
									if (item instanceof IItemColor)
										Minecraft.getMinecraft().getItemColors()
												.registerItemColorHandler((IItemColor) item, block);
									if (block instanceof IBlockColor)
										Minecraft.getMinecraft().getBlockColors()
												.registerBlockColorHandler((IBlockColor) block, block);
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
				}
			}
		}
		for (Pair<Class, Class> tesrData : tesrs) {
			try {
				ClientRegistry.bindTileEntitySpecialRenderer(tesrData.getLeft(),
						(TileEntitySpecialRenderer<? super TileEntity>) tesrData.getRight().newInstance());
				registeredTESRs++;
			} catch (Exception e) {
				CJCore.logger.error("Unable to register TESR: " + tesrData.getRight().getName()
						+ "! The following error was thrown:");
				CJCore.logger.catching(Level.ERROR, e);
			}
		}

		CJCore.logger.info("Successfully registered renders for " + registeredItems + " items!");
		CJCore.logger.info("Successfully registered renders for " + registeredBlocks + " item blocks!");
		CJCore.logger.info("Successfully registered " + registeredTESRs + " TESRs!");
	}

	/**
	 * Generates an array of {@link ResourceLocation}s which is used by the
	 * {@link AutomaticRegistrar}
	 * 
	 * @param modid
	 *            The modid of the model
	 * @param prefix
	 *            The prefix of the variant
	 * @param suffix
	 *            The suffix of the variant
	 * @param enumClass
	 *            The enum class containing all of the variants
	 * @return the array of {@link ResourceLocation}s used by the
	 *         {@link AutomaticRegistrar}
	 */
	public static <E extends Enum<E> & IStringSerializable> ResourceLocation[] generateModelVariants(String modid,
			String prefix, String suffix, Class<E> enumClass) {
		List<ResourceLocation> variants = new ArrayList<>();
		for (IStringSerializable variant : EnumUtils.getEnumValues(enumClass))
			variants.add(new ResourceLocation(modid, prefix + variant.getName() + suffix));
		return variants.toArray(new ResourceLocation[0]);
	}

}
