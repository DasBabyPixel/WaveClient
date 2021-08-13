package de.dasbabypixel.waveclient.module.core.gui.util;

import java.util.Random;

import de.dasbabypixel.waveclient.module.core.util.color.RainbowColor;
import de.dasbabypixel.waveclient.wrappedloader.api.WaveClientAPI;
import de.dasbabypixel.waveclient.wrappedloader.api.render.DrawHelper;
import net.minecraft.client.Minecraft;

public class BackgroundGuiRainbow {
	private static final Random r = new Random();
	private static RainbowColor g1 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7)
			.setSpeed(0.916F);
	private static RainbowColor g2 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7)
			.setSpeed(1.215F);
	private static RainbowColor g3 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7)
			.setSpeed(1.015F);
	private static RainbowColor g4 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7)
			.setSpeed(0.816F);
	private static Minecraft mc = Minecraft.getMinecraft();

	public static void update() {
		g1.update();
		g2.update();
		g3.update();
		g4.update();
	}

	public static void draw() {
		DrawHelper.drawGradientRect(0, 0,
				mc.displayWidth / WaveClientAPI.getInstance().getScalingManager().getTrueScale(),
				mc.displayHeight / WaveClientAPI.getInstance().getScalingManager().getTrueScale(), g1, g2, g3, g4);
	}

	public static RainbowColor getCorner1() {
		return g1;
	}

	public static RainbowColor getCorner2() {
		return g2;
	}
}
