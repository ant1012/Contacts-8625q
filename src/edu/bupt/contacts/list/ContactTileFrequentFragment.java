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
package edu.bupt.contacts.list;

import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * Fragment containing a list of frequently contacted people.
 */
public class ContactTileFrequentFragment extends ContactTileListFragment {
    private static final String TAG = ContactTileFrequentFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View listLayout = inflateAndSetupView(inflater, container, savedInstanceState,
                R.layout.contact_tile_list_frequent);
        View headerView = ContactsUtils.createHeaderView(getActivity(),
                R.string.favoritesFrequentContacted);
        ViewGroup headerContainer = (ViewGroup) listLayout.findViewById(R.id.header_container);
        headerContainer.addView(headerView);
        return listLayout;
    }
}
