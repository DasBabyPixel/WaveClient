package de.dasbabypixel.waveclient.module.core.resource;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.util.ResourceLocation;

public class ResourceManager {

	private static ResourceManager instance;

	private final Set<IResourcePack> resourcePacks = new CopyOnWriteArraySet<>();
	private final Map<ResourceLocation, IResource> resources = new ConcurrentHashMap<>();
	private final Set<IResourceUnloadListener> unloadListeners = new CopyOnWriteArraySet<>();

	private ResourceManager() {
	}

	/**
	 * First reloads all ResourcePacks and then clears the resource cache. This can
	 * be done in a seperate thread and all resources will be replaced once they are
	 * reloaded.
	 */
	public void reloadResources() {
		this.resourcePacks.forEach(IResourcePack::reload);
		this.resources.clear();
	}

	/**
	 * @param location
	 * @return the resource for the specified location, and if no resource was found
	 *         in the cache the resource is loaded by the first ResourcePack having
	 *         the resource name available
	 */
	public IResource getResource(ResourceLocation location) {
		try {
			if (this.resources.containsKey(location)) {
				return this.resources.get(location);
			}
			return loadResource(location);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @param location
	 * @return the unloaded resource, null if no resource for the specified location
	 *         is in the cache.
	 */
	public IResource unloadResource(ResourceLocation location) {
		IResource resource = this.resources.remove(location);
		if (resource != null) {
			this.unloadListeners.forEach(l -> l.handleUnload(resource));
		}
		return resource;
	}

	private IResource loadResource(ResourceLocation location) throws IOException {
		for (IResourcePack pack : this.resourcePacks) {
			if (pack.hasResource(location)) {
				IResource resource = pack.getResource(location);
				resources.put(location, resource);
				return resource;
			}
		}
		return null;
	}

	/**
	 * @param pack Adds the resource pack to the lookup.
	 */
	public void addResourcePack(IResourcePack pack) {
		this.resourcePacks.add(pack);
	}

	/**
	 * @param pack Removes the resource pack from the lookup. Resources loaded by
	 *             the pack that are present in the cache will not be unloaded. Use
	 *             {@link ResourceManager#unloadResource(ResourceLocation)} to
	 *             unload the resource
	 */
	public void removeResourcePack(IResourcePack pack) {
		this.resourcePacks.remove(pack);
	}

	/**
	 * Adds the specified listener to the unloadListeners.
	 * 
	 * @param listener
	 */
	public void addUnloadListener(IResourceUnloadListener listener) {
		this.unloadListeners.add(listener);
	}

	/**
	 * Removes the specified listener from the unloadListeners.
	 * 
	 * @param listener
	 */
	public void removeUnloadListener(IResourceUnloadListener listener) {
		this.unloadListeners.remove(listener);
	}

	public static ResourceManager getInstance() {
		return instance;
	}

	public static void load() {
		instance = new ResourceManager();
	}
}
