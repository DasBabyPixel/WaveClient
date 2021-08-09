package de.dasbabypixel.waveclient.module.core.gui.api;

import java.util.function.Supplier;

public class Scaling {

	public static final Scaling PIXEL = new Scaling(() -> 1.0);
	public static final Scaling MINECRAFT = new Scaling(() -> (double) ScalingManager.getMinecraftScaling());

	private Supplier<Double> scale;

	public Scaling(Supplier<Double> scale) {
		this.scale = scale;
	}

	public Scaling multiply(double value) {
		return new Scaling(() -> scale.get() * value);
	}

	public double getScale() {
		return scale.get();
	}

	public static boolean isStaticScale(Scaling s) {
		return s == PIXEL || s == MINECRAFT;
	}

	public ScalingManager.Scale toScalingManagerScale(ScalingManager.Scale.Type type) {
		return new ScalingManager.Scale(getScale(), type);
	}

}
