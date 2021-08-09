package de.dasbabypixel.waveclient.module.core.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AccessTransformerField<T> {

	public static AccessTransformerField<Framebuffer> RenerGlobal_entityOutlineFramebuffer = new AccessTransformerField<>(
			ReflectionHelper.findField(RenderGlobal.class, "entityOutlineFramebuffer", "field_175015_z", "A"));
	public static AccessTransformerField<Double> RenderManager_renderPosX = new AccessTransformerField<Double>(
			ReflectionHelper.findField(RenderManager.class, "renderPosX", "field_78725_b", "o"));
	public static AccessTransformerField<Double> RenderManager_renderPosY = new AccessTransformerField<Double>(
			ReflectionHelper.findField(RenderManager.class, "renderPosY", "field_78726_c", "p"));
	public static AccessTransformerField<Double> RenderManager_renderPosZ = new AccessTransformerField<Double>(
			ReflectionHelper.findField(RenderManager.class, "renderPosZ", "field_78723_d", "q"));
	public static AccessTransformerField<Framebuffer> RenderGlobal_entityOutlineFramebuffer = new AccessTransformerField<Framebuffer>(
			ReflectionHelper.findField(RenderGlobal.class, "entityOutlineFramebuffer", "field_175015_z", "A"));
	public static AccessTransformerField<ShaderGroup> RenderGlobal_entityOutlineShader = new AccessTransformerField<>(
			ReflectionHelper.findField(RenderGlobal.class, "entityOutlineShader", "field_174991_A", "B"));
	public static AccessTransformerField<Timer> Minecraft_timer = new AccessTransformerField<>(
			ReflectionHelper.findField(Minecraft.class, "timer", "field_71428_T", "Y"));
	public static AccessTransformerField<RenderItem> Minecraft_renderItem = new AccessTransformerField<>(
			ReflectionHelper.findField(Minecraft.class, "renderItem", "field_175621_X", "ab"));
	public static AccessTransformerField<ItemRenderer> Minecraft_itemRenderer = new AccessTransformerField<>(
			ReflectionHelper.findField(Minecraft.class, "itemRenderer", "field_175620_Y", "ac"));
	public static AccessTransformerField<TextureManager> RenderItem_textureManager = new AccessTransformerField<>(
			ReflectionHelper.findField(RenderItem.class, "textureManager", "field_175057_n", "e"));
	public static AccessTransformerField<RenderItem> ItemRenderer_itemRenderer = new AccessTransformerField<>(
			ReflectionHelper.findField(ItemRenderer.class, "itemRenderer", "field_178112_h", "h"));
	public static AccessTransformerField<ModelManager> Minecraft_modelManager = new AccessTransformerField<>(
			ReflectionHelper.findField(Minecraft.class, "modelManager", "field_175617_aL", "aP"));
	public static AccessTransformerField<List<IResourcePack>> FallbackResourceManager_resourcePacks = new AccessTransformerField<>(
			ReflectionHelper.findField(FallbackResourceManager.class, "resourcePacks", "field_110540_a"));
	public static AccessTransformerField<Map<String, FallbackResourceManager>> SimpleReloadableResourceManager_domainResourceManagers = new AccessTransformerField<>(
			ReflectionHelper.findField(SimpleReloadableResourceManager.class, "domainResourceManagers",
					"field_110548_a"));
	public static AccessTransformerField<Map<ResourceLocation, ITextureObject>> TextureManager_mapTextureObjects = new AccessTransformerField<>(
			ReflectionHelper.findField(TextureManager.class, "mapTextureObjects", "field_110585_a"));
	public static AccessTransformerField<Set<String>> SimpleReloadableResourceManager_setResourceDomains = new AccessTransformerField<>(
			ReflectionHelper.findField(SimpleReloadableResourceManager.class, "setResourceDomains", "field_135057_d"));

	private Field field;
	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public AccessTransformerField(Field field) {
		this.field = field;
		clazz = (Class<T>) field.getType();
	}

	public void set(Object owner, T value) {
		try {
			field.set(owner, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public T get(Object owner) {
		try {
			return clazz.cast(field.get(owner));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public double getDouble(Object owner) {
		try {
			return field.getDouble(owner);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
