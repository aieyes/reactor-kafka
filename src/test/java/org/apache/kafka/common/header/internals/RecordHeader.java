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

package org.apache.kafka.common.header.internals;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.utils.Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class RecordHeader implements Header {
    private final String key;

    private ByteBuffer valueBuffer;

    private byte[] value;

    public RecordHeader(String key, byte[] value) {
        Objects.requireNonNull(key, "Null header keys are not permitted");
        this.key = key;
        this.value = value;
    }

    public RecordHeader(String key, ByteBuffer valueBuffer) {
        Objects.requireNonNull(key, "Null header keys are not permitted");
        this.key = key;
        this.valueBuffer = valueBuffer;
    }

    public String key() {
        return this.key;
    }

    public byte[] value() {
        if (this.value == null && this.valueBuffer != null) {
            this.value = Utils.toArray(this.valueBuffer);
            this.valueBuffer = null;
        }
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RecordHeader header = (RecordHeader) o;
        return Objects.equals(this.key, header.key) &&
                Arrays.equals(value(), header.value());
    }

    public int hashCode() {
        int result = (this.key != null) ? this.key.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(value());
        return result;
    }

    public String toString() {
        return "RecordHeader(key = " + this.key + ", value = " + Arrays.toString(value()) + ")";
    }
}
