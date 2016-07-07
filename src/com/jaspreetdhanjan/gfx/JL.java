package com.jaspreetdhanjan.gfx;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.*;
import java.util.*;

import javax.swing.JFrame;

public class JL {
	/**
	 * Holds display information required for creating display configurations.
	 * 
	 * @author Jaspreet Dhanjan
	 */
	private static class Display extends Canvas {
		private static final long serialVersionUID = 1L;

		public int screenWidth, screenHeight, scale;
		public boolean isResizable;
		public boolean isVisible;
		public String title = "";
		public int numBuffers = 3;

		public BufferedImage screenImage;

		public void createDisplay() {
			Dimension d = new Dimension(screenWidth * scale, screenHeight * scale);
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

			int w = screenWidth * scale;
			int h = screenHeight * scale;

			Graphics g = bs.getDrawGraphics();
			g.drawImage(screenImage, 0, 0, w, h, null);
			g.dispose();
			bs.show();
		}
	}

	/**
	 * Holds texture information required for creating texture configurations.
	 * 
	 * @author Jaspreet Dhanjan
	 */
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

	/**
	 * Holds point information required for coordinating polygons.
	 * 
	 * @author Jaspreet Dhanjan
	 */
	private static class Vertex {
		public int x, y;

		public Vertex(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Holds polygon information required for creating polygon configurations.
	 * 
	 * @author Jaspreet Dhanjan
	 */
	private static class Polygon {
		private List<Vertex> points = new ArrayList<Vertex>();

		public Polygon(Vertex... _points) {
			for (int i = 0; i < _points.length; i++) {
				points.add(_points[i]);
			}
		}

		public void addVertex(int x, int y) {
			points.add(new Vertex(x, y));
		}

		public int nodes() {
			return points.size();
		}
	}

	public static final int JL_DIMENSION = 0;
	public static final int JL_RESIZABLE = 1;
	public static final int JL_VISIBLE = 2;
	public static final int JL_NUM_BUFFERS = 3;
	public static final int JL_CREATE = 4;
	public static final int JL_SRC = 5;
	public static final int JL_NULL = 6;
	public static final int JL_TRUE = 7;
	public static final int JL_FALSE = 8;
	public static final int JL_SCALE = 9;
	public static final int JL_TRANSLATE = 10;
	public static final int JL_ADD = 11;

	private static final Texture NULL_TEXTURE = new Texture(1, 1);

	private static final List<Texture> TEXTURE_CONFIGURATIONS = new ArrayList<Texture>();
	private static final List<Polygon> POLY_CONFIGURATIONS = new ArrayList<Polygon>();

	private static Display displayConfiguration;
	private static int[] pixels;
	private static int currentColour = 0xffffffff;
	private static int currentTexture = JL_NULL;
	private static int xScale = 1, yScale = 1;
	private static int xOffs = 0, yOffs = 0;
	private static int texScaleX = 1, texScaleY = 1;
	private static int texTransX = 0, texTransY = 0;

	private JL() {
	}

	// Display stuff

	/**
	 * Creates a new display.
	 * 
	 * @return A new display configuration.
	 */
	public static int jlNewDisplay() {
		displayConfiguration = new Display();
		int id = (int) (Math.random() * 255);
		return id & 0xff;
	}

	/**
	 * Destroys a display configuration.
	 * 
	 * @param display
	 *            the display you would like to destroy.
	 */
	public static void jlDestroyDisplay(int display) {
		display = JL_NULL;
		displayConfiguration = null;
	}

	/**
	 * Sets the required display parameters.
	 * 
	 * @param display
	 *            the display you would like to configure.
	 * @param choose
	 *            from the JL constants what mode of the display you would like to configure.
	 * @param data
	 *            the data required to set the configuration.
	 */
	public static void jlDisplayConfiguration(int display, int mode, int... data) {
		if (display == JL_NULL) throw JLException.OBJECT_NOT_CREATED;

		switch (mode) {
			case JL_DIMENSION: {
				int scale = 1;
				if (data.length == 3) scale = data[2];

				displayConfiguration.screenWidth = data[0];
				displayConfiguration.screenHeight = data[1];
				displayConfiguration.scale = scale;
				displayConfiguration.screenImage = new BufferedImage(displayConfiguration.screenWidth, displayConfiguration.screenHeight, BufferedImage.TYPE_INT_ARGB);
				pixels = ((DataBufferInt) displayConfiguration.screenImage.getRaster().getDataBuffer()).getData();
				NULL_TEXTURE.pixels[0] = 0xffffffff;
				break;
			}
			case JL_NUM_BUFFERS: {
				displayConfiguration.numBuffers = data[0];
				break;
			}
			case JL_CREATE: {
				displayConfiguration.createDisplay();
				break;
			}
			case JL_RESIZABLE: {
				displayConfiguration.isResizable = data[0] == JL_TRUE;
				break;
			}
			case JL_VISIBLE: {
				displayConfiguration.isVisible = data[0] == JL_TRUE;
				break;
			}
			default: {
				throw JLException.INVALID_CONSTANT_EXCEPTION;
			}
		}
	}

	// Util stuff

	/**
	 * Updates the current display configuration. Must be called every update.
	 */
	public static void jlSwapBuffers() {
		displayConfiguration.swapBuffers();
	}

	/**
	 * Clears the screen buffer.
	 */
	public static void jlClearBuffer() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0xffffff;
		}
	}

