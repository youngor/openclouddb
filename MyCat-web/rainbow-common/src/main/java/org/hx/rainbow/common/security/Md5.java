package org.hx.rainbow.common.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	private volatile static Md5 md5 = null;
	
	private Md5(){};
	
	public static Md5  getInstance(){
		if(md5 == null){
			synchronized (Md5.class) {
				if(md5 == null){
					md5 = new Md5();
				}
			}
		}
		return md5;
	}

	public String encrypt(String value) {
		try {
			char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
				byte[] strTemp = value.getBytes();
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				mdTemp.update(strTemp);
				byte[] md = mdTemp.digest();
				int j = md.length;
				char str[] = new char[j * 2];
				int k = 0;
				for (int i = 0; i < j; i++) {
					byte byte0 = md[i];
					str[k++] = hexDigits[byte0 >>> 4 & 0xf];
					str[k++] = hexDigits[byte0 & 0xf];
				}
				return new String(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Md5.getInstance().encrypt("password"));
	}


}