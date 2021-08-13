package de.dasbabypixel.waveclient.module.core.gui.api;

import de.dasbabypixel.waveclient.wrappedloader.api.render.DrawHelper;
import de.dasbabypixel.waveclient.wrappedloader.api.util.Color;

public class BorderGui extends AbstractGui implements GuiWrapper {

	protected double borderThickness = 2;
	private Color borderColor = new Color(50, 50, 50, 255);

	protected Gui gui;

	public BorderGui(Gui gui) {
		super(0, 0);
		this.gui = gui;
		if (gui != null) {
			setPosition(gui.getX() - borderThickness, gui.getY() - borderThickness);
		}
	}

	@Override
	public Gui getGui() {
		return gui;
	}

	@Override
	public void draw(double mouseX, double mouseY) {
		double x = getGuiX();
		double y = getGuiY();
		double width = getGuiWidth();
		double height = getGuiHeight();
		DrawHelper.drawRect(x - borderThickness, y - borderThickness, borderThickness, height + 2 * borderThickness,
				borderColor);
		DrawHelper.drawRect(x + width, y - borderThickness, borderThickness, height + 2 * borderThickness, borderColor);
		DrawHelper.drawRect(x, y - borderThickness, width, borderThickness, borderColor);
		DrawHelper.drawRect(x, y + height, width, borderThickness, borderColor);
		gui.draw(mouseX, mouseY);
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public double getBorderThickness() {
		return borderThickness;
	}

	public void setBorderThickness(double borderThickness) {
		this.borderThickness = borderThickness;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	@Override
	public boolean isHovering(double mouseX, double mouseY) {
		return gui.isHovering(mouseX, mouseY);
	}

	@Override
	public void onClick(double mouseX, double mouseY, int mouseButton) {
		gui.onClick(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onDrag(double mouseX, double mouseY, int mouseButton, long dragTime) {
		gui.onDrag(mouseX, mouseY, mouseButton, dragTime);
	}

	@Override
	public void onMove(double oldMouseX, double oldMouseY, double mouseX, double mouseY) {
		gui.onMove(oldMouseX, oldMouseY, mouseX, mouseY);
	}

	@Override
	public void onKeyTyped(char key, int keycode) {
		gui.onKeyTyped(key, keycode);
	}

	@Override
	public void tick() {
		gui.tick();
	}

	@Override
	public void onScroll(double delta) {
		gui.onScroll(delta);
	}

	@Override
	public void onRelease(double mouseX, double mouseY, int state) {
		gui.onRelease(mouseX, mouseY, state);
	}

	@Override
	public void setPressed(int button, boolean pressed) {
		gui.setPressed(button, pressed);
	}

	@Override
	public boolean isPressed(int button) {
		return gui.isPressed(button);
	}

	@Override
	public double getX() {
		return getGuiX() - borderThickness;
	}

	@Override
	public double getY() {
		return getGuiY() - borderThickness;
	}

	@Override
	public void setX(double x) {
		if (gui != null) {
			gui.setX(x + borderThickness);
		}
	}

	@Override
	public void setY(double y) {
		if (gui != null) {
			gui.setY(y + borderThickness);
		}
	}

	@Override
	public void onClose() {
		gui.onClose();
	}

	@Override
	public String toString() {
		return gui.toString();
	}

	@Override
	public int hashCode() {
		return gui.hashCode();
	}

	@Override
	public double getWidth() {
		return getGuiWidth() + 2 * borderThickness;
	}

	@Override
	public double getHeight() {
		return getGuiHeight() + 2 * borderThickness;
	}

	public double getGuiX() {
		return gui.getX();
	}

	public double getGuiY() {
		return gui.getY();
	}

	public double getGuiWidth() {
		return gui.getWidth();
	}

	public double getGuiHeight() {
		return gui.getHeight();
	}

	@Override
	public void setWidth(double width) {
		gui.setWidth(width - 2 * borderThickness);
	}

	@Override
	public void setHeight(double height) {
		gui.setHeight(height - 2 * borderThickness);
	}

}
