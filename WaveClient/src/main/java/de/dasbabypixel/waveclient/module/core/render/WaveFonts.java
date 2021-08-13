package de.dasbabypixel.waveclient.module.core.render;

import de.dasbabypixel.waveclient.wrappedloader.api.WaveClientAPI;
import de.dasbabypixel.waveclient.wrappedloader.api.render.font.WaveFont;
import de.dasbabypixel.waveclient.wrappedloader.api.resource.ResourcePath;

public class WaveFonts {

	public static final WaveFont cinzel_regular = WaveClientAPI.getInstance()
			.getFontManager()
			.getFont(new ResourcePath("waveclient", "core/fonts/cinzel_regular.ttf"));

}
