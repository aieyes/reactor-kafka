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

package org.apache.kafka.common.acl;

import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

import java.util.HashMap;
import java.util.Locale;

@Evolving
public enum AclOperation {
    UNKNOWN((byte) 0),
    ANY((byte) 1),
    ALL((byte) 2),
    READ((byte) 3),
    WRITE((byte) 4),
    CREATE((byte) 5),
    DELETE((byte) 6),
    ALTER((byte) 7),
    DESCRIBE((byte) 8),
    CLUSTER_ACTION((byte) 9),
    DESCRIBE_CONFIGS((byte) 10),
    ALTER_CONFIGS((byte) 11),
    IDEMPOTENT_WRITE((byte) 12);

    private static final HashMap<Byte, AclOperation> CODE_TO_VALUE;

    private final byte code;

    static {
        CODE_TO_VALUE = new HashMap<>();
        for (AclOperation operation : values())
            CODE_TO_VALUE.put(Byte.valueOf(operation.code), operation);
    }

    public static AclOperation fromString(String str) throws IllegalArgumentException {
        try {
            return valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static AclOperation fromCode(byte code) {
        AclOperation operation = CODE_TO_VALUE.get(Byte.valueOf(code));
        if (operation == null)
            return UNKNOWN;
        return operation;
    }

    AclOperation(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}
