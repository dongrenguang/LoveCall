package loveCall.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loveCall.adapter.ContactListAdapter;
import loveCall.helper.DBHelper;
import loveCall.model.ContactBean;
import loveCall.view.QuickAlphabeticBar;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;

public class ContactListActivity extends Activity {
	DBHelper dbHelper;
	private ContactListAdapter adapter;
	private ListView contactList;
	private List<ContactBean> list;
	private AsyncQueryHandler asyncQueryHandler;
	private QuickAlphabeticBar alphabeticBar; 
	String groupName;
	Button returnButton;
	Intent intent;

	private Map<Integer, ContactBean> contactIdMap = null;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_contact_list);
		contactList = (ListView) findViewById(R.id.contact_list);
		intent = getIntent();
		Log.d("type", intent.getIntExtra("type", -1)+"");
		switch(intent.getIntExtra("type", -1)){

		case 0:
			contactList.setOnItemClickListener(new ForSpecialListener());
			break;
		case 1:
			Log.d("add_forTaskListener", "");
			contactList.setOnItemClickListener(new ForTaskListener());
			break;
		default:
			finish();
		}

		alphabeticBar = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);

		asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
		groupName = getIntent().getStringExtra("groupName");
		dbHelper = DBHelper.getInstance(this,1);

		init();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	private void init() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; 

		String[] projection = { BaseColumns._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };

		asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");

	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				contactIdMap = new HashMap<Integer, ContactBean>();
				list = new ArrayList<ContactBean>();
				cursor.moveToFirst(); 
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);

					if (contactIdMap.containsKey(contactId)) {
						// do nothing
					} else {

						ContactBean contact = new ContactBean();
						contact.setDisplayName(name);
						contact.setPhoneNum(number);
						contact.setSortKey(sortKey);
						contact.setPhotoId(photoId);
						contact.setLookUpKey(lookUpKey);
						list.add(contact);

						contactIdMap.put(contactId, contact);
					}
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}

			super.onQueryComplete(token, cookie, cursor);
		}

	}

	private void setAdapter(List<ContactBean> list) {
		adapter = new ContactListAdapter(this, list, alphabeticBar);
		contactList.setAdapter(adapter);
		alphabeticBar.init(ContactListActivity.this);
		alphabeticBar.setListView(contactList);
		alphabeticBar.setHight(alphabeticBar.getHeight());
		alphabeticBar.setVisibility(View.VISIBLE);
	}

	class ForSpecialListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos,
				long id) {
			String contactName = list.get(pos).getDisplayName();
			String number = list.get(pos).getPhoneNum();
			try{
				Cursor cursor = dbHelper.getReadableDatabase()
						.rawQuery("select * from specialPerson where contactname = '"+contactName+"'", null);
				if(cursor.getCount()>0){
					Toast.makeText(ContactListActivity.this,
							"该特别关注已存在", Toast.LENGTH_SHORT).show();
					finish();
				}else{

					ContentValues values = new ContentValues();
					values.put("groupname", groupName);
					values.put("contactname", contactName);
					values.put("number", number);
					dbHelper.getWritableDatabase().insert("specialPerson", null, values);
					finish();
				}
			}catch(Exception ex){
				Toast.makeText(ContactListActivity.this,
						"添加特别关注失败", Toast.LENGTH_SHORT).show();
			}

		}
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
        	ContactListActivity.this.setResult(0, intent);
        	ContactListActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	class ForTaskListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos,
				long id) {
			String contactName = list.get(pos).getDisplayName();
			String number = list.get(pos).getPhoneNum();
			Intent intent = new Intent(ContactListActivity.this, AddReminderActivity.class);
			Log.d("contactNAme", contactName);
			intent.putExtra("contactName", contactName);
			intent.putExtra("number", number);
			setResult(RESULT_OK, intent);
			finish();
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
