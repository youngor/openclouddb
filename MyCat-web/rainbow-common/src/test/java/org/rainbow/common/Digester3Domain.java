package org.rainbow.common;

import java.util.ArrayList;
import java.util.List;

public class Digester3Domain {
	private List<Digester3DomainDel> list = new ArrayList<Digester3DomainDel>();;
	private String a1;
	private String test;

	
	public String getA1() {
		return a1;
	}

	public void setA1(String a1) {
		this.a1 = a1;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public List<Digester3DomainDel> getList() {
		return list;
	}

	public void addList(Digester3DomainDel digester3DomainDel) {
		this.list.add(digester3DomainDel);
	}
	
	
}
