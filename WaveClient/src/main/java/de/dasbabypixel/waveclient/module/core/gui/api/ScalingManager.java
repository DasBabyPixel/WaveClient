package de.dasbabypixel.waveclient.module.core.gui.api;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class ScalingManager {
	private static final AtomicInteger scaleFactor = new AtomicInteger();
	private static final Stack<Scale> scales = new Stack<>();
	private static double currentScale = calculateScale();
	private static double trueScale = getMinecraftScaling();

	private static double calculateScale() {
		Scale scale = null;
		double d = 1.0;
		for (int i = scales.size() - 1; i >= 0;) {
			scale = scales.get(i);
			d *= scale.scale;
			i--;
			if (scale.type != Scale.Type.MULTIPLY) {
				break;
			}
		}
		return d;
	}

	public static double getTrueScale() {
		return trueScale;
	}

	public static double getScale() {
		return currentScale;
	}

	public static Scale getScaleObject() {
		return scales.isEmpty() ? null : scales.peek();
	}

	public static void pushScale(Scale scale) {
		if (!scales.isEmpty()) {
			GlStateManager.popMatrix();
		}
		scales.push(scale);
		scale0(scale);
	}

	private static void scale0(Scale scale) {
		setScaleVariables();
		GlStateManager.pushMatrix();
		final double s = getScale();
		GlStateManager.scale(s, s, s);
	}

	private static void setScaleVariables() {
		currentScale = calculateScale();
		trueScale = scales.isEmpty() ? getMinecraftScaling() : currentScale;
	}

	public static Scale popScale() {
		if (!scales.isEmpty()) {
			Scale scale = scales.pop();
			GlStateManager.popMatrix();
			if (!scales.isEmpty()) {
				scale0(scales.peek());
			}
			return scale;
		}
		return null;
	}

	public static double convertFromPixel(double in, double scale) {
		return in / scale;
	}

	public static double convertFromPixel(double in) {
		return convertFromPixel(in, getScale());
	}

	public static double convertToPixel(double in, double scale) {
		return in * scale;
	}

	public static double convertToPixel(double in) {
		return in * getScale();
	}

	public static double getPixelMouseX() {
		return Mouse.getX();
	}

	public static double getPixelMouseY() {
		return Minecraft.getMinecraft().displayHeight - Mouse.getY() - 1;
	}

	public static int getMinecraftScaling() {
		return scaleFactor.get();
	}

	public static void updateScaleFactor() {
		double old = scaleFactor.get();
		scaleFactor.set(new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor());
		if (old != scaleFactor.get()) {
			setScaleVariables();
		}
	}

	public static class Scale {
		public double scale;
		private Type type;

		public Scale(double scale, Type type) {
			this.scale = scale;
			this.type = type;
		}

		public Scale(double scale) {
			this(scale, Type.OVERRIDE);
		}

		public static enum Type {
			MULTIPLY, OVERRIDE
		}
	}
}
