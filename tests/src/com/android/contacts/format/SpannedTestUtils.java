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

package edu.bupt.contacts.format;

import android.test.suitebuilder.annotation.SmallTest;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import junit.framework.Assert;

/**
 * Utility class to check the value of spanned text in text views.
 */
@SmallTest
public class SpannedTestUtils {
    /**
     * Checks that the text contained in the text view matches the given HTML text.
     *
     * @param expectedHtmlText the expected text to be in the text view
     * @param textView the text view from which to get the text
     */
    public static void checkHtmlText(String expectedHtmlText, TextView textView) {
        String actualHtmlText = Html.toHtml((Spanned) textView.getText());
        if (TextUtils.isEmpty(expectedHtmlText)) {
            // If the text is empty, it does not add the <p></p> bits to it.
            Assert.assertEquals("", actualHtmlText);
        } else {
            Assert.assertEquals("<p dir=ltr>" + expectedHtmlText + "</p>\n", actualHtmlText);
        }
    }

}
