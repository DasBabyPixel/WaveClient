package de.dasbabypixel.waveclient.module.core.gui.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;

public abstract class AbstractGui implements Gui {

	private double x, y, width, height;
	private Map<Integer, Boolean> pressed = new HashMap<>();
	protected final Minecraft mc = Minecraft.getMinecraft();

	public AbstractGui() {
		this(0, 0);
	}

	public AbstractGui(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public boolean isPressed(int button) {
		return pressed.getOrDefault(button, false);
	}

	@Override
	public void setPressed(int button, boolean pressed) {
		this.pressed.put(button, pressed);
	}

}
