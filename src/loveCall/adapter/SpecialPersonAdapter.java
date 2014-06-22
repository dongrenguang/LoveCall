package loveCall.adapter;

import java.util.List;

import loveCall.activity.ContactListActivity;
import loveCall.activity.SpecialPersonActivity;
import loveCall.model.SpecialPerson;
import loveCall.model.SpecialPersonGroup;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.loveCall.R;


public class SpecialPersonAdapter extends BaseExpandableListAdapter {
	private LayoutInflater inflater = null;
	private List<SpecialPersonGroup> contactGroupList;
	Context context;


	public SpecialPersonAdapter(Context context, List<SpecialPersonGroup> contactGroupList) {
		this.contactGroupList = contactGroupList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}
	public List<SpecialPersonGroup> getContactGroupList(){
		return this.contactGroupList;
	}
	public void updatePersonList(List<SpecialPersonGroup> contactGroupList){
		this.contactGroupList = contactGroupList;
	}
	public void removeAll(){
		contactGroupList.clear();
	}
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return contactGroupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(contactGroupList.get(groupPosition).getPersonList() == null){
			return 0;
		}else{
			return contactGroupList.get(groupPosition).getPersonList().size();
		}
	}

	@Override
	public SpecialPersonGroup getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return contactGroupList.get(groupPosition);
	}

	@Override
	public SpecialPerson getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return contactGroupList.get(groupPosition).getPersonList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.special_group_item, null);
		}
		LinearLayout ll = new LinearLayout(context);
		ll.setBackgroundColor(context.getResources().getColor(R.color.blue));
		ll.setOrientation(0);

		ImageView logo = new ImageView(context);
		logo.setImageResource(R.drawable.group);
		logo.setPadding(50, 10, 0, 0);
		ll.addView(logo);

		TextView textView=new TextView(context);
		textView.setText(contactGroupList.get(groupPosition).getGroupName());
		textView.setPadding(0, 10, 100, 0);
		textView.setTextSize(22);
		textView.setTextColor(Color.WHITE);
		ll.addView(textView);

		ImageView button=new ImageView(context);
		button.setImageResource(R.drawable.add_person);
		button.setPadding(0, 10, 0, 0);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ContactListActivity.class);
				intent.putExtra("groupName", contactGroupList.get(groupPosition).getGroupName());
				intent.putExtra("type", 0);
				((Activity)context).startActivityForResult(intent, 0);
			}

		});
		ll.addView(button);

		ll.setTag(R.id.button1,groupPosition);
		ll.setTag(R.id.button2, -1);
		return ll;

		/*GroupViewHolder holder = new GroupViewHolder();
		ImageView logo = new ImageView(context);
        logo.setImageResource(R.drawable.group);
        logo.setPadding(50, 0, 0, 0);
        holder.imageView=logo;

		holder.groupName = (TextView) convertView.findViewById(R.id.contact_group_name);
		holder.groupName.setText(contactGroupList.get(groupPosition).getGroupName());

		Button button=new Button(context);
		button.setBackgroundResource(R.drawable.add_person);
		holder.addButton =button;
		button.setPadding(50, 0, 0, 0);
		holder.addButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ContactListActivity.class);
				intent.putExtra("groupName", contactGroupList.get(groupPosition).getGroupName());
				intent.putExtra("type", 0);
				((Activity)context).startActivityForResult(intent, 0);
			}

		});
		convertView.setTag(R.id.button1,groupPosition);
		convertView.setTag(R.id.button2, -1);
//		Log.d("set_tag_group", groupPosition+"");
		return convertView;*/
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		SpecialPerson contact = getChild(groupPosition, childPosition);
		
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(0);
		ImageView generallogo = new ImageView(context);
		generallogo.setImageResource(R.drawable.person);
		generallogo.setPadding(50, 10, 0, 0);
		ll.addView(generallogo);
		
		TextView name = new TextView(context);
		name.setText(contact.getContactName());
		name.setPadding(50, 20, 0, 0);
		name.setTextSize(17);
		ll.addView(name);
		
		TextView number=new TextView(context);
		number.setText(contact.getNumber());
		number.setPadding(50, 20, 0, 0);
		number.setTextSize(17);
		ll.addView(number);
		ll.setTag(R.id.button1, groupPosition);
		ll.setTag(R.id.button2, childPosition);
		return ll;

		/*ChildViewHolder viewHolder = null;
		SpecialPerson contact = getChild(groupPosition, childPosition);
		if (convertView != null) {
			viewHolder = (ChildViewHolder) convertView.getTag();
		} else {
			viewHolder = new ChildViewHolder();
			convertView = inflater.inflate(R.layout.special_person_item, null);
			viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
			viewHolder.contactNumber = (TextView) convertView.findViewById(R.id.contact_number);
		}
		viewHolder.contactName.setText(contact.getContactName());
		viewHolder.contactNumber.setText(contact.getNumber());

		convertView.setTag(viewHolder);
		convertView.setTag(R.id.button1, groupPosition);
		convertView.setTag(R.id.button2, childPosition);
		//		Log.d("set_tag_child", childPosition+"");
		return convertView;*/
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	private class GroupViewHolder {
		ImageView imageView;
		TextView groupName;
		Button addButton;
	}

	private class ChildViewHolder {
		public TextView contactName;
		public TextView contactNumber;
	}

}
