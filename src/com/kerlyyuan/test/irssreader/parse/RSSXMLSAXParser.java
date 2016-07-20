package com.kerlyyuan.test.irssreader.parse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**  
 *
 * @Title: XMLTextParser.java
 * @Project: iRssReader
 * @Package: com.tencent.test.irssreader.parse
 * @Description: TODO
 * @author: kerlyyuan  
 * @date: 2015年7月3日 下午2:18:32
 * @version: v1.0 
 * Copyright © 2015 Tencent. All rights reserved.
 */

public class RSSXMLSAXParser extends DefaultHandler{
	
	//private static final String TAG = RSSXMLSAXParser.class.getSimpleName();

    List<RSSItemInfo> list;
    
    RSSItemInfo rssItem;
    
    boolean itemIsValid = false;
    
    boolean isLink = false;
    
    String curValue = "";
    
    String curNodeName = "";
    
    private SAXParser parser;
    
    private static RSSXMLSAXParser me = null;
    
    private RSSXMLSAXParser(){
    	//实例化一个SAXParserFactory对象
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	try {
			parser = factory.newSAXParser();
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static RSSXMLSAXParser getInstance(){
    	if(me == null){
    		me = new RSSXMLSAXParser();
    	}
    	return me;
    }
    
    public List<RSSItemInfo> getRssList(InputSource is) {
    	try {
			parser.parse(is, me);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return list;
    }

    /*
     * 接口字符块通知
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
    	// super.characters(ch, start, length);
    	
        String theString = String.valueOf(ch, start, length);

        curValue += theString;

        return;
    }

    /*
     * 接收文档结束通知
     */
    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
        super.endDocument();
    }

    /*
     * 接收标签结束通知
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        if (qName.equals("item")){
        	list.add(rssItem);
        	itemIsValid = false;
        }else if(qName.equals("title") && itemIsValid){
        	rssItem.setTitle(curValue.trim());
        }else if(qName.equals("link") && itemIsValid){
        	rssItem.setLink(curValue.trim());
        	isLink = false;
        	//System.out.println("LINK:"+curValue.trim());
        }else if(qName.equals("description") && itemIsValid){
        	rssItem.setDescription(curValue);
        }else if(qName.equals("pubDate") && itemIsValid){
        	rssItem.setPubDate(curValue);
        }else if(qName.equals("category") && itemIsValid){
        	rssItem.setCategory(curValue);
        }else if(qName.equals("auther") && itemIsValid){
        	rssItem.setAuther(curValue);
        }
        curValue = "";
    }

    /*
     * 文档开始通知
     */
    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub
        list = new LinkedList<RSSItemInfo>();
    }

    /*
     * 标签开始通知
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        if (qName.equals("item")) {
        	rssItem = new RSSItemInfo();
        	itemIsValid = true;
        }
        if(qName.equals("link")){
        	isLink = true;
        }
        curNodeName = qName;
        return;
    }
}
