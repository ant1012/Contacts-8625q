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

package edu.bupt.contacts.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Spinner that notifies a listener when the user taps on an item, whether or not this results
 * in a change of selection.
 */
public class NotifyingSpinner extends Spinner {

    public interface SelectionListener {
        void onSetSelection(NotifyingSpinner view, int position);
    }

    private SelectionListener mListener;

    public NotifyingSpinner(Context context) {
        super(context);
    }

    public NotifyingSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSetSelectionListener(SelectionListener listener) {
        mListener = listener;
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);

        if (mListener != null) {
            mListener.onSetSelection(this, position);
        }
    }
}
