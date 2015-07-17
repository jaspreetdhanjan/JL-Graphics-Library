package com.jas.test;

import static com.jas.gfx.JL10.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jas.gfx.*;

public class HelloWorld implements Runnable {
	// Stops the run loop
	private boolean stop = false;

	// Create a display variable
	private int displayComponent;

	// Create a texture variable
	private int testTexture;

	// Create polygon
	private JLPolygon polygon = new JLPolygon();

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

		// Add points to the polygon
		polygon.addPoint(0, 0);
		polygon.addPoint(100, 0);
		polygon.addPoint(100, 100);
		polygon.addPoint(0, 100);
	}

	private void createDisplay() {
		int screenW = 400;
		int screenH = 300;
		int scale = 1;

		// Create a display and set the relevant configurations
		displayComponent = jlNewDisplay();
		jlDisplayConfiguration(displayComponent, JL_DIMENSION, screenW, screenH, scale);
		jlDisplayConfiguration(displayComponent, JL_RESIZABLE, true);
		jlDisplayConfiguration(displayComponent, JL_NUM_BUFFERS, 3);
		jlDisplayConfiguration(displayComponent, JL_VISIBLE, true);
		jlDisplayConfiguration(displayComponent, JL_CREATE);
	}

	private void render() {
		// Swap buffers
		jlSwapBuffers();
		
		// Clear the buffer
		jlClearBuffer();

		// Set the colour
		jlColour4i(255, 0, 0, 255);

		// Activate the texture we created
		jlActivateTexture(testTexture);
		
		// Draw the polygon
		jlDrawPoly(polygon);
		
		// Deactivate the texture
		jlDeactivateTextures();
	}

	private void onClose() {
		jlDestroyDisplay(displayComponent);
		jlDestroyTexture(testTexture);
	}

	public static void main(String[] args) {
		new Thread(new HelloWorld(), "Display Thread").start();
	}
}