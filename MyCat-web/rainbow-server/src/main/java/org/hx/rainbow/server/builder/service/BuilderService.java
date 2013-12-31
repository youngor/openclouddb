package org.hx.rainbow.server.builder.service;


import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class BuilderService extends BaseService{

	private static final String NAMESPACE = "BUILDER";
	private static final String QUERYCOMBOX = "queryCombox";
	private static final String QUERYCOMBOXCOUNT = "queryComboxCount";
	private static final String QUERYVIEWCOMBOX = "queryViewCombox";
	private static final String QUERYVIEWCOMBOXCOUNT = "queryViewComboxCount";
	private static final String QUERYDBLINK = "queryDBLink";
	private static final String COUNTDBLINK = "countDBLink";

	public RainbowContext queryCombox(RainbowContext context) {
		String value = (String)context.getAttr("q");
		if(value == null || value.trim().isEmpty()){
			context.removeAttr("q");
		}else{
			context.addAttr("q", value.toUpperCase());
		}
		String dblinkName = (String) context.getAttr("dblinkName");
		if(dblinkName==null||dblinkName.trim().isEmpty()){
			context.removeAttr("dblinkName");
		}
		super.queryByPage(context, NAMESPACE,QUERYCOMBOX,QUERYCOMBOXCOUNT);
		return context;
	}
	public RainbowContext queryViewCombox(RainbowContext context) {
		String value = (String)context.getAttr("q");
		if(value == null || value.trim().isEmpty()){
			context.removeAttr("q");
		}else{
			context.addAttr("q", value.toUpperCase());
		}
		String dblinkName = (String) context.getAttr("dblinkName");
		if(dblinkName==null||dblinkName.trim().isEmpty()){
			context.removeAttr("dblinkName");
		}
		super.queryByPage(context, NAMESPACE,QUERYVIEWCOMBOX,QUERYVIEWCOMBOXCOUNT);
		return context;
	}
	public RainbowContext queryDBLink(RainbowContext context) {
		String value = (String)context.getAttr("q");
		if(value == null || value.trim().isEmpty()){
			context.removeAttr("q");
		}else{
			context.addAttr("q", value.toUpperCase());
		}
		super.queryByPage(context, NAMESPACE,QUERYDBLINK,COUNTDBLINK);
		return context;
	}
	public RainbowContext query(RainbowContext context) {
		String tableName = (String) context.getAttr("tableName");
		if(tableName == null || tableName.trim().isEmpty()){
			context.setMsg("请选择要查看的表");
			return context;
		}
		String dblinkName = (String) context.getAttr("dblinkName");
		if(dblinkName==null||dblinkName.trim().isEmpty()){
			context.removeAttr("dblinkName");
		}
		super.query(context, NAMESPACE);		
		return context;
	}
	
}
