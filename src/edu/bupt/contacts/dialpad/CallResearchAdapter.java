package edu.bupt.contacts.dialpad;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.contacts.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
	public View getView(int position, View convertView, ViewGroup parent) {
		CallResearchModel model = null;
		convertView = View.inflate(context, R.layout.call_contact_search_item, null);
		TextView tvname = (TextView) convertView.findViewById(R.id.textView_contact_name);
		TextView tvtel = (TextView) convertView.findViewById(R.id.textView_contact_num);
//	ddd 删除拨号盘匹配字符	
		TextView tvGroup = (TextView) convertView.findViewById(R.id.textView_contact_group);
		model = contactList.get(position);
		tvname.setText(model.name);
		tvtel.setText(model.telnum);
		if(isShowAll){
			model.group = "";
		}
		
	//	ddd 删除 拨号盘匹配 字符 
		tvGroup.setText(model.group);
		return convertView;
	}
	
}