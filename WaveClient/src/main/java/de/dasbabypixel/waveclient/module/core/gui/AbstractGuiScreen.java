package de.dasbabypixel.waveclient.module.core.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import de.dasbabypixel.waveclient.module.core.gui.component.AbstractGui;
import de.dasbabypixel.waveclient.module.core.render.Images;
import de.dasbabypixel.waveclient.wrappedloader.api.gui.Gui;
import de.dasbabypixel.waveclient.wrappedloader.api.gui.GuiScreen;
import de.dasbabypixel.waveclient.wrappedloader.api.render.DrawHelper;
import de.dasbabypixel.waveclient.wrappedloader.api.util.Mouse;

public abstract class AbstractGuiScreen extends AbstractGui implements GuiScreen {

	public Collection<Gui> GUIs = new ArrayList<>();
	private Collection<Integer> mouseDown = new HashSet<>();

	@Override
	public final void mouseDrag(double mouseX, double mouseY, int clickedMouseButton, long dragTime) {
		try {
			if (doMouseDrag(mouseX, mouseY, clickedMouseButton, dragTime)) {
				doForGUIs(b -> {
					if (Mouse.isButtonDown(clickedMouseButton)) {
						b.mouseDrag(mouseX, mouseY, clickedMouseButton, dragTime);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		try {
			if (mouseDown.contains(mouseButton)) {
				mouseReleased(0, 0, mouseButton);
			}
			mouseDown.add(mouseButton);
			if (doMouseClick(mouseX, mouseY, mouseButton)) {
				doForGUIs(gui -> {
					if (gui.isHovering(mouseX, mouseY)) {
						gui.mouseClicked(mouseX, mouseY, mouseButton);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
		this.doMouseRelease(mouseX, mouseY, mouseButton);
		try {
			if (mouseDown.contains(mouseButton)) {
				mouseDown.remove(mouseButton);
				if (doMouseRelease(mouseX, mouseY, mouseButton)) {
					doForGUIs(gui -> {
						gui.mouseReleased(mouseX, mouseY, mouseButton);
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public final void draw(double mouseX, double mouseY, float partialTicks) {
		try {
			this.preDraw(mouseX, mouseY, partialTicks);
			this.doDraw(mouseX, mouseY, partialTicks);
			doForGUIs(gui -> {
				gui.update();
				gui.draw(mouseX, mouseY, partialTicks);
			});
			this.postDraw(mouseX, mouseY, partialTicks);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void doDraw(double mouseX, double mouseY, float partialTicks) {
	}

	public void postDraw(double mouseX, double mouseY, float partialTicks) {
	}

	public void preDraw(double mouseX, double mouseY, float partialTicks) {
	}

	public boolean doMouseClick(double mouseX, double mouseY, int mouseButton) {
		return true;
	}

	public boolean doMouseRelease(double mouseX, double mouseY, int state) {
		return true;
	}

	public boolean doMouseDrag(double mouseX, double mouseY, int mouseButton, long dragTime) {
		return true;
	}

	public void drawBackground() {
		DrawHelper.drawImageToArea(Images.background, getX(), getY(), getWidth(), getHeight());
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
}
