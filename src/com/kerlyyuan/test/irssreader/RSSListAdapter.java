package com.kerlyyuan.test.irssreader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import com.kerlyyuan.test.irssreader.parse.RSSItemInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**  
 *
 * @Title: RSSListAdapter.java
 * @Project: iRssReader
 * @Package: com.tencent.test.irssreader
 * @Description: TODO
 * @author: kerlyyuan  
 * @date: 2016年2月23日 下午2:55:17
 * @version: v1.0 
 * Copyright © 2016 Tencent. All rights reserved.
 */

public class RSSListAdapter extends BaseAdapter {
	
	private LinkedList<RSSItemInfo> data = null;
	private LayoutInflater layoutInflater;
	
	public RSSListAdapter(Context context,LinkedList<RSSItemInfo> data){
		this.data = data;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Item item = null;
		
		//初始化UI
		if(convertView == null){
			item = new Item();
			convertView = layoutInflater.inflate(R.layout.rss_list, null);
			item.title = (TextView) convertView.findViewById(R.id.rss_title);
			TextPaint tp = item.title.getPaint();
			tp.setFakeBoldText(true);
			item.pubDate = (TextView) convertView.findViewById(R.id.rss_pubDate);
			convertView.setTag(item);
		}else{
			item = (Item) convertView.getTag();
		}
		
		//绑定数据
		item.title.setText(data.get(position).getTitle());
		String dateStr = getDateStringFormat(data.get(position).getPubDate().trim());
		item.pubDate.setText(dateStr);
		return convertView;
	}
	
	public final class Item {
		public TextView title;
		public TextView pubDate;
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getDateStringFormat(String dateStr){
		String tmp = dateStr.trim();
		String result = "";
		if((tmp.indexOf(",") != -1 && tmp.indexOf("GMT") != -1)||
				(tmp.indexOf(",") != -1 && tmp.indexOf("+0800") != -1)){
			try {
				DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.ENGLISH);
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date date = format.parse(tmp);
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				result = format1.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(tmp.indexOf("T") != -1 && tmp.indexOf("Z") != -1){
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ENGLISH);
				Date date = format.parse(tmp);
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				result = format1.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(result.equals("")){
			result = dateStr;
		}
		return result;	
	}

}
