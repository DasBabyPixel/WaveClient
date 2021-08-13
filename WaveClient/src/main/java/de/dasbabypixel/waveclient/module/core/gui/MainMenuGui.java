package de.dasbabypixel.waveclient.module.core.gui;

import de.dasbabypixel.waveclient.module.core.gui.component.WaveButton;

public class MainMenuGui extends AbstractGuiScreen {

	@Override
	public void init() {
		GUIs.add(new WaveButton().setText("Single Player")
				.setSize(7)
				.setSize(280, 50)
				.setPosition(getWidth() / 2 - 280 / 2, 175));
		GUIs.add(new WaveButton().setText("Multiplayer")
				.setSize(7)
				.setSize(280, 50)
				.setPosition(getWidth() / 2 - 280 / 2, 235));
	}

	@Override
	public void doDraw(double mouseX, double mouseY, float partialTicks) {
		this.drawBackground();
	}
}
