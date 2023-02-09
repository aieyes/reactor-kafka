/*
 * Copyright (c) 2023 VMware Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.common.protocol;

import org.apache.kafka.common.utils.Utils;

import java.util.Iterator;

public final class MessageUtil {
    public static short serializedUtf8Length(CharSequence input) {
        int count = Utils.utf8Length(input);
        if (count > 32767)
            throw new RuntimeException("String " + input + " is too long to serialize.");
        return (short) count;
    }

    public static String deepToString(Iterator<?> iter) {
        StringBuilder bld = new StringBuilder("[");
        String prefix = "";
        while (iter.hasNext()) {
            Object object = iter.next();
            bld.append(prefix);
            bld.append(object.toString());
            prefix = ", ";
        }
        bld.append("]");
        return bld.toString();
    }
}
