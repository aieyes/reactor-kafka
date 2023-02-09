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

import java.util.Objects;

class AccessControlEntryData {
    private final String principal;

    private final String host;

    private final AclOperation operation;

    private final AclPermissionType permissionType;

    AccessControlEntryData(String principal, String host, AclOperation operation, AclPermissionType permissionType) {
        this.principal = principal;
        this.host = host;
        this.operation = operation;
        this.permissionType = permissionType;
    }

    String principal() {
        return this.principal;
    }

    String host() {
        return this.host;
    }

    AclOperation operation() {
        return this.operation;
    }

    AclPermissionType permissionType() {
        return this.permissionType;
    }

    public String findIndefiniteField() {
        if (principal() == null)
            return "Principal is NULL";
        if (host() == null)
            return "Host is NULL";
        if (operation() == AclOperation.ANY)
            return "Operation is ANY";
        if (operation() == AclOperation.UNKNOWN)
            return "Operation is UNKNOWN";
        if (permissionType() == AclPermissionType.ANY)
            return "Permission type is ANY";
        if (permissionType() == AclPermissionType.UNKNOWN)
            return "Permission type is UNKNOWN";
        return null;
    }

    public String toString() {
        return "(principal=" + ((this.principal == null) ? "<any>" : this.principal) + ", host=" + ((this.host == null) ? "<any>" : this.host) + ", operation=" + this.operation + ", permissionType=" + this.permissionType + ")";
    }

    boolean isUnknown() {
        return this.operation.isUnknown() || this.permissionType.isUnknown();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AccessControlEntryData))
            return false;
        AccessControlEntryData other = (AccessControlEntryData) o;
        return Objects.equals(this.principal, other.principal) &&
                Objects.equals(this.host, other.host) &&
                Objects.equals(this.operation, other.operation) &&
                Objects.equals(this.permissionType, other.permissionType);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.principal, this.host, this.operation, this.permissionType});
    }
}
