package loveCall.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loveCall.helper.DBHelper;
import loveCall.model.Reminder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class TaskListActivity extends BaseActivity implements
		OnItemSelectedListener, OnItemClickListener {
	private DBHelper dbHelper;
	private GridView gridView;
	ArrayList<Reminder> reminderList = new ArrayList<Reminder>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasklist);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setVerticalSpacing(5);
		gridView.setHorizontalSpacing(5);

		dbHelper = DBHelper.getInstance(this, 1);
		init();
		setColor();
		gridView.setOnItemSelectedListener(this);
		gridView.setOnItemClickListener(this);
		
		Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.background_color);
        this.getWindow().setBackgroundDrawable(drawable);

		// update();

		// Bundle bundle=this.getIntent().getExtras();
		// int update=0;
		// try {
		// update=bundle.getInt("update");
		// if(update==1){
		// Intent intent = new Intent(TaskListActivity.this,
		// AddReminderActivity.class);
		// setResult(0,intent);
		// //finish();
		// }
		// } catch (Exception e) {
		// // TODO: handle exception
		// Log.v("update", "failed");
		// }

	}

	// public void update(){
	// // setContentView(R.layout.activity_tasklist);
	// dbHelper = DBHelper.getInstance(this, 1);
	// init();
	// setColor();
	// gridView.setOnItemSelectedListener(this);
	// gridView.setOnItemClickListener(this);
	// }

	private void init() {
		reminderList.clear();
		if(GetReminders()==true){
			List<Map<String, Object>> cells = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < reminderList.size(); i++) {
				Reminder reminder = reminderList.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("NAME_PHONE",
						reminder.getContactName());
	/*			map.put("NAME_PHONE",
						reminder.getContactName() + "["
								+ reminder.getContactPhone() + "]");*/
				map.put("NOTE", reminder.getNote());
				map.put("DETAILS", reminder.getDetails());
				// map.put("COLOR", Color.BLUE);
				cells.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(this, cells,
					R.layout.activity_tasklist_cell, new String[] { "NAME_PHONE",
							"NOTE", "DETAILS" }, new int[] { R.id.task_cell_name,
							R.id.task_cell_comment, R.id.task_cell_time });
			gridView.setAdapter(adapter);
		}
		else{
			this.setContentView(R.layout.activity_tasklist_empty);
		}
		
		
	}

	private boolean GetReminders() {
		String[] columns = null;
		columns = new String[] { "flag", "contactname", "contactphone", "note",
				"details" };
		Cursor cursor = dbHelper.getReadableDatabase().query("reminder",
				columns, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Reminder reminder = new Reminder(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2),
					cursor.getString(3), cursor.getString(4));
			reminderList.add(reminder);
		}
		cursor.close();
		try {

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(reminderList.isEmpty())
			return false;
		else
			return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// view.setBackgroundColor(Color.RED);
		new AlertDialog.Builder(TaskListActivity.this).setPositiveButton("删除任务",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						new AlertDialog.Builder(TaskListActivity.this)
								.setTitle("确定要删除该任务？")
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface arg0,
													int arg1) {
												// TODO Auto-generated method
												// stub
												deleteReminder(position);
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
											}
										}).show();

						//
					}
				}).show();

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	private void deleteReminder(int position) {
		String[] args = new String[1];
		Reminder reminder = reminderList.get(position);
		args[0] = reminder.getFlag() + "";

		dbHelper.getWritableDatabase().delete("reminder", "flag=?", args);
		init();
		Toast.makeText(this, "已删除该任务", Toast.LENGTH_LONG).show();

	}

	private void setColor() {
		// gridView[0, 0].Style.BackColor = Color.RED;
		// gridView.Rows[1].DefaultCellStyle.BackColor = Color.YELLOW;
		// gridView.setBackgroundColor(Color.RED);
		// gridView.getChildAt(0).setBackgroundColor(Color.RED);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		init();
	}
		
		@Override
		public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		}
	// private void update(){
	// Thread t=new Thread(new Runnable(){
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// while (true) {
	// init();
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// //代码
	// });
	// t.start();
	// }

	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// while(true){
	// Log.v("run", "yes");
	// init();
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

}
