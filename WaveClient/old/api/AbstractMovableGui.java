package de.dasbabypixel.waveclient.module.core.gui.api;

public abstract class AbstractMovableGui extends AbstractGui {
	
	private double offsetX;
	private double offsetY;
	protected boolean moveEnabled = true;

	public AbstractMovableGui(double x, double y) {
		super(x, y);
	}

	@Override
	public void onClick(double mouseX, double mouseY, int mouseButton) {
		if (mouseButton == 0) {
			offsetX = mouseX - getX();
			offsetY = mouseY - getY();
		}
	}

	public boolean isMoveEnabled() {
		return moveEnabled;
	}

	@Override
	public void onDrag(double mouseX, double mouseY, int mouseButton, long dragTime) {
		if (isMoveEnabled()) {
			if (isPressed(0)) {
				setX(mouseX - offsetX);
				setY(mouseY - offsetY);
			}
		}
	}

	@Override
	public void onMove(double oldMouseX, double oldMouseY, double mouseX, double mouseY) {
		if (isMoveEnabled()) {
			if (isPressed(0)) {
				setX(mouseX - offsetX);
				setY(mouseY - offsetY);
			}
		}
	}
}
