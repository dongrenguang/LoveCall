package loveCall.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallRecord {
	SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String number;
	int type; //0:income  1:outgoing
	private Date start;
	private Date end;
	
	public CallRecord(String number, int type, String s, String e) throws ParseException{
		this.number = number;
		this.type = type;
		start = dateformat.parse(s);
		end = dateformat.parse(e);
//		Log.d("call_record", number);
//		Log.d("call_record", type+"");
//		Log.d("call_record", s);
//		Log.d("call_record", e);
		checkNumber();
	}
	
	

	private void checkNumber() {
		if(number.charAt(0)=='+'){
			this.number=this.number.substring(3);
		}
		
	}



	public CallRecord(String number, int type, Calendar startCal, Calendar endCal){
		this.number = number;
		this.type = type;
		this.start = startCal.getTime();
		this.end = endCal.getTime();
		checkNumber();
	}
	
	public CallRecord(String number, int type, Date startDate, int duration) {
		this.number = number;
		this.type = type;
		this.start = startDate;
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.SECOND, duration);
		end = c.getTime();
		checkNumber();
	}

	public String getStartTimeString(){
		return dateformat.format(start);
	}
	public String getEndTimeString(){
		return dateformat.format(end);
	}
	public String getNumber(){
		return this.number;
	}
	public int getType(){
		return this.type;
	}
	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}
	
	public int getPeriodMinutes(){
		return (int) ((end.getTime() - start.getTime())/(1000*60));
	}
}
