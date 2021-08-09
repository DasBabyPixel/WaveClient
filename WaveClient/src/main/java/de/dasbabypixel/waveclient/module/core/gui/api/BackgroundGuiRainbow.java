package de.dasbabypixel.waveclient.module.core.gui.api;

import java.util.Random;

import de.dasbabypixel.waveclient.module.core.render.DrawHelper;
import de.dasbabypixel.waveclient.module.core.util.color.RainbowColor;
import net.minecraft.client.Minecraft;

public class BackgroundGuiRainbow {
	private static final Random r = new Random();
	private static RainbowColor g1 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7).setSpeed(0.916F);
	private static RainbowColor g2 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7).setSpeed(1.215F);
	private static RainbowColor g3 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7).setSpeed(1.015F);
	private static RainbowColor g4 = new RainbowColor(r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.7).setSpeed(0.816F);
	private static Minecraft mc = Minecraft.getMinecraft();

	public static void update() {
		g1.update();
		g2.update();
		g3.update();
		g4.update();
	}

	public static void draw() {
//		DrawHelper.drawGradientRect(0, 0, mc.displayWidth / ScalingManager.getScale(),
//				mc.displayHeight / ScalingManager.getScale(), g1.toARGB(), g2.toARGB());
		DrawHelper.drawGradientRect(0, 0, mc.displayWidth / ScalingManager.getScale(),
				mc.displayHeight / ScalingManager.getScale(), g1.toARGB(), g2.toARGB(), g3.toARGB(), g4.toARGB());
	}

	public static RainbowColor getCorner1() {
		return g1;
	}

	public static RainbowColor getCorner2() {
		return g2;
	}
}
