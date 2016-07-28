package com.jaspreetdhanjan.gfx;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
	private List<Vertex> points = new ArrayList<Vertex>();

	public Polygon(Vertex... pts) {
		for (int i = 0; i < pts.length; i++) {
			points.add(pts[i]);
		}
	}
	
	public boolean checkPixelInside(Polygon poly, int xPixel, int yPixel) {
		boolean inside = false;

		int nodes = poly.nodes();
		for (int i = 0, j = nodes - 1; i < nodes; j = i++) {
			Vertex p0 = poly.points.get(i);
			Vertex p1 = poly.points.get(j);

			inside ^= ((p0.y >= yPixel) != (p1.y >= yPixel)) && (xPixel <= (p1.x - p0.x) * (yPixel - p0.y) / (p1.y - p0.y) + p0.x);
		}
		return inside;
	}

	public void addVertex(int x, int y) {
		points.add(new Vertex(x, y));
	}

	public int nodes() {
		return points.size();
	}
}