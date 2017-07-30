package cjminecraft.core.util;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import joptsimple.internal.Strings;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles any updates required using a custom JSON file hosted on a webserver
 * 
 * @author CJMinecraft
 *
 */
public class VersionChecker {

	/**
	 * The URL for the CJCore update json file
	 */
	public static final String cjcoreURL = "https://raw.githubusercontent.com/CJMinecraft01/CJCore/1.12/update.json";

	/**
	 * States whether an update is available using the update json file
	 * 
	 * @param url
	 *            The url where the update file is found
	 * @param currentVersion
	 *            The current version of the mod
	 * @return Whether an update is available
	 */
	public static boolean isUpdate(String url, String currentVersion) {
		try {
			URL URL = new URL(url);
			JsonReader jr = new JsonReader(new InputStreamReader(URL.openStream()));
			JsonElement je = new JsonParser().parse(jr);
			JsonObject jo = je.getAsJsonObject();
			String version = jo.get("version").getAsString();
			if (!version.equals(currentVersion))
				return true;
		} catch (Exception e) {
			CJCore.logger.catching(e);
		}
		return false;
	}

	/**
	 * Will display a message when there is an update available using
	 * information from the update file
	 * 
	 * @param url
	 *            The url path to the file
	 * @param modid
	 *            So that the update checker can be disabled in the config
	 * @param currentVersion
	 *            The current mod version
	 * @param player
	 *            The player to send the messages to
	 */
	public static void checkForUpdate(String url, String modid, String currentVersion, EntityPlayer player) {
		if (!CJCoreConfig.UPDATE_CHECKER_MODS.containsKey(modid) || !CJCoreConfig.UPDATE_CHECKER_MODS.get(modid))
			return;
		String version;
		String name = "";
		List<String> changeLog = new ArrayList<>();
		String downloadURL = "";
		boolean updateRequired = false;
		try {
			URL URL = new URL(url);
			JsonReader jr = new JsonReader(new InputStreamReader(URL.openStream()));
			JsonElement je = new JsonParser().parse(jr);
			JsonObject jo = je.getAsJsonObject();
			version = jo.get("version").getAsString();
			if (!version.equals(currentVersion))
				updateRequired = true;
			if (updateRequired) {
				name = jo.get("name").getAsString();
				JsonArray cl = jo.get("changelog").getAsJsonArray();
				for (JsonElement e : cl) {
					changeLog.add(e.getAsString());
				}
				if(jo.has("download"))
					downloadURL = jo.get("download").getAsString();
			}
		} catch (Exception e) {
			CJCore.logger.info("Error reading update url: " + url);
			CJCore.logger.catching(e);
			return;
		}
		if (updateRequired) {
			player.sendMessage(new TextComponentString(TextFormatting.WHITE
					+ I18n.format("update.ready", TextFormatting.GOLD + name + TextFormatting.WHITE)));
			player.sendMessage(new TextComponentString(TextFormatting.WHITE
					+ I18n.format("update.version", TextFormatting.DARK_RED + currentVersion + TextFormatting.WHITE,
							TextFormatting.DARK_GREEN + version)));
			ITextComponent changeLogAndVersion = new TextComponentString("");
			if (!Strings.isNullOrEmpty(downloadURL))
				changeLogAndVersion.appendSibling(new TextComponentString(TextFormatting.WHITE + "["
						+ TextFormatting.DARK_AQUA + I18n.format("update.download") + TextFormatting.WHITE + "] ")
								.setStyle(new Style()
										.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadURL))));
			if (changeLog.size() > 0) {
				StringBuilder changeLogString = new StringBuilder();
				for (String log : changeLog) {
					if (log.startsWith("="))
						changeLogString.append(TextFormatting.AQUA);
					else if (log.startsWith("-"))
						changeLogString.append(TextFormatting.RED);
					else if (log.startsWith("+"))
						changeLogString.append(TextFormatting.GREEN);
					else
						changeLogString.append(TextFormatting.WHITE);
					changeLogString.append(log).append("\n");
				}
				changeLogString = new StringBuilder(changeLogString.substring(0, changeLogString.length() - 1));
				changeLogAndVersion.appendSibling(new TextComponentString(
						"[" + TextFormatting.DARK_AQUA + I18n.format("update.changelog") + TextFormatting.WHITE + "]")
								.setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new TextComponentString(changeLogString.toString())))));
			}
			player.sendMessage(changeLogAndVersion);
		}
	}
}