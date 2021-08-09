package de.dasbabypixel.waveclient.module.core.util.color;

import java.util.Random;

public class RainbowColor extends Color {

	protected float speed = 1;
	protected float directionRed = 1;
	protected float directionGreen = 1;
	protected float directionBlue = 1;
	protected float red;
	protected float green;
	protected float blue;
	protected float alpha;

	private Random offset = new Random();

	public RainbowColor(double red, double green, double blue, double alpha) {
		super(red, green, blue, alpha);
	}

	public RainbowColor(float red, float green, float blue, float alpha) {
		super(red, green, blue, alpha);
	}

	public RainbowColor(int red, int green, int blue, int alpha) {
		super(red, green, blue, alpha);
	}

	public void update() {
		float speed = this.speed / 50;
		float sr = speed * directionRed;
		float sg = speed * directionGreen;
		float sb = speed * directionBlue;

		float nr = getRedFloat() + sr + nextOffset();
		float ng = getGreenFloat() + sg + nextOffset();
		float nb = getBlueFloat() + sb + nextOffset();

		if (nr >= 1 || nr <= 0) {
			nr = directionRed;
			directionRed = -directionRed;
		}
		if (ng >= 1 || ng <= 0) {
			ng = directionGreen;
			directionGreen = -directionGreen;
		}
		if (nb >= 1 || nb <= 0) {
			nb = directionBlue;
			directionBlue = -directionBlue;
		}
		setRed(nr);
		setGreen(ng);
		setBlue(nb);
	}

	protected float nextOffset() {
		return (offset.nextFloat() - 0.5F) / 150;
	}

	@Override
	public int getAlpha() {
		return (int) (alpha * 255);
	}

	@Override
	public int getBlue() {
		return (int) (blue * 255);
	}

	@Override
	public int getGreen() {
		return (int) (green * 255);
	}

	@Override
	public int getRed() {
		return (int) (red * 255);
	}

	@Override
	public float getRedFloat() {
		return red;
	}

	@Override
	public float getBlueFloat() {
		return blue;
	}

	@Override
	public float getAlphaFloat() {
		return alpha;
	}

	@Override
	public float getGreenFloat() {
		return green;
	}

	@Override
	public void setRed(int red) {
		setRed(red / 255F);
	}

	@Override
	public void setGreen(int green) {
		setGreen(green / 255F);
	}

	@Override
	public void setBlue(int blue) {
		setBlue(blue / 255F);
	}

	@Override
	public void setAlpha(int alpha) {
		setAlpha(alpha / 255F);
	}

	@Override
	public void setRed(float red) {
		this.red = clamp(red);
	}

	@Override
	public void setGreen(float green) {
		this.green = clamp(green);
	}

	@Override
	public void setBlue(float blue) {
		this.blue = clamp(blue);
	}

	@Override
	public void setAlpha(float alpha) {
		this.alpha = clamp(alpha);
	}

	public float getSpeed() {
		return speed;
	}

	public RainbowColor setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	protected static float clamp(float f) {
		return clamp(f, 0, 1);
	}

	protected static float clamp(float f, float min, float max) {
		return Math.min(max, Math.max(f, min));
	}
}
