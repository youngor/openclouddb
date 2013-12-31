package org.hx.rainbow.server.sa.server;

import java.util.Date;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ProductService extends BaseService {
	private static final String NAMESPACE = "SAPRODUCT";
	private static final String QUERY_AUTHR_CUSTOMER = "queryAuthrCustomer";
	private static final String COUNT_AUTHR_CUSTOMER = "countAuthrCustomer";
	private static final String QUERY_COMBOX = "queryCombox";
	
	
	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryCombox(RainbowContext context) {
		super.query(context, NAMESPACE,QUERY_COMBOX);
		return context;
	}
	
	/**
	 * 获取所有客户
	 * @param context
	 * @return
	 */
	public RainbowContext queryAuthrCustomer(RainbowContext context) {
		try{
			context.setRows(getDao().query(NAMESPACE,QUERY_AUTHR_CUSTOMER,context.getAttr(),context.getLimit(),context.getPage()));
			context.setTotal(getDao().count(NAMESPACE, COUNT_AUTHR_CUSTOMER,context.getAttr()));
			context.setMsg("查询到" + context.getRows().size() + "条记录!");
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("查询失败");
		}
		return context;
	}
	
	
	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
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
