package de.dasbabypixel.waveclient.module.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import de.dasbabypixel.waveclient.module.core.gui.MainMenuGui;
import de.dasbabypixel.waveclient.module.core.listener.GuiUpdateListener;
import de.dasbabypixel.waveclient.module.core.listener.KeybindUpdateListener;
import de.dasbabypixel.waveclient.module.core.util.Keybind;
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
import de.dasbabypixel.waveclient.wrappedloader.api.WaveClientAPI;
import de.dasbabypixel.waveclient.wrappedloader.api.resource.ModuleResourcePack;
import de.dasbabypixel.waveclient.wrappedloader.api.resource.ResourcePack;
import de.dasbabypixel.waveclient.wrappedloader.api.util.Keyboard;

public class CoreModule extends de.dasbabypixel.waveclient.module.Module {

	private static CoreModule instance;
	private GuiUpdateListener guiUpdateListener;
	private KeybindUpdateListener keybindUpdateListener;
	private ResourcePack resourcePack;

	public CoreModule() {
		super();
		instance = this;
	}

	@Override
	public void onDisableAsync() {
		this.keybindUpdateListener.unregister();
		this.guiUpdateListener.unregister();
	}

	@Override
	public void onDisableSync() {
	}

	@Override
	public void onEnableAsync() {
		this.resourcePack = new ModuleResourcePack(this);
		WaveClientAPI.getInstance().getResourceManager().addResourcePack(resourcePack);
		this.keybindUpdateListener = new KeybindUpdateListener();
		this.guiUpdateListener = new GuiUpdateListener();
		try {
			System.out.println("aasdadhbfoidsfbakfigzbaoirgbroia oivh srivb");
			this.keybindUpdateListener.register();
			this.guiUpdateListener.register();

			new Keybind(this, "mainmenu", Keyboard.KEY_O).registerHandler(new Keybind.KeybindHandler(bind -> {
				if (WaveClientAPI.getInstance().getMinecraft().getCurrentScreen() == null) {
					WaveClientAPI.getInstance().getMinecraft().displayGuiScreen(new MainMenuGui());
				}
			}, Keybind.KeybindHandler.Type.ON_PRESS));

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
//		recreateGuiScreen();
	}

	/*
	 * private void recreateGuiScreen() { GuiScreen guiScreen =
	 * Minecraft.getMinecraft().currentScreen; if (guiScreen != null) { Class<?>
	 * screenClass = guiScreen.getClass(); ModuleClassLoader mcl =
	 * this.getClassLoader(); if (mcl == null) { return; } Collection<String>
	 * classNames =
	 * mcl.classes.stream().map(Class::getName).collect(Collectors.toList()); if
	 * (classNames.contains(screenClass.getName())) { try { Class<?> clazz =
	 * mcl.classes.stream() .filter(c -> c.getName().equals(screenClass.getName()))
	 * .findAny() .orElse(null); if (clazz == null) {
	 * getLogger().warn("Missing GuiScreen class(" + screenClass.getName() +
	 * ") -> Closing screen"); Minecraft.getMinecraft().displayGuiScreen(null);
	 * return; } Constructor<?> cons = null; try { cons = clazz.getConstructor(); }
	 * catch (Exception e) { getLogger().
	 * warn("Missing GuiScreen Constructor: Please create an empty Constructor for Class: "
	 * + clazz.getName()); Minecraft.getMinecraft().displayGuiScreen(null); return;
	 * } cons.setAccessible(true); Object oscreen = cons.newInstance(); if
	 * (!(oscreen instanceof GuiScreen)) {
	 * getLogger().warn("New GuiScreen Class does not extend GuiScreen: " +
	 * clazz.getName()); Minecraft.getMinecraft().displayGuiScreen(null); return; }
	 * GuiScreen screen = (GuiScreen) oscreen;
	 * Minecraft.getMinecraft().displayGuiScreen(screen);
	 * getLogger().info("Recreated GuiScreen! " + clazz.getName()); } catch
	 * (Exception e) { e.printStackTrace(); } } } }
	 */

	public static CoreModule getInstance() {
		return instance;
	}
}
