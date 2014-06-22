package loveCall.model;

public class HistoryByGroup implements Comparable<HistoryByGroup>{
	private String groupName;
	private int inMinutes;
	private int outMinutes;
	
	public HistoryByGroup(String groupName) {
		int englishLength = 0;
		int characterLength = 0;
		for(int i = 0; i < groupName.length(); i++){
			char c = groupName.charAt(i);
			if((c >= '0' && c <= '9')||(c >= 'a' && c <= 'z')){
				englishLength++;
			}else{
				characterLength++;
			}
			if((englishLength/2 + characterLength) > 4){
				groupName = groupName.substring(0, i);
			}
		}
		
		
		this.groupName = groupName;
		this.inMinutes = 0;
		this.outMinutes = 0;
	}
	
	public void addInMinutes(int m){
		inMinutes += m;
	}
	
	public void addOutMinutes(int m){
		outMinutes += m;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getInMinutes() {
		return inMinutes;
	}

	public int getOutMinutes() {
		return outMinutes;
	}

	@Override
	public int compareTo(HistoryByGroup h2) {
		return (h2.getInMinutes() + h2.getOutMinutes()-(this.inMinutes + this.outMinutes));
	}

}