	/**
	 * Scales all drawn vertices on the screen with integer precision.
	 * 
	 * @param xScale
	 *            scales the size along the x-axis.
	 * @param yScale
	 *            scales the size along the y-axis.
	 */
	public static void jlScale(int xScale, int yScale) {
		if (xScale < 1) xScale = 1;
		if (yScale < 1) yScale = 1;

		JL.xScale = xScale;
		JL.yScale = yScale;
	}

	/**
	 * Translates all drawn vertices on the screen with integer precision.
	 * 
	 * @param xOffs
	 *            translates the position along the x-axis.
	 * @param yOffs
	 *            translates the position along the y-axis.
	 */
	public static void jlTranslate(int xOffs, int yOffs) {
		JL.xOffs = xOffs;
		JL.yOffs = yOffs;
	}

	/**
	 * Colours all draw vertices on the screen from value 0 to 255.
	 * 
	 * @param r
	 *            red channel.
	 * @param g
	 *            green channel.
	 * @param b
	 *            blue channel.
	 * @param a
	 *            alpha channel.
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
	 * 
	 * @param r
	 *            red channel.
	 * @param g
	 *            green channel.
	 * @param b
	 *            blue channel.
	 * @param a
	 *            alpha channel.
	 */
	public static void jlColour4f(float r, float g, float b, float a) {
		int _a = (int) (a * 255f);
		int _r = (int) (r * 255f);
		int _g = (int) (g * 255f);
		int _b = (int) (b * 255f);

		currentColour = (_a << 24) | (_r << 16) | (_g << 8) | _b;
	}

	/**
	 * Sets a pixel on the screen to a specific colour designated by jlColour4i()/ jlColour4f().
	 * 
	 * @param x
	 *            the x location in Cartesian coordinates of the pixel.
	 * @param y
	 *            the y location in Cartesian coordinates of the pixel.
	 */
	public static void jlSetPixel(int x, int y) {
		int xx = x + xOffs;
		int yy = y + yOffs;
		if (xx >= 0 || yy >= 0 || xx < displayConfiguration.screenWidth || yy < displayConfiguration.screenHeight) {
			pixels[xx + yy * displayConfiguration.screenWidth] = currentColour;
		}
	}

	// Shape drawing stuff

	/**
	 * Creates an unfilled rectangle with a specific colour designated by jlColour4i()/ jlColour4f().
	 * 
	 * @param x0
	 *            the x location in Cartesian coordinates of the top left corner of the rectangle.
	 * @param y0
	 *            the y location in Cartesian coordinates of the top left corner of the rectangle.
	 * @param x1
	 *            the x location in Cartesian coordinates of the bottom right corner of the rectangle.
	 * @param y1
	 *            the y location in Cartesian coordinates of the bottom right corner of the rectangle.
	 */
	public static void jlDrawBox(int x0, int y0, int x1, int y1) {
		x1 *= xScale;
		y1 *= yScale;

		int xx0 = x0 + xOffs;
		int xx1 = x1 + xOffs;
		int yy0 = y0 + yOffs;
		int yy1 = y1 + yOffs;

		if (xx0 < 0) xx0 = 0;
		if (yy0 < 0) yy0 = 0;
		if (xx1 > displayConfiguration.screenWidth) xx1 = displayConfiguration.screenWidth;
		if (yy1 > displayConfiguration.screenHeight) yy1 = displayConfiguration.screenHeight;

		for (int y = yy0; y <= yy1; y++) {
			for (int x = xx0; x <= xx1; x++) {
				if (x < 0 || y < 0 || x >= displayConfiguration.screenWidth || y >= displayConfiguration.screenHeight) continue;

				if (x == x0 || y == y0 || x == x1 || y == y1) pixels[x + y * displayConfiguration.screenWidth] = currentColour;
				if (y > y0 && y < y1 && x < x1 - 1) x = x1 - 1;
			}
		}
	}

