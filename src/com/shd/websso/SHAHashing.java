/**
 * 
 */
package com.shd.websso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author chliu
 * 
 */
public class SHAHashing {
	private static SHAHashing instance = null;

	/**
	 * 
	 */
	public SHAHashing() {
		// TODO Auto-generated constructor stub
	}

	public static SHAHashing getInstance() {
		if (instance == null) {
			instance = new SHAHashing();
		}
		return instance;
	}

	public String hashing(String str) {
		System.out.println("before hash" + str);
		MessageDigest md;
		StringBuffer sb = new StringBuffer();
		StringBuffer hexString = new StringBuffer();
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			// System.out.println("Hex format : " + sb.toString());

			// convert the byte to hex format method 2
			for (int i = 0; i < byteData.length; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			// System.out.println("Hex format : " + hexString.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SHAHashing ss = SHAHashing.getInstance();
		System.out.println(ss.hashing("password"));

	}

}
