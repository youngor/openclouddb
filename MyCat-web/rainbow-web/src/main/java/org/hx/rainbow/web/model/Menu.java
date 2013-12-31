package org.hx.rainbow.web.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Menu implements java.io.Serializable {
	
	private static final long serialVersionUID = 1386330606054540343L;
	
	
	private String pid;
	private String pname;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private String id;
	private String text;
	private String url;
	private String iconCls;
	private BigDecimal seq;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}


	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}
}
