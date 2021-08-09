package de.dasbabypixel.waveclient.module.core.gui;

import de.dasbabypixel.waveclient.module.core.gui.api.AbstractGuiScreen;
import de.dasbabypixel.waveclient.module.core.gui.api.WaveButton;

public class MainMenuGui extends AbstractGuiScreen {

	@Override
	public void init() {
		GUIs.add(new WaveButton().setText("HURENSOHN")
				.setSize(7)
				.setSize(280, 50)
				.setPosition(getWidth() / 2 - 280 / 2, 175));
		GUIs.add(new WaveButton().setText("Multiplayer")
				.setSize(7)
				.setSize(280, 50)
				.setPosition(getWidth() / 2 - 280 / 2, 235));
	}

	@Override
	public void draw(double mouseX, double mouseY) {
		this.drawDefaultBackground();
	}
}
