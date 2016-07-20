package com.kerlyyuan.test.irssreader.parse;

import java.io.Serializable;

/**  
 *
 * @Title: RSSItemInfo.java
 * @Project: iRssReader
 * @Package: com.tencent.test.irssreader.parse
 * @Description: TODO
 * @author: kerlyyuan  
 * @date: 2015年7月3日 下午2:24:31
 * @version: v1.0 
 * Copyright © 2015 Tencent. All rights reserved.
 */

public class RSSItemInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String title = "";                //标题
	
	String link = "";                 //链接地址
	
	String description = "";          //内容简要描述
	
	String pubDate = "";              //发布时间
	
	String category = "";             //所属目录
	
	String auther = "";               //作者

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAuther() {
		return auther;
	}

	public void setAuther(String auther) {
		this.auther = auther;
	}

	@Override
	public String toString(){
		return this.title;
	}
	
}
