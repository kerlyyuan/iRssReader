package com.kerlyyuan.test.irssreader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kerlyyuan.test.irssreader.parse.RSSPaser;
import com.kerlyyuan.test.irssreader.parse.RSSSource;
import com.kerlyyuan.test.irssreader.parse.RSSItemInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**  
 *
 * @Title: SettingsActivity.java
 * @Project: iRssReader
 * @Package: com.tencent.test.irssreader
 * @Description: TODO
 * @author: kerlyyuan  
 * @date: 2016年2月16日 下午4:17:02
 * @version: v1.0 
 * Copyright © 2016 Tencent. All rights reserved.
 */

public class SettingsActivity extends FragmentActivity{
	
	private static final String TAG = SettingsActivity.class.getSimpleName();
	
	private ListView listView;
	
	private ArrayAdapter<String> adapter;
	
	ProgressDialog mDialog;
	
	private List<String> data = new ArrayList<String>();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.settings);
        
        getData();
        
        listView = (ListView) findViewById(R.id.settings_title_list);
        adapter = new ArrayAdapter<String>(this, 
        		R.layout.settings_list_item,R.id.settings_rss_title_item,data);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //ToastUtils.show(getApplicationContext(), R.string.drop_down_tip);
            	Log.d(TAG,"position:"+position);
                if(position == RSSSource.getRSSTitlList().length){
                	showAddOperationDialog();
                }else{
                	showEditOperationDialog(position);
                }
            }
        });
	}
	
	private List<String> getData(){
		data.clear();
        for(int i=0;i<RSSSource.getRSSTitlList().length;i++){
        	data.add(RSSSource.getRSSTitlList()[i]);
        }
        data.add("+");
        return data;
    }
	
	/**
	 * 显示添加rss源对话框
	 * */
	private void showAddOperationDialog(){
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.settings_add_rss,
				(ViewGroup) findViewById(R.id.rss_add_dialog));
		
		final EditText etRSSTitle = (EditText) layout.findViewById(R.id.et_add_title);
		final EditText etRSSUrl = (EditText) layout.findViewById(R.id.et_add_url);
		
		new AlertDialog.Builder(this).setTitle("添加RSS源")
			.setView(layout).setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String title = etRSSTitle.getText().toString();
					String url = etRSSUrl.getText().toString();
					/*Toast.makeText(SettingsActivity.this, 
							title+"-"+url, Toast.LENGTH_SHORT).show();*/
					
					new CheckUrlTask(title,url).execute();
				}
				
			})
			.setNegativeButton("取消", null).show();
	}
	
	/**
	 * 显示编辑rss对话框
	 * */
	private void showEditOperationDialog(final int position){
		new AlertDialog.Builder(SettingsActivity.this)
		.setTitle("编辑")
		.setItems(R.array.dialog_arrays,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
						String[] items = getResources()
							.getStringArray(R.array.dialog_arrays);
						if(items[which].equals(SettingsEditManager.MODIFY)){
							editRSSData(position);
						}else if(items[which].equals(SettingsEditManager.DELETE)){
							String title = RSSSource.getRSSTitlList()[position];
							new AlertDialog.Builder(SettingsActivity.this)
								.setTitle("提示").setMessage("确认删除"+title)
								.setPositiveButton("确认",new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										boolean removeResult = RSSSource.delete(position);
										if(removeResult){
											
											SharedPreferences sharedPreferences = getSharedPreferences(
													MainActivity.SHARED_TAG, Context.MODE_PRIVATE);
											Editor editor = sharedPreferences.edit();
											int num = RSSSource.getRSSTitlList().length;
											editor.putInt("num", num);
											if(position != num){
												for(int i = position;i<num;i++){
													String title = RSSSource.getRSSTitlList()[i];
													editor.putString(""+i, title+" "+RSSSource.extRssUrlDict.get(title));
												}
												editor.remove(""+num);
											}
											editor.commit();
											
											if(MainActivity.curRssUrlIndex >= num){
												MainActivity.curRssUrlIndex = num-1;
											}

											Toast.makeText(SettingsActivity.this, 
													"删除成功!", Toast.LENGTH_SHORT).show();
											getData();
											adapter.notifyDataSetChanged();
										}else{
											Toast.makeText(SettingsActivity.this, 
													"删除失败!", Toast.LENGTH_SHORT).show();
										}
									}
								}).setNegativeButton("取消",null).create().show();
						}else{
							Log.d(TAG,"rss edit operate cancel.");
						}
						
				}
			}).create().show();
	}
	
	private void editRSSData(final int index){
		final String title = RSSSource.getRSSTitlList()[index];
		final String url = RSSSource.extRssUrlDict.get(title);
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.settings_edit_rss,
				(ViewGroup) findViewById(R.id.rss_edit_dialog));
		EditText etRSSUrl = (EditText) layout.findViewById(R.id.et_rss_uri);
		etRSSUrl.setText(url);
		etRSSUrl.setSelection(etRSSUrl.getText().length());
		
		new AlertDialog.Builder(this).setTitle(title)
			.setView(layout).setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					CheckUrlTask task = new CheckUrlTask(title,url,false);
					task.setIndex(index);
					task.execute();
				}
				
			})
			.setNegativeButton("取消", null).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.settings, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {  
/*	    case R.id.action_edit:  
	        Toast.makeText(this, "开始编辑RSS源列表", Toast.LENGTH_SHORT).show();  
	        return true;  */
	    case R.id.action_settings_edit:  
	        //Toast.makeText(this, "编辑功能暂未开启", Toast.LENGTH_SHORT).show();
	        return true;  
	    default:  
	        return super.onOptionsItemSelected(item);  
	    }
	}

	class CheckUrlTask extends AsyncTask<Void, Integer, Boolean>{
		
		String url = "";
		String title = "";
		boolean isAdd = true;
		int index = -1;
		
		public CheckUrlTask(String title,String url) {
			this.title = title;
            this.url = url;
        }
		
		public CheckUrlTask(String title,String url,boolean isAdd) {
			this.title = title;
            this.url = url;
            this.isAdd = isAdd;
        }
		
		public void setIndex(int index){
			this.index = index;
		}
		
		@Override
		protected void onPreExecute(){
			mDialog = ProgressDialog.show(SettingsActivity.this, "温馨提示", "正在检查RSS源数据......");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			boolean checkUrlValid = false;
			try {
				List<RSSItemInfo> result = RSSPaser.parse(url);
				
				if(result != null && result.size() != 0){
					try{
						Random rand = new Random();
						String link = result.get(rand.nextInt(result.size()-1)).getLink();
						Log.d(TAG,"RSS Source check link:"+link);
						URL url = new URL(link);
				        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				        conn.setConnectTimeout(5000);
				        conn.setRequestMethod("GET");
				        if (conn.getResponseCode() == 200) {
				        	checkUrlValid = true;
				        }
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return checkUrlValid;
		}
		
		@Override  
	    protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if(result){
				Toast.makeText(SettingsActivity.this, "RSS源检查成功！", Toast.LENGTH_SHORT).show();
				SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_TAG, 
						Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				if(isAdd){
					RSSSource.add(title);
					RSSSource.extRssUrlDict.put(title, url);
					
					//添加到内存数据的同事，还需要更新本地存储信息
					int curNum = RSSSource.getRSSTitlList().length;
					editor.putInt("num", curNum);
					editor.putString(""+(curNum-1), title+" "+url);
					editor.commit();
				}else{
					editor.putString(""+index, title+" "+url);
					editor.commit();
				}
				
				getData();
				
				adapter.notifyDataSetChanged();
			}else{
				Toast.makeText(SettingsActivity.this, "RSS源无效！", Toast.LENGTH_SHORT).show();  
			}
		}
	}
}
