package de.dasbabypixel.waveclient.module.core.resource;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;

public interface IResourcePack {

	void reload();

	IResource getResource(String name) throws IOException;

	default IResource getResource(ResourceLocation location) throws IOException {
		return getResource(locationToName(location));
	}

	boolean hasResource(String name);

	default boolean hasResource(ResourceLocation location) {
		return hasResource(locationToName(location));
	}

	public static String locationToName(ResourceLocation location) {
		return String.format("%s/%s/%s", "assets", location.getResourceDomain(), location.getResourcePath());
	}
}
