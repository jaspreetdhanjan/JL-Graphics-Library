package com.jas.test;

import static com.jas.gfx.JL10.*;

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

		jlDisplayConfiguration(displayComponent, JL_DIMENSION, 800, 600);
		jlDisplayConfiguration(displayComponent, JL_RESIZABLE, true);
		jlDisplayConfiguration(displayComponent, JL_NUM_BUFFERS, 3);
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
		jlClearBuffer();

		jlColor4i(255, 255, 255, 255);
		jlBox(0, 0, 100, 100);
	}

	private void onClose() {
		jlDestroyDisplay(displayComponent);
		jlDestroyTexture(testTexture);
	}

	public static void main(String[] args) {
		new Thread(new HelloWorld(), "Display Thread").start();
	}
}