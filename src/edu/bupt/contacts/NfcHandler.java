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
 * limitations under the License.
 */

package edu.bupt.contacts;

import edu.bupt.contacts.detail.ContactDetailFragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Profile;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
  * This class implements sharing the currently displayed
  * contact to another device using NFC. NFC sharing is only
  * enabled when the activity is in the foreground and resumed.
  * When an NFC link is established, {@link #createMessage}
  * will be called to create the data to be sent over the link,
  * which is a vCard in this case.
  */
public class NfcHandler implements NfcAdapter.CreateNdefMessageCallback {

    private static final String TAG = "ContactNfcHandler";
    private static final String PROFILE_LOOKUP_KEY = "profile";
    private final ContactDetailFragment mContactFragment;

    public static void register(Activity activity, ContactDetailFragment contactFragment) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(activity.getApplicationContext());
        if (adapter == null) {
            return;  // NFC not available on this device
        }
        adapter.setNdefPushMessageCallback(new NfcHandler(contactFragment), activity);
    }

    public NfcHandler(ContactDetailFragment contactFragment) {
        mContactFragment = contactFragment;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        // Get the current contact URI
        Uri contactUri = mContactFragment.getUri();
        ContentResolver resolver = mContactFragment.getActivity().getContentResolver();
        if (contactUri != null) {
            final String lookupKey = Uri.encode(contactUri.getPathSegments().get(2));
            final Uri shareUri;
            // TODO find out where to get this constant from, or find another way
            // of determining this.
            if (lookupKey.equals(PROFILE_LOOKUP_KEY)) {
                shareUri = Profile.CONTENT_VCARD_URI.buildUpon().
                appendQueryParameter(Contacts.QUERY_PARAMETER_VCARD_NO_PHOTO, "true").
                build();
            } else {
                shareUri = Contacts.CONTENT_VCARD_URI.buildUpon().
                appendPath(lookupKey).
                appendQueryParameter(Contacts.QUERY_PARAMETER_VCARD_NO_PHOTO, "true").
                build();
            }
            ByteArrayOutputStream ndefBytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int r;
            try {
                InputStream vcardInputStream = resolver.openInputStream(shareUri);
                while ((r = vcardInputStream.read(buffer)) > 0) {
                    ndefBytes.write(buffer, 0, r);
                }

                NdefRecord record = NdefRecord.createMime("text/x-vcard", ndefBytes.toByteArray());
                return new NdefMessage(record);
            } catch (IOException e) {
                Log.e(TAG, "IOException creating vcard.");
                return null;
            }
        } else {
            Log.w(TAG, "No contact URI to share.");
            return null;
        }
    }
}
