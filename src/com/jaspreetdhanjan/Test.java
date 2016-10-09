package com.jaspreetdhanjan;

import static com.jaspreetdhanjan.jl.JL.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.glass.events.KeyEvent;

public class Test implements Runnable {
	private int screenW = 800;
	private int screenH = 600;
	private boolean stop = false;
	private int displayComponent;

	private int testTexture;

	public void run() {
		init();

		long lastTime = System.currentTimeMillis();
		int frames = 0;

		while (!stop) {
			tick();
			render();
			frames++;
			while (System.currentTimeMillis() - lastTime > 1000) {
				System.out.println(frames + " fps");
				lastTime += 1000;
				frames = 0;
			}
		}

		onClose();
	}

	private void init() {
		displayComponent = jlNewDisplay();
		jlDisplayConfiguration(displayComponent, JL_DIMENSION, screenW, screenH);
		jlDisplayConfiguration(displayComponent, JL_RESIZABLE, JL_FALSE);
		jlDisplayConfiguration(displayComponent, JL_NUM_BUFFERS, 1);
		jlDisplayConfiguration(displayComponent, JL_VISIBLE, JL_TRUE);
		jlDisplayConfiguration(displayComponent, JL_CREATE); // Important! Create the display after applying configurations.

		jlCreateKeyboard();
		jlCreateMouse();

		// Load a test texture
		testTexture = loadTexture("/texture.png");
	}

	private int loadTexture(String path) {
		int tex = jlNewTexture();

		BufferedImage textureImage = null;
		try {
			textureImage = ImageIO.read(getClass().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int texWidth = textureImage.getWidth();
		int texHeight = textureImage.getHeight();
		int[] texturePixels = textureImage.getRGB(0, 0, texWidth, texHeight, null, 0, texWidth);

		jlTextureConfiguration(testTexture, JL_DIMENSION, texWidth, texHeight);
		jlTextureConfiguration(testTexture, JL_SRC, texturePixels);

		return tex;
	}

	private void tick() {
		if (jlGetKeyStatus(KeyEvent.VK_SPACE) == JL_TRUE) System.out.println("Space was pressed!");
	}

	private void render() {
		jlSwapBuffers(); // Must be called every frame.
		jlClearBuffer();

		jlColour4f(1f, 0f, 0f, 1f);
		jlDrawQuad(0, 0, screenW, screenH);

		jlColour4f(1f, 1f, 1f, 1f);

		jlActivateTexture(testTexture);
		jlDrawCircle(150, 200, 40);
		jlDeactivateTextures(); // Deactivate when not using. Does not support multi-texturing yet.
	}

	private void onClose() {
		jlDestroyDisplay(displayComponent);
	}

	public static void main(String[] args) {
		new Thread(new Test()).start();
	}
}