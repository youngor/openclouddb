package org.hx.rainbow.common.util;

public class ObjectId {
	public String toString(){
		return java.util.UUID.randomUUID().toString().replaceAll("-", "");
	}
	public static void main(String[] args) {
		ObjectId o = new ObjectId();
		System.out.println(o.toString());
	}
}
