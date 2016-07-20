package com.kerlyyuan.test.irssreader.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**  
 *
 * @Title: RSSSource.java
 * @Project: iRssReader
 * @Package: com.tencent.test.irssreader.parse
 * @Description: TODO
 * @author: kerlyyuan  
 * @date: 2015年11月12日 下午2:48:51
 * @version: v1.0 
 * Copyright © 2015 Tencent. All rights reserved.
 */

public class RSSSource {
	
	private static ArrayList<String> extRssTitleList = new ArrayList<String>();
	
	private static String[] extRssTitleItems = null;
	
	public static Map<String,String> extRssUrlDict = new HashMap<String,String>();
	
	
	public static void clear(){
		extRssTitleList.clear();
		extRssUrlDict.clear();
		extRssTitleItems = null;
	}
	
	public static String[] getRSSTitlList(){
		if(extRssTitleItems == null){
			extRssTitleItems = new String[extRssTitleList.size()];
			for(int i=0;i<extRssTitleList.size();i++){
				extRssTitleItems[i] = extRssTitleList.get(i);
			}
			return extRssTitleItems;
		}else{
			return extRssTitleItems;
		}
	}
	
	public static void add(String title){
		extRssTitleList.add(title);
		//需要重置items的值
		extRssTitleItems = null;
	}
	
	public static boolean delete(int index){
		if(index < extRssTitleList.size()){
			String value = extRssTitleList.remove(index);
			extRssUrlDict.remove(value);
			extRssTitleItems = null;
			return true;
		}
		return false;
	}
	
	public static final String[] RSS_TITLE_LIST = new String[]{
		"腾讯科技",
		"cnBeta资讯",
		"36氪资讯",
		"环球科学",
		"月光博客",
		"虎扑篮球新声",
		"NBA焦点新闻"
	};
	
	public static final Map<String,String> RSS_URL_DICT = new HashMap<String,String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(RSS_TITLE_LIST[0],"http://tech.qq.com/web/webnews/rss_11.xml");
			put(RSS_TITLE_LIST[1],"http://www.cnbeta.com/backend.php");
			put(RSS_TITLE_LIST[2],"http://www.36kr.com/feed");
			put(RSS_TITLE_LIST[3],"http://blog.sina.com.cn/rss/sciam.xml");
			put(RSS_TITLE_LIST[4],"http://www.williamlong.info/rss.xml");
			put(RSS_TITLE_LIST[5],"http://voice.hupu.com/generated/voice/news_nba.xml");
			put(RSS_TITLE_LIST[6],"http://news.baidu.com/n?cmd=1&class=nba&tn=rss");
			
		}
	};

}
