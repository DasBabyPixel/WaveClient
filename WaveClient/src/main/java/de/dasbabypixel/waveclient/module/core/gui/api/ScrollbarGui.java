package de.dasbabypixel.waveclient.module.core.gui.api;

import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import de.dasbabypixel.waveclient.module.core.render.DrawHelper;
import de.dasbabypixel.waveclient.module.core.util.color.Color;
import de.dasbabypixel.waveclient.module.core.util.math.MathHelper;

public class ScrollbarGui extends AbstractGui implements GuiWrapper {

	private Gui gui;
	private Scrollbar verticalScrollbar = new Scrollbar(() -> {
		return gui.getHeight() != getHeightWithoutScrollbar();
	});
	private Scrollbar horizontalScrollbar = new Scrollbar(() -> {
		return gui.getWidth() != getWidthWithoutScrollbar();
	});
	private double verticalScrollWhenReached = 150;
	private double horizontalScrollWhenReached = 100;
	private double verticalPixelsScrolled = 0;
	private double currentVerticalPixelsScrolled = 0;
	private double horizontalPixelsScrolled = 0;
	private double currentHorizontalPixelsScrolled = 0;
	private long lastUpdate = System.currentTimeMillis();
	private double verticalScrollbarWidth = 10;
	private double horizontalScrollbarHeight = 10;
	private double verticalSpeed = 1;
	private double horizontalSpeed = 1;

	public ScrollbarGui(Gui gui) {
		super(0, 0);
		this.gui = gui;
	}

	@Override
	public Gui getGui() {
		return gui;
	}

	private void update() {
		final long currentTime = System.currentTimeMillis();
		final long diff = currentTime - lastUpdate;
		lastUpdate = currentTime;
		vertical: {
			final double diffPixels = verticalPixelsScrolled - currentVerticalPixelsScrolled;
			if (Math.round(diffPixels * 100) == 0) {
				break vertical;
			}
			double increment = diff * diffPixels / (200.0 / verticalSpeed);
			currentVerticalPixelsScrolled += increment;
		}
		horizontal: {
			final double diffPixels = horizontalPixelsScrolled - currentHorizontalPixelsScrolled;
			if (Math.round(diffPixels * 100) == 0) {
				break horizontal;
			}
			double increment = diff * diffPixels / (200.0 / horizontalSpeed);
			currentHorizontalPixelsScrolled += increment;
		}
	}

