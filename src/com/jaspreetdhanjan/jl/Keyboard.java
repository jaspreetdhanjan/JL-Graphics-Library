package com.jaspreetdhanjan.jl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class Keyboard implements KeyListener {
	private Map<Integer, Boolean> keyMap = new HashMap<Integer, Boolean>(65535);

	public Keyboard() {
		reset();
	}

	private void reset() {
		for (int i = 0; i < 65535; i++) {
			keyMap.put(i, false);
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		keyMap.put(e.getExtendedKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		keyMap.put(e.getExtendedKeyCode(), false);
	}

	public boolean getKeyStatus(int keyCode) {
		return keyMap.get(keyCode);
	}
}