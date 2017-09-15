package com.sirap.basic.tool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("rawtypes")
public class ArisDetail {
	private Class glass;
	private Set<String> imports = new TreeSet<>();
	
	public ArisDetail(Class glass) {
		this.glass = glass;
	}
	
	private List<String> content = new ArrayList<>();
	
	public List<String> getAllParts() {
		String location = getLocation();
		String packageInfo = readPackageInfo();
		String definition = readClassDefinition();
		List<String> constructors = readConstructors();
		List<String> fields = readFields();
		List<String> methods = readMethods();
		List<String> importInfo = createImportSentences();

		if(!EmptyUtil.isNullOrEmpty(location)) {
			content.add(location);
			content.add("");
		}
		if(!EmptyUtil.isNullOrEmpty(packageInfo)) {
			content.add(packageInfo);
			content.add("");
		}
		if(!EmptyUtil.isNullOrEmpty(importInfo)) {
			content.addAll(importInfo);
			content.add("");
		}
		content.add(definition);
		if(!EmptyUtil.isNullOrEmpty(fields)) {
			content.add("");
			content.addAll(fields);
		}
		if(!EmptyUtil.isNullOrEmpty(constructors)) {
			content.add("");
			content.addAll(constructors);
		}
		if(!EmptyUtil.isNullOrEmpty(methods)) {
			content.add("");
			content.addAll(methods);
		}
		content.add("}");
		
		return content;
	}
	
	private List<String> createImportSentences() {
		List<String> items = new ArrayList<>();
		for(String item : imports) {
			items.add("import " + item + ";");
		}
		
		return items;
	}
	
	private String getLocation() {
		CodeSource source = glass.getProtectionDomain().getCodeSource();
		if(source != null && source.getLocation() != null) {
			String temp = source.getLocation().toString();
			temp = temp.replaceAll("^file:/", "").replaceAll("/$", "");
			return temp;
		} else {
			String entryName = glass.getName().replace('.', '/') + ".class";
			String someRuntimeJar = ArisUtil.belongsToWhichRuntimeJar(entryName);
			if(someRuntimeJar != null) {
				return someRuntimeJar;
			} else {
				return StrUtil.occupy("Uncanny, not found {0} in {1}", entryName, ArisUtil.getRuntimeLibraryLocation());
			}
		}
	}
	
	private String readPackageInfo() {
		Package pack = glass.getPackage();
		return pack != null ? "package " + pack.getName() + ";" : "";
	}
	
	private String readClassDefinition() {
		StringBuilder sb = new StringBuilder();
		String modifiers= Modifier.toString(glass.getModifiers());
		String classOrInterfaceOrElse = "class";
		if(glass.isEnum()) {
			classOrInterfaceOrElse = "enum";
		} else if(glass.isInterface()) {
			classOrInterfaceOrElse = "";
		}
		boolean isInterface = glass.isInterface();
		sb.append(modifiers).append(" ");
		if(!EmptyUtil.isNullOrEmpty(classOrInterfaceOrElse)) {
			sb.append(classOrInterfaceOrElse).append(" ");
		}
		sb.append(glass.getSimpleName());
		List<String> supers = readAllSuperClasses(glass);
		if(!EmptyUtil.isNullOrEmpty(supers)) {
			sb.append(" extends ").append(StrUtil.connect(supers, ", "));
		}
		Class[] facesArr = glass.getInterfaces();
		List<String> faceNames = new ArrayList<>();
		for(Class face : facesArr) {
			addImports(face.getName());
			faceNames.add(face.getSimpleName());
		}
		if(!EmptyUtil.isNullOrEmpty(faceNames)) {
			if(isInterface) {
				sb.append(" extends ");
			} else {
				sb.append(" implements ");
			}
			sb.append(StrUtil.connect(faceNames, ", "));
		}
		
		sb.append(" ").append("{");
		
		return sb.toString();
	}
	
	private Object getFieldValue(Field item) {
		Class type = item.getType();
		try {
			Object value = item.get(glass);
			String arrValue = StrUtil.arrayToString(value);
			if(arrValue != null) {
				return arrValue;
			} else if("char".equals(type.toString()) || Character.class.equals(type)) {
				return "\'" + value + "\'";
			} else if(String.class.equals(type)) {
				return "\"" + value + "\"";
			} else {
				return value;
			}
		} catch (Exception ex) {
			//throw new MexException(ex);
		}
		
		return null;
	}

	private List<String> readFields() {
		Field[] fields= glass.getDeclaredFields(); 
		List<String> items = new ArrayList<>();
		for(Field item : fields) {
            String modifier = Modifier.toString(item.getModifiers());  
			Class type = item.getType();
			addImports(type.getName());
            
            StringBuilder sb = new StringBuilder();
            sb.append("\t");
            if(!EmptyUtil.isNullOrEmpty(modifier)) {
            	sb.append(modifier).append(" ");
            }
            sb.append(type.getSimpleName()).append(" ");

            sb.append(item.getName());
            Object value = getFieldValue(item);
            if(value != null) {
                sb.append(" = ").append(value);
            }
            sb.append(";");
            
            items.add(sb.toString());
		}
		
		Collections.sort(items);
		
		return items;
	}

