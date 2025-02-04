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
package edu.bupt.contacts;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.ContactsContract.Groups;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * Group loader for the group list that includes details such as the number of contacts per group
 * and number of groups per account. This list is sorted by account type, account name, where the
 * group names are in alphabetical order. Note that the list excludes default, favorite, and deleted
 * groups.
 */
public final class GroupListLoader extends CursorLoader {

    private final static String[] COLUMNS = new String[] {
        Groups.ACCOUNT_NAME,
        Groups.ACCOUNT_TYPE,
        Groups.DATA_SET,
        Groups._ID,
        Groups.TITLE,
        Groups.SUMMARY_COUNT,
    };

    public final static int ACCOUNT_NAME = 0;
    public final static int ACCOUNT_TYPE = 1;
    public final static int DATA_SET = 2;
    public final static int GROUP_ID = 3;
    public final static int TITLE = 4;
    public final static int MEMBER_COUNT = 5;

    private static final Uri GROUP_LIST_URI = Groups.CONTENT_SUMMARY_URI;

    public GroupListLoader(Context context) {
        super(context, GROUP_LIST_URI, COLUMNS, Groups.ACCOUNT_TYPE + " NOT NULL AND "
                + Groups.ACCOUNT_NAME + " NOT NULL AND " + Groups.AUTO_ADD + "=0 AND " +
                Groups.FAVORITES + "=0 AND " + Groups.DELETED + "=0", null,
                Groups.ACCOUNT_TYPE + ", " + Groups.ACCOUNT_NAME + ", " + Groups.DATA_SET + ", " +
                Groups.TITLE + " COLLATE LOCALIZED ASC");
    }
}
