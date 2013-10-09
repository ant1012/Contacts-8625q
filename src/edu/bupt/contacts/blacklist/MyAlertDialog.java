package edu.bupt.contacts.blacklist;

import edu.bupt.contacts.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

public class MyAlertDialog extends Dialog {

    public MyAlertDialog(Context context) {
        super(context);
    }

    public MyAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private View view;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public MyAlertDialog create() {

            final MyAlertDialog dialog = new MyAlertDialog(context,
                    R.style.MyAlertDialogStyle);
            if (view != null) {
                dialog.setContentView(view);
            } else {

            }
            return dialog;
        }
    }
}
