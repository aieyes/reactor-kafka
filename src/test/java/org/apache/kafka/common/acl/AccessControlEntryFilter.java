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
public class AccessControlEntryFilter {
    private final AccessControlEntryData data;

    public static final AccessControlEntryFilter ANY = new AccessControlEntryFilter(null, null, AclOperation.ANY, AclPermissionType.ANY);

    public AccessControlEntryFilter(String principal, String host, AclOperation operation, AclPermissionType permissionType) {
        Objects.requireNonNull(operation);
        Objects.requireNonNull(permissionType);
        this.data = new AccessControlEntryData(principal, host, operation, permissionType);
    }

    AccessControlEntryFilter(AccessControlEntryData data) {
        this.data = data;
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

    public String toString() {
        return this.data.toString();
    }

    public boolean isUnknown() {
        return this.data.isUnknown();
    }

    public boolean matches(AccessControlEntry other) {
        if (principal() != null && !this.data.principal().equals(other.principal()))
            return false;
        if (host() != null && !host().equals(other.host()))
            return false;
        if (operation() != AclOperation.ANY && !operation().equals(other.operation()))
            return false;
        return permissionType() == AclPermissionType.ANY || permissionType().equals(other.permissionType());
    }

    public boolean matchesAtMostOne() {
        return findIndefiniteField() == null;
    }

    public String findIndefiniteField() {
        return this.data.findIndefiniteField();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AccessControlEntryFilter))
            return false;
        AccessControlEntryFilter other = (AccessControlEntryFilter) o;
        return this.data.equals(other.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}
