package com.jaspreetdhanjan.phys.shape;

import com.jaspreetdhanjan.phys.*;
import com.jaspreetdhanjan.vecmath.Vec2;

/**
 * Collision information for a Line Segment from points p0 to p1.
 * 
 * @author jaspreetdhanjan
 */

public class LineShape implements CollisionShape {
	private AABB tmp = new AABB();

	private Vec2 p0, p1;
	private Vec2 normal;
	private float len;

	public LineShape(Vec2 p0, Vec2 p1) {
		this.p0 = p0;
		this.p1 = p1;
		normal = p1.clone().sub(p0).normalise();
		len = p0.distanceTo(p1);
	}

	public Collision getCollision(CircleShape shape) {
		float r = shape.getRadius();
		Vec2 m = p0.clone().sub(shape.getNextPos());
		float b = m.dot(normal);
		float c = m.dot(m) - r * r;
		if (c > 0 && b > 0) return null;
		float discr = b * b - c;
		if (discr < 0) return null;
		float t = (float) (-b - Math.sqrt(discr));
		if (t < 0) return null;
		t /= len;
		if (t > 1) return null;

		Vec2 normal = p0.clone().mulAdd(p1, t).sub(shape.getPos()).normalise();
		return new Collision(shape, normal);
	}

	public Collision getCollision(LineShape line) {
		Vec2 r = p1.clone().sub(p0);
		Vec2 s = line.p1.clone().sub(line.p0);

		float d = r.cross(s);
		if (d == 0) return null;
		float u = (line.p0.clone().sub(p0)).cross(r) / d;
		float t = (line.p0.clone().sub(p0)).cross(s) / d;

		if (d != 0 && (0 <= t && t <= 1) && (0 <= u && u <= 1)) {
			return new Collision(line, null);
		}
		return null;
	}

	public float distanceTo(Vec2 p) {
		float px = p1.x - p0.x;
		float py = p1.y - p0.y;
		float pp = px * px + py * py;

		float dpx = p.x - p0.x;
		float dpy = p.y - p0.y;

		float u = (dpx * px + dpy * py) / pp;
		if (u > 1) u = 1;
		if (u < 0) u = 0;

		float xClosest = p0.x + u * px;
		float yClosest = p0.y + u * py;

		float dx = xClosest - p.x;
		float dy = yClosest - p.y;
		float dd = (float) Math.sqrt(dx * dx + dy * dy);
		return dd;
	}

	public void push(Vec2 m) {
		p0.add(m);
		p1.add(m);
	}

	public AABB getAABB() {
		tmp.set(p0.x, p0.y, p1.x, p1.y);
		return tmp;
	}

	public Vec2 getNormal() {
		return normal;
	}

	public float getLength() {
		return len;
	}

	public boolean equals(Object o) {
		if (o instanceof LineShape) {
			LineShape other = (LineShape) o;
			return other.p0.equals(p0) && other.p1.equals(p1);
		}
		return false;
	}
}