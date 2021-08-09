package de.dasbabypixel.waveclient.module.core;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import de.dasbabypixel.waveclient.module.ModuleClassLoader;
import de.dasbabypixel.waveclient.module.core.gui.MainMenuGui;
import de.dasbabypixel.waveclient.module.core.gui.ModulesGui;
import de.dasbabypixel.waveclient.module.core.gui.api.ModuleGui;
import de.dasbabypixel.waveclient.module.core.keybind.Keybind;
import de.dasbabypixel.waveclient.module.core.listener.ESPListener;
import de.dasbabypixel.waveclient.module.core.listener.GuiUpdateListener;
import de.dasbabypixel.waveclient.module.core.listener.KeybindUpdateListener;
import de.dasbabypixel.waveclient.module.core.module.Module;
import de.dasbabypixel.waveclient.module.core.render.font.WaveFont;
import de.dasbabypixel.waveclient.module.core.render.image.Images;
import de.dasbabypixel.waveclient.module.core.resource.IResourcePack;
import de.dasbabypixel.waveclient.module.core.resource.ModuleResourcePack;
import de.dasbabypixel.waveclient.module.core.resource.ResourceManager;
import de.dasbabypixel.waveclient.module.core.resource.texture.TextureManager;
import de.dasbabypixel.waveclient.networking.common.ConnectionData;
import de.dasbabypixel.waveclient.updater.CustomServerUpdater;
import de.dasbabypixel.waveclient.updater.Updater;
import de.dasbabypixel.waveclient.updater.client.connection.Credentials;
import de.dasbabypixel.waveclient.updater.client.connection.DefaultLoginClientConnection;
import de.dasbabypixel.waveclient.updater.client.connection.InvalidLoginHandler;
import de.dasbabypixel.waveclient.updater.client.connection.LoginClientConnection;
import de.dasbabypixel.waveclient.updater.client.connection.LoginData;
import de.dasbabypixel.waveclient.updater.client.connection.SuccessLoginHandler;
import de.dasbabypixel.waveclient.updater.relocation.netty.handler.ssl.SslContextBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class CoreModule extends de.dasbabypixel.waveclient.module.Module {

	private static CoreModule instance;
	private GuiUpdateListener guiUpdateListener;
	private KeybindUpdateListener keybindUpdateListener;
	private ESPListener espListener;
	private ModuleGui.Listener moduleGuiListener;
	private IResourcePack resourcePack;
//	private DeprecatedResourcePack resourcePack;

	public CoreModule() {
		super();
		instance = this;
	}

	@Override
	public void onDisableAsync() {

		Module.ModuleFile.save();
		this.keybindUpdateListener.unregister();
		this.guiUpdateListener.unregister();
		this.espListener.unregister();
		this.moduleGuiListener.unregister();

		ResourceManager.getInstance().removeResourcePack(resourcePack);
		TextureManager.getInstance().close();
		WaveFont.unloadFonts();
	}

	@Override
	public void onDisableSync() {
	}

	@Override
	public void onEnableAsync() {
		this.keybindUpdateListener = new KeybindUpdateListener();
		this.guiUpdateListener = new GuiUpdateListener();
		this.espListener = new ESPListener();
		this.moduleGuiListener = new ModuleGui.Listener();
		try {
			System.out.println("aasdadhbfoidsfbakfigzbaoirgbroia oivh srivb");

			System.out.println("aasdadhbfoidsfbakfigzbaoirgbroia oivh srivb");
			System.out.println("aasdadhbfoidsfbakfigzbaoirgbroia oivh srivb");
			this.keybindUpdateListener.register();
			this.guiUpdateListener.register();
			this.espListener.register();
			this.moduleGuiListener.register();
			this.resourcePack = new ModuleResourcePack(this);
			ResourceManager.load();
			ResourceManager.getInstance().addResourcePack(resourcePack);
			TextureManager.load();
			WaveFont.loadFonts();
			Images.loadImages();

			Keybind keybind = new Keybind(this, "gui.modules", Keyboard.KEY_L);
			keybind.registerHandler(new Keybind.KeybindHandler(bind -> {
				if (Minecraft.getMinecraft().currentScreen == null)
					Minecraft.getMinecraft().displayGuiScreen(new ModulesGui());
			}, Keybind.KeybindHandler.Type.ON_PRESS));

			new Keybind(this, "mainmenu", Keyboard.KEY_O).registerHandler(new Keybind.KeybindHandler(bind -> {
				if (Minecraft.getMinecraft().currentScreen == null)
					Minecraft.getMinecraft().displayGuiScreen(new MainMenuGui());
			}, Keybind.KeybindHandler.Type.ON_PRESS));

			Module.ModuleFile.load();

			Module.root().addChild("esp", () -> new Module("esp"));

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public Updater createUpdater() {
		try {
			InetAddress address = null;
			try {
				address = InetAddress.getByName("updater.wavemod.de");
				if (!address.isReachable(5000)) {
					throw new UnknownHostException();
				}
			} catch (UnknownHostException ex) {
				address = InetAddress.getLocalHost();
			}
			LoginClientConnection connection = new DefaultLoginClientConnection(SslContextBuilder.forClient()
					.trustManager(
							de.dasbabypixel.waveclient.updater.relocation.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE)
					.build(), new InetSocketAddress(address, ConnectionData.PORT));
			connection.setAutoReconnect(true);
			connection.connect();
			connection.login(new Credentials(null, null), new SuccessLoginHandler() {
				@Override
				public void loginSuccess(LoginData data) {
					System.out.println("Logged in!");

				}
			}, new InvalidLoginHandler() {
				@Override
				public void denyLogin(LoginData data, Reason reason) {
					System.out.println("Login denied: " + reason);
				}
			});
			return new CustomServerUpdater("core", "core", this.getEntry(), connection, 5000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void onEnableSync() {
//		DeprecatedImages.load(this.resourcePack);
		recreateGuiScreen();
	}

	private void recreateGuiScreen() {
		GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
		if (guiScreen != null) {
			Class<?> screenClass = guiScreen.getClass();
			ModuleClassLoader mcl = this.getClassLoader();
			if (mcl == null) {
				return;
			}
			Collection<String> classNames = mcl.classes.stream().map(Class::getName).collect(Collectors.toList());
			if (classNames.contains(screenClass.getName())) {
				try {
					Class<?> clazz = mcl.classes.stream()
							.filter(c -> c.getName().equals(screenClass.getName()))
							.findAny()
							.orElse(null);
					if (clazz == null) {
						getLogger().warn("Missing GuiScreen class(" + screenClass.getName() + ") -> Closing screen");
						Minecraft.getMinecraft().displayGuiScreen(null);
						return;
					}
					Constructor<?> cons = null;
					try {
						cons = clazz.getConstructor();
					} catch (Exception e) {
						getLogger().warn("Missing GuiScreen Constructor: Please create an empty Constructor for Class: "
								+ clazz.getName());
						Minecraft.getMinecraft().displayGuiScreen(null);
						return;
					}
					cons.setAccessible(true);
					Object oscreen = cons.newInstance();
					if (!(oscreen instanceof GuiScreen)) {
						getLogger().warn("New GuiScreen Class does not extend GuiScreen: " + clazz.getName());
						Minecraft.getMinecraft().displayGuiScreen(null);
						return;
					}
					GuiScreen screen = (GuiScreen) oscreen;
					Minecraft.getMinecraft().displayGuiScreen(screen);
					getLogger().info("Recreated GuiScreen! " + clazz.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static CoreModule getInstance() {
		return instance;
	}
}
