package com.jaspreetdhanjan.jl;

import java.awt.*;
import java.awt.image.*;

import javax.swing.JFrame;

public class Display extends Canvas {
	private static final long serialVersionUID = 1L;

	public int screenWidth, screenHeight, screenScale;
	public boolean isResizable;
	public boolean isVisible;
	public String title = "";
	public int numBuffers = 3;
	public BufferedImage screenImage;

	public void createDisplay() {
		Dimension d = new Dimension(screenWidth * screenScale, screenHeight * screenScale);
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
			requestFocus();
			createBufferStrategy(numBuffers);
			return;
		}

		int w = screenWidth * screenScale;
		int h = screenHeight * screenScale;

		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);
		g.drawImage(screenImage, 0, 0, w, h, null);
		g.dispose();
		bs.show();
	}
}