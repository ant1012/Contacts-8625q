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

package a_vcard.android.provider;

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



public interface BaseColumns
{
    /**
     * The unique ID for a row.
     * <P>Type: INTEGER (long)</P>
     */
    public static final String _ID = "_id";

    /**
     * The count of rows in a directory.
     * <P>Type: INTEGER</P>
     */
    public static final String _COUNT = "_count";
}
