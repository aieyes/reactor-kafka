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

import java.nio.charset.StandardCharsets;

public interface Writable {
    void writeByte(byte paramByte);

    void writeShort(short paramShort);

    void writeInt(int paramInt);

    void writeLong(long paramLong);

    void writeArray(byte[] paramArrayOfbyte);

    default void writeNullableBytes(byte[] arr) {
        if (arr == null) {
            writeInt(-1);
        } else {
            writeBytes(arr);
        }
    }

    default void writeBytes(byte[] arr) {
        writeInt(arr.length);
        writeArray(arr);
    }

    default void writeNullableString(String string) {
        if (string == null) {
            writeShort((short) -1);
        } else {
            writeString(string);
        }
    }

    default void writeString(String string) {
        byte[] arr = string.getBytes(StandardCharsets.UTF_8);
        if (arr.length > 32767)
            throw new RuntimeException("Can't store string longer than 32767");
        writeShort((short) arr.length);
        writeArray(arr);
    }
}
