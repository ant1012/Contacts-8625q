/*
 * Copyright (C) 2006 The Android Open Source Project
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

package a_vcard.android.text;
/**
 * 北邮ANT实验室
 * zzz
 * 
 * 联系人中的a_vcard包是为了实现短信应用中的vcard预览功能而加入的。
 * 这是一个Google提供的开源库android-vcard，代码提取自Android 1.0
 * https://code.google.com/p/android-vcard/
 * Apache License 2.0
 * 
 * 此文件取自android-vcard库，未作修改
 * 
 * */


/**
 * This is the interface for text to which markup objects can be
 * attached and detached.  Not all Spannable classes have mutable text;
 * see {@link Editable} for that.
 */
public interface Spannable
extends Spanned
{
    /**
     * Attach the specified markup object to the range <code>start&hellip;end</code>
     * of the text, or move the object to that range if it was already
     * attached elsewhere.  See {@link Spanned} for an explanation of
     * what the flags mean.  The object can be one that has meaning only
     * within your application, or it can be one that the text system will
     * use to affect text display or behavior.  Some noteworthy ones are
     * the subclasses of {@link android.text.style.CharacterStyle} and
     * {@link android.text.style.ParagraphStyle}, and
     * {@link android.text.TextWatcher} and
     * {@link android.text.SpanWatcher}.
     */
    public void setSpan(Object what, int start, int end, int flags);

    /**
     * Remove the specified object from the range of text to which it
     * was attached, if any.  It is OK to remove an object that was never
     * attached in the first place.
     */
    public void removeSpan(Object what);

//    /**
//     * Factory used by TextView to create new Spannables.  You can subclass
//     * it to provide something other than SpannableString.
//     */
//    public static class Factory {
//        private static Spannable.Factory sInstance = new Spannable.Factory();
//
//        /**
//         * Returns the standard Spannable Factory.
//         */
//        public static Spannable.Factory getInstance() {
//            return sInstance;
//        }
//
//        /**
//         * Returns a new SpannableString from the specified CharSequence.
//         * You can override this to provide a different kind of Spannable.
//         */
//        public Spannable newSpannable(CharSequence source) {
//            return new SpannableString(source);
//        }
//    }
}
