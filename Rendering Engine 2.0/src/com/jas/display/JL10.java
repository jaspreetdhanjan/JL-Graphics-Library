package com.jas.display;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.*;

import javax.swing.JFrame;

public class JL10 {
	private static class Texture {
		public int w, h;
		public int[] pixels;
	}

	private static class Display extends Canvas {
		private static final long serialVersionUID = 1L;

		public int w, h;
		public boolean isResizable;
		public boolean isVisible;
		public String title = "";
		public int numBuffers = 3;
		public BufferedImage screenImage;

		public void createDisplay() {
			Dimension d = new Dimension(w, h);

			setMinimumSize(d);
			setMaximumSize(d);
			setPreferredSize(d);

			JFrame frame = new JFrame(title);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.add(this, BorderLayout.CENTER);
			frame.pack();
			frame.setResizable(isResizable);
			frame.setLocationRelativeTo(null);
			frame.setVisible(isVisible);
		}

		public void render() {
			BufferStrategy bs = getBufferStrategy();
			if (bs == null) {
				createBufferStrategy(numBuffers);
				return;
			}

			Graphics g = bs.getDrawGraphics();
			g.drawImage(screenImage, 0, 0, w, h, null);
			g.dispose();
			bs.show();
		}
	}

	public static final int JL_DIMENSION = 0;
	public static final int JL_RESIZABLE = 1;
	public static final int JL_VISIBLE = 2;
	public static final int JL_NUM_BUFFERS = 3;
	public static final int JL_CREATE = 4;
	public static final int JL_SRC = 5;

	private static final List<Display> displayConfigurations = new ArrayList<Display>();
	private static final List<Texture> textureConfigurations = new ArrayList<Texture>();

	private static int[] pixels;
	private static int currentColor = 0xffffffff;
	private static int xScale = 1, yScale = 1;
	private static int xOffs = 0, yOffs = 0;

	private JL10() {
	}

	// Util stuff

	/**
	 * This method updates the current display configuration. Must be called every tick.
	 */
	public static void jlUpdate() {
		getLatestDisplayConfiguration().render();
	}

