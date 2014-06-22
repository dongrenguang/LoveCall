package loveCall.model;

import java.util.ArrayList;
import java.util.List;


public class SpecialPersonGroup implements Comparable<SpecialPersonGroup>{
	private String groupName;
	private List<SpecialPerson> personList;

	public SpecialPersonGroup( String groupName) {
		this.groupName = groupName;
		this.personList = new ArrayList<SpecialPerson>();
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<SpecialPerson> getPersonList() {
		return personList;
	}
	public void setPersonList(List<SpecialPerson> personList) {
		this.personList = personList;
	}

	public void addContact(SpecialPerson contact) {
		personList.add(contact);

	}
	@Override
	public int compareTo(SpecialPersonGroup g2) {
		// TODO Auto-generated method stub
		return this.groupName.compareTo(g2.groupName);
	}

	public SpecialPerson getPerson(int pos) {
		return this.personList.get(pos);
	}
	
	public void deletePerson(int pos){
		this.personList.remove(pos);
	}
}
