/*
 * Copyright (C) 2007 The Android Open Source Project
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
package edu.bupt.contacts.list;

import edu.bupt.contacts.widget.TextHighlightingAnimation;

import android.view.View;
import android.widget.ListView;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * A {@link TextHighlightingAnimation} that redraws just the contact display name in a
 * list item.
 */
public class ContactNameHighlightingAnimation extends TextHighlightingAnimation {
    private final ListView mListView;
    private boolean mSavedScrollingCacheEnabledFlag;

    public ContactNameHighlightingAnimation(ListView listView, int duration) {
        super(duration);
        this.mListView = listView;
    }

    /**
     * Redraws all visible items of the list corresponding to contacts
     */
    @Override
    protected void invalidate() {
        int childCount = mListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = mListView.getChildAt(i);
            if (itemView instanceof ContactListItemView) {
                final ContactListItemView view = (ContactListItemView)itemView;
                view.getNameTextView().invalidate();
            }
        }
    }

    @Override
    protected void onAnimationStarted() {
        mSavedScrollingCacheEnabledFlag = mListView.isScrollingCacheEnabled();
        mListView.setScrollingCacheEnabled(false);
    }

    @Override
    protected void onAnimationEnded() {
        mListView.setScrollingCacheEnabled(mSavedScrollingCacheEnabledFlag);
    }
}
