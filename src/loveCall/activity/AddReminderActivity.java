package loveCall.activity;

import java.awt.font.NumericShaper;
import java.util.*;

import loveCall.activity.time.*;
import loveCall.broadcastReceiver.*;
import loveCall.helper.*;
import android.R.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.widget.*;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.AdapterView.*;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class AddReminderActivity extends BaseActivity {
	private DBHelper dbHelper;
	private Calendar calendar = Calendar.getInstance();// onlyonce ,everyday
	private String dayOfWeek = "";// everyweek
	private int dayOfMonth = 1;// everymonth
	private EditText nameEditText;
	private EditText numberEditText;
	private EditText noteEditeText;
	private EditText showtimeTextView;
	//private TextView showContactsTextView;
	private String contactName = null;
	private String contactPhone = null;
	private String note = "";
	private int year,month,day,hour,minute;
	private AlertDialog dialog;
	private Context context;

	private final int CONTACTLIST = 0;
	private final int ONLY_ONCE = 1;
	private final int EVERY_DAY = 2;
	private final int EVERY_WEEK = 3;
	private final int EVERY_MONTH = 4;
	private final int CONTACT_SPECIALPERSON = 5;
	private int frequency;

	private int contactFrom;
	private final int FROM_CONTACTLIST = 1;
	private final int FROM_INPUT = 2;
	private final int FROM_SPECIAL = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_reminder);
		showtimeTextView = (EditText) findViewById(R.id.add_showtime);
		showtimeTextView.setEnabled(false);
		nameEditText = (EditText) findViewById(R.id.add_name);
		numberEditText = (EditText) findViewById(R.id.add_num);
		noteEditeText = (EditText) findViewById(R.id.add_note);
		//		showContactsTextView = (TextView) findViewById(R.id.show_contacts);
		//
		//		showContactsTextView.setVisibility(View.INVISIBLE);
		//		nameEditText.setVisibility(View.GONE);
		//		numberEditText.setVisibility(View.GONE);

		calendar.add(Calendar.MINUTE, 1);

		dbHelper = DBHelper.getInstance(this, 1);
		contactFrom = FROM_INPUT;

		// time=new Time("GMT+8");
		// time.setToNow();
		context=this;
	}

	public void selectContact(final View view){
		dialog = new AlertDialog.Builder(this) 
		.setItems(R.array.add_contact_labels, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg2) {
				if(arg2==0)
					onContactListClick(view);
				if(arg2==1)
					onSpecialAttentionClick(view);
				dialog.cancel();
			}
		})
		.setTitle("选择添加方式").setNegativeButton("取消", new DialogInterface.OnClickListener() { 		  
			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				dialog.cancel();  
			}  
		}).create();  
		dialog.show(); 
	}

	public void selectTime(final View view){  
		dialog = new AlertDialog.Builder(this) 
		.setItems(R.array.add_time_labels, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg2) {
				if(arg2==0)
					onOnlyOnceClick(view);
				if(arg2==1)
					onEveryDayClick(view);
				if(arg2==2)
					onEveryWeekClick(view);
				if(arg2==3)
					onEveryMonthClick(view);
				dialog.cancel();
			}
		})
		.setTitle("选择添加方式").setNegativeButton("取消", new DialogInterface.OnClickListener() { 		  
			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				dialog.cancel();  
			}  
		}).create();  
		dialog.show(); 
	}
	
	public void clear(View view){
		nameEditText.setText("");
		numberEditText.setText("");
	}

	public void onContactListClick(View view) {
		contactFrom = FROM_CONTACTLIST;
		contactName = null;
		contactPhone = null;
		//		nameEditText.setVisibility(View.GONE);
		//		numberEditText.setVisibility(View.GONE);
		//		showContactsTextView.setVisibility(View.VISIBLE);

		//		Intent intent = new Intent(Intent.ACTION_PICK,
		//				ContactsContract.Contacts.CONTENT_URI);
		Intent intent = new Intent(AddReminderActivity.this, ContactListActivity.class);
		intent.putExtra("type", 1);
		this.startActivityForResult(intent, CONTACTLIST);
		noteEditeText.setFocusable(true);
	}

	public void onInputContactsClick(View view) {
		contactFrom = FROM_INPUT;
		contactName = null;
		contactPhone = null;
		//		nameEditText.setVisibility(View.VISIBLE);
		//		numberEditText.setVisibility(View.VISIBLE);

		//		showContactsTextView.setVisibility(View.GONE);
		nameEditText.setText("");
		numberEditText.setText("");
		nameEditText.setFocusable(true);
	}

	public void onSpecialAttentionClick(View view) {
		contactFrom = FROM_SPECIAL;
		contactName = null;
		contactPhone = null;
		//		nameEditText.setVisibility(View.GONE);
		//		numberEditText.setVisibility(View.GONE);
		//		showContactsTextView.setVisibility(View.VISIBLE);

		Intent intent = new Intent(AddReminderActivity.this,SpecialPersonActivity.class);
		intent.putExtra("addReminder", true);
		startActivityForResult(intent, CONTACT_SPECIALPERSON);
		noteEditeText.setFocusable(true);
	}

	public void onOnlyOnceClick(View view) {
		frequency = ONLY_ONCE;

		final DatePicker datePicker=new DatePicker(this);
		datePicker.init(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),null);

		final AlertDialog dialog = new AlertDialog.Builder(this)  
		.setTitle("选择日期").setView(datePicker)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				year=datePicker.getYear();
				month=datePicker.getMonth();
				day=datePicker.getDayOfMonth();
				dialog.cancel();

				final TimePicker timePicker=new TimePicker(context);
				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

				final AlertDialog dialog1 = new AlertDialog.Builder(context)  
				.setTitle("选择时间").setView(timePicker)
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog1, int which) {
						hour=timePicker.getCurrentHour();
						minute=timePicker.getCurrentMinute();
						dialog1.cancel();

						Intent intent = new Intent(context, AddReminderActivity.class);
						Bundle bundle=new Bundle();
						bundle.putInt("year", year);
						bundle.putInt("month",month);
						bundle.putInt("day",day);
						bundle.putInt("hour", hour);
						bundle.putInt("minute", minute);
						Log.v("time", datePicker.getYear()+"/"+datePicker.getMonth()+"/"+
								datePicker.getDayOfMonth()+" "+timePicker.getCurrentHour()+":"
								+timePicker.getCurrentMinute());
						intent.putExtras(bundle);
						onActivityResult(ONLY_ONCE, 1, intent);
					}
				})
				.setNegativeButton("取消",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog1, int which) {
						dialog1.cancel();
					}
				}).create();
				dialog1.show();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() { 		  
			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				dialog.cancel();  
			}  
		}).create();  
		dialog.show(); 		
	}

	public void onEveryDayClick(View view) {
		frequency = EVERY_DAY;

		final TimePicker timePicker=new TimePicker(context);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		
		final AlertDialog dialog1 = new AlertDialog.Builder(context)  
		.setTitle("选择时间").setView(timePicker)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog1, int which) {
				hour=timePicker.getCurrentHour();
				minute=timePicker.getCurrentMinute();
				dialog1.cancel();

				Intent intent = new Intent(context, AddReminderActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt("hour", hour);
				bundle.putInt("minute", minute);
				Log.v("time", timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
				intent.putExtras(bundle);
				onActivityResult(EVERY_DAY, 1, intent);
			}
		})
		.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog1, int which) {
				dialog1.cancel();
			}
		}).create();
		dialog1.show();	
	}

	public void onEveryWeekClick(View view) {
		frequency = EVERY_WEEK;
		
		RelativeLayout re=new RelativeLayout(context);
		final Spinner spinner=new Spinner(this);
		String[] WEEKS = { "星期一", "星期二", "星期三", "星期四", "星期五",
		"星期六", "星期日", };
		ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, WEEKS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		re.addView(spinner,new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		
		final TimePicker timePicker=new TimePicker(context);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		re.addView(timePicker,new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		
		final AlertDialog alertDialog=new AlertDialog.Builder(context)
		.setTitle("选择时间").setView(re)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface alertDialog, int which) {
				hour=timePicker.getCurrentHour();
				minute=timePicker.getCurrentMinute();
				String week=(String) spinner.getSelectedItem();
				alertDialog.cancel();

				Intent intent = new Intent(context, AddReminderActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt("hour", hour);
				bundle.putInt("minute", minute);
				bundle.putString("week", week);
				Log.v("time", timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
				intent.putExtras(bundle);
				onActivityResult(EVERY_WEEK, 1, intent);
			}
		})
		.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface alertDialog, int which) {
				alertDialog.cancel();
			}
		}).create();
		alertDialog.show();	
	}

	public void onEveryMonthClick(View view) {
		frequency = EVERY_MONTH;
		
		String[] DAYS = new String[30];
		for(int i=0;i<30;i++)
			DAYS[i]=i+1+"号";
		RelativeLayout re=new RelativeLayout(context);
		final Spinner spinner=new Spinner(this);
		ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, DAYS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		re.addView(spinner,new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		
		final TimePicker timePicker=new TimePicker(context);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		re.addView(timePicker,new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		
		final AlertDialog alertDialog=new AlertDialog.Builder(context)
		.setTitle("选择时间").setView(re)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface alertDialog, int which) {
				hour=timePicker.getCurrentHour();
				minute=timePicker.getCurrentMinute();
				day=spinner.getSelectedItemPosition()+1;
				alertDialog.cancel();

				Intent intent = new Intent(context, AddReminderActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt("hour", hour);
				bundle.putInt("minute", minute);
				bundle.putInt("day", day);
				Log.v("time", timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
				intent.putExtras(bundle);
				onActivityResult(EVERY_MONTH, 1, intent);
			}
		})
		.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface alertDialog, int which) {
				alertDialog.cancel();
			}
		}).create();
		alertDialog.show();	
	}

	public void onSaveClick(View view) {
		note = noteEditeText.getText().toString().trim();
		// 判断联系人姓名和号码
		if (contactFrom == FROM_INPUT) {
			contactName = nameEditText.getText().toString().trim();
			contactPhone = numberEditText.getText().toString().trim();
			if (contactPhone == null || contactPhone.equals("")) {
				Toast.makeText(this, "联系人号码不能为空！", Toast.LENGTH_LONG).show();
				numberEditText.setFocusable(true);
				return;
			}
		} else if (contactFrom == FROM_CONTACTLIST) {
			if (contactPhone == null || contactPhone.equals("")) {
				Toast.makeText(this, "联系人号码不能为空！", Toast.LENGTH_LONG).show();
				return;
			}
			if (contactName == null) {
				contactName = "";
			}
		} else if (contactFrom == FROM_SPECIAL) {
			if (contactPhone == null || contactPhone.equals("")) {
				Toast.makeText(this, "联系人号码不能为空！", Toast.LENGTH_LONG).show();
				return;
			}
			if (contactName == null) {
				contactName = "";
			}
		} else {
			Toast.makeText(this, "请先设置联系人！", Toast.LENGTH_LONG).show();
			return;
		}
		Log.v("contactName:phone", contactName + ":" + contactPhone);

		int flag;
		switch (frequency) {
		case ONLY_ONCE:
			flag = alarmOnlyOnce(calendar,false);
			break;
		case EVERY_DAY:
			flag = alarmRepeat(
					getNearestDay(calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE)), 24 * 60 * 60 * 1000);
			break;
		case EVERY_WEEK:
			flag = alarmRepeat(
					getNearestDayOfWeek(calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							getWeekByInt(dayOfWeek)), 7 * 24 * 60 * 60 * 1000);
			break;
		case EVERY_MONTH:
			flag = alarmOnlyOnce(getNearestDayOfMonth(
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), dayOfMonth),true);
			break;
		default:
			Toast.makeText(this, "请先设置时间！", Toast.LENGTH_LONG).show();
			return;
		}

		// 将此闹钟信息存入数据库
		saveReminder(flag);
		clear();
		//		Intent intent= new Intent(AddReminderActivity.this,
		//				TaskListActivity.class);
		//		Bundle bundle = new Bundle();
		//		bundle.putInt("update", 1);
		//		intent.putExtras(bundle);
		//		startActivityForResult(intent, UPDATETASKLIST);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CONTACTLIST||requestCode == CONTACT_SPECIALPERSON) {// onContactListClick()
			switch (resultCode) {
			case RESULT_OK:
				contactName = data.getStringExtra("contactName");
				contactPhone = data.getStringExtra("number");

				nameEditText.setText(contactName);
				numberEditText.setText(contactPhone);
				//				showContactsTextView.setText(contactName + "  " + contactPhone);
				break;

			default:
				Log.d("contactnumber2", "default");
				break;
			}
		} else if (requestCode == ONLY_ONCE) {// onAddTimeClick()
			switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
			case 0:
				break;
			case 1:
				Bundle b = data.getExtras(); // data为B中回传的Intent
				int year = b.getInt("year");
				int month = b.getInt("month");
				int day = b.getInt("day");
				int hourOfDay = b.getInt("hour");
				int minute = b.getInt("minute");
				String minuteString = "" + minute;
				if (minute < 10)
					minuteString = "0" + minute;
				// time.set(0, minute, hour, day, month, year);
				calendar.set(year, month, day, hourOfDay, minute);
				showtimeTextView.setText(year + "年" + (month + 1) + "月" + day
						+ "日  " + hourOfDay + ":" + minuteString);
				break;
			default:
				break;
			}

		} else if (requestCode == EVERY_DAY) {// onEveryDayClick(View view)
			switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
			case 0:
				break;
			case 1:
				Bundle b = data.getExtras(); // data为B中回传的Intent
				int hour = b.getInt("hour");
				int minute = b.getInt("minute");
				String minuteString = "" + minute;
				if (minute < 10)
					minuteString = "0" + minute;
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				// time.set(0, minute, hour, time.monthDay, time.month,
				// time.year);
				showtimeTextView.setText("每天 " + hour + ":" + minuteString);
				break;
			default:
				break;
			}

		} else if (requestCode == EVERY_WEEK) {// onEveryWeekClick(View view)
			switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
			case 0:
				break;
			case 1:
				Bundle b = data.getExtras(); // data为B中回传的Intent
				int hour = b.getInt("hour");
				int minute = b.getInt("minute");
				dayOfWeek = b.getString("week");
				String minuteString = "" + minute;
				if (minute < 10)
					minuteString = "0" + minute;
				calendar.set(Calendar.HOUR_OF_DAY, hour);// ?
				calendar.set(Calendar.MINUTE, minute);
				// time.set(0, minute, hour, time.monthDay, time.month,
				// time.year);
				showtimeTextView.setText("每周" + dayOfWeek + "  " + hour + ":"
						+ minuteString);
				break;
			default:
				break;
			}

		}
		else if (requestCode == EVERY_MONTH) {
			switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
			case 0:
				break;
			case 1:
				Bundle b = data.getExtras(); // data为B中回传的Intent
				int hour = b.getInt("hour");
				int minute = b.getInt("minute");
				dayOfMonth = b.getInt("day");
				String minuteString = "" + minute;
				if (minute < 10)
					minuteString = "0" + minute;
				calendar.set(Calendar.HOUR_OF_DAY, hour);// ?
				calendar.set(Calendar.MINUTE, minute);
				// time.set(0, minute, hour, time.monthDay, time.month,
				// time.year);
				showtimeTextView.setText("每月" + dayOfMonth + "日  " + hour + ":"
						+ minuteString);
				break;
			default:
				break;
			}

		}else {

		}
	}

	private int getWeekByInt(String w) {
		if (w.equals("星期日")) {
			return 1;
		} else if (w.equals("星期一")) {
			return 2;
		} else if (w.equals("星期二")) {
			return 3;
		} else if (w.equals("星期三")) {
			return 4;
		} else if (w.equals("星期四")) {
			return 5;
		} else if (w.equals("星期五")) {
			return 6;
		} else if (w.equals("星期六")) {
			return 7;
		} else {
			Log.v("weekTransforError", "Error");
			return -1;
		}
	}

	// 单次闹铃注册
	private int alarmOnlyOnce(Calendar cal,boolean isEveryMonth) {
		Calendar calendar1 = Calendar.getInstance();
		int flag = (int) (calendar1.getTimeInMillis() / 1000);// 该闹钟的唯一标志位，用于取消闹钟

		Intent intent;
		if(isEveryMonth){
			intent= new Intent(AddReminderActivity.this,
					EveryMonthAlarmReceiver.class);
		}else{
			intent= new Intent(AddReminderActivity.this,
					AlarmReceiver.class);
		}
		intent.putExtra("contactName", contactName);
		intent.putExtra("contactPhone", contactPhone);
		intent.putExtra("note", note);
		intent.putExtra("flag", flag);
		//intent.putExtra("everymonth", (frequency==EVERY_MONTH)?1:0);
		intent.setAction("ALARM_ACTION");
		PendingIntent sender = PendingIntent.getBroadcast(
				AddReminderActivity.this, flag, intent, 0);

		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		String tipString = cal.get(Calendar.YEAR) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + " "
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE);
		Toast.makeText(this, "设置成功！下一次将在 " + tipString + " 提醒您",
				Toast.LENGTH_LONG).show();
		return flag;
	}

	//重复闹铃
	private int alarmRepeat(Calendar cal, int cycle) {
		Calendar calendar1 = Calendar.getInstance();
		int flag = (int) (calendar1.getTimeInMillis() / 1000);

		Intent intent = new Intent(AddReminderActivity.this,
				AlarmReceiver.class);
		intent.putExtra("contactName", contactName);
		intent.putExtra("contactPhone", contactPhone);
		intent.putExtra("note", note);
		intent.putExtra("flag", flag);
		intent.setAction("ALARM_ACTION");
		PendingIntent sender = PendingIntent.getBroadcast(
				AddReminderActivity.this, flag, intent, 0);
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				cycle, sender);
		String tipString = cal.get(Calendar.YEAR) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + " "
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE);
		Toast.makeText(this, "设置成功！下一次将在 " + tipString + " 提醒您",
				Toast.LENGTH_LONG).show();
		return flag;
	}

	private Calendar getNearestDay(int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		int nowHour = cal.get(Calendar.HOUR_OF_DAY);
		int nowMinnute = cal.get(Calendar.MINUTE);
		if (hour > nowHour || (hour == nowHour && minute > nowMinnute)) {// 今天
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
		} else {// 明天
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
		}
		// Log.v("getNearestDay",
		// cal.get(Calendar.YEAR)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
		String tipString = cal.get(Calendar.YEAR) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + " "
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE);
		Toast.makeText(this, "设置成功！下一次将在 " + tipString + " 提醒您",
				Toast.LENGTH_LONG).show();
		return cal;
	}

	private Calendar getNearestDayOfWeek(int hour, int minute, int dayOfWeek) {
		Calendar cal = Calendar.getInstance();
		int nowDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);// 周日为1，周六为7
		int nowHour = cal.get(Calendar.HOUR_OF_DAY);
		int nowMinnute = cal.get(Calendar.MINUTE);
		if (dayOfWeek > nowDayOfWeek
				|| (dayOfWeek == nowDayOfWeek && (hour > nowHour || (hour == nowHour && minute > nowMinnute)))) {// 本周（现在之后某时）开始
			cal.add(Calendar.DAY_OF_MONTH, dayOfWeek - nowDayOfWeek);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
		} else {// 下周开始
			cal.add(Calendar.DAY_OF_MONTH, 7 - nowDayOfWeek + dayOfWeek);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
		}
		String tipString = cal.get(Calendar.YEAR) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + " "
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE);
		Toast.makeText(this, "设置成功！下一次将在 " + tipString + " 提醒您",
				Toast.LENGTH_LONG).show();
		return cal;
	}

	private Calendar getNearestDayOfMonth(int hour, int minute, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		int totalDaysOfCurrentMonth = getTotalDaysOfCurrentMonth();
		if (totalDaysOfCurrentMonth < dayOfMonth) {// 当月天数小于所定的日期，比如2月（定的是每月30号）
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			String tipString = cal.get(Calendar.YEAR) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.DAY_OF_MONTH) + " "
					+ cal.get(Calendar.HOUR_OF_DAY) + ":"
					+ cal.get(Calendar.MINUTE);
			Toast.makeText(this, "设置成功！下一次将在 " + tipString + " 提醒您",
					Toast.LENGTH_LONG).show();
			return cal;
		}

		int nowDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);// 1号为1
		int nowHour = cal.get(Calendar.HOUR_OF_DAY);
		int nowMinnute = cal.get(Calendar.MINUTE);
		if (dayOfMonth > nowDayOfMonth
				|| (dayOfMonth == nowDayOfMonth && (hour > nowHour || (hour == nowHour && minute > nowMinnute)))) {// 本月开始
			cal.add(Calendar.DAY_OF_MONTH, dayOfMonth - nowDayOfMonth);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
		} else {// 下月开始
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
		}

		String tipString = cal.get(Calendar.YEAR) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + " "
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE);
		Toast.makeText(this, "设置成功！下一次将在 " + tipString + " 提醒您",
				Toast.LENGTH_LONG).show();
		return cal;
	}

	private int getTotalDaysOfCurrentMonth() {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	private void saveReminder(int flag) {
		try {
			ContentValues values = new ContentValues();
			values.put("flag", flag);
			values.put("contactname", contactName);
			values.put("contactphone", contactPhone);
			values.put("note", note);
			values.put("details", showtimeTextView.getText().toString());
			dbHelper.getWritableDatabase().insert("reminder", null, values);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(AddReminderActivity.this, "保存提醒数据失败！",
					Toast.LENGTH_SHORT).show();
		}
	}

	//点击保存按钮后清空屏幕
	private void clear(){
		calendar = Calendar.getInstance();
		dayOfWeek = "";
		dayOfMonth = 1;
		nameEditText.setText("");
		numberEditText.setText("");
		noteEditeText.setText("");;
		showtimeTextView.setText("");
		//		showContactsTextView.setText("");
		contactName = null;
		contactPhone = null;
		note = "";
		frequency=0;
		contactFrom=0;

		//		showContactsTextView.setVisibility(View.INVISIBLE);
		//		nameEditText.setVisibility(View.GONE);
		//		numberEditText.setVisibility(View.GONE);

		calendar.add(Calendar.MINUTE, 1);
		contactFrom = FROM_INPUT;
	}
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
}
