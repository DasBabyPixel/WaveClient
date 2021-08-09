package de.dasbabypixel.waveclient.module.core.resource;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import de.dasbabypixel.waveclient.WaveClient;
import de.dasbabypixel.waveclient.module.Module;
import de.dasbabypixel.waveclient.module.ModuleClassLoader;
import de.dasbabypixel.waveclient.module.core.util.AccessTransformerField;
import jline.internal.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class DeprecatedResourcePack implements IResourcePack, Closeable {

	private final Module module;
	private final PackFile packFile;
	private final ModuleClassLoader loader;

	public DeprecatedResourcePack(Module module) {
		this(module, new PackFile(module));
	}

	public DeprecatedResourcePack(Module module, PackFile packFile) {
		this.packFile = packFile;
		this.module = module;
		this.loader = packFile.loader;
	}

	public void loadToMinecraft() {
		SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager();
		manager.reloadResourcePack(this);
	}

	@Override
	public void close() throws IOException {
		SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager();
		Map<String, FallbackResourceManager> domainResourceManagers = AccessTransformerField.SimpleReloadableResourceManager_domainResourceManagers
				.get(manager);
		Set<String> setResourceDomains = AccessTransformerField.SimpleReloadableResourceManager_setResourceDomains
				.get(manager);
		for (String domain : this.getResourceDomains()) {
			FallbackResourceManager rm = domainResourceManagers.get(domain);
			if (rm != null) {
				List<IResourcePack> resourcePacks = AccessTransformerField.FallbackResourceManager_resourcePacks
						.get(rm);
				resourcePacks.remove(this);
				if (resourcePacks.isEmpty()) {
					domainResourceManagers.remove(domain);
					rm = null;
				}
			}
			if (rm == null) {
				setResourceDomains.remove(domain);
			}
		}
	}

	public Module getModule() {
		return module;
	}

	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException {
		return getInputStreamByName(locationToName(location));
	}

	@Override
	public boolean resourceExists(ResourceLocation location) {
		return packFile.resources.contains(locationToName(location));
	}

	@Override
	public Set<String> getResourceDomains() {
		return packFile.resourceDomains;
	}

	@Override
	public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_)
			throws IOException {
		InputStream stream = this.getInputStreamByName("pack.mcmeta");
		T t = readMetadata(p_135058_1_, stream, p_135058_2_);
		IOUtils.closeQuietly(stream);
		return t;
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		InputStream stream = this.getInputStreamByName("pack.png");
		BufferedImage image = TextureUtil.readBufferedImage(stream);
		IOUtils.closeQuietly(stream);
		return image;
	}

	@Override
	public String getPackName() {
		return module.getName();
	}

	public InputStream getInputStreamByName(String name) throws IOException {
		InputStream stream = loader.getResourceAsStream(name);
		if (stream == null) {
			throw new ResourcePackFileNotFoundException(
					WaveClient.getInstance().getModuleManager().getModulesFolder().resolve(module.getName()).toFile(),
					name);
		}
		return stream;
	}

	public String locationToName(ResourceLocation location) {
		return String.format("%s/%s/%s", "assets", location.getResourceDomain(), location.getResourcePath());
	}

	public static <T extends IMetadataSection> T readMetadata(IMetadataSerializer serializer, InputStream stream,
			String name) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
			IOUtils.closeQuietly(reader);
			return serializer.parseMetadataSection(name, object);
		} catch (Throwable ex) {
			throw new JsonParseException(name);
		}
	}

	public static class PackFile {
		public static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
		public final Set<String> resources = new HashSet<>();
		public final Set<String> resourceDomains = new HashSet<>();
		public final ModuleClassLoader loader;

		public PackFile(Module module) {
			this(module.getClassLoader());
		}

		public PackFile(ModuleClassLoader loader) {
			this.loader = loader;
			this.resources.addAll(loader.getEntries()
					.stream()
					.filter(name -> name.startsWith("assets/"))
					.collect(Collectors.toList()));
			for (String name : this.resources) {
				List<String> args = Lists.newArrayList(entryNameSplitter.split(name));
				if (args.size() > 1) {
					String e = args.get(1);
					this.resourceDomains.add(e);
				}
			}
		}
	}
}
