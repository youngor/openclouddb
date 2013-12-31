package org.hx.rainbow.server.sa.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.security.rsa.RainbowSecurity;
import org.hx.rainbow.common.security.rsa.impl.RainbowSecurityImpl;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class LicensesService extends BaseService {
	private static final String NAMESPACE = "SALICENSES";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	

	
	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
        StringBuffer sb = new StringBuffer();
        sb.append(context.getAttr("customerCode"));
        sb.append(context.getAttr("customerName"));
        sb.append(context.getAttr("productCompany"));
        sb.append(context.getAttr("productCode"));
        sb.append(context.getAttr("productName"));
        sb.append(context.getAttr("expiringDate"));
        sb.append(context.getAttr("licVersion"));
        sb.append(context.getAttr("licenseCode"));
        sb.append(context.getAttr("licenseMode"));
        sb.append(context.getAttr("signingDate"));
        sb.append(context.getAttr("startDate"));
        sb.append(context.getAttr("versionNumber"));
        Map<String,Object> paramData = new HashMap<String,Object>();
        paramData.put("code", context.getAttr("encryptModel"));
        Map<String,Object> rsaData = getDao().get("SARSA", "query", paramData);
        String privateExponent = (String)rsaData.get("privateKey");
        String modulus = (String)rsaData.get("modulus");
		RainbowSecurity security = new RainbowSecurityImpl();
		context.addAttr("signature", security.encrypt(getHashCode(sb.toString()), modulus, privateExponent));
		super.insert(context, NAMESPACE);
		context.setMsg("授权成功!");
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		context.setMsg("取消授权成功!");
		return context;
	}
	
	private String getHashCode(String value){
		Integer hashCode = new HashCodeBuilder().append(value.toCharArray()).toHashCode();
		return String.valueOf(hashCode & 0x7FFFFFFF);
	}
}
