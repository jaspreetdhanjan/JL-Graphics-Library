package com.jaspreetdhanjan.phys;

public class AABB {
	public float x0, y0, x1, y1;

	public AABB() {
	}

	public AABB(float x0, float y0, float x1, float y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}

	public void set(float x0, float y0, float x1, float y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}

	public boolean contains(AABB bb) {
		if (bb.x0 < x0 || bb.y0 < y0) return false;
		if (bb.x1 > x1 || bb.y1 > y1) return false;
		return true;
	}

	public boolean overlaps(AABB bb) {
		if (bb.x1 < x0 || bb.y1 < y0) return false;
		if (bb.x0 > x1 || bb.y0 > y1) return false;
		return true;
	}

	public float getX() {
		return x0;
	}

	public float getY() {
		return y0;
	}

	public float getWidth() {
		return x1 - x0;
	}

	public float getHeight() {
		return y1 - y0;
	}
}