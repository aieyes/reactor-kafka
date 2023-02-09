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

package org.apache.kafka.common.resource;

import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Evolving
public enum PatternType {
    UNKNOWN((byte) 0),
    ANY((byte) 1),
    MATCH((byte) 2),
    LITERAL((byte) 3),
    PREFIXED((byte) 4);

    private static final Map<Byte, PatternType> CODE_TO_VALUE;

    private static final Map<String, PatternType> NAME_TO_VALUE;

    private final byte code;

    static {
        CODE_TO_VALUE = Collections.unmodifiableMap(
                (Map<? extends Byte, ? extends PatternType>) Arrays.<PatternType>stream(values())
                        .collect(Collectors.toMap(PatternType::code, Function.identity())));
        NAME_TO_VALUE = Collections.unmodifiableMap(
                (Map<? extends String, ? extends PatternType>) Arrays.<PatternType>stream(values())
                        .collect(Collectors.toMap(Enum<PatternType>::name, Function.identity())));
    }

    PatternType(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isSpecific() {
        return this != UNKNOWN && this != ANY && this != MATCH;
    }

    public static PatternType fromCode(byte code) {
        return CODE_TO_VALUE.getOrDefault(Byte.valueOf(code), UNKNOWN);
    }

    public static PatternType fromString(String name) {
        return NAME_TO_VALUE.getOrDefault(name, UNKNOWN);
    }
}
