package de.dasbabypixel.waveclient.module.core.util.math;

public class MathHelper {

	public static int clamp(int min, int max, int val) {
		return Math.max(min, Math.min(max, val));
	}

	public static double clamp(double min, double max, double val) {
		return Math.max(min, Math.min(max, val));
	}

	public static float clamp(float min, float max, float val) {
		return Math.max(min, Math.min(max, val));
	}

	public static double signum(double delta) {
		return delta < 0 ? -1 : delta > 0 ? 1 : 0;
	}
}
