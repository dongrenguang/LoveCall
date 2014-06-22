package loveCall.model;


public class HistoryByContact implements Comparable<HistoryByContact>{
	private String contactName;
	private int inMinutes;
	private int outMinutes;
	
	public HistoryByContact(String contactName) {
		int englishLength = 0;
		int characterLength = 0;
		for(int i = 0; i < contactName.length(); i++){
			char c = contactName.charAt(i);
			if((c >= '0' && c <= '9')||(c >= 'a' && c <= 'z')){
				englishLength++;
			}else{
				characterLength++;
			}
			if((englishLength/2 + characterLength) > 4){
				contactName = contactName.substring(0, i);
			}
		}
		
		
		this.contactName = contactName;
		this.inMinutes = 0;
		this.outMinutes = 0;
	}
	
	public void addInMinutes(int m){
		inMinutes += m;
	}
	
	public void addOutMinutes(int m){
		outMinutes += m;
	}

	public String getContactName() {
		return contactName;
	}

	public int getInMinutes() {
		return inMinutes;
	}

	public int getOutMinutes() {
		return outMinutes;
	}

	@Override
	public int compareTo(HistoryByContact h2) {
		return (h2.getInMinutes() + h2.getOutMinutes()-(this.inMinutes + this.outMinutes));
	}

	
	
}
