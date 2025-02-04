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
 * InputFilters can be attached to {@link Editable}s to constrain the
 * changes that can be made to them.
 */
public interface InputFilter
{
    /**
     * This method is called when the buffer is going to replace the
     * range <code>dstart &hellip; dend</code> of <code>dest</code>
     * with the new text from the range <code>start &hellip; end</code>
     * of <code>source</code>.  Return the CharSequence that you would
     * like to have placed there instead, including an empty string
     * if appropriate, or <code>null</code> to accept the original
     * replacement.  Be careful to not to reject 0-length replacements,
     * as this is what happens when you delete text.  Also beware that
     * you should not attempt to make any changes to <code>dest</code>
     * from this method; you may only examine it for context.
     * 
     * Note: If <var>source</var> is an instance of {@link Spanned} or
     * {@link Spannable}, the span objects in the <var>source</var> should be 
     * copied into the filtered result (i.e. the non-null return value). 
     * {@link TextUtils#copySpansFrom} can be used for convenience.
     */
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend);

//    /**
//     * This filter will capitalize all the lower case letters that are added
//     * through edits.
//     */
//    public static class AllCaps implements InputFilter {
//        public CharSequence filter(CharSequence source, int start, int end,
//                                   Spanned dest, int dstart, int dend) {
//            for (int i = start; i < end; i++) {
//                if (Character.isLowerCase(source.charAt(i))) {
//                    char[] v = new char[end - start];
//                    TextUtils.getChars(source, start, end, v, 0);
//                    String s = new String(v).toUpperCase();
//
//                    if (source instanceof Spanned) {
//                        SpannableString sp = new SpannableString(s);
//                        TextUtils.copySpansFrom((Spanned) source,
//                                                start, end, null, sp, 0);
//                        return sp;
//                    } else {
//                        return s;
//                    }
//                }
//            }
//
//            return null; // keep original
//        }
//    }

    /**
     * This filter will constrain edits not to make the length of the text
     * greater than the specified length.
     */
    public static class LengthFilter implements InputFilter {
        public LengthFilter(int max) {
            mMax = max;
        }

        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int keep = mMax - (dest.length() - (dend - dstart));

            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                return source.subSequence(start, start + keep);
            }
        }

        private int mMax;
    }
}
