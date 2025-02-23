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

package reactor.kafka.sender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import reactor.core.scheduler.Scheduler;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import javax.naming.AuthenticationException;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

public interface SenderOptions<K, V> {

    /**
     * Creates a sender options instance with default properties.
     * @return new instance of sender options
     */
    @NonNull
    static <K, V> SenderOptions<K, V> create() {
        return new ImmutableSenderOptions<>();
    }

    /**
     * Creates a sender options instance with the specified config overrides for the underlying
     * Kafka {@link Producer}.
     * @return new instance of sender options
     */
    @NonNull
    static <K, V> SenderOptions<K, V> create(@NonNull Map<String, Object> configProperties) {
        return new ImmutableSenderOptions<>(configProperties);
    }

    /**
     * Creates a sender options instance with the specified config overrides for the underlying
     * Kafka {@link Producer}.
     * @return new instance of sender options
     */
    @NonNull
    static <K, V> SenderOptions<K, V> create(@NonNull Properties configProperties) {
        return new ImmutableSenderOptions<>(configProperties);
    }

    /**
     * Returns the configuration properties for the underlying Kafka {@link Producer}.
     * @return configuration options for Kafka producer
     */
    @NonNull
    Map<String, Object> producerProperties();

    /**
     * Returns the Kafka {@link Producer} configuration property value for the specified option name.
     * @return Kafka producer configuration option value
     */
    @Nullable
    Object producerProperty(@NonNull String name);

    /**
     * Sets Kafka {@link Producer} configuration property to the specified value.
     * @return sender options with updated Kafka producer option
     */
    @NonNull
    SenderOptions<K, V> producerProperty(@NonNull String name, @NonNull Object value);

    /**
     *
     * Returns optionally a serializer witch is used by {@link KafkaProducer} for key serialization.
     * @return configured key serializer instant
     */
    @Nullable
    Serializer<K> keySerializer();

    /**
     * Set a concrete serializer instant to be used by the {@link KafkaProducer} for keys. Overrides any setting of the
     * {@link ProducerConfig#KEY_SERIALIZER_CLASS_CONFIG} property.
     * @param keySerializer key serializer to use in the consumer
     * @return options instance with new key serializer
     */
    @NonNull
    SenderOptions<K, V> withKeySerializer(@NonNull Serializer<K> keySerializer);

    /**
     *
     * Returns optionally a serializer witch is used by {@link KafkaProducer} for value serialization.
     * @return configured value serializer instant
     */
    @Nullable
    Serializer<V> valueSerializer();

    /**
     * Set a concrete serializer instant to be used by the {@link KafkaProducer} for values. Overrides any setting of the
     * {@link ProducerConfig#VALUE_SERIALIZER_CLASS_CONFIG} property.
     * @param valueSerializer value serializer to use in the consumer
     * @return options instance with new value serializer
     */
    @NonNull
    SenderOptions<K, V> withValueSerializer(@NonNull Serializer<V> valueSerializer);

    /**
     * Returns the scheduler used for publishing send results.
     * @return response scheduler
     */
    @NonNull
    Scheduler scheduler();

    /**
     * Sets the scheduler used for publishing send results.
     * @return sender options with updated response scheduler
     */
    @NonNull
    SenderOptions<K, V> scheduler(@NonNull Scheduler scheduler);

    /**
     * Returns the maximum number of in-flight records that are fetched
     * from the outbound record publisher while acknowledgements are pending.
     * @return maximum number of in-flight records
     */
    @NonNull
    int maxInFlight();

    /**
     * Configures the maximum number of in-flight records that are fetched
     * from the outbound record publisher while acknowledgements are pending.
     * This limit must be configured along with {@link ProducerConfig#BUFFER_MEMORY_CONFIG}
     * to control memory usage and to avoid blocking the reactive pipeline.
     * @return sender options with new in-flight limit
     */
    @NonNull
    SenderOptions<K, V> maxInFlight(@NonNull int maxInFlight);

    /**
     * Returns stopOnError configuration which indicates if a send operation
     * should be terminated when an error is encountered. If set to false, send
     * is attempted for all records in a sequence even if send of one of the records fails
     * with a non-fatal exception.
     * @return boolean indicating if send sequences should fail on first error
     */
    @NonNull
    boolean stopOnError();

    /**
     * Configures error handling behaviour for {@link KafkaSender#send(org.reactivestreams.Publisher)}.
     * If set to true, send fails when an error is encountered and only records
     * that are already in transit may be delivered after the first error. If set to false,
     * an attempt is made to send each record to Kafka, even if one or more records cannot
     * be delivered after the configured number of retries due to a non-fatal exception.
     * This flag should be set along with {@link ProducerConfig#RETRIES_CONFIG} and
     * {@link ProducerConfig#ACKS_CONFIG} to configure the required quality-of-service.
     * By default, stopOnError is true.
     * @param stopOnError true to stop each send sequence on first failure
     * @return sender options with the new stopOnError flag.
     */
    @NonNull
    SenderOptions<K, V> stopOnError(@NonNull boolean stopOnError);

    /**
     * Returns the timeout for graceful shutdown of this sender.
     * @return close timeout duration
     */
    @NonNull
    Duration closeTimeout();

    /**
     * Configures the timeout for graceful shutdown of this sender.
     * @return sender options with updated close timeout
     */
    @NonNull
    SenderOptions<K, V> closeTimeout(@NonNull Duration timeout);

    /**
     * kafka client 0.10.1.1 not supported
     */
    @NonNull
    default boolean isTransactional() {
        String transactionalId = transactionalId();
        return transactionalId != null && !transactionalId.isEmpty();
    }

    /**
     * Returns the configured transactional id
     * @return transactional id
     */
    @Nullable
    default String transactionalId() {
        return null;
    }

    @NonNull
    default boolean fatalException(@NonNull Throwable t) {
        return t instanceof AuthenticationException;
    }
}
