package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.geom.Rectangle2D;
import java.io.Closeable;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.lwjgl.opengl.GL11;

import de.dasbabypixel.waveclient.module.core.gui.api.ScalingManager;
import de.dasbabypixel.waveclient.module.core.render.font.WaveFont;
import de.dasbabypixel.waveclient.module.core.util.Pair;
import de.dasbabypixel.waveclient.module.core.util.color.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class DynamicWaveFontRenderer implements Closeable {

	private final WaveFont font;
	private final Map<Character, Map<Integer, BitMap>> bitmaps = new ConcurrentHashMap<>();
	private final ExecutorService executor;
	private final Collection<Future<?>> futures = ConcurrentHashMap.newKeySet();
	private final Collection<Pair<Integer, Character>> generatingBitmaps = ConcurrentHashMap.newKeySet();

	public final EmptyBitMap emptyBitMap = new EmptyBitMap();

	public DynamicWaveFontRenderer(WaveFont font) {
		this.font = font;
		this.executor = Executors.newWorkStealingPool();
	}

	/**
	 * @param string
	 * @param x
	 * @param y
	 * @param color
	 * @deprecated generates alot of bitmaps
	 */
	@Deprecated
	public void ddrawString(String string, double x, double y,
			de.dasbabypixel.waveclient.module.core.util.color.Color color) {
		this.ddrawString(string, x, y, color, getBitMap((int) (ScalingManager.getTrueScale() * 8) + 1));
	}

	/**
	 * @param string
	 * @param x
	 * @param y
	 * @param color
	 * @param lookupMaps
	 * @deprecated multiple lookupMaps are not good
	 */
	@Deprecated
	public void ddrawString(String string, double x, double y,
			de.dasbabypixel.waveclient.module.core.util.color.Color color, BitMap... lookupMaps) {
		GlStateManager.pushMatrix();
		double s = 1.0 / ScalingManager.getTrueScale();
		GlStateManager.scale(s, s, s);

		int offsetX = 0;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer wr = tessellator.getWorldRenderer();
		final float r = color.getRedFloat();
		final float g = color.getGreenFloat();
		final float b = color.getBlueFloat();
		final float a = color.getAlphaFloat();

		for (char c : string.toCharArray()) {
			final double posX = x + offsetX;
			final double posY = y;
			BitMap map = bitMap(c, lookupMaps);
			map.texture().bind();
			Rectangle2D tc = map.getGlyphCoords(c);
			Glyph glyph = map.getGlyph(c);

			wr.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);

			double tlX = posX;
			double trX = posX + glyph.getMaxWidth();
			double blX = posX;
			double brX = posX + glyph.getMaxWidth();
			double tlY = posY;
			double trY = posY;
			double blY = posY + glyph.getMaxHeight();
			double brY = posY + glyph.getMaxHeight();

			wr.pos(tlX, tlY, 0.0).tex(tc.getMinX(), tc.getMinY()).color(r, g, b, a).endVertex();
			wr.pos(blX, blY, 0.0).tex(tc.getMinX(), tc.getMaxY()).color(r, g, b, a).endVertex();
			wr.pos(trX, trY, 0.0).tex(tc.getMaxX(), tc.getMinY()).color(r, g, b, a).endVertex();
			wr.pos(brX, brY, 0.0).tex(tc.getMaxX(), tc.getMaxY()).color(r, g, b, a).endVertex();
			tessellator.draw();
			offsetX += glyph.getWidth();
		}
		GlStateManager.popMatrix();
	}

	/**
	 * @param string
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param color
	 * @param precision
	 * @deprecated stretches the text
	 */
	@Deprecated
	public void ddrawString(String string, double x, double y, double w, double h,
			de.dasbabypixel.waveclient.module.core.util.color.Color color, int precision) {
		if (string.isEmpty()) {
			return;
		}
		int offsetX = 0;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer wr = tessellator.getWorldRenderer();
		final float r = color.getRedFloat();
		final float g = color.getGreenFloat();
		final float b = color.getBlueFloat();
		final float a = color.getAlphaFloat();

		final BitMap lookupMaps = getBitMap(precision);

		double stringw = stringWidth(string, lookupMaps);
		double stringh = stringHeight(string, lookupMaps);

		double widthMult = w / stringw;
		double heightMult = h / stringh;
		for (char c : string.toCharArray()) {
			BitMap map = bitMap(c, lookupMaps);
			map.texture().bind();
			final double posX = x + offsetX;
			final double posY = y;

			Glyph glyph = map.getGlyph(c);
			Rectangle2D tc = map.getGlyphCoords(c);

			wr.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);

			double tlX = posX;
			double trX = posX + glyph.getMaxWidth();
			double blX = posX;
			double brX = posX + glyph.getMaxWidth();
			double tlY = posY;
			double trY = posY;
			double blY = posY + glyph.getMaxHeight();
			double brY = posY + glyph.getMaxHeight();

			wr.pos(tlX * widthMult, tlY * heightMult, 0.0)
					.tex(tc.getMinX(), tc.getMinY())
					.color(r, g, b, a)
					.endVertex();
			wr.pos(blX * widthMult, blY * heightMult, 0.0)
					.tex(tc.getMinX(), tc.getMaxY())
					.color(r, g, b, a)
					.endVertex();
			wr.pos(trX * widthMult, trY * heightMult, 0.0)
					.tex(tc.getMaxX(), tc.getMinY())
					.color(r, g, b, a)
					.endVertex();
			wr.pos(brX * widthMult, brY * heightMult, 0.0)
					.tex(tc.getMaxX(), tc.getMaxY())
					.color(r, g, b, a)
					.endVertex();
			tessellator.draw();
			offsetX += glyph.getWidth();

		}
	}

	public void drawString(String string, double x, double y) {
		drawString(string, x, y, null);
	}

	public void drawString(String string, double x, double y, Color color) {
		drawString(string, x, y, color, 9);
	}

	public void drawString(String string, double x, double y, int size) {
		drawString(string, x, y, null, size);
	}

	public void drawString(String string, double x, double y, double scale, Color color) {
		drawString(string, x, y, scale, color, 9);
	}

	public void drawString(String string, double x, double y, Color color, int size) {
		drawString(string, x, y, 1, color, size);
	}

	public void drawString(String string, double x, double y, double scale, Color color, int size) {
		drawString(string, x, y, color, generateGlyphLookup(string, (int) (size * scale)));
	}

	public void drawCenteredString(String string, double x, double y, double w, double h, int size) {
		drawCenteredString(string, x, y, w, h, null, size);
	}

	public void drawCenteredString(String string, double x, double y, double w, double h, Color color, int size) {
		drawString(string, x + w / 2 - stringWidth(string, size) / 2 / ScalingManager.getTrueScale(),
				y + h / 2 - stringHeight(string, size) / 2 / ScalingManager.getTrueScale(), color, size);
	}

	public void drawString(String string, double x, double y, Color color, GlyphLookup lookup) {
		double s = 1.0 / ScalingManager.getTrueScale();

		double offsetx = 0;
		final boolean nullColor = color == null;
		if (color == null) {
			color = new Color(255, 255, 0, 255);
		}
		final float r = color.getRedFloat(), g = color.getGreenFloat(), b = color.getBlueFloat(),
				a = color.getAlphaFloat();
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();

		int size = lookup.getGlyphSize();
		double expectedWidth = stringWidth(string, size);
		double expectedHeight = stringHeight(string, size);
		double width = stringWidth(string, lookup);
		double height = stringHeight(string, lookup);

		double widthStretch = expectedWidth / width;
		double heightStretch = expectedHeight / height;

		for (char c : string.toCharArray()) {
			final double posX = x + offsetx;
			final double posY = y;
			lookup.bindGlyph(c);
			Glyph glyph = lookup.getGlyph(c);
			Rectangle2D tc = lookup.getGlyphTexCoords(c);

			wr.begin(GL11.GL_TRIANGLE_STRIP,
					nullColor ? DefaultVertexFormats.POSITION_TEX : DefaultVertexFormats.POSITION_TEX_COLOR);

			double tlX = posX;
			double trX = posX + glyph.getMaxWidth() * s;
			double blX = posX;
			double brX = posX + glyph.getMaxWidth() * s;
			double tlY = posY;
			double trY = posY;
			double blY = posY + glyph.getMaxHeight() * s;
			double brY = posY + glyph.getMaxHeight() * s;

			wr.pos(tlX * widthStretch, tlY * heightStretch, 0.0).tex(tc.getMinX(), tc.getMinY());
			if (!nullColor)
				wr.color(r, g, b, a);
			wr.endVertex();
			wr.pos(blX * widthStretch, blY * heightStretch, 0.0).tex(tc.getMinX(), tc.getMaxY());
			if (!nullColor)
				wr.color(r, g, b, a);
			wr.endVertex();
			wr.pos(trX * widthStretch, trY * heightStretch, 0.0).tex(tc.getMaxX(), tc.getMinY());
			if (!nullColor)
				wr.color(r, g, b, a);
			wr.endVertex();
			wr.pos(brX * widthStretch, brY * heightStretch, 0.0).tex(tc.getMaxX(), tc.getMaxY());
			if (!nullColor)
				wr.color(r, g, b, a);
			wr.endVertex();
			tess.draw();
			offsetx += glyph.getWidth() * s;
		}
	}

	public double stringHeight(String string, int glyphSize) {
		double height = 0;
		for (char c : string.toCharArray()) {
			GlyphData glyph = new SimpleGlyphData(this, c, glyphSize);
			height = Math.max(height, glyph.getMaxHeight());
		}
		return height;
	}

	public double stringWidth(String string, int glyphSize) {
		double width = 0;
		double maxWidth = 0;
		for (char c : string.toCharArray()) {
			GlyphData glyph = new SimpleGlyphData(this, c, glyphSize);
			maxWidth = Math.max(maxWidth, glyph.getMaxWidth());
			width += glyph.getWidth();
		}
		return Math.max(width, maxWidth);
	}

	public double stringWidth(String string, GlyphLookup lookup) {
		double width = 0;
		double maxWidth = 0;
		for (char c : string.toCharArray()) {
			Glyph glyph = lookup.getGlyph(c);
			maxWidth = Math.max(maxWidth, glyph.getMaxWidth());
			width += glyph.getWidth();
		}
		return Math.max(width, maxWidth);
	}

	public double stringHeight(String string, GlyphLookup lookup) {
		double height = 0;
		for (char c : string.toCharArray()) {
			Glyph glyph = lookup.getGlyph(c);
			height = Math.max(height, glyph.getMaxHeight());
		}
		return height;
	}

	public GlyphLookup generateGlyphLookup(String string, int size) {
		return new SimpleGlyphLookup(getBitMap(size), emptyBitMap);
	}

	private synchronized void generateBitMap(int fontSize, char startChar) {
		Pair<Integer, Character> p = new Pair<>(fontSize, startChar);
		if (generatingBitmaps.contains(p)) {
			return;
		}
		if (this.executor.isShutdown()) {
			return;
		}
		generatingBitmaps.add(p);
		Future<?> future = this.executor.submit(() -> {
			try {
				SimpleBitMap map = new SimpleBitMap(this, fontSize, startChar);
				this.bitmaps.get(startChar).put(fontSize, map);
				map.texture().uploadImage(map.image());
//				ImageIO.write(map.image(), "png", new File("bitmap" + fontSize + ".png"));
//				CoreModule.getInstance().getLogger().info("Generated bitmap with size " + fontSize);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		});
		this.futures.add(future);
	}

	@Deprecated
	public double stringWidth(String string, BitMap... lookupMaps) {
		double width = 0;
		double maxWidth = 0;
		for (char c : string.toCharArray()) {
			BitMap map = bitMap(c, lookupMaps);
			Glyph glyph = map.getGlyph(c);
			maxWidth = Math.max(maxWidth, glyph.getMaxWidth());
			width += glyph.getWidth();
		}
		return Math.max(width, maxWidth);
	}

	@Deprecated
	public double charWidth(char c, BitMap... lookupMaps) {
		return stringWidth(Character.toString(c), lookupMaps);
	}

	@Deprecated
	public double stringHeight(String string, BitMap... lookupMaps) {
		double height = 0;
		for (char c : string.toCharArray()) {
			BitMap map = bitMap(c, lookupMaps);
			Glyph glyph = map.getGlyph(c);
			height = Math.max(c, glyph.getMaxHeight());
		}
		return height;
	}

	@Deprecated
	private BitMap bitMap(char c, BitMap... lookupMaps) {
		BitMap map = findLookup(c, lookupMaps);
		if (map == null) {
			System.out.println("Could not find bitmap for '" + c + "'. Generating BitMap... with fontSize 15");
			map = getBitMap(c, 15);
		}
		return map;
	}

	@Deprecated
	private BitMap findLookup(char c, BitMap... lookupMaps) {
		for (BitMap map : lookupMaps) {
			if (map.hasGlyph(c)) {
				return map;
			}
		}
		return null;
	}

	public BitMap getBitMap(int fontSize) {
		return getBitMap((char) 0, fontSize);
	}

	public BitMap getBitMap(char startChar, int fontSize) {
		Map<Integer, BitMap> map = bitmaps.get(startChar);
		if (!bitmaps.containsKey(startChar)) {
			bitmaps.put(startChar, new ConcurrentHashMap<>());
			map = bitmaps.get(startChar);
		}
		if (map.containsKey(fontSize)) {
			return map.get(fontSize);
		}
		return findBestBitMapForSize(map, fontSize, startChar);
	}

	private BitMap findBestBitMapForSize(Map<Integer, BitMap> maps, int fontSize, char startChar) {
		generateBitMap(fontSize, startChar);
		if (maps.isEmpty()) {
			return emptyBitMap;
		}
		NavigableSet<Integer> sorted = new TreeSet<>();
		for (int key : maps.keySet()) {
			sorted.add(key);
		}
		int best = sorted.pollLast();
		int tmp;
		while (!sorted.isEmpty()) {
			tmp = sorted.pollLast();
			if (tmp < fontSize) {
				break;
			}
			if (tmp == fontSize) {
				best = tmp;
				break;
			}
			if (tmp > fontSize) {
				best = tmp;
			}
		}
		return maps.get(best);
	}

	@Override
	public void close() {
		this.executor.shutdownNow();
		for (Future<?> future : this.futures) {
			future.cancel(true);
		}
		this.futures.clear();
		this.bitmaps.values().forEach(map -> {
			map.values().forEach(bitmap -> {
				bitmap.texture().delete();
			});
		});
		this.emptyBitMap.texture().delete();
	}

	public WaveFont getFont() {
		return font;
	}
}
