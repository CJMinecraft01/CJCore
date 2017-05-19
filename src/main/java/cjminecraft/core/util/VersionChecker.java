package cjminecraft.core.util;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import cjminecraft.core.CJCore;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

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
	public static String cjcoreURL = "https://raw.githubusercontent.com/CJMinecraft01/CJCore/master/update.json";

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
	 * @param currentVersion
	 *            The current mod version
	 * @param player
	 *            The player to send the messages to
	 */
	public static void checkForUpdate(String url, String currentVersion, EntityPlayer player) {
		String version = "";
		String name = "";
		List<String> changeLog = new ArrayList<String>();
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
			player.sendMessage(new TextComponentString(TextFormatting.WHITE + "[" + TextFormatting.DARK_AQUA
					+ I18n.format("update.download") + TextFormatting.WHITE + "]").setStyle(
							new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadURL))));
			for (String log : changeLog) {
				player.sendMessage(new TextComponentString(TextFormatting.AQUA + log));
			}

		}
	}

}
