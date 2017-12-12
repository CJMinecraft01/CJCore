package cjminecraft.core.client.gui;

import java.util.List;

import cjminecraft.core.client.gui.element.ElementEnergyBar;
import cjminecraft.core.client.gui.overlay.OverlayBase;

/**
 * Represents an element which behaves differently when in an
 * {@link OverlayBase}. {@link ElementEnergyBar} is a good example
 * 
 * @author CJMinecraft
 *
 */
public interface ISpecialOverlayElement {

	/**
	 * Drawn on top of the foreground layer, will be the last bit rendered for
	 * this element
	 */
	void drawSpecialLayer();

	/**
	 * Add overlay text which will be drawn on the side of the
	 * {@link OverlayBase}
	 * 
	 * @param text
	 *            The overlay text to add to
	 */
	void addOverlayText(List<String> text);

}
