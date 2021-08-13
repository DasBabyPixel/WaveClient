package de.dasbabypixel.waveclient.module.core.gui.component;

import de.dasbabypixel.waveclient.wrappedloader.api.gui.Gui;

public abstract class AbstractGui implements Gui {

	private double w, h, x, y;

	public AbstractGui() {
		x = 0;
		y = 0;
		w = 0;
		h = 0;
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
		return w;
	}

	@Override
	public double getHeight() {
		return h;
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
	public void setWidth(double w) {
		this.w = w;
	}

	@Override
	public void setHeight(double h) {
		this.h = h;
	}

	@Override
	public void draw(double var1, double var3, float var5) {

	}

	@Override
	public void mouseClicked(double var1, double var3, int var5) {

	}

	@Override
	public void mouseReleased(double var1, double var3, int var5) {

	}

	@Override
	public void mouseDrag(double var1, double var3, int var5, long var6) {

	}

	@Override
	public void mouseMove(double var1, double var3, double var5, double var7) {

	}

	@Override
	public void keyTyped(char var1, int var2) {

	}

	@Override
	public void keyReleased(char var1, int var2) {

	}

	@Override
	public void onScroll(double delta) {

	}

	@Override
	public void onClose() {

	}

	@Override
	public void onOpen() {

	}

	@Override
	public void init() {

	}

	@Override
	public void update() {

	}
}
