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

public class EveryWeekActivity extends BaseActivity {
	private TimePicker timePicker;
	private final String[] WEEKS = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六" };
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private String week;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_everyweek);

		spinner = (Spinner) findViewById(R.id.spinner1);
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, WEEKS);
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
		week = bundle.getString("week");
		int index=getIndex(week);
		spinner.setSelection(index,true);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(h);
		timePicker.setCurrentMinute(min);
	}

	public void onCancelClick(View view) {
		Intent intent = new Intent(EveryWeekActivity.this,
				AddReminderActivity.class);
		setResult(0, intent);
		finish();
	}

	public void onConfirmClick(View view) {
		Intent intent = new Intent(EveryWeekActivity.this,
				AddReminderActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("hour", timePicker.getCurrentHour());
		bundle.putInt("minute", timePicker.getCurrentMinute());
		bundle.putString("week", week);
		intent.putExtras(bundle);
		setResult(1, intent);
		finish();
	}

	class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			week = WEEKS[arg2];
			// if(weekString.equals("星期日")){
			// week=0;
			// }else if(weekString.equals("星期一")){
			// week=1;
			// }else if(weekString.equals("星期二")){
			// week=2;
			// }else if(weekString.equals("星期三")){
			// week=3;
			// }else if(weekString.equals("星期四")){
			// week=4;
			// }else if(weekString.equals("星期五")){
			// week=5;
			// }else if(weekString.equals("星期六")){
			// week=6;
			// }
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}
	
	private int getIndex(String s){
		if(s.equals("星期一")){
			return 1;
		}else if(s.equals("星期二")){
			return 2;
		}else if(s.equals("星期三")){
			return 3;
		}else if(s.equals("星期四")){
			return 4;
		}else if(s.equals("星期五")){
			return 5;
		}else if(s.equals("星期六")){
			return 6;
		}else {
			return 0;
		}
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
