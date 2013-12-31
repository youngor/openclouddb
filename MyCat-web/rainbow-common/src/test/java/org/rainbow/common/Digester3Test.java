/*package org.rainbow.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.CallMethodRule;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Digester3Test {
	public static void main(String[] args) throws IOException, SAXException {
		String aa = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<body><header>" +
					"<a1>123</a1>" +
					"<test>123</test>"+
					""+
					"<alist>"+
					"		<b>"+
					"			<c1>sdf</c1>"+
					"			<c2>123</c2>"+
					"			<c3>123</c3>"+
					"		</b>"+
					"		<b>"+
					"			<c1>12312</c1>"+
					"			<c2>123</c2>"+
					"			<c3>123</c3>"+
					"		</b>"+
					"	</alist></header></body>";

		System.out.println(aa);
		StringReader sr = new StringReader(aa);
		InputSource is = new InputSource(sr);
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("body/header", HashMap.class);
		digester.addCallMethod("body/header", "put", 2);
		digester.addCallParam("body/header", 0, "a1");
		digester.addCallParam("body/header", 1, "body/header/a1");

//		digester.addSetProperties( "body/header");
//		digester.addBeanPropertySetter("body/header/a1", "a1");
//		digester.addBeanPropertySetter("body/header/test", "test");
//		digester.addObjectCreate("body/header/alist/b", Digester3DomainDel.class);
//		digester.addBeanPropertySetter("body/header/alist/b/c1", "c1");
//		digester.addBeanPropertySetter("body/header/alist/b/c2", "c2");
//		digester.addBeanPropertySetter("body/header/alist/b/c3", "c3");
//		digester.addSetNext("body/header/alist/b", "addList");
		Map map = (Map)digester.parse(is);
//		System.out.println(digester3Domain.getList().get(1).getC1());
		System.out.println(map.toString());

		
	}
}
*/