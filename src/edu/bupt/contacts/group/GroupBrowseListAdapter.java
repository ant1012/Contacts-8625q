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

import edu.bupt.contacts.ContactPhotoManager;
import edu.bupt.contacts.GroupListLoader;
import edu.bupt.contacts.R;
import edu.bupt.contacts.blacklist.WhiteListDBHelper;
import edu.bupt.contacts.model.AccountType;
import edu.bupt.contacts.model.AccountTypeManager;

import com.android.internal.util.Objects;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Groups;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 联系人应用的群组页面的列表Adapter
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，有修改
 * 
 * */

/**
 * Adapter to populate the list of groups.
 */
public class GroupBrowseListAdapter extends BaseAdapter {
    private final String TAG = "GroupBrowseListAdapter";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final AccountTypeManager mAccountTypeManager;

    private Cursor mCursor;

    private boolean mSelectionVisible;
    private Uri mSelectedGroupUri;

    public GroupBrowseListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mAccountTypeManager = AccountTypeManager.getInstance(mContext);
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;

        // If there's no selected group already and the cursor is valid, then by
        // default, select the
        // first group
        if (mSelectedGroupUri == null && cursor != null && cursor.getCount() > 0) {
            GroupListItem firstItem = getItem(0);
            long groupId = (firstItem == null) ? null : firstItem.getGroupId();
            mSelectedGroupUri = getGroupUriFromId(groupId);
        }

