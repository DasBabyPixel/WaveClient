package de.dasbabypixel.waveclient.module.core.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.dasbabypixel.waveclient.WaveClient;
import de.dasbabypixel.waveclient.module.Module;
import de.dasbabypixel.waveclient.module.ModuleClassLoader;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;

public class ModuleResourcePack implements IResourcePack {

	private final String prefix;
	private final Module module;
	private final ModuleClassLoader loader;
	private final Set<String> resourceNames = new HashSet<>();
	private final Map<String, IResource> resources = new ConcurrentHashMap<>();

	public ModuleResourcePack(Module module) {
		this("assets/", module);
	}

	public ModuleResourcePack(String prefix, Module module) {
		this.prefix = prefix == null ? "" : prefix;
		this.module = module;
		this.loader = module.getClassLoader();
		this.loader.getEntries().stream().filter(name -> name.startsWith(this.prefix)).forEach(this.resourceNames::add);
	}

	@Override
	public void reload() {
		this.resources.clear();
	}

	@Override
	public IResource getResource(String name) throws IOException {
		if (resourceNames.contains(name))
			return resources.containsKey(name) ? resources.get(name) : loadResource(name);
		throw new ResourcePackFileNotFoundException(WaveClient.getInstance()
				.getModuleManager()
				.getModulesFolder()
				.resolve(this.module.getEntry().getName())
				.toFile(), name);
	}

	private IResource loadResource(String name) throws IOException {
		ModuleResource resource = new ModuleResource(this.loader.getResourceAsStream(name));
		resources.put(name, resource);
		return resource;
	}

	@Override
	public boolean hasResource(String name) {
		return this.resourceNames.contains(name);
	}

	public class ModuleResource implements IResource {

		private final byte[] data;

		public ModuleResource(InputStream stream) throws IOException {
			BufferedInputStream in = new BufferedInputStream(stream);
			byte[] buffer = new byte[2048];
			List<Byte> bytes = new ArrayList<>();
			while (in.available() > 0) {
				int read = in.read(buffer);
				for (int i = 0; i < read; i++) {
					bytes.add(buffer[i]);
				}
			}
			in.close();
			stream.close();
			byte[] arr = new byte[bytes.size()];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = bytes.get(i);
			}
			this.data = arr;
		}

		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(this.data);
		}
	}
}
