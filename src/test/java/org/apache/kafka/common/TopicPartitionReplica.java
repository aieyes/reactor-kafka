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

package org.apache.kafka.common;

import org.apache.kafka.common.utils.Utils;

import java.io.Serializable;

public final class TopicPartitionReplica implements Serializable {
    private static final long serialVersionUID = -5155213080730928688L;
    private int hash = 0;

    private final int brokerId;

    private final int partition;

    private final String topic;

    public TopicPartitionReplica(String topic, int partition, int brokerId) {
        this.topic = Utils.notNull(topic);
        this.partition = partition;
        this.brokerId = brokerId;
    }

    public String topic() {
        return this.topic;
    }

    public int partition() {
        return this.partition;
    }

    public int brokerId() {
        return this.brokerId;
    }

    public int hashCode() {
        if (this.hash != 0)
            return this.hash;
        int prime = 31;
        int result = 1;
        result = 31 * result + this.topic.hashCode();
        result = 31 * result + this.partition;
        result = 31 * result + this.brokerId;
        this.hash = result;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopicPartitionReplica other = (TopicPartitionReplica) obj;
        return this.partition == other.partition && this.brokerId == other.brokerId && this.topic.equals(other.topic);
    }

    public String toString() {
        return String.format("%s-%d-%d", this.topic, this.partition, this.brokerId);
    }
}
