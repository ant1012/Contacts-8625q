package edu.bupt.contacts.ipcall;

import android.content.Context;
import android.content.SharedPreferences;

public class IPCall {
	
	protected  SharedPreferences sp ;
	
	public IPCall(Context context){
		sp = context.getSharedPreferences("edu.bupt.contacts.ip", context.MODE_PRIVATE);
	}
	
	public boolean isCDMAIPEnabled(){
		if(!(sp.getString("CDMA_IP_KEY", null)== null)){
        	return true;
        }
		return false;
	}
	
	public boolean isGSMIPEnabled(){
		if(!(sp.getString("GSM_IP_KEY", null)== null)){
        	return true;
        }
		return false;
	}
	
	public String getCDMAIPCode(){
		
		return sp.getString("CDMA_IP_KEY", null);
	}
	
	public String getGSMIPCode(){
		return sp.getString("GSM_IP_KEY", null);
	}
}
