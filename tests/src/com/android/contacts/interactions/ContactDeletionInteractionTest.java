/*
 * Copyright (C) 2010 The Android Open Source Project
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

package edu.bupt.contacts.interactions;

import edu.bupt.contacts.ContactsApplication;
import edu.bupt.contacts.R;
import edu.bupt.contacts.model.AccountType;
import edu.bupt.contacts.model.AccountTypeManager;
import edu.bupt.contacts.model.BaseAccountType;
import edu.bupt.contacts.test.FragmentTestActivity;
import edu.bupt.contacts.test.InjectedServices;
import edu.bupt.contacts.tests.mocks.ContactsMockContext;
import edu.bupt.contacts.tests.mocks.MockAccountTypeManager;
import edu.bupt.contacts.tests.mocks.MockContentProvider;
import edu.bupt.contacts.tests.mocks.MockContentProvider.Query;
import edu.bupt.contacts.util.IntegrationTestUtils;

import android.content.ContentUris;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Entity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Tests for {@link ContactDeletionInteraction}.
 *
 * Running all tests:
 *
 *   runtest contacts
 * or
 *   adb shell am instrument \
 *     -w edu.bupt.contacts.tests/android.test.InstrumentationTestRunner
 */
@SmallTest
public class ContactDeletionInteractionTest
        extends ActivityInstrumentationTestCase2<FragmentTestActivity> {

    static {
        // AsyncTask class needs to be initialized on the main thread.
        AsyncTask.init();
    }

    private static final Uri CONTACT_URI = ContentUris.withAppendedId(Contacts.CONTENT_URI, 13);
    private static final Uri ENTITY_URI = Uri.withAppendedPath(
            CONTACT_URI, Entity.CONTENT_DIRECTORY);

    public static final String WRITABLE_ACCOUNT_TYPE = "writable";
    public static final String READONLY_ACCOUNT_TYPE = "readonly";

    private ContactsMockContext mContext;
    private MockContentProvider mContactsProvider;
    private ContactDeletionInteraction mFragment;
    private IntegrationTestUtils mUtils;

    public ContactDeletionInteractionTest() {
        super(FragmentTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // This test requires that the screen be turned on.
        mUtils = new IntegrationTestUtils(getInstrumentation());
        mUtils.acquireScreenWakeLock(getInstrumentation().getTargetContext());

        mContext = new ContactsMockContext(getInstrumentation().getTargetContext());
        InjectedServices services = new InjectedServices();
        services.setContentResolver(mContext.getContentResolver());

        AccountType readOnlyAccountType = new BaseAccountType() {
            @Override
            public boolean areContactsWritable() {
                return false;
            }
        };
        readOnlyAccountType.accountType = READONLY_ACCOUNT_TYPE;

        AccountType writableAccountType = new BaseAccountType() {
            @Override
            public boolean areContactsWritable() {
                return true;
            }
        };
        writableAccountType.accountType = WRITABLE_ACCOUNT_TYPE;

        services.setSystemService(AccountTypeManager.ACCOUNT_TYPE_SERVICE,
                new MockAccountTypeManager(
                        new AccountType[] { writableAccountType, readOnlyAccountType }, null));
        ContactsApplication.injectServices(services);
        mContactsProvider = mContext.getContactsProvider();
    }

    @Override
    protected void tearDown() throws Exception {
        ContactsApplication.injectServices(null);
        mUtils.releaseScreenWakeLock();
        super.tearDown();
    }

    public void testSingleWritableRawContact() {
        expectQuery().returnRow(1, WRITABLE_ACCOUNT_TYPE, null, 13, "foo");
        assertWithMessageId(R.string.deleteConfirmation);
    }

    public void testReadOnlyRawContacts() {
        expectQuery().returnRow(1, READONLY_ACCOUNT_TYPE, null, 13, "foo");
        assertWithMessageId(R.string.readOnlyContactWarning);
    }

    public void testMixOfWritableAndReadOnlyRawContacts() {
        expectQuery()
                .returnRow(1, WRITABLE_ACCOUNT_TYPE, null, 13, "foo")
                .returnRow(2, READONLY_ACCOUNT_TYPE, null, 13, "foo");
        assertWithMessageId(R.string.readOnlyContactDeleteConfirmation);
    }

    public void testMultipleWritableRawContacts() {
        expectQuery()
                .returnRow(1, WRITABLE_ACCOUNT_TYPE, null, 13, "foo")
                .returnRow(2, WRITABLE_ACCOUNT_TYPE, null, 13, "foo");
        assertWithMessageId(R.string.multipleContactDeleteConfirmation);
    }

    private Query expectQuery() {
        return mContactsProvider.expectQuery(ENTITY_URI).withProjection(
                Entity.RAW_CONTACT_ID, Entity.ACCOUNT_TYPE, Entity.DATA_SET, Entity.CONTACT_ID,
                Entity.LOOKUP_KEY);
    }

    private void assertWithMessageId(int messageId) {
        final FragmentTestActivity activity = getActivity();

        final TestLoaderManager mockLoaderManager = new TestLoaderManager();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mFragment = ContactDeletionInteraction.startWithTestLoaderManager(
                        activity, CONTACT_URI, false, mockLoaderManager);
            }
        });

        getInstrumentation().waitForIdleSync();

        mockLoaderManager.waitForLoaders(R.id.dialog_delete_contact_loader_id);

        getInstrumentation().waitForIdleSync();

        mContext.verify();

        assertEquals(messageId, mFragment.mMessageId);
    }
}
