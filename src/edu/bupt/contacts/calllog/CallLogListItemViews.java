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

package edu.bupt.contacts.calllog;

import edu.bupt.contacts.PhoneCallDetailsViews;
import edu.bupt.contacts.R;
import edu.bupt.contacts.test.NeededForTesting;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

/**
 * 北邮ANT实验室
 * ddd
 * 
 * 
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，有修改
 * 
 * */


/**
 * Simple value object containing the various views within a call log entry.
 */
public final class CallLogListItemViews {
    /** The quick contact badge for the contact. */
    public final ImageView quickContactView;
    /** The primary action view of the entry. */
    public final View primaryActionView;
    /** The secondary action button on the entry. */
    public final ImageView secondaryActionView;
    /** The divider between the primary and secondary actions. */
    public final View dividerView;
    /** The details of the phone call. */
    public final PhoneCallDetailsViews phoneCallDetailsViews;
    /** The text of the header of a section. */
    public final TextView listHeaderTextView;
    /** The divider to be shown below items. */
    public final View bottomDivider;
    
    
    /** by yuan */
    public final ImageView thirdaryActionView;

    private CallLogListItemViews(ImageView quickContactView, View primaryActionView,
            ImageView secondaryActionView, View dividerView,
            PhoneCallDetailsViews phoneCallDetailsViews,
            TextView listHeaderTextView, View bottomDivider, ImageView thirdaryActionView) {
        this.quickContactView = quickContactView;
        this.primaryActionView = primaryActionView;
        this.secondaryActionView = secondaryActionView;
        this.dividerView = dividerView;
        this.phoneCallDetailsViews = phoneCallDetailsViews;
        this.listHeaderTextView = listHeaderTextView;
        this.bottomDivider = bottomDivider;
        this.thirdaryActionView = thirdaryActionView;
    }

    public static CallLogListItemViews fromView(View view) {
        return new CallLogListItemViews(
                (ImageView) view.findViewById(R.id.quick_contact_photo),
                view.findViewById(R.id.primary_action_view),
                (ImageView) view.findViewById(R.id.secondary_action_icon),
                view.findViewById(R.id.divider),
                PhoneCallDetailsViews.fromView(view),
                (TextView) view.findViewById(R.id.call_log_header),
                view.findViewById(R.id.call_log_divider),
                (ImageView) view.findViewById(R.id.thirdary_action_icon));
    }

    @NeededForTesting
    public static CallLogListItemViews createForTest(Context context) {
        return new CallLogListItemViews(
                new ImageView(context),
                new View(context),
                new ImageView(context),
                new View(context),
                PhoneCallDetailsViews.createForTest(context),
                new TextView(context),
                new View(context),
                new ImageView(context));
    }
}
