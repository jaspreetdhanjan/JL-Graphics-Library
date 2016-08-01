package com.jaspreetdhanjan.jl;

public class Texture {
	public int w, h;
	public int[] pixels;

	public Texture() {
	}

	public Texture(int w, int h) {
		this.w = w;
		this.h = h;
		pixels = new int[w * h];
	}
}