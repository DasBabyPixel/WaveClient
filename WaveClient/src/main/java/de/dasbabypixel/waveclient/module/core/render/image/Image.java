package de.dasbabypixel.waveclient.module.core.render.image;

import de.dasbabypixel.waveclient.module.core.resource.IResource;
import de.dasbabypixel.waveclient.module.core.resource.texture.AbstractTexture;
import de.dasbabypixel.waveclient.module.core.resource.texture.TextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Image {

	private final AbstractTexture texture;

	public Image(ResourceLocation location) {
		this(TextureManager.getInstance().getTexture(location));
	}

	public Image(IResource resource) {
		this(TextureManager.getInstance().getTexture(resource));
	}

	public Image(AbstractTexture texture) {
		this.texture = texture;
	}

	public AbstractTexture getTexture() {
		return texture;
	}

	public void bind() {
		Minecraft.getMinecraft().addScheduledTask(texture::bind);
	}
	
	public boolean isCreated() {
		return texture.isCreated();
	}
}
