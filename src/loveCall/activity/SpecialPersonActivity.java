package loveCall.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import loveCall.adapter.SpecialPersonAdapter;
import loveCall.helper.DBHelper;
import loveCall.model.SpecialPerson;
import loveCall.model.SpecialPersonGroup;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.example.loveCall.R;
import com.umeng.analytics.MobclickAgent;


public class SpecialPersonActivity extends Activity implements OnClickListener {
	DBHelper dbHelper;
	private List<SpecialPersonGroup> contactGroupList;
	private ExpandableListView expandlistView;
	private SpecialPersonAdapter vipAdapter;
	private Button addContactGroupButton;
	private int pressedGroupId;
	private int pressedChildId;


	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_special_person);
		expandlistView = (ExpandableListView) findViewById(R.id.vip_expandlist);
		addContactGroupButton = (Button) findViewById(R.id.add_contact_group_btn);
		addContactGroupButton.setOnClickListener(this);

		dbHelper = DBHelper.getInstance(this,1);
		putInitData();

		vipAdapter = new SpecialPersonAdapter(this, contactGroupList);
		expandlistView.setAdapter(vipAdapter);
		expandlistView.setOnItemClickListener(new MyClickListener());
		expandlistView.setOnCreateContextMenuListener(new MyContextMenuListener());
		//		expandlistView.setGroupIndicator(null); // 去掉默认带的箭头

		// 遍历所有group,将所有项设置成默认展开
		int groupCount = expandlistView.getCount();
		for (int i = 0; i < groupCount; i++) {
			expandlistView.expandGroup(i);
		}
		Intent intent = getIntent();
		boolean fromAddReminder = false;
		fromAddReminder = intent.getBooleanExtra("addReminder", false);
		if(fromAddReminder){
			expandlistView.setOnChildClickListener(new ForAddReminderListener());
		}
	}

	private void putInitData() {
		contactGroupList = new ArrayList<SpecialPersonGroup>();
		String[] columns = null;
		Map<String, SpecialPersonGroup> map = null;

		columns = new String[]{"groupname"};
		Cursor cursor = dbHelper.getReadableDatabase().query("specialGroup", columns, null, null, null, null, null);
		map = new HashMap<String, SpecialPersonGroup>();
		while(cursor.moveToNext()){
			if(!map.containsKey(cursor.getString(0))){
				map.put(cursor.getString(0), new SpecialPersonGroup(cursor.getString(0)));
			}
		}
		cursor.close();
		columns = new String[]{"groupname", "contactname", "number"};
		Cursor cursor2 = dbHelper.getReadableDatabase().query("specialPerson", columns, null, null, null, null, null);
		while(cursor2.moveToNext()){
			SpecialPerson p = new SpecialPerson(cursor2.getString(1), cursor2.getString(2));
			//			Log.d("add_person_query", cursor2.getString(0));
			SpecialPersonGroup g = map.get(cursor2.getString(0));
			if(g != null){
				g.addContact(p);
			}
		}
		cursor2.close();
		Iterator iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry entry = (Entry)iter.next();
			contactGroupList.add((SpecialPersonGroup)entry.getValue());
		}
		for(SpecialPersonGroup g:contactGroupList){
			Collections.sort(g.getPersonList());
		}
		Collections.sort(contactGroupList);

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_contact_group_btn:
			final EditText editText = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("请输入组名称")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(editText)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){ 

				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					addGroup(editText.getText().toString());

				}
			})
			.setNegativeButton("取消", null).show();


			break;
		default:
			break;
		}
	}

	private void addGroup(String groupName){
		if(groupName.trim().equals("")){
			Toast.makeText(SpecialPersonActivity.this,
					"组名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(groupName.trim().length()>8){
			Toast.makeText(SpecialPersonActivity.this,
					"组名必须在8个字符以内", Toast.LENGTH_SHORT).show();
			return;
		}
		for(SpecialPersonGroup g:contactGroupList){
			if(groupName.trim().equals(g.getGroupName())){
				Toast.makeText(SpecialPersonActivity.this,
						"已存在该组", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		try{
			//			Log.i("addGroup", "add a group");//
			ContentValues values = new ContentValues();
			values.put("groupname", groupName);
			dbHelper.getWritableDatabase().insert("specialGroup", null, values);

			Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from specialGroup where groupname = '"+groupName+"'", null);
			if(cursor.moveToNext()){
				contactGroupList.add(new SpecialPersonGroup(cursor.getString(0)));
			}
			cursor.close();
			Collections.sort(contactGroupList);
			updateAdapter();
		}catch(Exception ex){
			Toast.makeText(SpecialPersonActivity.this,
					"添加组失败", Toast.LENGTH_SHORT).show();
		}
	}

	class MyClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View v,
				int pos, long id) {
			pressedGroupId = (Integer)v.getTag(R.id.button1);
			pressedChildId = (Integer)v.getTag(R.id.button2);
			expandlistView.showContextMenu();
		}

	}
	class MyContextMenuListener implements OnCreateContextMenuListener{

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo info) {
			MenuInflater inflater=getMenuInflater();
			inflater.inflate(R.menu.special_context_menu, menu);			
		}


	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
		//                .getMenuInfo();
		switch (item.getItemId()) {
		case R.id.delete_special:
			if(pressedChildId == -1){//删除组
				new AlertDialog.Builder(this) 
				.setTitle("删除组")
				.setMessage("该组内的所有特别关注也将被删除，确定吗？")
				.setPositiveButton("确定", new  DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteGroup();
					}
				})
				.setNegativeButton("取消", null)
				.show();

			}else{//删除个人
				new AlertDialog.Builder(this) 
				.setTitle("删除特别关注")
				.setMessage("确定删除该特别关注？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						deletePerson();
					}
				})
				.setNegativeButton("取消", null)
				.show();
				
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void deleteGroup(){
		String[] args = new String[1];
		String groupname =  contactGroupList.get(pressedGroupId).getGroupName();
		contactGroupList.remove(pressedGroupId);
		args[0] =groupname;
		dbHelper.getWritableDatabase().delete("specialGroup", "groupname=?", args);

		Cursor cursor = dbHelper.getReadableDatabase()
				.rawQuery("select number from specialPerson where groupname = '"+groupname+"'", null);
		ArrayList<String> contactNumberList = new ArrayList<String>();
		while(cursor.moveToNext()){
			contactNumberList.add(cursor.getString(0));
		}

		dbHelper.getWritableDatabase().delete("specialPerson", "groupname=?", args);

		args = new String[contactNumberList.size()];
		for(int i = 0; i < contactNumberList.size(); i++){
			args[i] = contactNumberList.get(i);
		}
		for(int i = 0; i<args.length; i++){
			try{
			String arg[] = new String[1];
			arg[0] = args[i];
			dbHelper.getWritableDatabase().delete("callRecord", "number=?", arg);
			}catch(Exception ex){}
		}
		updateAdapter();
	}
	private void deletePerson(){
		String[] args = new String[1];
		SpecialPersonGroup g = contactGroupList.get(pressedGroupId);
		SpecialPerson p = g.getPerson(pressedChildId);
		g.deletePerson(pressedChildId);

		args[0] = p.getContactName();
		dbHelper.getWritableDatabase().delete("specialPerson", "contactname=?", args);
		try{
			args[0] = p.getNumber();
			dbHelper.getWritableDatabase().delete("callRecord", "number=?", args);
		}catch(Exception ex){

		}
		updateAdapter();
	}

	private void updateAdapter(){
		vipAdapter.updatePersonList(contactGroupList);
		vipAdapter.notifyDataSetChanged();
		expandlistView.setAdapter(vipAdapter);
		// 遍历所有group,将所有项设置成默认展开
		int groupCount = expandlistView.getCount();
		for (int i = 0; i < groupCount; i++) {
			expandlistView.expandGroup(i);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		putInitData();
		updateAdapter();
	}
	class ForAddReminderListener implements OnChildClickListener{

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			SpecialPersonGroup g = contactGroupList.get(groupPosition);
			SpecialPerson p = g.getPerson(childPosition);
			Intent intent = new Intent(SpecialPersonActivity.this, AddReminderActivity.class);
			Log.d("contactNAme", p.getContactName());
			intent.putExtra("contactName", p.getContactName());
			intent.putExtra("number", p.getNumber());
			setResult(RESULT_OK, intent);
			finish();
			return false;
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