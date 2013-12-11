package edu.bupt.contacts.calllog;

import java.util.Calendar;
import java.util.Date;

import edu.bupt.contacts.R;
import edu.bupt.contacts.activities.DialtactsActivity;
import android.os.Bundle;
import android.provider.CallLog;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

public class CallLogSearchActivity extends Activity implements OnQueryTextListener, OnCloseListener {
    private static final String TAG = "CallLogSearchActivity";

    private ListView searchCalllogListview;
    private Cursor cursor = null;
    private CallLogSearchAdapter listAdapter;
    private ContentResolver resolver;

    private int cfromyear = 0;
    private int cfrommonth = 0;
    private int cfromday = 0;
    private int cfromhour = 0;// 010
    private int ctoyear = 0;
    private int ctomonth = 0;
    private int ctoday = 0;
    private int ctohour = 0;// 010
    private Calendar cfrom;
    private Calendar cto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log_search);

        searchCalllogListview = (ListView) findViewById(R.id.listView_clear_call_log);

        resolver = getContentResolver();
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[] { "_id", "number", "date", "type", "name" },
                null, null, CallLog.Calls.DATE + " DESC");

        listAdapter = new CallLogSearchAdapter(this, R.layout.call_log_search_item, cursor, new String[] {
                CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.TYPE }, new int[] { R.id.item_time,
                R.id.item_number, R.id.item_type });

        searchCalllogListview.setAdapter(listAdapter);

        configureActionBar();

        /** zzz */
        // create dialog to choose a search mode
        // LayoutInflater inflater =LayoutInflater.from(this);
        // View v = inflater.inflate(R.layout.dialog_set_merge,null);
        // v.set
        // new
        // AlertDialog.Builder(this).setTitle(R.string.calllog_search).setView(v).show();

        /** zzz */
        // create dialog to choose a search mode
        // copied from sms project
        AlertDialog.Builder builder = new Builder(CallLogSearchActivity.this);

        builder.setSingleChoiceItems( // 设置单选列表选项
                R.array.call_log_search_selections, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                        // Log.v("TraditionalActivity", "which:"+which);

                        case 0: // search with name
                            Log.v(TAG, "which - " + which);
                            Log.v(TAG, "search with name");
                            // flag = 5;
                            // zaizhe

                            // Log.v("TraditionalActivity", "which:" + which +
                            // "flag: " + flag);

                            dialog.dismiss();

                            final EditText etSearchName = new EditText(CallLogSearchActivity.this);
                            etSearchName.setInputType(InputType.TYPE_CLASS_TEXT);
                            AlertDialog.Builder nameSearchBuilder = new AlertDialog.Builder(CallLogSearchActivity.this);

                            nameSearchBuilder.setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle(R.string.call_log_search_with_name).setView(etSearchName)
                                    .setPositiveButton(R.string.ok, new OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Log.v(TAG, "onClick");
                                            Log.i(TAG, "search name - " + etSearchName.getText().toString());
                                            queryCallLogWithName(etSearchName.getText().toString());
                                            arg0.dismiss();
                                        }

                                    }).setNegativeButton(R.string.cancel, null);

                            nameSearchBuilder.create().show();

                            // LayoutInflater address_inflater =
                            // getLayoutInflater();
                            //
                            // View address_layout =
                            // address_inflater.inflate(R.layout.searchbody,
                            // null);
                            //
                            // Builder addressBuilder = new
                            // Builder(TraditionalActivity.this);
                            //
                            // addressBuilder.setIcon(android.R.drawable.ic_dialog_info);
                            // addressBuilder.setTitle(R.string.traditional_search_address);
                            // addressBuilder.setView(address_layout);
                            //
                            // final TextView addressText = (TextView)
                            // address_layout.findViewById(R.id.body);
                            //
                            // addressBuilder.setPositiveButton(R.string.traditional_ok,
                            // new OnClickListener() {
                            // public void onClick(DialogInterface dialog, int
                            // which) {
                            //
                            // body = addressText.getText().toString();
                            //
                            // fillSearchResult(flag, model_flag);
                            // flag = 0;
                            // dialog.dismiss();
                            //
                            // }
                            // });
                            //
                            // addressBuilder.setNegativeButton(R.string.traditional_cancel,
                            // new OnClickListener() {
                            // public void onClick(DialogInterface dialog, int
                            // which) {
                            //
                            // dialog.dismiss();
                            //
                            // }
                            // });
                            // // zaizhe
                            // // fillSearchResult(flag,model_flag);
                            // // flag=0;
                            // addressBuilder.show();

                            break;

                        case 1: // search for number
                            Log.v(TAG, "which - " + which);
                            Log.v(TAG, "search with name");

                            // flag = 1;
                            // zaizhe

                            dialog.dismiss();

                            final EditText etSearchNumber = new EditText(CallLogSearchActivity.this);
                            etSearchNumber.setInputType(InputType.TYPE_CLASS_TEXT);
                            AlertDialog.Builder numberSearchBuilder = new AlertDialog.Builder(
                                    CallLogSearchActivity.this);

                            numberSearchBuilder.setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle(R.string.call_log_search_with_number).setView(etSearchNumber)
                                    .setPositiveButton(R.string.ok, new OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Log.v(TAG, "onClick");
                                            Log.i(TAG, "search name - " + etSearchNumber.getText().toString());
                                            queryCallLogWithNumber(etSearchNumber.getText().toString());
                                            arg0.dismiss();
                                        }

                                    }).setNegativeButton(R.string.cancel, null);

                            numberSearchBuilder.create().show();

                            // Log.v("TraditionalActivity", "which:" + which +
                            // "flag: " + flag);
                            //
                            // dialog.dismiss();
                            // LayoutInflater number_inflater =
                            // getLayoutInflater();
                            //
                            // View numbder_layout =
                            // number_inflater.inflate(R.layout.searchbody,
                            // null);
                            //
                            // Builder numbderBuilder = new
                            // Builder(TraditionalActivity.this);
                            //
                            // numbderBuilder.setIcon(android.R.drawable.ic_dialog_info);
                            // numbderBuilder.setTitle(R.string.traditional_search_address);
                            // numbderBuilder.setView(numbder_layout);
                            //
                            // final TextView numbderText = (TextView)
                            // numbder_layout.findViewById(R.id.body);
                            //
                            // numbderBuilder.setPositiveButton(R.string.traditional_ok,
                            // new OnClickListener() {
                            // public void onClick(DialogInterface dialog, int
                            // which) {
                            //
                            // body = numbderText.getText().toString();
                            //
                            // fillSearchResult(flag, model_flag);
                            // flag = 0;
                            // dialog.dismiss();
                            //
                            // }
                            // });
                            //
                            // numbderBuilder.setNegativeButton(R.string.traditional_cancel,
                            // new OnClickListener() {
                            // public void onClick(DialogInterface dialog, int
                            // which) {
                            //
                            // dialog.dismiss();
                            //
                            // }
                            // });
                            // // zaizhe
                            // // fillSearchResult(flag,model_flag);
                            // // flag=0;
                            // numbderBuilder.show();

                            break;

                        // case 2:
                        // flag = 2;
                        //
                        // dialog.dismiss();
                        // LayoutInflater body_inflater = getLayoutInflater();
                        //
                        // View body_layout =
                        // body_inflater.inflate(R.layout.searchbody, null);
                        //
                        // Builder bodyBuilder = new
                        // Builder(TraditionalActivity.this);
                        //
                        // bodyBuilder.setIcon(android.R.drawable.ic_dialog_info);
                        // bodyBuilder.setTitle(R.string.traditional_search_body);
                        // bodyBuilder.setView(body_layout);
                        //
                        // final TextView bodyText = (TextView)
                        // body_layout.findViewById(R.id.body);
                        //
                        // bodyBuilder.setPositiveButton(R.string.traditional_ok,
                        // new OnClickListener() {
                        // public void onClick(DialogInterface dialog, int
                        // which) {
                        //
                        // body = bodyText.getText().toString();
                        //
                        // fillSearchResult(flag, model_flag);
                        // flag = 0;
                        //
                        // }
                        // });
                        //
                        // bodyBuilder.setNegativeButton(R.string.traditional_cancel,
                        // new OnClickListener() {
                        // public void onClick(DialogInterface dialog, int
                        // which) {
                        //
                        // dialog.dismiss();
                        //
                        // }
                        // });
                        // // zaizhe
                        // // fillSearchResult(flag,model_flag);
                        // // flag=0;
                        // bodyBuilder.show();
                        //
                        // break;
                        case 2: // search with date stamp

                            dialog.dismiss();

                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.call_log_search_with_date_dialog,
                                    (ViewGroup) findViewById(R.id.dialog));

                            DatePicker datePickerFrom = (DatePicker) layout.findViewById(R.id.datePickerFrom);
                            TimePicker timePickerFrom = (TimePicker) layout.findViewById(R.id.timePickerFrom);
                            timePickerFrom.setIs24HourView(true);
                            DatePicker datePickerTo = (DatePicker) layout.findViewById(R.id.datePickerTo);
                            TimePicker timePickerTo = (TimePicker) layout.findViewById(R.id.timePickerTo);
                            timePickerTo.setIs24HourView(true);
                            Time now = new Time();
                            now.setToNow();
                            datePickerFrom.setMaxDate(now.toMillis(false));// 010
                            datePickerTo.setMaxDate(now.toMillis(false));// 010

                            final TextView textView_From = (TextView) layout.findViewById(R.id.textView_From);
                            final TextView textView_To = (TextView) layout.findViewById(R.id.textView_To);

                            Builder searchBuilder = new Builder(CallLogSearchActivity.this);
                            searchBuilder.setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle(R.string.call_log_search_with_date).setView(layout)
                                    .setPositiveButton(R.string.ok, new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Calendar cfrom = Calendar.getInstance();
                                            cfrom.set(Calendar.YEAR, cfromyear);
                                            cfrom.set(Calendar.MONTH, cfrommonth - 1);
                                            cfrom.set(Calendar.DAY_OF_MONTH, cfromday);
                                            // 010
                                            cfrom.set(Calendar.HOUR_OF_DAY, cfromhour);
                                            cfrom.set(Calendar.MINUTE, 0);
                                            cfrom.set(Calendar.SECOND, 0);
                                            cfrom.set(Calendar.MILLISECOND, 0);
                                            // Log.v("cfrom", "" +
                                            // cfrom.getTime());
                                            cto = Calendar.getInstance();
                                            cto.set(Calendar.YEAR, ctoyear);
                                            cto.set(Calendar.MONTH, ctomonth - 1);
                                            cto.set(Calendar.DAY_OF_MONTH, ctoday);
                                            cto.set(Calendar.HOUR_OF_DAY, ctohour);// 010
                                            cto.set(Calendar.MINUTE, 59);
                                            cto.set(Calendar.SECOND, 59);
                                            cto.set(Calendar.MILLISECOND, 0);
                                            // Log.v("cto", "" +
                                            // cto.getTime());// 010

                                            if (cfrom.getTimeInMillis() < cto.getTimeInMillis()) {
                                                // getRecord(6,cfrom.getTimeInMillis(),cto.getTimeInMillis());
                                                // flag = 3;

                                                // list.clear();
                                                // listItemAdapter
                                                // .notifyDataSetChanged();
                                                // Log.v("flag44444", ""
                                                // + flag);
                                                // fillListView();

                                                // zaizhe
                                                // fillSearchResult(flag,
                                                // model_flag);
                                                // flag = 0;

                                                Log.i(TAG, "cfrom - " + cfrom.getTimeInMillis());
                                                Log.i(TAG, "cto - " + cto.getTimeInMillis());
                                                queryCallLog(cfrom.getTimeInMillis(), cto.getTimeInMillis());

                                            } else {
                                                Toast.makeText(CallLogSearchActivity.this,
                                                        R.string.call_log_search_error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton(R.string.cancel, null);

                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int monthOfYear = calendar.get(Calendar.MONTH);
                            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                            // 010
                            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            int second = calendar.get(Calendar.SECOND);
                            int millisecond = calendar.get(Calendar.MILLISECOND);

                            cfromyear = year;
                            cfrommonth = monthOfYear + 1;
                            cfromday = dayOfMonth;
                            cfromhour = hourOfDay;// 010

                            ctoyear = year;
                            ctomonth = monthOfYear + 1;
                            ctoday = dayOfMonth;
                            ctohour = hourOfDay;// 010

                            datePickerFrom.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {
                                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    // textView_From.setText("您选择的日期是："+year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日。");
                                    cfromyear = year;
                                    cfrommonth = monthOfYear + 1;
                                    cfromday = dayOfMonth;

                                }
                            });
                            timePickerFrom.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                @Override
                                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                                    cfromhour = hourOfDay;// 010
                                }
                            });

                            datePickerTo.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {
                                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    // textView_To.setText("您选择的日期是："+year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日。");
                                    ctoyear = year;
                                    ctomonth = monthOfYear + 1;
                                    ctoday = dayOfMonth;
                                }
                            });
                            timePickerTo.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                @Override
                                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                                    ctohour = hourOfDay;// 010
                                }
                            });
                            searchBuilder.create().show();

                            break;

                        }
                    }
                });
        // zaizhe
        // builder.setPositiveButton(R.string.traditional_ok,
        // new DialogInterface.OnClickListener() {
        //
        // public void onClick(DialogInterface dialog, int which) {
        //
        // dialog.dismiss();
        //
        // fillSearchResult(flag,model_flag);
        // flag=0;
        //
        // // list.clear();
        // // listItemAdapter.notifyDataSetChanged();
        // // Log.v("1111", "1111");
        // // fillListView();
        //
        // }
        //
        // });
        // builder.setNegativeButton(R.string.traditional_cancel,
        // new DialogInterface.OnClickListener() {
        //
        // public void onClick(DialogInterface dialog, int which) {
        //
        // dialog.dismiss();
        // }
        //
        // });
        builder.setTitle(R.string.calllog_search).create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        case R.id.show_threedays: {
            long threeto = System.currentTimeMillis();
            long threefrom = System.currentTimeMillis() - 2 * 24 * 3600 * 1000L;
            Log.v(new Date(threefrom).toLocaleString(), new Date(threeto).toLocaleString());

            queryCallLog(threefrom, threeto);
            return true;
        }
        case R.id.show_threedays_ago: { // modified by yuan
            long threeagoto = System.currentTimeMillis() - 3 * 24 * 3600 * 1000L;
            Log.v(threeagoto + "", threeagoto + "");
            queryCallLog(0, threeagoto);
            return true;
        }
        default:
            ;
            // throw new IllegalArgumentException();
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /** zzz */
        // getMenuInflater().inflate(R.menu.actionbar_searchview, menu);
        // getMenuInflater().inflate(R.menu.call_log_search_options, menu);
        // SearchView searchView = (SearchView)
        // menu.findItem(R.id.menu_search).getActionView();
        // searchView.setOnQueryTextListener(this);
        // searchView.setOnCloseListener(this);
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
        // Log.v("search2",str);
        return false;
    }

    public void queryCallLog(String str) {
        Cursor cursor = null;
        String[] projection = new String[] { "_id", "number", "date", "type", "name" };
        String selection = null;
        // String selection = CallLog.Calls.NUMBER+"="+str;
        String[] selectionArgs = null;
        if (str.length() > 0) {
            selectionArgs = new String[] { "%" + str + "%", "%" + str + "%" };
            selection = CallLog.Calls.NUMBER + " like ? or " + CallLog.Calls.CACHED_NAME + " like ? ";
        }
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE
                + " DESC");
        this.listAdapter.changeCursor(cursor);
        this.listAdapter.notifyDataSetChanged();
    }

    public void queryCallLogWithName(String str) {
        Cursor cursor = null;
        String[] projection = new String[] { "_id", "number", "date", "type", "name" };
        String selection = null;
        // String selection = CallLog.Calls.NUMBER+"="+str;
        String[] selectionArgs = null;
        if (str.length() > 0) {
            selectionArgs = new String[] { "%" + str + "%" };
            selection = CallLog.Calls.CACHED_NAME + " like ? ";
        }
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE
                + " DESC");
        this.listAdapter.changeCursor(cursor);
        this.listAdapter.notifyDataSetChanged();
    }

    public void queryCallLogWithNumber(String str) {
        Cursor cursor = null;
        String[] projection = new String[] { "_id", "number", "date", "type", "name" };
        String selection = null;
        // String selection = CallLog.Calls.NUMBER+"="+str;
        String[] selectionArgs = null;
        if (str.length() > 0) {
            selectionArgs = new String[] { "%" + str + "%" };
            selection = CallLog.Calls.NUMBER + " like ? ";
        }
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE
                + " DESC");
        this.listAdapter.changeCursor(cursor);
        this.listAdapter.notifyDataSetChanged();
    }

    public void queryCallLog(long from, long to) {
        Cursor cursor = null;
        String[] projection = new String[] { "_id", "number", "date", "type", "name" };
        String selection = null;
        String[] selectionArgs = null;
        if (from > to) {
            return;
        } else if (from == to) {
            selection = CallLog.Calls.DATE + " = ? ";
            selectionArgs = new String[] { from + "" };
        } else {
            selection = CallLog.Calls.DATE + " >= ? and " + CallLog.Calls.DATE + " <= ? ";
            selectionArgs = new String[] { "" + from, "" + to };
        }
        Log.v(selection.toString(), selectionArgs.toString());
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE
                + " DESC");
        Log.v("cursor", cursor.getCount() + "");
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
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // Log.v("ok","ok");
    // switch (item.getItemId()) {
    // case R.id.show_threedays:
    // long threeto = System.currentTimeMillis();
    // long threefrom = System.currentTimeMillis() - 3*24*3600*1000L;
    // Log.v(threefrom+"",threeto+"");
    // queryCallLog(threefrom,threeto);
    // return true;
    //
    // case R.id.show_threedays_ago: // modified by yuan
    // long threeagoto = System.currentTimeMillis() - 3*24*3600*1000L;
    // Log.v(threeagoto+"",threeagoto+"");
    // queryCallLog(0,threeagoto);
    // return true;
    // }
    // return false;
    // }

}
