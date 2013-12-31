package org.rainbow.common;

import java.lang.reflect.ParameterizedType;

public class TestRefencen<T>{

	@SuppressWarnings("unused")
	private Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public TestRefencen(){
		ParameterizedType pt = (ParameterizedType) this.getClass() .getGenericSuperclass();
			this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
	}
}
