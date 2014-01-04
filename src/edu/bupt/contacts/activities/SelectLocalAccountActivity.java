package edu.bupt.contacts.activities;

import edu.bupt.contacts.R;
import edu.bupt.contacts.model.AccountWithDataSet;
import edu.bupt.contacts.util.AccountsListAdapter;
import edu.bupt.contacts.util.AccountsListAdapter.AccountListFilter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Intents;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 选择账户，本地或者SIM卡
 * 
 * */

/** zzz */
// select account before edit new contact
public class SelectLocalAccountActivity extends Activity {
    private final String TAG = "SelectAccountActivity";
    private AccountsListAdapter mAccountListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_account);

        final TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(getString(R.string.select_local_account));

        final ListView accountListView = (ListView) findViewById(R.id.account_list);
        mAccountListAdapter = new AccountsListAdapter(this,
                AccountListFilter.ALL_ACCOUNTS);//AccountListFilter.ACCOUNTS_CONTACT_WRITABLE
        accountListView.setAdapter(mAccountListAdapter);
        accountListView.setOnItemClickListener(mAccountListItemClickListener);
    }

    private final OnItemClickListener mAccountListItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mAccountListAdapter == null) {
                return;
            }
            saveAccountAndReturnResult(mAccountListAdapter.getItem(position));
        }
    };

    private void saveAccountAndReturnResult(AccountWithDataSet account) {
        // Save this as the default account
//        mEditorUtils.saveDefaultAndAllAccounts(account);

        // Pass account info in activity result intent
        Intent intent = new Intent();
        intent.putExtra(Intents.Insert.ACCOUNT, account);
        setResult(RESULT_OK, intent);
        finish();
    }
}
