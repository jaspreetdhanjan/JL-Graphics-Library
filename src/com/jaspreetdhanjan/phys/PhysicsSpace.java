package com.jaspreetdhanjan.phys;

import java.util.*;

import com.jaspreetdhanjan.phys.collider.*;
import com.jaspreetdhanjan.phys.shape.*;
import com.jaspreetdhanjan.vecmath.Vec2;

public class PhysicsSpace {
	private static final float gravityPower = -9.81f;
	private static final Vec2 gravityDir = new Vec2(0, -1);

	private AABB bb;
	private List<DynamicCollider> dynamicColliders = new ArrayList<DynamicCollider>();
	private List<StaticCollider> staticColliders = new ArrayList<StaticCollider>();

	private List<Collider> cache = new ArrayList<Collider>();
	private List<Collider> toRemove = new ArrayList<Collider>();
	private boolean cacheDirty = true;

	public PhysicsSpace(AABB bb) {
		this.bb = bb;

		addStaticShape(new LineShape(new Vec2(0, 0), new Vec2(bb.getWidth(), 0)));
		addStaticShape(new LineShape(new Vec2(0, 0), new Vec2(0, bb.getHeight())));
		addStaticShape(new LineShape(new Vec2(bb.getWidth(), 0), new Vec2(bb.getWidth(), bb.getHeight())));
		addStaticShape(new LineShape(new Vec2(0, bb.getHeight()), new Vec2(bb.getWidth(), bb.getHeight())));
	}

	public void addDynamicShape(CollisionShape shape, float mass) {
		DynamicCollider c = new DynamicCollider(shape, mass);
		dynamicColliders.add(c);
		cacheDirty = true;
	}

	public void addStaticShape(CollisionShape shape) {
		StaticCollider c = new StaticCollider(shape);
		staticColliders.add(c);
		cacheDirty = true;
	}

	public void removeShape(CollisionShape shape) {
		for (Collider c : getColliders()) {
			if (c.getCollisionShape().equals(shape)) {
				remove(c);
				return;
			}
		}
	}

	private void remove(Collider c) {
		if (c instanceof DynamicCollider) dynamicColliders.remove(c);
		if (c instanceof StaticCollider) staticColliders.remove(c);
		cacheDirty = true;
	}

	public void tick() {
		Vec2 g = gravityDir.clone().mul(gravityPower);

		for (DynamicCollider dynamicCollider : dynamicColliders) {
			dynamicCollider.addForce(g);

			List<Collision> collisionPerCollider = dynamicCollider.checkCollisionsWith(getColliders());
			for (Collision c : collisionPerCollider) {
				dynamicCollider.onCollision(c);
			}

			if (isInside(dynamicCollider.getCollisionShape().getAABB())) {
				dynamicCollider.tryMove();
			} else {
				toRemove.add(dynamicCollider);
				continue;
			}
		}

		for (Collider c : toRemove) {
			remove(c);
		}
		toRemove.clear();
	}

	private boolean isInside(AABB otherBB) {
		return bb.overlaps(otherBB);
	}

	public AABB getAABB() {
		return bb;
	}

	private List<Collider> getColliders() {
		checkCollidersDirty();
		return cache;
	}

	private void checkCollidersDirty() {
		if (!cacheDirty) return;
		cache.clear();

		removeDuplicates(staticColliders);
		cache.addAll(staticColliders);
		removeDuplicates(dynamicColliders);
		cache.addAll(dynamicColliders);
	}

	private <T extends Collider> void removeDuplicates(List<T> colliders) {
		Set<T> uniqueColliders = new HashSet<T>();
		uniqueColliders.addAll(colliders);

		int removedDuplicates = colliders.size() - uniqueColliders.size();
		if (removedDuplicates > 0) {
			System.out.println("Removed " + removedDuplicates + " duplicate colliders");
			colliders.clear();
			colliders.addAll(uniqueColliders);
		}
	}
}