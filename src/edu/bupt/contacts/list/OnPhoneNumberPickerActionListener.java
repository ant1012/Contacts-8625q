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
package edu.bupt.contacts.list;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * Action callbacks that can be sent by a phone number picker.
 */
public interface OnPhoneNumberPickerActionListener  {

    /**
     * Returns the selected phone number to the requester.
     */
    void onPickPhoneNumberAction(Uri dataUri);

    /**
     * Returns the selected number as a shortcut intent.
     */
    void onShortcutIntentCreated(Intent intent);

    /**
     * Called when home menu in {@link ActionBar} is clicked by the user.
     */
    void onHomeInActionBarSelected();
}
