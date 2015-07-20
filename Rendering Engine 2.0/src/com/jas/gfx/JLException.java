package com.jas.gfx;

public class JLException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final RuntimeException INVALID_CONSTANT_EXCEPTION = new RuntimeException("Invalid constant!");
	public static final RuntimeException ACTIVE_TEXTURE_EXCEPTION = new RuntimeException("You cannot delete an active texture!");

	public JLException(String error) {
		super(error);
	}
}