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

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Evolving
public abstract class AdminClient implements AutoCloseable {
    public static AdminClient create(Properties props) {
        return null;
    }

    public static AdminClient create(Map<String, Object> conf) {
        return null;
    }

    public void close() {
        close(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    @Deprecated
    public void close(long duration, TimeUnit unit) {
        close(Duration.ofMillis(unit.toMillis(duration)));
    }

    public abstract void close(Duration paramDuration);

    public CreateTopicsResult createTopics(Collection<NewTopic> newTopics) {
        return null;
    }

    public DescribeTopicsResult describeTopics(Collection<String> topicNames) {
        return describeTopics(topicNames, new DescribeTopicsOptions());
    }

    public abstract DescribeTopicsResult describeTopics(Collection<String> paramCollection, DescribeTopicsOptions paramDescribeTopicsOptions);

    public CreatePartitionsResult createPartitions(Map<String, NewPartitions> newPartitions) {
        return createPartitions(newPartitions, new CreatePartitionsOptions());
    }

    public abstract CreatePartitionsResult createPartitions(Map<String, NewPartitions> paramMap, CreatePartitionsOptions paramCreatePartitionsOptions);

    public abstract Map<MetricName, ? extends Metric> metrics();
}
