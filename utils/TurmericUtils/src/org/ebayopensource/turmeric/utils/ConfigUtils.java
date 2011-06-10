/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.utils.config.NameValue;
import org.ebayopensource.turmeric.utils.config.OptionList;


public class ConfigUtils {
	private static final String NL = "\n";
	
	public static OptionList copyOptionList(OptionList inList) {
		if (inList == null)
			return null;
		OptionList outList = new OptionList();
		putNameValueList(inList.getOption(), outList.getOption());
		return outList;
	}

	public static void putNameValueList(List<NameValue> inList, List<NameValue> outList) {
		for (NameValue nv : inList) {
			NameValue outNv = new NameValue();
			outNv.setName(nv.getName());
			outNv.setValue(nv.getValue());
			outList.add(outNv);
		}
	}
	
	public static QName copyQName(QName inName) {
		if (inName == null) {
			return null;
		}
		return new QName(inName.getNamespaceURI(), inName.getLocalPart());
	}

	
	public static <T> void dumpList(StringBuffer sb, Collection<T> slist) {
		boolean first = true;
		for (T s : slist) {
			if (!first) {
				sb.append(",");
			}
			sb.append(s);
			first = false;
		}
	}
	
	public static void dumpStringMap(StringBuffer sb, Map<String, String> map, String prefix) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(prefix + "key="+key+" value="+value+'\n');
		}
		
	}
	
	public static void dumpIntegerMap(StringBuffer sb, Map<String, Integer> map, String prefix) {
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			sb.append(prefix + "key="+key+" value="+value+'\n');
		}
		
	}

	public static void dumpOptionList(StringBuffer sb, OptionList options, String prefix) {
		if (options != null && options.getOption() != null
				&& !options.getOption().isEmpty()) {
			
			sb.append(prefix+"\toptions:" + NL);
			for (NameValue nv : options.getOption()) {
				sb.append(prefix+"\t\t("+NVToString(nv)+")" + NL);
			}
		}
	}
	
	public static String NVToString(NameValue nv) {
		return nv.getName()+"="+nv.getValue();
	}
}
