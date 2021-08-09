package de.dasbabypixel.waveclient.module.core.resource.texture;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.client.Minecraft;

public abstract class AbstractTexture {

	private final AtomicReference<BufferedImage> image = new AtomicReference<>();

	public abstract AbstractTexture create();

	public abstract AbstractTexture bind();

	public abstract AbstractTexture delete();

	public final AbstractTexture uploadImage(BufferedImage image) {
		this.image.set(image);
		int[] rgb = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
		Minecraft.getMinecraft().addScheduledTask(() -> this.create().uploadImage(rgb, image.getWidth(), image.getHeight()));
		return this;
	}

	protected abstract AbstractTexture uploadImage(int[] rgb, int width, int height);

	public abstract boolean isCreated();

	public final BufferedImage currentImage() {
		return this.image.get();
	}
}
