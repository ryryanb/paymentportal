package com.moadbus.web.biller.utils;

public class OtpUtils {
	private static final int MIN = 100000;
	private static final int MAX = 999999;

	public static int getRandomNumberSixDigit() {
		return (int) Math.floor(Math.random() * (MAX - MIN + 1)) + MIN;
	}
}
