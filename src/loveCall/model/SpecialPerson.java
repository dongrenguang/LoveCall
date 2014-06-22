package loveCall.model;

public class SpecialPerson implements Comparable<SpecialPerson>{
	private String contactName;
	private String number;
	
	public SpecialPerson(String contactName, String number){
		this.contactName = contactName;
		this.number = number;
	}
	
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public int compareTo(SpecialPerson p) {
		// TODO Auto-generated method stub
		return this.contactName.compareTo(p.getContactName());
	}
}