	/**
	 * This method clears the screen with the color designated used by jlColor4i()/jlColor4f().
	 */
	public static void jlClearScreen() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0xff000000;
		}
	}

	/**
	 * Scales all drawn vertices on the screen with integer precision.
	 */
	public static void jlScale(int xScale, int yScale) {
		if (xScale < 1) xScale = 1;
		if (yScale < 1) yScale = 1;

		JL10.xScale = xScale;
		JL10.yScale = yScale;
	}

	/**
	 * Translates all drawn vertices on the screen with integer precision.
	 */
	public static void jlTranslate(int xOffs, int yOffs) {
		JL10.xOffs = xOffs;
		JL10.yOffs = yOffs;
	}

	/**
	 * Colors all draw vertices on the screen from value 0 to 255.
	 */
	public static void jlColor4i(int r, int g, int b, int a) {
		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		if (r > 255) r = 255;
		if (g > 255) g = 255;
		if (b > 255) b = 255;

		currentColor = (a << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * Colors all draw vertices on the screen from value 0f to 1f.
	 */
	public static void jlColor4f(float r, float g, float b, float a) {
		int rr = (int) r * 255;
		int gg = (int) g * 255;
		int bb = (int) b * 255;
		int aa = (int) a * 255;

		currentColor = (aa << 24) | (rr << 16) | (gg << 8) | bb;
	}

	/**
	 * Sets a pixel on the screen to a specific color designated by jlColor4i()/ jlColor4f().
	 */
	public static void jlSetPixel(int x, int y) {
		Display config = getLatestDisplayConfiguration();

		int xx = x + xOffs;
		int yy = y + yOffs;

		if (xx < 0 || yy < 0 || xx >= config.w || yy >= config.h) return;
		pixels[xx + yy * config.w] = currentColor;
	}

	// Display stuff

	/**
	 * @return the current display configuration.
	 */
	private static Display getLatestDisplayConfiguration() {
		return displayConfigurations.get(displayConfigurations.size() - 1);
	}

	/**
	 * @return A new display configuration.
	 */
	public static int jlNewDisplay() {
		displayConfigurations.add(new Display());
		return displayConfigurations.size() - 1;
	}

	/**
	 * Destroys a display configuration.
	 */
	public static void jlDestroyDisplay(int display) {
		displayConfigurations.remove(display);
	}

	/**
	 * Sets the required numerical display parameters.
	 */
	public static void jlDisplayConfiguration(int display, int mode, int[] data) {
		Display config = displayConfigurations.get(display);

		if (mode == JL_DIMENSION) {
			config.w = data[0];
			config.h = data[1];
			config.screenImage = new BufferedImage(config.w, config.h, BufferedImage.TYPE_INT_ARGB);
			pixels = ((DataBufferInt) config.screenImage.getRaster().getDataBuffer()).getData();
		} else if (mode == JL_NUM_BUFFERS) {
			config.numBuffers = data[0];
		} else {
			throw new RuntimeException("Invalid constant!");
		}
	}

	/**
	 * Sets the required boolean display parameters.
	 */
	public static void jlDisplayConfiguration(int display, int mode, boolean b) {
		Display config = displayConfigurations.get(display);

		if (mode == JL_RESIZABLE) {
			config.isResizable = b;
		} else if (mode == JL_VISIBLE) {
			config.isVisible = b;
		} else if (mode == JL_CREATE) {
			config.createDisplay();
		} else {
			throw new RuntimeException("Invalid constant!");
		}
	}

	// Shape drawing stuff

	public static void jlBox(int xx0, int yy0, int xx1, int yy1) {
		Display config = getLatestDisplayConfiguration();

		int x0 = xx0 + xOffs;
		int x1 = xx1 + xOffs;
		int y0 = yy0 + yOffs;
		int y1 = yy1 + yOffs;

		for (int x = x0; x < x1 * xScale; x++) {
			if (x < 0 || x >= config.w || y0 < 0 || y0 >= config.h) continue;
			pixels[x + y0 * config.w] = currentColor;

			if (y1 < 0 || y1 >= config.h) continue;
			pixels[x + (y0 + (y1 - y0) * yScale) * config.w] = currentColor;
		}
		for (int y = y0; y < y1 * yScale; y++) {
			if (y < 0 || y >= config.h || x0 < 0 || x0 >= config.w) continue;
			pixels[x0 + y * config.w] = currentColor;

			if (x1 < 0 || x1 >= config.w) continue;
			pixels[(x0 + (x1 - x0) * xScale) + y * config.w] = currentColor;
		}
	}

	public static void jlQuad(int xx0, int yy0, int xx1, int yy1) {
		Display config = getLatestDisplayConfiguration();

		int x0 = xx0 + xOffs;
		int x1 = xx1 + xOffs;
		int y0 = yy0 + yOffs;
		int y1 = yy1 + yOffs;

		for (int y = y0; y < y1 * yScale; y++) {
			if (y < 0 || y >= config.h) continue;
			for (int x = x0; x < x1 * xScale; x++) {
				if (x < 0 || x >= config.w) continue;
				pixels[x + y * config.w] = currentColor;
			}
		}
	}

	public static void jlLine(int xx0, int yy0, int xx1, int yy1) {
		Display config = getLatestDisplayConfiguration();

		int x0 = xx0 + xOffs;
		int x1 = xx1 + xOffs;
		int y0 = yy0 + yOffs;
		int y1 = yy1 + yOffs;

		int dx = x1 - x0;
		int dy = y1 - y0;
		int D = 2 * dy - dx;

		int y = y0;

		for (int x = x0; x < x1 * xScale; x++) {
			if (D > 0) {
				y++;
				if (x < 0 || x >= config.w) continue;
				if (y < 0 || y >= config.h) continue;
				pixels[x + y * config.w] = currentColor;
				D += (2 * dy - 2 * dx);
			} else {
				if (x < 0 || x >= config.w) continue;
				if (y < 0 || y >= config.h) continue;
				pixels[x + y * config.w] = currentColor;
				D += 2 * dy;
			}
		}
	}

	// Texture stuff

	/**
	 * @return A new texture configuration.
	 */
	public static int jlNewTexture() {
		textureConfigurations.add(new Texture());
		return textureConfigurations.size() - 1;
	}

	/**
	 * Destroys a given texture.
	 */
	public static void jlDestroyTexture(int tex) {
		textureConfigurations.remove(tex);
	}

	/**
	 * Sets the required numerical texture parameters.
	 */
	public static void jlTextureConfiguration(int tex, int mode, int[] data) {
		Texture texture = textureConfigurations.get(tex);

		if (mode == JL_SRC) {
			texture.pixels = data;
		} else if (mode == JL_DIMENSION) {
			texture.w = data[0];
			texture.h = data[1];
		} else {
			throw new RuntimeException("Invalid constant!");
		}
	}

	/**
	 * Binds a texture to the drawn vertices. To "unbind" input a tex of "-1".
	 */

	public static void jlDrawTexture(int texture) {
		Display config = getLatestDisplayConfiguration();
		Texture sprite = textureConfigurations.get(texture);

		for (int y = 0; y < sprite.h * yScale; y++) {
			int yp = y + yOffs;
			if (yp < 0 || yp >= config.h) continue;
			for (int x = 0; x < sprite.w * xScale; x++) {
				int xp = x + xOffs;
				if (xp < 0 || xp >= config.w) continue;

				int tex = sprite.pixels[(x / xScale) + (y / yScale) * sprite.w];
				if (tex != 0) pixels[xp + yp * config.w] = tex & currentColor;
			}
		}
	}
}