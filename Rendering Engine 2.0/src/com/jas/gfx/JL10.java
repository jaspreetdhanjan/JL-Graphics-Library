package com.jas.gfx;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.*;
import java.util.*;

import javax.swing.JFrame;

public class JL10 {
	private static class Texture {
		public int w, h;
		public int[] pixels;

		public Texture() {
		}

		public Texture(int w, int h) {
			this.w = w;
			this.h = h;
			pixels = new int[w * h];
		}
	}

	private static class Display extends Canvas {
		private static final long serialVersionUID = 1L;

		public int w, h;
		public int scale;
		public boolean isResizable;
		public boolean isVisible;
		public String title = "";
		public int numBuffers = 3;
		public BufferedImage screenImage;

		public void createDisplay() {
			Dimension d = new Dimension(w * scale, h * scale);

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

		public void swapBuffers() {
			BufferStrategy bs = getBufferStrategy();
			if (bs == null) {
				createBufferStrategy(numBuffers);
				return;
			}

			Graphics g = bs.getDrawGraphics();
			g.drawImage(screenImage, 0, 0, w * scale, h * scale, null);
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
	public static final int JL_NULL = 6;

	private static final List<Display> DISPLAY_CONFIGURATIONS = new ArrayList<Display>();
	private static final List<Texture> TEXTURE_CONFIGURATIONS = new ArrayList<Texture>();
	private static final Texture NULL_TEXTURE = new Texture(1, 1);

	private static int[] pixels;
	private static int currentColour = 0xffffffff;
	private static int currentTexture = JL_NULL;
	private static int xScale = 1, yScale = 1;
	private static int xOffs = 0, yOffs = 0;

	private JL10() {
	}

	// Display stuff

	/**
	 * @return the current display configuration.
	 */
	private static Display getLatestDisplayConfiguration() {
		return DISPLAY_CONFIGURATIONS.get(DISPLAY_CONFIGURATIONS.size() - 1);
	}

	/**
	 * @return A new display configuration.
	 */
	public static int jlNewDisplay() {
		DISPLAY_CONFIGURATIONS.add(new Display());
		return DISPLAY_CONFIGURATIONS.size() - 1;
	}

	/**
	 * Destroys a display configuration.
	 */
	public static void jlDestroyDisplay(int display) {
		DISPLAY_CONFIGURATIONS.remove(display);
	}

	/**
	 * Sets the required numerical display parameters.
	 */
	public static void jlDisplayConfiguration(int display, int mode, int... data) {
		Display config = DISPLAY_CONFIGURATIONS.get(display);

		if (mode == JL_DIMENSION) {
			config.w = data[0];
			config.h = data[1];
			config.scale = data[2];

			config.screenImage = new BufferedImage(config.w, config.h, BufferedImage.TYPE_INT_ARGB);
			pixels = ((DataBufferInt) config.screenImage.getRaster().getDataBuffer()).getData();

			for (int i = 0; i < 1; i++) {
				NULL_TEXTURE.pixels[i] = 0xffffffff;
			}

		} else if (mode == JL_NUM_BUFFERS) {
			config.numBuffers = data[0];
		} else if (mode == JL_CREATE) {
			config.createDisplay();
		} else {
			throw new JLException("Invalid constant!");
		}
	}

	/**
	 * Sets the required boolean display parameters.
	 */
	public static void jlDisplayConfiguration(int display, int mode, boolean b) {
		Display config = DISPLAY_CONFIGURATIONS.get(display);

		if (mode == JL_RESIZABLE) {
			config.isResizable = b;
		} else if (mode == JL_VISIBLE) {
			config.isVisible = b;
		} else {
			throw new JLException("Invalid constant!");
		}
	}

	// Util stuff

	/**
	 * This method updates the current display configuration. Must be called every update.
	 */
	public static void jlSwapBuffers() {
		getLatestDisplayConfiguration().swapBuffers();
	}

	/**
	 * This method clears the buffer.
	 */
	public static void jlClearBuffer() {
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
	 * Colours all draw vertices on the screen from value 0 to 255.
	 */
	public static void jlColour4i(int r, int g, int b, int a) {
		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		if (r > 255) r = 255;
		if (g > 255) g = 255;
		if (b > 255) b = 255;

		currentColour = (a << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * Colours all draw vertices on the screen from value 0f to 1f.
	 */
	public static void jlColour4f(float r, float g, float b, float a) {
		int aa = (int) (a * 255f);
		int rr = (int) (r * 255f);
		int gg = (int) (g * 255f);
		int bb = (int) (b * 255f);

		currentColour = (aa << 24) | (rr << 16) | (gg << 8) | bb;
	}

	/**
	 * Sets a pixel on the screen to a specific colour designated by jlColour4i()/ jlColour4f().
	 */
	public static void jlSetPixel(int x, int y) {
		Display config = getLatestDisplayConfiguration();

		int xx = x + xOffs;
		int yy = y + yOffs;

		if (xx < 0 || yy < 0 || xx >= config.w || yy >= config.h) return;
		pixels[xx + yy * config.w] = currentColour;
	}

	// Shape drawing stuff

	/**
	 * Creates an unfilled box with min (xx0, yy0) to max (xx1, yy1).
	 */
	public static void jlDrawBox(int x0, int y0, int x1, int y1) {
		Display config = getLatestDisplayConfiguration();

		int xx0 = x0 + xOffs;
		int xx1 = x1 + xOffs;
		int yy0 = y0 + yOffs;
		int yy1 = y1 + yOffs;

		if (xx0 < 0) xx0 = 0;
		if (yy0 < 0) yy0 = 0;
		if (xx1 > config.w) xx1 = config.w;
		if (yy1 > config.h) yy1 = config.h;

		for (int y = yy0; y <= yy1; y++) {
			for (int x = xx0; x <= xx1; x++) {
				if (x == x0 || y == y0 || x == x1 || y == y1) pixels[x + y * config.w] = currentColour;
				if (y > y0 && y < y1 && x < x1 - 1) x = x1 - 1;
			}
		}
	}

	/**
	 * Creates a line from coordinate (x0, y0) to (x1, y1).
	 */
	public static void jlDrawLine(int x0, int y0, int x1, int y1) {
		Display config = getLatestDisplayConfiguration();

		int xx0 = x0 + xOffs;
		int xx1 = x1 + xOffs;
		int yy0 = y0 + yOffs;
		int yy1 = y1 + yOffs;

		int dx = xx1 - xx0;
		int dy = yy1 - yy0;
		int D = 2 * dy - dx;

		int y = yy0;

		for (int x = xx0; x < xx1 * xScale; x++) {
			if (D > 0) {
				y++;
				if (x < 0 || x >= config.w) continue;
				if (y < 0 || y >= config.h) continue;

				pixels[x + y * config.w] = currentColour;
				D += (2 * dy - 2 * dx);
			} else {
				if (x < 0 || x >= config.w) continue;
				if (y < 0 || y >= config.h) continue;

				pixels[x + y * config.w] = currentColour;
				D += 2 * dy;
			}
		}
	}

	/**
	 * Creates a filled box with min (x0, y0) to max (x1, y1).
	 */
	public static void jlDrawQuad(int x0, int y0, int x1, int y1) {
		Display config = getLatestDisplayConfiguration();
		Texture sprite = getTextureConfiguration(currentTexture);

		int xx0 = x0 + xOffs;
		int xx1 = x1 + xOffs;
		int yy0 = y0 + yOffs;
		int yy1 = y1 + yOffs;

		for (int y = yy0; y < yy1 * yScale; y++) {
			if (y < 0 || y >= config.h) continue;

			for (int x = xx0; x < xx1 * xScale; x++) {
				if (x < 0 || x >= config.w) continue;

				int colour = sprite.pixels[(x % sprite.w) + (y % sprite.h) * sprite.w];
				pixels[x + y * config.w] = colour & currentColour;
			}
		}
	}

	/**
	 * Creates a circle at coordinate (xp, yp) with radius r.
	 */
	public static void jlDrawCircle(int xp, int yp, int r) {
		Display config = getLatestDisplayConfiguration();
		Texture sprite = getTextureConfiguration(currentTexture);

		int x0 = xp + xOffs - r;
		int x1 = xp + xOffs + r;
		int y0 = yp + yOffs - r;
		int y1 = yp + yOffs + r;

		if (x0 < 0) x0 = 0;
		if (y0 < 0) y0 = 0;
		if (x1 > config.w) x1 = config.w;
		if (y1 > config.h) y1 = config.h;

		for (int y = y0; y < y1; y++) {
			int yd = y - yp;

			for (int x = x0; x < x1; x++) {
				int xd = x - xp;
				int dd = xd * xd + yd * yd;

				int colour = sprite.pixels[(x % sprite.w) + (y % sprite.h) * sprite.w];
				if (dd <= r * r) pixels[x + y * config.w] = colour & currentColour;
			}
		}
	}

	/**
	 * Draws an inputted polygon.
	 */
	public static void jlDrawPoly(JLPolygon p) {
		Display config = getLatestDisplayConfiguration();
		Texture sprite = getTextureConfiguration(currentTexture);

		for (int y = 0; y < config.h; y++) {
			for (int x = 0; x < config.w; x++) {
				int colour = sprite.pixels[(x % sprite.w) + (y % sprite.h) * sprite.w];
				if (p.checkPointContains(x, y)) pixels[x + y * config.w] = colour & currentColour;
			}
		}
	}

	// Texture stuff

	/**
	 * @return A new texture identifier.
	 */
	public static int jlNewTexture() {
		TEXTURE_CONFIGURATIONS.add(new Texture());
		return TEXTURE_CONFIGURATIONS.size() - 1;
	}

	/**
	 * Destroys a given texture.
	 */
	public static void jlDestroyTexture(int tex) {
		TEXTURE_CONFIGURATIONS.remove(tex);
	}

	/**
	 * Sets the required numerical texture parameters.
	 */
	public static void jlTextureConfiguration(int tex, int mode, int... data) {
		Texture texture = TEXTURE_CONFIGURATIONS.get(tex);

		if (mode == JL_SRC) {
			texture.pixels = data;
		} else if (mode == JL_DIMENSION) {
			texture.w = data[0];
			texture.h = data[1];
		} else {
			throw new JLException("Invalid constant!");
		}
	}

	/**
	 * Activates the created texture. All vertices draw after this is called will have a texture binded to them.
	 */
	public static void jlActivateTexture(int texture) {
		currentTexture = texture;
	}

	/**
	 * Deactivates any potential textures that could have been previously activated.
	 */
	public static void jlDeactivateTextures() {
		currentTexture = JL_NULL;
	}

	/**
	 * @return The TextureConfiguration that was specified in the parameter.
	 */
	private static Texture getTextureConfiguration(int texture) {
		if (texture == JL_NULL) return NULL_TEXTURE;
		if (texture < 0 || texture >= TEXTURE_CONFIGURATIONS.size()) throw new JLException("Invalid texture");
		return TEXTURE_CONFIGURATIONS.get(texture);
	}
}