	/**
	 * Creates a line from coordinate (x0, y0) to (x1, y1).
	 * 
	 * @param x0
	 *            the x location in Cartesian coordinates of one point of the line.
	 * @param y0
	 *            the y location in Cartesian coordinates of one point of the line.
	 * @param x1
	 *            the x location in Cartesian coordinates of the other point of the line.
	 * @param y1
	 *            the y location in Cartesian coordinates of the other point of the line.
	 */
	public static void jlDrawLine(int x0, int y0, int x1, int y1) {
		x1 *= xScale;
		y1 *= yScale;

		int xx0 = x0 + xOffs;
		int xx1 = x1 + xOffs;
		int yy0 = y0 + yOffs;
		int yy1 = y1 + yOffs;

		int dx = xx1 - xx0;
		int dy = yy1 - yy0;
		int D = 2 * dy - dx;

		int y = yy0;

		for (int x = xx0; x < xx1; x++) {
			if (D > 0) {
				y++;
				if (x < 0 || x >= displayConfiguration.screenWidth) continue;
				if (y < 0 || y >= displayConfiguration.screenHeight) continue;
				pixels[x + y * displayConfiguration.screenWidth] = currentColour;

				D += (2 * dy - 2 * dx);
			} else {
				if (x < 0 || x >= displayConfiguration.screenWidth) continue;
				if (y < 0 || y >= displayConfiguration.screenHeight) continue;
				pixels[x + y * displayConfiguration.screenWidth] = currentColour;

				D += 2 * dy;
			}
		}
	}

	/**
	 * Creates an rectangle with a specific colour designated by jlColour4i()/ jlColour4f().
	 * 
	 * @param x0
	 *            the x location in Cartesian coordinates of the top left corner of the rectangle.
	 * @param y0
	 *            the y location in Cartesian coordinates of the top left corner of the rectangle.
	 * @param x1
	 *            the x location in Cartesian coordinates of the bottom right corner of the rectangle.
	 * @param y1
	 *            the y location in Cartesian coordinates of the bottom right corner of the rectangle.
	 */
	public static void jlDrawQuad(int x0, int y0, int x1, int y1) {
		Texture sprite = getTextureConfiguration(currentTexture);

		int xx0 = x0 + xOffs;
		int xx1 = (x1 + xOffs) * xScale;
		int yy0 = y0 + yOffs;
		int yy1 = (y1 + yOffs) * yScale;

		for (int y = yy0; y < yy1; y++) {
			for (int x = xx0; x < xx1; x++) {
				if (x < 0 || y < 0 || x >= displayConfiguration.screenWidth || y >= displayConfiguration.screenHeight) continue;

				int xo = x + texTransX;
				int yo = y + texTransY;
				int u = (xo / texScaleX) % sprite.w;
				int v = (yo / texScaleY) % sprite.h;
				int src = sprite.pixels[u + v * sprite.w];
				pixels[x + y * displayConfiguration.screenWidth] = src & currentColour;
			}
		}
	}

	/**
	 * Creates an circle with a specific colour designated by jlColour4i()/ jlColour4f().
	 * 
	 * @param xp
	 *            the x location in Cartesian coordinates of the center of the circle.
	 * @param yp
	 *            the y location in Cartesian coordinates of the center of the circle.
	 * @param r
	 *            the radius of the circle.
	 */
	public static void jlDrawCircle(int xp, int yp, int r) {
		Texture sprite = getTextureConfiguration(currentTexture);

		int x0 = xp + xOffs - r;
		int x1 = xp + xOffs + r;
		int y0 = yp + yOffs - r;
		int y1 = yp + yOffs + r;

		if (x0 < 0) x0 = 0;
		if (y0 < 0) y0 = 0;
		if (x1 > displayConfiguration.screenWidth) x1 = displayConfiguration.screenWidth;
		if (y1 > displayConfiguration.screenHeight) y1 = displayConfiguration.screenHeight;

		for (int y = y0; y < y1 * yScale; y++) {
			for (int x = x0; x < x1 * xScale; x++) {
				if (x < 0 || y < 0 || x >= displayConfiguration.screenWidth || y >= displayConfiguration.screenHeight) continue;

				int xd = x - xp;
				int yd = y - yp;
				int dd = (xd * xd + yd * yd);
				if (dd >= r * r) continue;

				int xo = x + texTransX;
				int yo = y + texTransY;
				int u = (xo / texScaleX) % sprite.w;
				int v = (yo / texScaleY) % sprite.h;
				int src = sprite.pixels[u + v * sprite.w];
				pixels[x + y * displayConfiguration.screenWidth] = src & currentColour;
			}
		}
	}

	// Texture stuff

	/**
	 * Creates a new texture.
	 * 
	 * @return A new texture configuration.
	 */
	public static int jlNewTexture() {
		TEXTURE_CONFIGURATIONS.add(new Texture());
		return TEXTURE_CONFIGURATIONS.size() - 1;
	}