        notifyDataSetChanged();
    }

    public int getSelectedGroupPosition() {
        if (mSelectedGroupUri == null || mCursor == null || mCursor.getCount() == 0) {
            return -1;
        }

        int index = 0;
        mCursor.moveToPosition(-1);
        while (mCursor.moveToNext()) {
            long groupId = mCursor.getLong(GroupListLoader.GROUP_ID);
            Uri uri = getGroupUriFromId(groupId);
            if (mSelectedGroupUri.equals(uri)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void setSelectionVisible(boolean flag) {
        mSelectionVisible = flag;
    }

    public void setSelectedGroup(Uri groupUri) {
        mSelectedGroupUri = groupUri;
    }

    private boolean isSelectedGroup(Uri groupUri) {
        return mSelectedGroupUri != null && mSelectedGroupUri.equals(groupUri);
    }

    public Uri getSelectedGroup() {
        return mSelectedGroupUri;
    }

    @Override
    public int getCount() {
        /** zzz */
        // zzz 如果白名单显示到群组，群组列表就增加了一项(通讯录功能9)
        SharedPreferences sp = mContext.getSharedPreferences("blacklist_pref", 0);
        if (sp.getBoolean("show_as_group", false)) {
            return mCursor == null ? 1 : mCursor.getCount() + 1;
        }

        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // @Override
    // public GroupListItem getItem(int position) {
    // if (mCursor == null || mCursor.isClosed() ||
    // !mCursor.moveToPosition(position)) {
    // return null;
    // }
    // String accountName = mCursor.getString(GroupListLoader.ACCOUNT_NAME);
    // String accountType = mCursor.getString(GroupListLoader.ACCOUNT_TYPE);
    // String dataSet = null;
    // long groupId = 0;
    // String title = null;
    // int memberCount = 0;
    // String previousGroupAccountName;
    // String previousGroupAccountType;
    // String previousGroupDataSet;
    // boolean isFirstGroupInAccount = true;
    // if("PHONE".equals(accountName) &&
    // "com.android.localphone".equals(accountType)){
    // dataSet = mCursor.getString(GroupListLoader.DATA_SET);
    // groupId = mCursor.getLong(GroupListLoader.GROUP_ID);
    // title = mCursor.getString(GroupListLoader.TITLE);
    // memberCount = mCursor.getInt(GroupListLoader.MEMBER_COUNT);
    //
    // // Figure out if this is the first group for this account name / account
    // type pair by
    // // checking the previous entry. This is to determine whether or not we
    // need to display an
    // // account header in this item.
    // int previousIndex = position - 1;
    // // boolean isFirstGroupInAccount = true;
    // if (previousIndex >= 0 && mCursor.moveToPosition(previousIndex)) {
    // previousGroupAccountName =
    // mCursor.getString(GroupListLoader.ACCOUNT_NAME);
    // previousGroupAccountType =
    // mCursor.getString(GroupListLoader.ACCOUNT_TYPE);
    // previousGroupDataSet = mCursor.getString(GroupListLoader.DATA_SET);
    //
    // if (accountName.equals(previousGroupAccountName) &&
    // accountType.equals(previousGroupAccountType) &&
    // Objects.equal(dataSet, previousGroupDataSet)) {
    // isFirstGroupInAccount = false;
    // }
    // }
    // }
    //
    //
    // return new GroupListItem(accountName, accountType, dataSet, groupId,
    // title,
    // isFirstGroupInAccount, memberCount);
    // }

    @Override
    public GroupListItem getItem(int position) {

        /** zzz */
        Log.v(TAG, "position - " + position);

        if (mCursor == null || mCursor.isClosed() || !mCursor.moveToPosition(position)) {

            /** zzz */
            // zzz 如果白名单显示到群组，最后一项是白名单的入口(通讯录功能9)
            if (position == getCount() - 1) {
                Log.v(TAG, "position == getCount() - 1");
                return new GroupListItem("accountName", "accountType", "dataSet", -1,
                        mContext.getString(R.string.white_list), true, position);
            }

            return null;
        }
        String accountName = mCursor.getString(GroupListLoader.ACCOUNT_NAME);
        String accountType = mCursor.getString(GroupListLoader.ACCOUNT_TYPE);
        String dataSet = mCursor.getString(GroupListLoader.DATA_SET);
        long groupId = mCursor.getLong(GroupListLoader.GROUP_ID);
        String title = mCursor.getString(GroupListLoader.TITLE);
        int memberCount = mCursor.getInt(GroupListLoader.MEMBER_COUNT);
        Log.v(TAG, "Account " + accountName + ";" + accountType);

        // Figure out if this is the first group for this account name / account
        // type pair by
        // checking the previous entry. This is to determine whether or not we
        // need to display an
        // account header in this item.
        int previousIndex = position - 1;
        boolean isFirstGroupInAccount = true;
        if (previousIndex >= 0 && mCursor.moveToPosition(previousIndex)) {
            String previousGroupAccountName = mCursor.getString(GroupListLoader.ACCOUNT_NAME);
            String previousGroupAccountType = mCursor.getString(GroupListLoader.ACCOUNT_TYPE);
            String previousGroupDataSet = mCursor.getString(GroupListLoader.DATA_SET);

            if (accountName.equals(previousGroupAccountName) && accountType.equals(previousGroupAccountType)
                    && Objects.equal(dataSet, previousGroupDataSet)) {
                isFirstGroupInAccount = false;
            }
        }

        return new GroupListItem(accountName, accountType, dataSet, groupId, title, isFirstGroupInAccount, memberCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /** zzz */
        Log.v(TAG, "position - " + position);

        GroupListItem entry = getItem(position);
        View result;
        GroupListItemViewCache viewCache;
        if (convertView != null) {
            result = convertView;
            viewCache = (GroupListItemViewCache) result.getTag();
        } else {
            result = mLayoutInflater.inflate(R.layout.group_browse_list_item, parent, false);
            viewCache = new GroupListItemViewCache(result);
            result.setTag(viewCache);
        }

        // Add a header if this is the first group in an account and hide the
        // divider
        if (entry.isFirstGroupInAccount()) {
            bindHeaderView(entry, viewCache);
            viewCache.accountHeader.setVisibility(View.VISIBLE);
            viewCache.divider.setVisibility(View.GONE);
            if (position == 0) {
                // Have the list's top padding in the first header.
                //
                // This allows the ListView to show correct fading effect on
                // top.
                // If we have topPadding in the ListView itself, an
                // inappropriate padding is
                // inserted between fading items and the top edge.
                viewCache.accountHeaderExtraTopPadding.setVisibility(View.VISIBLE);
            } else {
                viewCache.accountHeaderExtraTopPadding.setVisibility(View.GONE);
            }
        } else {
            viewCache.accountHeader.setVisibility(View.GONE);
            viewCache.divider.setVisibility(View.VISIBLE);
            viewCache.accountHeaderExtraTopPadding.setVisibility(View.GONE);
        }

        // Bind the group data
        /** zzz */
        // zzz 如果白名单显示到群组，最后一项是白名单的入口(通讯录功能9)
        if (entry.getGroupId() == -1) { // white list
            viewCache.accountType.setText(R.string.white_list);
            viewCache.accountName.setText("");
            viewCache.groupTitle.setText(entry.getTitle());

            int count = 0;
            WhiteListDBHelper mDBHelper = new WhiteListDBHelper(mContext);
            count = mDBHelper.getCount();

            String memberCountString = mContext.getResources().getQuantityString(
                    R.plurals.group_list_num_contacts_in_group, count, count);
            viewCache.groupMemberCount.setText(memberCountString);
        } else {
            Uri groupUri = getGroupUriFromId(entry.getGroupId());
            String memberCountString = mContext.getResources().getQuantityString(
                    R.plurals.group_list_num_contacts_in_group, entry.getMemberCount(), entry.getMemberCount());
            viewCache.setUri(groupUri);
            viewCache.groupTitle.setText(entry.getTitle());
            viewCache.groupMemberCount.setText(memberCountString);

            if (mSelectionVisible) {
                result.setActivated(isSelectedGroup(groupUri));
            }
        }

        return result;
    }

    private void bindHeaderView(GroupListItem entry, GroupListItemViewCache viewCache) {
        AccountType accountType = mAccountTypeManager.getAccountType(entry.getAccountType(), entry.getDataSet());
        viewCache.accountType.setText(accountType.getDisplayLabel(mContext).toString());
        viewCache.accountName.setText(entry.getAccountName());
    }

    private static Uri getGroupUriFromId(long groupId) {
        return ContentUris.withAppendedId(Groups.CONTENT_URI, groupId);
    }

    /**
     * Cache of the children views of a contact detail entry represented by a
     * {@link GroupListItem}
     */
    public static class GroupListItemViewCache {
        public final TextView accountType;
        public final TextView accountName;
        public final TextView groupTitle;
        public final TextView groupMemberCount;
        public final View accountHeader;
        public final View accountHeaderExtraTopPadding;
        public final View divider;
        private Uri mUri;

        public GroupListItemViewCache(View view) {
            accountType = (TextView) view.findViewById(R.id.account_type);
            accountName = (TextView) view.findViewById(R.id.account_name);
            groupTitle = (TextView) view.findViewById(R.id.label);
            groupMemberCount = (TextView) view.findViewById(R.id.count);
            accountHeader = view.findViewById(R.id.group_list_header);
            accountHeaderExtraTopPadding = view.findViewById(R.id.header_extra_top_padding);
            divider = view.findViewById(R.id.divider);
        }

        public void setUri(Uri uri) {
            mUri = uri;
        }

        public Uri getUri() {
            return mUri;
        }
    }
}