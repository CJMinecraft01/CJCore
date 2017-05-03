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

public class VersionChecker {

	public static String cjcoreURL = "https://raw.githubusercontent.com/CJMinecraft01/CJCore/master/update.json";

	public static void checkForUpdate(String url, String versionFieldClass, String versionFieldName,
			EntityPlayer player) {
		String version = "";
		String currentVersion = "";
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
			Class clazz = Class.forName(versionFieldClass);
			Field versionField = clazz.getDeclaredField(versionFieldName);
			if (version != versionField.get(clazz))
				updateRequired = true;
			currentVersion = (String) versionField.get(clazz);
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
			TextComponentString text = new TextComponentString(TextFormatting.RED + I18n.format("update.ready", name, version));
			text.setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadURL)));
			player.sendMessage(text);
			for (String log : changeLog) {
				text = new TextComponentString(TextFormatting.AQUA + "+ " + I18n.format(log));
				text.setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadURL)));
				player.sendMessage(text);
			}
		}
	}

}
