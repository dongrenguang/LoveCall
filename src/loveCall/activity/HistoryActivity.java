package loveCall.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import loveCall.helper.DBHelper;
import loveCall.helper.ScreenShot;
import loveCall.model.CallRecord;
import loveCall.model.HistoryByContact;
import loveCall.model.HistoryByGroup;
import loveCall.view.BarChartView;
import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class HistoryActivity extends Activity {
	//	private static final String APP_ID = "268226";
	//
	//	private static final String API_KEY = "a8db5eb5d27745418393bb3d8c1cdf3c";
	//
	//	private static final String SECRET_KEY = "9165d2dc170942eaae7e515512938b20";
	final int MAX_BAR_COUNT = 6;
	DBHelper dbHelper;
	Spinner time_spinner;
	Spinner show_type_spinner;
	//Button pick_span_btn;
	//	Button share_btn;
	Map<String , String> specialNumberMap;
	Map<String, HistoryByContact> historyByContactMap; //key:contactName
	Map<String, HistoryByGroup> historyByGroupMap; //key:groupName
	private double inData[];
	private double outData[];
	private List<String> options = new ArrayList<String>();
	private BarChartView view;
	LinearLayout layout;
	ScreenShot screenShot;
	//	RennClient rennClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_history);
		
		Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.background_color);
        this.getWindow().setBackgroundDrawable(drawable);

		dbHelper = DBHelper.getInstance(this,1);
		time_spinner = (Spinner) findViewById(R.id.time_span_spinner);
		show_type_spinner = (Spinner) findViewById(R.id.show_type_spinner);
		time_spinner.setOnItemSelectedListener(new MySpinnerListener());
		show_type_spinner.setOnItemSelectedListener(new MySpinnerListener());
		//pick_span_btn = (Button) findViewById(R.id.pick_time_span_btn);
		//		share_btn = (Button) findViewById(R.id.share_to_renren_btn);
		//pick_span_btn.setOnClickListener(new PickSpanListener());
		//		share_btn.setOnClickListener(new ShareListener());
		layout = (LinearLayout) findViewById(R.id.barview_content);
		//layout.setBackgroundColor(0xffffffff)
		//		specialNumberMap = new HashMap<String, String>();
		//		screenShot = new ScreenShot(this);
		time_spinner.setSelection(2);
		show_type_spinner.setSelection(0);
		init();

	}

	private void init() {
		Log.d("init_history	", "init_history");

		specialNumberMap = new HashMap<String, String>();
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select number from specialPerson", null);
		if(cursor.getCount()>0){
			while(cursor.moveToNext()){
				specialNumberMap.put(cursor.getString(0), null);
			}
			cursor.close();

			cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null); 
			while(cursor.moveToNext()){
				//号码                                                                                             
				String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));  
				if(number.length()!=0&&number.charAt(0)=='+'){
					number=number.substring(3);
				}

				Log.d("number", number);

				if(specialNumberMap.containsKey(number)){
					Log.d("number", "containsKey");
					int type = -1;
					switch(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE)))){
					case Calls.INCOMING_TYPE:                                                                        
						type = 0;                                                                                 
						break;                                                                                       
					case Calls.OUTGOING_TYPE:                                                                        
						type = 1;                                                                                 
						break;    
					default:
						break;
					}
					if(type == -1)
						continue;
					Date startDate = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE))));
					Log.d("start_date", startDate.toString());

					int duration = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DURATION)));  
					CallRecord h = new CallRecord(number, type, startDate, duration);


					Cursor cursor2 = dbHelper.getReadableDatabase().rawQuery
							("select * from callRecord where number ='"+number+"' and start = '"+h.getStartTimeString()+"'", null);
					if(cursor2.getCount() == 0){
						ContentValues values = new ContentValues();
						values.put("number",h.getNumber());
						values.put("type", h.getType());
						values.put("start", h.getStartTimeString());
						values.put("end", h.getEndTimeString());

						dbHelper.getWritableDatabase().insert("callRecord", null, values);
					}
					cursor2.close();
				}
			} 
			cursor.close();
		}
		if(show_type_spinner.getSelectedItemPosition() == 0){
			showTablesByContact();
		}else{
			showTablesByGroup();
		}
	}
	private void showTablesByContact(){

		historyByContactMap = new HashMap<String, HistoryByContact>();

		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from specialPerson", null);
		while(cursor.moveToNext()){
			historyByContactMap.put(cursor.getString(1), new HistoryByContact(cursor.getString(1)));
		}
		cursor.close();


		Date earliestDate = getEarliestCal(time_spinner.getSelectedItemPosition());
		cursor = dbHelper.getReadableDatabase().rawQuery("select * from callRecord", null);
		while(cursor.moveToNext()){
			try {
				CallRecord record = new CallRecord(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
				if(record.getStart().after(earliestDate)){

					String number = record.getNumber();
					Cursor cursor_s= dbHelper.getReadableDatabase().rawQuery("select contactname from specialPerson where number = '"+number+"'", null);
					cursor_s.moveToFirst();
					String contactname = cursor_s.getString(0);
					cursor_s.close();
					if(!historyByContactMap.containsKey(contactname)){
						historyByContactMap.put(contactname, new HistoryByContact(contactname));
					}
					HistoryByContact h = historyByContactMap.get(contactname);
					if(record.getType() == 0){
						h.addInMinutes(record.getPeriodMinutes());
					}else{
						h.addOutMinutes(record.getPeriodMinutes());
					}
				}
			} catch (ParseException e) {
				Toast.makeText(HistoryActivity.this,
						"exception:"+e.toString(), Toast.LENGTH_SHORT).show();
			}
		}

		int size = historyByContactMap.size();

		List<HistoryByContact> hList = new ArrayList<HistoryByContact>();
		double maxY = 0;
		if(size == 0){
			inData = new double[1];
			outData = new double[1];
			maxY = 1;
		}else{
			Iterator iter = historyByContactMap.entrySet().iterator();
			while(iter.hasNext()){
				Entry entry = (Entry) iter.next();
				//			String name = (String) entry.getKey();
				HistoryByContact h = (HistoryByContact) entry.getValue();
				hList.add(h);

			}

			Collections.sort(hList);
			while(hList.size()>MAX_BAR_COUNT){
				hList.remove(hList.size()-1);
			}
			inData = new double[hList.size()];
			outData = new double[hList.size()];
			for(int i = 0; i<hList.size(); i++){
				HistoryByContact h = hList.get(i);
				Log.d("history", h.getContactName()+"---"+h.getInMinutes()+"---"+h.getOutMinutes());
			}
			maxY = hList.get(0).getInMinutes();
			options = new ArrayList<String>();
			Log.d("size", options.size()+"");
			options.add("");
			for(int i = 0; i < hList.size(); i++){
				HistoryByContact h = hList.get(i);
				options.add(h.getContactName()); 
				inData[i] = h.getInMinutes();
				outData[i] = h.getOutMinutes();
				if(maxY < Math.max(h.getInMinutes(), h.getOutMinutes())){
					maxY =  Math.max(h.getInMinutes(), h.getOutMinutes());
				}
			}
		}
		layout.removeAllViews();
		view = new BarChartView(this);
		view.initData(inData, outData, options, "历史记录", maxY, hList.size());
		layout.addView(view.getBarChartView());

	}

	private void showTablesByGroup(){

		historyByGroupMap = new HashMap<String, HistoryByGroup>();

		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from specialPerson", null);
		while(cursor.moveToNext()){
			historyByGroupMap.put(cursor.getString(0), new HistoryByGroup(cursor.getString(0)));
		}
		cursor.close();


		Date earliestDate = getEarliestCal(time_spinner.getSelectedItemPosition());
		cursor = dbHelper.getReadableDatabase().rawQuery("select * from callRecord", null);
		while(cursor.moveToNext()){
			try {
				CallRecord record = new CallRecord(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
				if(record.getStart().after(earliestDate)){

					String number = record.getNumber();
					Cursor cursor_s= dbHelper.getReadableDatabase().rawQuery("select groupname from specialPerson where number = '"+number+"'", null);
					cursor_s.moveToFirst();
					String groupname = cursor_s.getString(0);
					cursor_s.close();
					if(!historyByGroupMap.containsKey(groupname)){
						historyByGroupMap.put(groupname, new HistoryByGroup(groupname));
					}
					HistoryByGroup h = historyByGroupMap.get(groupname);
					if(record.getType() == 0){
						h.addInMinutes(record.getPeriodMinutes());
						Log.d("addInMinute", ""+record.getPeriodMinutes());
					}else{
						h.addOutMinutes(record.getPeriodMinutes());
						Log.d("addOutMinute", ""+record.getPeriodMinutes());
					}
				}
			} catch (ParseException e) {
				Toast.makeText(HistoryActivity.this,
						"exception:"+e.toString(), Toast.LENGTH_SHORT).show();
			}
		}

		int size = historyByGroupMap.size();

		List<HistoryByGroup> hList = new ArrayList<HistoryByGroup>();
		double maxY = 0;
		if(size == 0){
			inData = new double[1];
			outData = new double[1];
			maxY = 1;
		}else{
			Iterator iter = historyByGroupMap.entrySet().iterator();
			while(iter.hasNext()){
				Entry entry = (Entry) iter.next();
				//			String name = (String) entry.getKey();
				HistoryByGroup h = (HistoryByGroup) entry.getValue();
				hList.add(h);

			}

			Collections.sort(hList);
			while(hList.size()>MAX_BAR_COUNT){
				hList.remove(hList.size()-1);
			}
			inData = new double[hList.size()];
			outData = new double[hList.size()];
			for(int i = 0; i<hList.size(); i++){
				HistoryByGroup h = hList.get(i);
				Log.d("history", h.getGroupName()+"---"+h.getInMinutes()+"---"+h.getOutMinutes());
			}
			maxY = hList.get(0).getInMinutes();
			options = new ArrayList<String>();
			Log.d("size", options.size()+"");
			options.add("");
			for(int i = 0; i < hList.size(); i++){
				HistoryByGroup h = hList.get(i);
				options.add(h.getGroupName()); 
				inData[i] = h.getInMinutes();
				outData[i] = h.getOutMinutes();
				if(maxY < Math.max(h.getInMinutes(), h.getOutMinutes())){
					maxY =  Math.max(h.getInMinutes(), h.getOutMinutes());
				}
			}
		}
		layout.removeAllViews();
		view = new BarChartView(this);
		view.initData(inData, outData, options, "历史记录", maxY, hList.size());
		layout.addView(view.getBarChartView());

	}

	private Date getEarliestCal(int selectedPos) {
		Calendar c = Calendar.getInstance();
		switch(selectedPos){
		case 0://近6个月
			c.add(Calendar.MONTH, -6);
			break;
		case 1://近3个月
			c.add(Calendar.MONTH, -3);
			break;
		case 2://近1个月
			c.add(Calendar.MONTH, -1);
			break;
		case 3://近1周
			c.add(Calendar.WEEK_OF_YEAR, -1);
			break;
		default:
			break;
		}
		return c.getTime();
	}

//	private class PickSpanListener implements OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			init();
//		}
//
//	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		time_spinner.setSelection(2);
		show_type_spinner.setSelection(0);
		MobclickAgent.onResume(this);
		init();
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	class MySpinnerListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			init();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
