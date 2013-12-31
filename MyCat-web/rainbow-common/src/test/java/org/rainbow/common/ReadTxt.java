package org.rainbow.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ReadTxt {
	public static void main(String[] args) {
		File file = new File("D:\\userId.txt");
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
		    BufferedReader br = new BufferedReader(isr);
		    StringBuffer sb = new StringBuffer();
		    String lineTXT ="";
		    while ((lineTXT = br.readLine()) != null){
		    	sb.append("'" + lineTXT + "',");
		    }
		    System.out.println(sb.toString());
		    isr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
