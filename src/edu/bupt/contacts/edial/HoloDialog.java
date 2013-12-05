package edu.bupt.contacts.edial;

import edu.bupt.contacts.R;
import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

/** zzz */

/**
 * 类描述： 设置HoloDialog样式为R.style.HoloDialogTheme，为EdialDialog提供父类 ddd
 * */
public class HoloDialog extends Dialog {

    public HoloDialog(final Context context) {
        // super(context);
        super(context, R.style.HoloDialogTheme);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }
}
