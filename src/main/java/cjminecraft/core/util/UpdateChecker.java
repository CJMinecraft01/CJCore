package cjminecraft.core.util;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Action;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import joptsimple.internal.Strings;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * Handles any updates required using a custom JSON file hosted on a webserver.
 * Will automatically output whether an update is available when the player
 * joins the game
 * 
 * @author CJMinecraft
 *
 */
@Mod.EventBusSubscriber
public class UpdateChecker {

	/**
	 * The URL for the CJCore update json file
	 */
	public static final String cjcoreURL = "https://raw.githubusercontent.com/CJMinecraft01/CJCore/1.12/update.json";

	/**
	 * All the registered update urls with their {@link ModContainer} paired
	 * with it
	 */
	private static final HashMap<ModContainer, String> updateURLs = new HashMap<>();

	/**
	 * When the player joins the game, we should output whether there is an
	 * update
	 * 
	 * @param event
	 *            The event we are hooking into
	 */
	@SubscribeEvent
	public static void onPlayerJoin(PlayerLoggedInEvent event) {
		for (ModContainer mod : updateURLs.keySet()) {
			outputUpdateAvailable(mod, updateURLs.get(mod), event.player);
		}
	}

	/**
	 * Registers an update URL with a modid allowing CJCore to check if there is
	 * an update and notifying the player accordingly
	 * 
	 * @param modid
	 *            The modid of the mod which uses the URL
	 * @param url
	 *            The URL of the update JSON file
	 */
	public static void registerUpdateURL(String modid, String url) {
		ModContainer mod = null;
		for (ModContainer modCotainer : Loader.instance().getActiveModList()) {
			if (modCotainer.getModId().equals(modid)) {
				mod = modCotainer;
			}
		}
		if (mod == null) {
			// The mod is either not active, or present. Print an error
			return;
		}

		if (!updateURLs.containsKey(mod)) {
			updateURLs.put(mod, url);
			if (!CJCoreConfig.UPDATE_CHECKER_MODS.containsKey(mod.getModId())) {
				CJCoreConfig.UPDATE_CHECKER_MODS.put(mod.getModId(), false);
			}
		}
	}

	/**
	 * Checks for updates. Internal method, should not be called outside of
	 * {@link CJCore}
	 */
	public static void postInit() {
		List<ModContainer> upToDateMods = new ArrayList<>();
		for (ModContainer mod : updateURLs.keySet()) {
			Pair<Boolean, String> update = isUpdateAvailable(updateURLs.get(mod), mod.getVersion());
			if (update.getLeft()) {
				CJCore.logger.info("An update is available for: " + mod.getName() + ", Current version: "
						+ mod.getVersion() + ", Latest version: " + update.getRight());
			} else {
				upToDateMods.add(mod);
			}
		}
		for (ModContainer mod : upToDateMods) {
			updateURLs.remove(mod);
		}
	}

	/**
	 * States whether an update is available using the update json file
	 * 
	 * @param url
	 *            The url where the update file is found
	 * @param currentVersion
	 *            The current version of the mod
	 * @return Whether an update is available (and the new version)
	 */
	public static Pair<Boolean, String> isUpdateAvailable(String url, String currentVersion) {
		try {
			URL URL = new URL(url);
			JsonReader jr = new JsonReader(new InputStreamReader(URL.openStream()));
			JsonElement je = new JsonParser().parse(jr);
			JsonObject jo = je.getAsJsonObject();
			String version = jo.get("version").getAsString();
			if (!version.equals(currentVersion))
				return Pair.<Boolean, String>of(true, version);
		} catch (Exception e) {
			CJCore.logger.catching(e);
		}
		return Pair.<Boolean, String>of(false, "");
	}

	/**
	 * Outputs to the player whether an update is available for the given mod at
	 * the given URL
	 * 
	 * @param mod
	 *            The mod to check whether an update is available
	 * @param url
	 *            The URL of the update JSON
	 * @param player
	 *            The player to send the message to
	 */
	public static void outputUpdateAvailable(ModContainer mod, String url, EntityPlayer player) {
		if (!CJCoreConfig.UPDATE_CHECKER_MODS.containsKey(mod.getModId())
				|| !CJCoreConfig.UPDATE_CHECKER_MODS.get(mod.getModId()))
			return;

		String version = "";
		String name = "";
		List<String> changeLog = new ArrayList<String>();
		String downloadURL = "";
		try {
			URL URL = new URL(url);
			JsonReader jr = new JsonReader(new InputStreamReader(URL.openStream()));
			JsonElement je = new JsonParser().parse(jr);
			JsonObject jo = je.getAsJsonObject();
			version = jo.get("version").getAsString();
			name = jo.get("name").getAsString();
			JsonArray cl = jo.get("changelog").getAsJsonArray();
			for (JsonElement e : cl) {
				changeLog.add(e.getAsString());
			}
			if (jo.has("download"))
				downloadURL = jo.get("download").getAsString();
		} catch (Exception e) {
			CJCore.logger.info("Error reading update url: " + url);
			CJCore.logger.catching(e);
			return;
		}
		player.sendMessage(new TextComponentString(
				TextFormatting.WHITE + I18n.format("update.ready", TextFormatting.GOLD + name + TextFormatting.WHITE)));
		player.sendMessage(new TextComponentString(TextFormatting.WHITE
				+ I18n.format("update.version", TextFormatting.DARK_RED + mod.getVersion() + TextFormatting.WHITE,
						TextFormatting.DARK_GREEN + version)));
		ITextComponent changeLogAndVersion = new TextComponentString("");
		if (!Strings.isNullOrEmpty(downloadURL))
			changeLogAndVersion.appendSibling(new TextComponentString(TextFormatting.WHITE + "["
					+ TextFormatting.DARK_AQUA + I18n.format("update.download") + TextFormatting.WHITE + "] ").setStyle(
							new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadURL))));
		if (changeLog.size() > 0) {
			String changeLogString = "";
			for (String log : changeLog) {
				if (log.startsWith("="))
					changeLogString += TextFormatting.AQUA;
				else if (log.startsWith("-"))
					changeLogString += TextFormatting.RED;
				else if (log.startsWith("+"))
					changeLogString += TextFormatting.GREEN;
				else
					changeLogString += TextFormatting.WHITE;
				changeLogString += log + "\n";
			}
			changeLogString = changeLogString.substring(0, changeLogString.length() - 1);
			changeLogAndVersion.appendSibling(new TextComponentString(
					"[" + TextFormatting.DARK_AQUA + I18n.format("update.changelog") + TextFormatting.WHITE + "]")
							.setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new TextComponentString(changeLogString)))));
		}
		player.sendMessage(changeLogAndVersion);
	}

}