	@Override
	public void draw(double mouseX, double mouseY) {
		final double scale = ScalingManager.getScale();
		ScalingManager.pushScale(Scaling.PIXEL.toScalingManagerScale(ScalingManager.Scale.Type.OVERRIDE));

		final double x = getX();
		final double y = getY();
		final double guiwidth = gui.getWidth();
		final double guiheight = gui.getHeight();
		final double pixelX = scale * x;
		final double pixelY = scale * y;
		final double pixelMouseX = scale * mouseX;
		final double pixelMouseY = scale * mouseY;
		final double pixelGuiWidth = scale * guiwidth;
		final double pixelGuiHeight = scale * guiheight;
		final double simpleHeight = getHeightWithoutScrollbar();
		final double simpleWidth = getWidthWithoutScrollbar();

		if (verticalScrollbar.isVisible()) {
			// Basic values

			final double bgw = getVerticalScrollbarBackgroundWidth();
			final double sw = getVerticalScrollbarWidth();
			final double bgdiff = bgw - sw;
			final double bgy = pixelY;
			final double bgx = pixelX + scale * simpleWidth;
			final double sx = bgx + bgdiff / 2;
			final double bgh = scale * simpleHeight;

			// Scrollbar height
			final double maxsh = bgh - bgdiff;
			final double shpercent = MathHelper.clamp(0, 1, maxsh / pixelGuiHeight);
			final double sh = maxsh * shpercent;

			// Scrollbar y
			final double sy = bgy + bgdiff / 2 + currentVerticalPixelsScrolled * shpercent * scale;

			verticalScrollbar.draw(sx, sy, sw, sh, pixelMouseX, pixelMouseY, bgx, bgy, bgw, bgh);
		}
		if (horizontalScrollbar.isVisible()) {
			// Basic values
			final double bgh = getHorizontalScrollbarBackgroundHeight();
			final double sh = getHorizontalScrollbarHeight();
			final double bgdiff = bgh - sh;
			final double bgx = pixelX;
			final double bgy = pixelY + scale * simpleHeight;
			final double sy = bgy + bgdiff / 2;
			final double bgw = scale * simpleWidth;

			// Scrollbar width
			final double maxsw = bgw - bgdiff;
			final double swpercent = MathHelper.clamp(0, 1, maxsw / pixelGuiWidth);
			final double sw = maxsw * swpercent;

			// Scrollbar x
			final double sx = bgx + bgdiff / 2 + currentHorizontalPixelsScrolled * swpercent * scale;

			horizontalScrollbar.draw(sx, sy, sw, sh, pixelMouseX, pixelMouseY, bgx, bgy, bgw, bgh);
		}

		ScalingManager.popScale();

		final int sx = (int) pixelX;
		final int sy = (int) (mc.displayHeight - pixelY - this.getHeightWithoutScrollbar() * scale);
		final int sw = (int) (this.getWidthWithoutScrollbar() * scale);
		final int sh = (int) (this.getHeightWithoutScrollbar() * scale);

		GL11.glScissor(sx, sy, sw, sh);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		try {
			gui.setX(x - currentHorizontalPixelsScrolled);
			gui.setY(y - currentVerticalPixelsScrolled);
			gui.draw(mouseX, mouseY);
			gui.setX(x);
			gui.setY(y);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

//		DrawHelper.drawRect(getX(), getY(), getWidthWithoutScrollbar(), getHeightWithoutScrollbar(),
//				new Color(0, 255, 0, 100).toARGB());
//		DrawHelper.drawRect(getX(), getY(), getWidthWithScrollbar(scale), getHeightWithScrollbar(scale),
//				new Color(255, 0, 0, 100).toARGB());
	}

	@Override
	public void onClick(double mouseX, double mouseY, int mouseButton) {
		final double scale = ScalingManager.getScale();
		final double pixelMouseX = scale * mouseX;
		final double pixelMouseY = scale * mouseY;
		if (verticalScrollbar.hovering(pixelMouseX, pixelMouseY) && verticalScrollbar.isVisible()) {
			verticalScrollbar.modifying = true;
			verticalScrollbar.offset = (pixelMouseY - verticalScrollbar.lastY);
			if (mouseButton == 0) {
				verticalSpeed = 8;
			}
		} else if (horizontalScrollbar.hovering(pixelMouseX, pixelMouseY) && horizontalScrollbar.isVisible()) {
			horizontalScrollbar.modifying = true;
			horizontalScrollbar.offset = (pixelMouseX - horizontalScrollbar.lastX);
			if (mouseButton == 0) {
				horizontalSpeed = 8;
			}
		} else if (isHoveringOnGui(mouseX, mouseY)) {
			gui.setPressed(mouseButton, true);
			gui.onClick(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void onDrag(double mouseX, double mouseY, int mouseButton, long dragMillis) {
		final double scale = ScalingManager.getScale();
		final double x = getX();
		final double y = getY();
		final double pixelX = scale * x;
		final double pixelY = scale * y;
		final double pixelMouseX = scale * mouseX;
		final double pixelMouseY = scale * mouseY;
		final double guiwidth = gui.getWidth();
		final double guiheight = gui.getHeight();
		final double pixelGuiWidth = scale * guiwidth;
		final double pixelGuiHeight = scale * guiheight;
		final double simpleHeight = getHeightWithoutScrollbar();
		final double simpleWidth = getWidthWithoutScrollbar();
		if (this.verticalScrollbar.modifying && verticalScrollbar.isVisible()) {

			final double bgw = getVerticalScrollbarBackgroundWidth();
			final double sw = getVerticalScrollbarWidth();
			final double bgdiff = bgw - sw;
			final double bgh = scale * simpleHeight;
			// Scrollbar height
			final double maxsh = bgh - bgdiff;
			final double shpercent = MathHelper.clamp(0, 1, maxsh / pixelGuiHeight);
			final double sh = maxsh * shpercent;
			final double minsy = pixelY + bgdiff / 2;
			final double maxsy = minsy + maxsh - sh;
			final double newY = MathHelper.clamp(minsy, maxsy, pixelMouseY - verticalScrollbar.offset);
			final double newLocalY = newY - minsy;
			final double newPercentScrolled = newLocalY / (maxsh - sh);
			final double newPixelsScrolled = newPercentScrolled * (guiheight - this.getHeightWithoutScrollbar());
//			final double diff = verticalPixelsScrolled - currentVerticalPixelsScrolled;
			setVerticalPixelsScrolled(newPixelsScrolled);
//			this.currentVerticalPixelsScrolled = newPixelsScrolled + diff;
		} else if (this.horizontalScrollbar.modifying && horizontalScrollbar.isVisible()) {
			final double bgh = getHorizontalScrollbarBackgroundHeight();
			final double sh = getHorizontalScrollbarHeight();
			final double bgdiff = bgh - sh;
			final double bgw = scale * simpleWidth;
			// Scrollbar width;
			final double maxsw = bgw - bgdiff;
			final double swpercent = MathHelper.clamp(0, 1, maxsw / pixelGuiWidth);
			final double sw = maxsw * swpercent;
			final double minsx = pixelX + bgdiff / 2;
			final double maxsx = minsx + maxsw - sw;
			final double newX = MathHelper.clamp(minsx, maxsx, pixelMouseX - horizontalScrollbar.offset);
			final double newLocalX = newX - minsx;
			final double newPercentScrolled = newLocalX / (maxsw - sw);
			final double newPixelsScrolled = newPercentScrolled * (guiwidth - this.getWidthWithoutScrollbar());
//			final double diff = horizontalPixelsScrolled - currentHorizontalPixelsScrolled;
			setHorizontalPixelsScrolled(newPixelsScrolled);
//			this.currentHorizontalPixelsScrolled = newPixelsScrolled + diff;
		} else {
			gui.onDrag(mouseX, mouseY, mouseButton, dragMillis);
		}
	}

	public boolean isHoveringOnGui(double mouseX, double mouseY) {
		return Gui.isHovering(getX(), getY(), getWidthWithoutScrollbar(), getHeightWithoutScrollbar(), mouseX, mouseY);
	}

	@Override
	public void onKeyReleased(char key, int keycode) {
		gui.onKeyReleased(key, keycode);
	}

	@Override
	public void onKeyTyped(char key, int keycode) {
		gui.onKeyTyped(key, keycode);
	}

	@Override
	public void onMove(double oldMouseX, double oldMouseY, double mouseX, double mouseY) {
		gui.onMove(oldMouseX, oldMouseY, mouseX, mouseY);
	}

	@Override
	public void onRelease(double mouseX, double mouseY, int mouseButton) {
		if (gui.isPressed(mouseButton)) {
			gui.onRelease(mouseX, mouseY, mouseButton);
			gui.setPressed(mouseButton, false);
		}
		if (verticalScrollbar.modifying) {
			verticalScrollbar.modifying = false;
			verticalSpeed = 1;
		}
		if (horizontalScrollbar.modifying) {
			horizontalScrollbar.modifying = false;
			horizontalSpeed = 1;
		}
	}

	@Override
	public void onScroll(double delta) {
		double extra = getVerticalScrollInterval() * -MathHelper.signum(delta);
		final double newPixelScrolled = extra * gui.getHeight();
		setVerticalPixelsScrolled(verticalPixelsScrolled + newPixelScrolled);
//		gui.onScroll(delta);
	}

	@Override
	public void tick() {
		this.update();
		gui.tick();
	}

	@Override
	public void onClose() {
		gui.onClose();
	}

	@Override
	public void playButtonPressSound() {
		gui.playButtonPressSound();
	}

	@Override
	public void setWidth(double width) {
		gui.setWidth(width);
	}

	@Override
	public void setHeight(double height) {
		gui.setHeight(height);
	}

	@Override
	public void setX(double x) {
		gui.setX(x);
	}

	@Override
	public void setY(double y) {
		gui.setY(y);
	}

	public double getVerticalScrollInterval() {
		return this.getHeightWithoutScrollbar() / gui.getHeight() / 3.0;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public double getWidth() {
		return verticalScrollbar.isVisible() ? getWidthWithScrollbar(ScalingManager.getScale())
				: getWidthWithoutScrollbar();
	}

	@Override
	public double getHeight() {
		return horizontalScrollbar.isVisible() ? getHeightWithScrollbar(ScalingManager.getScale())
				: getHeightWithoutScrollbar();
	}

	public double getWidthWithoutScrollbar() {
		return Math.min(gui.getWidth(), horizontalScrollWhenReached);
	}

	public double getHeightWithoutScrollbar() {
		return Math.min(gui.getHeight(), verticalScrollWhenReached);
	}

	public double getWidthWithScrollbar(double scale) {
		return getWidthWithoutScrollbar() + getVerticalScrollbarBackgroundWidth() / scale;
	}

	public double getHeightWithScrollbar(double scale) {
		return getHeightWithoutScrollbar() + getHorizontalScrollbarBackgroundHeight() / scale;
	}

	public double getVerticalScrollbarBackgroundWidth() {
		return getVerticalScrollbarWidth() + 2;
	}

	public double getHorizontalScrollbarBackgroundHeight() {
		return getHorizontalScrollbarHeight() + 2;
	}

	public void setVerticalPixelsScrolled(double verticalPixelsScrolled) {
		this.verticalPixelsScrolled = MathHelper.clamp(0, gui.getHeight() - getHeightWithoutScrollbar(),
				verticalPixelsScrolled);
	}

	public void setHorizontalPixelsScrolled(double horizontalPixelsScrolled) {
		this.horizontalPixelsScrolled = MathHelper.clamp(0, gui.getWidth() - getWidthWithoutScrollbar(),
				horizontalPixelsScrolled);
	}

	public double getHorizontalScrollbarHeight() {
		return horizontalScrollbarHeight;
	}

	public double getVerticalScrollbarWidth() {
		return verticalScrollbarWidth;
	}

	@Override
	public double getX() {
		return gui.getX();
	}

	@Override
	public double getY() {
		return gui.getY();
	}

	public Scrollbar getVerticalScrollbar() {
		return verticalScrollbar;
	}

	public Scrollbar getHorizontalScrollbar() {
		return horizontalScrollbar;
	}

	public final class Scrollbar {
		private Color scrollbarColor = new Color(120, 120, 120, 255);
		private Color scrollbarHoverColor = new Color(80, 80, 80, 255);
		private Color scrollbarBackground = new Color(150, 150, 150, 255);
		private double lastX;
		private double lastY;
		private double lastWidth;
		private double lastHeight;
		private double offset;
		private Supplier<Boolean> visibility;

		private Scrollbar(Supplier<Boolean> visibility) {
			this.visibility = visibility;
		}

		private boolean modifying = false;

		public boolean isVisible() {
			return visibility.get();
		}

		public void draw(double x, double y, double width, double height, double mouseX, double mouseY, double bgx,
				double bgy, double bgWidth, double bgHeight) {
			this.lastX = x;
			this.lastY = y;
			this.lastWidth = width;
			this.lastHeight = height;
			if (height != 0) {
				boolean hovering = hovering(mouseX, mouseY);
				DrawHelper.drawRect(bgx, bgy, bgWidth, bgHeight, scrollbarBackground.toARGB());
				DrawHelper.drawRect(x, y, width, height,
						hovering || modifying ? scrollbarHoverColor.toARGB() : scrollbarColor.toARGB());
			}
		}

		private boolean hovering(double mouseX, double mouseY) {
			return Gui.isHovering(lastX, lastY, lastWidth, lastHeight, mouseX, mouseY);
		}
	}
}
