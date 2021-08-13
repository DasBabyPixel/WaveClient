package de.dasbabypixel.waveclient.module.core.listener;

import java.util.Collection;
import java.util.HashSet;

import org.lwjgl.opengl.GL11;

import de.dasbabypixel.waveclient.module.core.util.AccessTransformerMethod;
import de.dasbabypixel.waveclient.wrappedloader.api.event.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ESPListener extends AbstractListener {

	public boolean espActive = false;
	public boolean espAll = true;
	private Collection<RenderLivingEvent.Pre<?>> toRender = new HashSet<>();
	private boolean rendering = false;
	public Collection<EntityLivingBase> espTargets = new HashSet<>();

	@EventHandler
	public void handle(RenderWorldLastEvent event) {
		if (this.toRender.isEmpty())
			return;
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		rendering = true;
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();

		AccessTransformerMethod.EntityRenderer_setupCameraTransform_float_int
				.invoke(Minecraft.getMinecraft().entityRenderer, event.partialTicks, 2);

		RenderHelper.enableStandardItemLighting();
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		boolean oldRenderShadow = rm.isRenderShadow();
		rm.setRenderShadow(false);
		GlStateManager.pushMatrix();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GlStateManager.popMatrix();

		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		for (RenderLivingEvent.Pre<?> e : toRender) {
			GlStateManager.disableLighting();
			rm.renderEntityStatic(e.entity, event.partialTicks, false);
		}
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GlStateManager.pushMatrix();
		GlStateManager.enableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GlStateManager.popMatrix();
		rm.setRenderShadow(oldRenderShadow);
		Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		rendering = false;
		toRender.clear();
	}

	@EventHandler(priority = -1)
	public void handle(RenderLivingEvent.Pre<?> event) {
		if (espActive && (Minecraft.getMinecraft().currentScreen == null)) {
			if (!rendering && (espAll || espTargets.contains(event.entity))) {
				this.toRender.add(event);
				event.setCanceled(true);
			}
		}
	}
}
