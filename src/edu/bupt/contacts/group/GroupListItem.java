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
package edu.bupt.contacts.group;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * Meta-data for a contact group.  We load all groups associated with the contact's
 * constituent accounts.
 */
public final class GroupListItem {
    private final String mAccountName;
    private final String mAccountType;
    private final String mDataSet;
    private final long mGroupId;
    private final String mTitle;
    private final boolean mIsFirstGroupInAccount;
    private final int mMemberCount;

    public GroupListItem(String accountName, String accountType, String dataSet, long groupId,
            String title, boolean isFirstGroupInAccount, int memberCount) {
        mAccountName = accountName;
        mAccountType = accountType;
        mDataSet = dataSet;
        mGroupId = groupId;
        mTitle = title;
        mIsFirstGroupInAccount = isFirstGroupInAccount;
        mMemberCount = memberCount;
    }

    public String getAccountName() {
        return mAccountName;
    }

    public String getAccountType() {
        return mAccountType;
    }

    public String getDataSet() {
        return mDataSet;
    }

    public long getGroupId() {
        return mGroupId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getMemberCount() {
        return mMemberCount;
    }

    public boolean hasMemberCount() {
        return mMemberCount != -1;
    }

    public boolean isFirstGroupInAccount() {
        return mIsFirstGroupInAccount;
    }
}