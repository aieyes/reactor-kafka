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

package org.apache.kafka.common.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ConfigResource {
    private final Type type;

    private final String name;

    public enum Type {
        BROKER((byte) 4),
        TOPIC((byte) 2),
        UNKNOWN((byte) 0);

        private final byte id;

        private static final Map<Byte, Type> TYPES = Collections.unmodifiableMap(
                Arrays.stream(values()).collect(Collectors.toMap(Type::id, Function.identity())));

        static {

        }

        Type(byte id) {
            this.id = id;
        }

        public byte id() {
            return this.id;
        }

        public static Type forId(byte id) {
            return TYPES.getOrDefault(Byte.valueOf(id), UNKNOWN);
        }
    }

    public ConfigResource(Type type, String name) {
        Objects.requireNonNull(type, "type should not be null");
        Objects.requireNonNull(name, "name should not be null");
        this.type = type;
        this.name = name;
    }

    public Type type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public boolean isDefault() {
        return this.name.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ConfigResource that = (ConfigResource) o;
        return this.type == that.type && this.name.equals(that.name);
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }

    public String toString() {
        return "ConfigResource(type=" + this.type + ", name='" + this.name + "')";
    }
}
