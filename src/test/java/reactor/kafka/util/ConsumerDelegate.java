/*
 * Copyright (c) 2020-2023 VMware Inc. or its affiliates, All Rights Reserved.
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

package reactor.kafka.util;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ConsumerDelegate<K, V> implements Consumer<K, V> {

    static final Method CURRENT_LAG_METHOD;

    static {
        Method method;
        try {
            method = Consumer.class.getDeclaredMethod("currentLag", TopicPartition.class);
        } catch (NoSuchMethodException | SecurityException e) {
            method = null;
        }
        CURRENT_LAG_METHOD = method;
    }

    final Consumer<K, V> delegate;

    public ConsumerDelegate(Consumer<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Set<TopicPartition> assignment() {
        return delegate.assignment();
    }

    @Override
    public Set<String> subscription() {
        return delegate.subscription();
    }

    @Override
    public void subscribe(Collection<String> topics) {
        delegate.subscribe(topics);
    }

    @Override
    public void subscribe(Collection<String> topics, ConsumerRebalanceListener callback) {
        delegate.subscribe(topics, callback);
    }

    @Override
    public void assign(Collection<TopicPartition> partitions) {
        delegate.assign(partitions);
    }

    @Override
    public void subscribe(Pattern pattern, ConsumerRebalanceListener callback) {
        delegate.subscribe(pattern, callback);
    }

    @Override
    public void unsubscribe() {
        delegate.unsubscribe();
    }

    @Override
    public ConsumerRecords<K, V> poll(long timeout) {
        return delegate.poll(timeout);
    }

    @Override
    public void commitSync() {
        delegate.commitSync();
    }

    @Override
    public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets) {
        delegate.commitSync(offsets);
    }

    @Override
    public void commitAsync() {
        delegate.commitAsync();
    }

    @Override
    public void commitAsync(OffsetCommitCallback callback) {
        delegate.commitAsync(callback);
    }

    @Override
    public void commitAsync(Map<TopicPartition, OffsetAndMetadata> offsets, OffsetCommitCallback callback) {
        delegate.commitAsync(offsets, callback);
    }

    @Override
    public void seek(TopicPartition partition, long offset) {
        delegate.seek(partition, offset);
    }

    @Override
    public void seekToBeginning(Collection<TopicPartition> partitions) {
        delegate.seekToBeginning(partitions);
    }

    @Override
    public void seekToEnd(Collection<TopicPartition> partitions) {
        delegate.seekToEnd(partitions);
    }

    @Override
    public long position(TopicPartition partition) {
        return delegate.position(partition);
    }

    @Override
    public OffsetAndMetadata committed(TopicPartition partition) {
        return delegate.committed(partition);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return delegate.metrics();
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
        return delegate.partitionsFor(topic);
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics() {
        return delegate.listTopics();
    }

    @Override
    public Set<TopicPartition> paused() {
        return delegate.paused();
    }

    @Override
    public void pause(Collection<TopicPartition> partitions) {
        delegate.pause(partitions);
    }

    @Override
    public void resume(Collection<TopicPartition> partitions) {
        delegate.resume(partitions);
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> timestampsToSearch) {
        return delegate.offsetsForTimes(timestampsToSearch);
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> partitions) {
        return delegate.beginningOffsets(partitions);
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> partitions) {
        return delegate.endOffsets(partitions);
    }

    /**
     * Get the consumer's current lag on the partition. Returns an "empty" {@link OptionalLong} if the lag is not known,
     * for example if there is no position yet, or if the end offset is not known yet.
     * Only available with kafka-clients 3.0.0 or later.
     * <p>
     * This method uses locally cached metadata and never makes a remote call.
     *
     * @param topicPartition The partition to get the lag for.
     *
     * @return This {@code Consumer} instance's current lag for the given partition.
     *
     * @throws IllegalStateException if the {@code topicPartition} is not assigned
     **/
    public OptionalLong currentLag(TopicPartition topicPartition) {
        if (CURRENT_LAG_METHOD != null) {
            try {
                return (OptionalLong) CURRENT_LAG_METHOD.invoke(this.delegate, topicPartition);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("kafka-clients version does not support this method");
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void close(long timeout, TimeUnit unit) {
        delegate.close(timeout, unit);
    }

    @Override
    public void wakeup() {
        delegate.wakeup();
    }
}
