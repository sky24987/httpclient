package org.app.ticket.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class StringUtil {
	public static boolean isEmptyString(String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	public static boolean isEmptyArray(List list) {
		return (list == null) || (list.size() == 0);
	}

	public static Integer[] convertToIntegerArray(String[] s) {
		Integer[] num = new Integer[s.length];
		for (int i = 0; i < s.length; i++) {
			num[i] = new Integer(s[i]);
		}
		return num;
	}

	public static String arrayToString(String[] str) {
		if (str == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			sb.append(str[i]);
			sb.append(", ");
		}
		return sb.toString();
	}

	public static boolean isExist(String str, String[] array) {
		boolean result = false;
		if (array == null) {
			return result;
		}
		for (int i = 0; i < array.length; i++) {
			if (str.equals(array[i]))
				result = true;
		}
		return result;
	}

	public static String rightAlign(String data, int length, String fill) {
		for (int i = data.length(); i < length; i++) {
			data = fill + data;
		}
		return data;
	}

	public static String leftAlign(String data, int length, String fill) {
		for (int i = data.length(); i < length; i++) {
			data = data + fill;
		}
		return data;
	}

	public static String getBASE64(String s) {
		if (s == null)
			return null;
		try {
			//String encode = new BASE64Encoder().encode(s.getBytes("UTF-8"));
			return new String(Base64.encodeBase64(s.getBytes()));
			
		} catch (Exception ex) {
			return null;
		}
		
	}

	public static String toMD5(String plainText)
			throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(plainText.getBytes());
		byte[] by = messageDigest.digest();

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < by.length; i++) {
			int val = by[i];
			if (val < 0)
				val += 256;
			else if (val < 16) {
				buf.append("0");
			}
			buf.append(Integer.toHexString(val));
		}
		return buf.toString();
	}

	public static boolean isEqualString(String arg0, String arg1) {
		return arg0.trim().equals(arg1.trim());
	}
}
