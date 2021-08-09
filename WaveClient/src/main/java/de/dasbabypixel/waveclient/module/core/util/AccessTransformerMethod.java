package de.dasbabypixel.waveclient.module.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AccessTransformerMethod<T> {

	public static AccessTransformerMethod<Void> RenderItem_preTransform_ItemStack = new AccessTransformerMethod<>(
			ReflectionHelper.findMethod(RenderItem.class, (RenderItem) null, new String[] {
					"preTransform", "func_175046_c", "b"
			}, ItemStack.class));
	public static AccessTransformerMethod<Void> RenderItem_registerItem_Item_int_String = new AccessTransformerMethod<>(
			ReflectionHelper.findMethod(RenderItem.class, (RenderItem) null, new String[] {
					"registerItem", "func_175048_a", "a"
			}, Item.class, int.class, String.class));
	public static AccessTransformerMethod<Void> RenderItem_registerBlock_Block_int_String = new AccessTransformerMethod<>(
			ReflectionHelper.findMethod(RenderItem.class, (RenderItem) null, new String[] {
					"registerBlock", "func_175029_a", "a"
			}, Block.class, int.class, String.class));
	public static AccessTransformerMethod<Void> EntityRenderer_renderCloudsCheck_RenderGlobal_float_int = new AccessTransformerMethod<>(
			ReflectionHelper.findMethod(EntityRenderer.class, (EntityRenderer) null, new String[] {
					"renderCloudsCheck", "func_180437_a", "a"
			}, RenderGlobal.class, float.class, int.class));
	public static AccessTransformerMethod<Void> EntityRenderer_setupCameraTransform_float_int = new AccessTransformerMethod<>(
			ReflectionHelper.findMethod(EntityRenderer.class, (EntityRenderer) null, new String[] {
					"setupCameraTransform", "func_78479_a", "a"
			}, float.class, int.class));

	private Method method;
	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public AccessTransformerMethod(Method method) {
		this.method = method;
		clazz = (Class<T>) method.getReturnType();
	}

	public T invoke(Object owner, Object... args) {
		try {
			Object ret = method.invoke(owner, args);
			return clazz == null ? null : clazz.cast(ret);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
