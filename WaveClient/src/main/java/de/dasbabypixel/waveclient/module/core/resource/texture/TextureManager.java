package de.dasbabypixel.waveclient.module.core.resource.texture;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFutureTask;

import de.dasbabypixel.waveclient.module.core.resource.IResource;
import de.dasbabypixel.waveclient.module.core.resource.IResourceUnloadListener;
import de.dasbabypixel.waveclient.module.core.resource.ResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class TextureManager implements IResourceUnloadListener {

	private static TextureManager instance;
	private final ResourceManager resourceManager;
	private final Lock lock = new ReentrantLock();
	private final BiMap<IResource, AbstractTexture> textures = Maps.synchronizedBiMap(HashBiMap.create());
	private final BiMap<AbstractTexture, Future<?>> futures = Maps.synchronizedBiMap(HashBiMap.create());
	private final ExecutorService executor;

	private TextureManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		this.resourceManager.addUnloadListener(this);
		this.executor = Executors.newWorkStealingPool();
	}

	public AbstractTexture getTexture(ResourceLocation location) {
		return getTexture(ResourceManager.getInstance().getResource(location));
	}

	public AbstractTexture getTexture(IResource resource) {
		if (resource == null) {
			return null;
		}
		lock.lock();
		if (textures.containsKey(resource)) {
			AbstractTexture texture = textures.get(resource);
			lock.unlock();
			return texture;
		}
		final SimpleTexture texture = new SimpleTexture();
		textures.put(resource, texture);
		futures.put(texture, executor.submit(() -> {
			try {
				ImageInputStream in = ImageIO.createImageInputStream(resource.getInputStream());
				BufferedImage image = ImageIO.read(in);
				if (image == null)
					in.close();
				// This will automatically execute on the main thread if it is already on it.
				texture.uploadImage(image);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}));
		lock.unlock();
		return texture;
	}

	public AbstractTexture getTextureNow(IResource resource) {
		AbstractTexture texture = null;
		lock.lock();
		if (textures.containsKey(resource)) {
			texture = textures.get(resource);
		}
		if (texture != null) {
			if (texture.isCreated()) {
				lock.unlock();
				return texture;
			}
			Future<?> future = futures.get(texture);
			future.cancel(true);
		}
		AtomicReference<AbstractTexture> tex = new AtomicReference<>();
		ListenableFutureTask<?> future = ListenableFutureTask.create(() -> {
			try {
				ImageInputStream in = ImageIO.createImageInputStream(resource.getInputStream());
				BufferedImage image = ImageIO.read(in);
				if (image == null)
					in.close();
				tex.get().uploadImage(image);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}, null);
		if (texture == null) {
			texture = new SimpleTexture();
			textures.put(resource, texture);
		}
		futures.put(texture, future);
		lock.unlock();
		tex.set(texture);
		try {
			future.run();
		} catch (Throwable ex2) {
			ex2.printStackTrace();
		}
		return texture;
	}

	public void unloadTexture(IResource resource) {
		lock.lock();
		if (this.textures.containsKey(resource)) {
			AbstractTexture texture = this.textures.get(resource);
			lock.unlock();
			this.unloadTexture(texture);
			return;
		}
		lock.unlock();
	}

	public void unloadTexture(AbstractTexture texture) {
		lock.lock();
		if (this.textures.containsValue(texture)) {
			this.textures.inverse().remove(texture);
			Minecraft.getMinecraft().addScheduledTask(texture::delete);
		}
		lock.unlock();
	}

	@Override
	public void handleUnload(IResource resource) {
		this.unloadTexture(resource);
	}

	public void close() {
		executor.shutdownNow();
		lock.lock();
		this.textures.values().forEach(tex -> Minecraft.getMinecraft().addScheduledTask(tex::delete));
		lock.unlock();
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public static TextureManager getInstance() {
		return instance;
	}

	public static void load() {
		instance = new TextureManager(ResourceManager.getInstance());
	}
}
