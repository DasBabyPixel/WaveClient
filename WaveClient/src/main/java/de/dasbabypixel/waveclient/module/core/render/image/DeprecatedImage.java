package de.dasbabypixel.waveclient.module.core.render.image;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;

import de.dasbabypixel.waveclient.module.core.resource.DeprecatedResourcePack;
import de.dasbabypixel.waveclient.module.core.util.AccessTransformerField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

@Deprecated
public class DeprecatedImage {

	private final ResourceLocation location;
	private final int width, height;

	DeprecatedImage(DeprecatedResourcePack resourcePack, ResourceLocation location) {
		this.location = location;
		int width = 0, height = 0;
		try {
			InputStream stream = resourcePack.getInputStream(location);
			BufferedImage image = TextureUtil.readBufferedImage(stream);
			width = image.getWidth();
			height = image.getHeight();
			stream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.width = width;
		this.height = height;
		DeprecatedImages.imageList.add(this);
		Map<ResourceLocation, ITextureObject> textures = AccessTransformerField.TextureManager_mapTextureObjects
				.get(Minecraft.getMinecraft().getTextureManager());
		textures.remove(location);
		Minecraft.getMinecraft().getTextureManager().loadTexture(location, new SimpleTexture(location));
	}

	public void bind() {
		Minecraft.getMinecraft().renderEngine.bindTexture(location);
	}

	public int getHeight() {
		return height;
	}

	public ResourceLocation getLocation() {
		return location;
	}

	public int getWidth() {
		return width;
	}
}