	private List<String> readMethods() {
		Method[] methods= glass.getDeclaredMethods();
		List<String> items = new ArrayList<>();
		for(Method item : methods) {
            String modifier = Modifier.toString(item.getModifiers());  
			Class type = item.getReturnType();
			addImports(type.getName());
            
            StringBuilder sb = new StringBuilder();
            sb.append("\t");
            if(!EmptyUtil.isNullOrEmpty(modifier)) {
            	sb.append(modifier).append(" ");
            }
            sb.append(type.getSimpleName()).append(" ");
            sb.append(item.getName());
            Class[] paramTypesArr = item.getParameterTypes();
            Parameter[] paramNamesArr = item.getParameters();
            List<String> paramInfo = new ArrayList<>();
            sb.append("(");
    		for(int i = 0; i < paramTypesArr.length; i++) {
    			Class paramType = paramTypesArr[i];
    			addImports(paramType.getName());
    			
    			String paramName = paramNamesArr[i].getName();
    			paramInfo.add(paramType.getSimpleName() + " " + paramName);
    		}
    		sb.append(StrUtil.connect(paramInfo, ", "));
            sb.append(")");
            
            List<String> exceptionNames = new ArrayList<>();
            Class[] expcetionTypesArr = item.getExceptionTypes();
            for(int i = 0; i < expcetionTypesArr.length; i++) {
    			Class expcetionType = expcetionTypesArr[i];
    			addImports(expcetionType.getName());
    			exceptionNames.add(expcetionType.getSimpleName());
    		}
            
            if(!EmptyUtil.isNullOrEmpty(exceptionNames)) {
            	sb.append(" ");
            	sb.append(StrUtil.connect(exceptionNames, ", "));
            }
            
            sb.append(";");
            items.add(sb.toString());
		}
		Collections.sort(items);
		
		return items;
	}

	private List<String> readConstructors() {
		Constructor[] methods = glass.getDeclaredConstructors();
		List<String> items = new ArrayList<>();
		for(Constructor item : methods) {
            String modifier = Modifier.toString(item.getModifiers());  
            
            StringBuilder sb = new StringBuilder();
            sb.append("\t");
            if(!EmptyUtil.isNullOrEmpty(modifier)) {
            	sb.append(modifier).append(" ");
            }
            sb.append(glass.getSimpleName());
            Class[] paramTypesArr = item.getParameterTypes();
            Parameter[] paramNamesArr = item.getParameters();
            List<String> paramInfo = new ArrayList<>();
            sb.append("(");
    		for(int i = 0; i < paramTypesArr.length; i++) {
    			Class paramType = paramTypesArr[i];
    			addImports(paramType.getName());
    			
    			String paramName = paramNamesArr[i].getName();
    			paramInfo.add(paramType.getSimpleName() + " " + paramName);
    		}
    		sb.append(StrUtil.connect(paramInfo, ", "));
            sb.append(")");
            
            List<String> exceptionNames = new ArrayList<>();
            Class[] expcetionTypesArr = item.getExceptionTypes();
            for(int i = 0; i < expcetionTypesArr.length; i++) {
    			Class expcetionType = expcetionTypesArr[i];
    			addImports(expcetionType.getName());
    			exceptionNames.add(expcetionType.getSimpleName());
    		}
            
            if(!EmptyUtil.isNullOrEmpty(exceptionNames)) {
            	sb.append(" ");
            	sb.append(StrUtil.connect(exceptionNames, ", "));
            }
            
            sb.append(";");
            items.add(sb.toString());
		}
		Collections.sort(items);
		
		return items;
	}
	
	private List<String> readAllSuperClasses(Class source) {
		List<String> names = new ArrayList<>();
		Class father = source.getSuperclass();
		while(father != null) {
			if(father.equals(Object.class)) {
				break;	
			}
			addImports(father.getName());
			names.add(father.getSimpleName());
			father = father.getSuperclass();
		}
		
		return names;
	}
	
	private boolean addImports(String className) {
		if(Object.class.getName().equals(className)) {
			return false;
		}

		String realClassName = null;
		String regexArrayType = "\\[L(.+);";
		String arrayTypeName = StrUtil.parseParam(regexArrayType, className);
		if(arrayTypeName != null) {
			realClassName = arrayTypeName;
		} else {
			realClassName = className;
		}
		
		if(!realClassName.contains(".")) {
			return false;
		}
		
		imports.add(realClassName);
		return true;
	}
}

enum LOTS {
	
}
