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
public enum AclPermissionType {
    UNKNOWN((byte) 0),
    ANY((byte) 1),
    DENY((byte) 2),
    ALLOW((byte) 3);

    private static final HashMap<Byte, AclPermissionType> CODE_TO_VALUE;

    private final byte code;

    static {
        CODE_TO_VALUE = new HashMap<>();
        for (AclPermissionType permissionType : values())
            CODE_TO_VALUE.put(Byte.valueOf(permissionType.code), permissionType);
    }

    public static AclPermissionType fromString(String str) {
        try {
            return valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static AclPermissionType fromCode(byte code) {
        AclPermissionType permissionType = CODE_TO_VALUE.get(Byte.valueOf(code));
        if (permissionType == null)
            return UNKNOWN;
        return permissionType;
    }

    AclPermissionType(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}
