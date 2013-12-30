package edu.bupt.contacts.dialpad;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.contacts.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 北邮ANT实验室
 * ddd
 * 拨号盘号码搜索匹配adapter
 * 
 * */

public class CallResearchAdapter extends BaseAdapter{
	
	public List<CallResearchModel> contactList;
	
	private Context context;
	
	private boolean isShowAll;
	
	public CallResearchAdapter(Context context){
        this.contactList = new ArrayList<CallResearchModel>();
        this.context = context;
	}
	
	public List<CallResearchModel> getAdapterList(){
		return contactList;
	}
	
	public void refresh(List<CallResearchModel> refreshList, boolean isShowAll){
		this.isShowAll = isShowAll;
		contactList.clear();
		contactList.addAll(refreshList);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return contactList.size();
	}

	@Override
	public Object getItem(int position) {
		return contactList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	/**
	 * 在拨号盘输入电话号码，自动匹配对应联系人，显示联系人姓名、号码、匹配项
	 * */
	public View getView(int position, View convertView, ViewGroup parent) {
		CallResearchModel model = null;
		convertView = View.inflate(context, R.layout.call_contact_search_item, null);
		TextView tvname = (TextView) convertView.findViewById(R.id.textView_contact_name); //联系人姓名
		TextView tvtel = (TextView) convertView.findViewById(R.id.textView_contact_num);   //联系人号码

		TextView tvGroup = (TextView) convertView.findViewById(R.id.textView_contact_group);//联系人匹配项
		model = contactList.get(position);
		tvname.setText(model.name);
		tvtel.setText(model.telnum);
		if(isShowAll){
			model.group = "";
		}

		tvGroup.setText(model.group);
		return convertView;
	}
	
}