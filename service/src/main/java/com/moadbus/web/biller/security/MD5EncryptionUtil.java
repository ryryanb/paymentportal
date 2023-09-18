package com.moadbus.web.biller.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5EncryptionUtil {
	/**
	 * encrypt password in MD5
	 * 
	 * @param inputString
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String encryptMD5(String inputString) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(inputString.getBytes());

		byte[] digest = md.digest();

		return convertByteToHex(digest);
	}

	private static String convertByteToHex(byte[] byteData) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
}
