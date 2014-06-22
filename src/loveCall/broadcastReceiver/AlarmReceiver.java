package loveCall.broadcastReceiver;

import java.util.HashMap;

import loveCall.activity.ReminderActivity;
import loveCall.helper.DBHelper;
import loveCall.model.SpecialPersonGroup;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	private DBHelper dbHelper;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		dbHelper = DBHelper.getInstance(context, 1);
		String contactName = intent.getStringExtra("contactName");
		String contactPhone = intent.getStringExtra("contactPhone");
		String note = intent.getStringExtra("note");
		int flag = intent.getIntExtra("flag", 0);

		// Toast.makeText(context,
		// "打给"+contactName+contactPhone+note+" flag+"+flag,
		// Toast.LENGTH_LONG).show();

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
		return true;
	}

}
