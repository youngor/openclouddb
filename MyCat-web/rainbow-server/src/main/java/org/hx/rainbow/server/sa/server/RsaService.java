package org.hx.rainbow.server.sa.server;

import java.security.KeyPair;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.security.rsa.util.SecurityUtil;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class RsaService extends BaseService {
	private static final String NAMESPACE = "SARSA";
	private static final String QUERY_COMBOX = "queryCombox";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryCombox(RainbowContext context) {
		super.query(context, NAMESPACE, QUERY_COMBOX);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		
		try {
			SecurityUtil securityUtil = SecurityUtil.getInstance();
			KeyPair keyPair = securityUtil.getKeyPair();
			String modulus = securityUtil.getModulus(keyPair);
			String privateKey = securityUtil.getPrivateExponent(keyPair);
			String publicKey = securityUtil.getPublicExponent(keyPair);
			context.addAttr("modulus", modulus);
			context.addAttr("privateKey", privateKey);
			context.addAttr("publicKey", publicKey);			
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("生成密钥错误,系统异常!");
			return context;
		}
		super.insert(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
}
