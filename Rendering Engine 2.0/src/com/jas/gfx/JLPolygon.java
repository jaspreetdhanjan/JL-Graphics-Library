package com.jas.gfx;

import java.util.*;

public class JLPolygon {
	public List<JLPoint> JLPoints = new ArrayList<JLPoint>();

	public JLPolygon(JLPoint... JLPoints) {
		for (int i = 0; i < JLPoints.length; i++) {
			this.JLPoints.add(JLPoints[i]);
		}
	}

	public void addPoint(JLPoint JLPoint) {
		JLPoints.add(JLPoint);
	}

	public void addPoint(int x, int y) {
		JLPoints.add(new JLPoint(x, y));
	}

	public boolean checkPointContains(JLPoint JLPoint) {
		boolean inside = false;
		int verts = JLPoints.size();

		for (int i = 0, j = verts - 1; i < verts; j = i++) {
			JLPoint ip = JLPoints.get(i);
			JLPoint jp = JLPoints.get(j);
			if (((ip.y >= JLPoint.y) != (jp.y >= JLPoint.y)) && (JLPoint.x <= (jp.x - ip.x) * (JLPoint.y - ip.y) / (jp.y - ip.y) + ip.x)) {
				inside = !inside;
			}
		}
		return inside;
	}

	public boolean checkPointContains(int x, int y) {
		boolean inside = false;
		int verts = JLPoints.size();

		for (int i = 0, j = verts - 1; i < verts; j = i++) {
			JLPoint ip = JLPoints.get(i);
			JLPoint jp = JLPoints.get(j);
			if (((ip.y >= y) != (jp.y >= y)) && (x <= (jp.x - ip.x) * (y - ip.y) / (jp.y - ip.y) + ip.x)) {
				inside = !inside;
			}
		}
		return inside;
	}
}