package com.jas.test;

import static com.jas.gfx.JL.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A simple test that demonstrates how to setup the JL graphics library.
 * 
 * @author Jaspreet Dhanjan
 */

public class HelloWorld implements Runnable {
	// Stops the run loop
	private boolean stop = false;

	// Create a display variable
	private int displayComponent;

	// Create a texture variable
	private int testTexture;

	// Create a polygon buffer variable
	private int polygonBuffer;

	public void run() {
		// Initialise objects in the thread
		init();

		// Run loop
		while (!stop) {
			render();

			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		onClose();
	}

	private void init() {
		// Create the display
		createDisplay();

		// Load the image
		BufferedImage img = null;
		try {
			img = ImageIO.read(getClass().getResourceAsStream("/texture.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int texWidth = img.getWidth();
		int texHeight = img.getHeight();
		int[] texPixels = img.getRGB(0, 0, texWidth, texHeight, null, 0, texWidth);

		// Create a new texture and send it to JL
		testTexture = jlNewTexture();
		jlTextureConfiguration(testTexture, JL_DIMENSION, texWidth, texHeight);
		jlTextureConfiguration(testTexture, JL_SRC, texPixels);
		jlTextureConfiguration(testTexture, JL_TEXTURE_SCALE, 1, 1);

		// Create polygon buffer and add vertices
		polygonBuffer = jlNewPolygonBuffer();
		int offs = 200;
		jlPolygonBufferConfiguration(polygonBuffer, JL_ADD, -50 + offs, -50 + offs);
		jlPolygonBufferConfiguration(polygonBuffer, JL_ADD, 50 + offs, -50 + offs);
		jlPolygonBufferConfiguration(polygonBuffer, JL_ADD, 0 + offs, 50 + offs);
	}

	private void createDisplay() {
		int screenW = 400;
		int screenH = 300;
		int scale = 1;

		// Create a display and set the relevant configurations
		displayComponent = jlNewDisplay();
		jlDisplayConfiguration(displayComponent, JL_DIMENSION, screenW, screenH, scale);
		jlDisplayConfiguration(displayComponent, JL_RESIZABLE, JL_TRUE);
		jlDisplayConfiguration(displayComponent, JL_NUM_BUFFERS, 3);
		jlDisplayConfiguration(displayComponent, JL_VISIBLE, JL_TRUE);

		// IMPORTANT. You must create the display AFTER setting the configurations.
		jlDisplayConfiguration(displayComponent, JL_CREATE);
	}

	private void render() {
		// Swap buffers
		jlSwapBuffers();

		// Clear the buffer
		jlClearBuffer();

		// Set the colour
		jlColour4i(255, 255, 255, 255);

		// Activate the texture we created
		jlActivateTexture(testTexture);
		
		jlDrawCircle(150, 150, 50);

		// Draw the polygon
		jlDrawPoly(polygonBuffer);

		// Deactivate the texture
		jlDeactivateTextures();
	}

	// Destroy objects
	private void onClose() {
		jlDestroyDisplay(displayComponent);
		jlDestroyTexture(testTexture);
		jlDestroyPolygon(polygonBuffer);
	}

	public static void main(String[] args) {
		new Thread(new HelloWorld(), "Display Thread").start();
	}
}