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

package reactor.kafka.sender.internals;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.TransactionManager;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

class DefaultTransactionManager<K, V> implements TransactionManager {

    private final Mono<Producer<K, V>> producerMono;

    private final SenderOptions<K, V> senderOptions;

    private Consumer<Boolean> txComplete = b -> { };

    DefaultTransactionManager(Mono<Producer<K, V>> producerMono, SenderOptions<K, V> senderOptions) {
        this.producerMono = producerMono;
        this.senderOptions = senderOptions;
    }

    @Override
    public TransactionManager transactionComplete(Consumer<Boolean> txCompleteConsumer) {
        Objects.requireNonNull(txCompleteConsumer);
        this.txComplete = txCompleteConsumer;
        return this;
    }


    @Override
    public <T> Mono<T> begin() {
        return producerMono.flatMap(p -> Mono.fromRunnable(() -> {
            DefaultKafkaSender.log.debug("Begin a new transaction for producer {}", senderOptions.transactionalId());
        }));
    }

    @Override
    public <T> Mono<T> sendOffsets(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId) {
        return producerMono.flatMap(producer -> Mono.fromRunnable(() -> {
            if (!offsets.isEmpty()) {
                DefaultKafkaSender.log.trace("Sent offsets to transaction for producer {}, offsets: {}", senderOptions.transactionalId(), offsets);
            }
        }));
    }

    @Override
    public <T> Mono<T> commit() {
        return producerMono.flatMap(producer -> Mono.fromRunnable(() -> {
            DefaultKafkaSender.log.debug("Commit current transaction for producer {}", senderOptions.transactionalId());
            txComplete.accept(true);
        }));
    }

    @Override
    public <T> Mono<T> abort() {
        return producerMono.flatMap(p -> Mono.fromRunnable(() -> {
            DefaultKafkaSender.log.debug("Abort current transaction for producer {}", senderOptions.transactionalId());
            txComplete.accept(false);
        }));
    }

    @Override
    public Scheduler scheduler() {
        return senderOptions.scheduler();
    }
}
