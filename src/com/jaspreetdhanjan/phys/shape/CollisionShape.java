package com.jaspreetdhanjan.phys.shape;

import com.jaspreetdhanjan.phys.*;
import com.jaspreetdhanjan.vecmath.Vec2;

public interface CollisionShape {
	public Collision getCollision(CircleShape shape);

	public Collision getCollision(LineShape shape);

	public float distanceTo(Vec2 p);

	public void push(Vec2 m);

	public AABB getAABB();
}