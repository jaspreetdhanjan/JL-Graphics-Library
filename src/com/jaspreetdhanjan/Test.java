package com.jaspreetdhanjan;

import static com.jaspreetdhanjan.gfx.JL.*;

import java.awt.event.KeyEvent;
import java.util.*;

import com.jaspreetdhanjan.phys.*;
import com.jaspreetdhanjan.phys.shape.CircleShape;
import com.jaspreetdhanjan.vecmath.Vec2;

public class Test implements Runnable {
	private int screenW = 800;
	private int screenH = 600;
	private boolean stop = false;
	private int displayComponent;

	private PhysicsSpace physicsSpace = new PhysicsSpace(new AABB(0, 0, screenW, screenH));

	private Random random = new Random();
	private List<CircleShape> testCircles = new ArrayList<CircleShape>();
	private List<Integer> testCircleColours = new ArrayList<Integer>();

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
		jlDisplayConfiguration(displayComponent, JL_CREATE);

		jlCreateKeyboard();
	}

	private void addRandomCircle() {
		float randomRadius = 10 + (random.nextFloat() * 50f);
		float xx = random.nextInt((int) physicsSpace.getAABB().getWidth());
		float yy = randomRadius * 2;

		CircleShape circle = new CircleShape(new Vec2(xx, yy), randomRadius);
		testCircles.add(circle);
		physicsSpace.addDynamicShape(circle, randomRadius / 100f);
		testCircleColours.add(random.nextInt(0x555555));
	}

	boolean hold = false;
	int maxHandles = 12;

	private void tick() {
		if (jlGetKeyStatus(KeyEvent.VK_SPACE) && !hold && maxHandles > 0) {
			hold = true;
			addRandomCircle();
		}

		if (hold) {
			maxHandles--;
			hold = false;
		}

		physicsSpace.tick();
	}

	private void render() {
		jlSwapBuffers();
		jlClearBuffer();

		jlColour4f(0f, 0f, 0f, 1f);
		jlDrawQuad(0, 0, screenW, screenH);

		for (int i = 0; i < testCircles.size(); i++) {
			CircleShape circle = testCircles.get(i);
			AABB bb = circle.getAABB();
			Vec2 p = circle.getPos();

			int c = testCircleColours.get(i);
			float r = (c >> 16) & 255;
			float g = (c >> 8) & 255;
			float b = (c) & 255;

			jlColour4f(r, g, b, 1f);
			jlDrawBox((int) bb.x0, (int) bb.y0, (int) bb.x1, (int) bb.y1);
			jlDrawCircle((int) p.x, (int) p.y, (int) circle.getRadius());
		}
	}

	private void onClose() {
		jlDestroyDisplay(displayComponent);
	}

	public static void main(String[] args) {
		new Thread(new Test()).start();
	}
}