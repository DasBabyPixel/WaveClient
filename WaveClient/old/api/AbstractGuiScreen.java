package de.dasbabypixel.waveclient.module.core.gui.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.dasbabypixel.waveclient.module.core.listener.BaseListener;
import de.dasbabypixel.waveclient.module.core.render.Images;
import de.dasbabypixel.waveclient.wrappedloader.api.WaveClientAPI;
import de.dasbabypixel.waveclient.wrappedloader.api.event.events.gui.GuiOpenEvent;
import de.dasbabypixel.waveclient.wrappedloader.api.gui.minecraft.GuiMainMenu;
import de.dasbabypixel.waveclient.wrappedloader.api.render.DrawHelper;
import de.dasbabypixel.waveclient.wrappedloader.api.render.ScalingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class AbstractGuiScreen extends GuiScreen implements Gui {

	private static Stack<AbstractGuiScreen> guiScreens = new Stack<>();
	public static boolean otherGuiOpens = false;

	public Collection<Gui> GUIs = new ArrayList<>();
	private Collection<Integer> down = new HashSet<>();
	private Map<Integer, Boolean> pressed = new HashMap<>();

	private int touchValue;
	private int eventButton;
	private int firstDragButton = -1;
	private long lastMouseEvent;
	private double oldMouseX;
	private double oldMouseY;

	private boolean isOpened = false;

	@Override
	public void initGui() {
		otherGuiOpens = false;
		if (!isOpened) {
			isOpened = true;
			if (guiScreens.isEmpty() || guiScreens.peek() != this) {
				guiScreens.push(this);
			}
		}
		try {
			GUIs.clear();
			this.init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onGuiClosed() {
		if (isOpened) {
			isOpened = false;
			if (!guiScreens.isEmpty()) {
				if (guiScreens.peek() == this) {
					if (!otherGuiOpens) {
						guiScreens.pop();
					}
				}
			}
			try {
				this.onClose();
				for (Gui gui : GUIs) {
					gui.onClose();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean isOpened() {
		return isOpened;
	}

	@Override
	public void draw(double mouseX, double mouseY) {
	}

	protected void enableScaling() {
		double scale = 1.0 / WaveClientAPI.getInstance().getScalingManager().getMinecraftScaling();
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		WaveClientAPI.getInstance().getScalingManager().pushScale(getScaling());
	}

	protected ScalingManager.Scale getScaling() {
//		return Scaling.MINECRAFT.toScalingManagerScale(Type.OVERRIDE);
		return new ScalingManager.Scale(WaveClientAPI.getInstance().getScalingManager().getMinecraftScaling(),
				ScalingManager.Scale.Type.OVERRIDE);
	}

	protected void disableScaling() {
		WaveClientAPI.getInstance().getScalingManager().popScale();
		GlStateManager.popMatrix();
	}

	@Override
	public void drawBackground(int tint) {
		DrawHelper.drawImageToArea(Images.background, 0, 0, getWidth(), getHeight());
	}

	@Override
	public void drawDefaultBackground() {
		this.drawBackground(0);
	}

	@Override
	public void drawScreen(int unused1, int unused2, float partialTicks) {
		enableScaling();
		double pixelMouseX = WaveClientAPI.getInstance().getScalingManager().getPixelMouseX();
		double pixelMouseY = WaveClientAPI.getInstance().getScalingManager().getPixelMouseY();
		double mouseX = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseX);
		double mouseY = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseY);
		try {
			this.tick();
			this.preDraw(mouseX, mouseY, partialTicks);
			this.draw(mouseX, mouseY);
			doForGUIs(gui -> {
				gui.tick();
				gui.draw(mouseX, mouseY);
			});
			this.postDraw(mouseX, mouseY, partialTicks);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		disableScaling();
	}

	@Override
	protected final void mouseClicked(int unusedMouseX, int unusedMouseY, int mouseButton) throws IOException {
		enableScaling();
		double pixelMouseX = WaveClientAPI.getInstance().getScalingManager().getPixelMouseX();
		double pixelMouseY = WaveClientAPI.getInstance().getScalingManager().getPixelMouseY();
		double mouseX = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseX);
		double mouseY = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseY);
		this.onClick(mouseX, mouseY, mouseButton);
		try {
			if (down.contains(mouseButton)) {
				mouseReleased(0, 0, mouseButton);
			}
			down.add(mouseButton);
			if (mouseClick(mouseX, mouseY, mouseButton)) {
				doForGUIs(gui -> {
					if (gui.isHovering(mouseX, mouseY)) {
						gui.onClick(mouseX, mouseY, mouseButton);
						gui.setPressed(mouseButton, true);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		disableScaling();
	}

	protected final void mouseScrolled(double delta) {
		enableScaling();
		this.onScroll(delta);
		try {
			doForGUIs(gui -> {
				gui.onScroll(delta);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		disableScaling();
	}

	@Override
	public void handleMouseInput() throws IOException {
		int delta = Mouse.getEventDWheel();
		if (delta != 0) {
			this.mouseScrolled(delta);
		}

		double pixelMouseX = WaveClientAPI.getInstance().getScalingManager().getPixelMouseX();
		double pixelMouseY = WaveClientAPI.getInstance().getScalingManager().getPixelMouseY();
		double mouseX = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseX);
		double mouseY = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseY);
		int button = Mouse.getEventButton();

		double diffX = oldMouseX - mouseX;
		double diffY = oldMouseY - mouseY;
		if (diffX != 0 || diffY != 0) {
			mouseMove(mouseX, mouseY, oldMouseX, oldMouseY);
		}

		oldMouseX = mouseX;
		oldMouseY = mouseY;
		if (Mouse.getEventButtonState()) {
			if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
				return;
			}

			this.eventButton = button;
			this.lastMouseEvent = Minecraft.getSystemTime();
			this.mouseClicked(0, 0, this.eventButton);
		} else if (button != -1) {
			if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
				return;
			}

			this.eventButton = -1;
			if (firstDragButton == button) {
				firstDragButton = -1;
			}
			this.mouseReleased(0, 0, button);
		} else if ((this.eventButton != -1 && this.lastMouseEvent > 0L) || firstDragButton != -1) {
			if (firstDragButton == -1) {
				firstDragButton = eventButton;
			}
			long l = Minecraft.getSystemTime() - this.lastMouseEvent;
			this.mouseClickMove(0, 0, firstDragButton, l);
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		if (Keyboard.getEventKeyState()) {
			this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
		} else {
			this.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey());
		}

		this.mc.dispatchKeypresses();
	}

	@Override
	public void handleInput() throws IOException {
		if (Mouse.isCreated()) {
			while (Mouse.next()) {
				if (net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre(this)))
					continue;
				this.handleMouseInput();
				if (this.equals(this.mc.currentScreen))
					net.minecraftforge.common.MinecraftForge.EVENT_BUS
							.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post(this));
			}
		}

		if (Keyboard.isCreated()) {
			while (Keyboard.next()) {
				if (net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this)))
					continue;
				this.handleKeyboardInput();
				if (this.equals(this.mc.currentScreen))
					net.minecraftforge.common.MinecraftForge.EVENT_BUS
							.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
			}
		}
	}

	protected final void mouseMove(double unusedMouseX, double unusedMouseY, double oldMouseX, double oldMouseY) {
		enableScaling();
		double pixelMouseX = WaveClientAPI.getInstance().getScalingManager().getPixelMouseX();
		double pixelMouseY = WaveClientAPI.getInstance().getScalingManager().getPixelMouseY();
		double mouseX = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseX);
		double mouseY = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseY);
		this.onMove(oldMouseX, oldMouseY, mouseX, mouseY);
		try {
			doForGUIs(gui -> {
				gui.onMove(oldMouseX, oldMouseY, mouseX, mouseY);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		disableScaling();
	}

	protected final void keyReleased(char typedChar, int keyCode) {
		try {
			this.onKeyReleased(typedChar, keyCode);
			doForGUIs(gui -> {
				gui.onKeyReleased(typedChar, keyCode);
			});
			checkExitKeys(keyCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected final void keyTyped(char typedChar, int keyCode) throws IOException {
		try {
			this.onKeyTyped(typedChar, keyCode);
			doForGUIs(gui -> {
				gui.onKeyTyped(typedChar, keyCode);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void checkExitKeys(int keyCode) {
//		if (KeybindEntry.wasModified) {
//			KeybindEntry.wasModified = false;
//			return;
//		}
		if (keyCode == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen(null);

			if (this.mc.currentScreen == null) {
				this.mc.setIngameFocus();
			}
		}
	}

	@Override
	protected final void mouseReleased(int unusedMouseX, int unusedMouseY, int state) {
		enableScaling();
		double pixelMouseX = WaveClientAPI.getInstance().getScalingManager().getPixelMouseX();
		double pixelMouseY = WaveClientAPI.getInstance().getScalingManager().getPixelMouseY();
		double mouseX = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseX);
		double mouseY = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseY);
		this.onRelease(mouseX, mouseY, state);
		try {
			if (down.contains(state)) {
				down.remove(state);
				if (mouseRelease(mouseX, mouseY, state)) {
					doForGUIs(gui -> {
						if (gui.isPressed(state)) {
							gui.onRelease(mouseX, mouseY, state);
							gui.setPressed(state, false);
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		disableScaling();
	}

	@Override
	protected final void mouseClickMove(int unusedMouseX, int unusedMouseY, int clickedMouseButton,
			long timeSinceLastClick) {
		enableScaling();
		double pixelMouseX = WaveClientAPI.getInstance().getScalingManager().getPixelMouseX();
		double pixelMouseY = WaveClientAPI.getInstance().getScalingManager().getPixelMouseY();
		double mouseX = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseX);
		double mouseY = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(pixelMouseY);
		this.onDrag(pixelMouseX, pixelMouseY, clickedMouseButton, timeSinceLastClick);
		try {
			if (mouseDrag(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
				doForGUIs(b -> {
					if (b.isPressed(clickedMouseButton)) {
						b.onDrag(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		disableScaling();
	}

	public void postDraw(double mouseX, double mouseY, float partialTicks) {
	}

	public void preDraw(double mouseX, double mouseY, float partialTicks) {
	}

	@Override
	public double getX() {
		return 0;
	}

	@Override
	public double getY() {
		return 0;
	}

	@Override
	public void setX(double X) {
	}

	@Override
	public void setY(double y) {
	}

	@Override
	public double getWidth() {
		enableScaling();
		double width = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(mc.displayWidth);
		disableScaling();
		return width;
	}

	@Override
	public double getHeight() {
		enableScaling();
		double height = WaveClientAPI.getInstance().getScalingManager().convertFromPixel(mc.displayHeight);
		disableScaling();
		return height;
	}

	@Override
	public void setWidth(double width) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeight(double height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPressed(int button) {
		return pressed.getOrDefault(button, false);
	}

	@Override
	public void setPressed(int button, boolean pressed) {
		this.pressed.put(button, pressed);
	}

	protected final void doForGUIs(Consumer<Gui> cons) {
		doForGUIs(cons, v -> true);
	}

	protected final void doForGUIs(Consumer<Gui> cons, Predicate<Gui> predicate) {
		doForGUIs(cons, predicate, Gui.class);
	}

	protected final <V> void doForGUIs(Consumer<V> cons, Class<V> clazz) {
		doForGUIs(cons, v -> true, clazz);
	}

	@SuppressWarnings("unchecked")
	protected final <V> void doForGUIs(Consumer<V> cons, Predicate<V> predicate, Class<V> clazz) {
		for (Gui gui : new ArrayList<>(GUIs)) {
			try {
				if (!clazz.isAssignableFrom(gui.getClass())) {
					continue;
				}
				V v = (V) gui;
				if (predicate.test(v)) {
					cons.accept(v);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static class CloseListener extends BaseListener<GuiOpenEvent> {
		public CloseListener() {
			super(GuiOpenEvent.class, event -> {
				if (!guiScreens.isEmpty()) {

					if (event.getGui() instanceof AbstractGuiScreen) {
						AbstractGuiScreen.otherGuiOpens = true;
					} else {
						if (event.getGui() == null || event.getGui() instanceof GuiMainMenu) {
							AbstractGuiScreen ags = guiScreens.peek();
							ags.onGuiClosed();
							ags = guiScreens.pop();
							event.setGui((de.dasbabypixel.waveclient.wrappedloader.api.gui.Gui) ags);
						} else {
							guiScreens.clear();
						}
					}
				}
			});
		}
	}

	public void init() {
	}

	public boolean mouseClick(double mouseX, double mouseY, int mouseButton) {
		return true;
	}

	public boolean mouseRelease(double mouseX, double mouseY, int state) {
		return true;
	}

	public boolean mouseDrag(double mouseX, double mouseY, int mouseButton, long dragTime) {
		return true;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
