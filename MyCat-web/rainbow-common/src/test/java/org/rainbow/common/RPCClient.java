/*package org.rainbow.common;



import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.hx.rainbow.common.ws.util.WSDataUtil;



public class RPCClient {

	public static void main(String[] args) throws Exception {
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		 		"<XML>" +
		 		"<MESSAGEHEAD>" +
		 		"<SENDER> PLATFORM </SENDER>" +
		 		"<FILETYPE> XML </FILETYPE>" +
		 		"<CONTENTTYPE>Customer </CONTENTTYPE>" +
		 		"<SENDTIME>2008-05-30 10:10:10 </SENDTIME>" +
		 		"<FILENAME>20080530101010</FILENAME>" +
		 		"<FILEFUNCTION> ADD </FILEFUNCTION>" +
		 		"</MESSAGEHEAD>" +
				"<MESSAGEDETAIL>" +
				"<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><XML><CONTENTLIST>"+
				"		<CONTENT> "+
				"  	<HEAEDER>"+
				"</HEADER> "+
				"<DETAIL>"+
				"		<GCOMPANYID> GCOMPANYID </GCOMPANYID> "+  
				"		<COMPANYID> COMPANYID </COMPANYID>  "+
				"        <COMPANYNAME> COMPANYNAME </COMPANYNAME>  "+ 
				"		<COMPANYTYPE> COMPANYTYPE </COMPANYTYPE>  "+
				"        <GOMACODE> GOMACODE </GOMACODE >   "+
				"		<CONNECTOR> CONNECTOR </CONNECTOR >  "+
				"        <CONNPHONE> CONNPHONE </CONNPHONE >   "+
				"		<INVOICEADDRESS> INVOICEADDRESS </INVOICEADDRESS>  "+
				"        <DELIVERADDRESS> DELIVERADDRESS </DELIVERADDRESS>   	"+
				"	</DETAIL> "+                                    	
				"   	</CONTENT>    "+                                	
				"    </CONTENTLIST>  "+                              	
				"</XML>  "+                                      	
				"  ]]>  "+                                     	
				"</MESSAGEDETAIL>  "+
				"</XML>";
                              
		
		try{
			long start = System.currentTimeMillis();
//			URL url = new java.net.URL("http://127.0.0.1:8080/rainbow/ws/GreetingService?wsdl");
//			QName service =  new javax.xml.namespace.QName("http://service.ws.test.gary.com/", "GreetingServiceImplService");
//	        RPCServiceClient serviceClient = new RPCServiceClient(null,url,service,"GreetingServiceImplPort");
//
//	        
//	       // EndpointReference targetEPR = new EndpointReference("http://127.0.0.1:8080/rainbow/ws/GreetingService");
//	       // option.setTo(targetEPR);
//	          // 指定访问 方法的参数值 
//	          Object[] opAddEntryArgs = new Object[] { xmlStr }; 
//	          // 指定访问 方法返回值的数据类型的Class 对象 
//	          Class[] classes = new Class[] { String.class }; 
//	          // 指定要调用的方法及WSDL 文件的命名空间 
//	          // QName 第一个参数为命名空间即文件中xschema中targetnamespace的值 
//	          QName opAddEntry = new QName("http://service.ws.test.gary.com/", "echo");
//	           // 调用访问 方法并输出该方法的返回值 
//	          Object res=serviceClient.invokeBlocking(opAddEntry,opAddEntryArgs, classes)[0];

			//axis1.4中动态调用
			System.out.println(WSDataUtil.getInstance()
					.sendWS4AXIS("http://127.0.0.1:8081/rainbow/ws/SetAddressLine?wsdl", "http://service.ws.wsplat.spl.com/", 
							"AddressLineImplService", "AddressLineImplPort", "setAddressLine","xmlStr", xmlStr, 10000l));
			
			//axis1.4客户端生成调用
//			AddressLineImplService service = new AddressLineImplServiceLocator();
//			AddressLine addressLine = service.getAddressLineImplPort();
//			System.out.println(addressLine.setAddressLine(xmlStr));
			System.out.println(System.currentTimeMillis() - start);
			// System.out.println(serviceClient.invokeBlocking(opAddEntry,
			// opAddEntryArgs, classes)[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}*/