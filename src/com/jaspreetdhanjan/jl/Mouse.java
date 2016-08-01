package com.jaspreetdhanjan.jl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {
	private int mouseX, mouseY;
	private boolean mb0, mb1;

	public void mouseDragged(MouseEvent e) {
		updateMouse(e);
	}

	public void mouseMoved(MouseEvent e) {
		updateMouse(e);
	}

	public void mouseClicked(MouseEvent e) {
		updateMouse(e);
	}

	public void mousePressed(MouseEvent e) {
		updateMouse(e);
	}

	public void mouseReleased(MouseEvent e) {
		updateMouse(e);
	}

	public void mouseEntered(MouseEvent e) {
		updateMouse(e);
	}

	public void mouseExited(MouseEvent e) {
		updateMouse(e);
	}

	private void updateMouse(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mb0 = e.getButton() == MouseEvent.BUTTON1;
		mb1 = e.getButton() == MouseEvent.BUTTON3;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public boolean getLeftMouseButton() {
		return mb0;
	}

	public boolean getRightMouseButton() {
		return mb1;
	}
}