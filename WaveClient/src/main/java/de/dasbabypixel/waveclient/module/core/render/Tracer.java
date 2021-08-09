package de.dasbabypixel.waveclient.module.core.render;

import org.lwjgl.opengl.GL11;

import de.dasbabypixel.waveclient.module.core.util.math.Vector;

public class Tracer {

	public static void displayTracerLine(Vector startPosition, Vector targetPosition) {
		System.out.println("Displaying tracer");
		GL11.glBlendFunc(770, 771);
		GL11.glLineWidth(3.0F);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);

		double size = 0.45;
		double ytSize = 0.35;
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(0, 5, 0);

		double x = targetPosition.getX();
		double y = targetPosition.getY();
		double z = targetPosition.getZ();
		double px = startPosition.getX();
		double py = startPosition.getY();
		double pz = startPosition.getZ();
		double deltax = px - x;
		double deltay = py - y;
		double deltaz = pz - z;

		if (x != px || y != py || z != pz) {
			GL11.glVertex3d(0, 0, 0);
			GL11.glVertex3d((-deltax + size) - 0.5, (ytSize - deltay) + 1.0, (-deltaz - size) + 0.5);
			System.out.println(String.format("%.1f | %.1f | %.1f", deltax, deltay, deltaz));
		} else {
			System.out.println("error");
		}

		GL11.glEnd();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
