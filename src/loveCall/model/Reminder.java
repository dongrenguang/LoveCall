package loveCall.model;

public class Reminder {
	private int flag;//标识
	private String contactName;//姓名
	private String contactPhone;//号码
	//private IFrequency frequency;
	private String note;//备注
	private String details;
	public Reminder(int flag, String contactName, String contactPhone,
			String note, String details) {
		super();
		this.flag = flag;
		this.contactName = contactName;
		this.contactPhone = contactPhone;
		this.note = note;
		this.details = details;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	
	

}
