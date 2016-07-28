package com.jaspreetdhanjan.phys.collider;

import java.util.ArrayList;
import java.util.List;

import com.jaspreetdhanjan.phys.*;
import com.jaspreetdhanjan.phys.shape.*;
import com.jaspreetdhanjan.vecmath.Vec2;

public class DynamicCollider extends Collider {
	private final float mass;

	private Vec2 f = new Vec2();
	private List<Collision> lastCollisions = new ArrayList<Collision>();
	private List<Collision> collisions = new ArrayList<Collision>();

	public DynamicCollider(CollisionShape shape, float mass) {
		super(shape);
		this.mass = mass;
	}

	public void addForce(Vec2 a) {
		Vec2 ma = a.clone().mul(mass);
		f.set(ma);
	}

	public void tryMove() {
		getCollisionShape().push(f);
	}

	public void onCollision(Collision collision) {
		Vec2 bounceBack = collision.getNormal().clone().mul(-f.length());
		addForce(bounceBack);
	}

	public <T extends Collider> List<Collision> checkCollisionsWith(List<T> colliders) {
		lastCollisions.addAll(collisions);
		collisions.clear();

		for (Collider collider : colliders) {
			if (collider == this) continue;

			CollisionShape s0 = this.getCollisionShape();
			CollisionShape s1 = collider.getCollisionShape();
			if (!s0.getAABB().overlaps(s1.getAABB())) continue;

			Collision c = Collision.sortAndRetrieve(s0, s1);
			if (c != null) {
				collisions.add(c);
			}
		}
		return collisions;
	}

	public float getMass() {
		return mass;
	}
}