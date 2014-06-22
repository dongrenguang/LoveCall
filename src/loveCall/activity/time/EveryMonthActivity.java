package loveCall.activity.time;

import loveCall.activity.AddReminderActivity;
import loveCall.activity.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class EveryMonthActivity extends BaseActivity{
	private TimePicker timePicker;
	private final Integer[] DAYS = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
			                   16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
	private Spinner spinner;
	private ArrayAdapter<Integer> adapter;
	private int day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_everymonth);

		spinner = (Spinner) findViewById(R.id.spinner1);
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item, DAYS);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		spinner.setAdapter(adapter);
		// 添加事件Spinner事件监听
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		// 设置默认值
		spinner.setVisibility(View.VISIBLE);
		
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		Bundle bundle = this.getIntent().getExtras();
		int h = bundle.getInt("hour");
		int min = bundle.getInt("minute");
		day = bundle.getInt("day");
		spinner.setSelection(day-1,true);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(h);
		timePicker.setCurrentMinute(min);
	}

	public void onCancelClick(View view) {
		Intent intent = new Intent(EveryMonthActivity.this,
				AddReminderActivity.class);
		setResult(0, intent);
		finish();
	}

	public void onConfirmClick(View view) {
		Intent intent = new Intent(EveryMonthActivity.this,
				AddReminderActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("hour", timePicker.getCurrentHour());
		bundle.putInt("minute", timePicker.getCurrentMinute());
		bundle.putInt("day", day);
		intent.putExtras(bundle);
		setResult(1, intent);
		finish();
	}

	class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			day = DAYS[arg2];
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
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
