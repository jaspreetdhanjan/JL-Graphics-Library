package com.jaspreetdhanjan.gfx;

public class JLException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final JLException INVALID_CONSTANT_EXCEPTION = new JLException("Invalid constant!");
	public static final JLException ACTIVE_TEXTURE_EXCEPTION = new JLException("You cannot delete an active texture!");
	public static final JLException DIVIDING_BY_ZERO_EXCEPTION = new JLException("You cannot scale by factor zero!");
	public static final JLException OBJECT_NOT_CREATED = new JLException("Parameter not created!");

	private JLException(String error) {
		super(error);
	}
}