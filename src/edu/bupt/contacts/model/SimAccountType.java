/*
 * Copyright (C) 2009 The Android Open Source Project
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

package edu.bupt.contacts.model;

import com.google.android.collect.Lists;

import edu.bupt.contacts.R;
import edu.bupt.contacts.model.AccountType.DefinitionException;
import edu.bupt.contacts.model.AccountType.EditField;
import edu.bupt.contacts.model.BaseAccountType.SimpleInflater;
import edu.bupt.contacts.test.NeededForTesting;
import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.util.Log;

public class SimAccountType extends BaseAccountType {
    private static final String TAG = "SimAccountType";

    private SimAccountType(Context context, String resPackageName) {
        this.accountType = null;
        this.dataSet = null;
        this.titleRes = R.string.account_phone;
        this.iconRes = R.mipmap.ic_launcher_contacts;

        // Note those are only set for unit tests.
        this.resourcePackageName = resPackageName;
        this.syncAdapterPackageName = resPackageName;

        try {
            addDataKindStructuredName(context);
            addDataKindDisplayName(context);
            addDataKindPhoneticName(context);
            addDataKindPhone(context);

            mIsInitialized = true;
        } catch (DefinitionException e) {
            Log.e(TAG, "Problem building account type", e);
        }
    }

    public SimAccountType(Context context) {
        this(context, null);
    }

    @Override
    protected DataKind addDataKindDisplayName(Context context) throws DefinitionException {
        DataKind kind = addKind(new DataKind(DataKind.PSEUDO_MIME_TYPE_DISPLAY_NAME,
                R.string.nameLabelsGroup, -1, true, R.layout.text_fields_editor_view));
        kind.actionHeader = new SimpleInflater(R.string.nameLabelsGroup);
        kind.actionBody = new SimpleInflater(Nickname.NAME);
        kind.typeOverallMax = 1;

        kind.fieldList = Lists.newArrayList();
        kind.fieldList.add(new EditField(StructuredName.DISPLAY_NAME,
                R.string.full_name, FLAGS_PERSON_NAME).setShortForm(true));

        return kind;
    }

    /**
     * Used to compare with an {@link ExternalAccountType} built from a test contacts.xml.
     * In order to build {@link DataKind}s with the same resource package name,
     * {@code resPackageName} is injectable.
     */
    @NeededForTesting
    static AccountType createWithPackageNameForTest(Context context, String resPackageName) {
        return new SimAccountType(context, resPackageName);
    }

    @Override
    public boolean areContactsWritable() {
        Log.d(TAG, "areContactsWritable");
        return true;
    }
}
