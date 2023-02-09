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

package org.apache.kafka.common.requests;

public enum IsolationLevel {
    READ_UNCOMMITTED((byte) 0),
    READ_COMMITTED((byte) 1);

    private final byte id;

    IsolationLevel(byte id) {
        this.id = id;
    }

    public byte id() {
        return this.id;
    }

    public static IsolationLevel forId(byte id) {
        switch (id) {
            case 0:
                return READ_UNCOMMITTED;
            case 1:
                return READ_COMMITTED;
        }
        throw new IllegalArgumentException("Unknown isolation level " + id);
    }
}
