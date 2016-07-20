package com.kerlyyuan.test.irssreader.parse;

import java.util.List;

import org.xml.sax.InputSource;

/**  
 *
 * @Title: XMLTextParseManager.java
 * @Project: iRssReader
 * @Package: com.tencent.test.irssreader.parse
 * @Description: TODO
 * @author: kerlyyuan  
 * @date: 2015年7月3日 下午2:20:44
 * @version: v1.0 
 * Copyright © 2015 Tencent. All rights reserved.
 */

public class RSSXMLParseManager {
	
	public static final String PARSE_TYPE = "sax";
	
	public static final String PARSE_DOM = "dom";
	
	public static List<RSSItemInfo> parse(InputSource is){
		return parse(is,PARSE_TYPE);
	}
	
	public static List<RSSItemInfo> parse(InputSource is,String type){
		if(PARSE_TYPE.equals(type)){
			return RSSXMLSAXParser.getInstance().getRssList(is);
		}else if(PARSE_DOM.equals(type)){
			return null;
		}else{
			return null;
		}
	}

}
