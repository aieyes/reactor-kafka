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

import org.apache.kafka.common.annotation.InterfaceStability.Evolving;
import org.apache.kafka.common.internals.KafkaFutureImpl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Evolving
public abstract class KafkaFuture<T> implements Future<T> {
    public static interface BaseFunction<A, B> {
        B apply(A param1A);
    }

    public static abstract class Function<A, B> implements BaseFunction<A, B> {
    }

    public static interface BiConsumer<A, B> {
        void accept(A param1A, B param1B);
    }

    private static class AllOfAdapter<R> implements BiConsumer<R, Throwable> {
        private int remainingResponses;

        private KafkaFuture<?> future;

        public AllOfAdapter(int remainingResponses, KafkaFuture<?> future) {
            this.remainingResponses = remainingResponses;
            this.future = future;
            maybeComplete();
        }

        public synchronized void accept(R newValue, Throwable exception) {
            if (this.remainingResponses <= 0)
                return;
            if (exception != null) {
                this.remainingResponses = 0;
                this.future.completeExceptionally(exception);
            } else {
                this.remainingResponses--;
                maybeComplete();
            }
        }

        private void maybeComplete() {
            if (this.remainingResponses <= 0)
                this.future.complete(null);
        }
    }

    public static <U> KafkaFuture<U> completedFuture(U value) {
        KafkaFutureImpl<U> kafkaFutureImpl = new KafkaFutureImpl<>();
        kafkaFutureImpl.complete(value);
        return kafkaFutureImpl;
    }

    public static KafkaFuture<Void> allOf(KafkaFuture<?>... futures) {
        KafkaFutureImpl<Void> kafkaFutureImpl = new KafkaFutureImpl<>();
        AllOfAdapter<Object> allOfWaiter = new AllOfAdapter<>(futures.length, kafkaFutureImpl);
        for (KafkaFuture<?> future : futures)
            future.addWaiter(allOfWaiter);
        return kafkaFutureImpl;
    }

    public abstract <R> KafkaFuture<R> thenApply(BaseFunction<T, R> paramBaseFunction);

    public abstract <R> KafkaFuture<R> thenApply(Function<T, R> paramFunction);

    public abstract KafkaFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> paramBiConsumer);

    protected abstract void addWaiter(BiConsumer<? super T, ? super Throwable> paramBiConsumer);

    protected abstract boolean complete(T paramT);

    protected abstract boolean completeExceptionally(Throwable paramThrowable);

    public abstract boolean cancel(boolean paramBoolean);

    public abstract T get() throws InterruptedException, ExecutionException;

    public abstract T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract T getNow(T paramT) throws InterruptedException, ExecutionException;

    public abstract boolean isCancelled();

    public abstract boolean isCompletedExceptionally();

    public abstract boolean isDone();
}
