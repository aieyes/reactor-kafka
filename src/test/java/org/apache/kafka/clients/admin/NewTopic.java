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

import org.apache.kafka.common.message.CreateTopicsRequestData;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NewTopic {
    private final String name;

    private final int numPartitions;

    private final short replicationFactor;

    private final Map<Integer, List<Integer>> replicasAssignments;

    private Map<String, String> configs = null;

    public NewTopic(String name, int numPartitions, short replicationFactor) {
        this.name = name;
        this.numPartitions = numPartitions;
        this.replicationFactor = replicationFactor;
        this.replicasAssignments = null;
    }

    public NewTopic(String name, Map<Integer, List<Integer>> replicasAssignments) {
        this.name = name;
        this.numPartitions = -1;
        this.replicationFactor = -1;
        this.replicasAssignments = Collections.unmodifiableMap(replicasAssignments);
    }

    public String name() {
        return this.name;
    }

    public int numPartitions() {
        return this.numPartitions;
    }

    public short replicationFactor() {
        return this.replicationFactor;
    }

    public Map<Integer, List<Integer>> replicasAssignments() {
        return this.replicasAssignments;
    }

    public NewTopic configs(Map<String, String> configs) {
        this.configs = configs;
        return this;
    }

    public Map<String, String> configs() {
        return this.configs;
    }

    CreateTopicsRequestData.CreatableTopic convertToCreatableTopic() {
        CreateTopicsRequestData.CreatableTopic creatableTopic = (new CreateTopicsRequestData.CreatableTopic()).setName(this.name).setNumPartitions(this.numPartitions).setReplicationFactor(this.replicationFactor);
        if (this.replicasAssignments != null)
            for (Map.Entry<Integer, List<Integer>> entry : this.replicasAssignments.entrySet())
                creatableTopic.assignments().add((new CreateTopicsRequestData.CreatableReplicaAssignment())

                        .setPartitionIndex(entry.getKey().intValue())
                        .setBrokerIds(entry.getValue()));
        if (this.configs != null)
            for (Map.Entry<String, String> entry : this.configs.entrySet())
                creatableTopic.configs().add((new CreateTopicsRequestData.CreateableTopicConfig())
                        .setName(entry.getKey())
                        .setValue(entry.getValue()));
        return creatableTopic;
    }

    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("(name=").append(this.name)
                .append(", numPartitions=").append(this.numPartitions)
                .append(", replicationFactor=").append(this.replicationFactor)
                .append(", replicasAssignments=").append(this.replicasAssignments)
                .append(", configs=").append(this.configs)
                .append(")");
        return bld.toString();
    }
}
