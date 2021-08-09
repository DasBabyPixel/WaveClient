package de.dasbabypixel.waveclient.module.core.render;

import org.lwjgl.opengl.GL11;

import de.dasbabypixel.waveclient.module.core.gui.api.Text;
import de.dasbabypixel.waveclient.module.core.render.image.DeprecatedImage;
import de.dasbabypixel.waveclient.module.core.render.image.Image;
import de.dasbabypixel.waveclient.module.core.util.AccessTransformerField;
import de.dasbabypixel.waveclient.module.core.util.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

@SuppressWarnings("deprecation")
public class DrawHelper {

	public static void drawCenteredString(FontRenderer fontRenderer, String text, double x, double y, double width,
			double height, int color) {
		float fx = (float) x;
		float fy = (float) y;
		float fwidth = (float) width;
		float fheight = (float) height;
		fontRenderer.drawString(text, fx + fwidth / 2 - fontRenderer.getStringWidth(text) / 2,
				fy + fheight / 2 - fontRenderer.FONT_HEIGHT / 2, color, false);
	}

	public static void drawCenteredText(double x, double y, double width, double height, Text text) {
		drawCenteredString(text.getFont(), text.getContent(), x, y, width, height, text.getColor().toARGB());
	}

	public static void drawVerticallyCenteredString(FontRenderer fontRenderer, String text, double x, double y,
			double height, int color) {
		float fx = (float) x;
		float fy = (float) y;
		float fheight = (float) height;
		fontRenderer.drawString(text, fx, fy + fheight / 2 - fontRenderer.FONT_HEIGHT / 2, color, false);
	}

	public static void drawImage(Image image, double x, double y, double z, double width, double height, double imageX,
			double imageY, double imageWidth, double imageHeight) {
		if (image.isCreated()) {
			image.bind();
			drawImage(x, y, z, width, height, imageX, imageY, imageWidth, imageHeight);
		}
	}

	public static void drawImageToArea(Image image, double x, double y, double width, double height) {
		if (image.isCreated()) {
			image.bind();
			drawImageToArea(x, y, width, height);
		}
	}

	@Deprecated
	public static void drawImageToArea(DeprecatedImage image, double x, double y, double width, double height) {
		image.bind();
		drawImageToArea(x, y, width, height);
	}

	public static void drawImageToArea(double x, double y, double width, double height) {
		drawImage(x, y, 0, width, height, 0, 0, width, height);
	}

	@Deprecated
	public static void drawImage(DeprecatedImage image, double x, double y, double z, double width, double height,
			double imageX, double imageY, double imageWidth, double imageHeight) {
		image.bind();
		drawImage(x, y, z, width, height, imageX, imageY, imageWidth, imageHeight);
	}

	public static void drawImage(double x, double y, double z, double width, double height, double imageX,
			double imageY, double imageWidth, double imageHeight) {
		double pixelWidth = 1.0 / imageWidth;
		double pixelHeight = 1.0 / imageHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer wr = tessellator.getWorldRenderer();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		wr.pos(x, y + height, z).tex(imageX * pixelWidth, (imageY + height) * pixelHeight).endVertex();
		wr.pos(x + width, y + height, z)
				.tex((imageX + width) * pixelWidth, (imageY + height) * pixelHeight)
				.endVertex();
		wr.pos(x + width, y, z).tex((imageX + width) * pixelWidth, imageY * pixelHeight).endVertex();
		wr.pos(x, y, z).tex(imageX * pixelWidth, imageY * pixelHeight).endVertex();
		tessellator.draw();
//		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.popMatrix();
	}

