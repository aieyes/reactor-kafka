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

import java.util.Objects;

@Evolving
public class ResourcePatternFilter {
    public static final ResourcePatternFilter ANY = new ResourcePatternFilter(ResourceType.ANY, null, PatternType.ANY);

    private final ResourceType resourceType;

    private final String name;

    private final PatternType patternType;

    public ResourcePatternFilter(ResourceType resourceType, String name, PatternType patternType) {
        this.resourceType = Objects.<ResourceType>requireNonNull(resourceType, "resourceType");
        this.name = name;
        this.patternType = Objects.<PatternType>requireNonNull(patternType, "patternType");
    }

    public boolean isUnknown() {
        return (this.resourceType.isUnknown() || this.patternType.isUnknown());
    }

    public ResourceType resourceType() {
        return this.resourceType;
    }

    public String name() {
        return this.name;
    }

    public PatternType patternType() {
        return this.patternType;
    }

    public boolean matches(ResourcePattern pattern) {
        if (!this.resourceType.equals(ResourceType.ANY) && !this.resourceType.equals(pattern.resourceType()))
            return false;
        if (!this.patternType.equals(PatternType.ANY) && !this.patternType.equals(PatternType.MATCH) && !this.patternType.equals(pattern.patternType()))
            return false;
        if (this.name == null)
            return true;
        if (this.patternType.equals(PatternType.ANY) || this.patternType.equals(pattern.patternType()))
            return this.name.equals(pattern.name());
        switch (pattern.patternType()) {
            case LITERAL:
                return (this.name.equals(pattern.name()) || pattern.name().equals("*"));
            case PREFIXED:
                return this.name.startsWith(pattern.name());
        }
        throw new IllegalArgumentException("Unsupported PatternType: " + pattern.patternType());
    }

    public boolean matchesAtMostOne() {
        return (findIndefiniteField() == null);
    }

    public String findIndefiniteField() {
        if (this.resourceType == ResourceType.ANY)
            return "Resource type is ANY.";
        if (this.resourceType == ResourceType.UNKNOWN)
            return "Resource type is UNKNOWN.";
        if (this.name == null)
            return "Resource name is NULL.";
        if (this.patternType == PatternType.MATCH)
            return "Resource pattern type is MATCH.";
        if (this.patternType == PatternType.UNKNOWN)
            return "Resource pattern type is UNKNOWN.";
        return null;
    }

    public String toString() {
        return "ResourcePattern(resourceType=" + this.resourceType + ", name=" + ((this.name == null) ? "<any>" : this.name) + ", patternType=" + this.patternType + ")";
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ResourcePatternFilter resource = (ResourcePatternFilter) o;
        return this.resourceType == resource.resourceType &&
                Objects.equals(this.name, resource.name) && this.patternType == resource.patternType;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.resourceType, this.name, this.patternType});
    }
}
