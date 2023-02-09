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

import java.util.Objects;

@Evolving
public class AccessControlEntry {
    final AccessControlEntryData data;

    public AccessControlEntry(String principal, String host, AclOperation operation, AclPermissionType permissionType) {
        Objects.requireNonNull(principal);
        Objects.requireNonNull(host);
        Objects.requireNonNull(operation);
        if (operation == AclOperation.ANY)
            throw new IllegalArgumentException("operation must not be ANY");
        Objects.requireNonNull(permissionType);
        if (permissionType == AclPermissionType.ANY)
            throw new IllegalArgumentException("permissionType must not be ANY");
        this.data = new AccessControlEntryData(principal, host, operation, permissionType);
    }

    public String principal() {
        return this.data.principal();
    }

    public String host() {
        return this.data.host();
    }

    public AclOperation operation() {
        return this.data.operation();
    }

    public AclPermissionType permissionType() {
        return this.data.permissionType();
    }

    public AccessControlEntryFilter toFilter() {
        return new AccessControlEntryFilter(this.data);
    }

    public String toString() {
        return this.data.toString();
    }

    public boolean isUnknown() {
        return this.data.isUnknown();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AccessControlEntry))
            return false;
        AccessControlEntry other = (AccessControlEntry) o;
        return this.data.equals(other.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}
