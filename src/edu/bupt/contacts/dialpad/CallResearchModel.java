package edu.bupt.contacts.dialpad;

public class CallResearchModel{
	
	public String name;
	public String telnum;
	public String pyname;
	public String group;
	public String searchnum;
	
	public CallResearchModel(){
		
	}
	
	public CallResearchModel(String name, String telnum){
		this.name = name;
		this.telnum = telnum;
		this.group = "";
		pyname = BaseUtil.getPingYin(name);
		searchnum = BaseUtil.getSearchPhoneNumber(telnum);
	}
}