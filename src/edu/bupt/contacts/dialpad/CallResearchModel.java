package edu.bupt.contacts.dialpad;

/**
 * 北邮ANT实验室
 * ddd
 * 拨号盘号码搜索匹配model
 * 
 * */

public class CallResearchModel{
	
	public String name;
	public String telnum;
	public String pyname;
	public String group;
	public String searchnum;
	
	public CallResearchModel(){
		
	}
	
	public CallResearchModel(String name, String telnum){
		this.name = name; //初始姓名
		this.telnum = telnum;//初始电话号码
		this.group = "";    //匹配项
		pyname = BaseUtil.getPingYin(name); //姓名汉语拼音
		searchnum = BaseUtil.getSearchPhoneNumber(telnum); //电话号码去除空格、横杠
	}
}