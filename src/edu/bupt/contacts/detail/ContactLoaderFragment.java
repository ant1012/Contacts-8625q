/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package edu.bupt.contacts.detail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.bupt.contacts.ContactLoader;
import edu.bupt.contacts.ContactSaveService;
import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.R;
import edu.bupt.contacts.activities.MenuCalendarActivity;
import edu.bupt.contacts.activities.MenuHistoryActivity;
import edu.bupt.contacts.activities.MultiSelectExport;
import edu.bupt.contacts.activities.PersonInfo;
import edu.bupt.contacts.activities.ContactDetailActivity.FragmentKeyListener;
import edu.bupt.contacts.blacklist.BlacklistDBHelper;
import edu.bupt.contacts.blacklist.WhiteListDBHelper;
import edu.bupt.contacts.list.ContactMultiSelectAdapter;
import edu.bupt.contacts.list.ShortcutIntentBuilder;
import edu.bupt.contacts.list.ShortcutIntentBuilder.OnShortcutIntentCreatedListener;
import edu.bupt.contacts.msgring.MsgRingDBHelper;
import edu.bupt.contacts.util.PhoneCapabilityTester;
import edu.bupt.contacts.vcard.VCardComposer;

import com.android.internal.util.Objects;
import com.android.vcard.VCardConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * This is an invisible worker {@link Fragment} that loads the contact details
 * for the contact card. The data is then passed to the listener, who can then
 * pass the data to other {@link View}s.
 */
public class ContactLoaderFragment extends Fragment implements FragmentKeyListener {

    private static final String TAG = ContactLoaderFragment.class.getSimpleName();

    /** The launch code when picking a ringtone */
    private static final int REQUEST_CODE_PICK_RINGTONE = 1;
    private static final int REQUEST_CODE_PICK_MSGRING = 2;

    /** This is the Intent action to install a shortcut in the launcher. */
    private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    private boolean mOptionsMenuOptions;
    private boolean mOptionsMenuEditable;
    private boolean mOptionsMenuShareable;
    private boolean mOptionsMenuCanCreateShortcut;
    private boolean mSendToVoicemailState;
    private String mCustomRingtone;
    private String mCustomMsgRing;

    /**
     * This is a listener to the {@link ContactLoaderFragment} and will be
     * notified when the contact details have finished loading or if the user
     * selects any menu options.
     */
    public static interface ContactLoaderFragmentListener {
        /**
         * Contact was not found, so somehow close this fragment. This is raised
         * after a contact is removed via Menu/Delete
         */
        public void onContactNotFound();

        /**
         * Contact details have finished loading.
         */
        public void onDetailsLoaded(ContactLoader.Result result);

        /**
         * User decided to go to Edit-Mode
         */
        public void onEditRequested(Uri lookupUri);

        /**
         * User decided to delete the contact
         */
        public void onDeleteRequested(Uri lookupUri);

    }

    private static final int LOADER_DETAILS = 1;

    private static final String KEY_CONTACT_URI = "contactUri";
    private static final String LOADER_ARG_CONTACT_URI = "contactUri";

    private Context mContext;
    private Uri mLookupUri;
    private ContactLoaderFragmentListener mListener;

    private ContactLoader.Result mContactData;

