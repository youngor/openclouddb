package org.hx.rainbow.common.core.rest;

import java.util.Enumeration;
import java.util.ResourceBundle;

import com.google.inject.Binder;
import com.google.inject.Module;

public class RestModule implements Module
{
   public void configure(final Binder binder)
   {
	  try {
		  ResourceBundle rb = ResourceBundle.getBundle("resteasy");
		  Enumeration<String> enu = rb.getKeys();
		  while (enu.hasMoreElements()) {
				String className = enu.nextElement().toString();
				binder.bind(Class.forName(className));
		  }
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
    
   }
}