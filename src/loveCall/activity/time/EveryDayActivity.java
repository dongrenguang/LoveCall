package loveCall.activity.time;

import loveCall.activity.AddReminderActivity;
import loveCall.activity.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class EveryDayActivity extends BaseActivity implements OnTimeChangedListener{
	private TimePicker timePicker;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_everyday);
		timePicker=(TimePicker)findViewById(R.id.timePicker);
		
		Bundle bundle=this.getIntent().getExtras();
		int h=bundle.getInt("hour");
		int min=bundle.getInt("minute");
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(h);
		timePicker.setCurrentMinute(min);
	}
	
	public void onCancelClick(View view){
		Intent intent = new Intent(EveryDayActivity.this, AddReminderActivity.class);
		setResult(0,intent); 
		finish();
	}
	
	public void onConfirmClick(View view){
		Intent intent = new Intent(EveryDayActivity.this, AddReminderActivity.class);
		Bundle bundle=new Bundle();
		bundle.putInt("hour", timePicker.getCurrentHour());
		bundle.putInt("minute", timePicker.getCurrentMinute());
		Log.v("time", timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
		intent.putExtras(bundle);
		setResult(1,intent);
		finish();		
	}
	
	
	
	@Override
	public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		}
		public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		}
}
