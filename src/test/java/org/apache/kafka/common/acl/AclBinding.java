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
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.Resource;
import org.apache.kafka.common.resource.ResourcePattern;

import java.util.Objects;

@Evolving
public class AclBinding {
    private final ResourcePattern pattern;

    private final AccessControlEntry entry;

    public AclBinding(ResourcePattern pattern, AccessControlEntry entry) {
        this.pattern = Objects.<ResourcePattern>requireNonNull(pattern, "pattern");
        this.entry = Objects.<AccessControlEntry>requireNonNull(entry, "entry");
    }

    @Deprecated
    public AclBinding(Resource resource, AccessControlEntry entry) {
        this(new ResourcePattern(resource.resourceType(), resource.name(), PatternType.LITERAL), entry);
    }

    public boolean isUnknown() {
        return this.pattern.isUnknown() || this.entry.isUnknown();
    }

    public ResourcePattern pattern() {
        return this.pattern;
    }

    public final AccessControlEntry entry() {
        return this.entry;
    }

    public AclBindingFilter toFilter() {
        return new AclBindingFilter(this.pattern.toFilter(), this.entry.toFilter());
    }

    public String toString() {
        return "(pattern=" + this.pattern + ", entry=" + this.entry + ")";
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AclBinding that = (AclBinding) o;
        return Objects.equals(this.pattern, that.pattern) &&
                Objects.equals(this.entry, that.entry);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.pattern, this.entry});
    }
}
