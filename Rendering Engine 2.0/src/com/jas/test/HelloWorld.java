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

			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		onClose();
	}

	private void init() {
		createDisplay();

		BufferedImage img = null;
		try {
			img = ImageIO.read(getClass().getResourceAsStream("/texture.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int texWidth = img.getWidth();
		int texHeight = img.getHeight();
		int[] texPixels = img.getRGB(0, 0, texWidth, texHeight, null, 0, texWidth);

		testTexture = jlNewTexture();
		jlTextureConfiguration(testTexture, JL_DIMENSION, texWidth, texHeight);
		jlTextureConfiguration(testTexture, JL_SRC, texPixels);
	}

	private void createDisplay() {
		int screenW = 400;
		int screenH = 300;
		int scale = 1;

		displayComponent = jlNewDisplay();
		jlDisplayConfiguration(displayComponent, JL_DIMENSION, screenW, screenH, scale);
		jlDisplayConfiguration(displayComponent, JL_RESIZABLE, true);
		jlDisplayConfiguration(displayComponent, JL_NUM_BUFFERS, 3);
		jlDisplayConfiguration(displayComponent, JL_VISIBLE, true);
		jlDisplayConfiguration(displayComponent, JL_CREATE);
	}

	private int ticks = 0;

	private void tick() {
		ticks++;
	}

	private void render() {
		jlSwapBuffers();

		jlColour4i(255, 255, 255, 255);
		jlClearBuffer();

		/*
		jlScale(2, 2);

		int xx = (int) (Math.cos(ticks % 160.0 / 60.0) * 5.0);
		int yy = (int) (Math.sin(ticks % 160.0 / 60.0) * 5.0);

		for (int y = 0; y < yy; y++) {
			for (int x = 0; x < xx; x++) {
				jlTranslate(50 * x, 50 * y);
				jlDrawTexture(testTexture);
			}
		}*/

//		jlActivateTexture(testTexture);
		jlDrawQuad(0, 0, 100, 100);
		jlDeactivateTexture();
	}

	private void onClose() {
		jlDestroyDisplay(displayComponent);
		jlDestroyTexture(testTexture);
	}

	public static void main(String[] args) {
		new Thread(new HelloWorld(), "Display Thread").start();
	}
}