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

package edu.bupt.contacts.group;

import edu.bupt.contacts.R;
import edu.bupt.contacts.model.AccountType;
import edu.bupt.contacts.model.AccountTypeManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

public class GroupDetailDisplayUtils {

    private GroupDetailDisplayUtils() {
        // Disallow explicit creation of this class.
    }

    public static View getNewGroupSourceView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.group_source_button, null);
    }

    public static void bindGroupSourceView(Context context, View view, String accountTypeString,
            String dataSet) {
        AccountTypeManager accountTypeManager = AccountTypeManager.getInstance(context);
        AccountType accountType = accountTypeManager.getAccountType(accountTypeString, dataSet);

        TextView label = (TextView) view.findViewById(android.R.id.title);
        if (label == null) {
            throw new IllegalStateException("Group source view must contain a TextView with id"
                    + "android.R.id.label");
        }
        label.setText(accountType.getViewGroupLabel(context));

        ImageView accountIcon = (ImageView) view.findViewById(android.R.id.icon);
        if (accountIcon == null) {
            throw new IllegalStateException("Group source view must contain an ImageView with id"
                    + "android.R.id.icon");
        }
        accountIcon.setImageDrawable(accountType.getDisplayIcon(context));
    }
}