	/**
	 * Destroys a given texture.
	 * 
	 * @param texture
	 *            the texture you would like to destroy.
	 */
	public static void jlDestroyTexture(int texture) {
		if (texture == currentTexture) throw JLException.ACTIVE_TEXTURE_EXCEPTION;
		TEXTURE_CONFIGURATIONS.remove(texture);
	}

	/**
	 * Sets the required display parameters.
	 * 
	 * @param display
	 *            the display you would like to configure.
	 * @param choose
	 *            from the JL constants what mode of the display you would like to configure.
	 * @param data
	 *            the data required to set the configuration.
	 */
	public static void jlTextureConfiguration(int texture, int mode, int... data) {
		Texture tex = getTextureConfiguration(texture);

		switch (mode) {
			case JL_SRC: {
				tex.pixels = data;
				break;
			}
			case JL_DIMENSION: {
				tex.w = data[0];
				tex.h = data[1];
				break;
			}
			case JL_SCALE: {
				int xScale = data[0];
				int yScale = data[1];
				if (xScale <= 0) xScale = 1;
				if (yScale <= 0) yScale = 1;
				texScaleX = xScale;
				texScaleY = yScale;
				break;
			}
			case JL_TRANSLATE: {
				texTransX = data[0];
				texTransY = data[1];
				break;
			}
			default: {
				throw JLException.INVALID_CONSTANT_EXCEPTION;
			}
		}
	}

	/**
	 * Activates the created texture. All vertices draw after this is called will have a texture binded to them.
	 * 
	 * @param texture
	 *            the texture you would like to activate.
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

	private static Texture getTextureConfiguration(int texture) {
		if (texture == JL_NULL) return NULL_TEXTURE;
		if (texture < 0 || texture >= TEXTURE_CONFIGURATIONS.size()) throw JLException.INVALID_CONSTANT_EXCEPTION;
		return TEXTURE_CONFIGURATIONS.get(texture);
	}

	// Polygon renderer stuff

	/**
	 * Creates a polygon buffer.
	 * 
	 * @return A new polygon configuration.
	 */
	public static int jlNewPolygonBuffer() {
		POLY_CONFIGURATIONS.add(new Polygon());
		return POLY_CONFIGURATIONS.size() - 1;
	}

	/**
	 * Destroys a given polygon buffer.
	 * 
	 * @param polygonBuffer
	 *            the polygon buffer you would like to destroy.
	 */
	public static void jlDestroyPolygon(int polygonBuffer) {
		POLY_CONFIGURATIONS.remove(polygonBuffer);
	}

	public static void jlPolygonBufferConfiguration(int polygonBuffer, int mode, int... values) {
		Polygon poly = getPolygonConfiguration(polygonBuffer);

		switch (mode) {
			case JL_ADD: {
				poly.addVertex(values[0], values[1]);
				break;
			}
			default: {
				throw JLException.INVALID_CONSTANT_EXCEPTION;
			}
		}
	}

	/**
	 * Renders the polygon buffer.
	 * 
	 * @param polygonBuffer
	 *            the polygon buffer you would like to draw.
	 */
	public static void jlDrawPoly(int polygonBuffer) {
		Texture sprite = getTextureConfiguration(currentTexture);
		Polygon poly = getPolygonConfiguration(polygonBuffer);

		int x0 = 0;
		int y0 = 0;
		int x1 = displayConfiguration.screenWidth * xScale;
		int y1 = displayConfiguration.screenHeight * yScale;

		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				if (x < 0 || y < 0 || x >= displayConfiguration.screenWidth || y >= displayConfiguration.screenHeight) continue;

				boolean inside = checkPixelInside(poly, x, y);
				if (!inside) continue;

				int xo = x + texTransX;
				int yo = y + texTransY;
				int u = (xo / texScaleX) % sprite.w;
				int v = (yo / texScaleY) % sprite.h;
				int src = sprite.pixels[u + v * sprite.w];

				int xx = x + xOffs;
				int yy = y + yOffs;
				if (xx < 0 || yy < 0 || xx >= displayConfiguration.screenWidth || yy >= displayConfiguration.screenHeight) continue;
				pixels[xx + yy * displayConfiguration.screenWidth] = src & currentColour;
			}
		}
	}

	// Check if pixel is inside the polygon.
	private static boolean checkPixelInside(Polygon poly, int x, int y) {
		boolean inside = false;

		int nodes = poly.nodes();
		for (int i = 0, j = nodes - 1; i < nodes; j = i++) {
			Vertex p0 = poly.points.get(i);
			Vertex p1 = poly.points.get(j);

			inside ^= ((p0.y >= y) != (p1.y >= y)) && (x <= (p1.x - p0.x) * (y - p0.y) / (p1.y - p0.y) + p0.x);
		}
		return inside;
	}

	private static Polygon getPolygonConfiguration(int polygonBuffer) {
		return POLY_CONFIGURATIONS.get(polygonBuffer);
	}
}