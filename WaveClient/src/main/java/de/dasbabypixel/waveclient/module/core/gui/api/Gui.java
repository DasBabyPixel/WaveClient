package de.dasbabypixel.waveclient.module.core.gui.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public interface Gui {

	void draw(double mouseX, double mouseY);

	double getX();

	double getY();

	void setX(double X);

	void setY(double y);

	double getWidth();

	double getHeight();

	void setWidth(double width);

	void setHeight(double height);

	boolean isPressed(int button);

	void setPressed(int button, boolean pressed);

	default void onClick(double mouseX, double mouseY, int mouseButton) {
	}

	default void onRelease(double mouseX, double mouseY, int mouseButton) {
	}

	default void onDrag(double mouseX, double mouseY, int mouseButton, long dragMillis) {
	}

	default void onMove(double oldMouseX, double oldMouseY, double mouseX, double mouseY) {
	}

	default void onKeyTyped(char key, int keycode) {
	}

	default void onKeyReleased(char key, int keycode) {
	}

	default void onClose() {
	}

	default void tick() {
	}

	default Gui setPosition(double x, double y) {
		this.setX(x);
		this.setY(y);
		return this;
	}
	
	default Gui setSize(double width, double height) {
		this.setWidth(width);
		this.setHeight(height);
		return this;
	}

	default void onScroll(double delta) {
	}

	default boolean isHovering(double mouseX, double mouseY) {
		return isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY);
	}

	static boolean isHovering(double x, double y, double width, double height, double mouseX, double mouseY) {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	default void playButtonPressSound() {
		Minecraft.getMinecraft()
				.getSoundHandler()
				.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}

}