	public static void drawGradientRect(double left, double top, double right, double bottom, int startColor,
			int endColor) {
		float alpha1 = (startColor >> 24 & 255) / 255.0F;
		float red1 = (startColor >> 16 & 255) / 255.0F;
		float green1 = (startColor >> 8 & 255) / 255.0F;
		float blue1 = (startColor & 255) / 255.0F;
		float alpha2 = (endColor >> 24 & 255) / 255.0F;
		float red2 = (endColor >> 16 & 255) / 255.0F;
		float green2 = (endColor >> 8 & 255) / 255.0F;
		float blue2 = (endColor & 255) / 255.0F;
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(right, top, 0).color(red1, green1, blue1, alpha1).endVertex();
		worldrenderer.pos(left, top, 0).color(red1, green1, blue1, alpha1).endVertex();
		worldrenderer.pos(left, bottom, 0).color(red2, green2, blue2, alpha2).endVertex();
		worldrenderer.pos(right, bottom, 0).color(red2, green2, blue2, alpha2).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
//		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	public static void drawGradientRect(double left, double top, double right, double bottom, int topLeft, int topRight,
			int bottomLeft, int bottomRight) {
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(right, top, 0)
				.color(Color.getRed(topRight), Color.getGreen(topRight), Color.getBlue(topRight),
						Color.getAlpha(topRight))
				.endVertex();
		worldrenderer.pos(left, top, 0)
				.color(Color.getRed(topLeft), Color.getGreen(topLeft), Color.getBlue(topLeft), Color.getAlpha(topLeft))
				.endVertex();
		worldrenderer.pos(left, bottom, 0)
				.color(Color.getRed(bottomLeft), Color.getGreen(bottomLeft), Color.getBlue(bottomLeft),
						Color.getAlpha(bottomLeft))
				.endVertex();
		worldrenderer.pos(right, bottom, 0)
				.color(Color.getRed(bottomRight), Color.getGreen(bottomRight), Color.getBlue(bottomRight),
						Color.getAlpha(bottomRight))
				.endVertex();

		tessellator.draw();
		GlStateManager.shadeModel(7424);
//		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green,
			float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, alpha);
		drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glLineWidth(lineWdith);
		GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
		drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.begin(3, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(3, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(1, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		tessellator.draw();
	}

	public static void drawBoundingBox(AxisAlignedBB aa) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		tessellator.draw();
	}

	public static void displayEntityOutline(Entity entity, double x, double y, double z, float partialTicks) {
		final Minecraft mc = Minecraft.getMinecraft();
		final RenderManager rm = mc.getRenderManager();
		final RenderGlobal rg = mc.renderGlobal;
		final ShaderGroup entityOutlineShader = AccessTransformerField.RenderGlobal_entityOutlineShader.get(rg);
		GlStateManager.depthFunc(519);
//		GlStateManager.disableFog();
		Framebuffer entityOutlineFramebuffer = AccessTransformerField.RenderGlobal_entityOutlineFramebuffer.get(rg);
		entityOutlineFramebuffer.framebufferClear();
		entityOutlineFramebuffer.bindFramebuffer(false);
		RenderHelper.disableStandardItemLighting();
		rm.setRenderOutlines(true);
		boolean flag = (mc.getRenderViewEntity() instanceof EntityLivingBase);
		boolean flag1 = (entity.isInRangeToRender3d(x, y, z));

		if (flag && flag1) {
			rm.renderEntitySimple(entity, partialTicks);
		}
		rm.setRenderOutlines(false);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.depthMask(false);
		entityOutlineShader.loadShaderGroup(partialTicks);
		GlStateManager.depthMask(true);
		mc.getFramebuffer().bindFramebuffer(false);
//		GlStateManager.enableFog();
//		GlStateManager.enableBlend();
//		GlStateManager.enableColorMaterial();
		GlStateManager.depthFunc(515);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
	}

	public static void drawCross(double x, double y, double width, double height, double lineWidth, int color) {
		double left = x;
		double top = y;
		double right = x + width;
		double bottom = y + height;
		if (left > right) {
			double i = left;
			left = right;
			right = i;
		}
		if (top > bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		float alpha = (color >> 24 & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer wr = tessellator.getWorldRenderer();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.color(red, green, blue, alpha);

		double depth = 0;

		// Unten links nach oben rechts
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		wr.pos(left + lineWidth, bottom, depth).endVertex();
		wr.pos(right, top + lineWidth, depth).endVertex();
		wr.pos(right - lineWidth, top, depth).endVertex();
		wr.pos(left, bottom - lineWidth, depth).endVertex();

		// Oben links nach unten rechts
		wr.pos(left, top + lineWidth, depth).endVertex();
		wr.pos(right - lineWidth, bottom, depth).endVertex();
		wr.pos(right, bottom - lineWidth, depth).endVertex();
		wr.pos(left + lineWidth, top, depth).endVertex();
		tessellator.draw();

		wr.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

		// Oben Links Ecke
		wr.pos(left, top, depth).endVertex();
		wr.pos(left, top + lineWidth, depth).endVertex();
		wr.pos(left + lineWidth, top, depth).endVertex();
		// Unten Links Ecke
		wr.pos(left, bottom, depth).endVertex();
		wr.pos(left + lineWidth, bottom, depth).endVertex();
		wr.pos(left, bottom - lineWidth, depth).endVertex();
		// Unten Rechts Ecke
		wr.pos(right, bottom, depth).endVertex();
		wr.pos(right, bottom - lineWidth, depth).endVertex();
		wr.pos(right - lineWidth, bottom, depth).endVertex();
		// Oben Rechts Ecke
		wr.pos(right, top, depth).endVertex();
		wr.pos(right - lineWidth, top, depth).endVertex();
		wr.pos(right, top + lineWidth, depth).endVertex();

		tessellator.draw();

		GlStateManager.resetColor();
		GlStateManager.enableTexture2D();
//		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public static void drawRect(double x, double y, double width, double height, int color) {
		double left = x;
		double top = y;
		double right = x + width;
		double bottom = y + height;
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		float alpha = (color >> 24 & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer wr = tessellator.getWorldRenderer();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.color(red, green, blue, alpha);
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		wr.pos(left, bottom, 0.0D).endVertex();
		wr.pos(right, bottom, 0.0D).endVertex();
		wr.pos(right, top, 0.0D).endVertex();
		wr.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.resetColor();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	public static void drawFilledBox(AxisAlignedBB axisAlignedBB) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
		worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
		tessellator.draw();
	}

	public static void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
		drawTriangle(0, 0, new Triangle(x1, y1, x2, y2, x3, y3), color);
	}

	public static void drawTriangle(double x, double y, Triangle triangle, int color) {
		y -= 1;
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		float alpha = (color >> 24 & 255) / 255.0F;
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.color(red, green, blue, alpha);
		wr.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
		wr.pos(triangle.p1.x + x, triangle.p1.y + y, 0).endVertex();
		wr.pos(triangle.p2.x + x, triangle.p2.y + y, 0).endVertex();
		wr.pos(triangle.p3.x + x, triangle.p3.y + y, 0).endVertex();
		tess.draw();
		GlStateManager.resetColor();
		GlStateManager.enableTexture2D();
//		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public static class Point {
		public double x, y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

	}

	public static class Triangle {
		public Point p1, p2, p3;

		public Triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
			p1 = new Point(x1, y1);
			p2 = new Point(x2, y2);
			p3 = new Point(x3, y3);
		}

	}
}
