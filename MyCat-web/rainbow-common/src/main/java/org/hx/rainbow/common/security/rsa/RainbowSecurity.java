package org.hx.rainbow.common.security.rsa;

public interface RainbowSecurity {
	/**
	 * 加密
	 * @param securityCode 验证码
	 * @param modulus 密钥的modulus值
	 * @param privateExponent 私钥的Exponent值
	 * @return
	 */
	public abstract String encrypt(String securityCode,String modulus,String privateExponent);
	
	/**
	 * 解密
	 * @param modulus 密钥modulus值
     * @param privateExponent 私钥的Exponent值
	 * @param securityCode 验证码
	 * @return
	 */
	public abstract String decrypt(String securityCode,String modulus,String publicExponent);
}
