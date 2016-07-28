package com.jaspreetdhanjan.phys.collider;

import com.jaspreetdhanjan.phys.Collision;
import com.jaspreetdhanjan.phys.shape.CollisionShape;

public class Collider {
	private final CollisionShape shape;

	public Collider(CollisionShape shape) {
		this.shape = shape;
	}

	public void onCollision(Collision collision) {
	}

	public CollisionShape getCollisionShape() {
		return shape;
	}
}