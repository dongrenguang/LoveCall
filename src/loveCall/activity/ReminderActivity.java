package loveCall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class ReminderActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		
		Bundle bundle = this.getIntent().getExtras();
		String contactName=bundle.getString("contactName");
		final String contactPhone=bundle.getString("contactPhone");
		String note=bundle.getString("note");
		int flag=bundle.getInt("note");
		
		new AlertDialog.Builder(ReminderActivity.this)
				.setTitle("爱电话提醒")
				.setPositiveButton("打给"+( (contactName==null || contactName.equals(""))?contactPhone:contactName ),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+contactPhone));  
			                    startActivity(intent); 
								finish();
							}
						})
				.setNegativeButton("下次再打~",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();

							}
						}).show();
	}
	
	public void On_Click(View view){
		finish();
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
