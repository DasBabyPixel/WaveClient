package de.dasbabypixel.waveclient.module.core.util.color;

import com.google.common.primitives.Ints;

public class Color {

	public static final Color black = new Color(0, 0, 0, 255), white = new Color(255, 255, 255, 255);

	protected int red;
	protected int green;
	protected int blue;
	protected int alpha;

	public Color(int red, int green, int blue, int alpha) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}

	public Color(float red, float green, float blue, float alpha) {
		this((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
	}

	public Color(double red, double green, double blue, double alpha) {
		this((float) red, (float) green, (float) blue, (float) alpha);
	}

	public int getRed() {
		return red;
	}

	public int getAlpha() {
		return alpha;
	}

	public int getBlue() {
		return blue;
	}

	public int getGreen() {
		return green;
	}

	public float getRedFloat() {
		return getRed() / 255F;
	}

	public float getGreenFloat() {
		return getGreen() / 255F;
	}

	public float getBlueFloat() {
		return getBlue() / 255F;
	}

	public float getAlphaFloat() {
		return getAlpha() / 255F;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public void setAlpha(float alpha) {
		setAlpha((int) (alpha * 255));
	}

	public void setBlue(float blue) {
		setBlue((int) (blue * 255));
	}

	public void setGreen(float green) {
		setGreen((int) (green * 255));
	}

	public void setRed(float red) {
		setRed((int) (red * 255));
	}

	public void setAlpha(double alpha) {
		setAlpha((float) alpha);
	}

	public void setBlue(double blue) {
		setBlue((float) blue);
	}

	public void setGreen(double green) {
		setGreen((float) green);
	}

	public void setRed(double red) {
		setRed((float) red);
	}

	public static int getAlpha(int color) {
		return color >> 24 & 255;
	}

	public static int getRed(int color) {
		return color >> 16 & 255;
	}

	public static int getGreen(int color) {
		return color >> 8 & 255;
	}

	public static int getBlue(int color) {
		return color & 255;
	}

	public int toARGB() {
		return Ints.fromBytes((byte) getAlpha(), (byte) getRed(), (byte) getGreen(), (byte) getBlue());
	}

	public Color withAlpha(float alpha) {
		return withAlpha((int) alpha * 255);
	}

	public Color withAlpha(int alpha) {
		return withAlpha((byte) alpha);
	}
}
