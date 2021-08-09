package de.dasbabypixel.waveclient.module.core.render.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.dasbabypixel.waveclient.module.core.resource.DeprecatedResourcePack;
import de.dasbabypixel.waveclient.module.core.util.AccessTransformerField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

@Deprecated
public class DeprecatedImages {

	public static final Collection<DeprecatedImage> imageList = new ArrayList<>();

	public static DeprecatedImage wavelogo;
	public static DeprecatedImage background;
	public static DeprecatedImage defaultFont;
	public static DeprecatedImage button;
	public static DeprecatedImage button_hover;

	public static void load(DeprecatedResourcePack pack) {
		wavelogo = new DeprecatedImage(pack, new ResourceLocation("waveclient", "core/textures/wavelogo.png"));
		background = new DeprecatedImage(pack, new ResourceLocation("waveclient", "core/textures/background.png"));
		defaultFont = new DeprecatedImage(pack, new ResourceLocation("waveclient", "core/textures/customfont/ascii.png"));
		button = new DeprecatedImage(pack, new ResourceLocation("waveclient", "core/textures/button/button.png"));
		button_hover = new DeprecatedImage(pack, new ResourceLocation("waveclient", "core/textures/button/button_hover.png"));
	}

	public static void unloadImages() {
		Map<ResourceLocation, ITextureObject> textures = AccessTransformerField.TextureManager_mapTextureObjects
				.get(Minecraft.getMinecraft().getTextureManager());
		imageList.stream().map(DeprecatedImage::getLocation).forEach(location -> {
			Minecraft.getMinecraft().getTextureManager().deleteTexture(location);
			textures.remove(location);
		});
		imageList.clear();
	}
}
