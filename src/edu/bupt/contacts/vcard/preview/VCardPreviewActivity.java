package edu.bupt.contacts.vcard.preview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.contacts.R;
import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class VCardPreviewActivity extends ListActivity {
    private String TAG = "VCardPreviewActivity";
    List<Map<String, Object>> contents;
    String filename = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView(R.layout.activity_vcard_preview);

        contents = new ArrayList<Map<String, Object>>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        filename = intent.getStringExtra("file");
        Log.v(TAG, "filename - " + filename);
        String vcardString = getStringFromFile(filename);

        // parse the string
        VCardParser parser = new VCardParser();
        VDataBuilder builder = new VDataBuilder();
        boolean parsed;
        try {
            parsed = parser.parse(vcardString, "UTF-8", builder);
            if (!parsed) {
                Log.e(TAG, "Could not parse vCard file");
            }
        } catch (VCardException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get all parsed contacts
        List<VNode> pimContacts = builder.vNodeList;

        // do something for all the contacts
        contents.clear();
        for (VNode contact : pimContacts) {
            ArrayList<PropertyNode> props = contact.propList;
            Map<String, Object> map = new HashMap<String, Object>();
            StringBuilder sb = new StringBuilder();

            // contact name - FN property
            String name = null;
            for (PropertyNode prop : props) {
                if ("FN".equals(prop.propName)) {
                    name = prop.propValue;
                    Log.v(TAG, "name - " + name);
                    sb.append(getString(R.string.vcard_preview_name));
                    sb.append(": ");
                    sb.append(name);
                    sb.append('\n');
                    // break;
                }
            }

            // similarly for other properties (N, ORG, TEL, etc)
            // ...

            // contact name - TEL property
            String tel = null;
            for (PropertyNode prop : props) {
                if ("TEL".equals(prop.propName)) {
                    tel = prop.propValue;
                    Log.v(TAG, "tel - " + tel);
                    sb.append(getString(R.string.vcard_preview_tel));
                    sb.append(": ");
                    sb.append(replacePattern(tel, "(\\-)", ""));
                    sb.append('\n');
                    // break;
                    // break;
                }
            }

            // contact name - EMAIL property
            String email = null;
            for (PropertyNode prop : props) {
                if ("EMAIL".equals(prop.propName)) {
                    email = prop.propValue;
                    Log.v(TAG, "email - " + email);
                    sb.append(getString(R.string.vcard_preview_email));
                    sb.append(": ");
                    sb.append(email);
                    sb.append('\n');
                    // break;
                }
            }

            // contact name - ADR property
            String adr = null;
            for (PropertyNode prop : props) {
                if ("ADR".equals(prop.propName)) {
                    adr = prop.propValue;
                    Log.v(TAG, "adr - " + adr);
                    sb.append(getString(R.string.vcard_preview_adr));
                    sb.append(": ");
                    sb.append(replacePattern(adr, "(\\;)", ""));
                    sb.append('\n');
                    // break;
                }
            }

            // contact name - ORG property
            String org = null;
            for (PropertyNode prop : props) {
                if ("ORG".equals(prop.propName)) {
                    org = prop.propValue;
                    Log.v(TAG, "org - " + org);
                    sb.append(getString(R.string.vcard_preview_org));
                    sb.append(": ");
                    sb.append(replacePattern(org, "(\\;)", " "));
                    sb.append('\n');
                    // break;
                }
            }

            // contact name - URL property
            String url = null;
            for (PropertyNode prop : props) {
                if ("URL".equals(prop.propName)) {
                    url = prop.propValue;
                    Log.v(TAG, "url - " + url);
                    sb.append(getString(R.string.vcard_preview_url));
                    sb.append(": ");
                    sb.append(url);
                    sb.append('\n');
                    // break;
                }
            }

            // contact name - X-ESURFING-GROUP property
            String group = null;
            for (PropertyNode prop : props) {
                if ("X-ESURFING-GROUP".equals(prop.propName)) {
                    group = prop.propValue;
                    Log.v(TAG, "X-ESURFING-GROUP - " + group);
                    sb.append(getString(R.string.vcard_preview_group));
                    sb.append(": ");
                    sb.append(group);
                    sb.append('\n');
                    // break;
                }
            }
            map.put("TITLE", sb.toString());
            Log.v(TAG, sb.toString());

            Log.d(TAG, "Found contact: " + name);
            contents.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, (List<Map<String, Object>>) contents,
                R.layout.vcard_preview_list_item, new String[] { "TITLE" }, new int[] { R.id.listitem_title });

        getListView().setAdapter(adapter);
    }

    /** zzz */
    private String getStringFromFile(String filename) {
        if (filename != null) {
            File file = new File(filename);
            Log.i(TAG, "filename - " + filename);
            if (file.exists()) {
                FileInputStream fin;
                try {
                    fin = new FileInputStream(file);
                    int buffersize = fin.available();
                    byte buffer[] = new byte[buffersize];
                    fin.read(buffer);
                    fin.close();
                    String string = new String(buffer);

                    Log.v(TAG, string);
                    return string;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "file not exist");
                Toast.makeText(this, R.string.unknown, Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
        return null;
    }

    private String replacePattern(String origin, String pattern, String replace) {
        Log.i(TAG, "origin - " + origin);
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replace);
        }

        m.appendTail(sb);
        Log.i(TAG, "sb.toString() - " + sb.toString());
        return sb.toString();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 0, 0, R.string.cancel);
        menu.add(0, 1, 0, R.string.save);
        menu.findItem(0).setShowAsAction(1);
        menu.findItem(1).setShowAsAction(2);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
            Log.v(TAG, "cancel");
            finish();
            break;
        case 1:
            Log.v(TAG, "save");
            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("file://" + filename);
            i.setDataAndType(data, "text/vcard");
            startActivity(i);

            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
