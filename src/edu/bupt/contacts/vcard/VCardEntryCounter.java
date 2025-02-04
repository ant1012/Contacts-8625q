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
package edu.bupt.contacts.vcard;

import com.android.vcard.VCardProperty;

/**
 * The class which just counts the number of vCard entries in the specified input.
 */
public class VCardEntryCounter implements VCardInterpreter {
    private int mCount;

    public int getCount() {
        return mCount;
    }

    @Override
    public void onVCardStarted() {
    }

    @Override
    public void onVCardEnded() {
    }

    @Override
    public void onEntryStarted() {
    }

    @Override
    public void onEntryEnded() {
        mCount++;
    }

    @Override
    public void onPropertyCreated(VCardProperty property) {
    }
}
