package com.jaspreetdhanjan.jl;

public class JLException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final JLException INVALID_CONSTANT_EXCEPTION = new JLException("Invalid constant!", 1);
	public static final JLException ACTIVE_TEXTURE_EXCEPTION = new JLException("You cannot delete an active texture!", 2);
	public static final JLException DIVIDING_BY_ZERO_EXCEPTION = new JLException("You cannot scale by factor zero!", 3);
	public static final JLException OBJECT_NOT_CREATED = new JLException("Parameter not created!", 4);
	public static final JLException KEYBOARD_NOT_CREATED_EXCEPTION = new JLException("Keyboard was not created!", 5);
	public static final JLException MOUSE_NOT_CREATED_EXCEPTION = new JLException("Mouse was not created!", 6);

	private JLException(String error, int reference) {
		super("JL EXCEPTION #" + reference + " – " + error);
	}
}