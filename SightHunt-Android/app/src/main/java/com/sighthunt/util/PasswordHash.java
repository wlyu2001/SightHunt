package com.sighthunt.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {
	public static String hash(String password) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// default charset is UTF-8
		try {
			return new String(messageDigest.digest(password.getBytes()), "UTF-16");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
}
