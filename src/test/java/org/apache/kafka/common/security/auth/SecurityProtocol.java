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

package org.apache.kafka.common.security.auth;

import java.util.*;

public enum SecurityProtocol {
    PLAINTEXT(0, "PLAINTEXT"),
    SSL(1, "SSL"),
    SASL_PLAINTEXT(2, "SASL_PLAINTEXT"),
    SASL_SSL(3, "SASL_SSL");

    private static final Map<Short, SecurityProtocol> CODE_TO_SECURITY_PROTOCOL;

    private static final List<String> NAMES;

    public final short id;

    public final String name;

    static {
        SecurityProtocol[] protocols = values();
        List<String> names = new ArrayList<>(protocols.length);
        Map<Short, SecurityProtocol> codeToSecurityProtocol = new HashMap<>(protocols.length);
        for (SecurityProtocol proto : protocols) {
            codeToSecurityProtocol.put(Short.valueOf(proto.id), proto);
            names.add(proto.name);
        }
        CODE_TO_SECURITY_PROTOCOL = Collections.unmodifiableMap(codeToSecurityProtocol);
        NAMES = Collections.unmodifiableList(names);
    }

    SecurityProtocol(int id, String name) {
        this.id = (short) id;
        this.name = name;
    }

    public static List<String> names() {
        return NAMES;
    }

    public static SecurityProtocol forId(short id) {
        return CODE_TO_SECURITY_PROTOCOL.get(Short.valueOf(id));
    }

    public static SecurityProtocol forName(String name) {
        return valueOf(name.toUpperCase(Locale.ROOT));
    }
}
