package org.rainbow.common;

import java.io.File;

public class CreatIntall {
	public static void main(String[] args) {
		CreatIntall.createMaven();
	}
	
	public static void createMaven(){
		File file = new File("E:\\hx\\mywork\\rainbow\\rainbow\\rainbow-common\\lib\\new");
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File filechild : files) {
				String fileName = filechild.getName();
//				System.out.println(fileName);
				String artifactId = fileName.substring(0,fileName.lastIndexOf('-'));
				String version = fileName.substring(fileName.lastIndexOf('-')+1,fileName.lastIndexOf('.'));
//				System.out.println("mvn install:install-file -DgroupId=org.rainbow.lib -DartifactId="+artifactId+" -Dversion="+version+" -Dclassifier=deps  -Dpackaging=jar -Dfile="+fileName+"");
//				System.out.println("mvn install:install-file -DgroupId=org.rainbow.lib -DartifactId="+artifactId+" -Dversion="+version+"  -Dpackaging=jar -Dfile="+fileName+"");
				
					System.out.println("<dependency>");
					System.out.println("    <groupId>org.rainbow.lib</groupId>");
					System.out.println("    <artifactId>"+artifactId+"</artifactId>");
					System.out.println("    <version>${"+artifactId+".version}</version>");
					System.out.println("</dependency>");
					System.out.println("<"+artifactId+".version>"+version+"</"+artifactId+".version>");
			}
		}
	
	}
	
	
	public static void changeSpringJarName(){
		File file = new File("E:\\hx\\mywork\\lib\\spring-framework-3.1.3.RELEASE\\dist");
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File filechild : files) {
				String fileName = filechild.getName();
				//String gropid = fileName.substring(0,fileName.lastIndexOf(".", fileName.indexOf('-')));
				String artifactId = fileName.substring(fileName.lastIndexOf('.',fileName.lastIndexOf('-'))+1,fileName.lastIndexOf('-'));
				String version = fileName.substring(fileName.lastIndexOf('-')+1,fileName.lastIndexOf('.'));
				System.out.println(filechild.getParent()+"\\spring-"+artifactId+"-"+version+".jar");
				filechild.renameTo(new File(filechild.getParent()+"\\spring-"+artifactId+"-"+version+".jar"));

			}
		}
	}
}


