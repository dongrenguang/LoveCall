package loveCall.activity;


import java.util.Calendar;

import loveCall.broadcastReceiver.ReadHistoryAlarmReceiver;
import loveCall.broadcastReceiver.SystemReminderReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends TabActivity {
	AlarmManager readHistoryAlarmManager;
	TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		//		setContentView(R.layout.activity_main);
		tabHost = getTabHost();
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		//		LayoutInflater.from(this).inflate(R.layout.main,tabHost.getTabContentView(), true);

        
		TabView tabView1 = new TabView(this,R.drawable.tab_tasklist_normal,"任务清单");
		tabHost.addTab(tabHost
				.newTabSpec("tab1")
				//.setIndicator(tabView1)
				.setIndicator(tabView1)
/*				.setIndicator("任务清单",
						getResources().getDrawable(R.drawable.tasklist))*/
						.setContent(new Intent(this, TaskListActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		
		TabView tabView2 = new TabView(this,R.drawable.tab_addreminder_normal,"新增提醒");
		tabHost.addTab(tabHost
				.newTabSpec("tab2")
				.setIndicator(tabView2)
				/*setIndicator("新增提醒",
						getResources().getDrawable(R.drawable.add))*/
						.setContent(new Intent(this, AddReminderActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		TabView tabView3 = new TabView(this,R.drawable.tab_specialones_normal,"特别关注");
		tabHost.addTab(tabHost
				.newTabSpec("tab3")
				.setIndicator(tabView3)
				/*setIndicator("特别关注",
						getResources().getDrawable(R.drawable.specialperson))*/
						.setContent(new Intent(this, SpecialPersonActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		TabView tabView4 = new TabView(this,R.drawable.tab_history_normal,"历史记录");
		tabHost.addTab(tabHost
				.newTabSpec("tab4")
				.setIndicator(tabView4)
/*				.setIndicator("历史记录",
						getResources().getDrawable(R.drawable.history))*/
						.setContent(new Intent(this, HistoryActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(new OnTabChangedListener());
		initFirstTab();

		readHistoryAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, ReadHistoryAlarmReceiver.class);
		final PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
		readHistoryAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 24*3600*1000, pi); 

		systemRemind();

	}


	//	public void onAddClick(View view) {
	//		startActivity(new Intent(MainActivity.this, AddReminderActivity.class));
	//
	//	}
	//
	//	public void onHistoryClick(View view) {
	//		startActivity(new Intent(MainActivity.this, HistoryActivity.class));
	//	}
	//
	//	public void onSpecialClick(View view) {
	//		startActivity(new Intent(MainActivity.this, SpecialPersonActivity.class));
	//	}
	//
	//	public void onTaskListClick(View view) {
	//		startActivity(new Intent(MainActivity.this, TaskListActivity.class));
	//	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem mi){
		if(mi.isCheckable()){
			mi.setChecked(true);
		}
		switch(mi.getItemId()){
		case R.id.action_item_help:
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			break;
		case R.id.action_item_update:
			Toast.makeText(this,
					"手动检测更新", Toast.LENGTH_SHORT).show();
			UmengUpdateAgent.forceUpdate(this);
			break;
		}
		return true;
	}

	
	//后台根据最近通话情况，自动提醒
		private void systemRemind(){
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 7);
			
			Intent intent = new Intent(MainActivity.this,SystemReminderReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(
			MainActivity.this, 1000000, intent, 0);
			AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
			manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					7*24*60*60*1000, sender);
		}


	private class TabView extends LinearLayout{
		private ImageView imageView ;  
        private TextView textView;  
          
        public TabView(Context c, int icon, String text) {  
            super(c);  
            
            setOrientation(VERTICAL);  
            setGravity(Gravity.CENTER);  
            setBackgroundColor(getResources().getColor(R.color.background_gray));
              
            imageView = new ImageView(c); 
            imageView.setTag("tab_image");
            imageView.setScaleType(ScaleType.CENTER_INSIDE);
            imageView.setImageDrawable(this.getResources().getDrawable(icon));  
            imageView.setBackgroundColor(Color.TRANSPARENT);  
            addView(imageView);  
              
            textView = new TextView(c); 
            textView.setTag("tab_text");
            textView.setText(text);  
            textView.setTextSize(14);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);  
            
            addView(textView);  
        
        }  
       
	}
	
	private void initFirstTab() {
		tabHost.setCurrentTabByTag("tab1");
		
		TabView view = (TabView)tabHost.getCurrentTabView();
		view.setBackgroundColor(getResources().getColor(R.color.pink));
		view.imageView.setBackgroundColor(Color.TRANSPARENT);
		view.textView.setTextColor(Color.WHITE);

		view.imageView.setImageDrawable(getResources().getDrawable(R.drawable.tab_tasklist_highlight));
	}
	
	
	private class OnTabChangedListener implements OnTabChangeListener{

		@Override
		public void onTabChanged(String tabID) {
			
			
			//unselected
			for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
				
		        tabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.background_gray));
		        ImageView iv = (ImageView)tabHost.getTabWidget().getChildAt(i).findViewWithTag("tab_image");
		        switch(i){
		        case 0:
		        	iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_tasklist_normal));
		        	break;
		        case 1:
		        	iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_addreminder_normal));
		        	break;
		        case 2:
		        	iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_specialones_normal));
		        	break;
		        case 3:
		        	iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_history_normal));
		        	break;
		        default:
		        	break;	        	
		        }
		        TextView tv = (TextView)tabHost.getTabWidget().getChildAt(i).findViewWithTag("tab_text");
		        tv.setTextColor(getResources().getColor(R.color.black));
		        
		    }
		    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.pink));
		    ImageView i = (ImageView)tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).findViewWithTag("tab_image");
		    switch(tabHost.getCurrentTab()){
		    case 0:
	        	i.setImageDrawable(getResources().getDrawable(R.drawable.tab_tasklist_highlight));
	        	break;
	        case 1:
	        	i.setImageDrawable(getResources().getDrawable(R.drawable.tab_addreminder_highlight));
	        	break;
	        case 2:
	        	i.setImageDrawable(getResources().getDrawable(R.drawable.tab_specialones_highlight));
	        	break;
	        case 3:
	        	i.setImageDrawable(getResources().getDrawable(R.drawable.tab_history_highlight));
	        	break;
	        default:
	        	break;	   
		    }
		    TextView t = (TextView)tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).findViewWithTag("tab_text");
		    t.setTextColor(getResources().getColor(R.color.white));
			
			tabHost.setCurrentTabByTag(tabID);

			
		}

		
		
	}

}
