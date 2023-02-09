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

import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

import java.util.HashMap;
import java.util.Map;

@Evolving
public class DescribeTopicsResult {
    private final Map<String, KafkaFuture<TopicDescription>> futures;

    DescribeTopicsResult(Map<String, KafkaFuture<TopicDescription>> futures) {
        this.futures = futures;
    }

    public Map<String, KafkaFuture<TopicDescription>> values() {
        return this.futures;
    }

    @SuppressWarnings({"rawtypes"})
    public KafkaFuture<Map<String, TopicDescription>> all() {
        return KafkaFuture.allOf((KafkaFuture[]) this.futures.values().toArray((Object[]) new KafkaFuture[0]))
                .thenApply(v -> {
                    Map<String, TopicDescription> descriptions = new HashMap<>(DescribeTopicsResult.this.futures.size());
                    for (Map.Entry<String, KafkaFuture<TopicDescription>> entry : DescribeTopicsResult.this.futures.entrySet()) {
                        try {
                            descriptions.put(entry.getKey(), (TopicDescription) ((KafkaFuture) entry.getValue()).get());
                        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return descriptions;
                });
    }
}
