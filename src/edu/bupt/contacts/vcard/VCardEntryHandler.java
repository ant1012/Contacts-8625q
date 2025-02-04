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

/**
 * <p>
 * The interface called by {@link VCardEntryConstructor}.
 * </p>
 * <p>
 * This class is useful when you don't want to know vCard data in detail. If you want to know
 * it, it would be better to consider using {@link VCardInterpreter}.
 * </p>
 */
public interface VCardEntryHandler {
    /**
     * Called when the parsing started.
     */
    public void onStart();

    /**
     * The method called when one vCard entry is created. Children come before their parent in
     * nested vCard files.
     *
     * e.g.
     * In the following vCard, the entry for "entry2" comes before one for "entry1".
     * <code>
     * BEGIN:VCARD
     * N:entry1
     * BEGIN:VCARD
     * N:entry2
     * END:VCARD
     * END:VCARD
     * </code>
     */
    public void onEntryCreated(final VCardEntry entry);

    /**
     * Called when the parsing ended.
     * Able to be use this method for showing performance log, etc.
     */
    public void onEnd();
}
