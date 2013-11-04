package edu.bupt.contacts.calllog;

import java.util.Date;

import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.R;
import edu.bupt.contacts.activities.DialtactsActivity;
import edu.bupt.contacts.numberlocate.NumberLocateSetting;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;


public class CallLogSearch extends Activity implements OnQueryTextListener, OnCloseListener {

	private ListView searchCalllogListview;
	private Cursor cursor = null;
	private CallLogSearchAdapter listAdapter;
	private ContentResolver resolver;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_log_search);
		
		searchCalllogListview = (ListView)findViewById(R.id.listView_clear_call_log);

		resolver = getContentResolver();
		cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{"_id","number","date","type","name"}, null,  null, CallLog.Calls.DATE + " DESC");
		
		listAdapter = new CallLogSearchAdapter(this, R.layout.call_log_search_item, 
                cursor,
                new String[]{CallLog.Calls.DATE,CallLog.Calls.NUMBER,CallLog.Calls.TYPE}, 
                new int[]{R.id.item_time,R.id.item_number,R.id.item_type});
         
        searchCalllogListview.setAdapter(listAdapter);

		configureActionBar();
		
	}
	
    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        }
    }
    
    /** Invoked when the user presses the home button in the action bar. */
    private void onHomeSelected() {
        Intent intent = new Intent(this, DialtactsActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onHomeSelected();
                return true;
            }
            case R.id.show_threedays:{
            	long threeto = System.currentTimeMillis();
        		long threefrom = System.currentTimeMillis() - 2*24*3600*1000L;
        		Log.v(new Date(threefrom).toLocaleString(),new Date(threeto).toLocaleString());
        		
            	queryCallLog(threefrom,threeto);
        	    return true;
            }
            case R.id.show_threedays_ago:{  // modified by yuan 
            	long threeagoto = System.currentTimeMillis() - 3*24*3600*1000L;
            	Log.v(threeagoto+"",threeagoto+"");
            	queryCallLog(0,threeagoto);
                return true;
            }
            default: ;
                //throw new IllegalArgumentException();
            return true;
        }
    }
	
	@Override   
	public boolean onCreateOptionsMenu(Menu menu) {   
		getMenuInflater().inflate(R.menu.actionbar_searchview, menu);
		getMenuInflater().inflate(R.menu.call_log_search_options, menu);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setOnQueryTextListener(this);
	    searchView.setOnCloseListener(this);
	    return super.onCreateOptionsMenu(menu);
  
	}
	
	@Override
	public boolean onQueryTextChange(String str) {
		// TODO Auto-generated method stub
			queryCallLog(str);		
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String str) {
		// TODO Auto-generated method stub
		//Log.v("search2",str);
		return false;
	} 
	
	public void queryCallLog(String str){
		Cursor cursor = null;
		String[] projection = new String[]{"_id","number","date","type","name"};
		String selection = null;
		//String selection = CallLog.Calls.NUMBER+"="+str;
		String[] selectionArgs = null;
		if(str.length()>0){
			selectionArgs = new String[]{"%"+str+"%","%"+str+"%"};
			selection = CallLog.Calls.NUMBER+" like ? or " + CallLog.Calls.CACHED_NAME + " like ? ";
		}		 
		cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection , selection,  selectionArgs,  CallLog.Calls.DATE + " DESC");
	    this.listAdapter.changeCursor(cursor);
		this.listAdapter.notifyDataSetChanged();
	}
	
	public void queryCallLog(long from,long to){
		Cursor cursor = null;
		String[] projection = new String[]{"_id","number","date","type","name"};
		String selection = null;
		String[] selectionArgs = null;
		if(from>to){
			return;
		}else if(from == to){
			selection = CallLog.Calls.DATE+" = ? ";
			selectionArgs = new String[]{from+""};			
		}else{
			selection = CallLog.Calls.DATE+" >= ? and " + CallLog.Calls.DATE + " <= ? ";
			selectionArgs = new String[]{""+from,""+to};
		}
		Log.v(selection.toString(),selectionArgs.toString());
		cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection , selection,  selectionArgs,  CallLog.Calls.DATE + " DESC");
	    Log.v("cursor",cursor.getCount()+"");
		this.listAdapter.changeCursor(cursor);
		this.listAdapter.notifyDataSetChanged();
	}

	protected void onDestroy() {
        super.onDestroy();
        cursor.close(); 
    }

	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub
		return false;
	}
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	Log.v("ok","ok");
//        switch (item.getItemId()) {
//            case R.id.show_threedays:
//            	long threeto = System.currentTimeMillis();
//        		long threefrom = System.currentTimeMillis() - 3*24*3600*1000L;
//        		Log.v(threefrom+"",threeto+"");
//            	queryCallLog(threefrom,threeto);
//        	    return true;
//        
//            case R.id.show_threedays_ago:  // modified by yuan 
//            	long threeagoto = System.currentTimeMillis() - 3*24*3600*1000L;
//            	Log.v(threeagoto+"",threeagoto+"");
//            	queryCallLog(0,threeagoto);
//                return true;
//        }
//        return false;
//    }
	
}
