package com.jaspreetdhanjan.phys.shape;

import com.jaspreetdhanjan.phys.*;
import com.jaspreetdhanjan.vecmath.Vec2;

/**
 * Collision information for a circle with a center position and radius.
 * 
 * @author jaspreetdhanjan
 */

public class CircleShape implements CollisionShape {
	private AABB tmp = new AABB();
	private Vec2 nextPos = new Vec2();

	private Vec2 pos;
	private float radius;

	public CircleShape(Vec2 pos, float radius) {
		this.pos = pos;
		this.radius = radius;
		nextPos.set(pos);
	}

	public Collision getCollision(CircleShape shape) {
		float xd = shape.pos.x - pos.x;
		float yd = shape.pos.y - pos.y;
		float dd = xd * xd + yd * yd;
		float rr = radius + shape.radius;

		if (dd <= rr * rr) {
			return new Collision(shape, new Vec2(xd, yd).normalise());
		}
		return null;
	}

	public Collision getCollision(LineShape shape) {
		Collision c = shape.getCollision(this);
		if (c != null) {
			c.otherCollider = shape;
		}
		return c;
	}

	public float distanceTo(Vec2 p) {
		return pos.distanceTo(p) - radius;
	}

	public void push(Vec2 m) {
		pos.add(m);
		nextPos.set(pos).mulAdd(m, 1);
	}

	public AABB getAABB() {
		tmp.set(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius);
		return tmp;
	}

	public Vec2 getPos() {
		return pos;
	}

	public Vec2 getNextPos() {
		return nextPos;
	}

	public float getRadius() {
		return radius;
	}

	public boolean equals(Object o) {
		if (o instanceof CircleShape) {
			CircleShape other = (CircleShape) o;
			return other.pos.equals(pos) && other.radius == radius;
		}
		return false;
	}
}