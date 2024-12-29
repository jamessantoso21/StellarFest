package utils;

import models.User;

public class Auth {
	private static User user;
	
	private Auth() {};
	
	public static User get() {
		return user;
	}
	
	public static void set(User u) {
		user = u;
	}
	
	public static void clear() {
		user = null;
	}
}
