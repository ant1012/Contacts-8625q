//package edu.bupt.contacts.settings;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.preference.Preference;
//import android.util.AttributeSet;
//import android.util.Log;
//
//public class IPSelectPreference extends Preference {
//
//    private static final String TAG = "IPSelectPreference";
//
//    public IPSelectPreference(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    protected Object onGetDefaultValue(TypedArray a, int index) {
//        return a.getString(index);
//    }
//
//    @Override
//    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
//        Log.v(TAG, "onSetInitialValue");
//        // long value;
//        // if (restorePersistedValue)
//        // value = getPersistedLong(0);
//        // else {
//        // value = Long.parseLong(defaultValue.toString());
//        // }
//        super.setDefaultValue(defaultValue);
//    }
//}
