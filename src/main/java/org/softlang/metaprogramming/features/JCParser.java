package org.softlang.metaprogramming.features;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;



public class JCParser{
	
	//fetch every java file
	public static List<FileInputStream> initJC(String path) {
		
		File projectDir = new File(path);
		File[] projectFiles = projectDir.listFiles();
		
		List<String> javaFiles = Arrays.stream(projectFiles).map(f -> {
			if(f.isFile()) return f.getName();
				return null;
		})
				.filter(x -> (!(x.equals("ObjectFactory.java")) 
						&& !(x.equals("package-info.java")) 
						&& (x.endsWith(".java")) ))
				.collect(Collectors.toList());
		
		List<FileInputStream> fInputs = javaFiles.stream().map(f -> {
			try {
				return new FileInputStream(path + f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());
		return fInputs;
	}
		
	public static Map<String, HashMap<String, String>> parseJC(List<FileInputStream> fInputs) {
			
			Map<String, HashMap<String, String>> parsedClazzez = new HashMap<>();
			
			fInputs.forEach(fileName -> {
		            CompilationUnit cu = JavaParser.parse(fileName);
		            HashMap<String, String> fieldStorage = new HashMap<>();
					for (TypeDeclaration type : cu.getTypes()) {
						cu.findAll(FieldDeclaration.class).forEach(field -> {
					    field.getVariables().forEach(variable -> {
					        fieldStorage.put(variable.getName().asString() , variable.getType().asString() );
					    	});
						});
						parsedClazzez.put(cu.findAll(ClassOrInterfaceDeclaration.class).get(0).getNameAsString() , fieldStorage);
					}
		    });
			System.out.println();
			System.out.println("-----JC-----:");
			parsedClazzez.forEach((cName, fields) -> {
				System.out.println(cName+": ");
				fields.forEach((decl, type) -> {
					System.out.println(" "+type+" "+decl);
				});
			});
			return parsedClazzez;
	}
}