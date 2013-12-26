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
package edu.bupt.contacts.interactions;

import edu.bupt.contacts.ContactSaveService;
import edu.bupt.contacts.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * 北邮ANT实验室
 * ddd
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * A dialog for deleting a group.
 */
public class GroupDeletionDialogFragment extends DialogFragment {

    private static final String ARG_GROUP_ID = "groupId";
    private static final String ARG_LABEL = "label";
    private static final String ARG_SHOULD_END_ACTIVITY = "endActivity";

    public static void show(FragmentManager fragmentManager, long groupId, String label,
            boolean endActivity) {
        GroupDeletionDialogFragment dialog = new GroupDeletionDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        args.putString(ARG_LABEL, label);
        args.putBoolean(ARG_SHOULD_END_ACTIVITY, endActivity);
        dialog.setArguments(args);
        dialog.show(fragmentManager, "deleteGroup");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String label = getArguments().getString(ARG_LABEL);
        String message = getActivity().getString(R.string.delete_group_dialog_message, label);

        return new AlertDialog.Builder(getActivity())
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteGroup();
                        }
                    }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    protected void deleteGroup() {
        Bundle arguments = getArguments();
        long groupId = arguments.getLong(ARG_GROUP_ID);

        getActivity().startService(ContactSaveService.createGroupDeletionIntent(
                getActivity(), groupId));
        if (shouldEndActivity()) {
            getActivity().finish();
        }
    }

    private boolean shouldEndActivity() {
        return getArguments().getBoolean(ARG_SHOULD_END_ACTIVITY);
    }
}
