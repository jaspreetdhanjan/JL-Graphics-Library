package com.jas.test;

import static com.jas.display.JL10.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HelloWorld implements Runnable {
	private boolean stop = false;
	private int displayComponent;
	private int testTexture;

	public void run() {
		init();

		while (!stop) {
			tick();
			render();
		}

		onClose();
	}

	private void init() {
		BufferedImage textureImage = null;
		try {
			textureImage = ImageIO.read(getClass().getResourceAsStream("/texture.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int texWidth = textureImage.getWidth();
		int texHeight = textureImage.getHeight();
		int[] texturePixels = textureImage.getRGB(0, 0, texWidth, texHeight, null, 0, texWidth);

		displayComponent = jlNewDisplay();

		jlDisplayConfiguration(displayComponent, JL_DIMENSION, new int[] { 800, 600 });
		jlDisplayConfiguration(displayComponent, JL_RESIZABLE, true);
		jlDisplayConfiguration(displayComponent, JL_NUM_BUFFERS, new int[] { 3 });
		jlDisplayConfiguration(displayComponent, JL_VISIBLE, true);
		jlDisplayConfiguration(displayComponent, JL_CREATE, true);

		testTexture = jlNewTexture();

		jlTextureConfiguration(testTexture, JL_DIMENSION, new int[] { texWidth, texHeight });
		jlTextureConfiguration(testTexture, JL_SRC, texturePixels);
	}

	private void tick() {
	}

	private void render() {
		jlUpdate();
		jlClearScreen();

		jlColor4i(255, 0, 255, 255);

		
		jlTranslate(100, 100);
		int x0 = 0;
		int y0 = 0;
		int x1 = 128;
		int y1 = 128;

		// north
		jlLine(x0, y0, x1, y0);
		// east
		jlLine(x1, y0, x1, y1);
		// south
		jlLine(x0, y1, x1, y1);
		// west
		jlLine(x0, y0, x0, y1);
	}

	private void onClose() {
		jlDestroyDisplay(displayComponent);
		jlDestroyTexture(testTexture);
	}

	public static void main(String[] args) {
		new Thread(new HelloWorld(), "Display Thread").start();
	}
}