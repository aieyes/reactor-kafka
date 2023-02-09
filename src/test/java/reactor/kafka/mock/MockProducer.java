/*
 * Copyright (c) 2016-2023 VMware Inc. or its affiliates, All Rights Reserved.
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

package reactor.kafka.mock;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.LeaderNotAvailableException;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.ProducerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MockProducer implements Producer<Integer, String> {

    private final ScheduledExecutorService executor;
    private final MockCluster cluster;
    private final AtomicInteger inFlightCount;
    public final AtomicInteger sendCount = new AtomicInteger();
    private SenderOptions<Integer, String> senderOptions;
    private long sendDelayMs;
    private boolean closed;
    private boolean inFlightCheckEnabled;

    public int beginCount;
    public int commitCount;
    public int abortCount;
    public int sendOffsetsCount;

    public MockProducer(MockCluster cluster) {
        executor = Executors.newSingleThreadScheduledExecutor();
        this.cluster = cluster;
        inFlightCount = new AtomicInteger();
    }

    public void configure(SenderOptions<Integer, String> senderOptions) {
        this.senderOptions = senderOptions;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isBlocked() {
        try {
            return executor.submit(() -> false).get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return true;
        }
    }

    // Inflight counts are per-send flux. Checks should be enabled only in tests
    // with a single send flow.
    public void enableInFlightCheck() {
        this.inFlightCheckEnabled = true;
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<Integer, String> record) {
        return send(record, null);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<Integer, String> record, Callback callback) {
        if (inFlightCount.incrementAndGet() > senderOptions.maxInFlight()) {
            if (inFlightCheckEnabled)
                throw new IllegalStateException("Max inflight limit reached: " + inFlightCount);
        }
        sendCount.incrementAndGet();
        return executor.schedule(() -> doSend(record, callback), sendDelayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void flush() {
        call(() -> true);
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
        return call(() -> {
            List<PartitionInfo> partitionInfo = cluster.cluster().partitionsForTopic(topic);
            if (partitionInfo == null || partitionInfo.isEmpty())
                throw new InvalidTopicException(topic);
            else
                return partitionInfo;
        });
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return new HashMap<>();
    }

    @Override
    public void close() {
        closed = true;
        executor.shutdown();
    }

    @Override
    public void close(long timeout, TimeUnit unit) {
        close();
    }

    private <T> T call(Callable<T> callable) {
        try {
            return executor.submit(callable).get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException)
                throw (RuntimeException) e.getCause();
            else
                throw new RuntimeException(e.getCause());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public RecordMetadata doSend(ProducerRecord<Integer, String> record, Callback callback) {
        List<PartitionInfo> partitionInfo = cluster.cluster().availablePartitionsForTopic(record.topic());
        TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
        inFlightCount.decrementAndGet();
        if (partitionInfo == null || partitionInfo.isEmpty()) {
            InvalidTopicException e = new InvalidTopicException("Topic not found: " + record.topic());
            callback.onCompletion(null, e);
            throw e;
        } else if (!cluster.leaderAvailable(topicPartition)) {
            LeaderNotAvailableException e = new LeaderNotAvailableException("Leader not available for " + topicPartition);
            callback.onCompletion(null, e);
            throw e;
        } else {
            try {
                long offset = cluster.appendMessage(record, !senderOptions.isTransactional());
                RecordMetadata metadata = new RecordMetadata(topicPartition, 0, offset, System.currentTimeMillis(), 0L, 4, record.value().length());
                callback.onCompletion(metadata, null);
                return metadata;
            } catch (Exception e) {
                callback.onCompletion(null, e);
                throw e;
            }
        }

    }
    public void fenceProducer() {
        verifyProducerState();
    }

    private void verifyProducerState() {
        if (this.closed)
            throw new IllegalStateException("MockProducer is already closed.");
    }

    public static class Pool extends ProducerFactory {
        private final List<MockProducer> freeProducers = new ArrayList<>();
        private final List<MockProducer> producersInUse = new ArrayList<>();
        public Pool(List<MockProducer> freeProducers) {
            this.freeProducers.addAll(freeProducers);
        }
        @Override
        @SuppressWarnings("unchecked")
        public <K, V> Producer<K, V> createProducer(SenderOptions<K, V> senderOptions) {
            MockProducer producer = freeProducers.remove(0);
            producer.configure((SenderOptions<Integer, String>) senderOptions);
            producersInUse.add(producer);
            return (Producer<K, V>) producer;
        }
        public List<MockProducer> producersInUse() {
            return producersInUse;
        }
        public void addProducer(MockProducer producer) {
            freeProducers.add(producer);
        }
    }
}
