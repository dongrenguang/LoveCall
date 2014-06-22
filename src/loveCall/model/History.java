package loveCall.model;

import java.util.Comparator;

public class History implements Comparable<History>{
	private String name;
	private String number;
	private int duration;
	
	public History(String name , String number,int duration) {
		super();
		this.number = number;
		this.name = name;
		this.duration = duration;
	}
	
	public History() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void addDuration(int a){
		this.duration+=a;
	}

	@Override
	public int compareTo(History arg0) {
		// TODO Auto-generated method stub
		return arg0.getDuration()-this.duration;
	}
	
	

}
