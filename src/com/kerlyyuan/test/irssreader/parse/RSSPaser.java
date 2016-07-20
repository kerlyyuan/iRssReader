package com.kerlyyuan.test.irssreader.parse;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.xml.sax.InputSource;

import android.util.Log;

/**  
 *
 * @Title: RSSPaser.java
 * @Project: iRssReader
 * @Package: com.tencent.test.irssreader.parse
 * @Description: TODO
 * @author: kerlyyuan  
 * @date: 2016年2月17日 下午5:13:42
 * @version: v1.0 
 * Copyright © 2016 Tencent. All rights reserved.
 */

public class RSSPaser {
	
	private static final String TAG = RSSPaser.class.getSimpleName();
	
	public static List<RSSItemInfo> parse(String link) throws IOException{
		URL url = new URL(link);
		
		Charset charset = getUrlCharset(url);
		Log.d(TAG, "charsetName:"+charset.name());
		
		InputStreamReader streamReader = new InputStreamReader(url.openStream(),charset.name()); 
		InputSource is = new InputSource(streamReader);
		return RSSXMLParseManager.parse(is);
	}
	
	private static Charset getUrlCharset(URL url){
		/*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
         * 指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        detector.add(new ByteOrderMarkDetector());
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         * 
         * 用到antlr.jar、chardet.jar
         */
        detector.add(JChardetFacade.getInstance());
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        
        
        java.nio.charset.Charset charset = null;
        try {
            charset = detector.detectCodepage(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (charset != null) {
            return charset;
        }
        return null;
	}

}
