/*
 * Copyright (C) 2012 The Android Open Source Project
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

package edu.bupt.contacts.dialpad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * 北邮ANT实验室
 * ddd
 * 
 * 拨号盘
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，无修改
 * 
 * */

/**
 * Custom {@link ImageButton} for dialpad buttons.
 *
 * During horizontal swipe, we want to exit "fading out" animation offered by its background
 * just after starting the swipe.This class overrides {@link #onTouchEvent(MotionEvent)} to achieve
 * the behavior.
 */
public class DialpadImageButton extends ImageButton {
    public interface OnPressedListener {
        public void onPressed(View view, boolean pressed);
    }

    private OnPressedListener mOnPressedListener;

    public void setOnPressedListener(OnPressedListener onPressedListener) {
        mOnPressedListener = onPressedListener;
    }

    public DialpadImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialpadImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (mOnPressedListener != null) {
            mOnPressedListener.onPressed(this, pressed);
        }
    }
}
