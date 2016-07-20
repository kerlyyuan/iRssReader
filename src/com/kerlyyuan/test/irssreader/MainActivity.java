package com.kerlyyuan.test.irssreader;


import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.kerlyyuan.test.irssreader.parse.RSSPaser;
import com.kerlyyuan.test.irssreader.parse.RSSSource;
import com.kerlyyuan.test.irssreader.parse.RSSItemInfo;

import cn.trinea.android.common.view.DropDownListView;
import cn.trinea.android.common.view.DropDownListView.OnDropDownListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



@SuppressLint("HandlerLeak")
public class MainActivity extends FragmentActivity {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	//private static final int TIME_OUT = 60000;
	
	public static final String SHARED_TAG = "rss_url_index";
	
	private LinkedList<RSSItemInfo> listItems = null;
    private DropDownListView  listView = null;
    //private ArrayAdapter<RSSItemInfo> adapter;
    private BaseAdapter adapter;
    public int  moreDataCount = 0;
    
    ProgressDialog mDialog;

    public static int curRssUrlIndex = 0;
    private String curRssUrl = "";
    
    private static boolean updateFlag = false;
    
    //back键退出标记
    boolean isExist = false;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d(TAG,"onCreate");
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.drop_down_listview);
		
		initRSSSourceData();

        listView = (DropDownListView)findViewById(R.id.list_view);
        listView.setVisibility(View.INVISIBLE);
        listView.setSaveEnabled(true);
        
        // set drop down listener
        listView.setOnDropDownListener(new OnDropDownListener() {

            @Override
            public void onDropDown() {
            	//updateFlag = true;
               
            	new GetDataTask(true).execute();

            }
        });

        /*// set on bottom listener
        listView.setOnBottomListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new GetDataTask(false).execute();
            }
        });*/
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ToastUtils.show(getApplicationContext(), R.string.drop_down_tip);
            	Log.d(TAG,"listView size:"+listItems.size()+",position:"+position);
                RSSItemInfo rss = listItems.get(position-1);
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rss.getLink()));  
                intent.addCategory(Intent. CATEGORY_BROWSABLE);  
                intent.addCategory(Intent. CATEGORY_DEFAULT);  
                startActivity(intent);*/
                
                Log.d(TAG,"title:"+rss.getTitle());
                Log.d(TAG,"link:"+rss.getLink());
                
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(rss.getLink()));
                try{
                	startActivity(intent);
                }catch(Exception e){
                	Toast.makeText(getApplicationContext(), "数据源链接异常！", Toast.LENGTH_SHORT).show();
                }
                
            }
        });
        
        listView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
        	
        });
        
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("数据源")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(
						RSSSource.getRSSTitlList(), curRssUrlIndex,
						new OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								if(curRssUrlIndex != which){
									curRssUrlIndex = which;
									SharedPreferences sharedPreferences = 
											getSharedPreferences(SHARED_TAG, Context.MODE_PRIVATE);
									Editor editor = sharedPreferences.edit();
									editor.putInt("index", which);
									editor.commit();
									curRssUrl = RSSSource.getRSSTitlList()[which];
									dialog.dismiss();
							
									new GetDataTask(true).execute();
	 
								}
							}
						}).setNegativeButton("取消", null).show();
				return true;
			}
        });
        
        //listView.setShowFooterWhenNoMore(true);
        
        listItems = new LinkedList<RSSItemInfo>();

        new GetDataTask(true).execute();

        mDialog = ProgressDialog.show(MainActivity.this, "温馨提示", "正在加载数据......");

        //adapter = new ArrayAdapter<RSSItemInfo>(this, android.R.layout.simple_list_item_1, listItems);
        adapter = new RSSListAdapter(this,listItems);
        listView.setAdapter(adapter);
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg){
			isExist = false; //取消退出标志位
		}
	};
	
	/**
	 * 监听back键，当连续2次点击时间在2秒内，则退出程序
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!isExist){
				isExist = true;
				Toast.makeText(getApplicationContext(), 
						"再按一次退出程序", Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessageDelayed(0,2000);
				return false;
			}else{
				/*Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);*/
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		setTitle(getTitleString());
		
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
	    case R.id.action_settings:  
	        //Toast.makeText(this, "设置功能暂未开启", Toast.LENGTH_SHORT).show();
	    	Intent intent = new Intent(this, SettingsActivity.class);
	    	startActivity(intent);
	        return true;
	    case R.id.action_about:
	    	new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
	    	.setTitle("帮助信息")  
            .setMessage(getResources().getString(R.string.app_name)+" v1.0"  
                            + "\n"  
                            + "作者：E品-江南雨\n"  
                            + "邮件：290259324@qq.com\n"
                            + "如果在使用过程中有任何问题可以随时联系我！")  
            .setPositiveButton("确定",  
                    new DialogInterface.OnClickListener() {  
                        public void onClick(DialogInterface dialog,  
                                int whichButton) {  

                            /* User clicked OK so do some stuff */  
                        }  
                    }).create().show();
	    default:  
	        return super.onOptionsItemSelected(item);  
	    }
	}
	
	@Override
	public void finish(){
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_TAG, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("index", curRssUrlIndex);
		editor.commit();
		super.finish();
	}
	
	private boolean refreshStats(){
		boolean result = false;
		try {
			listItems.clear();
			
			listItems.addAll(RSSPaser.parse(RSSSource.extRssUrlDict.get(curRssUrl)));
			result = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d(TAG,"mData size:"+listItems.size()+",listView will update.");
		
		return result;
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, Boolean> {

        private boolean isDropDown;

        public GetDataTask(boolean isDropDown) {
            this.isDropDown = isDropDown;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
        	
        	Log.d(TAG,"updateFlag:"+updateFlag);

        	if(updateFlag){
        		/*Toast.makeText(getApplicationContext(), "数据正在刷新中，请稍后再试。。。",
        			     Toast.LENGTH_SHORT).show();*/
        		return true;
        	}else{
        		updateFlag = true;
        		return refreshStats();
        	}
            
        }

        @SuppressLint("SimpleDateFormat")
		@Override
        protected void onPostExecute(Boolean result) {
        	
        	mDialog.dismiss();
        	listView.setVisibility(View.VISIBLE);
        	
        	if(result){
				Toast.makeText(getApplicationContext(), "数据更新成功！", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), "数据更新失败，"
						+ "请检查网络或数据源是否有效！", Toast.LENGTH_SHORT).show();
			}
        	
        	updateFlag = false;

            if (isDropDown) {
            	
            	setTitle(getTitleString());
            	
                //listItems.addFirst("Added after drop down");
                adapter.notifyDataSetChanged();

                // should call onDropDownComplete function of DropDownListView at end of drop down complete.
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                listView.onDropDownComplete(getString(R.string.update_at) + dateFormat.format(new Date()));
            } /*else {
                moreDataCount++;
                listItems.add("Added after on bottom");
                adapter.notifyDataSetChanged();

                if (moreDataCount >= MORE_DATA_MAX_COUNT) {
                    listView.setHasMore(false);
                }

                // should call onBottomComplete function of DropDownListView at end of on bottom complete.
                listView.onBottomComplete();
            }*/

            super.onPostExecute(result);
        }
    }
	
	private void initRSSSourceData(){
		RSSSource.clear();
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_TAG, Context.MODE_PRIVATE);
		
		//检查是否有初始化过原始数据
		int num = sharedPreferences.getInt("num", -1);
		if(num < 0){
			Editor editor = sharedPreferences.edit();
			editor.putInt("num", RSSSource.RSS_TITLE_LIST.length);
			editor.putInt("index", 0);
			for(int i=0;i<RSSSource.RSS_TITLE_LIST.length;i++){
				String title = RSSSource.RSS_TITLE_LIST[i];
				editor.putString(""+i, title+" "+RSSSource.RSS_URL_DICT.get(title));
			}
			editor.commit();
			
			for(String s : RSSSource.RSS_TITLE_LIST){
				RSSSource.add(s);
			}
			RSSSource.extRssUrlDict = RSSSource.RSS_URL_DICT;
		}else{
			for(int i=0;i<num;i++){
				String data = sharedPreferences.getString(""+i, null);
				if(data == null){
					Log.d(TAG,"warning : sharedPreferences data is null,index:"+i);
				}else{
					String[] items = data.split(" ");
					if(items.length == 2){
						RSSSource.add(items[0].trim());
						RSSSource.extRssUrlDict.put(items[0].trim(), items[1].trim());
					}else{
						Log.d(TAG,"sharedPreferences data error:"+data);
					}
				}
			}
		}
		
		//读取当前的数据RSS索引位置
		curRssUrlIndex = sharedPreferences.getInt("index", 0);
		String indexData = sharedPreferences.getString(""+curRssUrlIndex, null);
		if(indexData == null){
			Log.d(TAG,"cur index data is null:"+curRssUrlIndex);
			return;
		}else{
			curRssUrl = indexData.split(" ")[0].trim();
		}
	}
	
	private String getTitleString(){
		return getResources().getString(R.string.app_name)+" — "+curRssUrl;
	}

}