    public ContactLoaderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLookupUri = savedInstanceState.getParcelable(KEY_CONTACT_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CONTACT_URI, mLookupUri);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        setHasOptionsMenu(true);
        // This is an invisible view. This fragment is declared in a layout, so
        // it can't be
        // "viewless". (i.e. can't return null here.)
        // See also the comment in the layout file.
        return inflater.inflate(R.layout.contact_detail_loader_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mLookupUri != null) {
            Bundle args = new Bundle();
            args.putParcelable(LOADER_ARG_CONTACT_URI, mLookupUri);
            getLoaderManager().initLoader(LOADER_DETAILS, args, mDetailLoaderListener);
        }
    }

    public void loadUri(Uri lookupUri) {
        if (Objects.equal(lookupUri, mLookupUri)) {
            // Same URI, no need to load the data again
            return;
        }

        mLookupUri = lookupUri;
        if (mLookupUri == null) {
            getLoaderManager().destroyLoader(LOADER_DETAILS);
            mContactData = null;
            if (mListener != null) {
                mListener.onDetailsLoaded(mContactData);
            }
        } else if (getActivity() != null) {
            Bundle args = new Bundle();
            args.putParcelable(LOADER_ARG_CONTACT_URI, mLookupUri);
            getLoaderManager().restartLoader(LOADER_DETAILS, args, mDetailLoaderListener);
        }
    }

    public void setListener(ContactLoaderFragmentListener value) {
        mListener = value;
    }

    /**
     * The listener for the detail loader
     */
    private final LoaderManager.LoaderCallbacks<ContactLoader.Result> mDetailLoaderListener = new LoaderCallbacks<ContactLoader.Result>() {
        @Override
        public Loader<ContactLoader.Result> onCreateLoader(int id, Bundle args) {
            Uri lookupUri = args.getParcelable(LOADER_ARG_CONTACT_URI);
            return new ContactLoader(mContext, lookupUri, true /* loadGroupMetaData */, true /* loadStreamItems */,
                    true /* load invitable account types */, true /* postViewNotification */);
        }

        @Override
        public void onLoadFinished(Loader<ContactLoader.Result> loader, ContactLoader.Result data) {
            if (!mLookupUri.equals(data.getRequestedUri())) {
                Log.e(TAG, "Different URI: requested=" + mLookupUri + "  actual=" + data);
                return;
            }

            if (data.isError()) {
                // This shouldn't ever happen, so throw an exception. The {@link
                // ContactLoader}
                // should log the actual exception.
                throw new IllegalStateException("Failed to load contact", data.getException());
            } else if (data.isNotFound()) {
                Log.i(TAG, "No contact found: " + ((ContactLoader) loader).getLookupUri());
                mContactData = null;
            } else {
                mContactData = data;
            }

            if (mListener != null) {
                if (mContactData == null) {
                    mListener.onContactNotFound();
                } else {
                    mListener.onDetailsLoaded(mContactData);
                }
            }
            // Make sure the options menu is setup correctly with the loaded
            // data.
            getActivity().invalidateOptionsMenu();
        }

        @Override
        public void onLoaderReset(Loader<ContactLoader.Result> loader) {
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.view_contact, menu);
    }

    public boolean isOptionsMenuChanged() {
        return mOptionsMenuOptions != isContactOptionsChangeEnabled() || mOptionsMenuEditable != isContactEditable()
                || mOptionsMenuShareable != isContactShareable()
                || mOptionsMenuCanCreateShortcut != isContactCanCreateShortcut();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mOptionsMenuOptions = isContactOptionsChangeEnabled();
        mOptionsMenuEditable = isContactEditable();
        mOptionsMenuShareable = isContactShareable();
        mOptionsMenuCanCreateShortcut = isContactCanCreateShortcut();
        Log.i("mContactData", "mContactData =" + mContactData);
        if (mContactData != null) {
            mSendToVoicemailState = mContactData.isSendToVoicemail();
            mCustomRingtone = mContactData.getCustomRingtone();
            mCustomMsgRing = mContactData.getCustomMsgRing();
            Log.i("mCustomMsgRing", "mCustomMsgRing =" + mCustomMsgRing);
        }

        // Hide telephony-related settings (ringtone, send to voicemail)
        // if we don't have a telephone
        final MenuItem optionsSendToVoicemail = menu.findItem(R.id.menu_send_to_voicemail);
        if (optionsSendToVoicemail != null) {
            optionsSendToVoicemail.setChecked(mSendToVoicemailState);
            optionsSendToVoicemail.setVisible(mOptionsMenuOptions);
        }
        final MenuItem optionsRingtone = menu.findItem(R.id.menu_set_ringtone);
        final MenuItem optionsMsgRing = menu.findItem(R.id.menu_set_msgring);
        if (optionsRingtone != null) {
            optionsRingtone.setVisible(mOptionsMenuOptions);
        }
        if (optionsMsgRing != null) {
            optionsMsgRing.setVisible(mOptionsMenuOptions);
        }

        final MenuItem editMenu = menu.findItem(R.id.menu_edit);
        editMenu.setVisible(mOptionsMenuEditable);

        final MenuItem deleteMenu = menu.findItem(R.id.menu_delete);
        deleteMenu.setVisible(mOptionsMenuEditable);

        final MenuItem shareMenu = menu.findItem(R.id.menu_share);
        shareMenu.setVisible(mOptionsMenuShareable);

        final MenuItem createContactShortcutMenu = menu.findItem(R.id.menu_create_contact_shortcut);
        createContactShortcutMenu.setVisible(mOptionsMenuCanCreateShortcut);
    }

    public boolean isContactOptionsChangeEnabled() {
        return mContactData != null && !mContactData.isDirectoryEntry() && PhoneCapabilityTester.isPhone(mContext);
    }

    public boolean isContactEditable() {
        return mContactData != null && !mContactData.isDirectoryEntry();
    }

    public boolean isContactShareable() {
        return mContactData != null && !mContactData.isDirectoryEntry();
    }

    public boolean isContactCanCreateShortcut() {
        return mContactData != null && !mContactData.isUserProfile() && !mContactData.isDirectoryEntry();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_edit: {
            if (mListener != null)
                mListener.onEditRequested(mLookupUri);

            break;
        }
        case R.id.menu_delete: {
            if (mListener != null)
                mListener.onDeleteRequested(mLookupUri);
            return true;
        }
        case R.id.menu_calendar: {
            Log.i("mLookupUri", "" + mLookupUri);
            String check_calendar = "" + mLookupUri;
            String shortmsg[] = check_calendar.split("/");
            Log.i("update", "" + shortmsg[6]);
            Bundle bundle = new Bundle();
            bundle.putString("check_calendar", shortmsg[6]);
            Intent intent = new Intent(mContext, MenuCalendarActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        case R.id.menu_history: {
            Log.i("mLookupUri", "" + mLookupUri);
            String check_calendar = "" + mLookupUri;
            String shortmsg[] = check_calendar.split("/");
            Log.i("update", "" + shortmsg[6]);
            Bundle bundle = new Bundle();
            bundle.putString("check_history", shortmsg[6]);
            Intent intent = new Intent(mContext, MenuHistoryActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        case R.id.menu_set_ringtone: {
            if (mContactData == null)
                return false;
            doPickRingtone();
            return true;
        }
        /** baoge */
        case R.id.menu_set_msgring: {
            if (mContactData == null)
                return false;
            doPickMsgRing();
            // msgUri();
            return true;
        }
        case R.id.menu_share: {
            if (mContactData == null)
                return false;

            final String lookupKey = mContactData.getLookupKey();
            Uri shareUri = Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, lookupKey);

            /** zzz */
            // if (mContactData.isUserProfile()) {
            // // User is sharing the profile. We don't want to force the
            // // receiver to have
            // // the highly-privileged READ_PROFILE permission, so we need to
            // // request a
            // // pre-authorized URI from the provider.
            // shareUri = getPreAuthorizedUri(shareUri);
            // }
            //
            // final Intent intent = new Intent(Intent.ACTION_SEND);
            // intent.setType(Contacts.CONTENT_VCARD_TYPE);
            // intent.putExtra(Intent.EXTRA_STREAM, shareUri);

            final int vcardType = VCardConfig.getVCardTypeFromString(getString(R.string.config_export_vcard_type));

            VCardComposer composer = null;
            composer = new VCardComposer(mContext, vcardType, true);

            // do query
            Cursor cursor = mContext.getContentResolver().query(mLookupUri, null, null, null, null);

            // init
            if (!composer.init(cursor)) {
                final String errorReason = composer.getErrorReason();
                Log.e(TAG, "initialization of vCard composer failed: " + errorReason);
                return false;
            }

            final int total = composer.getCount();
            if (total == 0) {
                Toast.makeText(mContext, R.string.share_error, Toast.LENGTH_SHORT).show();
                return false;
            }
            Log.i(TAG, "composer.getCount() - " + total);

            StringBuilder sb = new StringBuilder();
            while (!composer.isAfterLast()) {
                sb.append(composer.createOneEntry());
            }
            Log.i(TAG, sb.toString());
            File tempFile = null;
            try {
                tempFile = File.createTempFile("VCard-" + mContactData.getDisplayName(), ".vcf",
                        mContext.getExternalCacheDir());
                FileOutputStream fos = new FileOutputStream(tempFile);
                byte[] bytes = sb.toString().getBytes();
                fos.write(bytes);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // send
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/x-vcard");
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));

            // Launch chooser to share contact via
            final CharSequence chooseTitle = mContext.getText(R.string.share_via);
            final Intent chooseIntent = Intent.createChooser(i, chooseTitle);

            try {
                mContext.startActivity(chooseIntent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(mContext, R.string.share_error, Toast.LENGTH_SHORT).show();
            }

            // // Launch chooser to share contact via
            // final CharSequence chooseTitle = mContext
            // .getText(R.string.share_via);
            // final Intent chooseIntent = Intent.createChooser(intent,
            // chooseTitle);
            //
            // try {
            // mContext.startActivity(chooseIntent);
            // } catch (ActivityNotFoundException ex) {
            // Toast.makeText(mContext, R.string.share_error,
            // Toast.LENGTH_SHORT).show();
            // }
            return true;
        }
        case R.id.menu_send_to_voicemail: {
            // Update state and save
            mSendToVoicemailState = !mSendToVoicemailState;
            item.setChecked(mSendToVoicemailState);
            Intent intent = ContactSaveService.createSetSendToVoicemail(mContext, mLookupUri, mSendToVoicemailState);
            mContext.startService(intent);
            return true;
        }
        case R.id.menu_create_contact_shortcut: {
            // Create a launcher shortcut with this contact
            createLauncherShortcutWithContact();
            return true;
        }

        /** zzz */
        case R.id.menu_add_to_blacklist: {
            Log.d(TAG, "menu_add_to_blacklist");

            final String name = mContactData.getDisplayName();
            // Log.i(TAG, mContactData.getContentValues().toString());
            // Log.i(TAG, mContactData.getContentValues().get(2).toString());
            // Log.i(TAG,
            // mContactData.getContentValues().get(2).getAsString("data1"));
            //
            // final String phone = mContactData.getContentValues().get(2)
            // .getAsString("data1");

            // get phone number
            Uri uri = mContactData.getLookupUri();
            String phone = null;
            long contactId;

            Cursor c = mContext.getContentResolver().query(uri, null, null, null, null);
            int phoneIdx = 0;
            int idIdx = c.getColumnIndexOrThrow(Phone._ID);

            c.moveToNext();
            // phone = c.getString(phoneIdx);
            contactId = c.getLong(idIdx);

            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor p = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] { CommonDataKinds.Phone.NUMBER }, CommonDataKinds.Phone.CONTACT_ID + " =? ",
                        new String[] { String.valueOf(contactId) }, null);
                p.moveToNext();
                phoneIdx = p.getColumnIndexOrThrow(Phone.NUMBER);
                phone = p.getString(phoneIdx);
                p.close();
            }
            c.close();

            Log.i(TAG, "phone - " + phone);
            if (phone == null) {
                Toast.makeText(mContext, R.string.menu_add_to_blacklist_nophonenum, Toast.LENGTH_SHORT).show();
                return true;
            }

            final String phoneFinal = phone;

            new AlertDialog.Builder(mContext).setTitle(R.string.menu_add_to_blacklist)
                    .setMessage(mContext.getString(R.string.menu_add_to_blacklist_check, name))
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            BlacklistDBHelper mDBHelper;
                            mDBHelper = new BlacklistDBHelper(mContext);
                            mDBHelper.addPeople(name, phoneFinal);
                            Toast.makeText(mContext, R.string.menu_add_to_blacklist, Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(android.R.string.cancel, null).show();

            return true;
        }

        case R.id.menu_add_to_whitelist: {
            Log.d(TAG, "menu_add_to_whitelist");

            final String name = mContactData.getDisplayName();
            // Log.i(TAG, mContactData.getContentValues().toString());
            //
            // Log.i(TAG, mContactData.getContentValues().get(2).toString());
            // Log.i(TAG,
            // mContactData.getContentValues().get(2).getAsString("data1"));
            //
            // final String phone = mContactData.getContentValues().get(2)
            // .getAsString("data1");

            // get phone number
            Uri uri = mContactData.getLookupUri();
            String phone = null;
            long contactId;

            Cursor c = mContext.getContentResolver().query(uri, null, null, null, null);
            int phoneIdx = 0;
            int idIdx = c.getColumnIndexOrThrow(Phone._ID);

            c.moveToNext();
            // phone = c.getString(phoneIdx);
            contactId = c.getLong(idIdx);

            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor p = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] { CommonDataKinds.Phone.NUMBER }, CommonDataKinds.Phone.CONTACT_ID + " =? ",
                        new String[] { String.valueOf(contactId) }, null);
                p.moveToNext();
                phoneIdx = p.getColumnIndexOrThrow(Phone.NUMBER);
                phone = p.getString(phoneIdx);
                p.close();
            }
            c.close();

            Log.i(TAG, "phone - " + phone);
            if (phone == null) {
                Toast.makeText(mContext, R.string.menu_add_to_blacklist_nophonenum, Toast.LENGTH_SHORT).show();
                return true;
            }

            final String phoneFinal = phone;

            new AlertDialog.Builder(mContext).setTitle(R.string.menu_add_to_whitelist)
                    .setMessage(mContext.getString(R.string.menu_add_to_whitelist_check, name))
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            WhiteListDBHelper mDBHelper;
                            mDBHelper = new WhiteListDBHelper(mContext);
                            mDBHelper.addPeople(name, phoneFinal);
                            Toast.makeText(mContext, R.string.menu_add_to_whitelist, Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(android.R.string.cancel, null).show();

            return true;

        }
        /** zzz */
        case R.id.menu_share_as_text: {
            Log.v(TAG, "R.id.menu_share_as_text");
            shareAsText();
            return true;
        }
        /** zzz */
        case R.id.menu_edit_and_dial: {
            Log.v(TAG, "R.id.menu_edit_and_dial");
            Uri uri = mContactData.getLookupUri();
            String phone = null;
            long contactId;

            Cursor c = mContext.getContentResolver().query(uri, null, null, null, null);
            int phoneIdx = 0;
            int idIdx = c.getColumnIndexOrThrow(Phone._ID);

            c.moveToNext();
            // phone = c.getString(phoneIdx);
            contactId = c.getLong(idIdx);

            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor p = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] { CommonDataKinds.Phone.NUMBER }, CommonDataKinds.Phone.CONTACT_ID + " =? ",
                        new String[] { String.valueOf(contactId) }, null);
                p.moveToNext();
                phoneIdx = p.getColumnIndexOrThrow(Phone.NUMBER);
                phone = p.getString(phoneIdx);
                p.close();
            }
            c.close();
            startActivity(new Intent(Intent.ACTION_DIAL, ContactsUtils.getCallUri(phone)));
        }
        }
        return false;
    }

    /** zzz */
    // copied from qqq
    private void shareAsText() {
        ArrayList<String> number_selected = new ArrayList<String>();

        Uri contactData2 = mLookupUri;

        final Cursor c2 = mContext.getContentResolver().query(contactData2, null, null, null, null);
        if (c2.moveToFirst()) {

            String id = c2.getString(c2.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

            String name = c2.getString(c2.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            if (name != null) {
                number_selected.add("姓名： " + name);
            }

            Log.i(TAG, "id - " + id);

            Cursor emailCur = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
            while (emailCur.moveToNext()) {
                // This would allow you get several email addresses
                // if the email addresses were stored in an array
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                String emailType = emailCur.getString(emailCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                number_selected.add("Email: " + email);
                Log.i(TAG, "email: " + email + " type: " + emailType);

            }
            emailCur.close();

            String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] addrWhereParams = new String[] { id,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
            Cursor addrCur = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, addrWhere,
                    addrWhereParams, null);
            while (addrCur.moveToNext()) {
                String poBox = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                String street = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String city = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String state = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                String postalCode = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String country = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                String type = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                StringBuilder sb_location = new StringBuilder();

                if (country != null) {
                    sb_location.append(country);
                }

                if (state != null) {
                    sb_location.append(state);
                }

                if (city != null) {
                    sb_location.append(city);
                }

                if (street != null) {
                    sb_location.append(street);
                }

                // if(!((String)poBox).equals("")){
                // sb_location.append(poBox);
                // }
                //
                if (!sb_location.toString().equals("")) {

                    number_selected.add("地址： " + sb_location.toString());
                }

                Log.i(TAG, "street: " + street + " city: " + city + state + postalCode + country + type);

            }
            addrCur.close();

            String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] imWhereParams = new String[] { id, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE };
            Cursor imCur = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, imWhere,
                    imWhereParams, null);
            if (imCur.moveToFirst()) {
                String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                String imType;
                imType = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));

                number_selected.add("即时消息： " + imName);

                Log.i(TAG, "imName: " + imName + " imType: " + imType);

            }
            imCur.close();

            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] orgWhereParams = new String[] { id,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE };
            Cursor orgCur = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, orgWhere,
                    orgWhereParams, null);
            if (orgCur.moveToFirst()) {
                String orgName = orgCur.getString(orgCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                String title = orgCur.getString(orgCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

                number_selected.add("单位： " + orgName + " 职务：" + title);
                Log.i(TAG, "orgName: " + orgName + " title: " + title);

            }
            orgCur.close();

            String hasPhone = c2.getString(c2.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            Log.i(TAG, "hasPhone - " + hasPhone);

            if (hasPhone.equalsIgnoreCase("1")) {
                Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                phones.moveToFirst();
                // ArrayList<String> number_selected = new
                // ArrayList<String>();

                do {

                    number_selected.add("电话： " + phones.getString(phones.getColumnIndex("data1")));

                } while (phones.moveToNext());

                Log.v("ComposeMessageActivity for test", number_selected.toString());

                final String[] number_a = new String[number_selected.size()];
                final boolean[] number_b = new boolean[number_selected.size()];
                for (int i = 0; i < number_selected.size(); i++) {
                    number_a[i] = number_selected.get(i) + "\n";
                    number_b[i] = false;
                }
                // String[]number_array= (String[])
                // number_selected.toArray();

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMultiChoiceItems(number_a, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int which, boolean isChecked) {
                        // TODO Auto-generated method stub

                        Log.v("ComposeMessageActivity for test", "before number_b[which]: " + number_b[which]);

                        number_b[which] = !number_b[which];
                        Log.v("ComposeMessageActivity for test", "after number_b[which]: " + number_b[which]);

                    }

                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        StringBuilder sb_text = new StringBuilder();

                        for (int i = 0; i < number_b.length; i++) {

                            if (number_b[i]) {
                                Log.v("ComposeMessageActivity for test", "number_a[i]: " + number_a[i]);

                                sb_text.append(number_a[i]);
                                // number_a
                            }

                        }

                        /** zzz */
                        Log.i(TAG, sb_text.toString());

                        Uri uri = Uri.parse("smsto:");
                        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                        it.putExtra("sms_body", sb_text.toString());
                        startActivity(it);

                        dialog.dismiss();

                    }

                }).setNegativeButton("取消", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub

                        dialog.dismiss();

                    }

                });

                // builder.setSingleChoiceItems(number_a, 0, new
                // OnClickListener(){
                //
                // @Override
                // public void onClick(DialogInterface arg0, int position) {
                // // TODO Auto-generated method stub
                //
                //
                //
                //
                //
                //
                // String number_single =number_a[position];
                //
                // String name = c2
                // .getString(c2
                // .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //
                //
                // mTextEditor.setText("姓名： "+name+" 电话号码： "+number_single);
                //
                //
                // arg0.dismiss();
                // }
                //
                // });

                builder.create().show();

                // else{
                //
                //
                // String number_single =phones.getString(phones
                // .getColumnIndex("data1"));
                //
                // String name = c2
                // .getString(c2
                // .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //
                //
                // mTextEditor.setText("姓名： "+name+" 电话号码： "+number_single);
                // }

                // String numbers = phones.getString(phones
                // .getColumnIndex("data1"));

                // mRecipientsEditor.setText(numbers);
                // mTextEditor.requestFocus();
                // Log.i(TAG, "number - " + numbers);
            }

            // Log.i(TAG, "name - " + name);

        }
    }

    /**
     * Creates a launcher shortcut with the current contact.
     */
    private void createLauncherShortcutWithContact() {
        // Hold the parent activity of this fragment in case this fragment is
        // destroyed
        // before the callback to onShortcutIntentCreated(...)
        final Activity parentActivity = getActivity();

        ShortcutIntentBuilder builder = new ShortcutIntentBuilder(parentActivity,
                new OnShortcutIntentCreatedListener() {

                    @Override
                    public void onShortcutIntentCreated(Uri uri, Intent shortcutIntent) {
                        // Broadcast the shortcutIntent to the launcher to
                        // create a
                        // shortcut to this contact
                        shortcutIntent.setAction(ACTION_INSTALL_SHORTCUT);
                        parentActivity.sendBroadcast(shortcutIntent);

                        // Send a toast to give feedback to the user that a
                        // shortcut to this
                        // contact was added to the launcher.
                        Toast.makeText(parentActivity, R.string.createContactShortcutSuccessful, Toast.LENGTH_SHORT)
                                .show();
                    }

                });
        builder.createContactShortcutIntent(mLookupUri);
    }

    /**
     * Calls into the contacts provider to get a pre-authorized version of the
     * given URI.
     */
    private Uri getPreAuthorizedUri(Uri uri) {
        Bundle uriBundle = new Bundle();
        uriBundle.putParcelable(ContactsContract.Authorization.KEY_URI_TO_AUTHORIZE, uri);
        Bundle authResponse = mContext.getContentResolver().call(ContactsContract.AUTHORITY_URI,
                ContactsContract.Authorization.AUTHORIZATION_METHOD, null, uriBundle);
        if (authResponse != null) {
            return (Uri) authResponse.getParcelable(ContactsContract.Authorization.KEY_AUTHORIZED_URI);
        } else {
            return uri;
        }
    }

    @Override
    public boolean handleKeyDown(int keyCode) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DEL: {
            if (mListener != null)
                mListener.onDeleteRequested(mLookupUri);
            return true;
        }
        }
        return false;
    }

    private void doPickRingtone() {

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        // Allow user to pick 'Default'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        // Show only ringtones
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        // Don't show 'Silent'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

        Uri ringtoneUri;
        if (mCustomRingtone != null) {
            ringtoneUri = Uri.parse(mCustomRingtone);
        } else {
            // Otherwise pick default ringtone Uri so that something is
            // selected.
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        // Put checkmark next to the current ringtone for this contact
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);

        // Launch!
        startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
    }

    /** baoge */
    private void doPickMsgRing() {

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        // // Allow user to pick 'Default'
        // intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        // Show only ringtones
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        // Don't show 'Silent'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

        /** zzz */
        Uri selectedRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(mContext,
                RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri);

        // Uri msgringUri;
        // Log.i("mCustomMsgRing",""+mCustomMsgRing);
        // if (mCustomMsgRing != null) {
        //
        // msgringUri = Uri.parse(mCustomMsgRing);
        // } else {
        // // Otherwise pick default ringtone Uri so that something is selected.
        // msgringUri =
        // RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // }

        // Put checkmark next to the current ringtone for this contact
        // intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
        // msgringUri);

        // Launch!
        startActivityForResult(intent, REQUEST_CODE_PICK_MSGRING);
    }

    private void msgUri() {
        Uri msgringUri;
        Log.i("mCustomMsgRing", "" + mCustomMsgRing);
        if (mCustomMsgRing != null) {

            msgringUri = Uri.parse(mCustomMsgRing);
        } else {
            // Otherwise pick default ringtone Uri so that something is
            // selected.
            msgringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        String msg = "" + mLookupUri;
        String shortmsg[] = msg.split("/");
        Log.i("update", "" + shortmsg[6]);
        ContentValues values = new ContentValues();
        values.put(RawContacts.SOURCE_ID, "" + msgringUri); // "content://media/internal/audio/media/31"
        mContext.getContentResolver().update(RawContacts.CONTENT_URI, values, "_id=" + shortmsg[6], null);
        // cur.moveToFirst();
        // while(cur.getCount() > cur.getPosition()) {
        // String raw_id = cur.getString(cur.getColumnIndex(RawContacts._ID));
        //
        // // if(id.equals("2176")){
        // // ; //msg = cur.getString(cur.getColumnIndex(RawContacts.SOURCE_ID))
        // // }
        //
        // cur.moveToNext();
        // }
        // cur.close();
        // return msg;
    }

    private String fetchMsgUri() {
        String msg = null;
        String msg0 = "" + mLookupUri;
        String shortmsg[] = msg0.split("/");
        Cursor cur = mContext.getContentResolver().query(RawContacts.CONTENT_URI, null, null, null, null);
        cur.moveToFirst();
        while (cur.getCount() > cur.getPosition()) {
            String raw_id = cur.getString(cur.getColumnIndex(RawContacts._ID));

            if (raw_id.equals(shortmsg[6])) {
                msg = cur.getString(cur.getColumnIndex(RawContacts.SOURCE_ID)); //
            }

            cur.moveToNext();
        }
        cur.close();
        return msg;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
        case REQUEST_CODE_PICK_RINGTONE: {
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Log.i("pickedUri", "" + pickedUri);
            handleRingtonePicked(pickedUri);
            break;
        }
        case REQUEST_CODE_PICK_MSGRING: {
            // msgUri();
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);// fetchMsgUri()
            Log.i("RingtoneManager", pickedUri + ";");
            handleMsgRingPicked(pickedUri);
            // String msg = "" + mLookupUri;
            // String shortmsg[] = msg.split("/");
            // Log.i("update", "" + shortmsg[6]);
            // ContentValues values = new ContentValues();
            // values.put(RawContacts.SOURCE_ID, "" + pickedUri); //
            // "content://media/internal/audio/media/31"
            // mContext.getContentResolver().update(RawContacts.CONTENT_URI,
            // values, "_id=" + shortmsg[6], null);
            break;
        }
        }
    }

    private void handleRingtonePicked(Uri pickedUri) {
        if (pickedUri == null || RingtoneManager.isDefault(pickedUri)) {
            mCustomRingtone = null;
        } else {
            mCustomRingtone = pickedUri.toString();
        }
        Intent intent = ContactSaveService.createSetRingtone(mContext, mLookupUri, mCustomRingtone);
        mContext.startService(intent);
    }

    /** baoge */
    // modified by zzz
    private void handleMsgRingPicked(Uri pickedUri) {
        if (pickedUri == null || RingtoneManager.isDefault(pickedUri)) {
            mCustomMsgRing = null;
        } else {
            mCustomMsgRing = pickedUri.toString();
        }
        // Intent intent = ContactSaveService.createSetMsgRing(mContext,
        // mLookupUri, mCustomMsgRing);
        // mContext.startService(intent);

        Log.i(TAG, "handleMsgRingPicked - " + pickedUri);
        // Cursor c = mContext.getContentResolver().query(mLookupUri, null,
        // null, null, null);
        // int idIdx = c.getColumnIndexOrThrow(Phone._ID);
        //
        // c.moveToNext();
        // long contactId = c.getLong(idIdx);

        final long contactId = ContentUris.parseId(mLookupUri);

        MsgRingDBHelper dbhelper = new MsgRingDBHelper(mContext, 1);
        dbhelper.setRing(String.valueOf(contactId), mCustomMsgRing);
    }

    /** Toggles whether to load stream items. Just for debugging */
    public void toggleLoadStreamItems() {
        Loader<ContactLoader.Result> loaderObj = getLoaderManager().getLoader(LOADER_DETAILS);
        ContactLoader loader = (ContactLoader) loaderObj;
        loader.setLoadStreamItems(!loader.getLoadStreamItems());
    }

    /** Returns whether to load stream items. Just for debugging */
    public boolean getLoadStreamItems() {
        Loader<ContactLoader.Result> loaderObj = getLoaderManager().getLoader(LOADER_DETAILS);
        ContactLoader loader = (ContactLoader) loaderObj;
        return loader != null && loader.getLoadStreamItems();
    }
}
