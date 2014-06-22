package loveCall.broadcastReceiver;

import java.util.Calendar;

import javax.security.auth.PrivateCredentialPermission;

import loveCall.activity.AddReminderActivity;
import loveCall.activity.ReminderActivity;
import loveCall.helper.DBHelper;
import android.R.integer;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

public class EveryMonthAlarmReceiver extends BroadcastReceiver {
		private String contactName;
		private String contactPhone;
		private String  note;
		private int flag;
		private int hour;
		private int minute;
		private int dayOfMonth;
		
		private DBHelper dbHelper;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		dbHelper = DBHelper.getInstance(context, 1);
		contactName = intent.getStringExtra("contactName");
		contactPhone = intent.getStringExtra("contactPhone");
		note = intent.getStringExtra("note");
		flag = intent.getIntExtra("flag", 0);
		
		hour=intent.getIntExtra("hour", 9);
		minute=intent.getIntExtra("minute", 0);
		dayOfMonth=intent.getIntExtra("dayOfMonth", 1);

//		Toast.makeText(context,
//				"打给" + contactName + contactPhone + note + " flag+" + flag,
//				Toast.LENGTH_LONG).show();

		
		Bundle bundle = new Bundle();
		bundle.putString("contactName", contactName);
		bundle.putString("contactPhone", contactPhone);
		bundle.putString("note", note);
		bundle.putInt("flag", flag);
		if (!exist(flag)) {// 该提醒已经从任务列表删除
			// 删除该alarm
			cancelAlarm(context, flag, contactName, contactPhone, note);
			return;
		}
		
		Intent dialogIntent = new Intent(context, ReminderActivity.class);
		dialogIntent.putExtras(bundle);

		dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

		context.startActivity(dialogIntent);
		alarmNextMonth(context, getDayOfNextMonth());
		
	}

	// 当本次提醒过之后要定好下一月的提醒，但是不必再存入数据库
	private void alarmNextMonth(Context context,Calendar cal) {
		Calendar calendar1 = Calendar.getInstance();
		//int flag = (int) (calendar1.getTimeInMillis() / 1000);// 该闹钟的唯一标志位，用于取消闹钟

		Intent intent = new Intent(context,EveryMonthAlarmReceiver.class);
		intent.putExtra("contactName", contactName);
		intent.putExtra("contactPhone", contactPhone);
		intent.putExtra("note", note);
		intent.putExtra("flag", flag);
		intent.setAction("ALARM_ACTION");
		PendingIntent sender = PendingIntent.getBroadcast(
				context, flag, intent, 0);

		AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
	}

	private Calendar getDayOfNextMonth() {
		Calendar cal = Calendar.getInstance();
		
		int totalDaysOfNextMonth = getTotalDaysOfNextMonth();
		if (totalDaysOfNextMonth < dayOfMonth) {// 下月天数小于所定的日期，比如2月（定的是每月30号）
			cal.add(Calendar.MONTH, 2);
		}else{
			cal.add(Calendar.MONTH, 1);
		}
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		
		return cal;
	}

	private int getTotalDaysOfNextMonth() {
		Calendar a = Calendar.getInstance();
		a.add(Calendar.MONTH, 1);
		a.set(Calendar.DATE, 1);// 把日期设置为下月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}
	
	//判断该提醒是不是还存在与数据库中
	private boolean exist(int flag) {
		String[] columns = new String[] { "flag" };
		Cursor cursor = dbHelper.getReadableDatabase().query("reminder",
				columns, null, null, null, null, null);
		while (cursor.moveToNext()) {
			if (cursor.getInt(0) == flag) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}

	// 取消闹铃
	private boolean cancelAlarm(Context context, int flag, String contactName,
			String contactPhone, String note) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("contactName", contactName);
		intent.putExtra("contactPhone", contactPhone);
		intent.putExtra("note", note);
		intent.putExtra("flag", flag);
		intent.setAction("ALARM_ACTION");
		PendingIntent sender = PendingIntent.getBroadcast(context, flag,
				intent, 0);
		AlarmManager am = (AlarmManager) context
				.getSystemService(context.ALARM_SERVICE);
		am.cancel(sender);
//		Toast.makeText(context, "已经取消闹铃" + contactName, Toast.LENGTH_LONG)
//				.show();
		return true;
	}

}
