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

package edu.bupt.contacts.editor;

import edu.bupt.contacts.model.DataKind;
import edu.bupt.contacts.model.EntityDelta;
import edu.bupt.contacts.model.EntityDelta.ValuesDelta;

import android.provider.ContactsContract.Data;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * Generic definition of something that edits a {@link Data} row through an
 * {@link ValuesDelta} object.
 */
public interface Editor {

    public interface EditorListener {
        /**
         * Called when the given {@link Editor} is requested to be deleted by the user.
         */
        public void onDeleteRequested(Editor editor);

        /**
         * Called when the given {@link Editor} has a request, for example it
         * wants to select a photo.
         */
        public void onRequest(int request);

        public static final int REQUEST_PICK_PHOTO = 1;
        public static final int FIELD_CHANGED = 2;
        public static final int FIELD_TURNED_EMPTY = 3;
        public static final int FIELD_TURNED_NON_EMPTY = 4;

        // The editor has switched between different representations of the same
        // data, e.g. from full name to structured name
        public static final int EDITOR_FORM_CHANGED = 5;
    }

    /**
     * Returns whether or not all the fields are empty in this {@link Editor}.
     */
    public boolean isEmpty();

    /**
     * Prepares this editor for the given {@link ValuesDelta}, which
     * builds any needed views. Any changes performed by the user will be
     * written back to that same object.
     */
    public void setValues(DataKind kind, ValuesDelta values, EntityDelta state, boolean readOnly,
            ViewIdGenerator vig);

    public void setDeletable(boolean deletable);

    /**
     * Add a specific {@link EditorListener} to this {@link Editor}.
     */
    public void setEditorListener(EditorListener listener);

    /**
     * Called internally when the contents of a specific field have changed,
     * allowing advanced editors to persist data in a specific way.
     */
    public void onFieldChanged(String column, String value);

    /**
     * Performs the delete operation for this {@link Editor}.
     */
    public void deleteEditor();

    /**
     * Clears all fields in this {@link Editor}.
     */
    public void clearAllFields();

    /**
     * Called internally when the user has added a new field.  This
     * allows the appropriate editor UI to be presented immediately.
     * For example, if a new "event" is added, a date-picker will
     * immediately pop up.
     */
    public void editNewlyAddedField();

}
