package com.jaspreetdhanjan.phys;

import com.jaspreetdhanjan.phys.shape.CircleShape;
import com.jaspreetdhanjan.phys.shape.CollisionShape;
import com.jaspreetdhanjan.phys.shape.LineShape;
import com.jaspreetdhanjan.vecmath.Vec2;

public class Collision {
	public CollisionShape otherCollider;
	private Vec2 normal;

	public Collision(CollisionShape otherCollider, Vec2 normal) {
		this.otherCollider = otherCollider;
		this.normal = normal;
	}

	public Vec2 getNormal() {
		return normal;
	}

	public static Collision sortAndRetrieve(CollisionShape s0, CollisionShape s1) {
		if (s0 instanceof CircleShape && s1 instanceof CircleShape) {
			CircleShape cc0 = (CircleShape) s0;
			CircleShape cc1 = (CircleShape) s1;
			return cc0.getCollision(cc1);
		} else if (s0 instanceof LineShape && s1 instanceof CircleShape) {
			LineShape cl = (LineShape) s0;
			CircleShape cc = (CircleShape) s1;
			return cl.getCollision(cc);
		} else if (s0 instanceof CircleShape && s1 instanceof LineShape) {
			CircleShape cc = (CircleShape) s0;
			LineShape cl = (LineShape) s1;
			return cc.getCollision(cl);
		} else if (s0 instanceof LineShape && s1 instanceof LineShape) {
			LineShape l0 = (LineShape) s0;
			LineShape l1 = (LineShape) s1;
			return l0.getCollision(l1);
		}

		return null;
	}
}