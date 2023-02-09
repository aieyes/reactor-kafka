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

package org.apache.kafka.clients.admin;

import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

import java.util.List;

@Evolving
public class NewPartitions {
    private int totalCount;

    private List<List<Integer>> newAssignments;

    private NewPartitions(int totalCount, List<List<Integer>> newAssignments) {
        this.totalCount = totalCount;
        this.newAssignments = newAssignments;
    }

    public static NewPartitions increaseTo(int totalCount) {
        return new NewPartitions(totalCount, null);
    }

    public static NewPartitions increaseTo(int totalCount, List<List<Integer>> newAssignments) {
        return new NewPartitions(totalCount, newAssignments);
    }

    public int totalCount() {
        return this.totalCount;
    }

    public List<List<Integer>> assignments() {
        return this.newAssignments;
    }

    public String toString() {
        return "(totalCount=" + totalCount() + ", newAssignments=" + assignments() + ")";
    }
}
