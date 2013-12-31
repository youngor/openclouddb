package org.hx.rainbow.common.security.rsa.impl;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.hx.rainbow.common.security.rsa.RainbowSecurity;
import org.hx.rainbow.common.security.rsa.util.SecurityUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class RainbowSecurityImpl implements RainbowSecurity{
	

	public String decrypt(String securityCode, String modulus, String publicExponent) {
		String deStr = null;
		try{
			if(securityCode != null){
				BASE64Decoder dec=new BASE64Decoder(); 
				PublicKey publicKey = SecurityUtil.getInstance().getPublicKey(modulus,publicExponent);   
				deStr = new String(SecurityUtil.getInstance().decrypt(publicKey,dec.decodeBuffer(securityCode))); 
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return deStr;
	}


	public String encrypt(String securityCode, String modulus, String privateExponent) {
		String encStr = null;
		try{
			if(securityCode != null){
				BASE64Encoder enc=new BASE64Encoder();
				PrivateKey privateKey = SecurityUtil.getInstance().getPrivateKey(modulus,privateExponent);   
				byte[] encByte = SecurityUtil.getInstance().encrypt(privateKey, securityCode.getBytes()); 
				encStr = enc.encode(encByte);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return encStr;
	}
}
