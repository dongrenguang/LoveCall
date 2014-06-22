package loveCall.activity.time;

import loveCall.activity.AddReminderActivity;
import loveCall.activity.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class OnlyOnceActivity extends BaseActivity implements OnDateChangedListener,OnTimeChangedListener{
	private DatePicker datePicker;
	private TimePicker timePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_onlyonce);
		datePicker=(DatePicker)findViewById(R.id.datepicker);
		timePicker=(TimePicker)findViewById(R.id.timepicker);
		
		Bundle bundle=this.getIntent().getExtras();
		int y=bundle.getInt("year");
		int m=bundle.getInt("month");
		int d=bundle.getInt("day");
		int h=bundle.getInt("hour");
		int min=bundle.getInt("minute");
		datePicker.init(y, m, d, this);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(h);
		timePicker.setCurrentMinute(min);
		Log.v("hour_min", h+":"+min);
	}
	
	public void onCancelClick(View view){
		Intent intent = new Intent(OnlyOnceActivity.this, AddReminderActivity.class);
		setResult(0,intent); 
		finish();
	}
	
	public void onConfirmClick(View view){
		Intent intent = new Intent(OnlyOnceActivity.this, AddReminderActivity.class);
		Bundle bundle=new Bundle();
		bundle.putInt("year", datePicker.getYear());
		bundle.putInt("month", datePicker.getMonth());
		bundle.putInt("day",datePicker.getDayOfMonth());
		bundle.putInt("hour", timePicker.getCurrentHour());
		bundle.putInt("minute", timePicker.getCurrentMinute());
		Log.v("time", datePicker.getYear()+"/"+datePicker.getMonth()+"/"+datePicker.getDayOfMonth()+" "+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
		intent.putExtras(bundle);
		setResult(1,intent);
		finish();		
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		
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
