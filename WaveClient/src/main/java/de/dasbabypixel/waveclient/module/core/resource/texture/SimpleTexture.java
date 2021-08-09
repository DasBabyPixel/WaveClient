package de.dasbabypixel.waveclient.module.core.resource.texture;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

public class SimpleTexture extends AbstractTexture {

	private AtomicBoolean created = new AtomicBoolean();
	private AtomicInteger textureId = new AtomicInteger();

	public SimpleTexture() {
	}

	@Override
	public AbstractTexture create() {
		if (created.compareAndSet(false, true)) {
			Minecraft.getMinecraft().addScheduledTask(() -> this.textureId.set(GlStateManager.generateTexture()));
		}
		return this;
	}

	@Override
	public AbstractTexture bind() {
		if (isCreated()) {
			Minecraft.getMinecraft().addScheduledTask(() -> GlStateManager.bindTexture(this.textureId.get()));
		}
		return this;
	}

	@Override
	public AbstractTexture delete() {
		if (created.compareAndSet(true, false)) {
			Minecraft.getMinecraft().addScheduledTask(() -> GlStateManager.deleteTexture(this.textureId.get()));
		}
		return this;
	}

	@Override
	public boolean isCreated() {
		return created.get();
	}

	@Override
	public AbstractTexture uploadImage(int[] rgb, int width, int height) {
		if (isCreated()) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TextureUtil.allocateTexture(this.textureId.get(), width, height);
				TextureUtil.uploadTexture(this.textureId.get(), rgb, width, height);
			});
		}
		return this;
	}
}
