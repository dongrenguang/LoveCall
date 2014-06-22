package loveCall.broadcastReceiver;

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

import loveCall.activity.SystemReminderActivity;
import loveCall.helper.DBHelper;
import loveCall.model.CallRecord;
import loveCall.model.History;
import loveCall.model.SpecialPersonGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class SystemReminderReceiver extends BroadcastReceiver {
	private DBHelper dbHelper;
	private List<SpecialPersonGroup> contactGroupList;
	private ArrayList<History> history1Week;
	private ArrayList<History> history1Month;
	private ArrayList<History> history3Month;
	private ArrayList<History> history6Month;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		dbHelper = DBHelper.getInstance(context, 1);
//		history1Week=getHistory(getEarliestCal(0));
		history1Month=getHistory(getEarliestCal(1));//最近一个月的通话情况
//		history3Month=getHistory(getEarliestCal(3));
//		history6Month=getHistory(getEarliestCal(6));
		if(history1Month.size()<=0){
			return;
		}
		History history=history1Month.get(history1Month.size()-1);
		reminder(context, history.getName(), history.getNumber(),history.getDuration());
	}
	
	/*
	 * 
	private void reminder(Context context,String name,String number,int duration){
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));  
		PendingIntent pi=PendingIntent.getActivity(context, 0, intent, 0);
		Notification notify=new Notification();
//		notify.icon=R.drawable.icon;
		notify.tickerText="爱电话提醒";
		notify.when=System.currentTimeMillis();
		notify.defaults=Notification.DEFAULT_SOUND;
		notify.defaults=Notification.DEFAULT_ALL;
		notify.setLatestEventInfo(context, "最近跟"+name+"联系得有点少哦", "点击直接拨号", pi);
		NotificationManager notificationManager=(NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		Calendar calendar1 = Calendar.getInstance();
		int flag = (int) (calendar1.getTimeInMillis() / 1000);
		notificationManager.notify(flag,notify);
		
		Toast.makeText(context, "系统提醒:"+name+number+":"+duration,Toast.LENGTH_LONG).show();
	}
	*/
	private void reminder(Context context,String name,String number,int duration){
		Bundle bundle = new Bundle();
		bundle.putString("contactName", name);
		bundle.putString("contactPhone", number);
		Intent dialogIntent = new Intent(context, SystemReminderActivity.class);
		dialogIntent.putExtras(bundle);

		dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

		context.startActivity(dialogIntent);
	}
	private ArrayList<History> getHistory(Date earliestDate) {
		Map<String, History> historyByContactMap = new HashMap<String, History>();

		Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
				"select * from specialPerson", null);
		while (cursor.moveToNext()) {
			historyByContactMap.put(cursor.getString(1),
					new History(cursor.getString(1), cursor.getString(2), 0));
		}
		cursor.close();

		cursor = dbHelper.getReadableDatabase().rawQuery(
				"select * from callRecord", null);
		while (cursor.moveToNext()) {
			try {
				CallRecord record = new CallRecord(cursor.getString(0),
						cursor.getInt(1), cursor.getString(2),
						cursor.getString(3));
				if (record.getStart().after(earliestDate)) {
					String number = record.getNumber();
					Cursor cursor_s = dbHelper.getReadableDatabase().rawQuery(
							"select contactname from specialPerson where number = '"
									+ number + "'", null);
					cursor_s.moveToFirst();
					String contactname = cursor_s.getString(0);
					cursor_s.close();
					if (!historyByContactMap.containsKey(contactname)) {
						historyByContactMap.put(contactname, new History(
								contactname, number, 0));
					}
					History h = historyByContactMap.get(contactname);
					h.addDuration(record.getPeriodMinutes());
				}
			} catch (ParseException e) {
				Log.d("moveeeeeeeeeeeeee", "1");
			}
		}

		ArrayList<History> historyList = new ArrayList<History>();
		Iterator iter = historyByContactMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			History h = (History) entry.getValue();
			historyList.add(h);
			Log.d("history",h.getName() + "---" + h.getNumber() + "---"+ h.getDuration());
		}
		Collections.sort(historyList);
		return historyList;
	}

	private Date getEarliestCal(int d) {
		Calendar c = Calendar.getInstance();
		switch (d) {
		case 6:// 近6个月
			c.add(Calendar.MONTH, -6);
			break;
		case 3:// 近3个月
			c.add(Calendar.MONTH, -3);
			break;
		case 1:// 近1个月
			c.add(Calendar.MONTH, -1);
			break;
		case 0:// 近1周
			c.add(Calendar.WEEK_OF_YEAR, -1);
			break;
		default:
			break;
		}
		return c.getTime();
	}
}
