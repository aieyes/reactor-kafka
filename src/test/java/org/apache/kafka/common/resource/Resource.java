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
public class Resource {
    private final ResourceType resourceType;

    private final String name;

    public static final String CLUSTER_NAME = "kafka-cluster";

    public static final Resource CLUSTER = new Resource(ResourceType.CLUSTER, "kafka-cluster");

    public Resource(ResourceType resourceType, String name) {
        Objects.requireNonNull(resourceType);
        this.resourceType = resourceType;
        Objects.requireNonNull(name);
        this.name = name;
    }

    public ResourceType resourceType() {
        return this.resourceType;
    }

    public String name() {
        return this.name;
    }

    public ResourceFilter toFilter() {
        return new ResourceFilter(this.resourceType, this.name);
    }

    public String toString() {
        return "(resourceType=" + this.resourceType + ", name=" + ((this.name == null) ? "<any>" : this.name) + ")";
    }

    public boolean isUnknown() {
        return this.resourceType.isUnknown();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Resource))
            return false;
        Resource other = (Resource) o;
        return this.resourceType.equals(other.resourceType) && Objects.equals(this.name, other.name);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.resourceType, this.name});
    }
}
