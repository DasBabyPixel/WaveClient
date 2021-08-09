package de.dasbabypixel.waveclient.module.core.gui;

import de.dasbabypixel.waveclient.module.core.gui.api.AbstractGuiScreen;
import de.dasbabypixel.waveclient.module.core.gui.api.BackgroundGuiRainbow;
import de.dasbabypixel.waveclient.module.core.gui.api.BorderGui;
import de.dasbabypixel.waveclient.module.core.gui.api.ExitCross;
import de.dasbabypixel.waveclient.module.core.gui.api.ModuleGui;
import de.dasbabypixel.waveclient.module.core.gui.api.ScrollbarGui;
import de.dasbabypixel.waveclient.module.core.module.Module;
import de.dasbabypixel.waveclient.module.core.util.color.Color;

public class ModulesGui extends AbstractGuiScreen {

	private Color exitCrossBox = new Color(130, 130, 130, 150);
	private Color exitCrossColor = new Color(220, 0, 0, 255);
	private Color exitCrossHoverColor = new Color(160, 0, 0, 255);
	private ExitCross exitCross = new ExitCross();
	private BorderGui exitCrossBorder = new BorderGui(exitCross);
	private boolean exitClicked = false;

	public ModulesGui() {
	}

	@Override
	public void init() {
		this.GUIs.clear();
		boolean initialized = false;
		for (Module module : Module.root().getChilds()) {
			ModuleGui gui = module.getData().get(ModuleGui.DATA_KEY);
			if (!gui.isInitialized()) {
				gui.initialize();
				initialized = true;
			}
		}
		if (initialized) {
			this.reset();
		} else {
			this.addGUIs();
		}
	}

	private void addGUIs() {
		int width = 100;
		int height = 100;
		Module.root()
				.getChilds()
				.stream()
				.map(Module::getData)
				.map(d -> (ModuleGui) d.get(ModuleGui.DATA_KEY))
				.map(ScrollbarGui::new)
				.map(gui -> gui.setSize(width, height))
				.forEach(g -> this.GUIs.add(g));
	}

	public void reset() {
		this.GUIs.clear();
		final int space = 10;
		int x = space;
		int y = space;
		for (Module module : Module.root().getChilds()) {
			ModuleGui gui = module.getData().get(ModuleGui.DATA_KEY);
			gui.setPosition(x, y);
			x += space + width;
		}
		this.addGUIs();
	}

	@Override
	public void preDraw(double mouseX, double mouseY, float partialTicks) {
		BackgroundGuiRainbow.draw();
	}

	@Override
	public void draw(double mouseX, double mouseY) {
	}

	@Override
	public void postDraw(double mouseX, double mouseY, float partialTicks) {
		drawExitCross(mouseX, mouseY);
	}

	@Override
	public void onClick(double mouseX, double mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if (exitCross.isHovering(mouseX, mouseY)) {
				exitClicked = true;
			}
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if (exitClicked && exitCross.isHovering(mouseX, mouseY)) {
				mc.displayGuiScreen(null);
			} else {
				exitClicked = false;
			}
		}
	}

	private void drawExitCross(double mouseX, double mouseY) {
		double rectWidth = 20;
		double rectHeight = 20;
		double indent = 3;
		double width = getWidth();
		double sizeDiff = 6;
		double crossWidth = rectWidth - sizeDiff;
		double crossHeight = rectHeight - sizeDiff;
		double lineWidth = 2;

		double rectX = width - rectWidth - indent;
		double rectY = 0 + indent;
		double crossX = rectX + sizeDiff / 2;
		double crossY = rectY + sizeDiff / 2;
		int rectColor = exitCrossBox.toARGB();
		int crossColor = exitCrossColor.toARGB();
		int crossHoverColor = exitCrossHoverColor.toARGB();
		exitCross.setValues(rectX, rectY, rectWidth, rectHeight, rectColor, crossX, crossY, crossWidth, crossHeight,
				lineWidth, exitClicked ? crossHoverColor : crossColor, crossHoverColor);
		(exitClicked ? exitCrossBorder : exitCross).draw(mouseX, mouseY);
	}
}