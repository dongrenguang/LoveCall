package loveCall.broadcastReceiver;

import java.util.Date;
import java.util.Map;

import loveCall.helper.DBHelper;
import loveCall.model.CallRecord;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.CallLog.Calls;

public class ReadHistoryAlarmReceiver extends BroadcastReceiver {
	DBHelper dbHelper;
	Map<String , String> specialNumberMap;
	@Override
	public void onReceive(Context context, Intent intent) {
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select number from specialPerson", null);
		while(cursor.moveToNext()){
			specialNumberMap.put(cursor.getString(0), null);
		}
		cursor.close();

		cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null); 
		if(cursor.moveToFirst()){
			do{
				//号码                                                                                             
				String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));  



				if(specialNumberMap.containsKey(number)){
					int type = -1;
					switch(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE)))){
					case Calls.INCOMING_TYPE:                                                                        
						type = 0;                                                                                 
						break;                                                                                       
					case Calls.OUTGOING_TYPE:                                                                        
						type = 1;                                                                                 
						break;    
					default:
						break;
					}
					if(type == -1)
						continue;
					Date startDate = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE))));


					int duration = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DURATION)));  
					CallRecord h = new CallRecord(number, type, startDate, duration);

					Cursor cursor2 = dbHelper.getReadableDatabase().rawQuery
							("select * from callRecords where number ='"+number+"' and start = '"+h.getStartTimeString()+"'", null);
					if(cursor2.getCount() == 0){
						ContentValues values = new ContentValues();
						values.put("number",h.getNumber());
						values.put("type", h.getType());
						values.put("start", h.getStartTimeString());
						values.put("end", h.getEndTimeString());

						dbHelper.getWritableDatabase().insert("callRecord", null, values);
					}
					cursor2.close();
				}
			}while(cursor.moveToNext());  
			cursor.close();
		}
		
	}

}
